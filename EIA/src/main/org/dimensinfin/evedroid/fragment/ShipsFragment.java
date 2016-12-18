//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.ShipsDataSource;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.enums.EVARIANT;
import org.dimensinfin.evedroid.factory.ShipPartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipsFragment extends AbstractNewPagerFragment {

	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	@Override
	public String getSubtitle() {
		String st = "";
		if (this.getVariant() == EVARIANT.SHIPS_BYLOCATION) st = "Ships - by Location";
		if (this.getVariant() == EVARIANT.SHIPS_BYCLASS) st = "Ships - by Class";
		return st;
	}

	@Override
	public String getTitle() {
		return this.getPilotName();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	//	/**
	//	 * Creates the structures when the fragment is about to be shown. It will inflate the layout where the
	//	 * generic fragment will be layered to show the content. It will get the Activity functionality for single
	//	 * page activities.
	//	 */
	//	@Override
	//	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	//		Log.i("NEOCOM", ">> ShipsFragment.onCreateView");
	//		final View theView = super.onCreateView(inflater, container, savedInstanceState);
	//		try {
	//			this.setIdentifier(_variant.hashCode());
	//		} catch (final RuntimeException rtex) {
	//			Log.e("NEOCOM", "RTEX> ShipsFragment.onCreateView - " + rtex.getMessage());
	//			rtex.printStackTrace();
	//			this.stopActivity(new RuntimeException("RTEX> ShipsFragment.onCreateView - " + rtex.getMessage()));
	//		}
	//		Log.i("NEOCOM", "<< ShipsFragment.onCreateView");
	//		return theView;
	//	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> [ShipsFragment.onStart]");
		try {
			//		this.setIdentifier(_variant.hashCode());
			this.registerDataSource();
		} catch (final RuntimeException rtex) {
			Log.e("NEOCOM", "RTEX> ShipsFragment.onStart - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> ShipsFragment.onStart - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< [ShipsFragment.onStart]");
	}

	private void registerDataSource() {
		Log.i("NEOCOM", ">> [ShipsFragment.registerDataSource]");
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(this.getPilotName())
				.addIdentifier(_variant.name());
		SpecialDataSource ds = new ShipsDataSource(locator, new ShipPartFactory(_variant));
		ds.setVariant(_variant);
		ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), this.getPilot().getCharacterID());
		this.setDataSource(AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds));
		Log.i("NEOCOM", "<< [ShipsFragment.registerDataSource]");
	}
}

// - UNUSED CODE ............................................................................................
