//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.fragment;

import java.util.HashMap;
//- IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.interfaces.IPartFactory;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.eveonline.neocom.activity.FittingListActivity.EFittingVariants;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.datasource.DataSourceLocator;
import org.dimensinfin.eveonline.neocom.datasource.SpecialDataSource;
import org.dimensinfin.eveonline.neocom.factory.FittingPartFactory;
import org.dimensinfin.eveonline.neocom.fragment.core.AbstractNewPagerFragment;
import org.dimensinfin.eveonline.neocom.interfaces.IPagerFragment;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.model.Separator.ESeparatorType;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

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

	@Override
	public void createFactory() {
		this.setFactory(new FittingPartFactory(this.getVariant()));
	}

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
	@Override
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
			SpecialDataSource ds = new FittingListDataSource(locator, this.getFactory());
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

//final class ExpandableGroup extends Separator {
//	private static final long serialVersionUID = 1642092995030622668L;
//
//	public ExpandableGroup(final String title) {
//		super(title);
//		// TODO Auto-generated constructor stub
//	}
//}

//- CLASS IMPLEMENTATION ...................................................................................
final class FittingListDataSource extends SpecialDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID	= 7810087592108417570L;
	private static Logger											logger						= Logger.getLogger("FittingDataSource");

	// - F I E L D - S E C T I O N ............................................................................
	private final Fitting											fit								= null;
	private final HashMap<String, Separator>	groups						= new HashMap<String, Separator>();
	private final Separator										defaultGroup			= new Separator("-UNDEFINED-HULL-");

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
				Separator targetGroup = groups.get(fit.getHull().getGroupName());
				if (null == targetGroup) {
					defaultGroup.addChild(fit);
				} else {
					targetGroup.addChild(fit);
				}
			}
			// Link the non empty hull groups into the Data model root.
			for (Separator group : groups.values())
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
		groups.put("Assault Frigate", new Separator("Assault Frigate").setType(ESeparatorType.SHIPTYPE_ASSAULTFRIGATE));
		groups.put("Attack Battlecruiser", new Separator("Attack Battlecruiser"));
		groups.put("Battleship", new Separator("Battleship"));
		groups.put("Black Ops", new Separator("Black Ops"));
		groups.put("Blockade Runner", new Separator("Blockade Runner"));
		groups.put("Capital Industrial Ship", new Separator("Capital Industrial Ship"));
		groups.put("Capsule", new Separator("Capsule"));
		groups.put("Carrier", new Separator("Carrier"));
		groups.put("Combat Battlecruiser", new Separator("Combat Battlecruiser"));
		groups.put("Combat Recon Ship", new Separator("Combat Recon Ship"));
		groups.put("Command Destroyer", new Separator("Command Destroyer"));
		groups.put("Command Ship", new Separator("Command Ship"));
		groups.put("Covert Ops", new Separator("Covert Ops"));
		groups.put("Cruiser", new Separator("Cruiser"));
		groups.put("Deep Space Transport", new Separator("Deep Space Transport"));
		groups.put("Destroyer", new Separator("Destroyer"));
		groups.put("Dreadnought", new Separator("Dreadnought"));
		groups.put("Electronic Attack Ship", new Separator("Electronic Attack Ship"));
		groups.put("Elite Battleship", new Separator("Elite Battleship"));
		groups.put("Exhumer", new Separator("Exhumer"));
		groups.put("Expedition Frigate", new Separator("Expedition Frigate"));
		groups.put("Force Auxiliary", new Separator("Force Auxiliary"));
		groups.put("Force Recon Ship", new Separator("Force Recon Ship"));
		groups.put("Freighter", new Separator("Freighter"));
		groups.put("Frigate", new Separator("Frigate"));
		groups.put("Heavy Assault Cruiser", new Separator("Heavy Assault Cruiser"));
		groups.put("Heavy Interdiction Cruiser", new Separator("Heavy Interdiction Cruiser"));
		groups.put("Industrial", new Separator("Industrial"));
		groups.put("Industrial Command Ship", new Separator("Industrial Command Ship"));
		groups.put("Interceptor", new Separator("Interceptor"));
		groups.put("Interdictor", new Separator("Interdictor"));
		groups.put("Jump Freighter", new Separator("Jump Freighter"));
		groups.put("Logistics", new Separator("Logistics"));
		groups.put("Logistics Frigate", new Separator("Logistics Frigate"));
		groups.put("Marauder", new Separator("Marauder"));
		groups.put("Mining Barge", new Separator("Mining Barge"));
		groups.put("Prototype Exploration Ship", new Separator("Prototype Exploration Ship"));
		groups.put("Rookie ship", new Separator("Rookie ship"));
		groups.put("Shuttle", new Separator("Shuttle"));
		groups.put("Stealth Bomber", new Separator("Stealth Bomber"));
		groups.put("Strategic Cruiser", new Separator("Strategic Cruiser"));
		groups.put("Supercarrier", new Separator("Supercarrier"));
		groups.put("Tactical Destroyer", new Separator("Tactical Destroyer"));
		groups.put("Titan", new Separator("Titan"));
	}
}
// - UNUSED CODE ............................................................................................
