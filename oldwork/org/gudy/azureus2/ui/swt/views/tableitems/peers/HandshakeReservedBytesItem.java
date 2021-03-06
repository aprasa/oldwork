/*
 * Created on 29 Aug 2007
 * Created by Allan Crooks
 * Copyright (C) 2007 Aelitis, All Rights Reserved.
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
package org.gudy.azureus2.ui.swt.views.tableitems.peers;

import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.core3.util.ByteFormatter;
import org.gudy.azureus2.plugins.ui.tables.TableCell;
import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

/**
 * @author Allan Crooks
 *
 */
public class HandshakeReservedBytesItem extends CoreTableColumn implements TableCellRefreshListener {
	
	public HandshakeReservedBytesItem(String table_id) {
		super("handshake_reserved", POSITION_INVISIBLE, 80, table_id);
		setRefreshInterval(INTERVAL_LIVE);
	}

	public void refresh(TableCell cell) {
	    PEPeer peer = (PEPeer)cell.getDataSource();
	    byte[] handshake_reserved = null;
	    if (peer != null) {handshake_reserved = peer.getHandshakeReservedBytes();}
	    
	    if (handshake_reserved == null) {
	    	cell.setText(""); return;
	    }
	    cell.setText(ByteFormatter.nicePrint(handshake_reserved, false));
	}

}
