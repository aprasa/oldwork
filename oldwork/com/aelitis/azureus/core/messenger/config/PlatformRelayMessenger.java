/**
 * Created on Apr 17, 2008
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

package com.aelitis.azureus.core.messenger.config;

import java.util.*;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.buddy.VuzeBuddy;
import com.aelitis.azureus.buddy.impl.VuzeBuddyManager;
import com.aelitis.azureus.core.crypto.VuzeCryptoException;
import com.aelitis.azureus.core.crypto.VuzeCryptoManager;
import com.aelitis.azureus.core.messenger.PlatformMessage;
import com.aelitis.azureus.core.messenger.PlatformMessenger;
import com.aelitis.azureus.core.messenger.PlatformMessengerListener;
import com.aelitis.azureus.core.security.CryptoHandler;
import com.aelitis.azureus.core.security.CryptoManagerFactory;
import com.aelitis.azureus.login.NotLoggedInException;
import com.aelitis.azureus.plugins.net.buddy.*;
import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin.cryptoResult;
import com.aelitis.azureus.util.LoginInfoManager;
import com.aelitis.azureus.util.MapUtils;

/**
 * @author TuxPaper
 * @created Apr 17, 2008
 *
 */
public class PlatformRelayMessenger
{
	public static final String MSG_ID = "AZMSG";

	public static final String LISTENER_ID = "relay";

	public static final long DEFAULT_RECHECKIN_MINS = 30;

	private static final long ERROR_RECHECKIN_MINS = 90;

	private static final boolean TEST_ERRORACK = System.getProperty(
			"relay.errack.test", "0").equals("1");

	public static String OP_FETCH = "fetch";

	public static String OP_PUT = "put";

	public static String OP_ACK = "ack";

	public static String OP_ERRORACK = "error_ack";

	public static String OP_COUNT = "count";

	public static List listeners = new ArrayList();

	private static TimerEventPerformer relayCheckPerformer;

	private static TimerEvent timerEvent;

	private static long lastPendingCount = 0;

	static {
		relayCheckPerformer = new TimerEventPerformer() {
			public void perform(TimerEvent event) {
				relayCheck();
			}
		};
	}

	/**
	 * Put a message on the relay server.
	 * <p>
	 * Note: There should be only one pk if your payload is encrypted or
	 *       has a buddyMessage
	 * 
	 * @param pks
	 * @param buddyMessage
	 * @param maxDelayMS
	 *
	 * @since 3.0.5.3
	 */

	public static final void put(final BuddyPluginBuddyMessage buddyMessage,
			long maxDelayMS, final putListener putListener) {
		try {
			// if ( true ) throw( new Exception( "bork bork" ));
			final String myPK = VuzeCryptoManager.getSingleton().getPublicKey(
					"RelayMessenger put");

			BuddyPluginBuddy pluginBuddy = buddyMessage.getBuddy();

			final String pk = pluginBuddy.getPublicKey();

			byte[] encode = BEncoder.encode(buddyMessage.getRequest());

			cryptoResult encryptResult = pluginBuddy.encrypt(encode);

			final Map mapParameters = new HashMap();
			mapParameters.put("sender_pk", myPK);
			mapParameters.put("recipient_pk", pk);
			mapParameters.put("payload", Base32.encode(encryptResult.getPayload()));
			mapParameters.put("ack_hash", Base32.encode(encryptResult.getChallenge()));

			PlatformMessage message = new PlatformMessage(MSG_ID, LISTENER_ID,
					OP_PUT, mapParameters, maxDelayMS) {
				// @see com.aelitis.azureus.core.messenger.PlatformMessage#toString()
				public String toString() {
					return "PlaformMessage {" + getSequenceNo() + ", " + getMessageID()
							+ ", " + getListenerID() + ", " + getOperationID() + ", sender="
							+ myPK + ", recipient=" + pk + ", ack_hash="
							+ mapParameters.get("ack_hash") + "}";
				}
			};

			PlatformMessengerListener listener = new PlatformMessengerListener() {
				public void messageSent(PlatformMessage message) {
				}

				public void replyReceived(PlatformMessage message, String replyType,
						Map reply) {
					boolean ok = false;

					try {
						String replyMessage = MapUtils.getMapString(reply, "message", null);

						if (replyMessage != null && replyMessage.equals("Ok")) {
							// good
							PlatformMessenger.debug("Relay: Ok to " + pk);

							putListener.putOK(buddyMessage);

							ok = true;

						} else {
							// bad
							PlatformMessenger.debug("Relay: FAILED for " + pk);

						}
					} finally {

						if (!ok) {

							putListener.putFailed(buddyMessage, new Exception(
									"Reply indicated failure: " + reply));
						}
					}
				}

			};

			PlatformMessenger.queueMessage(message, listener);

		} catch (Throwable e) {

			putListener.putFailed(buddyMessage, e);
		}
	}

