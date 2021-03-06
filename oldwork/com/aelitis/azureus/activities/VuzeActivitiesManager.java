/**
 * Created on Jan 28, 2008 
 *
 * Copyright 2008 Vuze, Inc.  All rights reserved.
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License only.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA 
 */

package com.aelitis.azureus.activities;

import java.util.*;

import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerListener;
import org.gudy.azureus2.core3.download.DownloadManagerState;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.global.GlobalManagerListener;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.*;
import com.aelitis.azureus.core.messenger.config.PlatformRatingMessenger;
import com.aelitis.azureus.core.messenger.config.PlatformVuzeActivitiesMessenger;
import com.aelitis.azureus.core.messenger.config.RatingUpdateListener2;
import com.aelitis.azureus.core.torrent.*;
import com.aelitis.azureus.util.Constants;
import com.aelitis.azureus.util.MapUtils;

/**
 * Manage Vuze News Entries.  Loads, Saves, and expires them
 * 
 * @author TuxPaper
 * @created Jan 28, 2008
 *
 */
public class VuzeActivitiesManager
{
	public static final long MAX_LIFE_MS = 1000L * 60 * 60 * 24 * 30;

	private static final long DEFAULT_PLATFORM_REFRESH = 60 * 60 * 1000L * 24;

	private static final long RATING_REMINDER_DELAY = 1000L * 60 * 60 * 24 * 3;

	private static final long WEEK_MS = 604800000L;

	private static final String SAVE_FILENAME = "VuzeActivities.config";

	private static ArrayList listeners = new ArrayList();

	private static ArrayList allEntries = new ArrayList();

	private static AEMonitor allEntries_mon = new AEMonitor("VuzeActivityMan");

	private static List removedEntries = new ArrayList();

	private static PlatformVuzeActivitiesMessenger.GetEntriesReplyListener replyListener;

	private static AEDiagnosticsLogger diag_logger;

	private static long lastVuzeNewsAt;

	private static boolean skipAutoSave = true;

	private static AEMonitor config_mon = new AEMonitor("ConfigMon");

	private static boolean saveEventsOnClose = false;

	static {
		if (System.getProperty("debug.vuzenews", "0").equals("1")) {
			diag_logger = AEDiagnostics.getLogger("v3.vuzenews");
			diag_logger.log("\n\nVuze News Logging Starts");
		} else {
			diag_logger = null;
		}
	}

	public static void initialize(final AzureusCore core) {
		new AEThread2("lazy init", true) {
			public void run() {
				_initialize(core);
			}
		}.start();
	}

