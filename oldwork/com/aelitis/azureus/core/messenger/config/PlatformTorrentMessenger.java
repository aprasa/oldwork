/**
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * AELITIS, SAS au capital de 63.529,40 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.messenger.config;

import java.util.*;

import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;

import com.aelitis.azureus.core.messenger.PlatformMessage;
import com.aelitis.azureus.core.messenger.PlatformMessenger;
import com.aelitis.azureus.core.messenger.PlatformMessengerListener;
import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;

/**
 * @author TuxPaper
 * @created Oct 13, 2006
 *
 */
public class PlatformTorrentMessenger
{
	public static String LISTENER_ID = "torrent";

	public static String OP_GETMETADATA = "get-metadata";

	public static String OP_STREAMCOMPLETE = "stream-complete";

	public static interface GetMetaDataReplyListener
	{
		public void messageSent();

		public void replyReceived(String replyType, Map mapHashes);
	}

	/**
	 * @param torrent
	 * @param maxDelayMS
	 * @param replyListener
	 *
	 * @since 3.0.0.7
	 */
	public static void getMetaData(TOTorrent[] torrents, long maxDelayMS,
			final GetMetaDataReplyListener replyListener) {
		Map mapParameters = new HashMap();
		List listContent = new ArrayList();
		List listHashes = new ArrayList();
		mapParameters.put("content-list", listContent);
		if (PlatformConfigMessenger.getRPCVersion() == 0) {
			// legacy support
			mapParameters.put("hashes", listHashes);
		}

		for (int i = 0; i < torrents.length; i++) {
			TOTorrent torrent = torrents[i];
			if (!PlatformTorrentUtils.isContent(torrent, true)) {
				continue;
			}

			String hash = null;
			try {
				hash = torrent.getHashWrapper().toBase32String();
			} catch (TOTorrentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (hash != null) {
				listHashes.add(hash);

				Map jsonSubObject = new HashMap();
				listContent.add(jsonSubObject);
				jsonSubObject.put("hash", hash);
				jsonSubObject.put("last-revision", new Long(
						PlatformTorrentUtils.getContentLastUpdated(torrent)));
			}
		}

		PlatformMessage message = new PlatformMessage("AZMSG", LISTENER_ID,
				OP_GETMETADATA, mapParameters, maxDelayMS);

		PlatformMessengerListener listener = new PlatformMessengerListener() {
			public void messageSent(PlatformMessage message) {
				replyListener.messageSent();
			}

			public void replyReceived(PlatformMessage message, String replyType,
					Map reply) {
				if (reply != null) {
					replyListener.replyReceived(replyType, reply);
				} else {
					replyListener.replyReceived(replyType, new HashMap());
				}
			}
		};

		PlatformMessenger.queueMessage(message, listener);
	}

	public static void streamComplete(TOTorrent torrent, long waitTime,
			int maxSeekAheadSecs, int numRebuffers, int numHardRebuffers) {
		String hash = null;
		try {
			hash = torrent.getHashWrapper().toBase32String();
		} catch (TOTorrentException e) {
		}

		if (hash == null) {
			return;
		}

		Map mapParameters = new HashMap();

		mapParameters.put("torrent-hash", hash);
		mapParameters.put("wait-time", new Long(waitTime));
		mapParameters.put("max-seek", new Long(maxSeekAheadSecs));
		mapParameters.put("num-rebuffers", new Long(numRebuffers));
		mapParameters.put("num-hard-rebuffers", new Long(numHardRebuffers));

		PlatformMessage message = new PlatformMessage("AZMSG", LISTENER_ID,
				OP_STREAMCOMPLETE, mapParameters, 3000);

		PlatformMessenger.queueMessage(message, null);
	}

	public static void streamComplete(TOTorrent torrent, Map info) {
		String hash = null;
		try {
			hash = torrent.getHashWrapper().toBase32String();
		} catch (TOTorrentException e) {
		}

		if (hash == null) {
			return;
		}

		Map mapParameters = new HashMap(info);

		mapParameters.put("torrent-hash", hash);

		PlatformMessage message = new PlatformMessage("AZMSG", LISTENER_ID,
				OP_STREAMCOMPLETE, mapParameters, 3000);

		PlatformMessenger.queueMessage(message, null);
	}
}
