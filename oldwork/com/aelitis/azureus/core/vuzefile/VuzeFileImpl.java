/*
 * Created on May 18, 2008
 * Created by Paul Gardner
 * 
 * Copyright 2008 Vuze, Inc.  All rights reserved.
 * 
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */


package com.aelitis.azureus.core.vuzefile;

import java.io.*;
import java.util.*;

import org.gudy.azureus2.core3.util.BEncoder;

public class 
VuzeFileImpl
	implements VuzeFile
{
	private VuzeFileHandler			handler;
	private VuzeFileComponent[]		components;
	
	protected
	VuzeFileImpl(
		VuzeFileHandler		_handler )
	{
		handler = _handler;
		
		components = new VuzeFileComponent[0];
	}
	
	protected
	VuzeFileImpl(
		VuzeFileHandler		_handler,
		Map					map )
	{
		handler = _handler;
		
		List	l_comps = (List)map.get( "components" );
		
		components = new VuzeFileComponent[l_comps.size()];
		
		for (int i=0;i<l_comps.size();i++){
			
			Map	comp = (Map)l_comps.get(i);
			
			int	type 	= ((Long)comp.get( "type" )).intValue();
			Map	content	= (Map)comp.get( "content" );
			
			components[i] = new comp( type, content );
		}
	}
	
	public VuzeFileComponent[] 
	getComponents()
	{
		return( components ); 
	}
	
	public VuzeFileComponent
	addComponent(
		int		type,
		Map		content )
	{
		VuzeFileComponent comp = new comp( type, content );
		
		int	old_len = components.length;
		
		VuzeFileComponent[] res = new VuzeFileComponent[old_len+1];
		
		System.arraycopy( components, 0, res, 0, old_len );
		
		res[ old_len ] = comp;
		
		components = res;
		
		return( comp );
	}
	
	public byte[] 
	exportToBytes() 
	
		throws IOException 
	{
		Map	map = new HashMap();
		
		Map vuze_map = new HashMap();
		
		map.put( "vuze", vuze_map );
		
		List	list = new ArrayList();
		
		vuze_map.put( "components", list );
		
		for (int i=0;i<components.length;i++){
			
			VuzeFileComponent comp = components[i];
			
			Map	entry = new HashMap();
			
			entry.put( "type", new Long( comp.getType()));
			
			entry.put( "content", comp.getContent());
			
			list.add( entry );
		}
				
		return( BEncoder.encode( map ));
	}
	
	public void 
	write(
		File target )
	
		throws IOException 
	{
		FileOutputStream	fos = new FileOutputStream( target );
		
		try{
			fos.write( exportToBytes());
			
		}finally{
			
			fos.close();
		}
	}
	
	protected class
	comp
		implements VuzeFileComponent
	{
		private int			type;
		private Map			contents;
		private boolean		processed;
		
		private Map			user_data;
		
		protected
		comp(
			int		_type,
			Map		_contents )
		{
			type		= _type;
			contents	= _contents;
		}
		
		public int
		getType()
		{
			return( type );
		}
		
		public Map
		getContent()
		{
			return( contents );
		}
		
		public void
		setProcessed()
		{
			processed	= true;
		}
		
		public boolean
		isProcessed()
		{
			return( processed );
		}
		
		public synchronized void
		setData(
			Object	key,
			Object	value )
		{
			if ( user_data == null ){
				
				user_data = new HashMap();
			}
			
			user_data.put( key, value );
		}
		
		public synchronized Object
		getData(
			Object	key )
		{
			if ( user_data == null ){
				
				return( null );
			}
			
			return( user_data.get( key ));
		}
	}
}
