/**
 * Created on Jul 20, 2008
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

package com.aelitis.azureus.ui.swt.views.skin;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.*;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.ui.swt.IconBarEnabler;
import org.gudy.azureus2.ui.swt.TorrentUtil;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.views.IView;
import org.gudy.azureus2.ui.swt.views.tableitems.mytorrents.RankItem;
import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;

import com.aelitis.azureus.core.AzureusCoreFactory;
import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
import com.aelitis.azureus.ui.common.table.TableView;
import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
import com.aelitis.azureus.ui.swt.skin.*;
import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
import com.aelitis.azureus.ui.swt.toolbar.ToolBarEnablerSelectedContent;
import com.aelitis.azureus.ui.swt.toolbar.ToolBarItem;
import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar;
import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBarEntrySWT;
import com.aelitis.azureus.util.Constants;
import com.aelitis.azureus.util.PlayUtils;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.ui.tables.TableColumn;

/**
 * @author TuxPaper
 * @created Jul 20, 2008
 */
public class ToolBarView
	extends SkinView
{
	private static toolbarButtonListener buttonListener;

	private Map items = new LinkedHashMap();

	private GlobalManager gm;

	Control lastControl = null;

	private boolean showText = true;

	private SWTSkinObject skinObject;

	private SWTSkinObject so2nd;

	private SWTSkinObject soGap;

	private boolean initComplete = false;

	// @see com.aelitis.azureus.ui.swt.views.skin.SkinView#showSupport(com.aelitis.azureus.ui.swt.skin.SWTSkinObject, java.lang.Object)
	public Object skinObjectInitialShow(final SWTSkinObject skinObject,
			Object params) {
		this.skinObject = skinObject;
		buttonListener = new toolbarButtonListener();
		gm = AzureusCoreFactory.getSingleton().getGlobalManager();
		so2nd = skinObject.getSkin().getSkinObject("global-toolbar-2nd");

		soGap = skinObject.getSkin().getSkinObject("toolbar-gap");
		if (soGap != null) {
			Control cGap = soGap.getControl();
			FormData fd = (FormData) cGap.getLayoutData();
			if (fd.width == SWT.DEFAULT) {
				cGap.getParent().addListener(SWT.Resize, new Listener() {
					public void handleEvent(Event event) {
						resizeGap();
					}
				});
			} else {
				soGap = null;
			}
		}

		ToolBarItem item;

		// ==download
		item = new ToolBarItem("download", "image.button.download",
				"v3.MainWindow.button.download") {
			// @see com.aelitis.azureus.ui.swt.toolbar.ToolBarItem#triggerToolBarItem()
			public void triggerToolBarItem() {
				// This is for our CDP pages
				ISelectedContent[] sc = SelectedContentManager.getCurrentlySelectedContent();
				if (sc != null && sc.length == 1
						&& (sc[0].getHash() != null || sc[0].getDownloadInfo() != null)) {
					TorrentListViewsUtils.downloadDataSource(sc[0], false,
							Constants.DL_REFERAL_TOOLBAR);
				}
			}
		};
		addToolBarItem(item);

		// ==play
		item = new ToolBarItem("play", "image.button.play", "iconBar.play") {
			// @see com.aelitis.azureus.ui.swt.toolbar.ToolBarItem#triggerToolBarItem()
			public void triggerToolBarItem() {
				ISelectedContent[] sc = SelectedContentManager.getCurrentlySelectedContent();
				if (sc != null) {
					TorrentListViewsUtils.playOrStreamDataSource(sc[0],
							this.getSkinButton(), Constants.DL_REFERAL_TOOLBAR);
				}
			}
		};
		addToolBarItem(item);

		addSeperator("toolbar.area.item.sep", soMain);

		lastControl = null;

		// ==OPEN
		//		item = new ToolBarItem("open", "image.toolbar.open", "iconBar.open") {
		//			public void triggerToolBarItem() {
		//				TorrentOpener.openTorrentWindow();
		//			}
		//		};
		//		addToolBarItem(item);


		// ==share
		item = new ToolBarItem("share", "image.button.share", "iconBar.share") {
			// @see com.aelitis.azureus.ui.swt.toolbar.ToolBarItem#triggerToolBarItem()
			public void triggerToolBarItem() {
				ISelectedContent[] contents = SelectedContentManager.getCurrentlySelectedContent();
				if (contents.length > 0) {
					VuzeShareUtils.getInstance().shareTorrent(contents[0], "ToolBar");
				}
			}
		};
		addToolBarItem(item, "toolbar.area.sitem.left", so2nd);
		addSeperator(so2nd);

		// ==run
		item = new ToolBarItem("run", "image.toolbar.run", "iconBar.run") {
			public void triggerToolBarItem() {
				String viewID = SelectedContentManager.getCurrentySelectedViewID();
				boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
				ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
				if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
					ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
					IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
					if(enabler != null) {
						enabler.itemActivated("start");
					}
				} else {
					DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
					if (dms != null) {
						TorrentUtil.runTorrents(dms);
	
						for (int i = 0; i < dms.length; i++) {
							DownloadManager dm = dms[i];
							PlatformTorrentUtils.setHasBeenOpened(dm, true);
						}
					}
				}
			}
		};
		addToolBarItem(item, "toolbar.area.sitem", so2nd);
		addSeperator(so2nd);

		// ==UP
		item = new ToolBarItem("up", "image.toolbar.up", "iconBar.up") {
			public void triggerToolBarItem() {
				String viewID = SelectedContentManager.getCurrentySelectedViewID();
				boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
				ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
				if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
					ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
					IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
					if(enabler != null) {
						enabler.itemActivated("up");
					}
				} else {
					DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
					if (dms != null) {
						Arrays.sort(dms, new Comparator() {
							public int compare(Object a, Object b) {
								return ((DownloadManager) a).getPosition()
										- ((DownloadManager) b).getPosition();
							}
						});
						for (int i = 0; i < dms.length; i++) {
							DownloadManager dm = (DownloadManager) dms[i];
							if (gm.isMoveableUp(dm)) {
								gm.moveUp(dm);
							}
						}
					}
				}
			}

			// @see com.aelitis.azureus.ui.swt.toolbar.ToolBarItem#triggerToolBarItemHold()
			public boolean triggerToolBarItemHold() {
				DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
				if (dms != null) {
					gm.moveTop(dms);
				}
				return true;
			}
		};
		addToolBarItem(item, "toolbar.area.sitem", so2nd);
		addSeperator(so2nd);

		// ==down
		item = new ToolBarItem("down", "image.toolbar.down", "iconBar.down") {
			public void triggerToolBarItem() {
				String viewID = SelectedContentManager.getCurrentySelectedViewID();
				boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
				ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
				if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
					ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
					IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
					if(enabler != null) {
						enabler.itemActivated("down");
					}
				} else {
					DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
					if (dms != null) {
						Arrays.sort(dms, new Comparator() {
							public int compare(Object a, Object b) {
								return ((DownloadManager) b).getPosition()
										- ((DownloadManager) a).getPosition();
							}
						});
						for (int i = 0; i < dms.length; i++) {
							DownloadManager dm = (DownloadManager) dms[i];
							if (gm.isMoveableDown(dm)) {
								gm.moveDown(dm);
							}
						}
					}
				}
			}

			// @see com.aelitis.azureus.ui.swt.toolbar.ToolBarItem#triggerToolBarItemHold()
			public boolean triggerToolBarItemHold() {
				DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
				if (dms != null) {
					gm.moveEnd(dms);
				}
				return true;
			}
		};
		addToolBarItem(item, "toolbar.area.sitem", so2nd);
		addSeperator(so2nd);

		// ==start
		item = new ToolBarItem("start", "image.toolbar.start", "iconBar.start") {
			public void triggerToolBarItem() {
				String viewID = SelectedContentManager.getCurrentySelectedViewID();
				boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
				ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
				if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
					ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
					IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
					if(enabler != null) {
						enabler.itemActivated("start");
					}
				} else {
					DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
					if (dms != null) {
						TorrentUtil.queueTorrents(dms, null);
					}
				}
			}
		};
		addToolBarItem(item, "toolbar.area.sitem", so2nd);
		//SWTSkinObjectContainer so = (SWTSkinObjectContainer) item.getSkinButton().getSkinObject();
		//so.setDebugAndChildren(true);
		addSeperator(so2nd);

		// ==stop
		item = new ToolBarItem("stop", "image.toolbar.stop", "iconBar.stop") {
			public void triggerToolBarItem() {
				String viewID = SelectedContentManager.getCurrentySelectedViewID();
				boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
				ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
				if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
					ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
					IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
					if(enabler != null) {
						enabler.itemActivated("stop");
					}
				} else {
					DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
					if (dms != null) {
						TorrentUtil.stopTorrents(dms, null);
					}
				}
			}
		};
		addToolBarItem(item, "toolbar.area.sitem", so2nd);
		addSeperator(so2nd);

		// ==remove
		item = new ToolBarItem("remove", "image.toolbar.remove", "iconBar.remove") {
			public void triggerToolBarItem() {
				String viewID = SelectedContentManager.getCurrentySelectedViewID();
				boolean isActivityView = "Activity".equals(viewID);
				
				boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
				ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
				if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
					ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
					IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
					if(enabler != null) {
						enabler.itemActivated("remove");
					}
				} else
				if (isActivityView) {
					SkinView view = SkinViewManager.getBySkinObjectID("Activity");
					if (view instanceof SBC_ActivityView) {
						SBC_ActivityView viewActivity = (SBC_ActivityView) view;
						viewActivity.removeSelected();
					}
				} else {
					DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
					if (dms == null) {
						return;
					}
					for (int i = 0; i < dms.length; i++) {
						DownloadManager dm = dms[i];
						if (dm != null) {
							boolean delete = !dm.getDownloadState().getFlag(
									Download.FLAG_DO_NOT_DELETE_DATA_ON_REMOVE);
							TorrentListViewsUtils.removeDownload(dm, null, true, delete);
						}
					}
				}
			}
		};
		addToolBarItem(item, "toolbar.area.sitem.right", so2nd);

		///////////////////////

		addSeperator("toolbar.area.item.sep3", so2nd);

		
		addNonToolBar("toolbar.area.sitem.left2", so2nd);

		// == mode big
		item = new ToolBarItem("modeBig", "image.toolbar.table_large", null);
		addToolBarItem(item, "toolbar.area.sitem.left", so2nd);
		item.setEnabled(false);
		
		Control bigItem = item.getSkinButton().getSkinObject().getControl();
		SWTSkinObject soTitle = skin.getSkinObject("toolbar-item-title",
				item.getSkinButton().getSkinObject());
		if (soTitle instanceof SWTSkinObjectText) {
			((SWTSkinObjectText) soTitle).setText("Vi");
			((SWTSkinObjectText) soTitle).setStyle(SWT.RIGHT);
		}

		addSeperator(so2nd);

		// == mode small
		item = new ToolBarItem("modeSmall", "image.toolbar.table_normal", null);
		addToolBarItem(item, "toolbar.area.sitem.right", so2nd);
		item.setEnabled(false);
		
		Control smallItem = item.getSkinButton().getSkinObject().getControl();
		
		soTitle = skin.getSkinObject("toolbar-item-title",
				item.getSkinButton().getSkinObject());
		if (soTitle instanceof SWTSkinObjectText) {
			((SWTSkinObjectText) soTitle).setText("ew");
			((SWTSkinObjectText) soTitle).setStyle(SWT.LEFT);
		}
		

		//addSeperator(so2nd);
		
		resizeGap();

		SelectedContentManager.addCurrentlySelectedContentListener(new SelectedContentListener() {
			public void currentlySelectedContentChanged(
					ISelectedContent[] currentContent, String viewID) {
				updateCoreItems(currentContent, viewID);
				UIFunctionsManagerSWT.getUIFunctionsSWT().refreshTorrentMenu();
			}
		});

		initComplete = true;

		return null;
	}

	/**
	 * 
	 *
	 * @since 3.1.1.1
	 */
	protected void resizeGap() {
		if (soGap == null) {
			skinObject.getControl().getParent().layout();
			return;
		}
		Rectangle boundsLeft = skinObject.getControl().getBounds();
		Rectangle boundsRight = so2nd.getControl().getBounds();

		Rectangle clientArea = soGap.getControl().getParent().getClientArea();

		FormData fd = (FormData) soGap.getControl().getLayoutData();
		fd.width = clientArea.width - (boundsLeft.x + boundsLeft.width)
				- (boundsRight.width);
		if (fd.width < 0) {
			fd.width = 0;
		} else if (fd.width > 50) {
			fd.width -= 30;
		} else if (fd.width > 20) {
			fd.width = 20;
		}
		soGap.getControl().getParent().layout();
	}

	/**
	 * 
	 *
	 * @since 3.1.1.1
	 */
	protected void updateCoreItems(ISelectedContent[] currentContent,
			String viewID) {
		String[] itemsNeedingSelection = {};

		String[] itemsNeedingDMSelection = {
			"remove",
			"up",
			"down",
			"top",
			"bottom",
		};

		String[] itemsRequiring1Selection = {
			"share",
			"details",
			"comment",
		};

		String[] itemsRequiring1DMSelection = {};

		boolean isActivityView = "Activity".equals(viewID);
		boolean isIconBarEnabler = "IconBarEnabler".equals(viewID);
		
		int numSelection = currentContent.length;
		boolean hasSelection = numSelection > 0;
		boolean has1Selection = numSelection == 1;
		ToolBarItem item;
		for (int i = 0; i < itemsNeedingSelection.length; i++) {
			String itemID = itemsNeedingSelection[i];
			item = getToolBarItem(itemID);

			if (item != null) {
				item.setEnabled(hasSelection);
			}
		}

		DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
		boolean isDMSelection = dms != null && dms.length > 0;

		for (int i = 0; i < itemsNeedingDMSelection.length; i++) {
			String itemID = itemsNeedingDMSelection[i];
			item = getToolBarItem(itemID);

			if (item != null) {
				item.setEnabled(hasSelection && isDMSelection);
			}
		}
		for (int i = 0; i < itemsRequiring1Selection.length; i++) {
			String itemID = itemsRequiring1Selection[i];
			item = getToolBarItem(itemID);

			if (item != null) {
				item.setEnabled(has1Selection);
			}
		}
		for (int i = 0; i < itemsRequiring1DMSelection.length; i++) {
			String itemID = itemsRequiring1DMSelection[i];
			item = getToolBarItem(itemID);

			if (item != null) {
				item.setEnabled(has1Selection && isDMSelection);
			}
		}

		boolean canStart = false;
		boolean canStop = false;
		
		if(isIconBarEnabler && currentContent.length > 0 && currentContent[0] != null && currentContent[0] instanceof ToolBarEnablerSelectedContent ) {
			ToolBarEnablerSelectedContent ibeSelected = (ToolBarEnablerSelectedContent) currentContent[0];
			IconBarEnabler enabler = ibeSelected.getIconBarEnabler();
			if(enabler != null) {
				
				String[] TBKEYS = new String[] {"download","play","share","run","up","down","start","stop","remove"};
				//String[] OLD_TBKEYS = new String[] {"run","up","down","start","stop","delete"};
				
				for(int i = 0 ; i < TBKEYS.length  ;i++) {
					item = getToolBarItem(TBKEYS[i]);
					if (item != null) {
						item.setEnabled(enabler.isEnabled(TBKEYS[i]));
					}
				}
			}
			
		} else
		if (isActivityView) {
			item = getToolBarItem("up");
			if (item != null) {
				item.setEnabled(false);
			}
			item = getToolBarItem("down");
			if (item != null) {
				item.setEnabled(false);
			}
			item = getToolBarItem("remove");
			if (item != null) {
				SkinView view = SkinViewManager.getBySkinObjectID("Activity");
				if (view instanceof SBC_ActivityView) {
					SBC_ActivityView viewActivity = (SBC_ActivityView) view;
					if(viewActivity.isVisible()) {
						item.setEnabled(viewActivity.getNumSelected() > 0);
					} else {
						item.setEnabled(false);
					}
				} else {
					item.setEnabled(false);
				}

			}
		} else if (currentContent.length > 0) {
			for (int i = 0; i < currentContent.length; i++) {
				ISelectedContent content = currentContent[i];
				DownloadManager dm = content.getDM();
				if (!canStart && ManagerUtils.isStartable(dm)) {
					canStart = true;
				}
				if (!canStop && ManagerUtils.isStopable(dm)) {
					canStop = true;
				}
			}
		}

		item = getToolBarItem("run");
		if (item != null) {
			boolean canRun = has1Selection;
			if (canRun) {
  			ISelectedContent content = currentContent[0];
  			DownloadManager dm = content.getDM();

  			if (dm == null) {
  				canRun = false;
  			} else {
    			TOTorrent torrent = dm.getTorrent();
    			if (!dm.getAssumedComplete() && torrent != null
  						&& torrent.isSimpleTorrent()) {
    				canRun = false;
    			} else if (PlatformTorrentUtils.useEMP(torrent)
  						&& PlatformTorrentUtils.embeddedPlayerAvail()
  						&& PlayUtils.canProgressiveOrIsComplete(torrent)) {
    				// play button enabled and not UMP.. don't need launch
    				canRun = false;
    			} else if (PlatformTorrentUtils.getAdId(torrent) != null) {
    				canRun = false;
    			}
  			}
			}
			item.setEnabled(canRun);
		}
		
		item = getToolBarItem("start");
		if (item != null) {
			item.setEnabled(canStart);
		}
		item = getToolBarItem("stop");
		if (item != null) {
			item.setEnabled(canStop);
		}
		item = getToolBarItem("play");
		if (item != null) {
			item.setEnabled(has1Selection && PlayUtils.canPlayDS(currentContent[0]));
		}
		item = getToolBarItem("download");
		if (item != null) {
			boolean enabled = has1Selection
					&& currentContent[0].getDM() == null
					&& (currentContent[0].getHash() != null || currentContent[0].getDownloadInfo() != null);
			item.setEnabled(enabled);
		}
		
		TableView tv = SelectedContentManager.getCurrentlySelectedTableView();
		if (tv != null) {
			TableColumn tc = tv.getTableColumn(RankItem.COLUMN_ID);
			if (tc != null && !tc.isVisible()) {
    		item = getToolBarItem("up");
    		if (item != null) {
    			item.setEnabled(false);
    		}
    		item = getToolBarItem("down");
    		if (item != null) {
    			item.setEnabled(false);
    		}
			}
		}
	}

	/**
	 * @param toolBarItem
	 *
	 * @since 3.1.1.1
	 */
	protected void activateViaSideBar(ToolBarItem toolBarItem) {
		SideBar sidebar = (SideBar) SkinViewManager.getByClass(SideBar.class);
		if (sidebar != null) {
			SideBarEntrySWT info = sidebar.getCurrentSideBarInfo();
			if (info.iview instanceof IconBarEnabler) {
				IconBarEnabler enabler = (IconBarEnabler) info.iview;
				enabler.itemActivated(toolBarItem.getId());
			}
		}
	}

	/**
	 * @param itemID
	 * @return
	 *
	 * @since 3.1.1.1
	 */
	public ToolBarItem getToolBarItem(String itemID) {
		return (ToolBarItem) items.get(itemID);
	}

	public ToolBarItem[] getAllToolBarItems() {
		return (ToolBarItem[]) items.values().toArray(new ToolBarItem[0]);
	}

	public void refreshCoreToolBarItems() {
		updateCoreItems(SelectedContentManager.getCurrentlySelectedContent(),
				SelectedContentManager.getCurrentySelectedViewID());
	}

	public void addToolBarItem(final ToolBarItem item) {
		addToolBarItem(item, "toolbar.area.item", soMain);
	}

	public void addToolBarItem(final ToolBarItem item, String templateID,
			SWTSkinObject soMain) {
		SWTSkinObject so = skin.createSkinObject("toolbar:" + item.getId(),
				templateID, soMain);
		if (so != null) {
			so.setTooltipID(item.getTooltipID());

			if (lastControl != null) {
				FormData fd = (FormData) so.getControl().getLayoutData();
				fd.left = new FormAttachment(lastControl);
			}

			so.setData("toolbaritem", item);
			SWTSkinButtonUtility btn = new SWTSkinButtonUtility(so,
					"toolbar-item-image");
			btn.setImage(item.getImageID());
			btn.addSelectionListener(buttonListener);
			item.setSkinButton(btn);

			SWTSkinObject soTitle = skin.getSkinObject("toolbar-item-title", so);
			if (soTitle instanceof SWTSkinObjectText) {
				((SWTSkinObjectText) soTitle).setTextID(item.getTextID());
			}

			if (initComplete) {
				Utils.relayout(so.getControl().getParent());
			}

			lastControl = item.getSkinButton().getSkinObject().getControl();
			items.put(item.getId(), item);
		}
	}

	private void addSeperator(SWTSkinObject soMain) {
		addSeperator("toolbar.area.sitem.sep", soMain);
	}

	private void addSeperator(String id, SWTSkinObject soMain) {
		SWTSkinObject so = skin.createSkinObject("toolbar_sep" + Math.random(), id,
				soMain);
		if (so != null) {
			if (lastControl != null) {
				FormData fd = (FormData) so.getControl().getLayoutData();
				fd.left = new FormAttachment(lastControl, fd.left == null ? 0
						: fd.left.offset);
			}

			lastControl = so.getControl();
		}
	}

	private void addNonToolBar(String skinid, SWTSkinObject soMain) {
		SWTSkinObject so = skin.createSkinObject("toolbar_d" + Math.random(),
				skinid, soMain);
		if (so != null) {
			if (lastControl != null) {
				FormData fd = (FormData) so.getControl().getLayoutData();
				fd.left = new FormAttachment(lastControl, fd.left == null ? 0
						: fd.left.offset);
			}

			lastControl = so.getControl();
		}
	}

	/**
	 * @param showText the showText to set
	 */
	public void setShowText(boolean showText) {
		this.showText = showText;
		ToolBarItem[] allToolBarItems = getAllToolBarItems();
		for (int i = 0; i < allToolBarItems.length; i++) {
			ToolBarItem tbi = allToolBarItems[i];
			SWTSkinObject so = tbi.getSkinButton().getSkinObject();
			SWTSkinObject soTitle = skin.getSkinObject("toolbar-item-title", so);
			soTitle.setVisible(showText);
		}
	}

	/**
	 * @return the showText
	 */
	public boolean getShowText() {
		return showText;
	}

	private static class toolbarButtonListener
		extends ButtonListenerAdapter
	{
		public void pressed(SWTSkinButtonUtility buttonUtility,
				SWTSkinObject skinObject, int stateMask) {
			ToolBarItem item = (ToolBarItem) buttonUtility.getSkinObject().getData(
					"toolbaritem");
			item.triggerToolBarItem();
		}

		public boolean held(SWTSkinButtonUtility buttonUtility) {
			ToolBarItem item = (ToolBarItem) buttonUtility.getSkinObject().getData(
					"toolbaritem");
			return item.triggerToolBarItemHold();
		}

		public void disabledStateChanged(SWTSkinButtonUtility buttonUtility,
				boolean disabled) {
			ToolBarItem item = (ToolBarItem) buttonUtility.getSkinObject().getData(
					"toolbaritem");
			item.setEnabled(!disabled);
		}
	}
}
