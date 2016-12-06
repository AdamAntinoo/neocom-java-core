//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import java.util.HashMap;
//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.RootNode;
import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.datasource.DataSourceLocator;
import org.dimensinfin.evedroid.datasource.SpecialDataSource;
import org.dimensinfin.evedroid.factory.FittingPartFactory;
import org.dimensinfin.evedroid.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.evedroid.interfaces.IPagerFragment;
import org.dimensinfin.evedroid.model.Fitting;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.os.Bundle;
import android.util.Log;

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
public class FittingListFragment extends AbstractNewPagerFragment implements IPagerFragment {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger							logger	= Logger.getLogger("FittingListFragment");

	// - F I E L D - S E C T I O N ............................................................................
	private final FittingPartFactory	factory	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public String getSubtitle() {
		return "";
	}

	@Override
	public String getTitle() {
		return "Fittings";
	}

	/**
	 * This code is identical on all Fragment implementations so can be moved to the super class.
	 */
	@Override
	public void onStart() {
		logger.info(">> [FittingListFragment.onStart]");
		try {
			setIdentifier(_variant.hashCode());
			registerDataSource();
			// This fragment has a header. Populate it with the datasource header contents.
			setHeaderContents();
		} catch (final RuntimeException rtex) {
			Log.e("EVEI", "RTEX> FittingListFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			stopActivity(new RuntimeException("RTEX> FittingListFragment.onCreateView - " + rtex.getMessage()));
		}
		super.onStart();
		logger.info("<< [FittingListFragment.onStart]");
	}

	/**
	 * This is the method to create and configure the DataSource. This code is specific for each fragment and
	 * also for each fragment variant.
	 */
	public void registerDataSource() {
		logger.info(">> [FittingListFragment.registerDataSource]");
		Bundle extras = getExtras();
		long capsuleerid = 0;
		//		String fittingLabel = "Purifier";
		if (null != extras) {
			capsuleerid = extras.getLong(AppWideConstants.extras.EXTRA_EVECHARACTERID);
			//			fittingLabel = extras.getString(AppWideConstants.EExtras.FITTINGID.name());
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(_variant.name()).addIdentifier(capsuleerid);
		// This part of the code may depend on the variant so surronud it with the detector.
		if (_variant == AppWideConstants.EFragment.FITTING_LIST) {
			// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
			SpecialDataSource ds = new FittingListDataSource(locator, new FittingPartFactory(_variant));
			ds.setVariant(_variant);
			ds.addParameter(AppWideConstants.EExtras.CAPSULEERID.name(), getPilot().getCharacterID());
			//			ds.addParameter(AppWideConstants.EExtras.FITTINGID.name(), fittingLabel);
			setDataSource(EVEDroidApp.getAppStore().getDataSourceConector().registerDataSource(ds));
		}
		logger.info("<< [FittingListFragment.registerDataSource]");
	}

	/**
	 * This Fragment has no header contents so the implementation is empty.
	 */
	public void setHeaderContents() {
	}
}

final class ExpandableGroup extends Separator {
	private static final long serialVersionUID = 1642092995030622668L;

	public ExpandableGroup(final String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}
}

//- CLASS IMPLEMENTATION ...................................................................................
final class FittingListDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= 7810087592108417570L;
	private static Logger											logger						= Logger.getLogger("FittingDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private final Fitting											fit								= null;
	private HashMap<String, ExpandableGroup>	groups						= new HashMap<String, ExpandableGroup>();
	private final ExpandableGroup							defaultGroup			= new ExpandableGroup("-UNDEFINED-HULL-");

	//	private final ArrayList<Asset>						ships							= null;

	//- C O N S T R U C T O R - S E C T I O N ................................................................
	public FittingListDataSource(final DataSourceLocator locator, final IPartFactory factory) {
		super(locator, factory);
	}

	//- M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Get the list of available fittings from the store and classify them from the Group category.
	 */
	public RootNode collaborate2Model() {
		logger.info(">> [FittingListDataSource.collaborate2Model]");
		try {
			AppModelStore store = EVEDroidApp.getAppStore();
			HashMap<String, Fitting> fitList = store.getFittings();
			// Create the list of Groups.
			_dataModelRoot = new RootNode();
			initGroups();
			for (Fitting fit : fitList.values()) {
				logger.info("-- [FittingListDataSource.collaborate2Model]> Classifying fitting: " + fit);
				// Classify the fitting.
				ExpandableGroup targetGroup = groups.get(fit.getHull().getGroupName());
				if (null == targetGroup) {
					defaultGroup.addChild(fit);
				} else {
					targetGroup.addChild(fit);
				}
			}
			// Link the non empty hull groups into the Data model root.
			for (ExpandableGroup group : groups.values()) {
				if (group.getChildren().size() > 0) {
					_dataModelRoot.addChild(group);
				}
			}
			// Add the default group if not empty.
			if (defaultGroup.getChildren().size() > 0) {
				_dataModelRoot.addChild(defaultGroup);
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			logger
					.severe("RTEX> FittingListDataSource.collaborate2Model-There is a problem while generating the Data model.");
		}
		logger.info("<< [FittingListDataSource.collaborate2Model]");
		return _dataModelRoot;
	}

	/**
	 * Returns the header root element that contains the header elements to show on the Activity.<br>
	 * For this implementation we just return the fitting that is the only element to include on the head.
	 */
	public RootNode getHeaderModel() {
		return new RootNode();
	}

	private void initGroups() {
		groups = new HashMap<String, ExpandableGroup>();
		groups.put("Interceptor", new ExpandableGroup("Interceptor"));
		groups.put("Battleship", new ExpandableGroup("Battleship"));
		groups.put("Frigate", new ExpandableGroup("Frigate"));
		groups.put("Cruiser", new ExpandableGroup("Cruiser"));
		groups.put("Covert Ops", new ExpandableGroup("Covert Ops"));
	}

}
// - UNUSED CODE ............................................................................................
