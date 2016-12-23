//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.fragment;

import java.util.HashMap;
//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.activity.FittingListActivity.EFittingVariants;
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
		FittingListFragment.logger.info(">> [FittingListFragment.onStart]");
		try {
			//			this.setIdentifier(_variant.hashCode());
			this.registerDataSource();
			// This fragment has a header. Populate it with the datasource header contents.
			this.setHeaderContents();
		} catch (final RuntimeException rtex) {
			FittingListFragment.logger.warning("RTEX> FittingListFragment.onCreateView - " + rtex.getMessage());
			rtex.printStackTrace();
			this.stopActivity(new RuntimeException("RTEX> FittingListFragment.onCreateView - " + rtex.getMessage()));
		}
		super.onStart();
		FittingListFragment.logger.info("<< [FittingListFragment.onStart]");
	}

	/**
	 * This is the method to create and configure the DataSource. This code is specific for each fragment and
	 * also for each fragment variant.
	 */
	public void registerDataSource() {
		FittingListFragment.logger.info(">> [FittingListFragment.registerDataSource]");
		Bundle extras = this.getExtras();
		long capsuleerid = 0;
		if (null != extras) {
			capsuleerid = extras.getLong(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name());
		}
		DataSourceLocator locator = new DataSourceLocator().addIdentifier(this.getVariant()).addIdentifier(capsuleerid);
		// This part of the code may depend on the variant so surround it with the detector.
		if (this.getVariant() == EFittingVariants.FITTING_LIST.name()) {
			// Register the datasource. If this same datasource is already at the manager we get it instead creating a new one.
			SpecialDataSource ds = new FittingListDataSource(locator, new FittingPartFactory(this.getVariant()));
			ds.setVariant(this.getVariant());
			ds.addParameter(AppWideConstants.EExtras.EXTRA_CAPSULEERID.name(), this.getPilot().getCharacterID());
			//			ds.addParameter(AppWideConstants.EExtras.FITTINGID.name(), fittingLabel);
			this.setDataSource(AppModelStore.getSingleton().getDataSourceConector().registerDataSource(ds));
		}
		FittingListFragment.logger.info("<< [FittingListFragment.registerDataSource]");
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
	private static final long												serialVersionUID	= 7810087592108417570L;
	private static Logger														logger						= Logger.getLogger("FittingDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private final Fitting														fit								= null;
	private final HashMap<String, ExpandableGroup>	groups						= new HashMap<String, ExpandableGroup>();
	private final ExpandableGroup										defaultGroup			= new ExpandableGroup("-UNDEFINED-HULL-");

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
		FittingListDataSource.logger.info(">> [FittingListDataSource.collaborate2Model]");
		try {
			AppModelStore store = AppModelStore.getSingleton();
			HashMap<String, Fitting> fitList = store.getFittings();
			// Create the list of Groups.
			_dataModelRoot = new RootNode();
			defaultGroup.clean();
			this.initGroups();
			for (Fitting fit : fitList.values()) {
				FittingListDataSource.logger.info("-- [FittingListDataSource.collaborate2Model]> Classifying fitting: " + fit);
				// Classify the fitting.
				ExpandableGroup targetGroup = groups.get(fit.getHull().getGroupName());
				if (null == targetGroup) {
					defaultGroup.addChild(fit);
				} else {
					targetGroup.addChild(fit);
				}
			}
			// Link the non empty hull groups into the Data model root.
			for (ExpandableGroup group : groups.values())
				if (group.getChildren().size() > 0) {
					_dataModelRoot.addChild(group);
				}
			// Add the default group if not empty.
			if (defaultGroup.getChildren().size() > 0) {
				_dataModelRoot.addChild(defaultGroup);
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			FittingListDataSource.logger
					.severe("RTEX> FittingListDataSource.collaborate2Model-There is a problem while generating the Data model.");
		}
		FittingListDataSource.logger.info("<< [FittingListDataSource.collaborate2Model]");
		return _dataModelRoot;
	}

	/**
	 * Returns the header root element that contains the header elements to show on the Activity.<br>
	 * For this implementation we just return the fitting that is the only element to include on the head.
	 */
	public RootNode getHeaderModel() {
		return new RootNode();
	}

	/**
	 * Creates the full list of ship types where to classify the fittings. To optimize and guarantee that the
	 * list is new every time the DataSource is used we keep the object being created on instantiation but
	 * cleared before initialization.
	 */
	private void initGroups() {
		groups.clear();
		groups.put("Assault Frigate", new ExpandableGroup("Assault Frigate"));
		groups.put("Attack Battlecruiser", new ExpandableGroup("Attack Battlecruiser"));
		groups.put("Battleship", new ExpandableGroup("Battleship"));
		groups.put("Black Ops", new ExpandableGroup("Black Ops"));
		groups.put("Blockade Runner", new ExpandableGroup("Blockade Runner"));
		groups.put("Capital Industrial Ship", new ExpandableGroup("Capital Industrial Ship"));
		groups.put("Capsule", new ExpandableGroup("Capsule"));
		groups.put("Carrier", new ExpandableGroup("Carrier"));
		groups.put("Combat Battlecruiser", new ExpandableGroup("Combat Battlecruiser"));
		groups.put("Combat Recon Ship", new ExpandableGroup("Combat Recon Ship"));
		groups.put("Command Destroyer", new ExpandableGroup("Command Destroyer"));
		groups.put("Command Ship", new ExpandableGroup("Command Ship"));
		groups.put("Covert Ops", new ExpandableGroup("Covert Ops"));
		groups.put("Cruiser", new ExpandableGroup("Cruiser"));
		groups.put("Deep Space Transport", new ExpandableGroup("Deep Space Transport"));
		groups.put("Destroyer", new ExpandableGroup("Destroyer"));
		groups.put("Dreadnought", new ExpandableGroup("Dreadnought"));
		groups.put("Electronic Attack Ship", new ExpandableGroup("Electronic Attack Ship"));
		groups.put("Elite Battleship", new ExpandableGroup("Elite Battleship"));
		groups.put("Exhumer", new ExpandableGroup("Exhumer"));
		groups.put("Expedition Frigate", new ExpandableGroup("Expedition Frigate"));
		groups.put("Force Auxiliary", new ExpandableGroup("Force Auxiliary"));
		groups.put("Force Recon Ship", new ExpandableGroup("Force Recon Ship"));
		groups.put("Freighter", new ExpandableGroup("Freighter"));
		groups.put("Frigate", new ExpandableGroup("Frigate"));
		groups.put("Heavy Assault Cruiser", new ExpandableGroup("Heavy Assault Cruiser"));
		groups.put("Heavy Interdiction Cruiser", new ExpandableGroup("Heavy Interdiction Cruiser"));
		groups.put("Industrial", new ExpandableGroup("Industrial"));
		groups.put("Industrial Command Ship", new ExpandableGroup("Industrial Command Ship"));
		groups.put("Interceptor", new ExpandableGroup("Interceptor"));
		groups.put("Interdictor", new ExpandableGroup("Interdictor"));
		groups.put("Jump Freighter", new ExpandableGroup("Jump Freighter"));
		groups.put("Logistics", new ExpandableGroup("Logistics"));
		groups.put("Logistics Frigate", new ExpandableGroup("Logistics Frigate"));
		groups.put("Marauder", new ExpandableGroup("Marauder"));
		groups.put("Mining Barge", new ExpandableGroup("Mining Barge"));
		groups.put("Prototype Exploration Ship", new ExpandableGroup("Prototype Exploration Ship"));
		groups.put("Rookie ship", new ExpandableGroup("Rookie ship"));
		groups.put("Shuttle", new ExpandableGroup("Shuttle"));
		groups.put("Stealth Bomber", new ExpandableGroup("Stealth Bomber"));
		groups.put("Strategic Cruiser", new ExpandableGroup("Strategic Cruiser"));
		groups.put("Supercarrier", new ExpandableGroup("Supercarrier"));
		groups.put("Tactical Destroyer", new ExpandableGroup("Tactical Destroyer"));
		groups.put("Titan", new ExpandableGroup("Titan"));
	}
}
// - UNUSED CODE ............................................................................................
