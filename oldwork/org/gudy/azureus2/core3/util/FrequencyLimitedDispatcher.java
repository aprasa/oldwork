/*
 * Created on 9 Aug 2006
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
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.core3.util;

public class 
FrequencyLimitedDispatcher 
{
	private AERunnable	target;
	private long		min_millis;
	
	private long			last_run;
	private DelayedEvent	delay_event;
	
	public
	FrequencyLimitedDispatcher(
		AERunnable			_target,
		int					_min_frequency_millis )
	{
		target		= _target;
		min_millis	= _min_frequency_millis;
	}
	
	public void
	dispatch()
	{
		long	now = SystemTime.getCurrentTime();
		
		boolean	run_it	= false;
		
		synchronized( this ){
			
			if ( delay_event == null ){
				
				long	delay = min_millis - (now - last_run );
	
				if ( now < last_run || delay <= 0 ){
					
					last_run	= now;
					
					run_it		= true;
					
				}else{
					
						// run too recently
											
					delay_event = 
						new DelayedEvent(
							"FreqLimDisp",
							delay,
							new AERunnable()
							{
								public void
								runSupport()
								{
									long	now = SystemTime.getCurrentTime();

									synchronized( FrequencyLimitedDispatcher.this ){

										last_run = now;
										
										delay_event	= null;
									}
									
									target.run();
								}
							});
				}
			}
		}
		
		if ( run_it ){
			
			target.run();
		}
	}
}
