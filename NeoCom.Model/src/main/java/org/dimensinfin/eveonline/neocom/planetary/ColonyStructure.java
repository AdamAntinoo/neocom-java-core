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
package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IJsonAngular;
import org.dimensinfin.eveonline.neocom.model.ColonyStructureFactory.EPlanetaryStructureType;

//- INTERFACE IMPLEMENTATION ...............................................................................
public interface ColonyStructure extends ICollaboration, IJsonAngular {
	public EPlanetaryStructureType getStructureType ();

	public String getPlanetName ();

	public long getTypeID ();

	public String getTypeName ();

	public long getSchematicID ();

	public long getLastLaunchTime ();

	public long getCycleTime ();

	public long getInstallTime ();

	public long getExpiryTime ();
}
