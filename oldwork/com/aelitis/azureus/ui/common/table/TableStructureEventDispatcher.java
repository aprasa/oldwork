/**
 * File    : TableStructureEventDispatcher.java
 * Created : 27 nov. 2003
 * By      : Olivier
 *
 * Copyright (C) 2004-2007 Aelitis SAS, All rights Reserved
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
 *
 * AELITIS, SAS au capital de 46,603.30 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */

package com.aelitis.azureus.ui.common.table;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.gudy.azureus2.core3.util.AEMonitor;

import com.aelitis.azureus.core.util.CopyOnWriteList;

/**
 * @author Olivier
 *
 */
public class TableStructureEventDispatcher implements
		TableStructureModificationListener
{

	private static Map instances = new HashMap();

	private static AEMonitor class_mon = new AEMonitor(
			"TableStructureEventDispatcher:class");

	private CopyOnWriteList listeners;

	private AEMonitor listeners_mon = new AEMonitor(
			"TableStructureEventDispatcher:L");

	/**
	 * 
	 */
	private TableStructureEventDispatcher() {
		listeners = new CopyOnWriteList(2);
	}

	public static TableStructureEventDispatcher getInstance(String sTableID) {
		try {
			class_mon.enter();

			TableStructureEventDispatcher instance = (TableStructureEventDispatcher) instances.get(sTableID);
			if (instance == null) {
				instance = new TableStructureEventDispatcher();
				instances.put(sTableID, instance);
			}
			return instance;
		} finally {

			class_mon.exit();
		}
	}

	public void addListener(TableStructureModificationListener listener) {
		try {
			listeners_mon.enter();

			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}

		} finally {

			listeners_mon.exit();
		}
	}

	public void removeListener(TableStructureModificationListener listener) {
		try {
			listeners_mon.enter();

			listeners.remove(listener);
		} finally {

			listeners_mon.exit();
		}
	}

	public void tableStructureChanged() {

			Iterator iter = listeners.iterator();
			while (iter.hasNext()) {
				TableStructureModificationListener listener = (TableStructureModificationListener) iter.next();
				listener.tableStructureChanged();
			}
	}

	public void columnSizeChanged(TableColumnCore tableColumn) {
			Iterator iter = listeners.iterator();
			while (iter.hasNext()) {
				TableStructureModificationListener listener = (TableStructureModificationListener) iter.next();
				listener.columnSizeChanged(tableColumn);
			}
	}

	public void columnInvalidate(TableColumnCore tableColumn) {

			Iterator iter = listeners.iterator();
			while (iter.hasNext()) {
				TableStructureModificationListener listener = (TableStructureModificationListener) iter.next();
				listener.columnInvalidate(tableColumn);
			}

	}

	public void cellInvalidate(TableColumnCore tableColumn, Object data_source) {

			Iterator iter = listeners.iterator();
			while (iter.hasNext()) {
				TableStructureModificationListener listener = (TableStructureModificationListener) iter.next();
				listener.cellInvalidate(tableColumn, data_source);
			}

	}

	
	public void columnOrderChanged(int[] iPositions) {

			Iterator iter = listeners.iterator();
			while (iter.hasNext()) {
				TableStructureModificationListener listener = (TableStructureModificationListener) iter.next();
				listener.columnOrderChanged(iPositions);
			}
	}
}