	public static final void fetch(long maxDelayMS)
			throws NotLoggedInException {
		if (!LoginInfoManager.getInstance().isLoggedIn()
				|| PlatformMessenger.isAuthorizedDelayed()) {
			resetTimerEvent(DEFAULT_RECHECKIN_MINS);
			throw new NotLoggedInException();
		}

		String myPK;
		try {
			myPK = VuzeCryptoManager.getSingleton().getPublicKey(null);
		} catch (VuzeCryptoException e) {
			resetTimerEvent(DEFAULT_RECHECKIN_MINS);
			Debug.out(e);
			return;
		}

		PlatformMessage message = new PlatformMessage(MSG_ID, LISTENER_ID,
				OP_FETCH, new Object[] {
					"pk",
					myPK
				}, maxDelayMS);

		final BuddyPlugin buddyPlugin = VuzeBuddyManager.getBuddyPlugin();
		if (buddyPlugin == null) {
			resetTimerEvent(DEFAULT_RECHECKIN_MINS);
			return;
		}

		PlatformMessengerListener listener = new PlatformMessengerListener() {

			public void messageSent(PlatformMessage message) {
			}

			public void replyReceived(PlatformMessage message, String replyType,
					Map reply) {
				List list = (List) MapUtils.getMapObject(reply, "messages",
						Collections.EMPTY_LIST, List.class);
				long recheckInMins = MapUtils.getMapLong(reply, "recheck-in-mins",
						PlatformMessenger.REPLY_EXCEPTION.equals(replyType)
								? ERROR_RECHECKIN_MINS : DEFAULT_RECHECKIN_MINS);

				resetTimerEvent(recheckInMins);

				for (Iterator iter = list.iterator(); iter.hasNext();) {
					Map map = (Map) iter.next();

					String pkSender = MapUtils.getMapString(map, "sender", null);
					long addedOn = SystemTime.getOffsetTime(MapUtils.getMapLong(map,
							"added-secs-ago", 0)
							* -1000);
					VuzeBuddy buddy = VuzeBuddyManager.getBuddyByPK(pkSender);

					BuddyPluginBuddy pluginBuddy = buddyPlugin.getBuddyFromPublicKey(pkSender);

					long ack_id = MapUtils.getMapLong(map, "id", -1);
					byte[] enc_payload = Base32.decode(MapUtils.getMapString(map,
							"payload", ""));

					cryptoResult decrypt;
					try {
						if (pluginBuddy == null) {
							decrypt = buddyPlugin.decrypt(pkSender, enc_payload);
						} else {
							decrypt = pluginBuddy.decrypt(enc_payload);
						}

						byte[] payload = decrypt.getPayload();

						Map decodedMap = BDecoder.decode(payload);

						PlatformMessenger.debug("Relay: got message from " + pkSender);

						if (TEST_ERRORACK) {
							errorAck(ack_id);
						} else {
							ack(ack_id, decrypt.getChallenge());
						}

						for (Iterator iter2 = listeners.iterator(); iter2.hasNext();) {
							VuzeRelayListener l = (VuzeRelayListener) iter2.next();
							l.newRelayServerPayLoad(buddy, pkSender, decodedMap, addedOn);
						}


					} catch (BuddyPluginPasswordException e) {

						// TODO we don't want to negative ack the message when we failed
						// to decrypt because not logged in...

					} catch (Exception e) {
						PlatformMessenger.debug("Relay: send ack_fail: " + e.toString());
						errorAck(ack_id);
					}
				}
				// "date" also sent, but not needed (?)
			}
		};
		PlatformMessenger.queueMessage(message, listener);
	}

	/**
	 * 
	 *
	 * @param recheckInMins 
	 * @since 3.0.5.3
	 */
	protected static void resetTimerEvent(long recheckInMins) {
		PlatformMessenger.debug("Relay: rechecking in " + recheckInMins + "m");

		if (timerEvent != null) {
			timerEvent.cancel();
		}
		timerEvent = SimpleTimer.addEvent("Relay Server Check",
				SystemTime.getOffsetTime(recheckInMins * 1000l * 60),
				relayCheckPerformer);
	}

	public static final void addRelayServerListener(VuzeRelayListener l) {
		listeners.add(l);
	}

