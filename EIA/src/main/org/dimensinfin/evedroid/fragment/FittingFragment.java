//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.FittingDataSource;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.FittingPartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.interfaces.IExtendedDataSource;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This is a test implementation that will run a testing configuration. The sources for fittings maybe already
 * fitted ships or XML fitting configuration files but there is no code now to import from such sources. <br>
 * <br>
 * Fragment implementation that will get some input form the user to select a fitting and a count of copies to
 * calculate the item requirements to cover that request. By default fittings are matched against the GARAGE
 * function Location. The GARAGE function may not be unique. If that case the matching should be against each
 * of the GARAGE locations.
 * 
 * @author Adam Antinoo
 */
public class FittingFragment extends AbstractNewPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger	= Logger.getLogger("FittingFragment");

	// - F I E L D - S E C T I O N ............................................................................
	private FittingPartFactory	factory	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public String getTitle() {
		return "Fitting - Under Test";
	}

	/**
	 * This code is identical on all Fragment implementations so can be moved to the super class.
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.i("NEOCOM", ">> FittingFragment.onCreateView");
		final View theView = super.onCreateView(inflater, container, savedInstanceState);
		try {
			//			setIdentifier(_variant.hashCode());
			this.registerDataSource();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> FittingFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> FittingFragment.onCreateView - " + rtex.getMessage()));
		}
		Log.i("NEOCOM", "<< FittingFragment.onCreateView");
		return theView;
	}

	/**
	 * This code is identical on all Fragment implementations so can be moved to the super class.
	 */
	@Override
	public void onStart() {
		Log.i("NEOCOM", ">> FittingFragment.onStart");
		try {
			this.registerDataSource();
			// This fragment has a header. Populate it with the datasource header contents.
			this.setHeaderContents();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> FittingFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> FittingFragment.onCreateView - " + rtex.getMessage()));
		}
		super.onStart();
		Log.i("NEOCOM", "<< FittingFragment.onStart");
	}

	private AbstractAndroidPart createPart(final AbstractComplexNode model) {
		IPartFactory factory = this.getFactory();
		IPart part = factory.createPart(model);
		part.setParent(null);
		return (AbstractAndroidPart) part;
	}

	private IExtendedDataSource getDataSource() {
		return _datasource;
	}

	private IPartFactory getFactory() {
		if (null == factory) factory = new FittingPartFactory(this.getVariant());
		return factory;
	}

	/**
	 * This is the single piece f code specific for this fragment. It should create the right class DataSource
	 * and connect it to the Fragment for their initialization during the <b>start</b> phase. <br>
	 * Current implementation is a test code to initialize the DataSorue with a predefined and testing fitting.
	 */
	private void registerDataSource() {
		Log.i("NEOCOM", ">> FittingFragment.registerDataSource");
		Bundle extras = this.getExtras();
		long capsuleerid = 0;
		String fittingLabel = "Purifier";
		if (null != extras) {
			capsuleerid = extras.getLong(AppWideConstants.EExtras.CAPSULEERID.name());
			fittingLabel = extras.getString(AppWideConstants.EExtras.FITTINGID.name());
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(this.getVariant()).addIdentifier(capsuleerid)
				.addIdentifier(fittingLabel);
		// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
		SpecialDataSource ds = new FittingDataSource(locator, new FittingPartFactory(this.getVariant()));
		ds.setVariant(this.getVariant());
		ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), this.getPilot().getCharacterID());
		ds.addParameter(AppWideConstants.EExtras.FITTINGID.name(), fittingLabel);
		ds = (SpecialDataSource) AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds);
		this.setDataSource(ds);
	}

	private void setHeaderContents() {
		RootNode headModel = ((FittingDataSource) this.getDataSource()).getHeaderModel();
		for (AbstractComplexNode model : headModel.collaborate2Model(this.getVariant())) {
			// Set the datasource as the listener for this parts events.
			AbstractAndroidPart pt = this.createPart(model);
			pt.addPropertyChangeListener(this.getDataSource());
			this.addtoHeader(pt);
		}
	}
}
// - UNUSED CODE ............................................................................................
