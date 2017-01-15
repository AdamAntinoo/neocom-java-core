//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.fragment;

import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.activity.AssetsDirectorActivity.EAssetVariants;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.datasource.AssetsByLocationDataSource;
import org.dimensinfin.eveonline.neocom.datasource.DataSourceLocator;
import org.dimensinfin.eveonline.neocom.datasource.SpecialDataSource;
import org.dimensinfin.eveonline.neocom.factory.AssetPartFactory;
import org.dimensinfin.eveonline.neocom.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.eveonline.neocom.interfaces.IPagerFragment;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.os.Bundle;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetsFragment extends AbstractNewPagerFragment implements IPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("AssetsFragment");

	// - F I E L D - S E C T I O N ............................................................................
	//	private int _filter = AppWideConstants.fragment.FRAGMENT_ASSETSBYLOCATION;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	@Override
	public void createFactory() {
		AssetsFragment.logger.info(">> [AssetsFragment.createFactory]");
		this.setFactory(new AssetPartFactory(this.getVariant()));
		AssetsFragment.logger.info("<< [AssetsFragment.createFactory]");
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String getSubtitle() {
		String st = "";
		if (this.getVariant() == EAssetVariants.ASSETS_BYLOCATION.name()) {
			st = "ASSETS - By Location";
		}
		if (this.getVariant() == EAssetVariants.ASSETS_MATERIALS.name()) {
			st = "ASSETS - Materials";
		}
		return st;
	}

	@Override
	public String getTitle() {
		return this.getPilotName();
	}

	@Override
	public void registerDataSource() {
		AssetsFragment.logger.info(">> [AssetsFragment.registerDataSource]");
		Bundle extras = this.getExtras();
		long capsuleerid = 0;
		if (null != extras) {
			capsuleerid = extras.getLong(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name());
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(this.getVariant()).addIdentifier(capsuleerid);
		// This part of the code may depend on the variant so surround it with the detector.
		if (this.getVariant() == EAssetVariants.ASSETS_BYLOCATION.name()) {
			// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
			SpecialDataSource ds = new AssetsByLocationDataSource(locator, this.getFactory());
			ds.setVariant(this.getVariant());
			ds.addParameter(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(), this.getPilot().getCharacterID());
			this.setDataSource(AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds));
		}
		//		if (this.getVariant() == EAssetVariants.ASSETS_MATERIALS.name()) {
		//			// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
		//			SpecialDataSource ds = new AssetsMaterialsDataSource(locator, getFactory());
		//			ds.setVariant(this.getVariant());
		//			ds.addParameter(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(), this.getPilot().getCharacterID());
		//			this.setDataSource(AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds));
		//		}
		AssetsFragment.logger.info("<< [AssetsFragment.registerDataSource]");
	}

	@Override
	public void setHeaderContents() {
		// TODO Auto-generated method stub

	}
}
// - UNUSED CODE ............................................................................................
