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
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.ShipDirectorActivity.EShipsVariants;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.Region;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.model.Ship;
import org.dimensinfin.evedroid.model.ShipLocation;
import org.dimensinfin.evedroid.part.AssetPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.LocationShipsPart;
import org.dimensinfin.evedroid.part.RegionPart;
import org.dimensinfin.evedroid.part.ShipPart;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("ShipPartFactory");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipPartFactory(final String variantSelected) {
		super(variantSelected);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part transformations here. <br>
	 * WARNING: Do not forget to setup the filter in the order most restrictive/less restrictive because some
	 * nodes will fall on multiple categories. The most higher level category should be first. Like
	 * Ship/Asset/Node.
	 */
	@Override
	public IPart createPart(final AbstractComplexNode node) {
		ShipPartFactory.logger.info("-- [ShipPartFactory.createPart]> Node class: " + node.getClass().getName());
		if (node instanceof Region) {
			IPart part = new RegionPart((Separator) node).setFactory(this)
					.setRenderMode(EShipsVariants.valueOf(this.getVariant()).hashCode());
			return part;
		}
		if (node instanceof ShipLocation) {
			IPart part = new LocationShipsPart((ShipLocation) node).setFactory(this)
					.setRenderMode(EShipsVariants.valueOf(this.getVariant()).hashCode());
			return part;
		}
		if (node instanceof Separator) {
			// These special separators can configure an specific icon.
			IPart part = null;
			switch (((Separator) node).getType()) {
				case SHIPSECTION_HIGH:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.filtericonhighslot).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPSECTION_MED:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.filtericonmediumslot).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPSECTION_LOW:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.filtericonlowslot).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPSECTION_RIGS:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.filtericonrigslot).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPSECTION_DRONES:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.filtericondrones).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPSECTION_CARGO:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.itemhangar).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				default:
					part = new GroupPart((Separator) node).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);

			}
			return part;
		}
		if (node instanceof Ship) {
			// There are two renders for Ship. Fitted that are ShipParts and packaged that are AssetParts.
			if (((Ship) node).isPackaged()) {
				IPart part = new ShipPart((NeoComAsset) node).setFactory(this)
						.setRenderMode(EShipsVariants.valueOf(this.getVariant()).hashCode());
				return part;
			} else {
				IPart part = new AssetPart((NeoComAsset) node).setFactory(this)
						.setRenderMode(EShipsVariants.valueOf(this.getVariant()).hashCode());
				return part;
			}
		}
		if (node instanceof NeoComAsset) {
			IPart part = new AssetPart((NeoComAsset) node).setFactory(this)
					.setRenderMode(EShipsVariants.valueOf(this.getVariant()).hashCode());
			return part;
		}
		// If no part is trapped then call the parent chain until one is found.
		return super.createPart(node);
	}
}

// - UNUSED CODE ............................................................................................
