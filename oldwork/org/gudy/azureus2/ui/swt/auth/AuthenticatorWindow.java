/*
 * File    : AuthenticatorWindow.java
 * Created : 25-Nov-2003
 * By      : parg
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.ui.swt.auth;

/**
 * @author parg
 *
 */

import java.net.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import org.gudy.azureus2.ui.swt.*;
import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;

import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.torrent.*;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.security.*;

import org.bouncycastle.util.encoders.Base64;

import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;

public class 
AuthenticatorWindow 
	implements SEPasswordListener
{
	private static final String	CONFIG_PARAM = "swt.auth.persistent.cache";
	
	protected Map	auth_cache = new HashMap();
	
	protected AEMonitor	this_mon	= new AEMonitor( "AuthWind" );
		
	public
	AuthenticatorWindow()
	{
		SESecurityManager.addPasswordListener( this );
		
		// System.out.println( "AuthenticatorWindow");
		
		Map	cache = COConfigurationManager.getMapParameter( CONFIG_PARAM, new HashMap());
		
		try{
			Iterator	it = cache.entrySet().iterator();
			
			while( it.hasNext()){
				
				Map.Entry entry	= (Map.Entry)it.next();
				
				String	key 	= (String)entry.getKey();
				Map		value 	= (Map)entry.getValue();
				
				String	user = new String((byte[])value.get( "user" ), "UTF-8" );
				char[]	pw	 = new String((byte[])value.get( "pw" ), "UTF-8" ).toCharArray();
				
				auth_cache.put( key, new authCache(	key, new PasswordAuthentication( user, pw ), true ));
			}
		
		}catch( Throwable e ){
			
			COConfigurationManager.setParameter( CONFIG_PARAM, new HashMap());
			
			Debug.printStackTrace(e);
		}
	}
	
	protected void
	saveAuthCache()
	{
		try{
			this_mon.enter();
	
			HashMap	map = new HashMap();

			Iterator	it = auth_cache.values().iterator();
			
			while( it.hasNext()){
				
				authCache	value 	= (authCache)it.next();
				
				if ( value.isPersistent()){
					
					try{
						HashMap	entry_map = new HashMap();
						
						entry_map.put( "user", value.getAuth().getUserName().getBytes( "UTF-8" ));
						entry_map.put( "pw", new String(value.getAuth().getPassword()).getBytes( "UTF-8" ));
						
						map.put( value.getKey(), entry_map );
						
					}catch( Throwable e ){
						
						Debug.printStackTrace(e);
					}
				}
			}
			
			COConfigurationManager.setParameter( CONFIG_PARAM, map );
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public void
	clearPasswords()
	{
		try{
			this_mon.enter();

			auth_cache = new HashMap();
			
			saveAuthCache();
			
		}finally{
			
			this_mon.exit();
		}	
	}
	
	public PasswordAuthentication
	getAuthentication(
		String		realm,
		URL			tracker )
	{
		try{
			this_mon.enter();
	
			return( getAuthentication( realm, tracker.getProtocol(), tracker.getHost(), tracker.getPort()));
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public void
	setAuthenticationOutcome(
		String		realm,
		URL			tracker,
		boolean		success )
	{
		try{
			this_mon.enter();
		
			setAuthenticationOutcome( realm, tracker.getProtocol(), tracker.getHost(), tracker.getPort(), success );
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public void
	setAuthenticationOutcome(
		String		realm,
		String		protocol,
		String		host,
		int			port,
		boolean		success )
	{
		try{
			this_mon.enter();
		
			String	tracker = protocol + "://" + host + ":" + port + "/";
			
			String auth_key = realm+":"+tracker;
			
			authCache	cache = (authCache)auth_cache.get( auth_key );
	
			if ( cache != null ){
	
				cache.setOutcome( success );
			}
		}finally{
			
			this_mon.exit();
		}
	}
	
	public PasswordAuthentication
	getAuthentication(
		String		realm,
		String		protocol,
		String		host,
		int			port )
	{
		try{
			this_mon.enter();
	
			String	tracker = protocol + "://" + host + ":" + port + "/";
	
			InetAddress bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
			
			String	self_addr;
	
			// System.out.println( "auth req for " + realm + " - " + tracker );
			
			if ( bind_ip == null || bind_ip.isAnyLocalAddress()){

				self_addr = "127.0.0.1";
		
			}else{
		
				self_addr = bind_ip.getHostAddress();
			}
	
				// when the tracker is connected to internally we don't want to prompt
				// for the password. Here we return a special user and the password hash
				// which is picked up in the tracker auth code - search for "<internal>"!
				
				// also include the tracker IP as well as for scrapes these can occur on
				// a raw torrent which hasn't been modified to point to localhost
			
			if ( 	host.equals(self_addr) ||
					host.equals(COConfigurationManager.getStringParameter("Tracker IP", ""))){
			
				try{
					byte[]	pw	= COConfigurationManager.getByteParameter("Tracker Password", new byte[0]);
				
					String str_pw = new String( Base64.encode(pw));
					
					return( new PasswordAuthentication( "<internal>", str_pw.toCharArray()));
						
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
				}	
			}
			
			String auth_key = realm+":"+tracker;
								
			authCache	cache = (authCache)auth_cache.get( auth_key );
						
			if ( cache != null ){
				
				PasswordAuthentication	auth = cache.getAuth();
				
				if ( auth != null ){
					
					return( auth );
				}
			}
				
			String[]	res = getAuthenticationDialog( realm, tracker );
				
			if ( res == null ){
				
				return( null );
				
			}else{
								
				PasswordAuthentication auth =  new PasswordAuthentication( res[0], res[1].toCharArray());
				
				boolean	save_pw = res[2].equals("true");
				
				boolean	old_entry_existed = auth_cache.put( auth_key, new authCache( auth_key, auth, save_pw )) != null;
				
				if ( save_pw || old_entry_existed ){
					
					saveAuthCache();
				}
				
				return( auth );
			}	
		}finally{
			
			this_mon.exit();
		}
	}
	
	
	protected String[]
	getAuthenticationDialog(
		final String		realm,
		final String		location )
	{
		final Display	display = SWTThread.getInstance().getDisplay();
		
		if ( display.isDisposed()){
			
			return( null );
		}
		
		final AESemaphore	sem = new AESemaphore("SWTAuth");
		
		final authDialog[]	dialog = new authDialog[1];
		
		TOTorrent		torrent = TorrentUtils.getTLSTorrent();
		
		final	boolean	is_tracker;
		final	String	details;
		
		if ( torrent == null ){
			
			is_tracker 	= false;
			
			details		= TorrentUtils.getTLSDescription();
			
		}else{
			
			details		= TorrentUtils.getLocalisedName( torrent );
			is_tracker	= true;
		}
		
		try{
			display.asyncExec(
				new AERunnable()
				{
					public void
					runSupport()
					{
						dialog[0] = new authDialog( sem, display, realm, is_tracker, location, details );
					}
				});
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
			
			return( null );
		}
		
		sem.reserve();
		
		String	user 	= dialog[0].getUsername();
		String	pw		= dialog[0].getPassword();
		String	persist	= dialog[0].savePassword()?"true":"false";
		
		if ( user == null ){
			
			return( null );
		}
		
		return( new String[]{ user, pw == null?"":pw, persist });
	}
	
	protected class
	authDialog
	{
		private Shell			shell;
		private AESemaphore		sem;
		
		private String		username;
		private String		password;
		private	boolean		persist;
		
		protected
		authDialog(
			AESemaphore		_sem,
			Display			display,
			String			realm,
			boolean			is_tracker,
			String			target,
			String			details )
		{
			sem	= _sem;
			
			if ( display.isDisposed()){
				
				sem.release();
				
				return;
			}
			
	 		shell = new Shell (display,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	 	
	 		Utils.setShellIcon(shell);
		 	Messages.setLanguageText(shell, "authenticator.title");
    		
		 	GridLayout layout = new GridLayout();
		 	layout.numColumns = 3;
		        
		 	shell.setLayout (layout);
	    
		 	GridData gridData;
	    
	    		// realm
	    		
			Label realm_label = new Label(shell,SWT.NULL);
			Messages.setLanguageText(realm_label, "authenticator.realm");
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			realm_label.setLayoutData(gridData);
			
			Label realm_value = new Label(shell,SWT.NULL);
			realm_value.setText(realm.replaceAll("&", "&&"));
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			realm_value.setLayoutData(gridData);
	    
	    		// target
			
			Label target_label = new Label(shell,SWT.NULL);
			Messages.setLanguageText(target_label, is_tracker?"authenticator.tracker":"authenticator.location");
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			target_label.setLayoutData(gridData);
			
			Label target_value = new Label(shell,SWT.NULL);
			target_value.setText(target.replaceAll("&", "&&"));
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			target_value.setLayoutData(gridData);
	    		
			if ( details != null ){
				
				Label details_label = new Label(shell,SWT.NULL);
				Messages.setLanguageText(details_label, is_tracker?"authenticator.torrent":"authenticator.details");
				gridData = new GridData(GridData.FILL_BOTH);
				gridData.horizontalSpan = 1;
				details_label.setLayoutData(gridData);
				
				Label details_value = new Label(shell,SWT.NULL);
				details_value.setText(details.replaceAll("&", "&&"));
				gridData = new GridData(GridData.FILL_BOTH);
				gridData.horizontalSpan = 2;
				details_value.setLayoutData(gridData);
			}
	    		// user
	    		
			Label user_label = new Label(shell,SWT.NULL);
			Messages.setLanguageText(user_label, "authenticator.user");
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			user_label.setLayoutData(gridData);
	
			final Text user_value = new Text(shell,SWT.BORDER);
			user_value.setText("");
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			user_value.setLayoutData(gridData);

			user_value.addListener(SWT.Modify, new Listener() {
			   public void handleEvent(Event event) {
				 username = user_value.getText();
			   }});

				// password
	    		
			Label password_label = new Label(shell,SWT.NULL);
			Messages.setLanguageText(password_label, "authenticator.password");
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			password_label.setLayoutData(gridData);
			
			final Text password_value = new Text(shell,SWT.BORDER);
			password_value.setEchoChar('*');
			password_value.setText("");
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			password_value.setLayoutData(gridData);

			password_value.addListener(SWT.Modify, new Listener() {
			   public void handleEvent(Event event) {
				 password = password_value.getText();
			   }});
			   
			// persist
			
			Label blank_label = new Label(shell,SWT.NULL);
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 1;
			blank_label.setLayoutData(gridData);
			
		    final Button checkBox = new Button(shell, SWT.CHECK);
		    checkBox.setText(MessageText.getString( "authenticator.savepassword" ));
			gridData = new GridData(GridData.FILL_BOTH);
			gridData.horizontalSpan = 2;
			checkBox.setLayoutData(gridData);
			checkBox.addListener(SWT.Selection,new Listener() {
		  		public void handleEvent(Event e) {
			 		persist = checkBox.getSelection();
		   		}
			 });
			
			// line
			
			Label labelSeparator = new Label(shell,SWT.SEPARATOR | SWT.HORIZONTAL);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			labelSeparator.setLayoutData(gridData);
			
				// buttons
				
			new Label(shell,SWT.NULL);

			Button bOk = new Button(shell,SWT.PUSH);
		 	Messages.setLanguageText(bOk, "Button.ok");
		 	gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END | GridData.HORIZONTAL_ALIGN_FILL);
		 	gridData.grabExcessHorizontalSpace = true;
		 	gridData.widthHint = 70;
		 	bOk.setLayoutData(gridData);
		 	bOk.addListener(SWT.Selection,new Listener() {
		  		public void handleEvent(Event e) {
			 		close(true);
		   		}
			 });
	    
		 	Button bCancel = new Button(shell,SWT.PUSH);
		 	Messages.setLanguageText(bCancel, "Button.cancel");
		 	gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		 	gridData.grabExcessHorizontalSpace = false;
		 	gridData.widthHint = 70;
		 	bCancel.setLayoutData(gridData);    
		 	bCancel.addListener(SWT.Selection,new Listener() {
		 		public void handleEvent(Event e) {
			 		close(false);
		   		}
		 	});
	    
			shell.setDefaultButton( bOk );
			
			shell.addListener(SWT.Traverse, new Listener() {	
				public void handleEvent(Event e) {
					if ( e.character == SWT.ESC){
						close( false );
					}
				}
			});

		
		 	shell.pack ();
		 	
			Utils.centreWindow( shell );

			shell.open ();   
		}
   
		protected void
		close(
			boolean		ok )
	 	{
	 		if ( ok ){
	 			
	 			if ( username == null ){
	 				
	 				username = "";
	 			}
	 			
	 			if ( password == null ){
	 				
	 				password = "";
	 			}
	 			
	 		}else{
	 			
	 			username	= null;
	 			password	= null;
	 		}
	 		
	 		shell.dispose();
	 		sem.release();
	 	}
	 	
	 	protected String
	 	getUsername()
	 	{
	 		return( username );
	 	}
	 	
	 	protected String
	 	getPassword()
	 	{
	 		return( password );
	 	}
	 	
	 	protected boolean
	 	savePassword()
	 	{
	 		return( persist );
	 	}
	}
	
	protected class
	authCache
	{
		private String					key;
		private PasswordAuthentication	auth;
		private boolean					persist;
		private int						life = 5;
		private boolean					succeeded;
		
		protected
		authCache(
			String						_key,
			PasswordAuthentication		_auth,
			boolean						_persist )
		{
			key			= _key;
			auth		= _auth;
			persist		= _persist;
		}
		
		protected String
		getKey()
		{
			return( key );
		}
		
		protected boolean
		isPersistent()
		{
			return( persist );
		}
		
		protected void
		setOutcome(
			boolean	success)
		{
			if ( success ){
				
				succeeded	= true;
				
			}else{
				
				if ( persist ){
					
					persist	= false;
					
					saveAuthCache();
				}
				
				if ( !succeeded ){
					
					auth	= null;
				}
			}
		}
		
		protected PasswordAuthentication
		getAuth()
		{
			if ( succeeded ){
				
				return( auth );
			}
			
			life--;
			
			if ( life >= 0 ){
				
				return( auth );
			}
			
			if ( persist ){
				
				persist	= false;
				
				saveAuthCache();
			}
			
			return( null );
		}
	}
		
}
