//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import org.dimensinfin.android.mvc.core.AbstractCorePart;
import org.dimensinfin.android.mvc.core.IEditPart;
import org.dimensinfin.android.mvc.core.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants.EFragment;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.PilotListDataSource;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.PartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.model.APIKey;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.part.APIKeyPart;
import org.dimensinfin.evedroid.part.PilotInfoPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotListFragment extends AbstractNewPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> PilotListFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			setIdentifier(_variant.hashCode());
			registerDataSource();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> PilotListFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> PilotListFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< PilotListFragment.onCreateView");
		return theView;
	}

	@Override
	public String getTitle() {
		return "Select Capsuleer";
	}

	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> PilotListFragment.onStart");
		try {
			// Check the datasource status and create a new one if still does not exists.
			if (checkDSState()) {
				registerDataSource();
			}
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> PilotListFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> PilotListFragment.onCreateView - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< PilotListFragment.onStart");
	}

	private void registerDataSource() {
		Log.i("NEOCOM", ">> FittingFragment.registerDataSource");
		// final long capsuleerid =
		// getExtras().getLong(AppWideConstants.EExtras.CAPSULEERID.name());
		// final long fittingid =
		// getExtras().getLong(AppWideConstants.EExtras.FITTINGID.name());
		// int capsuleerid = 100;
		// String fittingid = "Purifier";
		// Search for the datasource at the datasource manager.
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(_variant.name());
		// Register the datasource. If this same datasource is already at the
		// manager we get it instead creating a new one.
		SpecialDataSource ds = new PilotListDataSource(locator, new PilotListPartFactory(_variant));
		ds.setVariant(_variant);
		// ds.setExtras(getExtras();
		// ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(),
		// capsuleerid);
		// ds.addParameter(AppWideConstants.EExtras.FITTINGID.name(),
		// fittingid);
		ds = (SpecialDataSource) EVEDroidApp.getAppStore().getDataSourceConector().registerDataSource(ds);
		setDataSource(ds);
	}
}

// - CLASS IMPLEMENTATION ...................................................................................
final class PilotListPartFactory extends PartFactory implements IPartFactory {
	// - S T A T I C - S E C T I O N ..........................................................................
	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotListPartFactory(final EFragment _variant) {
		super(_variant);
	}

	//- M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The method should create the matching part for the model received but there is no other place where we
	 * should create the next levels of the hierarchy. So we will create the part trasnformationes here.
	 */
	@Override
	public IEditPart createPart(final IGEFNode node) {
		if (node instanceof APIKey) {
			AbstractCorePart part = new APIKeyPart((AbstractComplexNode) node).setFactory(this);
			return part;
		}
		if (node instanceof EveChar) {
			AbstractCorePart part = new PilotInfoPart((AbstractComplexNode) node).setFactory(this);
			return part;
		}
		return null;
	}
}

// - UNUSED CODE ............................................................................................
