//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.interfaces;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

/**
 * This interface defines the methods to be declared on assets (be them asset or blueprints) that have pointers to Locations.
 * While that pointer may point to a real Locations, most of the time points to a container asset. Then we should search for
 * the upper top most Location in the asset hierarchy to get the real location while storing the container reference as a new
 * pointer on thie node.
 */
public interface ILocatableAsset {
//	GetCharactersCharacterIdAssets200Ok.LocationTypeEnum getLocationType();

	long getLocationId();

	EveLocation getLocation();

	long getParentContainerId();

	NeoComAsset getParentContainer();

	boolean hasParent();

	NeoComNode setLocationId( long location );

	NeoComNode setLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType );

	NeoComNode setLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum newFlag );

	NeoComNode setLocationFlag( GetCharactersCharacterIdBlueprints200Ok.LocationFlagEnum newFlag );

	void setParentId( long pid );

	void setParentContainer( NeoComAsset newParent );

	NeoComNode store();
}
