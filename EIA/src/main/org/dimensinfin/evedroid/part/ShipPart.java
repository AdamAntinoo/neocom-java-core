//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

import java.util.logging.Logger;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.activity.FittingActivity;
import org.dimensinfin.evedroid.activity.ShipDirectorActivity.EShipsVariants;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.holder.Ship4AssetHolder;
import org.dimensinfin.evedroid.holder.Ship4PilotInfoHolder;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.Ship;
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
	public ShipPart(final NeoComAsset node) {
		super(node);
		// Set the expanded state by default
		this.getCastedModel().setExpanded(false);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_shipClassGroup() {
		return this.getCastedModel().getName() + " - " + this.getCastedModel().getGroupName();
	}

	@Override
	public Ship getCastedModel() {
		return (Ship) super.getModel();
	}

	/**
	 * The ship name is the user given name or in case it is not set we resort to the asset id as a
	 * differentiation item and ordering mechanism. Maybe I should use the ship category as a prefix to order
	 * the ships by categories but currently I will use only the asset identification.
	 */
	@Override
	public String getName() {
		return this.get_shipClassGroup();
	}

	@Override
	public void onClick(final View view) {
		if (!clickOverride) {
			// Clean the view to force an update.
			this.invalidate();
			this.toggleExpanded();
			this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
			clickOverride = false;
		}
	}

	/**
	 * This is the method called when the user select one item on the context menu.
	 */
	public boolean onContextItemSelected(final MenuItem item) {
		ShipPart.logger.info(">> [ShipPart.onContextItemSelected]");
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		// Process the command depending on the menu and the item selected
		switch (menuItemIndex) {
			case R.id.addshipasfitting:
				// Add this ship as a fitting.
				Fitting fit = new Fitting(this.getPilot().getAssetsManager());
				// Copy ship items to the fitting.
				Ship ship = this.getCastedModel();
				// Copy ship items to the fitting.
				fit.addHull(ship.getTypeID());
				for (NeoComAsset module : ship.getModules()) {
					fit.fitModule(module.getTypeID());
				}
				for (NeoComAsset module : ship.getRigs()) {
					fit.fitModule(module.getTypeID());
				}
				for (NeoComAsset module : ship.getDrones()) {
					fit.fitModule(module.getTypeID());
				}
				for (NeoComAsset module : ship.getCargo()) {
					fit.fitModule(module.getTypeID());
				}
				// Activate the fitting Activity with this fit as reference. And the pilot
				String label = this.getCastedModel().getUserLabel();
				if (null == label) {
					label = this.getCastedModel().getItemName();
				}
				fit.setName(label);
				AppModelStore.getSingleton().addFitting(fit, label);

				// Open the Fitting Activity
				final Intent intent = new Intent(this.getActivity(), FittingActivity.class);
				intent.putExtra(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(), this.getPilot().getCharacterID());
				intent.putExtra(AppWideConstants.EExtras.EXTRA_FITTINGID.name(), label);
				this.getActivity().startActivity(intent);
				break;

			default:
				break;
		}
		this.invalidate();
		// REFACTOR The event fires a EVENTSTRUCTURE_NEEDSREFRESH that is not
		// processed by the different event managers.
		this.fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_RECALCULATE, this, this);
		ShipPart.logger.info("<< [ShipPart.onContextItemSelected]");
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
		this.getActivity().getMenuInflater().inflate(R.menu.addshipasfitting_menu, menu);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ShipPart [");
		buffer.append(this.get_assetName());
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		if (this.getRenderMode() == AppWideConstants.fragment.FRAGMENT_PILOTINFO_SHIPS)
			return new Ship4PilotInfoHolder(this, _activity);
		if (this.getRenderMode() == AppWideConstants.fragment.FRAGMENT_ASSETSARESHIPS)
			return new Ship4AssetHolder(this, _activity);
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_SHIP4ASSETSBYLOCATION)
			return new Ship4AssetHolder(this, _activity);
		if (this.getRenderMode() == EShipsVariants.SHIPS_BYLOCATION.hashCode())
			return new Ship4AssetHolder(this, _activity);
		return new Ship4AssetHolder(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
