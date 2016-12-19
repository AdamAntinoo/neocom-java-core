//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import java.util.logging.Logger;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.CVariant;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.ShipsDataSource;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.ShipPartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipsFragment extends AbstractNewPagerFragment {
	public enum EShipsFragmentVariants {
		SHIPS_BYLOCATION, SHIPS_BYCLASS
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("ShipsFragment");
	static {
		CVariant.register(EShipsFragmentVariants.SHIPS_BYLOCATION.hashCode(),
				EShipsFragmentVariants.SHIPS_BYLOCATION.name());
		CVariant.register(EShipsFragmentVariants.SHIPS_BYCLASS.hashCode(), EShipsFragmentVariants.SHIPS_BYCLASS.name());
	}
	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	@Override
	public String getSubtitle() {
		String st = "";
		if (this.getVariant() == EShipsFragmentVariants.SHIPS_BYLOCATION.name()) st = "Ships - by Location";
		if (this.getVariant() == EShipsFragmentVariants.SHIPS_BYCLASS.name()) st = "Ships - by Class";
		return st;
	}

	@Override
	public String getTitle() {
		return this.getPilotName();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void onStart() {
		ShipsFragment.logger.info(">> [ShipsFragment.onStart]");
		try {
			//		this.setIdentifier(_variant.hashCode());
			this.registerDataSource();
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ShipsFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> ShipsFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		ShipsFragment.logger.info("<< [ShipsFragment.onStart]");
	}

	private void registerDataSource() {
		ShipsFragment.logger.info(">> [ShipsFragment.registerDataSource]");
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(this.getPilotName())
				.addIdentifier(this.getVariant());
		SpecialDataSource ds = new ShipsDataSource(locator, new ShipPartFactory(this.getVariant()));
		ds.setVariant(this.getVariant());
		ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), this.getPilot().getCharacterID());
		this.setDataSource(AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds));
		ShipsFragment.logger.info("<< [ShipsFragment.registerDataSource]");
	}
}

// - UNUSED CODE ............................................................................................
