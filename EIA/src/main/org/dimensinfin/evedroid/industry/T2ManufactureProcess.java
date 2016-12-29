//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.industry;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.enums.ETaskType;
import org.dimensinfin.eveonline.neocom.industry.EJobClasses;
import org.dimensinfin.eveonline.neocom.industry.IJobProcess;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.Action;
import org.dimensinfin.eveonline.neocom.model.EveTask;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.Skill;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class is the specialilzation for T2 manufacturing. Even most of the code is on the parent abstract
 * class the special codification required to manage T2 jobs is included inside this class. The key method is
 * <code>generateActions4Blueprint</code> that will create the model data to represent at the UI the
 * requirements for this manufacture job.
 * 
 * @author Adam Antinoo
 */
public class T2ManufactureProcess extends AbstractManufactureProcess implements IJobProcess {

	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long serialVersionUID = -1284879453130050090L;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public T2ManufactureProcess(final AssetsManager manager) {
		super(manager);
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	/**
	 * This method starts with a blueprint and generates the corresponding list of actions to be executed to
	 * have all the resources to launch and complete the job. This depends on the global generation settings
	 * because the resources get exhausted by each of the jobs and that should be reflected on the new action
	 * for next jobs.<br>
	 * It uses a new <code>AssetsManager</code> because the resource processing changes some of the resources
	 * used during the process. With a new manager we avoid clearing the currently cached information on the
	 * Pilot assets. <br>
	 * It also copies the LOM because the references Resources have to be modified to reflect the run counts.
	 * 
	 * @return
	 */
	public ArrayList<Action> generateActions4Blueprint() {
		Log.i("EVEI", ">> T2ManufactureProcess.generateActions4Blueprint.");
		// Initialize global structures.
		manufactureLocation = blueprint.getLocation();
		region = manufactureLocation.getRegion();
		actions4Item = pilot.getActions();
		// Clear structures to be sure we have the right data.
		requirements.clear();
		actionsRegistered.clear();
		// Get the resources needed for the completion of this job.
		runs = blueprint.getRuns();
		threads = blueprint.getQuantity();
		// Copy the LOM received to not modify the original data during the job
		// processing.
		for (Resource r : getLOM()) {
			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		}
		// Update the resource count depending on the sizing requirements for the job.
		for (Resource resource : requirements) {
			// Skills are treated differently.
			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				resource.setStackSize(1);
			} else {
				resource.setAdaptiveStackSize(runs * threads);
			}
			// If the resource being processed is the job blueprint reduce the
			// number of runs and set the counter.
			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
				resource.setStackSize(threads);
			}
		}
		// Resource list completed. Dump report to the log and start action processing.
		Log.i("EVEI", "-- T2ManufactureProcess.generateActions4Blueprint.List of requirements" + requirements);
		pointer = -1;
		try {
			do {
				pointer++;
				Resource resource = requirements.get(pointer);
				Log.i("EVEI", "-- T2ManufactureProcess.generateActions4Blueprint.Processing resource " + resource);
				// Check resources that are Skills. Give them an special
				// treatment.
				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
					currentAction = new Skill(resource);
					registerAction(currentAction);
					continue;
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
		} catch (RuntimeException rtex) {
			Log.e("RTEXCEPTION.CODE",
					"RT> T2ManufactureProcess.generateActions4Blueprint - Unexpected code behaviour. See stacktrace.");
			rtex.printStackTrace();
		}
		Log.i("EVEI", "<< T2ManufactureProcess.generateActions4Blueprint.");
		return getActions();
	}

	/**
	 * Gets from the eve database the manufacture duration for this module and applies the hardcoded skills that
	 * are required to perform perfect manufacture. On next releases maybe the skills are read and used to
	 * adjust this calculation.
	 * 
	 * @return the time in seconds to manufacture a copy if this module
	 */
	public int getCycleDuration() {
		int basetime = AppConnector.getDBConnector().searchJobExecutionTime(bpid,
				ModelWideConstants.activities.MANUFACTURING);
		// Adjust to the time with new industry equations.
		double time = basetime * ((100.0 - blueprint.getTimeEfficiency()) / 100.0) * calculateSkillModifier() * 1;
		return Long.valueOf(Math.round(time)).intValue();
	}

	public double getJobCost() {
		if (cost < 0.0) {
			cost = calculateCost();
		}
		return cost;
	}

