/*
 * Created on Jun 14, 2006 9:02:55 PM
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package com.aelitis.azureus.ui;

import org.gudy.azureus2.core3.download.DownloadManager;

import com.aelitis.azureus.core.AzureusCoreComponent;
import com.aelitis.azureus.ui.common.updater.UIUpdater;

/**
 * @author TuxPaper
 * @created Jun 14, 2006
 *
 *
 * TODO: Replace showXxxx(..) with showView(ID, ..) + ID Constants
 */
public interface UIFunctions
	extends AzureusCoreComponent
{
	public static int STATUSICON_NONE = 0;

	public static int STATUSICON_WARNING = 1;

	public static int STATUSICON_ERROR = 2;

	
	public static final int VIEW_CONSOLE = 0;
	public static final int VIEW_STATS = 1;
	public static final int VIEW_CONFIG = 4;
	public static final int VIEW_DM_DETAILS = 5;
	public static final int VIEW_DM_MULTI_OPTIONS = 6;
	public static final int VIEW_MYSHARES = 7;
	public static final int VIEW_MYTORRENTS = 8;
	public static final int VIEW_MYTRACKER = 9;
	public static final int VIEW_ALLPEERS = 10;
	public static final int VIEW_DETAILED_LISTVIEW = 11;
	

	/**
	 * Bring main window to the front
	 */
	void bringToFront();

	/**
	 * Bring main window to the front
	 * 
	 * @param noTricks Don't try any tricks to force it to the top
	 *
	 * @since 3.0.1.7
	 */
	void bringToFront(boolean noTricks);
	
	/**
	 * Change/Refresh the language of the UI
	 */
	void refreshLanguage();

	/**
	 * 
	 */
	void refreshIconBar();

	
	/**
	 * @param manager
	 */
	void removeManagerView(DownloadManager dm);

	/**
	 * @param string
	 */
	void setStatusText(String string);

	void setStatusText(int statustype, String string, UIStatusTextClickListener l);

	/**
	 * Request the UI be shut down.
	 * 
	 * @return true - request granted, UI is being shut down
	 *         false - request denied (example: password entry failed)
	 */
	boolean dispose(boolean for_restart, boolean close_already_in_progress);

	/**
	 * @param url
	 * @param target TODO
	 * @param w
	 * @param h
	 * @param allowResize 
	 * @return success level
	 */
	boolean viewURL(String url, String target, int w, int h, boolean allowResize,
			boolean isModal);

	boolean viewURL(String url, String target, double wPct, double hPct,
			boolean allowResize, boolean isModal);


	public UIFunctionsUserPrompter getUserPrompter(String title, String text,
			String[] buttons, int defaultOption);

	public int promptUser(String title, String text, String[] buttons,
			int defaultOption, String rememberID, String rememberText,
			boolean bRememberByDefault, int autoCloseInMS);
	
	/**
	 * Retrieves the class that handles periodically updating the UI
	 * 
	 * @return
	 *
	 * @since 3.1.1.1
	 */
	public UIUpdater getUIUpdater();

	/**
	 * @param viewID
	 * @param data
	 *
	 * @since 3.1.1.1
	 */
	void openView(int viewID, Object datasource);
}
