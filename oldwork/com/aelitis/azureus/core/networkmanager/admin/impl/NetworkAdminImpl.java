/*
 * Created on 1 Nov 2006
 * Created by Paul Gardner
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
 * AELITIS, SAS au capital de 63.529,40 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */


package com.aelitis.azureus.core.networkmanager.admin.impl;

import java.io.PrintWriter;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.*;
import java.util.regex.Pattern;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.config.ParameterListener;
import org.gudy.azureus2.core3.logging.LogAlert;
import org.gudy.azureus2.core3.logging.LogEvent;
import org.gudy.azureus2.core3.logging.LogIDs;
import org.gudy.azureus2.core3.logging.Logger;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.platform.PlatformManager;
import org.gudy.azureus2.platform.PlatformManagerCapabilities;
import org.gudy.azureus2.platform.PlatformManagerFactory;
import org.gudy.azureus2.platform.PlatformManagerPingCallback;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.platform.PlatformManagerException;

import com.aelitis.azureus.core.AzureusCore;
import com.aelitis.azureus.core.AzureusCoreFactory;
import com.aelitis.azureus.core.instancemanager.AZInstance;
import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
import com.aelitis.azureus.core.instancemanager.AZInstanceManagerListener;
import com.aelitis.azureus.core.instancemanager.AZInstanceTracked;
import com.aelitis.azureus.core.networkmanager.admin.*;
import com.aelitis.azureus.core.networkmanager.impl.http.HTTPNetworkManager;
import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
import com.aelitis.azureus.core.proxy.socks.AESocksProxy;
import com.aelitis.azureus.core.proxy.socks.AESocksProxyFactory;
import com.aelitis.azureus.core.util.CopyOnWriteList;
import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
import com.aelitis.azureus.plugins.upnp.UPnPPluginService;

