//  PROJECT:     NeoCom.DataManagement(NEOC.DM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java JRE 1.5 Specification.
//  DESCRIPTION: NeoCom pure Java library to maintain and manage all the data streams and
//                 connections. It will use the Models as the building blocks for the data
//                 and will isolate to the most the code from any platform implementation.
//               It will contain the Model Generators and use the external facilities for
//                 network connections to CCP XML api, CCP ESI api and Database storage. It
//                 will also make use of Cache facilities that will be glued at compilation
//                 time depending on destination platform.
package org.dimensinfin.eveonline.neocom.model;

import com.tlabs.eve.api.character.PlanetaryPin;

import org.dimensinfin.core.interfaces.IDownloadable;
import org.dimensinfin.core.interfaces.IExpandable;
import org.dimensinfin.core.interfaces.IJsonAngular;

import java.beans.PropertyChangeListener;
import java.util.List;

//- INTERFACE IMPLEMENTATION ...............................................................................
public interface Colony extends IDownloadable, IExpandable, IJsonAngular {
	public void addStructure (ColonyStructure structure);

	public long getPlanetID ();

	public String getPlanetName ();

	public String getSolarSystemName ();

	public long getPlanetTypeID ();

	public String getPlanetTypeName ();

	public int getNumberOfPins ();

	public List<PlanetaryPin> getPins ();

	public boolean isEmpty ();

	public void fireStructureChange (final String propertyName, final Object oldValue, final Object newValue);

	public void addPropertyChangeListener (final PropertyChangeListener newListener);

	public int getStructureCount ();

}