	private static void _initialize(AzureusCore core) {
		if (diag_logger != null) {
			diag_logger.log("Initialize Called");
		}
		
		AzureusCoreFactory.getSingleton().addLifecycleListener(new AzureusCoreLifecycleAdapter() {
		
			public void stopping(AzureusCore core) {
				if (saveEventsOnClose) {
					saveEventsNow();
				}
			}
		});

		loadEvents();
		
		{	
			//TODO TUX : could we remove the dl-related entries from the "removed/deleted" list too ?
			//Remove obsolete dl-related entries
			List obsoleteEntries = new ArrayList();
			VuzeActivitiesEntry[] entries = getAllEntries();
			for(int i = 0 ; i < entries.length ; i++) {
				VuzeActivitiesEntry entry = entries[i];
				if( 	entry.getTypeID().equals(VuzeActivitiesConstants.TYPEID_DL_ADDED) || 
						entry.getTypeID().equals(VuzeActivitiesConstants.TYPEID_DL_REMOVE) ||
						entry.getTypeID().equals(VuzeActivitiesConstants.TYPEID_DL_COMPLETE) ) {
					
					obsoleteEntries.add(entry);
					
				}
			}
			if(obsoleteEntries.size() > 0) {
				removeEntries((VuzeActivitiesEntry[]) obsoleteEntries.toArray(new VuzeActivitiesEntry[obsoleteEntries.size()]),true);
			}
		}

		replyListener = new PlatformVuzeActivitiesMessenger.GetEntriesReplyListener() {
			public void gotVuzeNewsEntries(VuzeActivitiesEntry[] entries,
					long refreshInMS) {
				if (diag_logger != null) {
					diag_logger.log("Received Reply from platform with " + entries.length
							+ " entries.  Refresh in " + refreshInMS);
				}

				addEntries(entries);

				if (refreshInMS <= 0) {
					refreshInMS = DEFAULT_PLATFORM_REFRESH;
				}

				SimpleTimer.addEvent("GetVuzeNews",
						SystemTime.getOffsetTime(refreshInMS), new TimerEventPerformer() {
							public void perform(TimerEvent event) {
								PlatformVuzeActivitiesMessenger.getEntries(Math.min(
										SystemTime.getCurrentTime() - lastVuzeNewsAt, MAX_LIFE_MS),
										5000, replyListener);
								lastVuzeNewsAt = SystemTime.getCurrentTime();
							}
						});
			}
		};

		pullActivitiesNow(5000);

		PlatformRatingMessenger.addListener(new RatingUpdateListener2() {
			// @see com.aelitis.azureus.core.messenger.config.PlatformRatingMessenger.RatingUpdateListener#ratingUpdated(com.aelitis.azureus.core.torrent.RatingInfoList)
			public void ratingUpdated(RatingInfoList rating) {
				if (!(rating instanceof SingleUserRatingInfo)) {
					return;
				}
				Object[] allEntriesArray = allEntries.toArray();
				for (int i = 0; i < allEntriesArray.length; i++) {
					VuzeActivitiesEntry entry = (VuzeActivitiesEntry) allEntriesArray[i];
					if (entry == null) {
						continue;
					}
					String typeID = entry.getTypeID();
					DownloadManager dm = entry.getDownloadManger();
					if (VuzeActivitiesConstants.TYPEID_RATING_REMINDER.equals(typeID)
							&& dm != null) {
						try {
							String hash = dm.getTorrent().getHashWrapper().toBase32String();
							if (rating.hasHash(hash)
									&& rating.getRatingValue(hash,
											PlatformRatingMessenger.RATE_TYPE_CONTENT) != GlobalRatingUtils.RATING_NONE) {
								removeEntries(new VuzeActivitiesEntry[] {
									entry
								});
							}
						} catch (Exception e) {
						}
					}
				}
			}
		});

		GlobalManagerListener gmListener = new GlobalManagerListener() {

			public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {
			}

			public void downloadManagerRemoved(DownloadManager dm) {
			}

			public void downloadManagerAdded(DownloadManager dm) {
				List entries = registerDM(dm);
				if (entries != null && entries.size() > 0) {
					addEntries((VuzeActivitiesEntry[]) entries.toArray(new VuzeActivitiesEntry[0]));
				}
			}

			public void destroyed() {
			}

			public void destroyInitiated() {
			}
		};

		
		
		
		List newEntries = new ArrayList();
		GlobalManager gm = core.getGlobalManager();
		gm.addListener(gmListener, false);

		List downloadManagers = gm.getDownloadManagers();
		for (Iterator iter = downloadManagers.iterator(); iter.hasNext();) {
			DownloadManager dm = (DownloadManager) iter.next();
			List entries = registerDM(dm);
			if (entries != null && entries.size() > 0) {
				newEntries.addAll(entries);
			}
		}

		if (newEntries.size() > 0) {
			trimReminders(newEntries, false);
			addEntries((VuzeActivitiesEntry[]) newEntries.toArray(new VuzeActivitiesEntry[0]));
		}

		try {
			allEntries_mon.enter();

			trimReminders(allEntries, true);
		} finally {
			allEntries_mon.exit();
		}
	}

	/**
	 * @param allEntries2
	 *
	 * @since 3.0.4.3
	 */
	private static void trimReminders(List entries, boolean liveRemove) {
		List listReminders = new ArrayList();
		for (Iterator iter = entries.iterator(); iter.hasNext();) {
			VuzeActivitiesEntry entry = (VuzeActivitiesEntry) iter.next();
			if (VuzeActivitiesConstants.TYPEID_RATING_REMINDER.equals(entry.getTypeID())) {
				listReminders.add(entry);
			}
		}
		if (listReminders.size() > 3) {
			Collections.sort(listReminders); // will be sorted by date ascending
			long weekBreak = SystemTime.getCurrentTime() - (WEEK_MS * 4);
			int numInWeek = 0;
			for (Iterator iter = listReminders.iterator(); iter.hasNext();) {
				VuzeActivitiesEntry entry = (VuzeActivitiesEntry) iter.next();

				if (entry.getTimestamp() < weekBreak) {
					numInWeek++;
					if (numInWeek > 3) {
						if (liveRemove) {
							removeEntries(new VuzeActivitiesEntry[] {
								entry
							});
						} else {
							entries.remove(entry);
						}
					}
				} else {
					numInWeek = 1;
					while (entry.getTimestamp() >= weekBreak) {
						weekBreak += WEEK_MS;
					}
				}
			}
		}
	}


