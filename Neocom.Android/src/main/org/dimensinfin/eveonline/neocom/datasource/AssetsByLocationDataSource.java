//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.datasource;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.core.RootPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Region;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This DataSource will manage the elements to shown on the assets list for a character in the page that lists
 * those assets by Region - Location - Container order. If the number of locations is greater than a
 * predefined setting (that will be modifiable on the UI on the settings page) then the locations will be
 * grouped into Regions. If the number is lower the locations will be the first display level. <br>
 * The second and next levels will be composed with Containers, Ships and Assets. Those levels will only be
 * read from the database only if the container or location is expanded so this will use a lazy evaluation
 * pattern do read and generate the momory structures that contain the assets.<br>
 * To manage this asset information the DataSource will heavily interface with the model AssetsManager that
 * will have stored all the downloaded information (so a change on an Activity will not clear that data) and
 * all the functionalities to manage assets when they are stored on the database and also if they are stored
 * in local memory.
 * 
 * @author Adam Antinoo
 */
public class AssetsByLocationDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("AssetsByLocationDataSource");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsByLocationDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * We will build the list of assets ordered by locations. This is quite easy since we have already selected
	 * the assets from the database and they are already stored on the AssetsManager. If the number of locations
	 * is below the limit then do not use the Regions.
	 */
	public RootNode collaborate2Model() {
		AbstractDataSource.logger.info(">> `[AssetsByLocationDataSource.collaborate2Model]");
		_dataModelRoot = new RootNode();
		try {
			final AssetsManager manager = AppModelStore.getSingleton().getPilot().getAssetsManager();
			//		 HashMap<Long, EveLocation> locations = manager.getLocations();
			if (this.showRegions(manager.getLocations().size())) {
				for (Region region : manager.getRegions().values()) {
					_dataModelRoot.addChild(region);
				}
			} else {
				for (EveLocation location : manager.getLocations().values()) {
					_dataModelRoot.addChild(location);
				}
			}
		} catch (final RuntimeException rte) {
			rte.printStackTrace();
			AbstractDataSource.logger.severe("E> There is a problem at: AssetsByLocationDataSource.createHierarchy.");
		}
		AbstractDataSource.logger.info("<< [AssetsByLocationDataSource.collaborate2Model]> model size: " + _root.size());
		return _dataModelRoot;
	}

	/**
	 * Set the RootPart and the sort element for it. Sort the regions or the locations by name.
	 */
	@Override
	public void createContentHierarchy() {
		_partModelRoot = new RootPart(_dataModelRoot, _partFactory)
				.setSorting(NeoComApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		super.createContentHierarchy();
	}

	@Override
	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		return new ArrayList<AbstractAndroidPart>();
	}

	private boolean showRegions(final int numberLocations) {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(AppModelStore.getSingleton().getActivity());
		final String locLimitString = prefs.getString(AppWideConstants.preference.PREF_LOCATIONSLIMIT,
				AppConnector.getResourceString(R.string.pref_numberOfLocations_default));
		// Check for the special value of unlimited.
		if (locLimitString.equalsIgnoreCase("Unlimited")) return false;
		// Convert the stored preference value to a number.
		int locLimit = 10;
		try {
			locLimit = Integer.parseInt(locLimitString);
		} catch (NumberFormatException nex) {
			locLimit = 10;
		}
		if (numberLocations > locLimit)
			return true;
		else
			return false;
	}
}

// - UNUSED CODE ............................................................................................
