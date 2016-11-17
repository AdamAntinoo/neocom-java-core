//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.factory;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.IEditPart;
import org.dimensinfin.android.mvc.core.IPartFactory;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;

//- CLASS IMPLEMENTATION ...................................................................................
public abstract class PartFactory implements IPartFactory {
	//- S T A T I C - S E C T I O N ..........................................................................
	private static Logger		logger	= Logger.getLogger("org.dimensinfin.eveonline.neocom.newclass");

	//- F I E L D - S E C T I O N ............................................................................
	private final EFragment	_variant;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public PartFactory(final EFragment _variant2) {
		_variant = _variant2;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	public abstract AbstractCorePart createPart(final AbstractDataSource ds, final IGEFNode node);
	// // if (node instanceof ApiKeyExtended) {
	// // APIKeyPart apipart = (APIKeyPart) new
	// // APIKeyPart((AbstractAndroidNode)
	// // node).setFactory(this).setDataStore(ds);
	// // // locpart.setContainerLocation(false);
	// // // _bodyParts.add(locpart);
	// // return apipart;
	// // }
	// if (node instanceof LabelGroup) {
	// LabelGroupPart groupPart = (LabelGroupPart) new
	// LabelGroupPart((LabelGroup) node).setFactory(this)
	// .setDataStore(ds);
	// // locpart.setContainerLocation(false);
	// // _bodyParts.add(locpart);
	// return groupPart;
	// }
	// // if (node instanceof RegionGroup) {
	// // AbstractAndroidPart rp = new RegionPart((RegionGroup) node)
	// //
	// .setRenderMode(AppWideConstants.rendermodes.RENDER_REGION_ASSETSBYLOCATION);
	// // node.addPropertyChangeListener(rp);
	// // _bodyParts.add(rp);
	// // return;
	// // }
	// // if (node instanceof EveLocation) {
	// // AbstractAndroidPart rp = new LocationAssetsPart(node)
	// // .setRenderMode(AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION);
	// // node.addPropertyChangeListener(rp);
	// // _bodyParts.add(rp);
	// // return;
	// // }
	// // if (node instanceof Asset) {
	// // Asset asset = (Asset) node;
	// // AssetPart apart = null;
	// // if (asset.isPackaged())
	// // apart = (AssetPart) new
	// //
	// AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_ASSET_ASSETPACKAGED);
	// // else {
	// // if (asset.isShip()) {
	// // apart = (AssetPart) new ShipPart(asset)
	// //
	// .setRenderMode(AppWideConstants.rendermodes.RENDER_SHIP_ASSETSBYLOCATION);
	// // node.addPropertyChangeListener(apart);
	// // _bodyParts.add(apart);
	// // return;
	// // }
	// // if (asset.isContainer()) {
	// // apart = (AssetPart) new ContainerPart(asset)
	// //
	// .setRenderMode(AppWideConstants.rendermodes.RENDER_CONTAINER_ASSETSBYLOCATION);
	// // node.addPropertyChangeListener(apart);
	// // _bodyParts.add(apart);
	// // return;
	// // }
	// // apart = (AssetPart) new AssetPart(asset)
	// //
	// .setRenderMode(AppWideConstants.rendermodes.RENDER_ASSET_ASSETSBYLOCATION);
	// // }
	// // node.addPropertyChangeListener(apart);
	// // _bodyParts.add(apart);
	// // return;
	// // }
	// // if (node instanceof LabelGroup) {
	// // LabelGroupPart gp = (LabelGroupPart) new LabelGroupPart((LabelGroup)
	// // node)
	// // .setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
	// // node.addPropertyChangeListener(gp);
	// // _bodyParts.add(gp);
	// // return;
	// // }
	// // if (node instanceof Separator) {
	// // TerminatorPart gp = new TerminatorPart(node);
	// // node.addPropertyChangeListener(gp);
	// // _bodyParts.add(gp);
	// // return;
	// // }
	// return null;
	// }

	//	public IEditPart createPart(final AbstractEditPart abstractEditPart, final Object model) {
	//		return createPart(model);
	//	}
	//
	public abstract IEditPart createPart(final IGEFNode node);
}

// - UNUSED CODE
// ............................................................................................
