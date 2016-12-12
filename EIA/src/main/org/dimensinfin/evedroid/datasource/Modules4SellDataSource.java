//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.datasource;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.factory.AbstractIndustryDataSource;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.part.LocationMarketPart;
import org.dimensinfin.evedroid.part.StackPart;
import org.dimensinfin.evedroid.storage.AppModelStore;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This datasource will generate the list of buys that are pending to be launched on the market. The
 * operations are grouped by locations so all that operations scheduled to be performed on a market hub will
 * be set toghether and then ordered by entry date being the oldest entered the first to be listed.
 * 
 * @author Adam Antinoo
 */
public class Modules4SellDataSource extends AbstractIndustryDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long												serialVersionUID	= -1904434849082581300L;

	// - F I E L D - S E C T I O N ............................................................................
	private final HashMap<Long, LocationMarketPart>	_locations				= new HashMap<Long, LocationMarketPart>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Modules4SellDataSource(final AppModelStore store) {
		super(store);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The hierarchy contains two levels of elements. The first level are the market hubs where the user has to
	 * launch the market actions. The second level ar the action pending ordered by registration date.
	 */
	@Override
	public void createContentHierarchy() {
		logger.info(">> DirectorsBoardActivity.T2Mod4SellDataSource.createHierarchy");
		// Clear the current list of elements.
		this._root.clear();

		// Get the list of T2 modules for this Pilot. Make it unique
		//		AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
		final ArrayList<NeoComAsset> modules = this._store.getPilot().getAssetsManager().searchT2Modules();
		final HashMap<String, StackPart> mods = new HashMap<String, StackPart>();
		for (final NeoComAsset mc : modules) {
			// Check if the item is already on the list.
			final boolean hit = mods.containsKey(mc.getItemName());
			// Only add to sell list the stacks with more than 9 elements.
			if (mc.getQuantity() >= 10) if (!hit) {
				final StackPart mcpart = new StackPart(new Resource(mc.getTypeID(), mc.getQuantity()));
				mcpart.setRenderMode(AppWideConstants.rendermodes.RENDER_MARKETORDERSCHEDULEDSELL);
				mods.put(mc.getItemName(), mcpart);
				this._root.add(mcpart);
			} else {
				final StackPart mcpart = mods.get(mc.getItemName());
				final Resource res = mcpart.getCastedModel();
				res.setQuantity(res.getQuantity() + mc.getQuantity());
			}
		}
		logger.info("<< DirectorsBoardActivity.T2Mod4SellDataSource.createHierarchy");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		Collections.sort(this._root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		return super.getPartHierarchy();
	}

	//	@Override
	//	public void propertyChange(final PropertyChangeEvent event) {
	//		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
	//			fireStructureChange(AppWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
	//					event.getNewValue());
	//		}
	//	}
}
// - UNUSED CODE ............................................................................................
