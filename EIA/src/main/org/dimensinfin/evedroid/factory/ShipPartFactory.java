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

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractCorePart;
import org.dimensinfin.android.mvc.interfaces.IEditPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.Region;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.model.ShipLocation;
import org.dimensinfin.evedroid.part.AssetPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.RegionPart;
import org.dimensinfin.evedroid.part.LocationShipsPart;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("ShipPartFactory");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipPartFactory(final EVARIANT variantSelected) {
		super(variantSelected);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part transformationes here.
	 */
	@Override
	public IEditPart createPart(final IGEFNode node) {
		if (node instanceof Region) {
			AbstractCorePart part = new RegionPart((Separator) node).setFactory(this);
			((AbstractAndroidPart) part).setRenderMode(EVARIANT.SHIPS_BYLOCATION.hashCode());
			return part;
		}
		if (node instanceof ShipLocation) {
			AbstractCorePart part = new LocationShipsPart((ShipLocation) node).setFactory(this);
			((AbstractAndroidPart) part).setRenderMode(EVARIANT.SHIPS_BYLOCATION.hashCode());
			return part;
		}
		if (node instanceof Separator) {
			AbstractCorePart part = new GroupPart((Separator) node).setFactory(this);
			((AbstractAndroidPart) part).setRenderMode(EVARIANT.SHIPS_BYLOCATION.hashCode());
			return part;
		}
		if (node instanceof NeoComAsset) {
			AbstractCorePart part = new AssetPart((NeoComAsset) node).setFactory(this);
			((AbstractAndroidPart) part).setRenderMode(EVARIANT.SHIPS_BYLOCATION.hashCode());
			return part;
		}
		return null;
	}
}

// - UNUSED CODE ............................................................................................
