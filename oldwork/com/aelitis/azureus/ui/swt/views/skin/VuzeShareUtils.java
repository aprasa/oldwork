package com.aelitis.azureus.ui.swt.views.skin;

import org.eclipse.swt.SWT;
import org.gudy.azureus2.core3.util.TorrentUtils;
import org.gudy.azureus2.ui.swt.Utils;

import com.aelitis.azureus.buddy.impl.VuzeBuddyManager;
import com.aelitis.azureus.core.messenger.config.PlatformBuddyMessenger;
import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
import com.aelitis.azureus.ui.selectedcontent.SelectedContentV3;
import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
import com.aelitis.azureus.ui.swt.shells.friends.ShareWizard;
import com.aelitis.azureus.ui.swt.utils.SWTLoginUtils;
import com.aelitis.azureus.util.Constants;

public class VuzeShareUtils
{

	private static VuzeShareUtils instance;

	private SharePage sharePage = null;

	public static VuzeShareUtils getInstance() {
		if (null == instance) {
			instance = new VuzeShareUtils();
		}
		return instance;
	}

	public void shareTorrent(ISelectedContent content, String referer) {
		if (content instanceof SelectedContentV3) {
			SelectedContentV3 sc = (SelectedContentV3) content;
			shareTorrent(sc, referer);
		} else if (content instanceof SelectedContent) {
			SelectedContent sc = (SelectedContent) content;
			shareTorrent(new SelectedContentV3(sc), referer);
		}
	}

	public void shareTorrent(final SelectedContentV3 currentContent,
			final String referer) {

		PlatformBuddyMessenger.startShare(referer,
				currentContent.isPlatformContent() ? currentContent.getHash() : null);

		if (!VuzeBuddyManager.isEnabled()) {
			VuzeBuddyManager.showDisabledDialog();
			return;
		}
		
		//TODO : Gudy : make sure that this private detection method is reliable enough
		if (currentContent.getDM() != null
				&& (TorrentUtils.isReallyPrivate(currentContent.getDM().getTorrent()))) {
			Utils.openMessageBox(Utils.findAnyShell(), SWT.OK, "v3.share.private",
					(String[]) null);
			return;
		}

		SWTLoginUtils.waitForLogin(new SWTLoginUtils.loginWaitListener() {
			public void loginComplete() {
				if (null != sharePage) {
					try {
						//sharePage.setShareItem(currentContent, referer);
						
						
						ShareWizard wizard = new ShareWizard(
								UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell(),
								SWT.DIALOG_TRIM | SWT.RESIZE);
						wizard.setText("Vuze - Wizard");
						wizard.setSize(500, 550);

						com.aelitis.azureus.ui.swt.shells.friends.SharePage newSharePage = (com.aelitis.azureus.ui.swt.shells.friends.SharePage) wizard.getPage(com.aelitis.azureus.ui.swt.shells.friends.SharePage.ID);
						newSharePage.setShareItem(currentContent, referer);

						/*
						 * Opens a centered free-floating shell
						 */

						UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
						if (null == uiFunctions) {
							/*
							 * Centers on the active monitor
							 */
							Utils.centreWindow(wizard.getShell());
						} else {
							/*
							 * Centers on the main application window
							 */
							Utils.centerWindowRelativeTo(wizard.getShell(),
									uiFunctions.getMainShell());
						}

						wizard.open();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public SharePage getSharePage() {
		return sharePage;
	}

	public void setSharePage(SharePage sharePage) {
		this.sharePage = sharePage;
	}

}
