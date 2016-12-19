//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.factory;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.model.Corporation;
import org.dimensinfin.evedroid.model.NeoComApiKey;
import org.dimensinfin.evedroid.model.Pilot;
import org.dimensinfin.evedroid.part.ApiKeyPart;
import org.dimensinfin.evedroid.part.PilotInfoPart;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("PilotPartFactory");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotPartFactory(final EVARIANT variantSelected) {
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
		if (this.getVariant() == EVARIANT.CAPSULEER_LIST.name()) {
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
		// If no part is trapped then call the parent chain until one is found.
		return super.createPart(node);
	}
}

// - UNUSED CODE ............................................................................................
