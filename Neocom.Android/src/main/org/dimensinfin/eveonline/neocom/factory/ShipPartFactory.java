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
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.activity.ShipDirectorActivity.EShipsVariants;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.Region;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.model.Ship;
import org.dimensinfin.eveonline.neocom.part.AssetPart;
import org.dimensinfin.eveonline.neocom.part.GroupPart;
import org.dimensinfin.eveonline.neocom.part.LocationShipsPart;
import org.dimensinfin.eveonline.neocom.part.RegionPart;
import org.dimensinfin.eveonline.neocom.part.ShipPart;

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
		if (node instanceof EveLocation) {
			IPart part = new LocationShipsPart((EveLocation) node).setFactory(this)
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
				case SHIPTYPE_BATTLECRUISER:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupbattlecruiser).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPTYPE_BATTLESHIP:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupbattleship).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPTYPE_CAPITAL:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupcapital).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPTYPE_CRUISER:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupcruiser).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPTYPE_DESTROYER:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupdestroyer).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPTYPE_FREIGHTER:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupfreighter).setFactory(this)
							.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
					break;
				case SHIPTYPE_FRIGATE:
					part = new GroupPart((Separator) node).setIconReference(R.drawable.groupfrigate).setFactory(this)
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