public class 
NetworkAdminImpl
	extends NetworkAdmin
	implements AEDiagnosticsEvidenceGenerator
{
	private static final LogIDs LOGID = LogIDs.NWMAN;
	
	private static final boolean	FULL_INTF_PROBE	= false;
	
	private Set							old_network_interfaces;
	private InetAddress[]				currentBindIPs			= new InetAddress[] { null };
	private boolean						supportsIPv6withNIO		= true;
	private boolean						supportsIPv6 = true;
	private boolean						supportsIPv4 = true;
	
	private CopyOnWriteList	listeners = new CopyOnWriteList();
		
	private NetworkAdminRouteListener
		trace_route_listener = new NetworkAdminRouteListener()
		{
			private int	node_count = 0;
			
			public boolean
			foundNode(
				NetworkAdminNode	node,
				int					distance,
				int					rtt )
			{
				node_count++;
				
				return( true );
			}
			
			public boolean
			timeout(
				int					distance )
			{
				if ( distance == 3 && node_count == 0 ){
					
					return( false );
				}
				
				return( true );
			}
		};
		
	private static final int ASN_MIN_CHECK = 30*60*1000;
	
	private long last_asn_lookup_time;
	
	private List asn_ips_checked = new ArrayList(0);
	
	private List as_history = new ArrayList();
	
		
	public
	NetworkAdminImpl()
	{
		COConfigurationManager.addParameterListener(
			new String[] {"Bind IP","Enforce Bind IP"},
			new ParameterListener()
			{
				public void 
				parameterChanged(
					String parameterName )
				{
					checkDefaultBindAddress( false );
				}
			});
		
		SimpleTimer.addPeriodicEvent(
			"NetworkAdmin:checker",
			15000,
			new TimerEventPerformer()
			{
				public void 
				perform(
					TimerEvent event )
				{
					checkNetworkInterfaces( false );
				}
			});
		
			// populate initial values
		
		checkNetworkInterfaces(true);
		
		checkDefaultBindAddress(true);
		
		AEDiagnostics.addEvidenceGenerator( this );
	}
	
	protected void
	checkNetworkInterfaces(
		boolean	first_time )
	{
		try{
			Enumeration 	nis = NetworkInterface.getNetworkInterfaces();
		
			boolean	changed	= false;

			if ( nis == null && old_network_interfaces == null ){
				
			}else if ( nis == null ){
				
				old_network_interfaces	= null;
					
				changed = true;
					
			}else if ( old_network_interfaces == null ){
				
				Set	new_network_interfaces = new HashSet();
				
				while( nis.hasMoreElements()){

					new_network_interfaces.add( nis.nextElement());
				}
				
				old_network_interfaces = new_network_interfaces;
				
				changed = true;
				
			}else{
				
				Set	new_network_interfaces = new HashSet();
				
				while( nis.hasMoreElements()){
					
					Object	 ni = nis.nextElement();
					
						// NetworkInterface's "equals" method is based on ni name + addresses
					
					if ( !old_network_interfaces.contains( ni )){
						
						changed	= true;
					}
					
					new_network_interfaces.add( ni );
				}
					
				if ( old_network_interfaces.size() != new_network_interfaces.size()){
					
					changed = true;
				}
				
				old_network_interfaces = new_network_interfaces;
			}
			
			if ( changed ){
				
				
				boolean newV6 = false;
				boolean newV4 = false;
				
				Set interfaces = old_network_interfaces;
				if (interfaces != null)
				{
					Iterator it = interfaces.iterator();
					while (it.hasNext())
					{
						NetworkInterface ni = (NetworkInterface) it.next();
						Enumeration addresses = ni.getInetAddresses();
						while (addresses.hasMoreElements())
						{
							InetAddress ia = (InetAddress) addresses.nextElement();

							if (ia.isLoopbackAddress())
								continue;
							if (ia instanceof Inet6Address && !ia.isLinkLocalAddress())
								newV6 = true;
							else if (ia instanceof Inet4Address)
								newV4 = true;
						}
					}
				}
				
				supportsIPv4 = newV4;
				supportsIPv6 = newV6;
				
				Logger.log(new LogEvent(LOGID, "NetworkAdmin: ipv4 supported: "+supportsIPv4+"; ipv6: "+supportsIPv6+"; probing v6+nio functionality"));
				
				if(newV6)
				{
					ServerSocketChannel channel = ServerSocketChannel.open();
					
					try
					{
						channel.configureBlocking(false);
						channel.socket().bind(new InetSocketAddress(anyLocalAddressIPv6, 0));
						Logger.log(new LogEvent(LOGID, "NetworkAdmin: testing nio + ipv6 bind successful"));

						supportsIPv6withNIO = true;
					} catch (Exception e)
					{
						Logger.log(new LogEvent(LOGID,LogEvent.LT_WARNING, "nio + ipv6 test failed",e));
						supportsIPv6withNIO = false;
					}
					
					channel.close();
				} else
					supportsIPv6withNIO = false;
					
				if ( !first_time ){
					
					Logger.log(
						new LogEvent(LOGID,
								"NetworkAdmin: network interfaces have changed" ));
				}
				
				firePropertyChange( NetworkAdmin.PR_NETWORK_INTERFACES );
				
				checkDefaultBindAddress( first_time );
			}
		}catch( Throwable e ){
		}
	}
	
	private int roundRobinCounterV4 = 0;
	private int roundRobinCounterV6 = 0;
	
	public InetAddress getMultiHomedOutgoingRoundRobinBindAddress(InetAddress target)
	{
		InetAddress[]	addresses = currentBindIPs;
		boolean v6 = target instanceof Inet6Address; 
		int previous = (v6 ? roundRobinCounterV6 : roundRobinCounterV4) % addresses.length;
		InetAddress toReturn = null;
		
		int i = previous;

		do
		{
			i++;i%= addresses.length;
			if (target == null || (v6 && addresses[i] instanceof Inet6Address) || (!v6 && addresses[i] instanceof Inet4Address))
			{
				toReturn = addresses[i];
				break;
			} else if(!v6 && addresses[i].isAnyLocalAddress())
			{
				toReturn = anyLocalAddressIPv4;
				break;				
			}
		} while(i!=previous);
			
		if(v6)
			roundRobinCounterV6 = i;
		else
			roundRobinCounterV4 = i;
		return toReturn != null ? toReturn : (v6 ? localhostV6 : localhostV4);
	}
	
	private static InetAddress anyLocalAddressIPv4;
	private static InetAddress anyLocalAddressIPv6;
	private static InetAddress localhostV4;
	private static InetAddress localhostV6;
	
	static
	{
		try
		{
			anyLocalAddressIPv4 = InetAddress.getByAddress(new byte[] { 0,0,0,0 });
			anyLocalAddressIPv6  = InetAddress.getByAddress(new byte[] {0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0});
			localhostV4 = InetAddress.getByAddress(new byte[] {127,0,0,1});
			localhostV6 = InetAddress.getByAddress(new byte[] {0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,1});
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public InetAddress[] getMultiHomedServiceBindAddresses(boolean nio)
	{
		InetAddress[] bindIPs = currentBindIPs;
		for(int i=0;i<bindIPs.length;i++)
		{
			if(bindIPs[i].isAnyLocalAddress())
				return new InetAddress[] {nio && !supportsIPv6withNIO && bindIPs[i] instanceof Inet6Address ? anyLocalAddressIPv4 : bindIPs[i]};
		}
		return bindIPs;
	}
	
	public InetAddress getSingleHomedServiceBindAddress(int proto)
	{
		InetAddress[] addrs = currentBindIPs;
		if(proto == IP_PROTOCOL_VERSION_AUTO)
			return addrs[0];
		else
			for(int i = 0;i<addrs.length;i++)
			{
				if( (proto == IP_PROTOCOL_VERSION_REQUIRE_V4 && addrs[i] instanceof Inet4Address || addrs[i].isAnyLocalAddress()) ||
					(proto == IP_PROTOCOL_VERSION_REQUIRE_V6 && addrs[i] instanceof Inet6Address) )
				return addrs[i];
			}
		throw new UnsupportedAddressTypeException();
	}
	
	public InetAddress[]
	getAllBindAddresses()
	{
		return( currentBindIPs );
	}
	
	private InetAddress[] calcBindAddresses(final String addressString, boolean enforceBind)
	{
		ArrayList addrs = new ArrayList();
		
		Pattern addressSplitter = Pattern.compile(";");
		Pattern interfaceSplitter = Pattern.compile("[\\]\\[]");
		
		String[] tokens = addressSplitter.split(addressString);

			addressLoop: for(int i=0;i<tokens.length;i++)
			{
				String currentAddress = tokens[i];
				InetAddress parsedAddress = null;
				
				try
				{ // literal ipv4 or ipv6 address
					if(currentAddress.indexOf('.') != -1 || currentAddress.indexOf(':') != -1)
						parsedAddress = InetAddress.getByName(currentAddress);
				} catch (Exception e)
				{ // ignore, could be an interface name containing a ':'
				}
				
				if(parsedAddress != null)
				{
					try
					{
						// allow wildcard address as 1st address, otherwise only interface addresses
						if((!parsedAddress.isAnyLocalAddress() || addrs.size() > 0) && NetworkInterface.getByInetAddress(parsedAddress) == null)
							continue;
					} catch (SocketException e)
					{
						Debug.printStackTrace(e);
						continue;
					}
					addrs.add(parsedAddress);
					continue;
				}
					
				// interface name
				String[] ifaces = interfaceSplitter.split(currentAddress);

				NetworkInterface netInterface = null;
				try
				{
					netInterface = NetworkInterface.getByName(ifaces[0]);
				} catch (SocketException e)
				{
					e.printStackTrace(); // should not happen
				}
				if(netInterface == null)
					continue;

				Enumeration interfaceAddresses = netInterface.getInetAddresses();
				if(ifaces.length != 2)
					while(interfaceAddresses.hasMoreElements())
						addrs.add(interfaceAddresses.nextElement());
				else
				{
					int selectedAddress = 0;
					try { selectedAddress = Integer.parseInt(ifaces[1]); }
					catch (NumberFormatException e) {} // ignore, user could by typing atm
					for(int j=0;interfaceAddresses.hasMoreElements();j++,interfaceAddresses.nextElement())
						if(j==selectedAddress)
						{
							addrs.add(interfaceAddresses.nextElement());
							continue addressLoop;						
						}
				}
			}
		
		if(addrs.size() < 1)
			return new InetAddress[] {enforceBind ? localhostV4 : (hasIPV6Potential() ? anyLocalAddressIPv6 : anyLocalAddressIPv4)};
		return (InetAddress[])addrs.toArray(new InetAddress[0]);
	}
	
	
	protected void checkDefaultBindAddress(boolean first_time)
	{
		boolean changed = false;
		String bind_ip = COConfigurationManager.getStringParameter("Bind IP", "").trim();
		boolean enforceBind = COConfigurationManager.getBooleanParameter("Enforce Bind IP");
		InetAddress[] addrs = calcBindAddresses(bind_ip, enforceBind);
		changed = !Arrays.equals(currentBindIPs, addrs);
		if(changed){
			currentBindIPs = addrs;
			if (!first_time)
			{
				String logmsg = "NetworkAdmin: default bind ip has changed to '";
				for(int i=0;i<addrs.length;i++)
					logmsg+=(addrs[i] == null ? "none" : addrs[i].getHostAddress()) + (i<addrs.length? ";" : "");
				logmsg+="'";
				Logger.log(new LogEvent(LOGID, logmsg));
			}
			firePropertyChange(NetworkAdmin.PR_DEFAULT_BIND_ADDRESS);
		}
	}
	
	public String getNetworkInterfacesAsString()
	{
		Set interfaces = old_network_interfaces;
		if (interfaces == null)
		{
			return ("");
		}
		Iterator it = interfaces.iterator();
		String str = "";
		while (it.hasNext())
		{
			NetworkInterface ni = (NetworkInterface) it.next();
			Enumeration addresses = ni.getInetAddresses();
			str+=ni.getName()+"\t("+ni.getDisplayName()+")\n";
			int i = 0;
			while(addresses.hasMoreElements())
				str+="\t"+ni.getName()+"["+(i++)+"]\t"+((InetAddress)addresses.nextElement()).getHostAddress()+"\n";
		}
		return (str);
	}
	
	public boolean
	hasIPV4Potential()
	{
		return supportsIPv4;
	}
	
	public boolean
	hasIPV6Potential(boolean nio)
	{
		return nio ? supportsIPv6withNIO : supportsIPv6;
	}
	
	
	public InetAddress 
	guessRoutableBindAddress() 
	{
		try{
				// see if we have a choice
			
			List	local_addresses 	= new ArrayList();
			List	non_local_addresses = new ArrayList();
			
			try{
				NetworkAdminNetworkInterface[] interfaces = getInterfaces();
				
				List possible = new ArrayList();
				
				for (int i=0;i<interfaces.length;i++){
					
					NetworkAdminNetworkInterface intf = interfaces[i];
					
					NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
					
					for (int j=0;j<addresses.length;j++){
						
						NetworkAdminNetworkInterfaceAddress address = addresses[j];
						
						InetAddress ia = address.getAddress();
						
						if ( ia.isLoopbackAddress()){
							
							continue;
						}
						
						if ( ia.isLinkLocalAddress() ||	ia.isSiteLocalAddress()){
							
							local_addresses.add( ia );
							
						}else{
							
							non_local_addresses.add( ia );
						}
						
						if ( 	( hasIPV4Potential() && ia instanceof Inet4Address ) ||
								( hasIPV6Potential() && ia instanceof Inet6Address )){
							
							possible.add( ia );
						}
					}
				}
				
				if ( possible.size() == 1 ){
					
					return((InetAddress)possible.get(0));
				}
			}catch( Throwable e ){				
			}
			
				// if we have a socks server then let's use a compatible address for it
			
			try{
				NetworkAdminSocksProxy[] socks = getSocksProxies();
				
				if ( socks.length > 0 ){
					
					return( mapAddressToBindIP( InetAddress.getByName( socks[0].getHost())));
				}
			}catch( Throwable e ){				
			}
			
				// next, same for nat devices
			
			try{
				NetworkAdminNATDevice[] nat = getNATDevices();
				
				if ( nat.length > 0 ){
					
					return( mapAddressToBindIP( nat[0].getAddress()));
				}
			}catch( Throwable e ){				
			}
			
			try{
				final AESemaphore 	sem 		= new AESemaphore( "NA:conTest" );
				final InetAddress[]	can_connect = { null };
				
				final int	timeout = 10*1000;
				
				for (int i=0;i<local_addresses.size();i++){
					
					final InetAddress address = (InetAddress)local_addresses.get(i);
					
					new AEThread2( "NA:conTest", true )
					{
						public void
						run()
						{
							if ( canConnectWithBind( address, timeout )){
								
								can_connect[0] = address;
								
								sem.release();
							}
						}
					}.start();
				}
				
				if ( sem.reserve( timeout )){
					
					return( can_connect[0] );
				}
			}catch( Throwable e ){
				
			}
			
				// take a chance on any non local addresses we have
			
			if ( non_local_addresses.size() > 0 ){
				
				return( guessAddress( non_local_addresses ));
			}
			
				// lastly, select local one at random
			
			if ( local_addresses.size() > 0 ){
				
				return( guessAddress( local_addresses ));
			}
			
				// ho hum
			
			return( null );
			
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
			
			return( null );
		}
	}
	
	protected boolean
	canConnectWithBind(
		InetAddress	bind_address,
		int			timeout )
	{
		Socket	socket = null;
	
		try{
			socket = new Socket();

			socket.bind( new InetSocketAddress( bind_address, 0 ));

			socket.setSoTimeout( timeout );

			socket.connect( new InetSocketAddress( "www.google.com", 80 ), timeout );
			
			return( true );
			
		}catch( Throwable e ){
			
			return( false );
			
		}finally{
			
			if ( socket != null ){
				
				try{
					socket.close();
					
				}catch( Throwable f ){
				}
			}
		}
	}
	
	protected InetAddress
	mapAddressToBindIP(
		InetAddress	address )
	{
		boolean[]	address_bits = bytesToBits( address.getAddress());

		NetworkAdminNetworkInterface[] interfaces = getInterfaces();
		
		InetAddress	best_bind_address	= null;
		int			best_prefix			= 0;
		
		for (int i=0;i<interfaces.length;i++){
			
			NetworkAdminNetworkInterface intf = interfaces[i];
			
			NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
			
			for (int j=0;j<addresses.length;j++){
				
				NetworkAdminNetworkInterfaceAddress bind_address = addresses[j];
				
				InetAddress ba = bind_address.getAddress();

				byte[]	bind_bytes = ba.getAddress();
		
				if ( address_bits.length == bind_bytes.length ){
			
					boolean[]	bind_bits = bytesToBits( bind_bytes );

					for (int k=0;k<bind_bits.length;k++){
				
						if ( address_bits[k] != bind_bits[k] ){
					
							break;
						}
				
						if ( k > best_prefix ){
					
							best_prefix	= k;
					
							best_bind_address	= ba;
						}
					}
				}
			}
		}
		
		return( best_bind_address );
	}
		
	protected boolean[]
  	bytesToBits(
  		byte[]	bytes )
  	{
  		boolean[]	res = new boolean[bytes.length*8];
  		
  		for (int i=0;i<bytes.length;i++){
  			
  			byte	b = bytes[i];
  			
  			for (int j=0;j<8;j++){
  				
  				res[i*8+j] = (b&(byte)(0x01<<(7-j))) != 0;
  			}
  		}
  				
  		return( res );
  	}
	
	protected InetAddress
	guessAddress(
		List	addresses )
	{
			// prioritise 192.168.0.* and 192.168.1.* as common
			// then ipv4 over ipv6
		
		for (int i=0;i<addresses.size();i++){
			
			InetAddress address = (InetAddress)addresses.get(i);
			
			String str = address.getHostAddress();
			
			if ( str.startsWith( "192.168.0." ) || str.startsWith( "192.168.1." )){
				
				return( address );
			}
		}
		
		for (int i=0;i<addresses.size();i++){
			
			InetAddress address = (InetAddress)addresses.get(i);
			
			if ( address instanceof Inet4Address ){
				
				return( address );
			}
		}
		
		for (int i=0;i<addresses.size();i++){
			
			InetAddress address = (InetAddress)addresses.get(i);
			
			if ( address instanceof Inet6Address ){
				
				return( address );
			}
		}
		
		if ( addresses.size() > 0 ){
			
			return((InetAddress)addresses.get(0));
		}
		
		return( null );
	}
	
	protected void
	firePropertyChange(
		String	property )
	{
		Iterator it = listeners.iterator();
		
		while( it.hasNext()){
			
			try{
				((NetworkAdminPropertyChangeListener)it.next()).propertyChanged( property );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	public NetworkAdminNetworkInterface[]
	getInterfaces()
	{
		Set	interfaces = old_network_interfaces;
		
		if ( interfaces == null ){
			
			return( new NetworkAdminNetworkInterface[0] );
		}
		
		NetworkAdminNetworkInterface[]	res = new NetworkAdminNetworkInterface[interfaces.size()];
		
		Iterator	it = interfaces.iterator();
				
		int	pos = 0;
		
		while( it.hasNext()){
			
			NetworkInterface ni = (NetworkInterface)it.next();

			res[pos++] = new networkInterface( ni );
		}
		
		return( res );
	}

	public NetworkAdminProtocol[]
 	getOutboundProtocols()
	{
		AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
				
		NetworkAdminProtocol[]	res = 
			{
				new NetworkAdminProtocolImpl( azureus_core, NetworkAdminProtocol.PT_HTTP ),
				new NetworkAdminProtocolImpl( azureus_core, NetworkAdminProtocol.PT_TCP ),
				new NetworkAdminProtocolImpl( azureus_core, NetworkAdminProtocol.PT_UDP ),
			};
		      
		return( res );
	}
 	
 	public NetworkAdminProtocol[]
 	getInboundProtocols()
 	{
		AzureusCore azureus_core = AzureusCoreFactory.getSingleton();

		List	protocols = new ArrayList();
		
		TCPNetworkManager	tcp_manager = TCPNetworkManager.getSingleton();
		
		if ( tcp_manager.isTCPListenerEnabled()){
			
			protocols.add( 
					new NetworkAdminProtocolImpl( 
							azureus_core, 
							NetworkAdminProtocol.PT_TCP, 
							tcp_manager.getTCPListeningPortNumber()));
		}

		UDPNetworkManager	udp_manager = UDPNetworkManager.getSingleton();
		
		int	done_udp = -1;
		
		if ( udp_manager.isUDPListenerEnabled()){
			
			protocols.add( 
					new NetworkAdminProtocolImpl( 
							azureus_core, 
							NetworkAdminProtocol.PT_UDP, 
							done_udp = udp_manager.getUDPListeningPortNumber()));
		}
		
		if ( udp_manager.isUDPNonDataListenerEnabled()){

			int	port = udp_manager.getUDPNonDataListeningPortNumber();
			
			if ( port != done_udp ){
				
				protocols.add( 
						new NetworkAdminProtocolImpl( 
								azureus_core, 
								NetworkAdminProtocol.PT_UDP, 
								done_udp = udp_manager.getUDPNonDataListeningPortNumber()));
	
			}
		}
		
		HTTPNetworkManager	http_manager = HTTPNetworkManager.getSingleton();
		
		if ( http_manager.isHTTPListenerEnabled()){
			
			protocols.add( 
					new NetworkAdminProtocolImpl( 
							azureus_core, 
							NetworkAdminProtocol.PT_HTTP, 
							http_manager.getHTTPListeningPortNumber()));
		}
	      
		return((NetworkAdminProtocol[])protocols.toArray( new NetworkAdminProtocol[protocols.size()]));
 	}
 	
	public InetAddress
	testProtocol(
		NetworkAdminProtocol	protocol )
	
		throws NetworkAdminException
	{
		return( protocol.test( null ));
	}
	   
	public NetworkAdminSocksProxy[]
	getSocksProxies()
	{
		String host = System.getProperty( "socksProxyHost", "" ).trim();
		String port = System.getProperty( "socksProxyPort", "" ).trim();
        
		String user 		= System.getProperty("java.net.socks.username", "" ).trim();
		String password 	= System.getProperty("java.net.socks.password", "").trim();

		List	res = new ArrayList();
		
		NetworkAdminSocksProxyImpl	p1 = new NetworkAdminSocksProxyImpl( host, port, user, password );
		
		if ( p1.isConfigured()){
		
			res.add( p1 );
		}
		
		if ( 	COConfigurationManager.getBooleanParameter( "Proxy.Data.Enable" ) &&
				!COConfigurationManager.getBooleanParameter( "Proxy.Data.Same" )){
			
			host 	= COConfigurationManager.getStringParameter( "Proxy.Data.Host" );
			port	= COConfigurationManager.getStringParameter( "Proxy.Data.Port" );
			user	= COConfigurationManager.getStringParameter( "Proxy.Data.Username" );
			
			if ( user.trim().equalsIgnoreCase("<none>")){
				user = "";
			}
			password = COConfigurationManager.getStringParameter( "Proxy.Data.Password" );
			
			NetworkAdminSocksProxyImpl	p2 = new NetworkAdminSocksProxyImpl( host, port, user, password );
			
			if ( p2.isConfigured()){
			
				res.add( p2 );
			}			
		}

		return((NetworkAdminSocksProxy[])res.toArray(new NetworkAdminSocksProxy[res.size()]));
	}
	
	public NetworkAdminHTTPProxy
	getHTTPProxy()
	{
		NetworkAdminHTTPProxyImpl	res = new NetworkAdminHTTPProxyImpl();
		
		if ( !res.isConfigured()){
		
			res	= null;
		}
		
		return( res );
	}
	
	public NetworkAdminNATDevice[]
	getNATDevices()
	{
		List	devices = new ArrayList();
		
		try{
	
		    PluginInterface upnp_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass( UPnPPlugin.class );
		    
		    if ( upnp_pi != null ){
	    	
		    	UPnPPlugin upnp = (UPnPPlugin)upnp_pi.getPlugin();
		    	
		    	UPnPPluginService[]	services = upnp.getServices();
		    	
		    	for (int i=0;i<services.length;i++){
		    		
		    		devices.add( new NetworkAdminNATDeviceImpl( services[i] ));
		    	}
		    }
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
		}
		
		return((NetworkAdminNATDevice[])devices.toArray(new NetworkAdminNATDevice[devices.size()]));
	}
	
	public NetworkAdminASN 
	getCurrentASN() 
	{
		List	asns = COConfigurationManager.getListParameter( "ASN Details", new ArrayList());
		
		if ( asns.size() == 0 ){
	
				// migration from when we only persisted a single AS
			
			String as 	= "";
			String asn 	= "";
			String bgp 	= "";

			try{
				as 		= COConfigurationManager.getStringParameter( "ASN AS" );
				asn 	= COConfigurationManager.getStringParameter( "ASN ASN" );
				bgp 	= COConfigurationManager.getStringParameter( "ASN BGP" );
					
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
			
			COConfigurationManager.removeParameter( "ASN AS" );
			COConfigurationManager.removeParameter( "ASN ASN" );
			COConfigurationManager.removeParameter( "ASN BGP" );
			COConfigurationManager.removeParameter( "ASN Autocheck Performed Time" );
			
			asns.add(ASNToMap(new NetworkAdminASNImpl(as, asn, bgp )));
			
			COConfigurationManager.setParameter( "ASN Details", asns );
		}
		
		if ( asns.size() > 0 ){
			
			Map	m = (Map)asns.get(0);
			
			return( ASNFromMap( m ));
		}
		
		return( new NetworkAdminASNImpl( "", "", "" ));
	}
	
	protected Map
	ASNToMap(
		NetworkAdminASNImpl	x )
	{
		Map	m = new HashMap();
		
		byte[]	as	= new byte[0];
		byte[]	asn	= new byte[0];
		byte[]	bgp	= new byte[0];
		
		try{	
			as	= x.getAS().getBytes("UTF-8");
			asn	= x.getASName().getBytes("UTF-8");
			bgp	= x.getBGPPrefix().getBytes("UTF-8");
	
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
		}
		
		m.put( "as", as );
		m.put( "name", asn );
		m.put( "bgp", bgp );
		
		return( m );
	}
	
	protected NetworkAdminASNImpl
	ASNFromMap(
		Map	m )
	{
		String	as		= "";
		String	asn		= "";
		String	bgp		= "";
		
		try{
			as	= new String((byte[])m.get("as"),"UTF-8");
			asn	= new String((byte[])m.get("name"),"UTF-8");
			bgp	= new String((byte[])m.get("bgp"),"UTF-8");
			
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
		}
		
		return( new NetworkAdminASNImpl( as, asn, bgp ));
	}
	
	public NetworkAdminASN
	lookupCurrentASN(
		InetAddress		address )
	
		throws NetworkAdminException
	{
		NetworkAdminASN current = getCurrentASN();
		
		if ( current.matchesCIDR( address )){
			
			return( current );
		}
		
		List	asns = COConfigurationManager.getListParameter( "ASN Details", new ArrayList());

		for (int i=0;i<asns.size();i++){
			
			Map	m = (Map)asns.get(i);
			
			NetworkAdminASN x = ASNFromMap( m );
			
			if ( x.matchesCIDR( address )){
				
				asns.remove(i);
				
				asns.add( 0, m );
				
				firePropertyChange( PR_AS );
				
				return( x );
			}
		}
		
		if ( asn_ips_checked.contains( address )){
			
			return( current );
		}
				
		long now = SystemTime.getCurrentTime();

		if ( now < last_asn_lookup_time || now - last_asn_lookup_time > ASN_MIN_CHECK ){

			last_asn_lookup_time	= now;

			NetworkAdminASNLookupImpl lookup = new NetworkAdminASNLookupImpl( address );

			NetworkAdminASNImpl x = lookup.lookup();
			
			asn_ips_checked.add( address );
			
			asns.add( 0, ASNToMap( x ));
			
			firePropertyChange( PR_AS );

			return( x );
		}
		
		return( current );
	}
		
	public NetworkAdminASN
	lookupASN(
		InetAddress		address )
	
		throws NetworkAdminException
	{
		NetworkAdminASN existing = getFromASHistory( address );
		
		if ( existing != null ){
			
			return( existing );
		}

		NetworkAdminASNLookupImpl lookup = new NetworkAdminASNLookupImpl( address );

		NetworkAdminASNImpl result = lookup.lookup();
			
		addToASHistory( result );
		
		return( result );
	}
		
	protected void
	addToASHistory(
		NetworkAdminASN	asn )
	{
		synchronized( as_history ){
			
			boolean	found = false;
			
			for (int i=0;i<as_history.size();i++){
				
				 NetworkAdminASN x = (NetworkAdminASN)as_history.get(i);
				 
				 if ( asn.getAS() == x.getAS()){
					 
					 found = true;
					 
					 break;
				 }
			}
			
			if ( !found ){
				
				as_history.add( asn );
				
				if ( as_history.size() > 256 ){
					
					as_history.remove(0);
				}
			}
		}
	}
	
	protected NetworkAdminASN
	getFromASHistory(
		InetAddress	address )
	{
		synchronized( as_history ){
			
			for (int i=0;i<as_history.size();i++){
				
				 NetworkAdminASN x = (NetworkAdminASN)as_history.get(i);
				 
				 if ( x.matchesCIDR( address )){
					 
					 return( x );
				 }
			}
		}
		
		return( null );
	}
	
	public void
	runInitialChecks()
	{
		AZInstanceManager i_man = AzureusCoreFactory.getSingleton().getInstanceManager();
		
		final AZInstance	my_instance = i_man.getMyInstance();
		
		i_man.addListener(
			new AZInstanceManagerListener()
			{
				private InetAddress external_address;
				
				public void
				instanceFound(
					AZInstance		instance )
				{
				}
				
				public void
				instanceChanged(
					AZInstance		instance )
				{
					if ( instance == my_instance ){
						
						InetAddress address = instance.getExternalAddress();
						
						if ( external_address == null || !external_address.equals( address )){
							
							external_address = address;
							
							try{
								lookupCurrentASN( address );
								
							}catch( Throwable e ){
								
								Debug.printStackTrace(e);
							}
						}
					}
				}
				
				public void
				instanceLost(
					AZInstance		instance )
				{
				}
				
				public void
				instanceTracked(
					AZInstanceTracked	instance )
				{
				}
			});
		
		if ( COConfigurationManager.getBooleanParameter( "Proxy.Check.On.Start" )){
			
			NetworkAdminSocksProxy[]	socks = getSocksProxies();
		
			for (int i=0;i<socks.length;i++){
				
				NetworkAdminSocksProxy	sock = socks[i];
				
				try{
					sock.getVersionsSupported();
			
				}catch( Throwable e ){
				
					Debug.printStackTrace( e );
					
					Logger.log(
						new LogAlert(
							true,
							LogAlert.AT_WARNING,
							"Socks proxy " + sock.getName() + " check failed: " + Debug.getNestedExceptionMessage( e )));
				}
			}
			
			NetworkAdminHTTPProxy http_proxy = getHTTPProxy();
			
			if ( http_proxy != null ){
			
				try{

					http_proxy.getDetails();
					
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
					
					Logger.log(
						new LogAlert(
							true,
							LogAlert.AT_WARNING,
							"HTTP proxy " + http_proxy.getName() + " check failed: " + Debug.getNestedExceptionMessage( e )));
				}
			}
		}

        NetworkAdminSpeedTestScheduler nast = NetworkAdminSpeedTestSchedulerImpl.getInstance();
        
        nast.initialise();
    }
	
	public boolean
	canTraceRoute()
	{
		PlatformManager	pm = PlatformManagerFactory.getPlatformManager();

		return( pm.hasCapability( PlatformManagerCapabilities.TraceRouteAvailability ));
	}
	
	public NetworkAdminNode[]
	getRoute(
		InetAddress						interface_address,
		InetAddress						target,
		final int						max_millis,
		final NetworkAdminRouteListener	listener )
	
		throws NetworkAdminException
	{
		PlatformManager	pm = PlatformManagerFactory.getPlatformManager();
			
		if ( !canTraceRoute()){
			
			throw( new NetworkAdminException( "No trace-route capability on platform" ));
		}
		
		final List	nodes = new ArrayList();
		
		try{
			pm.traceRoute( 
				interface_address,
				target,
				new PlatformManagerPingCallback()
				{
					private long	start_time = SystemTime.getCurrentTime();
					
					public boolean
					reportNode(
						int				distance,
						InetAddress		address,
						int				millis )
					{
						boolean	timeout	= false;
						
						if ( max_millis >= 0 ){
										
							long	now = SystemTime.getCurrentTime();
							
							if ( now < start_time ){
								
								start_time = now;
							}
							
							if ( now - start_time >= max_millis ){
								
								timeout = true;
							}
						}
						
						NetworkAdminNode	node = null;
						
						if ( address != null ){
							
							node = new networkNode( address, distance, millis );
							
							nodes.add( node );
						}
						
						boolean	result;
						
						if ( listener == null ){
							
							result = true;
							
						}else{

							if ( node == null ){
								
								result = listener.timeout( distance );
								
							}else{
								
								result =  listener.foundNode( node, distance, millis );
							}
						}
						
						return( result && !timeout );
					}
				});
		}catch( PlatformManagerException e ){
			
			throw( new NetworkAdminException( "trace-route failed", e ));
		}
		
		return((NetworkAdminNode[])nodes.toArray( new NetworkAdminNode[nodes.size()]));
	}
	
	public boolean
	canPing()
	{
		PlatformManager	pm = PlatformManagerFactory.getPlatformManager();

		return( pm.hasCapability( PlatformManagerCapabilities.PingAvailability ));
	}
	
	public NetworkAdminNode
	pingTarget(
		InetAddress						interface_address,
		InetAddress						target,
		final int						max_millis,
		final NetworkAdminRouteListener	listener )
	
		throws NetworkAdminException
	{
		PlatformManager	pm = PlatformManagerFactory.getPlatformManager();
			
		if ( !canPing()){
			
			throw( new NetworkAdminException( "No ping capability on platform" ));
		}
		
		final NetworkAdminNode[] nodes = { null };
		
		try{
			pm.ping(
				interface_address,
				target,
				new PlatformManagerPingCallback()
				{
					private long	start_time = SystemTime.getCurrentTime();
					
					public boolean
					reportNode(
						int				distance,
						InetAddress		address,
						int				millis )
					{
						boolean	timeout	= false;
						
						if ( max_millis >= 0 ){
										
							long	now = SystemTime.getCurrentTime();
							
							if ( now < start_time ){
								
								start_time = now;
							}
							
							if ( now - start_time >= max_millis ){
								
								timeout = true;
							}
						}
						
						NetworkAdminNode	node = null;
						
						if ( address != null ){
							
							node = new networkNode( address, distance, millis );
							
							nodes[0] = node;
						}
						
						boolean	result;
						
						if ( listener == null ){
							
							result = false;
							
						}else{

							if ( node == null ){
								
								result = listener.timeout( distance );
								
							}else{
								
								result =  listener.foundNode( node, distance, millis );
							}
						}
						
						return( result && !timeout );
					}
				});
		}catch( PlatformManagerException e ){
			
			throw( new NetworkAdminException( "ping failed", e ));
		}
		
		return( nodes[0] );
	}
	
	
	public void
	getRoutes(
		final InetAddress					target,
		final int							max_millis,
		final NetworkAdminRoutesListener	listener )
	
		throws NetworkAdminException
	{
		final List sems 	= new ArrayList();
		final List traces 	= new ArrayList();
		
		NetworkAdminNetworkInterface[] interfaces = getInterfaces();

		for (int i=0;i<interfaces.length;i++){
			
			NetworkAdminNetworkInterface	interf = (NetworkAdminNetworkInterface)interfaces[i];

			NetworkAdminNetworkInterfaceAddress[] addresses = interf.getAddresses();
			
			for (int j=0;j<addresses.length;j++){
				
				final NetworkAdminNetworkInterfaceAddress	address = addresses[j];
				
				InetAddress ia = address.getAddress();
				
				if ( ia.isLoopbackAddress() || ia instanceof Inet6Address ){
					
						// ignore
					
				}else{
					
					final AESemaphore sem = new AESemaphore( "parallelRouter" );
					
					final List		trace = new ArrayList();
					
					sems.add( sem );
					
					traces.add( trace );
					
					new AEThread2( "parallelRouter", true )
					{
						public void
						run()
						{
							try{
								address.getRoute( 
									target, 
									30000,
									new NetworkAdminRouteListener()
									{
										public boolean 
										foundNode(
											NetworkAdminNode 	node, 
											int 				distance, 
											int 				rtt ) 
										{
											trace.add( node );
											
											NetworkAdminNode[]	route = new NetworkAdminNode[trace.size()];
											
											trace.toArray( route );
											
											return( listener.foundNode( address, route, distance, rtt) );
										}
										
										public boolean 
										timeout(
											int distance )
										{
											NetworkAdminNode[]	route = new NetworkAdminNode[trace.size()];
											
											trace.toArray( route );

											return( listener.timeout( address, route, distance ));
										}
									});
								
							}catch( Throwable e ){
							
								e.printStackTrace();
								
							}finally{
								
								sem.release();
							}
						}
					}.start();
				}
			}
		}
		
		for (int i=0;i<sems.size();i++){
			
			((AESemaphore)sems.get(i)).reserve();
		}
	}
	
	public void
	pingTargets(
		final InetAddress					target,
		final int							max_millis,
		final NetworkAdminRoutesListener	listener )
	
		throws NetworkAdminException
	{
		final List sems 	= new ArrayList();
		final List traces 	= new ArrayList();
		
		NetworkAdminNetworkInterface[] interfaces = getInterfaces();

		for (int i=0;i<interfaces.length;i++){
			
			NetworkAdminNetworkInterface	interf = (NetworkAdminNetworkInterface)interfaces[i];

			NetworkAdminNetworkInterfaceAddress[] addresses = interf.getAddresses();
			
			for (int j=0;j<addresses.length;j++){
				
				final NetworkAdminNetworkInterfaceAddress	address = addresses[j];
				
				InetAddress ia = address.getAddress();
				
				if ( ia.isLoopbackAddress() || ia instanceof Inet6Address ){
					
						// ignore
					
				}else{
					
					final AESemaphore sem = new AESemaphore( "parallelPinger" );
					
					final List		trace = new ArrayList();
					
					sems.add( sem );
					
					traces.add( trace );
					
					new AEThread2( "parallelPinger", true )
					{
						public void
						run()
						{
							try{
								address.pingTarget( 
									target, 
									30000,
									new NetworkAdminRouteListener()
									{
										public boolean 
										foundNode(
											NetworkAdminNode 	node, 
											int 				distance, 
											int 				rtt ) 
										{
											trace.add( node );
											
											NetworkAdminNode[]	route = new NetworkAdminNode[trace.size()];
											
											trace.toArray( route );
											
											return( listener.foundNode( address, route, distance, rtt) );
										}
										
										public boolean 
										timeout(
											int distance )
										{
											NetworkAdminNode[]	route = new NetworkAdminNode[trace.size()];
											
											trace.toArray( route );

											return( listener.timeout( address, route, distance ));
										}
									});
								
							}catch( Throwable e ){
							
								e.printStackTrace();
								
							}finally{
								
								sem.release();
							}
						}
					}.start();
				}
			}
		}
		
		for (int i=0;i<sems.size();i++){
			
			((AESemaphore)sems.get(i)).reserve();
		}
	}
	
	public void
	addPropertyChangeListener(
		NetworkAdminPropertyChangeListener	listener )
	{
		listeners.add( listener );
	}
	
	public void
	addAndFirePropertyChangeListener(
		NetworkAdminPropertyChangeListener	listener )
	{
		listeners.add( listener );
		
		for (int i=0;i<NetworkAdmin.PR_NAMES.length;i++){
			
			try{
				listener.propertyChanged( PR_NAMES[i] );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	public void
	removePropertyChangeListener(
		NetworkAdminPropertyChangeListener	listener )
	{
		listeners.remove( listener );
	}
	
	public void
	generate(
		IndentWriter		writer )
	{
		writer.println( "Network Admin" );
		
		try{
			writer.indent();
			
			NetworkAdminHTTPProxy	proxy = getHTTPProxy();
			
			if ( proxy == null ){
				
				writer.println( "HTTP proxy: none" );
				
			}else{
				
				writer.println( "HTTP proxy: " + proxy.getName());
				
				try{
					
					NetworkAdminHTTPProxy.Details details = proxy.getDetails();
					
					writer.println( "    name: " + details.getServerName());
					writer.println( "    resp: " + details.getResponse());
					writer.println( "    auth: " + details.getAuthenticationType());
					
				}catch( NetworkAdminException e ){
					
					writer.println( "    failed: " + e.getLocalizedMessage());
				}
			}
			
			NetworkAdminSocksProxy[]	socks = getSocksProxies();
			
			if ( socks.length == 0 ){
				
				writer.println( "Socks proxy: none" );
				
			}else{
				
				for (int i=0;i<socks.length;i++){
					
					NetworkAdminSocksProxy	sock = socks[i];
					
					writer.println( "Socks proxy: " + sock.getName());
					
					try{
						String[] versions = sock.getVersionsSupported();
						
						String	str = "";
						
						for (int j=0;j<versions.length;j++){
							
							str += (j==0?"":",") + versions[j];
						}
						
						writer.println( "   version: " + str );
						
					}catch( NetworkAdminException e ){
						
						writer.println( "    failed: " + e.getLocalizedMessage());
					}
				}
			}
			
			NetworkAdminNATDevice[]	nat_devices = getNATDevices();
			
			writer.println( "NAT Devices: " + nat_devices.length );
			
			for (int i=0;i<nat_devices.length;i++){
				
				NetworkAdminNATDevice	device = nat_devices[i];
				
				writer.println( "    " + device.getName() + ",address=" + device.getAddress().getHostAddress() + ":" + device.getPort() + ",ext=" + device.getExternalAddress());
			}
			
			writer.println( "Interfaces" );
			
			writer.println( "   " + getNetworkInterfacesAsString());
			
		}finally{
	
			writer.exdent();
		}
	}
	
	public void 
	generateDiagnostics(
		final IndentWriter iw )
	{
		Set	public_addresses = new HashSet();
		
		NetworkAdminHTTPProxy	proxy = getHTTPProxy();
		
		if ( proxy == null ){
			
			iw.println( "HTTP proxy: none" );
			
		}else{
			
			iw.println( "HTTP proxy: " + proxy.getName());
			
			try{
				
				NetworkAdminHTTPProxy.Details details = proxy.getDetails();
				
				iw.println( "    name: " + details.getServerName());
				iw.println( "    resp: " + details.getResponse());
				iw.println( "    auth: " + details.getAuthenticationType());
				
			}catch( NetworkAdminException e ){
				
				iw.println( "    failed: " + e.getLocalizedMessage());
			}
		}
		
		NetworkAdminSocksProxy[]	socks = getSocksProxies();
		
		if ( socks.length == 0 ){
			
			iw.println( "Socks proxy: none" );
			
		}else{
			
			for (int i=0;i<socks.length;i++){
				
				NetworkAdminSocksProxy	sock = socks[i];
				
				iw.println( "Socks proxy: " + sock.getName());
				
				try{
					String[] versions = sock.getVersionsSupported();
					
					String	str = "";
					
					for (int j=0;j<versions.length;j++){
						
						str += (j==0?"":",") + versions[j];
					}
					
					iw.println( "   version: " + str );
					
				}catch( NetworkAdminException e ){
					
					iw.println( "    failed: " + e.getLocalizedMessage());
				}
			}
		}
		
		NetworkAdminNATDevice[]	nat_devices = getNATDevices();
		
		iw.println( "NAT Devices: " + nat_devices.length );
		
		for (int i=0;i<nat_devices.length;i++){
			
			NetworkAdminNATDevice	device = nat_devices[i];
			
			iw.println( "    " + device.getName() + ",address=" + device.getAddress().getHostAddress() + ":" + device.getPort() + ",ext=" + device.getExternalAddress());
			
			public_addresses.add( device.getExternalAddress());
		}
		
		iw.println( "Interfaces" );
		
		NetworkAdminNetworkInterface[] interfaces = getInterfaces();
		
		if ( FULL_INTF_PROBE ){
			
			if ( interfaces.length > 0 ){
				
				if ( interfaces.length > 1 || interfaces[0].getAddresses().length > 1 ){
					
					for (int i=0;i<interfaces.length;i++){
						
						networkInterface	interf = (networkInterface)interfaces[i];
						
						iw.indent();
						
						try{
							
							interf.generateDiagnostics( iw, public_addresses );
							
						}finally{
							
							iw.exdent();
						}
					}
				}else{
					
					if ( interfaces[0].getAddresses().length > 0 ){
						
						networkInterface.networkAddress address = (networkInterface.networkAddress)interfaces[0].getAddresses()[0];
						
						try{
							NetworkAdminNode[] nodes = address.getRoute( InetAddress.getByName("www.google.com"), 30000, trace_route_listener  );
							
							for (int i=0;i<nodes.length;i++){
								
								networkNode	node = (networkNode)nodes[i];
																
								iw.println( node.getString());
							}
						}catch( Throwable e ){
							
							iw.println( "Can't resolve host for route trace - " + e.getMessage());
						}
					}
				}
			}
		}else{
			
			try{
				pingTargets( 
					InetAddress.getByName( "www.google.com" ), 
					30000,
					new NetworkAdminRoutesListener()
					{
						private int	timeouts = 0;
						
						public boolean
						foundNode(
							NetworkAdminNetworkInterfaceAddress		intf,
							NetworkAdminNode[]						route,
							int										distance,
							int										rtt )
						{
							iw.println( intf.getAddress().getHostAddress() + ": " + route[route.length-1].getAddress().getHostAddress() + " (" + distance + ")" );

							return( false );
						}
						
						public boolean
						timeout(
							NetworkAdminNetworkInterfaceAddress		intf,
							NetworkAdminNode[]						route,
							int										distance )
						{
							iw.println( intf.getAddress().getHostAddress() + ": timeout (dist=" + distance + ")" );
							
							timeouts++;
							
							return( timeouts < 3 );
						}
					});
				
			}catch( Throwable e ){
				
				iw.println( "getRoutes failed: " + Debug.getNestedExceptionMessage( e ));
			}
		}
		
		iw.println( "Inbound protocols: default routing" );
		
		NetworkAdminProtocol[]	protocols = getInboundProtocols();
		
		for (int i=0;i<protocols.length;i++){
			
			NetworkAdminProtocol	protocol = protocols[i];
			
			try{
				InetAddress	ext_addr = testProtocol( protocol );
	
				if ( ext_addr != null ){
					
					public_addresses.add( ext_addr );
				}
	
				iw.println( "    " + protocol.getName() + " - " + ext_addr );
				
			}catch( NetworkAdminException e ){
				
				iw.println( "    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
			}
		}
		
		iw.println( "Outbound protocols: default routing" );
		
		protocols = getOutboundProtocols();
		
		for (int i=0;i<protocols.length;i++){
			
			NetworkAdminProtocol	protocol = protocols[i];
			
			try{

				InetAddress	ext_addr = testProtocol( protocol );
				
				if ( ext_addr != null ){
				
					public_addresses.add( ext_addr );
				}
				
				iw.println( "    " + protocol.getName() + " - " + ext_addr );
				
			}catch( NetworkAdminException e ){
				
				iw.println( "    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
			}
		}
		
		Iterator	it = public_addresses.iterator();
		
		iw.println( "Public Addresses" );
		
		while( it.hasNext()){
			
			InetAddress	pub_address = (InetAddress)it.next();
			
			try{
				NetworkAdminASN	res = lookupCurrentASN( pub_address );
				
				iw.println( "    " + pub_address.getHostAddress() + " -> " + res.getAS() + "/" + res.getASName());
				
			}catch( Throwable e ){
				
				iw.println( "    " + pub_address.getHostAddress() + " -> " + Debug.getNestedExceptionMessage(e));
			}
		}
	}
	
	protected class
	networkInterface
		implements NetworkAdminNetworkInterface
	{
		private NetworkInterface		ni;
		
		protected
		networkInterface(
			NetworkInterface	_ni )
		{
			ni	= _ni;
		}
		
		public String
		getDisplayName()
		{
			return( ni.getDisplayName());
		}
		
		public String
		getName()
		{
			return( ni.getName());
		}
		
		public NetworkAdminNetworkInterfaceAddress[]
		getAddresses()
		{
				// BAH NetworkInterface has lots of goodies but is 1.6
			
			Enumeration	e = ni.getInetAddresses();
		
			List	addresses = new ArrayList();
			
			while( e.hasMoreElements()){
				
				addresses.add( new networkAddress((InetAddress)e.nextElement()));
			}
	
			return((NetworkAdminNetworkInterfaceAddress[])addresses.toArray( new NetworkAdminNetworkInterfaceAddress[addresses.size()]));
		}
	
		public String
		getString()
		{
			String	str = getDisplayName() + "/" + getName() + " [";
			
			NetworkAdminNetworkInterfaceAddress[] addresses = getAddresses();
			
			for (int i=0;i<addresses.length;i++){
				
				networkAddress	addr = (networkAddress)addresses[i];
				
				str += (i==0?"":",") + addr.getAddress().getHostAddress();
			}
			
			return( str + "]" );
		}
		
		public void 
		generateDiagnostics(
			IndentWriter 	iw,
			Set				public_addresses )
		{
			iw.println( getDisplayName() + "/" + getName());
			
			NetworkAdminNetworkInterfaceAddress[] addresses = getAddresses();
			
			for (int i=0;i<addresses.length;i++){
				
				networkAddress	addr = (networkAddress)addresses[i];
				
				iw.indent();
				
				try{
					
					addr.generateDiagnostics( iw, public_addresses );
					
				}finally{
					
					iw.exdent();
				}
			}
		}
		

		protected class
		networkAddress
			implements NetworkAdminNetworkInterfaceAddress
		{
			private InetAddress		address;
			
			protected
			networkAddress(
					
				InetAddress	_address )
			{
				address = _address;
			}
			
			public NetworkAdminNetworkInterface
			getInterface()
			{
				return( networkInterface.this );
			}
			
			public InetAddress
			getAddress()
			{
				return( address );
			}
			
			public boolean
			isLoopback()
			{
				return( address.isLoopbackAddress());
			}
						
			public NetworkAdminNode[]
			getRoute(
				InetAddress						target,
				final int						max_millis,
				final NetworkAdminRouteListener	listener )
			
				throws NetworkAdminException
			{
				return( NetworkAdminImpl.this.getRoute( address, target, max_millis, listener));
			}
			
			public NetworkAdminNode
			pingTarget(
				InetAddress						target,
				final int						max_millis,
				final NetworkAdminRouteListener	listener )
			
				throws NetworkAdminException
			{
				return( NetworkAdminImpl.this.pingTarget( address, target, max_millis, listener));
			}
			
			public InetAddress
			testProtocol(
				NetworkAdminProtocol	protocol )
			
				throws NetworkAdminException
			{
				return( protocol.test( this ));
			}
			
			public void 
			generateDiagnostics(
				IndentWriter 	iw,
				Set				public_addresses )
			{
				iw.println( "" + getAddress());
				
				try{
					iw.println( "  Trace route" );
					
					iw.indent();
					
					if ( isLoopback()){
						
						iw.println( "Loopback - ignoring" );
						
					}else{
						
						try{
							NetworkAdminNode[] nodes = getRoute( InetAddress.getByName("www.google.com"), 30000, trace_route_listener );
							
							for (int i=0;i<nodes.length;i++){
								
								networkNode	node = (networkNode)nodes[i];
																
								iw.println( node.getString());
							}
						}catch( Throwable e ){
							
							iw.println( "Can't resolve host for route trace - " + e.getMessage());
						}
												
						iw.println( "Outbound protocols: bound" );
						
						NetworkAdminProtocol[]	protocols = getOutboundProtocols();
						
						for (int i=0;i<protocols.length;i++){
							
							NetworkAdminProtocol	protocol = protocols[i];
							
							try{
								InetAddress	res = testProtocol( protocol );
								
								if ( res != null ){
									
									public_addresses.add( res );
								}
								
								iw.println( "    " + protocol.getName() + " - " + res );
								
							}catch( NetworkAdminException e ){
								
								iw.println( "    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
							}
						}
						
						iw.println( "Inbound protocols: bound" );
						
						protocols = getInboundProtocols();
						
						for (int i=0;i<protocols.length;i++){
							
							NetworkAdminProtocol	protocol = protocols[i];
							
							try{
								InetAddress	res = testProtocol( protocol );
								
								if ( res != null ){
									
									public_addresses.add( res );
								}
								
								iw.println( "    " + protocol.getName() + " - " + res );
								
							}catch( NetworkAdminException e ){
								
								iw.println( "    " + protocol.getName() + " - " + Debug.getNestedExceptionMessage(e));
							}
						}
					}
				}finally{
					
					iw.exdent();
				}
			}
		}
	}
	
	protected class
	networkNode
		implements NetworkAdminNode
	{
		private InetAddress	address;
		private int			distance;
		private int			rtt;
		
		protected
		networkNode(
			InetAddress		_address,
			int				_distance,
			int				_millis )
		{
			address		= _address;
			distance	= _distance;
			rtt			= _millis;
		}
		
		public InetAddress
		getAddress()
		{
			return( address );
		}
		
		public boolean
		isLocalAddress()
		{
			return( address.isLinkLocalAddress() ||	address.isSiteLocalAddress()); 
		}

		public int
		getDistance()
		{
			return( distance );
		}
		
		public int
		getRTT()
		{
			return( rtt );
		}
		
		protected String
		getString()
		{
			if ( address == null ){
				
				return( "" + distance );
				
			}else{
			
				return( distance + "," + address + "[local=" + isLocalAddress() + "]," + rtt );
			}
		}
	}
	
	protected void
	generateDiagnostics(
		IndentWriter			iw,
		NetworkAdminProtocol[]	protocols )
	{
		for (int i=0;i<protocols.length;i++){
			
			NetworkAdminProtocol	protocol = protocols[i];
			
			iw.println( "Testing " + protocol.getName());
			
			try{
				InetAddress	ext_addr = testProtocol( protocol );
	
				iw.println( "    -> OK, public address=" + ext_addr );
				
			}catch( NetworkAdminException e ){
				
				iw.println( "    -> Failed: " + Debug.getNestedExceptionMessage(e));
			}
		}
	}
	
	public void
	logNATStatus(
		IndentWriter		iw )
	{
		generateDiagnostics( iw, getInboundProtocols());
	}
	
	public static void
	main(
		String[]	args )
	{
		boolean	TEST_SOCKS_PROXY 	= false;
		boolean	TEST_HTTP_PROXY		= false;
		
		try{
			if ( TEST_SOCKS_PROXY ){
				
				AESocksProxy proxy = AESocksProxyFactory.create( 4567, 10000, 10000 );
				
				proxy.setAllowExternalConnections( true );
				
				System.setProperty( "socksProxyHost", "localhost" );
				System.setProperty( "socksProxyPort", "4567" );
			}
			
			if ( TEST_HTTP_PROXY ){
			   
				System.setProperty("http.proxyHost", "localhost" );
			    System.setProperty("http.proxyPort", "3128" );
			    System.setProperty("https.proxyHost", "localhost" );
			    System.setProperty("https.proxyPort", "3128" );
			    			    
				Authenticator.setDefault(
						new Authenticator()
						{
							protected PasswordAuthentication
							getPasswordAuthentication()
							{
								return( new PasswordAuthentication( "fred", "bill".toCharArray()));
							}
						});

			}
			
			IndentWriter iw = new IndentWriter( new PrintWriter( System.out ));
			
			iw.setForce( true );
			
			COConfigurationManager.initialise();
			
			AzureusCoreFactory.create();
			
			NetworkAdmin admin = getSingleton();
			
			//admin.logNATStatus( iw );
			admin.generateDiagnostics( iw );
			
		}catch( Throwable e){
			
			e.printStackTrace();
		}
	}
}
