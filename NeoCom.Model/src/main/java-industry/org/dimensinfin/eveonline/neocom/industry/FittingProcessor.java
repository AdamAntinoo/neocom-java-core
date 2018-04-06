//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.PreferenceKeys;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Fitting;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.Property;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class FittingProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("FittingProcessor");

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * This is the selected location to be used when searching for resources for the Fitting processing. Setting/configuring
	 * this place properly will change the result of the fitting processing.
	 */
	protected EveLocation manufactureLocation = null;
	protected String region = null;
	private Credential credential = null;
	private AssetsManager assetsManager = null;

	/**
	 * The main element used for the manufacture job.
	 */
	protected NeoComBlueprint blueprint = null;
	/** The Pilot owner of the job and blueprint. Required to get the characterID. */
//	protected transient NeoComCharacter									pilot										= null;
	/**
	 * New and locally used AssetsManager used to process the job requests.
	 */
	protected transient AssetsManager industryAssetsManager = null;
	protected int bpid = -1;
	protected int moduleid = -1;
	protected ArrayList<Resource> lom = null;
	protected double cost = -1.0;
	protected int index = -1;
	private boolean totalcalculated = false;
	protected int totalManufacturable = -1;

	// - A C T I O N   P R O C E S S I N G
	protected HashMap<Long, Property> actions4Item = null;
	protected ArrayList<Resource> requirements = new ArrayList<Resource>();
	protected transient final HashMap<Integer, Action> actionsRegistered = new HashMap<Integer, Action>();
	protected transient Action currentAction = null;
	protected int pointer = -1;
	protected int runs = 10;
	protected int threads = 1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public FittingProcessor() {
//	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * The Fitting processor should get a Fitting (T1, T2, T3 ship) and running a set of rules generate a list of actions that
	 * will show all the activities to run to complete its construction. The actions have some default states that can be
	 * changed/configured for the industry man preferences like some items should be manufactured or invented.
	 * The start point is a <code>Fitting</code> and a number of copies. The assets required for the completion of the task are
	 * also obtained at the initiation of the process and can be changed/replaced during the processing to evaluate the result of
	 * some user defined actions like refining or job launching.
	 * So the while process starts with an initialization - item decomposition - task evaluation - task collecting.
	 * <p>
	 * Some tasks are related to a Location, identified as the Manufacture Preferred Location. For a Pilot there should be only one
	 * of the Stations marked as this special location. If this location is not specified we should then consider it the Home
	 * Location and if there is no default the current character location. If there is not a valid location the process fails and
	 * we do not perform the processing.
	 */
	public List<Action> processFitting( final int credentialIdentifier, final Fitting target, final int copyCount ) {
		logger.info(">> [FittingProcessor.processFitting]");
		// STEP0 01. Do the mandatory initialization such as getting a current list of assets or the current Manufacture location.
		// Get the work place location.
		manufactureLocation = searchManufactureLocation(credentialIdentifier);
		// TODO Using a mock up of the location identifier until I have a better implementation of the Properties.
		manufactureLocation = GlobalDataManager.searchLocation4Id(60006526);
		region = manufactureLocation.getRegion();
		// Get the list of character assets.
		assetsManager = GlobalDataManager.getAssetsManager(DataManagementModelStore.activateCredential(credentialIdentifier),
				true);
		// Clear processing variables.
		requirements.clear();
		actionsRegistered.clear();

		// STEP 02. Decompose the list of items and the hull for this Fitting.
		// Add the hull to the list of requirements.
		requirements.add(new Resource(target.getShipHullInfo().getItemId(), copyCount));
		// Add the list of items to the list of requirements.
		for (Fitting.FittingItem item : target.getItems()) {
			requirements.add(new Resource(item.getTypeId(), item.getQuantity() * copyCount));
		}

		// Resource list completed. Dump report to the log and start action processing.
		logger.info("-- [FittingProcessor.processFitting]> List of requirements: ", requirements);
		pointer = -1;
		try {
			try {
				do {
					pointer++;
					Resource resource = requirements.get(pointer);
					logger.info("-- [FittingProcessor.processFitting]> Processing resource: {}", resource);
//				// Check resources that are Skills. Give them an special treatment.
//				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
//					currentAction = new Skill(resource);
//					this.registerAction(currentAction);
//					continue;
//				}
					currentAction = new Action(resource);
					EveTask newTask = new EveTask(Action.ETaskType.REQUEST, resource);
					newTask.setQty(resource.getQuantity());
					// We register the action before to get erased on restarts. This has no impact on data since we use pointers to the
					// global structures.
					this.registerAction(currentAction);
					this.processRequest(newTask);
				} while (pointer < (requirements.size() - 1));
			} catch (RuntimeException rtex) {
				logger.info("RT> [FittingProcessor.processFitting]> Unexpected code behaviour. See stacktrace.");
				rtex.printStackTrace();
			}
			return this.getActions();
		} finally {
			logger.info("<< [FittingProcessor.processFitting]");
		}
	}
	protected List<Action> getActions() {
		final List<Action> result = new ArrayList<Action>();
		for (final Action action : actionsRegistered.values()) {
			result.add(action);
		}
		return result;
	}

	/**
	 * This is the main processing entry point. When a task is created it is entered to the manager to check of
	 * the action can be performed with other tasks that are less costly and that have a lower priority. If
	 * those changes generate a new set of resources then the progress structures are cleared and the process
	 * restarts again.
	 *
	 * @param newTask
	 */
	protected void processRequest( final EveTask newTask ) {
		// The task is a request. Check in order.
		final long requestQty = newTask.getQty();
		if (requestQty < 1) return;

//		// Check the special case for Asteroids
//		if (newTask.getTaskType() == Action.ETaskType.REQUEST) {
//			logger.info("RT> [FittingProcessor.processFitting]> Processing state> {} [x{}]", Action.ETaskType.REQUEST , requestQty );
//			final String category = newTask.getItem().getCategory();
//			logger.info("-- [FittingProcessor.processFitting]> Checking special case of Asteroids > {}", category);
//			// If the resource is an asteroid then we can Refine it.
//			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Asteroid)) {
//				//				Log.i("EVEI", "-- [AbstractManufactureProcess.processRequest]-Asteroid - request COMPLETED");
//				// Complete the action and add the minerals obtained as tasks.
//				currentAction.setCompleted(ETaskCompletion.COMPLETED, newTask.getQty());
//				// Add the refine of the mineral to the tasks.
//				final ArrayList<Resource> refineParameters = NeoComAppConnector.getSingleton().getCCPDBConnector()
//						.refineOre(newTask.getTypeId());
//				for (final Resource rc : refineParameters) {
//					final double mineral = Math.floor(Math.floor(newTask.getResource().getQuantity() / rc.getStackSize())
//							* (rc.getBaseQuantity() * AbstractManufactureProcess.REFINING_EFFICIENCY));
//					final EveTask refineTask = new EveTask(ETaskType.PRODUCE,
//							new Resource(rc.item.getItemID(), Double.valueOf(mineral).intValue()));
//					refineTask.setQty(Double.valueOf(mineral).intValue());
//					refineTask.setLocation(newTask.getLocation());
//					this.registerTask(90, refineTask);
//				}
//				return;
//			}
//		}
//		if (newTask.getTaskType() == Action.ETaskType.INVENTION) {
//			// TODO Implement the process invention
//			this.processInvent(newTask);
//			return;
//		}
		// Get the Assets that match the current type id.
		final List<NeoComAsset> available = assetsManager.getAssets4Type(newTask.getResource().getItem().getItemId());
		logger.info("-- [AbstractManufactureProcess.processRequest]> Total available assets 4 type: {}", available);
		// OPTIMIZATION. Do all Move tests only if there are arrest of this type available.
		if (available.size() > 0) {
			// See if there are assets of this type on the manufacture location before moving assets.
			// MOVE - manufacture location
			for (final NeoComAsset asset : available) {
				// Removed assets with no quantity. This can be possible after some job launching.
				if (asset.getQuantity() < 1) {
					continue;
				}
				// Check if the asset location matches the Manufacture location. If so USE it by reducing the availability count.
				if (asset.getLocation().equals(manufactureLocation)) {
					processMove(asset, newTask);
					return;
				}
//			final EveLocation loc = asset.getLocation();
//			if (loc.toString().equalsIgnoreCase(manufactureLocation.toString())) {
//				this.processMove(asset, newTask);
//				return;
//			}
//			}
				// Check the MOVE flag to control if the user allows to search for assets at other locations
				logger.info("-- [AbstractManufactureProcess.processRequest]> Checking Move Allowed flag > {}" + this.moveAllowed());
				if (this.moveAllowed()) {
					// See if we have that resource elsewhere ready for transportation.
					// MOVE - manufacture region
					if (asset.getLocation().getRegionID() == manufactureLocation.getRegionID()) {
						this.processMove(asset, newTask);
						return;
					}
					// Assets not in same region or not found. Try without region limits.
					// MOVE - rest of universe
					this.processMove(asset, newTask);
					return;
				}
			}
		}

		// If we reach this point we are sure that all other intents have been processed.
		// Continue processing a BUY request or its decomposition.
		logger.info("-- [AbstractManufactureProcess.processRequest]> Delegating processing to [processAction]");
//		this.processAction(newTask);
	}

	/**
	 * Creates a MOVE task that reduces the asset count on some place to cover a requirement.
	 *
	 * @param asset
	 * @param newTask
	 */
	protected void processMove( final NeoComAsset asset, final EveTask newTask ) {
		// Load data to do all the checks.
//		final EveLocation loc = asset.getLocation();
		final int requestQty = newTask.getQty();
		final int qty = asset.getQuantity();

		// Do the MOVE for som assets.
		final EveTask moveTask = new EveTask(Action.ETaskType.MOVE, newTask.getResource());
		// Stack quantity may not be enough to cover the requirements.
		moveTask.setQty(Math.min(requestQty, qty));
		moveTask.setLocation(asset.getLocation());
		moveTask.setDestination(manufactureLocation);
		// Treat the special case of assets already present on the Manufacture location.
		if (asset.getLocation().equals(manufactureLocation)) {
			// Convert the move to AVAILABLE because the locations match.
			// If the owner is -1 then this resource comes from a reprocessing.
			if (asset.getOwnerID() == -1) {
				moveTask.setTaskType(Action.ETaskType.EXTRACT);
			} else {
				moveTask.setTaskType(Action.ETaskType.AVAILABLE);
			}
			this.registerTask(90, moveTask, asset);
		} else {
			this.registerTask(400, moveTask, asset);
		}
		if (qty >= requestQty) {
			// This stack is able to complete the request.
			return;
		} else {
			// We need more locations to complete the request.
			final EveTask newRequest = new EveTask(Action.ETaskType.REQUEST, newTask.getResource())
					.setQty(requestQty - qty);
			this.processRequest(newRequest);
			return;
		}
	}

	/**
	 * Aggregates the new task to the list of tasks. Before adding the task to the list it checks if there is a
	 * task of the same item and type to accumulate the quantities instead of generating different tasks.<br>
	 * After it modifies the list it fires a change so any listeners will trigger update processes.
	 *
	 * @param pri
	 * @param task
	 */
	private synchronized void registerTask( final int pri, final EveTask task ) {
		// Check for completed tasks.
		if (task.getTaskType() == Action.ETaskType.AVAILABLE) {
			currentAction.setCompleted(Action.ETaskCompletion.COMPLETED, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.EXTRACT) {
			currentAction.setCompleted(Action.ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.SELL) {
			currentAction.setCompleted(Action.ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.REFINE) {
			currentAction.setCompleted(Action.ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.MOVE) {
			currentAction.setCompleted(Action.ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.BUILD) {
			currentAction.setCompleted(Action.ETaskCompletion.PENDING, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.BUY) {
			currentAction.setCompleted(Action.ETaskCompletion.MARKET, task.getQty());
		}
		if (task.getTaskType() == Action.ETaskType.BUYCOVERED) {
			currentAction.setCompleted(Action.ETaskCompletion.PENDING, task.getQty());
		}
		currentAction.registerTask(pri, task);
	}

	/**
	 * Register the task on the <code>Action</code>. This method is the one responsible to modify the assets
	 * used to complete the task.
	 *
	 * @param pri         priority of the task being registered.
	 * @param task        the task that completes the request or part of the request.
	 * @param targetAsset the asset used to complete the task when this action requires movement or transformation of
	 *                    other resources. This is used to change the memory copy of the asset so next actions will found
	 *                    an scenery similar to the one in real life and not an infinite number of resources.
	 */
	private void registerTask( final int pri, final EveTask task, final NeoComAsset targetAsset ) {
		logger.info("-- [FittingProcessor.registerTask]> Registering task request [" + pri + "] " + task);
//		this.performTask(task, targetAsset);
		//Also add the asset as a reference to the task. Useful when activating links.
		task.registerAsset(targetAsset);
		this.registerTask(pri, task);
	}

	protected boolean moveAllowed() {
		// Read the flag values from the preferences.
		boolean moveAllowed = GlobalDataManager.getDefaultSharedPreferences()
				.getBoolean(PreferenceKeys.prefkey_AllowMoveRequests.name(), true);
		return moveAllowed;
	}

	protected void registerAction( final Action action ) {
		// Test if already an action of the same item.
		final Action hit = actionsRegistered.get(action.getTypeId());
		if (null != hit) {
			currentAction = action;
		} else {
			actionsRegistered.put(action.getTypeId(), action);
		}
	}

	protected EveLocation searchManufactureLocation( final int credentialIdentifier ) {
		logger.info(">> [FittingProcessor.searchManufactureLocation]");
		credential = DataManagementModelStore.activateCredential(credentialIdentifier);
//		Assert.isNull(credential, "[FittingProcessor.searchManufactureLocation]> Credential " + credentialIdentifier + " not found.");
		return GlobalDataManager.searchLocation4Id(60006526);
	}

	// --- D E L E G A T E D   M E T H O D S
	@Override
	public String toString() {
		return new StringBuffer("FittingProcessor[")
				.append("field:").append(0).append(" ")
				.append("]")
				.append("->").append(super.toString())
				.toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
