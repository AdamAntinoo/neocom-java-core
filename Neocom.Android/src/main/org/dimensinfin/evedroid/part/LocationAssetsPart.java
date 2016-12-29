//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.render.Location4AssetsRender;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

import com.j256.ormlite.dao.Dao;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class LocationAssetsPart extends LocationPart implements IMenuActionTarget, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long										serialVersionUID	= -1226559463320336724L;
	private static String												_contextMenuTitle	= "Select Location Role";
	private static String[]											_contextMenu			= null;

	// - F I E L D - S E C T I O N ............................................................................
	private HashMap<Long, AbstractAndroidPart>	containerList			= new HashMap<Long, AbstractAndroidPart>();
	private final HashMap<Long, AssetPart>			containers				= new HashMap();;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public LocationAssetsPart(final EveLocation location) {
		super(location);
		LocationAssetsPart._contextMenu = EVEDroidApp.getSingletonApp().getResources()
				.getStringArray(R.array.locationFunctions);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Returns the number of assets located in this Eve Location. There are two values, one for the Location
	 * collapsed that will show the total number of assets that are found on the database and the other values
	 * when the Location is expanded that will show the number of graphical items (these are the real children
	 * on the location) that will hide the number of items inside other containers.
	 * 
	 * @return
	 */
	@Override
	public String get_locationContentCount() {
		long locationAssets = 0;
		if (this.getCastedModel().isExpanded())
			locationAssets = this.getChildren().size();
		else
			try {
				Dao<NeoComAsset, String> assetDao = AppConnector.getDBConnector().getAssetDAO();
				// DEBUG I have to get access to the pilot ID for some filters. Possible set the parent the root and from it to the Pilot
				locationAssets = assetDao
						.countOf(assetDao.queryBuilder().setCountOf(true).where().eq("ownerID", this.getPilot().getCharacterID())
								.and().eq("locationID", this.getCastedModel().getID()).prepare());
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
			}
		String countString = null;
		if (locationAssets > 1)
			countString = EveAbstractPart.qtyFormatter.format(locationAssets) + " items";
		else
			countString = EveAbstractPart.qtyFormatter.format(locationAssets) + " item";
		return countString;
	}

	public double getItemsValoration() {
		return itemsValueISK;
	}

	public double getItemsVolume() {
		return itemsVolume;
	}

	/**
	 * This is a lazy evaluation implementation that will access their database contents on the fist call. If
	 * those contents are already on place it will return the signal to update the UI.<br>
	 * During the download of the contents it will detect BPC and process the assets to generate aggregations of
	 * BPC to simplify the interface. It will also calculate the number of real assets (shown when the Location
	 * is collapsed) and the number of children (shown when the Location is expanded).<br>
	 * During the processing the number of contents should be changed to a spinning later replaced by the right
	 * number (expand/collapse).
	 */
	public void onClick(final View view) {
		if (!this.isDownloaded()) {
			// Generate the children of this Location and its Containers.
			this.clean();
			// Get the state of the configuration setting for the asset calculations.
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
			boolean calculateValue = sharedPrefs.getBoolean(AppWideConstants.preference.PREF_CALCULATEASSETVALUE, true);
			try {
				AssetsManager manager = this.getPilot().getAssetsManager();
				ArrayList<NeoComAsset> assetList = manager.searchAsset4Location(this.getCastedModel());
				for (NeoComAsset asset : assetList) {
					// Detect the type: Asset / Ship / Container
					AssetPart apart = null;
					if (asset.isShip()) {
						// Check if the ship is packaged.
						if (asset.isPackaged())
							apart = (AssetPart) new AssetPart(asset).setRenderMode(this.getRenderMode());
						else
							apart = (AssetPart) new ShipPart(asset).setRenderMode(this.getRenderMode());
					} else if (asset.isContainer())
						// Check if the ship is packaged.
						if (asset.isPackaged())
						apart = (AssetPart) new AssetPart(asset).setRenderMode(this.getRenderMode());
						else
						apart = (AssetPart) new ContainerPart(asset).setRenderMode(this.getRenderMode());
					else
						apart = (AssetPart) new AssetPart(asset).setRenderMode(this.getRenderMode());
					if (calculateValue) this.calculateValue(asset, apart);

					// Assets may not contain a parent (so they are on the Hangar floor) or are inside a Container/Ship
					NeoComAsset container = asset.getParentContainer();
					if (null == container)
						this.add2Location(apart);
					else
						this.add2Container(apart);
				}
				// Marks data as downloaded.
				this.getCastedModel().setDownloaded(true);
				// Clear current hierarchy before reloading the new.
				containerList = new HashMap<Long, AbstractAndroidPart>();
			} catch (RuntimeException rte) {
				// TODO Auto-generated catch block
				rte.printStackTrace();
				Log.i("LocationPart.onClick", "SQL Error while rading location assets.");
			}
		}
		// Toggle location to show its contents.
		this.getCastedModel().toggleExpanded();
		this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	public boolean onContextItemSelected(final MenuItem item) {
		Log.i("NEOCOM", ">> LocationAssetsPart.onContextItemSelected"); //$NON-NLS-1$
		final int menuItemIndex = item.getItemId();
		final String menuItemName = LocationAssetsPart._contextMenu[menuItemIndex];
		final EveLocation theSelectedLocation = this.getCastedModel();
		// Check for the role clearing.
		if (menuItemName.equalsIgnoreCase("-CLEAR-"))
			this.getPilot().clearLocationRoles(theSelectedLocation);
		else
			this.getPilot().addLocationRole(theSelectedLocation, menuItemName);
		this.invalidate();
		this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
		Log.i("NEOCOM", "<< LocationAssetsPart.onContextItemSelected"); //$NON-NLS-1$
		return true;
	}

	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("NEOCOM", ">> LocationAssetsPart.onCreateContextMenu"); //$NON-NLS-1$
		//		String _contextMenuTitle = "Select Location Role";
		//		String[] _contextMenu = EVEDroidApp.getSingletonApp().getResources().getStringArray(R.array.locationFunctions);
		menu.setHeaderTitle(LocationAssetsPart._contextMenuTitle);
		for (int i = 0; i < LocationAssetsPart._contextMenu.length; i++)
			menu.add(Menu.NONE, i, i, LocationAssetsPart._contextMenu[i]);
		Log.i("NEOCOM", "<< LocationAssetsPart.onCreateContextMenu"); //$NON-NLS-1$
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new Location4AssetsRender(this, _activity);
	}

	/**
	 * Search for this container reference on this Location's children until found. Then aggregates the asset to
	 * that container calculating stacking if this is possible. There can be containers inside container like
	 * the case where a container is on the hols of a ship. That special case will not be implemented on this
	 * first approach and all the container will be located at the Location's hangar floor.<br>
	 * Containers also do not have its market value added to the location's aggregation.
	 * 
	 * @param apart
	 */
	private void add2Container(final AssetPart apart) {
		Log.i("LocationAssetsPart", ">> LocationAssetsPart.add2Container");
		// Locate the container if already added to the location.
		long pcid = apart.getCastedModel().getParentContainer().getDAOID();
		Vector<IPart> childs = this.getChildren();
		for (IPart child : childs)
			if (child instanceof AssetPart) {
				AssetPart check = (AssetPart) child;
				long ccid = check.getCastedModel().getDAOID();
				if (ccid == pcid) {
					if (apart.getCastedModel().isBlueprint())
						check.checkAssetStacking(apart);
					else
						check.addChild(apart);
					Log.i("LocationAssetsPart", ".. LocationAssetsPart.add2Container added to container: " + ccid);
					return;
				}
			}
		// Add the container to the location and the the item to the container.
		NeoComAsset container = apart.getCastedModel().getParentContainer();
		AssetPart newpart = null;
		if (container.isShip())
			newpart = (AssetPart) new ShipPart(container).setRenderMode(this.getRenderMode());
		else if (container.isContainer())
			newpart = (AssetPart) new ContainerPart(container).setRenderMode(this.getRenderMode());
		else
			newpart = (AssetPart) new AssetPart(container).setRenderMode(this.getRenderMode());
		this.addChild(newpart);
		newpart.addChild(apart);
		Log.i("LocationAssetsPart", ".. LocationAssetsPart.add2Container created new container: " + container.getDAOID());
	}

	private void add2Location(final AssetPart apart) {
		//	if (apart instanceof AssetPart) {
		// Stacking is only for BPC
		//	if (apart instanceof AssetPart)
		if (apart.getCastedModel().isBlueprint())
			this.checkAssetStacking(apart);
		else
			this.addChild(apart);
		//	}
	}
}

// - UNUSED CODE ............................................................................................
