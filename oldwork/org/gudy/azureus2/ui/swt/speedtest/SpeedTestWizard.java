/*
 * Created on Apr 30, 2007
 * Created by Paul Gardner
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
 * AELITIS, SAS au capital de 63.529,40 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.ui.swt.speedtest;


import org.eclipse.swt.widgets.Display;
import org.gudy.azureus2.ui.swt.wizard.Wizard;

import com.aelitis.azureus.core.AzureusCore;

/**
 * @author Olivier
 *  
 */
public class 
SpeedTestWizard 
	extends Wizard 
{
	protected static final String	CFG_PREFIX = "speedtest.wizard.";

    public
	SpeedTestWizard(
		AzureusCore		azureus_core,
		Display 		display) 
	{
		super(azureus_core, "speedtest.wizard.title");
		SpeedTestPanel panel = new SpeedTestPanel(this, null);
		setFirstPanel(panel);
    }

    public void onClose(){

        super.onClose();
    }

}
