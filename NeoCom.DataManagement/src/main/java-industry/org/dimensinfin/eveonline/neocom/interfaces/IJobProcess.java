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

import java.util.ArrayList;

import com.beimin.eveapi.model.shared.Blueprint;

import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IJobProcess {
//	public ArrayList<Action> generateActions4Blueprint();

	public int getCycleDuration();

	public double getJobCost();

	public ArrayList<Resource> getLOM();

	public int getManufacturableCount();

	public double getMultiplier();

	public int getProductId();

	public int getProfitIndex();

//	public String getSubtitle();

//	public void setAssetsManager(AssetsManager industryAssetsManager);

	void setBlueprint( Blueprint target );

//	void setPilot(EveChar thePilot);
}

// - UNUSED CODE ............................................................................................
