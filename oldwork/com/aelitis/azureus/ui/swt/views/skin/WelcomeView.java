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

import org.gudy.azureus2.core3.config.COConfigurationManager;

import com.aelitis.azureus.ui.skin.SkinConstants;
import com.aelitis.azureus.ui.swt.browser.BrowserContext.loadingListener;
import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar;
import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBarCloseListener;
import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBarEntrySWT;
import com.aelitis.azureus.util.Constants;

/**
 * @author TuxPaper
 * @created Oct 1, 2006
 *
 */
public class WelcomeView
	extends SkinView
{
	private SWTSkinObjectBrowser browserSkinObject;

	public Object skinObjectInitialShow(final SWTSkinObject skinObject, Object params) {
		browserSkinObject = (SWTSkinObjectBrowser) skin.getSkinObject(
				SkinConstants.VIEWID_BROWSER_WELCOME, soMain);

		browserSkinObject.addListener(new loadingListener() {
			
			public void browserLoadingChanged(boolean loading, String url) {
				if (!loading) {
					skinObject.getControl().getParent().layout(true, true);
				}
			}
		});

		COConfigurationManager.setParameter("v3.Show Welcome", false);
		
		Object o = skinObject.getData("CreationParams");
		if (o instanceof String) {
			browserSkinObject.setURL((String) o);
		} else {
  		String sURL = Constants.URL_PREFIX + Constants.URL_WELCOME + "?"
  				+ Constants.URL_SUFFIX;
  		System.out.println(sURL);
  		browserSkinObject.setURL(sURL);
		}
		
		SideBarEntrySWT sideBarInfo = SideBar.getSideBarInfo(SideBar.SIDEBAR_SECTION_WELCOME);
		sideBarInfo.addListener(new SideBarCloseListener() {
			public void sidebarClosed(SideBarEntrySWT entry) {
				SideBar sidebar = (SideBar) SkinViewManager.getByClass(SideBar.class);
				if (sidebar != null) {
  				String startTab;
  				if (COConfigurationManager.getBooleanParameter("v3.Start Advanced")) {
  					startTab = SideBar.SIDEBAR_SECTION_LIBRARY;
  				} else {
  					startTab = SideBar.SIDEBAR_SECTION_BROWSE;
  				}

					sidebar.showItemByID(startTab);
				}
			}
		});
		
		return null;
	}
}
