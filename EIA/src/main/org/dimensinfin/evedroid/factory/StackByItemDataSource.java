//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.factory;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.EveItem;
import org.dimensinfin.evedroid.part.AssetPart;
import org.dimensinfin.evedroid.storage.AppModelStore;

//- CLASS IMPLEMENTATION ...................................................................................
/**
 * This Data Source will get the list of stacks that belonging to the current pilot have the type id selected.
 * This is performed with a cached query so the result should be quite fast once selected any time.
 * 
 * @author Adam Antinoo
 */
public class StackByItemDataSource extends AbstractIndustryDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 5106727564899914293L;

	// - F I E L D - S E C T I O N ............................................................................
	private EveItem						item							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public StackByItemDataSource(final AppModelStore store) {
		super(store);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		logger.info(">> StackByItemDataSource.createHierarchy");
		// Clear the current list of elements.
		_root.clear();

		// Get the list of Locations for this Pilot.
		try {
			AssetsManager manager = _store.getPilot().getAssetsManager();
			ArrayList<NeoComAsset> assets = manager.stacks4Item(item);
			for (NeoComAsset as : assets) {
				AssetPart part = (AssetPart) new AssetPart(as)
						.setRenderMode(AppWideConstants.fragment.FRAGMENT_ITEMMODULESTACKS);
				_root.add(part);
			}
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		logger.info("<< StackByItemDataSource.createHierarchy [" + _root.size() + "]");
	}

	public StackByItemDataSource setItem(final EveItem item) {
		this.item = item;
		return this;
	}
}

// - UNUSED CODE ............................................................................................