	public static final void removeRelayServerListener(VuzeRelayListener l) {
		listeners.remove(l);
	}

	private static final void ack(long id, byte[] ack) {
		String myPK;
		try {
			myPK = VuzeCryptoManager.getSingleton().getPublicKey(null);
		} catch (VuzeCryptoException e) {
			Debug.out(e);
			return;
		}

		Map mapACK = new HashMap();
		mapACK.put("id", new Long(id));
		mapACK.put("ack", Base32.encode(ack));

		Map mapParameters = new HashMap();
		mapParameters.put("recipient_pk", myPK);
		mapParameters.put("acks", new Object[] {
			mapACK
		});

		PlatformMessengerListener listener = new PlatformMessengerListener() {

			public void replyReceived(PlatformMessage message, String replyType,
					Map reply) {
				int numDeleted = MapUtils.getMapInt(reply, "deleted", 0);
				PlatformMessenger.debug("Relay: deleted " + numDeleted);
			}

			public void messageSent(PlatformMessage message) {
			}
		};

		PlatformMessage message = new PlatformMessage(MSG_ID, LISTENER_ID, OP_ACK,
				mapParameters, 500);

		PlatformMessenger.queueMessage(message, listener);
	}

	private static final void errorAck(long id) {
		String myPK;
		try {
			myPK = VuzeCryptoManager.getSingleton().getPublicKey("errorAck");
		} catch (VuzeCryptoException e) {
			Debug.out(e);
			return;
		}

		// The ack is a String version of the id, encrypted, and base32 encoded
		byte[] encryptBytes;
		try {
			byte[] content = String.valueOf(id).getBytes("UTF-8");
			CryptoHandler ecc_handler = CryptoManagerFactory.getSingleton().getECCHandler();
			encryptBytes = ecc_handler.sign(content, "Encrypting message for " + myPK);

			//try {
			//	System.err.println("Verify says: "
			//			+ ecc_handler.verify(Base32.decode(myPK), content, encryptBytes));
			//} catch (CryptoManagerException ee) {
			//	ee.printStackTrace();
			//}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		String s = Base32.encode(encryptBytes);

		Map mapACK = new HashMap();
		mapACK.put("id", new Long(id));
		mapACK.put("signature", s);

		Map mapParameters = new HashMap();
		mapParameters.put("recipient_pk", myPK);
		mapParameters.put("error_acks", new Object[] {
			mapACK
		});

		PlatformMessengerListener listener = new PlatformMessengerListener() {

			public void replyReceived(PlatformMessage message, String replyType,
					Map reply) {
				int numDeleted = MapUtils.getMapInt(reply, "deleted", 0);
				PlatformMessenger.debug("Relay: deleted " + numDeleted);
			}

			public void messageSent(PlatformMessage message) {
			}
		};

		PlatformMessage message = new PlatformMessage(MSG_ID, LISTENER_ID,
				OP_ERRORACK, mapParameters, 500);

		PlatformMessenger.queueMessage(message, listener);
	}

	public static void relayCheck() {
		if (!VuzeCryptoManager.getSingleton().hasPublicKey()) {
			return;
		}

		String myPK;
		try {
			myPK = VuzeCryptoManager.getSingleton().getPublicKey(null);
		} catch (VuzeCryptoException e) {
			Debug.out(e);
			return;
		}

		PlatformMessengerListener listener = new PlatformMessengerListener() {

			public void replyReceived(PlatformMessage message, String replyType,
					Map reply) {
				int count = MapUtils.getMapInt(reply, "count", 0);
				long recheckInMins = MapUtils.getMapLong(reply, "recheck-in-mins",
						PlatformMessenger.REPLY_EXCEPTION.equals(replyType)
								? ERROR_RECHECKIN_MINS : DEFAULT_RECHECKIN_MINS);
				resetTimerEvent(recheckInMins);

				if (count > 0 || lastPendingCount != count) {
					lastPendingCount = count;
					for (Iterator iter2 = listeners.iterator(); iter2.hasNext();) {
						VuzeRelayListener l = (VuzeRelayListener) iter2.next();
						l.hasPendingRelayMessage(count);
					}
				}
			}

			public void messageSent(PlatformMessage message) {
			}
		};

		PlatformMessage message = new PlatformMessage(MSG_ID, LISTENER_ID,
				OP_COUNT, new Object[] {
					"pk",
					myPK
				}, 0);

		PlatformMessenger.queueMessage(message, listener);
	}

	public interface putListener
	{
		public void putOK(BuddyPluginBuddyMessage message);

		public void putFailed(BuddyPluginBuddyMessage message, Throwable cause);
	}
}