	private static List registerDM(DownloadManager dm) {
		TOTorrent torrent = dm.getTorrent();
		if (PlatformTorrentUtils.getAdId(torrent) != null) {
			return null;
		}

		boolean isContent = PlatformTorrentUtils.isContent(torrent, true);

		List entries = new ArrayList();

		try {
			if (isContent) {
				long completedOn = dm.getDownloadState().getLongParameter(
						DownloadManagerState.PARAM_DOWNLOAD_COMPLETED_TIME);
				if (completedOn > 0
						&& SystemTime.getCurrentTime() - completedOn > RATING_REMINDER_DELAY) {
					int userRating = PlatformTorrentUtils.getUserRating(torrent);
					if (userRating < 0) {
						VuzeActivitiesEntry entry = new VuzeActivitiesEntry();
						entries.add(entry);

						String hash = torrent.getHashWrapper().toBase32String();
						String title;
						String url = Constants.URL_PREFIX + Constants.URL_DETAILS + hash
								+ ".html?" + Constants.URL_SUFFIX + "&client_ref=activity-"
								+ VuzeActivitiesConstants.TYPEID_RATING_REMINDER;
						title = "<A HREF=\"" + url + "\">"
								+ PlatformTorrentUtils.getContentTitle2(dm) + "</A>";
						entry.setAssetHash(hash);

						entry.setDownloadManager(dm);
						entry.setShowThumb(true);
						entry.setID(hash + ";r" + completedOn);
						entry.setText("To improve your recommendations, please rate "
								+ title);
						entry.setTimestamp(SystemTime.getCurrentTime());
						entry.setTypeID(VuzeActivitiesConstants.TYPEID_RATING_REMINDER, true);
					}
				}
			}
		} catch (Throwable t) {
			// ignore
		}

		return entries;
	}

	/**
	 * Pull entries from webapp
	 * 
	 * @param delay max time to wait before running request
	 *
	 * @since 3.0.4.3
	 */
	public static void pullActivitiesNow(long delay) {
		PlatformVuzeActivitiesMessenger.getEntries(Math.min(
				SystemTime.getCurrentTime() - lastVuzeNewsAt, MAX_LIFE_MS), delay,
				replyListener);
		lastVuzeNewsAt = SystemTime.getCurrentTime();
	}

	/**
	 * Pull entries from webapp
	 * 
	 * @param agoMS Pull all events within this timespan (ms)
	 * @param delay max time to wait before running request
	 *
	 * @since 3.0.4.3
	 */
	public static void pullActivitiesNow(long agoMS, long delay) {
		PlatformVuzeActivitiesMessenger.getEntries(agoMS, delay, replyListener);
		lastVuzeNewsAt = SystemTime.getCurrentTime();
	}

	/**
	 * Clear the removed entries list so that an entry that was once deleted will
	 * will be able to be added again
	 * 
	 *
	 * @since 3.0.4.3
	 */
	public static void resetRemovedEntries() {
		removedEntries.clear();
		saveEvents();
	}

	/**
	 * 
	 *
	 * @since 3.1.1.1
	 */
	private static void saveEvents() {
		saveEventsOnClose  = true;
	}

