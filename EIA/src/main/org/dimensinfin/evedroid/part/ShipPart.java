//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.FittingActivity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.holder.Ship4AssetHolder;
import org.dimensinfin.evedroid.holder.Ship4PilotInfoHolder;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipPart extends AssetPart implements OnClickListener, IMenuActionTarget {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -8714502444756843667L;
	private static Logger			logger						= Logger.getLogger("ShipPart");

	// - F I E L D - S E C T I O N ............................................................................
	/** This field if to remove the click after a long click. */
	private boolean						clickOverride			= false;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipPart(final AbstractGEFNode node) {
		super(node);
		// Set the expanded state by default
		getCastedModel().setExpanded(false);
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
	@Override
	public String getName() {
		return get_shipClassGroup();
		//		String userName = getCastedModel().getUserLabel();
		//		if (null == userName)
		//			return "#" + getCastedModel().getAssetID();
		//		else
		//			return userName;
	}

	//		public boolean onLongClick(final View target) {
	//			Log.i("EVEI", ">> ShipPart.onClick");
	//			Asset asset = getCastedModel();
	//			EVEDroidApp.getAppContext().setItem(asset.getItem());
	//			//TODO This should open another detailes ship page
	//			Intent intent = new Intent(getActivity(), ItemDetailsActivity.class);
	//			intent.putExtra(AppWideConstants.extras.EXTRA_EVEITEMID, getCastedModel().getAssetID());
	//			Log.i("EVEI", "<< ShipPart.onClick");
	//			return false;
	//		}
	/**
	 * Return the items contained on this ship. There are some grouping for that contents. Use the group
	 * containers to aggregate them into that block to simplify the UI presentation and separate fitted items
	 * from stored items..
	 * 
	 * @return list of parts that are accessible for this node.
	 */
	@Deprecated
	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> ch = getChildren();
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
		for (AbstractPropertyChanger node : ch) {
			int flag;
			if (node instanceof AssetPart) {
				flag = ((AssetPart) node).getCastedModel().getFlag();
				if ((flag > 10) && (flag < 19)) {
					lowslotGroup.addChild(node);
				} else if ((flag > 18) && (flag < 27)) {
					midslotGroup.addChild(node);
				} else if ((flag > 26) && (flag < 35)) {
					hislotGroup.addChild(node);
				} else if ((flag > 91) && (flag < 100)) {
					rigslotGroup.addChild(node);
				} else {
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

	@Override
	public void onClick(final View view) {
		if (!clickOverride) {
			// Clean the view to force an update.
			invalidate();
			toggleExpanded();
			fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
			clickOverride = false;
		}
	}

	/**
	 * This is the method called when the user select one item on the context menu.
	 */
	public boolean onContextItemSelected(final MenuItem item) {
		logger.info(">> [ShipPart.onContextItemSelected]");
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		// Process the command depending on the menu and the item selected
		switch (menuItemIndex) {
			case R.id.addshipasfitting:
				// Add this ship as a fitting.
				Fitting fit = new Fitting(getPilot().getAssetsManager());
				logger.info("-- [ShipPart.onContextItemSelected]> New for for hull: " + this.getCastedModel());
				fit.addHull(this.getCastedModel().getTypeID());
				// Add part children as Fitting content.
				for (AbstractPropertyChanger node : children) {
					if (node instanceof AssetPart) {
						int flag = ((AssetPart) node).getCastedModel().getFlag();
						if ((flag > 10) && (flag < 19)) {
							fit.fitModule(((AssetPart) node).getCastedModel().getTypeID());
						} else if ((flag > 18) && (flag < 27)) {
							fit.fitModule(((AssetPart) node).getCastedModel().getTypeID());
						} else if ((flag > 26) && (flag < 35)) {
							fit.fitModule(((AssetPart) node).getCastedModel().getTypeID());
						} else if ((flag > 91) && (flag < 100)) {
							fit.fitRig(((AssetPart) node).getCastedModel().getTypeID());
						} else {
							// Contents on ships go to the cargohold.
							fit.addCargo(((AssetPart) node).getCastedModel().getTypeID(),
									((AssetPart) node).getCastedModel().getQuantity());
						}
					}
				}
				// Activate the fitting Activity with this fit as reference. And the pilot
				AppModelStore store = EVEDroidApp.getAppStore();
				String label = this.getCastedModel().getUserLabel();
				if (null == label) {
					label = getCastedModel().getItemName();
				}
				store.addFitting(fit, label);

				// Open the Fitting Activity
				final Intent intent = new Intent(getActivity(), FittingActivity.class);
				intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
				intent.putExtra(AppWideConstants.EExtras.FITTINGID.name(), label);
				getActivity().startActivity(intent);
				break;

			default:
				break;
		}
		invalidate();
		// REFACTOR The event fires a EVENTSTRUCTURE_NEEDSREFRESH that is not
		// processed by the different event managers.
		fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
		logger.info("<< [ShipPart.onContextItemSelected]");
		return true;
	}

	/**
	 * If the user stays for some time on this view element then we should open a context menu that allows to
	 * select an action to add this ship as a fitting blueprint to be used in Industry jobs. The contextual menu
	 * allows for a better control of the interaction because allows to create a selection menu or a dialog.
	 */
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		// Clear click detection.
		clickOverride = false;
		// Show the menu to add this ship configuration as a Fitting.
		getActivity().getMenuInflater().inflate(R.menu.addshipasfitting_menu, menu);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ShipPart [");
		buffer.append(get_assetName());
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
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
