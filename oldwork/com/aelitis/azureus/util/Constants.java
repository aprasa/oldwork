/*
 * Created on Aug 30, 2006
 * Created by Alon Rohter
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
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */
package com.aelitis.azureus.util;

import java.util.Locale;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.config.ParameterListener;
import org.gudy.azureus2.core3.util.Base32;

import com.aelitis.azureus.core.AzureusCore;
import com.aelitis.azureus.core.crypto.VuzeCryptoManager;

/**
 * 
 */
public class Constants
{
	// isOS* constants copied from AZ2 for ease of use/access
	public static boolean isOSX = org.gudy.azureus2.core3.util.Constants.isOSX;

	public static boolean isWindows = org.gudy.azureus2.core3.util.Constants.isWindows;

	public static boolean isUnix = org.gudy.azureus2.core3.util.Constants.isUnix;

	public static final String DEFAULT_ADDRESS = "www.vuze.com"; //DO NOT TOUCH !!!!  use the -Dplatform_address=ip override instead

	public static final String DEFAULT_PORT = "80";

	public static final String DEFAULT_RELAY_ADDRESS = "www.vuze.com"; //DO NOT TOUCH !!!!  use the -Drelay_address=ip override instead

	public static final String DEFAULT_RELAY_PORT = "80";

	public static String URL_ADDRESS = System.getProperty("platform_address",
			DEFAULT_ADDRESS);

	public static String URL_PORT = System.getProperty("platform_port",
			DEFAULT_PORT);

	public static final String URL_PREFIX = "http://" + URL_ADDRESS + ":"
			+ URL_PORT + "/";

	public static final String DEFAULT_AUTHORIZED_RPC = "https://" + URL_ADDRESS
			+ ":443/rpc";

	public static String URL_SUFFIX;

	public static final String URL_ADD_SEARCH = "search?q=";

	public static final String URL_PLATFORM_MESSAGE = "?service=rpc";

	public static final String URL_POST_PLATFORM_DATA = "service=rpc";

	public static final String URL_BIG_BROWSE = "browse.start";

	public static final String URL_MINI_BROWSE = "dashboard.start";

	public static final String URL_PUBLISH = "publish.start";
	
	public static final String URL_WELCOME = "welcome.start";
	
	public static final String URL_PUBLISHED = "PublishedContent.html";

	public static final String URL_PUBLISHNEW = "PublishNewContent.html";

	public static final String URL_PUBLISH_ABOUT = "Publish.html";
	
	public static final String URL_DETAILS = "details/";

	public static final String URL_COMMENTS = "comment/";

	public static final String URL_SHARE = "share/";

	public static final String URL_PROFILE = "profile/";

	public static final String URL_BUDDY_ACCEPT = "buddy-accept/";

	public static final String URL_DOWNLOAD = "download/";

	public static final String URL_SUPPORT = URL_PREFIX + "support/";

	public static final String URL_FAQ = "http://faq.vuze.com/";
	
	/**
	 * To use this append the topic number to the end or use a pre-defined topic constant found in
	 * <code>FAQTopics</code>
	 */
	public static final String URL_FAQ_BY_TOPIC_ENTRY = "http://faq.vuze.com/?View=entry&EntryID=";

	public static final String URL_PUBLISH_INFO = URL_PREFIX + "Publish.html";

	public static final String URL_RPC = "rpc/";

	public static final String URL_BLOG = "http://blog.vuze.com/";
	
	public static final String URL_FORUMS = "http://forum.vuze.com/";
	
	public static final String URL_WIKI = "http://wiki.vuze.com/";
	
	public static String URL_RELAY_RPC = System.getProperty("relay_url",
			"http://" + System.getProperty("relay_address", DEFAULT_RELAY_ADDRESS)
					+ ":" + System.getProperty("relay_port", DEFAULT_RELAY_PORT)
					+ "/msgrelay/rpc");

	public static final String URL_AUTHORIZED_RPC = System.getProperty(
			"authorized_rpc", "1").equals("1") ? DEFAULT_AUTHORIZED_RPC : URL_PREFIX
			+ "app";

	public static final String URL_POP_UP = "?popup";

	public static final String URL_LOGIN = "login.start";

	public static final String URL_LOGOUT = "logout.start";

	public static final String URL_REGISTRATION = "register.start";

	public static final String URL_MY_PROFILE = "profile.start";

	public static final String URL_ACCOUNT = "account.start";

	// WARNING: TODO -- This is temporary and must be removed once the buddies features are complete
	public static final boolean DISABLE_BUDDIES_BAR = System.getProperty(
			"debug.buddies.bar", "1").equals("0");

	/**
	 * This verifier value is only used to validate that the page we're loading is
	 * in-fact a page from Vuze; mainly required by the LightBoxBrowserWindow
	 */
	public static final String URL_PAGE_VERIFIER_VALUE = "vuzePage";

	public static final boolean DIAG_TO_STDOUT = System.getProperty(
			"DIAG_TO_STDOUT", "0").equals("1");

	public static final String DL_REFERAL_PLAYDASHACTIVITY = "playdashboardactivity";
	
	public static final String DL_REFERAL_UNKNOWN = "unknown";

	public static final String DL_REFERAL_LAUNCH = "launch";

	public static final String DL_REFERAL_PLAYDM = "playdownloadmanager";

	public static final String DL_REFERAL_SELCONTENT = "selectedcontent";

	public static final String DL_REFERAL_DBLCLICK = "dblclick";

	public static final String DL_REFERAL_TOOLBAR = "toolbar";

	public static final String DL_REFERAL_DASHACTIVITY = "dashboardactivity";
	

	public static String AZID;

	public static void initialize(AzureusCore core) {
		AZID = Base32.encode(VuzeCryptoManager.getSingleton().getPlatformAZID());

		COConfigurationManager.addAndFireParameterListener("locale",
				new ParameterListener() {
					public void parameterChanged(String parameterName) {
						URL_SUFFIX = "azid=" + AZID + "&azv="
								+ org.gudy.azureus2.core3.util.Constants.AZUREUS_VERSION
								+ "&locale=" + Locale.getDefault().toString();
					}
				});
	}

	public static String appendURLSuffix(String url) {
		if (url.indexOf("azid=") < 0) {
			url += (url.indexOf('?') < 0 ? "?" : "&") + Constants.URL_SUFFIX;
		}
		return url;
	}
}
