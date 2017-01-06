//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.factory;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.part.AssetPart;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

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

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void createContentHierarchy() {
		AbstractDataSource.logger.info(">> StackByItemDataSource.createHierarchy");
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
			AbstractDataSource.logger
					.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		AbstractDataSource.logger.info("<< StackByItemDataSource.createHierarchy [" + _root.size() + "]");
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	public StackByItemDataSource setItem(final EveItem item) {
		this.item = item;
		return this;
	}
}

// - UNUSED CODE ............................................................................................
