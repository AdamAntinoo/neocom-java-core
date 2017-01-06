//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.factory;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.activity.DirectorsBoardActivity.EDashboardVariants;
import org.dimensinfin.eveonline.neocom.activity.PilotListActivity.EAccountsVariants;
import org.dimensinfin.eveonline.neocom.model.Corporation;
import org.dimensinfin.eveonline.neocom.model.Director;
import org.dimensinfin.eveonline.neocom.model.NeoComApiKey;
import org.dimensinfin.eveonline.neocom.model.Pilot;
import org.dimensinfin.eveonline.neocom.part.ApiKeyPart;
import org.dimensinfin.eveonline.neocom.part.DirectorPart;
import org.dimensinfin.eveonline.neocom.part.PilotInfoPart;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("PilotPartFactory");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotPartFactory(final String variantSelected) {
		super(variantSelected);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part transformations here.
	 */
	@Override
	public IPart createPart(final AbstractComplexNode node) {
		PilotPartFactory.logger.info("-- [PilotPartFactory.createPart]> Node class: " + node.getClass().getName());
		if (this.getVariant() == EAccountsVariants.CAPSULEER_LIST.name()) {
			if (node instanceof NeoComApiKey) {
				IPart part = new ApiKeyPart(node).setFactory(this);
				return part;
			}
			if (node instanceof Pilot) {
				IPart part = new PilotInfoPart(node).setFactory(this);
				return part;
			}
			if (node instanceof Corporation) {
				IPart part = new PilotInfoPart(node).setFactory(this);
				return part;
			}
		}
		if (this.getVariant() == EDashboardVariants.NEOCOM_DASHBOARD.name()) {
			if (node instanceof Director) {
				IPart part = new DirectorPart((Director) node);
				return part;
			}
		}
		// If no part is trapped then call the parent chain until one is found.
		return super.createPart(node);
	}
}

// - UNUSED CODE ............................................................................................