	public ArrayList<Resource> getLOM() {
		if (null == lom) {
			lom = AppConnector.getDBConnector().searchListOfMaterials(bpid);
		}
		if (lom.size() == 0) {
			lom = AppConnector.getDBConnector().searchListOfMaterials(bpid);
		}
		return lom;
	}

	// public MarketDataEntry getMarketData() {
	// return getDataBuyer().getBestMarket();
	// }

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

	@Override
	public int getRuns() {
		return runs;
	}

	// public double getSellPrice() {
	// return getDataBuyer().getBestMarket().getPrice();
	// }

	public String getSubtitle() {
		return "T2 Manufacture - Resources";
	}

	public int getThreads() {
		return threads;
	}

	public void setBlueprint(final NeoComBlueprint blueprint) {
		this.blueprint = blueprint;
		bpid = blueprint.getTypeID();
		moduleid = blueprint.getModuleTypeID();
	}

	@Override
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

	/**
	 * Gets the cost of each of the components and aggregates the result to obtain the real cost.<br>
	 * The calculations have to account for the extra cost of the invention of the blueprint. being this a T2
	 * blueprint job we have to spend some resources (mainly expensive datacores) in the invention of the
	 * blueprint. Those costs belong to the Invention Process and should be calculated there.<br>
	 * 
	 * The process skips the cost of the Skill books and other elements that are considered core manufactures
	 * costs, like the more volatile job launch costs that are on the handling costs.<br>
	 * 
	 * The invention costs are obtained from the T1 module that is required to obtain a T2 similar module. When
	 * scanning the resources if we found a T1 module then we can process the invention. Apart from Modules,
	 * there can be ships and charges that also are obtained in the same way.
	 * 
	 * @return the cost of the manufacture of a single output resource.
	 */
	private double calculateCost() {
		double manufactureCost = 0.0;
		StringBuffer resourceIDs = new StringBuffer(Integer.valueOf(bpid).toString());
		for (Resource resource : getLOM()) {
			// Remove blueprints and skills from budget.
			if (resource.item.getCategory().equalsIgnoreCase("Blueprint")) {
				continue;
			}
			if (resource.item.getCategory().equalsIgnoreCase("Skill")) {
				continue;
			}
			// Detect the invention result product to identify its blueprint.
			resourceIDs.append(",").append(Integer.valueOf(resource.getTypeID()).toString());
			// If resources are minerals then apply other prices.
			double resourcePrice = 0;
			if (resource.item.getGroupName().equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
				resourcePrice = resource.getItem().getHighestBuyerPrice().getPrice();
			} else {
				resourcePrice = resource.getItem().getLowestSellerPrice().getPrice();
			}
			double realcost = resource.getQuantity() * resourcePrice;
			manufactureCost += realcost;
		}
		// Search for the inventionable elements and get their invention costs.
		manufactureCost += calculateInventionCosts(resourceIDs.toString());
		return manufactureCost;
	}

	/**
	 * The review for this formulae is the benefit by 24 hours in millions.<br>
	 * manufactureTime in seconds * 10 = time to build a set<br>
	 * 24 hours / time = set per day<br>
	 * benefit * sets = index<br>
	 * Chnage. The time to manufacture an item is no more an issue so I will remove it from the calculations.
	 */
	private void calculateIndex() {
		// double manufactureTime = getCycleDuration() * 10.0;
		// double sets = Math.round((24 * 60 * 60) / manufactureTime);
		double sets = 1.0;
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

	/**
	 * Searches on the database for blueprints that generate one or more of the resource required to
	 * manufacture. The id found is the id of the blueprints whose Invention process has to be accounted.
	 * 
	 * @param resourceIDs
	 */
	private double calculateInventionCosts(final String resourceIDs) {
		double inventionCost = 0.0;
		// Query the database the ID of the blueprints required for the
		// invention of the parts.
		ArrayList<Integer> ids = AppConnector.getDBConnector().searchInventionableBlueprints(resourceIDs);
		for (Integer id : ids) {
			// Get access to the Invention process.
			IJobProcess invention = JobManager.generateJobProcess(getPilot(), new NeoComBlueprint(id), EJobClasses.INVENTION);
			inventionCost += invention.getJobCost();
		}
		return inventionCost;
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
