//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants.EExtras;
import org.dimensinfin.eveonline.neocom.core.AbstractNeoComNode;
import org.dimensinfin.eveonline.neocom.interfaces.INeoComDirector;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.content.Intent;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class will adapt an Activity to a node to relay some field data to the Part.
 * 
 * @author Adam Antinoo
 */
public class Director extends AbstractNeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger					logger	= Logger.getLogger("Director");
	// - F I E L D - S E C T I O N ............................................................................
	private final INeoComDirector	targetActivity;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Director(final INeoComDirector target) {
		targetActivity = target;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean checkActivation(final NeoComCharacter targetPilot) {
		return targetActivity.checkActivation(targetPilot);
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getActiveIcon() {
		return targetActivity.getIconReferenceActive();
	}

	public int getDimmedIcon() {
		return targetActivity.getIconReferenceInactive();
	}

	public String getName() {
		return targetActivity.getName();
	}

	// TODO the targetActivity should be really replaced bu the Director activity. Just checking if this works
	public void launchActivity(final NeoComCharacter targetPilot) {
		final Intent intent = new Intent(AppModelStore.getSingleton().getActivity(), targetActivity.getClass());
		// Send the pilot id and transfer it to the next Activity
		intent.putExtra(EExtras.EXTRA_CAPSULEERID.name(), targetPilot.getCharacterID());
		AppModelStore.getSingleton().getActivity().startActivity(intent);
	}
}

// - UNUSED CODE ............................................................................................
