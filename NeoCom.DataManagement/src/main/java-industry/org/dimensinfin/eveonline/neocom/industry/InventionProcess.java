//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.industry;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.EMarketSide;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.market.MarketDataSet;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.model.Skill;
import org.dimensinfin.eveonline.neocom.industry.AbstractManufactureProcess;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.interfaces.IJobProcess;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class InventionProcess extends AbstractManufactureProcess implements IJobProcess {

	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long serialVersionUID = -1284879453130050090L;
	private final ArrayList<Resource> datacores = new ArrayList<Resource>();
	/** New and locally used AssetsManager used to process the job requests. */
	private final AssetsManager manager = null;
	private int maxRuns = -2;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public InventionProcess(final AssetsManager manager) {
		super(manager);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * This method starts with a blueprint and generates the corresponding list
	 * of actions to be executed to have all the resources to launch and
	 * complete the job. This depends on the global generation settings because
	 * the resources get exhausted by each of the jobs and that should be
	 * reflected on the new action for next jobs.<br>
	 * It uses a new <code>AssetsManager</code> because the resource processing
	 * changes some of the resources used during the process. With a new manager
	 * we avoid clearing the currently cached information on the Pilot assets.
	 * 
	 * @return
	 */
	public ArrayList<Action> generateActions4Blueprint() {
		Log.i("ManufactureProcess", ">> T2ManufactureProcess.generateActions4Blueprint.");
		// To avoid changing the current cached assets, for this destructive
		// action get a new Manager.
		// manager = new AssetsManager(getPilot());

		// Initialize global structures.
		manufactureLocation = blueprint.getLocation();
		region = manufactureLocation.getRegion();
		actions4Item = pilot.getActions();
		// Clear structures to be sure we have the right data.
		requirements.clear();
		actionsRegistered.clear();
		// Get the resources needed for the completion of this job.
		runs = blueprint.getRuns();
		threads = 1;
		// Copy the LOM received to not modify the original data during the job
		// processing.
		for (Resource r : getLOM()) {
			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		}

		// requirements.addAll(getLOM());
		for (Resource resource : requirements) {
			// Skills are treated differently.
			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				resource.setAdaptiveStackSize(1);
				resource.setStackSize(1);
			} else {
				resource.setAdaptiveStackSize(runs);
				resource.setStackSize(resource.getStackSize() * threads);
			}
		}
		// Resource list completed. Dump report to the log and start action
		// processing.
		Log.i("EVEI", "-- T2ManufactureProcess.generateActions4Blueprint.List of requirements" + requirements);
		pointer = -1;
		try {
			do {
				pointer++;
				Resource resource = requirements.get(pointer);
				Log.i("T2ManufactureTaskGenerator", "-- Processing resource " + resource);
				// Check resources that are Skills. Give them an special
				// treatment.
				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
					currentAction = new Skill(resource);
					registerAction(currentAction);
					continue;
				}
				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				}
				currentAction = new Action(resource);
				EveTask newTask = new EveTask(ETaskType.REQUEST, resource);
				newTask.setQty(resource.getQuantity());
				// We register the action before to get erased on restarts.
				// This has no impact on data since we use pointers to the
				// global structures.
				registerAction(currentAction);
				processRequest(newTask);

			} while (pointer < (requirements.size() - 1));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getActions();
	}

	/**
	 * Gets from the eve database the manufacture duration for this module and
	 * applies the hardcoded skills that are required to perform perfect
	 * manufacture. On next releases maybe the skills are read and used to
	 * adjust this calculation.
	 * 
	 * @return the time in seconds to manufacture a copy if this module
	 */
	public int getCycleDuration() {
		int basetime = AppConnector.getDBConnector().searchJobExecutionTime(bpid,
				ModelWideConstants.activities.INVENTION);
		// Adjust to the time with new industry equations.
		double time = basetime;
		return Double.valueOf(time).intValue();
	}

	public double getJobCost() {
		if (cost < 0.0) {
			cost = calculateInventionCost();
		}
		return cost;
	}

	public ArrayList<Resource> getLOM() {
		if (null == lom) {
			lom = AppConnector.getDBConnector().searchListOfDatacores(bpid);
		}
		if (lom.size() == 0) {
			lom = AppConnector.getDBConnector().searchListOfDatacores(bpid);
		}
		return lom;
	}

	/**
	 * Calculates the minimum number of blueprints that can be manufactured with
	 * the resources stored at the blueprint location. It is supposed that the
	 * IndustryMananger has subtracted the pending jobs resources from those at
	 * the blueprint location prior to the calculations.<br>
	 * If this gets run after the generate action than the result may differ
	 * from the reality because some of the resources would have been consumed
	 * by the processing.
	 * 
	 * @param itemasset
	 * @return
	 */
	@Override
	public int getManufacturableCount() {
		if (!blueprint.isPrototype()) {
			// Check of this process was already run.
			if (maxRuns < -1) {
				EveLocation location = blueprint.getLocation();
				maxRuns = 999999;
				ArrayList<Resource> resourceList = getLOM();
				for (Resource resource : resourceList) {
					// Remove blueprints from the list of assets.
					if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
						continue;
					}
					if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
						continue;
					}
					// Get the corresponding resource quantity from the
					// location.
					ArrayList<Asset> available = getAsset4Type(resource.getTypeID());
					Log.i("EVEI", "-- InventionProcess.getManufacturableCount - available:" + available);
					int resourceCount = 0;
					for (Asset asset : available)
						if (asset.getLocationID() == location.getID()) {
							resourceCount += asset.getQuantity();
						}
					Log.i("EVEI", "-- InventionProcess.getManufacturableCount - resource count " + resource + " ["
							+ resourceCount + "]");
					int range = resourceCount / resource.getQuantity();
					if (range < maxRuns) {
						maxRuns = range;
					}
				}
			}
		} else {
			maxRuns = 0;
		}
		return maxRuns;
	}

	public double getMultiplier() {
		double topBuyerPrice = AppConnector.getDBConnector().searchItembyID(moduleid).getHighestBuyerPrice().getPrice();
		return topBuyerPrice / getJobCost();
	}

	/**
	 * Get the ID of the item produced by the job Invention applied to the
	 * referenced blueprint.
	 */
	@Override
	public int getProductID() {
		return AppConnector.getDBConnector().searchInventionProduct(blueprint.getTypeID());
	}

	public int getProfitIndex() {
		if (index < 0) {
			calculateIndex();
		}
		return index;
	}

	public int getRuns() {
		return runs;
	}

	public String getSubtitle() {
		return "T2 Invention - Resources";
	}

	public int getThreads() {
		return threads;
	}

	public void setBlueprint(final Blueprint blueprint) {
		this.blueprint = blueprint;
		bpid = blueprint.getTypeID();
		moduleid = blueprint.getModuleTypeID();
		// Do only if the blueprint is real.
		if (!blueprint.isPrototype()) {
			calculateMaxRuns();
		}
	}

	public void setRuns(final int runs) {
		this.runs = runs;
	}

	public void setThreads(final int threads) {
		this.threads = threads;
	}

	/**
	 * The review for this formulae is the benefit by 24 hours in millions.<br>
	 * manufactureTime in seconds * 10 = time to build a set<br>
	 * 24 hours / time = set per day<br>
	 * benefit * sets = index<br>
	 */
	private void calculateIndex() {
		double manufactureTime = getCycleDuration() * 10.0;
		double sets = Math.round((24 * 60 * 60) / manufactureTime);
		double sellPrice = AppConnector.getDBConnector().searchItembyID(moduleid).getHighestBuyerPrice().getPrice();
		double cost = getJobCost();
		if (sellPrice < 0) {
			index = 0;
		} else {
			int profit = Double.valueOf((sellPrice - getJobCost()) / 10000.0).intValue();
			if (profit > 0) {
				index = Double.valueOf(Math.floor(profit * sets)).intValue();
			} else {
				index = 0;
			}
		}
	}

	private double calculateInventionCost() {
		double inventionCost = 0.0;
		for (Resource resource : getLOM()) {
			// Drop from the calculation the Data Interfaces
			if (resource.getItem().getGroupName().equalsIgnoreCase("Data Interfaces")) {
				continue;
			}
			// Calculate resource quantity applying invention formulas.
			int newQty = Double.valueOf(Math.ceil(Double.valueOf(resource.getBaseQuantity() / 0.4))).intValue();
			resource.setQuantity(newQty);
			resource.setStackSize(1);
			// Calculate the cost.
			MarketDataSet data = AppConnector.getDBConnector().searchMarketData(resource.item.getItemID(),
					EMarketSide.SELLER);
			double resourcePrice = data.getBestMarket().getPrice();
			double realcost = resource.getQuantity() * resourcePrice;
			inventionCost += realcost;
		}
		return inventionCost / 10.0;
	}

	private void calculateMaxRuns() {
		getManufacturableCount();
	}

	private double calculateSkillModifier() {
		// int industry =
		// getPilot().getSkillLevel(ModelWideConstants.eveglobal.skills.INDUSTRY);
		// int advancedIndustry =
		// getPilot().getSkillLevel(ModelWideConstants.eveglobal.skills.ADVANCEDINDUSTRY);
		int industry = 5;
		int advancedIndustry = 5;
		double skillMod = (1.0 - (0.01 * 4 * industry)) * (1.0 - (0.01 * 3 * advancedIndustry));
		return skillMod;
	}
}

// - UNUSED CODE
// ............................................................................................
