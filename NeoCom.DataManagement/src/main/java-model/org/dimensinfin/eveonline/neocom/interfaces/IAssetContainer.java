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

import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;

import java.util.List;

/**
 * This interface has the methods for a virtual Asset container. Such type of containers have lists of assets while also store a
 * reference to the faceted owner of those assets like Locations, Containers, Holds or Ships and Citadels.
 * @author Adam Antinoo
 */
public interface IAssetContainer /*extends IExpandable */ {
	public int addAsset( NeoComAsset asset );

	public List<NeoComAsset> getAssets();

	public int getContentSize();

	public double getTotalValue();

	public double getTotalVolume();
}
