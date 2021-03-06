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

package com.aelitis.azureus.ui.swt.views.skin;

import org.gudy.azureus2.core3.internat.MessageText;

import com.aelitis.azureus.ui.swt.skin.*;
import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
import com.aelitis.azureus.ui.swt.views.TorrentListView;
import com.aelitis.azureus.ui.swt.views.TorrentListViewListener;
import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar;

/**
 * @author TuxPaper
 * @created Sep 30, 2006
 *
 * TODO Code similaries between MiniRecentList, MiniDownloadList, ManageCdList, 
 *     and ManageDlList.  Need to combine
 */
public class MiniRecentList
	extends SkinView
{
	private static String PREFIX = "minirecent-";

	private TorrentListView view;

	private SWTSkinObjectText skinHeaderText;

	public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params) {
		final SWTSkin skin = skinObject.getSkin();

		skinObject = skin.getSkinObject(PREFIX + "header-text");
		if (skinObject instanceof SWTSkinObjectText) {
			skinHeaderText = (SWTSkinObjectText) skinObject;
		}

		skinObject = skin.getSkinObject(PREFIX + "link");
		if (skinObject instanceof SWTSkinObjectText) {
			SWTSkinButtonUtility btn = new SWTSkinButtonUtility(skinObject);
			btn.addSelectionListener(new ButtonListenerAdapter() {
				public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
					SideBar sidebar = (SideBar)SkinViewManager.getByClass(SideBar.class);
					if (sidebar != null) {
						sidebar.showItemByID(SideBar.SIDEBAR_SECTION_LIBRARY);
					}
				}
			});
		}

		view = new TorrentListView(this, PREFIX,
				TorrentListView.VIEW_RECENT_DOWNLOADED, true, false);

		if (skinHeaderText != null) {
			view.addListener(new TorrentListViewListener() {
				public void countChanged() {
					skinHeaderText.setText(MessageText.getString(
							"v3.MainWindow.recentDL", new String[] {
								"" + view.size(true)
							}));
				}
			});
		}

		return null;
	}
}
