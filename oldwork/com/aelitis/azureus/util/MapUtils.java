/**
 * Copyright (C) 2007 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.util;

import java.util.*;

import org.bouncycastle.util.encoders.Base64;
import org.gudy.azureus2.core3.util.Base32;

/**
 * @author TuxPaper
 * @created Jun 1, 2007
 *
 */
public class MapUtils
{
	public static int getMapInt(Map map, String key, int def) {
		try {
			return ((Number) map.get(key)).intValue();
		} catch (Exception e) {
			return def;
		}
	}

	public static long getMapLong(Map map, String key, long def) {
		try {
			return ((Number) map.get(key)).longValue();
		} catch (Exception e) {
			return def;
		}
	}

	public static String getMapString(Map map, String key, String def) {
		try {
			Object o = map.get(key);
			if (o == null && !map.containsKey(key)) {
				return def;
			}
			if (o instanceof String) {
				return (String) o;
			}
			if (o instanceof byte[]) {
				return new String((byte[]) o, "utf-8");
			}
			return def;
		} catch (Exception t) {
			return def;
		}
	}

	public static byte[] getMapByteArray(Map map, String key, byte[] def) {
		try {
			Object o = map.get(key);
			if (o instanceof byte[]) {
				return (byte[]) o;
			}

			String b64Key = key + ".B64";
			if (map.containsKey(b64Key)) {
				o = map.get(b64Key);
				if (o instanceof String) {
					return Base64.decode((String) o);
				}
			}

			String b32Key = key + ".B32";
			if (map.containsKey(b32Key)) {
				o = map.get(b32Key);
				if (o instanceof String) {
					return Base32.decode((String) o);
				}
			}

			return def;
		} catch (Exception t) {
			return def;
		}
	}

	public static Object getMapObject(Map map, String key, Object def, Class cla) {
		try {
			Object o = map.get(key);
			if (cla.isInstance(o)) {
				return o;
			} else {
				return def;
			}
		} catch (Exception t) {
			return def;
		}
	}

	public static boolean getMapBoolean(Map map, String key, boolean def) {
		try {
			Object o = map.get(key);
			if (o instanceof Boolean) {
				return ((Boolean) o).booleanValue();
			}
			
			if (o instanceof Long) {
				return ((Long) o).longValue() == 1;
			}
			
			return def;
		} catch (Exception e) {
			return def;
		}
	}

	public static List getMapList(Map map, String key, List def) {
		try {
			List list = (List) map.get(key);
			if (list == null && !map.containsKey(key)) {
				return def;
			}
			return list;
		} catch (Exception t) {
			return def;
		}
	}

	public static Map getMapMap(Map map, String key, Map def) {
		try {
			Map valMap = (Map) map.get(key);
			if (valMap == null && !map.containsKey(key)) {
				return def;
			}
			return valMap;
		} catch (Exception t) {
			return def;
		}
	}

}