	/**
	 * 
	 *
	 * @since 3.0.4.3
	 */
	private static void loadEvents() {
		skipAutoSave = true;

		try {
			Map map = FileUtil.readResilientConfigFile(SAVE_FILENAME);

			lastVuzeNewsAt = MapUtils.getMapLong(map, "LastCheck", 0);
			long cutoffTime = getCutoffTime();
			if (lastVuzeNewsAt < cutoffTime) {
				lastVuzeNewsAt = cutoffTime;
			}

			Object value;

			List newRemovedEntries = (List) MapUtils.getMapObject(map,
					"removed-entries", null, List.class);
			if (newRemovedEntries != null) {
				for (Iterator iter = newRemovedEntries.iterator(); iter.hasNext();) {
					value = iter.next();
					if (!(value instanceof Map)) {
						continue;
					}
					VuzeActivitiesEntry entry = createEntryFromMap((Map) value, true);

					if (entry != null && entry.getTimestamp() > cutoffTime) {
						removedEntries.add(entry);
					}
				}
			}

			value = map.get("entries");
			if (!(value instanceof List)) {
				return;
			}

			List entries = (List) value;
			List entriesToAdd = new ArrayList(entries.size());
			for (Iterator iter = entries.iterator(); iter.hasNext();) {
				value = iter.next();
				if (!(value instanceof Map)) {
					continue;
				}

				VuzeActivitiesEntry entry = createEntryFromMap((Map) value, true);

				if (entry != null) {
					if (VuzeActivitiesConstants.TYPEID_RATING_REMINDER.equals(entry.getTypeID())) {
						entry.setShowThumb(true);
					}

					if (entry.getTimestamp() > cutoffTime) {
						entriesToAdd.add(entry);
					}
				}
			}

			int num = entriesToAdd.size();
			if (num > 0) {
				addEntries((VuzeActivitiesEntry[]) entriesToAdd.toArray(new VuzeActivitiesEntry[num]));
			}
		} finally {
			skipAutoSave = false;
		}
	}

	private static void saveEventsNow() {
		if (skipAutoSave) {
			return;
		}

		try {
			config_mon.enter();

			Map mapSave = new HashMap();
			mapSave.put("LastCheck", new Long(lastVuzeNewsAt));

			List entriesList = new ArrayList();

			VuzeActivitiesEntry[] allEntriesArray = getAllEntries();
			for (int i = 0; i < allEntriesArray.length; i++) {
				VuzeActivitiesEntry entry = allEntriesArray[i];
				if (entry == null) {
					continue;
				}

				boolean isHeader = VuzeActivitiesConstants.TYPEID_HEADER.equals(entry.getTypeID());
				if (!isHeader) {
					entriesList.add(entry.toMap());
				}
			}
			mapSave.put("entries", entriesList);

			List removedEntriesList = new ArrayList();
			for (Iterator iter = removedEntries.iterator(); iter.hasNext();) {
				VuzeActivitiesEntry entry = (VuzeActivitiesEntry) iter.next();
				removedEntriesList.add(entry.toDeletedMap());
			}
			mapSave.put("removed-entries", removedEntriesList);

			FileUtil.writeResilientConfigFile(SAVE_FILENAME, mapSave);

		} catch (Throwable t) {
			Debug.out(t);
		} finally {
			config_mon.exit();
		}
	}

	public static long getCutoffTime() {
		return SystemTime.getOffsetTime(-MAX_LIFE_MS);
	}

	public static void addListener(VuzeActivitiesListener l) {
		listeners.add(l);
	}

	public static void removeListener(VuzeActivitiesListener l) {
		listeners.remove(l);
	}

	/**
	 * 
	 * @param entries
	 * @return list of entries actually added (no dups)
	 *
	 * @since 3.0.4.3
	 */
	public static VuzeActivitiesEntry[] addEntries(VuzeActivitiesEntry[] entries) {
		long cutoffTime = getCutoffTime();

		ArrayList newEntries = new ArrayList(entries.length);
		ArrayList existingEntries = new ArrayList(0);

		try {
			allEntries_mon.enter();

			for (int i = 0; i < entries.length; i++) {
				VuzeActivitiesEntry entry = entries[i];
				boolean isHeader = VuzeActivitiesConstants.TYPEID_HEADER.equals(entry.getTypeID());
				if ((entry.getTimestamp() >= cutoffTime || isHeader)
						&& !removedEntries.contains(entry)) {
					if (allEntries.contains(entry)) {
						existingEntries.add(entry);
					} else {
						newEntries.add(entry);
						allEntries.add(entry);
					}
				}
			}
		} finally {
			allEntries_mon.exit();
		}

		VuzeActivitiesEntry[] newEntriesArray = (VuzeActivitiesEntry[]) newEntries.toArray(new VuzeActivitiesEntry[newEntries.size()]);

		if (newEntriesArray.length > 0) {
			saveEventsNow();

			Object[] listenersArray = listeners.toArray();
			for (int i = 0; i < listenersArray.length; i++) {
				VuzeActivitiesListener l = (VuzeActivitiesListener) listenersArray[i];
				l.vuzeNewsEntriesAdded(newEntriesArray);
			}
		}

		if (existingEntries.size() > 0) {
			if (newEntriesArray.length == 0) {
				saveEvents();
			}

  		for (Iterator iter = existingEntries.iterator(); iter.hasNext();) {
  			VuzeActivitiesEntry entry = (VuzeActivitiesEntry) iter.next();
  			triggerEntryChanged(entry);
  		}
		}

		return newEntriesArray;
	}

