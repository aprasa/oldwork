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

package com.aelitis.azureus.core.messenger;

import java.util.*;

import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SystemTime;

import com.aelitis.azureus.login.NotLoggedInException;
import com.aelitis.azureus.util.JSONUtils;
import com.aelitis.azureus.util.LoginInfoManager;

/**
 * @author TuxPaper
 * @created Sep 25, 2006
 *
 */
public class PlatformMessage
{
	private final String messageID;

	private final String listenerID;

	private final String operationID;

	private final Map parameters;

	private final long fireBeforeDate;

	private final long messageCreatedOn;

	private long lSequenceNo = -1;
	
	private boolean requiresAuthorization = false;
	
	private boolean loginAndRetry = false;

	/**
	 * @param messageID
	 * @param listenerID
	 * @param operationID
	 * @param parameters
	 * @param maxDelay
	 */
	public PlatformMessage(String messageID, String listenerID,
			String operationID, Map parameters, long maxDelayMS) {

		this.messageID = messageID;
		this.listenerID = listenerID;
		this.operationID = operationID;
		this.parameters = JSONUtils.encodeToJSONObject(parameters);

		messageCreatedOn = SystemTime.getCurrentTime();
		fireBeforeDate = messageCreatedOn + maxDelayMS;
	}

	public PlatformMessage(String messageID, String listenerID,
			String operationID, Object[] parameters, long maxDelayMS) {

		this.messageID = messageID;
		this.listenerID = listenerID;
		this.operationID = operationID;

		this.parameters = JSONUtils.encodeToJSONObject(parseParams(parameters));

		messageCreatedOn = SystemTime.getCurrentTime();
		fireBeforeDate = messageCreatedOn + maxDelayMS;
	}

	public static Map parseParams(Object[] parameters) {
		Map result = new HashMap();
		for (int i = 0; i < parameters.length - 1; i += 2) {
			try {
				if (parameters[i] instanceof String) {
					if (parameters[i + 1] instanceof String[]) {
						List list = Arrays.asList((String[]) parameters[i + 1]);
						result.put((String) parameters[i], list);
					} else if (parameters[i + 1] instanceof Object[]) {
						result.put((String) parameters[i],
								parseParams((Object[]) parameters[i + 1]));
					} else if (parameters[i + 1] instanceof Map) {
						result.put((String) parameters[i], (Map) parameters[i + 1]);
					} else {
						result.put((String) parameters[i], parameters[i + 1]);
					}
				}
			} catch (Exception e) {
				Debug.out("making JSONObject out of parsedParams", e);
			}
		}

		return result;
	}

	public long getFireBefore() {
		return fireBeforeDate;
	}

	public long getMessageCreated() {
		return messageCreatedOn;
	}

	public Map getParameters() {
		return parameters;
	}

	public String getListenerID() {
		return listenerID;
	}

	public String getMessageID() {
		return messageID;
	}

	public String getOperationID() {
		return operationID;
	}

	protected long getSequenceNo() {
		return lSequenceNo;
	}

	protected void setSequenceNo(long sequenceNo) {
		lSequenceNo = sequenceNo;
	}

	public String toString() {
		String paramString = parameters.toString();
		return "PlaformMessage {"
				+ lSequenceNo
				+ ", "
				+ messageID
				+ ", "
				+ listenerID
				+ ", "
				+ operationID
				+ ", "
				+ (paramString.length() > 32767 ? paramString.substring(0, 32767)
						: paramString) + "}";
	}

	/**
	 * @param requiresAuthorization the requiresAuthorization to set
	 * @throws NotLoggedInException 
	 */
	public void setRequiresAuthorization(
		boolean requiresAuthorization,
		boolean promptUser)
	throws NotLoggedInException {
		this.requiresAuthorization = requiresAuthorization;
		this.loginAndRetry = promptUser;

		if (!promptUser && !LoginInfoManager.getInstance().isLoggedIn()) {
			throw new NotLoggedInException();
		}
	}

	public void setRequiresAuthorizationNoCheck() {
		this.requiresAuthorization = true;
	}
	
	/**
	 * @return the requiresAuthorization
	 */
	public boolean requiresAuthorization() {
		return requiresAuthorization;
	}

	/**
	 * @param loginAndRetry the loginAndRetry to set
	 */
	public void setLoginAndRetry(boolean loginAndRetry) {
		this.loginAndRetry = loginAndRetry;
	}

	/**
	 * @return the loginAndRetry
	 */
	public boolean getLoginAndRetry() {
		return loginAndRetry;
	}
	
	public String toShortString() {
		return (requiresAuthorization ? "AUTH: " : "") + getMessageID() + "."
				+ getListenerID() + "." + getOperationID();
	}
}
