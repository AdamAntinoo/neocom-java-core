//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Vector;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.core.model.IGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.activity.IndustryT2Activity;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.INamedPart;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.industry.EJobClasses;
import org.dimensinfin.evedroid.industry.IJobProcess;
import org.dimensinfin.evedroid.industry.JobManager;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.model.Action;
import org.dimensinfin.evedroid.model.Blueprint;
import org.dimensinfin.evedroid.render.Blueprint4IndustryHeaderRender;
import org.dimensinfin.evedroid.render.Blueprint4IndustryRender;
import org.dimensinfin.evedroid.render.Blueprint4T2InventionRender;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class BlueprintPart extends MarketDataPart implements INamedPart, OnClickListener, IMenuActionTarget {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long	serialVersionUID	= -274331830590300917L;

	// - F I E L D - S E C T I O N
	// ............................................................................
	/**
	 * Stores the instance to the job process responsible to perform all the action and the job calculations.
	 */
	private IJobProcess				process						= null;
	/** The default job activity is Manufacturing. */
	private int								activity					= ModelWideConstants.activities.MANUFACTURING;
	/** The number of runs that can be created with the full blueprint stack. */
	private int								runCount					= 0;
	// /** The number of runs that can be manufactures with the current
	// resources. */
	// private final int maxRunCount = -1;
	/**
	 * The number of jobs that can and need to be launched depending on the number of blueprints and the
	 * quantity of resources.
	 */
	// private int jobs = -1;
	/** Number of blueprints on stack. */
	private int								bpccount					= 0;

	/** Number of blueprints that are to be used for manufacture. */
	// private int bpcmanufacturable = -1;

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public BlueprintPart(final AbstractGEFNode node) {
		super(node);
		bpccount = getCastedModel().getQuantity();
		runCount = bpccount * getCastedModel().getRuns();
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	public ArrayList<Action> generateActions() {
		return process.generateActions4Blueprint();
	}

	public int get_assetTypeID() {
		return getCastedModel().getModuleTypeID();
	}

	/**
	 * Returns the UI show value for the number of blueprints present on the stack.
	 * 
	 * @return
	 */
	public String get_blueprintCount() {
		final int count = getCastedModel().getQuantity();
		if (count > 1)
			return qtyFormatter.format(count) + " blueprints";
		else
			return qtyFormatter.format(count) + " blueprint";
	}

	public String get_blueprintMETE() {
		return qtyFormatter.format(getCastedModel().getMaterialEfficiency()) + " / "
				+ qtyFormatter.format(getCastedModel().getTimeEfficiency());
	}

	// public String get_blueprintName() {
	// return getCastedModel().getName();
	// }

	// public String get_blueprintRuns() {
	// return "[" + qtyFormatter.format(getCastedModel().getRuns()) + "]";
	// }

	/**
	 * Show the number of manufacturable copies on the list of available blueprints. Change the number of
	 * blueprints to something similar to this text: 2 BPCs -> 2PBCs [16 copies]. If the number is 0 copies then
	 * put it on red. If the number of copies is below the max then use the orange color and if the number is
	 * equal or greater that the available blueprint runs then it should be on white.
	 * 
	 * @return
	 */
	public Spanned get_bpccounts() {
		// Get the number of total copies manufacturable to set the color.
		String pctcolor = "#FFFFFF";
		if (getMaxRuns() < runCount) {
			pctcolor = "#FFA500";
		}
		if (getMaxRuns() == 0) {
			pctcolor = "#F00000";
		}
		final StringBuffer htmlCountString = new StringBuffer();
		htmlCountString.append("<font color='").append(pctcolor).append("'>");
		htmlCountString.append(bpccount).append(" BPCs [").append(getMaxRuns()).append(" copies]");
		htmlCountString.append("</font>");
		return Html.fromHtml(htmlCountString.toString());
	}

	/**
	 * The result if a pair of values that represent the number of blueprints on the location/container and the
	 * number of blueprints that can be really manufactured. This later number is shown on a color that depends
	 * on the resource availability. The color codes are GREEN for all blueprints can be manufactured to RED no
	 * blueprint can be manufactured.
	 * 
	 * @return
	 */
	public String get_bpccounts1() {
		return Integer.valueOf(bpccount).toString() + " BPCs";
	}

	public String get_jobsParameter(final int jobsnumber) {
		return qtyFormatter.format(jobsnumber) + " jobs";
	}

	// public String get_jobs() {
	// return qtyFormatter.format(jobs) + " jobs";
	// }

	public String get_manufactureIndex() {
		return moduleIndexFormatter.format(getProfitIndex());
	}

	/**
	 * The result has specific constraints for T1 blueprints. If T1 then limit the number of runs.<br>
	 * The method should return the number of possible runs. For T2 is the number of blueprints multiplied by
	 * the runs of each blueprint on the stack. For T1 this is limited to the runs of a single blueprint,
	 * whichever that number is. For T3 the calculations are the same as for T2. <br>
	 * Also we include the number of real runs that can be completed with the available resources at the
	 * blueprint location.<br>
	 * Those values are calculated before showing the results.
	 * 
	 * @return
	 */
	public String get_stackRuns() {
		// Calculate again the max number of manufacturable runs.
		final IJobProcess process = JobManager.generateJobProcess(getPilot(), getCastedModel(), EJobClasses.MANUFACTURE);
		final int maxRuns = process.getManufacturableCount();
		if (getCastedModel().getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI))
			return qtyFormatter.format(getCastedModel().getRuns()) + " / " + qtyFormatter.format(maxRuns);
		return qtyFormatter.format(runCount) + " / " + qtyFormatter.format(maxRuns);
	}

	// public Spanned get_profit() {
	// double profit = (getSellerprice() - process.getJobCost()) *
	// getPossibleRuns();
	// StringBuffer htmlResult = new StringBuffer("<font color='");
	// if (profit > 0)
	// htmlResult.append("#FF6CC417");
	// else
	// htmlResult.append("#FFF62217");
	// htmlResult.append("'>").append(priceFormatter.format(profit)).append("</font>");
	// return Html.fromHtml(htmlResult.toString());
	// }

	public String get_totalJobDuration(final int runs) {
		return generateTimeString(getCycleTime() * runs * 1000);
	}

	public int getBlueprintCount() {
		return bpccount;
	}

	/**
	 * Return the current value of the budget. Blueprints and skills are not added during this calculation and
	 * prices user that the lowest seller price found.<br>
	 * Calculates the cost to buy all the resources required to complete the manufacture job. For this it will
	 * get the actions associated to the blueprint and aggregate the cost for all the BUY tasks that are
	 * resulting from that actions.
	 */
	public double getBudget() {
		// Get the Actions and the BUY tasks from them.
		double budget = 0.0;
		Vector<AbstractPropertyChanger> actions = getChildren();
		for (final AbstractPropertyChanger action : actions) {
			Vector<IGEFNode> tasks = ((AbstractGEFNode) action).getChildren();
			for (final IGEFNode node : tasks)
				if (node instanceof TaskPart) {
					final TaskPart task = (TaskPart) node;
					if (task.getCastedModel().getItem().isBlueprint()) {
						continue;
					}
					if (task.getCastedModel().getTaskType() == ETaskType.BUY) {
						budget += task.getCastedModel().getQty() * task.getCastedModel().getPrice();
						Log.i("EVEI", "-- Incrementing budget by " + budget);
					}
				}
		}
		return budget;
	}

	public Blueprint getCastedModel() {
		return (Blueprint) getModel();
	}

	public int getCycleTime() {
		return process.getCycleDuration();
	}

	public String getGroupCategory() {
		return getCastedModel().getModuleGroup();
	}

	// /**
	// * Calculates the number of T2 blueprints that can be launched depending
	// on the available resources. This is
	// * calculated by the number of Datacores present and the number required
	// for each invention job. This number
	// * is a availability limit that is an absolute number.
	// *
	// * @return
	// */
	// public int getInventionCount() {
	// process =
	// JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(),
	// getCastedModel(),
	// EJobClasses.INVENTION);
	// return process.getManufacturableCount();
	// // double intermediate = (1.0 * manufacturableCount) / (1.0 *
	// getCastedModel().getRuns());
	// // // Limit the jobs to the number of blueprints
	// // jobs = Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(),
	// bpccount);
	// // bpcmanufacturable = jobs;
	// // }
	// // return manufacturableCount;
	// }

	public double getInventionCost() {
		process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), getCastedModel(),
				EJobClasses.INVENTION);
		return process.getJobCost();
	}

	public int getJobActivity() {
		return activity;
	}

	public int getJobs() {
		final IJobProcess process = JobManager.generateJobProcess(getPilot(), getCastedModel(), EJobClasses.MANUFACTURE);
		final int maxRuns = process.getManufacturableCount();
		final double intermediate = (1.0 * maxRuns) / (1.0 * getCastedModel().getRuns());
		final int jobs = Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(), bpccount);
		return jobs;
	}

	public ArrayList<Resource> getLOM() {
		return process.getLOM();
	}

	/**
	 * Shows the icon for manufacture and the manufacture calculated cost for this item if can be calculated.
	 * NOt all item types can have this value so the display has to reflect that. If the cost of manufacture is
	 * less that the best sell price then the price is shown in green and the sell multiplier is added to the
	 * price. If the manufacture cost is greater than the sell price it is shown in red.
	 * 
	 * @return
	 */
	public double getManufactureCost() {
		return getCastedModel().getJobProductionCost();
	}

	public int getMaxRuns() {
		return getCastedModel().getManufacturableCount();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getAssetID();
	}

	public String getName() {
		return getCastedModel().getName();
	}

	/**
	 * Get the minimum between the max runs available on the blueprint or the runs that can be created with the
	 * available resources. If this last value is greater that would mean that we can generate more jobs.
	 * 
	 * @return
	 */
	public int getPossibleRuns() {
		return Math.min(getCastedModel().getRuns(), getMaxRuns());
	}

	/** Return the type id of the job product. */
	public int getProductID() {
		return process.getProductID();
	}

	public int getProfitIndex() {
		return getCastedModel().getManufactureIndex();
	}

	public int getRunCount() {
		return runCount;
	}

	public int getRuns() {
		return getCastedModel().getRuns();
	}

	/**
	 * Calculates the total runtime for a job. The calculation implies to set the result for the number of jobs
	 * and the number of available and possible runs. So the job duration will be the number of runs of the job
	 * or the number of possible runs by the time to complete a run.
	 * 
	 * @return
	 */
	public int getRunTime() {
		return getPossibleRuns() * process.getCycleDuration();
	}

	/**
	 * Return the number of blueprints that are contained in this stack.
	 * 
	 * @return
	 */
	public int getStackSize() {
		return getCastedModel().getQuantity();
	}

	public String getSubtitle() {
		return process.getSubtitle();
	}

	public int getTypeID() {
		return getCastedModel().getTypeID();
	}

	public void incrementStack() {
		getCastedModel().setQuantity(getCastedModel().getQuantity() + 1);
	}

	/**
	 * Process a click on a blueprint target. This can happen in some pages so the action may depend on the
	 * render role that got assigned to the blueprint part when created.<br>
	 * For the blueprint manufacture pages jump to the IndustryT2Activity (should be renamed) and for the
	 * invention pages activate the activity IndustryInventionActivity.
	 */
	public void onClick(final View target) {
		Log.i("EVEI", ">> BlueprintPart.onClick");
		final Intent intent = new Intent(getActivity(), IndustryT2Activity.class);
		intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID, getPilot().getCharacterID());
		intent.putExtra(AppWideConstants.extras.EXTRA_BLUEPRINTID, getCastedModel().getAssetID());
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_BLUEPRINTT2INVENTION) {
			intent.putExtra(AppWideConstants.extras.EXTRA_BLUEPRINTACTIVITY, 8);
		} else {
			intent.putExtra(AppWideConstants.extras.EXTRA_BLUEPRINTACTIVITY, 1);
		}
		getActivity().startActivity(intent);
		Log.i("BlueprintPart", "<< BlueprintPart.onClick");
	}

	public boolean onContextItemSelected(final MenuItem item) {
		return false;
	}

	/**
	 * Creates the contextual menu for the selected blueprint. The menu depends on multiple factors like if the
	 * blueprint is rendered on the header or on other listings like the assets or the industry listings.
	 */
	// REFACTOR Removed during the DataSource integration
	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		// Log.i("EVEI", ">> BlueprintPart.onCreateContextMenu");
		// // PagerFragment frag = (PagerFragment) getFragment();
		// // For blueprints the menu depends on the renderer selected.
		// if ((getRenderMode() ==
		// AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRYHEADER)
		// || (getRenderMode() ==
		// AppWideConstants.rendermodes.RENDER_BLUEPRINTINVENTIONHEADER)) {
		// final JobRunsDialog dialog = new JobRunsDialog();
		// dialog.setBlueprintPart(this);
		// final BlueprintPart self = this;
		// // PagerFragment frag = (PagerFragment) getFragment();
		// // dialog.setFragment(frag);
		// dialog.setDialogCallback(new ADialogCallback() {
		//
		// @Override
		// public void onDialogNegativeClick(final DialogFragment dialog) {
		// }
		//
		// @Override
		// public void onDialogPositiveClick(final DialogFragment dialog) {
		// // Get the number of runs selected by the user.
		// final int runs = ((JobRunsDialog) dialog).getRuns();
		// // Verify with the number of runs the number of blueprints
		// // used.
		// Toast.makeText(getActivity(), "Selected Runs: " + runs,
		// Toast.LENGTH_LONG).show();
		// JobManager.launchJob(getPilot(), self, runs, getJobActivity());
		// final Intent intent = new Intent(getActivity(),
		// JobDirectorActivity.class);
		// intent.putExtra(AppWideConstants.extras.EXTRA_EVECHARACTERID,
		// getPilot().getCharacterID());
		// getActivity().startActivity(intent);
		// }
		// });
		// //
		// getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		// dialog.show(getActivity().getFragmentManager(), "JobRunsDialog");
		// }
		// Log.i("EVEI", "<< BlueprintPart.onCreateContextMenu");
	}

	/**
	 * Sets the type of activity to perform with the blueprint. There are options that will require this
	 * information to make a decision about the progress of the action. It also will pprepare the part to
	 * generate the output expected for the activity selected.br> The method instantiates a new process, being
	 * it a Manufacture process or an Invention process depending on the actility selected to perform the
	 * blueprint calculations.
	 * 
	 * @param activity
	 *          EVE activity code.
	 */
	public void setActivity(final int newActivity) {
		activity = newActivity;
		// Set the processor.
		if (activity == ModelWideConstants.activities.MANUFACTURING) {
			process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), getCastedModel(),
					EJobClasses.MANUFACTURE);
		}
		if (activity == ModelWideConstants.activities.INVENTION) {
			process = JobManager.generateJobProcess(EVEDroidApp.getAppStore().getPilot(), getCastedModel(),
					EJobClasses.INVENTION);
			// calculateRuns();
		}
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("BlueprintPart [");
		buffer.append(getCastedModel().getName()).append(" ");
		buffer.append("#").append(getCastedModel().getTypeID()).append(" ");
		buffer.append(" x").append(getCastedModel().getQuantity()).append(" ");
		buffer.append("Runs:").append(runCount).append("/").append(getMaxRuns()).append(" ");
		// buffer.append("Budget:").append(budget).append(" ");
		buffer.append("Actions:").append(getChildren().size()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected void initialize() {
		item = getCastedModel().getModuleItem();
		if (null == item)
			throw new RuntimeException("RT> BlueprintPart - The task item is not defined. " + getCastedModel().getName());
		// getManufacturableCount();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRYHEADER)
			return new Blueprint4IndustryHeaderRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_BLUEPRINTINVENTIONHEADER)
			return new Blueprint4IndustryHeaderRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY)
			return new Blueprint4IndustryRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_BLUEPRINTT2INVENTION)
			return new Blueprint4T2InventionRender(this, _activity);
		throw new RuntimeException("E> Undefined Render variant.");
	}

	private void calculateRuns() {
		final double intermediate = (1.0 * getMaxRuns()) / (1.0 * getCastedModel().getRuns());
		// Limit the jobs to the number of blueprints
		// jobs = Math.min(Double.valueOf(Math.ceil(intermediate)).intValue(),
		// bpccount);
		// bpcmanufacturable = jobs;
	}
}

// - UNUSED CODE
// ............................................................................................
