/*
 * 11/19/04		1.0 moved to LGPL.
 * 29/01/00		Initial version. mdm@techie.com
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package javazoom.jl.player;

import javazoom.jl.decoder.JavaLayerException;
public abstract class AudioDeviceFactory
{
	/**
	 * Creates a new <code>AudioDevice</code>.
	 * 
	 * @return	a new instance of a specific class of <code>AudioDevice</code>.
	 * @throws	JavaLayerException if an instance of AudioDevice could not
	 *			be created. 
	 */
	public abstract AudioDevice createAudioDevice() throws JavaLayerException;
	
	/**
	 * Creates an instance of an AudioDevice implementation. 
	 * @param loader	The <code>ClassLoader</code> to use to
	 *					load the named class, or null to use the
	 *					system class loader.
	 * @param name		The name of the class to load.
	 * @return			A newly-created instance of the audio device class.
	 */
	protected AudioDevice instantiate(ClassLoader loader, String name)
		throws ClassNotFoundException, 
			   IllegalAccessException, 
			   InstantiationException
	{
		AudioDevice dev = null;
		
		@SuppressWarnings("rawtypes")
		Class cls = null;
		if (loader==null)
		{
			cls = Class.forName(name);
		}
		else
		{
			cls = loader.loadClass(name);	
		}

		Object o = cls.newInstance();
		dev = (AudioDevice)o;
		
		return dev;
	}
}
