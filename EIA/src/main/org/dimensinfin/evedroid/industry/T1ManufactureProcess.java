//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.industry;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.NeoComBlueprint;
import org.dimensinfin.evedroid.model.EveTask;
import org.dimensinfin.evedroid.model.Skill;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class T1ManufactureProcess extends AbstractManufactureProcess implements IJobProcess {

	// - S T A T I C - S E C T I O N
	// ..........................................................................
	// private static final long serialVersionUID = -1284879453130050091L;
	// private static final double T1ME_LEVEL = 10.0;
	// private static final double T1PE_LEVEL = 20.0;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public T1ManufactureProcess(final AssetsManager manager) {
		super(manager);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * This method starts with a blueprint and generates the corresponding list of actions to be executed to
	 * have all the resources to launch and complete the job. This depends on the global generation settings
	 * because the resources get exhausted by each of the jobs and that should be reflected on the new action
	 * for next jobs.
	 * 
	 * @return
	 */
	public ArrayList<Action> generateActions4Blueprint() {
		Log.i("EVEI", ">> T1ManufactureProcess.generateActions4Blueprint.");
		// Initialize global structures.
		manufactureLocation = blueprint.getLocation();
		region = manufactureLocation.getRegion();
		actions4Item = pilot.getActions();
		// Clear structures to be sure we have the right data.
		requirements.clear();
		actionsRegistered.clear();
		// Get the resources needed for the completion of this job.
		runs = blueprint.getRuns();
		// If the Blueprint if for a ship then reduce the number of runs to 1.
		if (blueprint.getModuleCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Ship)) {
			runs = 1;
		}
		// For T1 blueprints limit this to a single blueprint
		threads = 1;
		// Copy the LOM received to not modify the original data during the job
		// processing.
		for (Resource r : getLOM()) {
			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		}
		for (Resource resource : requirements) {
			// Skills are treated differently.
			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				resource.setStackSize(1);
			} else {
				resource.setAdaptiveStackSize(runs);
			}
			// If the resource being processed is the job blueprint reduce the
			// number of runs and set the counter.
			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.NeoComBlueprint)) {
				resource.setStackSize(threads);
			}
		}

		// Resource list completed. Dump report to the log and start action
		// processing.
		Log.i("EVEI", "-- T1ManufactureProcess.generateActions4Blueprint.List of requirements" + requirements);
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
				} else {
					currentAction = new Action(resource);
					EveTask newTask = new EveTask(ETaskType.REQUEST, resource);
					newTask.setQty(resource.getQuantity());
					// We register the action before to get erased on restarts.
					// This has no impact on data since we use pointers to the
					// global structures.
					registerAction(currentAction);
					processRequest(newTask);
				}
			} while (pointer < (requirements.size() - 1));
		} catch (Exception ex) {
			Log.e("RTEXCEPTION.CODE",
					"RT> T1ManufactureProcess.generateActions4Blueprint - Unexpected code behaviour. See stacktrace.");
			ex.printStackTrace();
		}
		return getActions();
	}

	public int getCycleDuration() {
		double duration = ModelWideConstants.HOURS24;
		int time = AppConnector.getDBConnector().searchJobExecutionTime(bpid, ModelWideConstants.activities.MANUFACTURING);
		// Adjust to the time with new industry equations.
		double basetime = Math.round((time * (100.0 - blueprint.getTimeEfficiency())) / 100.0);
		return Double.valueOf(basetime).intValue();
	}

	public double getJobCost() {
		if (cost < 0.0) {
			cost = calculateCost();
		}
		return cost;
	}

	public ArrayList<Resource> getLOM() {
		if (null == lom) {
			lom = adjustRequired(AppConnector.getDBConnector().searchListOfMaterials(bpid));
		}
		if (lom.size() == 0) {
			lom = adjustRequired(AppConnector.getDBConnector().searchListOfMaterials(bpid));
		}
		return lom;
	}

	/**
	 * The multiplier is the number of times the market buyers will pay related to the cost of the item. The
	 * buyers top paying price will be divided by the manufacture cost price.
	 */
	public double getMultiplier() {
		double topBuyerPrice = AppConnector.getDBConnector().searchItembyID(moduleid).getHighestBuyerPrice().getPrice();
		return topBuyerPrice / getJobCost();
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
		return "T1 Manufacture - Resources";
	}

	public int getThreads() {
		return threads;
	}

	public void setBlueprint(final NeoComBlueprint blueprint) {
		this.blueprint = blueprint;
		bpid = blueprint.getTypeID();
		moduleid = blueprint.getModuleTypeID();
	}

	public void setRuns(final int runs) {
		this.runs = runs;
	}

	public void setThreads(final int threads) {
		this.threads = threads;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("T1ManufactureProcess [");
		buffer.append(super.toString());
		buffer.append(" ]");
		return buffer.toString();
	}

	private ArrayList<Resource> adjustRequired(final ArrayList<Resource> listofmaterials) {
		for (Resource resource : listofmaterials) {
			// Calculate the real amount of the resource depending on the ME of
			// the blueprint.
			double materialModifier = (100.0 - blueprint.getMaterialEfficiency()) / 100;
			int adjustedQty = Double
					.valueOf(Math.max(1, Math.ceil(Math.round(1.0 * resource.getQuantity() * materialModifier)))).intValue();
			resource.setQuantity(adjustedQty);
		}
		return listofmaterials;
	}

	/**
	 * Calculates the manufacture material costs for a single run of this item.
	 * 
	 * @return
	 */
	private double calculateCost() {
		double manufactureCost = 0.0;
		for (Resource resource : getLOM()) {
			// Remove blueprints and skill books.
			if (resource.item.getCategory().equalsIgnoreCase("Blueprint")) {
				continue;
			}
			if (resource.item.getCategory().equalsIgnoreCase("Skill")) {
				continue;
			}
			// If resources are minerals then apply other prices.
			double resourcePrice = 0;
			if (resource.item.getGroupName().equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
				resourcePrice = resource.getItem().getHighestBuyerPrice().getPrice();
			} else {
				resourcePrice = resource.getItem().getLowestSellerPrice().getPrice();
			}
			resourcePrice = resource.item.getLowestSellerPrice().getPrice();
			double realcost = resource.getQuantity() * resourcePrice;
			manufactureCost += realcost;
		}
		return manufactureCost;
	}

	/**
	 * The Index has not too much sense for T1 modules because most of them have a very adjusted marginal
	 * profit. We can default the T1 indet to be ten times the multiplier or to disable it altogether.
	 */
	private void calculateIndex() {
		// double profit = ((getSellPrice() - getCost()) * runs);
		// double estimatedIndex = profit / 10000.0;
		index = Double.valueOf(getMultiplier() * 10.0).intValue();
	}
}

// - UNUSED CODE
// ............................................................................................