	public static void removeEntries(VuzeActivitiesEntry[] entries) {
		removeEntries(entries, false);
	}
	
	public static void removeEntries(VuzeActivitiesEntry[] entries, boolean allowReAdd) {
		long cutoffTime = getCutoffTime();

		try {
			allEntries_mon.enter();

			for (int i = 0; i < entries.length; i++) {
				VuzeActivitiesEntry entry = entries[i];
				if (entry == null) {
					continue;
				}
				allEntries.remove(entry);
				boolean isHeader = VuzeActivitiesConstants.TYPEID_HEADER.equals(entry.getTypeID());
				if (!allowReAdd && entry.getTimestamp() > cutoffTime && !isHeader) {
					removedEntries.add(entry);
				}
			}
		} finally {
			allEntries_mon.exit();
		}

		Object[] listenersArray = listeners.toArray();
		for (int i = 0; i < listenersArray.length; i++) {
			VuzeActivitiesListener l = (VuzeActivitiesListener) listenersArray[i];
			l.vuzeNewsEntriesRemoved(entries);
		}
		saveEventsNow();
	}

	public static VuzeActivitiesEntry getEntryByID(String id) {
		try {
			allEntries_mon.enter();

			for (Iterator iter = allEntries.iterator(); iter.hasNext();) {
				VuzeActivitiesEntry entry = (VuzeActivitiesEntry) iter.next();
				if (entry == null) {
					continue;
				}
				String entryID = entry.getID();
				if (entryID != null && entryID.equals(id)) {
					return entry;
				}
			}
		} finally {
			allEntries_mon.exit();
		}

		return null;
	}

	public static VuzeActivitiesEntry[] getAllEntries() {
		return (VuzeActivitiesEntry[]) allEntries.toArray(new VuzeActivitiesEntry[allEntries.size()]);
	}
	
	public static int getNumEntries() {
		return allEntries.size();
	}

	public static void log(String s) {
		if (diag_logger != null) {
			diag_logger.log(s);
		}
	}

	/**
	 * @param vuzeActivitiesEntry
	 *
	 * @since 3.0.4.3
	 */
	public static void triggerEntryChanged(VuzeActivitiesEntry entry) {
		Object[] listenersArray = listeners.toArray();
		for (int i = 0; i < listenersArray.length; i++) {
			VuzeActivitiesListener l = (VuzeActivitiesListener) listenersArray[i];
			l.vuzeNewsEntryChanged(entry);
		}
		saveEvents();
	}

	/**
	 * @param map
	 * @return
	 *
	 * @since 3.0.5.3
	 */
	public static VuzeActivitiesEntry createEntryFromMap(Map map,
			boolean internalMap) {
		VuzeActivitiesEntry entry;
		String typeID = MapUtils.getMapString(map, "typeID", MapUtils.getMapString(
				map, "type-id", null));
		if (VuzeActivitiesConstants.TYPEID_BUDDYREQUEST.equals(typeID)) {
			entry = new VuzeActivitiesEntryBuddyRequest();
		} else if (VuzeActivitiesConstants.TYPEID_BUDDYSHARE.equals(typeID)) {
			entry = new VuzeActivitiesEntryContentShare();
		} else {
			entry = new VuzeActivitiesEntry();
		}
		if (internalMap) {
			entry.loadFromInternalMap(map);
		} else {
			entry.loadFromExternalMap(map);
		}
		return entry;
	}
}
