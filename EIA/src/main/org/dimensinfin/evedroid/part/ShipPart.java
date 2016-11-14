//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.holder.Ship4AssetHolder;
import org.dimensinfin.evedroid.holder.Ship4PilotInfoHolder;
import org.dimensinfin.evedroid.model.Separator;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipPart extends AssetPart implements OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -8714502444756843667L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_shipClassGroup() {
		return getCastedModel().getName() + " - " + getCastedModel().getGroupName();
	}

	/**
	 * The ship name is the user given name or in case it is not set we resort to the asset id as a
	 * differentiation item and ordering mechanism. Maybe I should use the ship category as a prefix to order
	 * the ships by categories but currently I will use only the asset identification.
	 */
	public String getName() {
		return get_shipClassGroup();
//		String userName = getCastedModel().getUserLabel();
//		if (null == userName)
//			return "#" + getCastedModel().getAssetID();
//		else
//			return userName;
	}

	//	public boolean onLongClick(final View target) {
	//		Log.i("EVEI", ">> ShipPart.onClick");
	//		Asset asset = getCastedModel();
	//		EVEDroidApp.getAppContext().setItem(asset.getItem());
	//		//TODO This should open another detailes ship page
	//		Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
	//		intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, getCastedModel().getAssetID());
	//		Log.i("EVEI", "<< ShipPart.onClick");
	//		return false;
	//	}
	/**
	 * Retunr the items conteined on this ship. There are some grouping for that contents. Use the group
	 * containers to aggregate them into that block to simplify the UI presentation and separate fitted items
	 * from stored items..
	 * 
	 * @return list of parts that are accessible for this node.
	 */
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<IGEFNode> ch = getChildren();
		// Create the groups and then classify the contents of each of them.
		GroupPart hislotGroup = (GroupPart) new GroupPart(new Separator("HISLOT"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
		hislotGroup.setIconReference(R.drawable.hislot);
		GroupPart midslotGroup = (GroupPart) new GroupPart(new Separator("MEDSLOT"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
		midslotGroup.setIconReference(R.drawable.midslot);
		GroupPart lowslotGroup = (GroupPart) new GroupPart(new Separator("LOWSLOT"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
		lowslotGroup.setIconReference(R.drawable.lowslot);
		GroupPart rigslotGroup = (GroupPart) new GroupPart(new Separator("RIGS"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
		rigslotGroup.setIconReference(R.drawable.rigslot);
		GroupPart cargoGroup = (GroupPart) new GroupPart(new Separator("CARGO HOLD"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING);
		cargoGroup.setIconReference(R.drawable.cargohold);
		for (IGEFNode node : ch) {
			int flag;
			if (node instanceof AssetPart) {
				flag = ((AssetPart) node).getCastedModel().getFlag();
				if ((flag > 10) && (flag < 19))
					lowslotGroup.addChild(node);
				else if ((flag > 18) && (flag < 27))
					midslotGroup.addChild(node);
				else if ((flag > 26) && (flag < 35))
					hislotGroup.addChild(node);
				else if ((flag > 91) && (flag < 100))
					rigslotGroup.addChild(node);
				else {
					// Contents on ships do not support expansion but when added to the cargohold.
					cargoGroup.addChild(node);
					AbstractAndroidPart part = (AbstractAndroidPart) node;
					if (part.isExpanded()) {
						ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
						for (AbstractAndroidPart gpart : grand) {
							cargoGroup.addChild(gpart);
						}
					}
				}
			}
		}
		// Add all non empty groups to the result list.
		if (cargoGroup.getChildren().size() > 0) {
			result.add(cargoGroup);
			result.addAll(cargoGroup.getPartChildren());
		}
		if (hislotGroup.getChildren().size() > 0) {
			result.add(hislotGroup);
			result.addAll(hislotGroup.getPartChildren());
		}
		if (midslotGroup.getChildren().size() > 0) {
			result.add(midslotGroup);
			result.addAll(midslotGroup.getPartChildren());
		}
		if (lowslotGroup.getChildren().size() > 0) {
			result.add(lowslotGroup);
			result.addAll(lowslotGroup.getPartChildren());
		}
		if (rigslotGroup.getChildren().size() > 0) {
			result.add(rigslotGroup);
			result.addAll(rigslotGroup.getPartChildren());
		}
		return result;
	}

	public void onClick(final View view) {
		// Toggle location to show its contents.
		toggleExpanded();
		fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("ShipPart [");
		buffer.append(get_assetName());
		buffer.append(" ]");
		return buffer.toString();
	}

	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		if (getRenderMode() == AppWideConstants.fragment.FRAGMENT_PILOTINFO_SHIPS)
			return new Ship4PilotInfoHolder(this, _activity);
		if (getRenderMode() == AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS)
			return new Ship4AssetHolder(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_SHIP4ASSETSBYLOCATION)
			return new Ship4AssetHolder(this, _activity);
		return new Ship4AssetHolder(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
