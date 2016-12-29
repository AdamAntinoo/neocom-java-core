//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.industry;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.Action;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

// - CLASS IMPLEMENTATION ...................................................................................
public interface IJobProcess {
	public ArrayList<Action> generateActions4Blueprint();

	public int getCycleDuration();

	public double getJobCost();

	public ArrayList<Resource> getLOM();

	public int getManufacturableCount();

	public double getMultiplier();

	public int getProductID();

	public int getProfitIndex();

	public String getSubtitle();

	public void setAssetsManager(AssetsManager industryAssetsManager);

	void setBlueprint(NeoComBlueprint target);

	void setPilot(NeoComCharacter thePilot);
}

// - UNUSED CODE ............................................................................................
