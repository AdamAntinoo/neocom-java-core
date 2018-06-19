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
package org.dimensinfin.eveonline.neocom.processor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.MarketOrder;
import org.dimensinfin.eveonline.neocom.database.entity.Property;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.enums.EPropertyTypes;
import org.dimensinfin.eveonline.neocom.enums.PreferenceKeys;
import org.dimensinfin.eveonline.neocom.industry.Action;
import org.dimensinfin.eveonline.neocom.industry.EveTask;
import org.dimensinfin.eveonline.neocom.industry.Fitting;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.database.entity.NeoComAsset;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class FittingProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("FittingProcessor");

	// - F I E L D - S E C T I O N ............................................................................
	/**
	 * List of Properties that define the different roles that can be applied to locations. Required to discriminate industrial
	 * actions against the complete set of locations with assets.
	 */
	private List<Property> roles = new ArrayList<>();
	private List<MarketOrder> scheduledOrders = new ArrayList<>();
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
//	protected NeoComBlueprint blueprint = null;
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

	// - M E T H O D - S E C T I O N ..........................................................................

	public FittingProcessor setCredential( final Credential credential ) {
		this.credential = credential;
		return this;
	}

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
	public List<Action> processFitting( final Credential credential, final Fitting target, final int copyCount ) {
		logger.info(">> [FittingProcessor.processFitting]");
		this.credential = credential;

		// STEP0 01. Do the mandatory initialization such as getting a current list of assets or the current Manufacture location.
		// Get all Location roles for this pilot.
		try {
			final HashMap<String, Object> queryParams = new HashMap<>();
			queryParams.put("ownerId", credential.getAccountId());
			queryParams.put("propertyType", EPropertyTypes.LOCATIONROLE.name());
			roles = new GlobalDataManager().getNeocomDBHelper().getPropertyDao().queryForFieldValues(queryParams);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		scheduledOrders = this.accessScheduledOrders();

		// Get the work place location. This should be a Manufacture targeted role location if defined. Home if not.
		manufactureLocation = searchManufactureLocation(credential);
		region = manufactureLocation.getRegion();
		// Get the list of actions declared for this Pilot.
		actions4Item = getPilotActions(credential);
		// Get the list of character assets.
		assetsManager = new AssetsManager(credential);
		// Clear processing variables.
		requirements.clear();
		actionsRegistered.clear();

		// STEP 02. Decompose the list of items and the hull for this Fitting.
		// Add the hull to the list of requirements.
		requirements.add(new Resource(target.getShipHullInfo().getItemId(), copyCount));
		// Add the list of items to the list of requirements.
		for (Fitting.FittingItem item : target.getItems()) {
			//During the addition of requirements join all the same type requests.
			boolean found = false;
			for (Resource res : requirements) {
				if (res.getTypeId() == item.getTypeId()) {
					found = true;
					res.setQuantity(res.getQuantity() + item.getQuantity() * copyCount);
					break;
				}
			}
			if (!found) requirements.add(new Resource(item.getTypeId(), item.getQuantity() * copyCount));
		}

		// Resource list completed. Dump report to the log and start action processing.
		logger.info("-- [FittingProcessor.processFitting]> List of requirements: ", requirements);
		pointer = -1;
		try {
			do {
				pointer++;
				Resource resource = requirements.get(pointer);
				logger.info("-- [FittingProcessor.processFitting]> Processing resource: {}", resource);
//				// Check resources that are Skills. Give them an special treatment.
//				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.SkillInTraining)) {
//					currentAction = new SkillInTraining(resource);
//					this.registerAction(currentAction);
//					continue;
//				}
				currentAction = new Action(resource);
				EveTask newTask = new EveTask(Action.ETaskType.REQUEST, resource);
				newTask.setQty(resource.getQuantity());
				// We register the action before to get erased on restarts. This has no impact on data since we use pointers to the
				// global structures.
				this.registerAction(currentAction);
				try {
					this.processRequest(newTask);
				} catch (RuntimeException rtex) {
					logger.info("RT> [FittingProcessor.processFitting]> Unexpected code behaviour. See stacktrace.");
					rtex.printStackTrace();
				}
			} while (pointer < (requirements.size() - 1));
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

	protected HashMap<Long, Property> getPilotActions( final Credential credential ) {
		final HashMap<Long, Property> actions4Character = new HashMap<Long, Property>();
		try {
			final HashMap<String, Object> queryParams = new HashMap<>();
			queryParams.put("ownerId", credential.getAccountId());
			queryParams.put("propertyType", EPropertyTypes.MANUFACTUREACTION.name());
			final List<Property> actionList = new GlobalDataManager().getNeocomDBHelper().getPropertyDao().queryForFieldValues(queryParams);
			// Process the returned list and store in the character.
			for (Property property : actionList) {
				// The type selected for the action is stored as the property key.
				actions4Character.put(Double.valueOf(property.getNumericValue()).longValue(), property);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return actions4Character;
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
		logger.info(">> [FittingProcessor.processRequest]");
		// The task is a request. Check in order.
		final long requestQty = newTask.getQty();
		// If quantity completed we have completed the processing.
		if (requestQty < 1) return;

		// Check the special case for Asteroids
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
		if (newTask.getTaskType() == Action.ETaskType.INVENTION) {
			// TODO Implement the process invention
//			this.processInvent(newTask);
			return;
		}
		// Get the Assets that match the current type id.
		List<NeoComAsset> available = getAssetsManager().getAssets4Type(newTask.getResource().getItem().getTypeId());
		//TODO - Shortcircuit the assets manager.
//		available = new ArrayList<>();
		logger.info("-- [FittingProcessor.processRequest]> Total available assets 4 type: {}", available);
		// OPTIMIZATION. Do all Move tests only if there are items of this type available.
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
				logger.info("-- [FittingProcessor.processRequest]> Checking Move Allowed flag > {}" + this.moveAllowed());
				if (this.moveAllowed()) {
					// See if we have that resource elsewhere ready for transportation.
					// MOVE - manufacture region
					if (asset.getLocation().getRegionId() == manufactureLocation.getRegionId()) {
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
		logger.info("-- [FittingProcessor.processRequest]> Delegating processing to [processAction]");
		this.processAction(newTask);
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
			if (asset.getOwnerId() == -1) {
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

	protected void processAction( final EveTask newTask ) {
		logger.info(">> [FittingProcessor.processAction]> Task:{}", newTask);
		final String category = newTask.getItem().getCategoryName();
		// Check the special case for T2 BPC to transform them to default INVENTION.
		if (newTask.getTaskType() == Action.ETaskType.REQUEST)
			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
				final String tech = newTask.getItem().getTech();
				if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					logger.info("-- [FittingProcessor.processAction]> T2 Blueprint - request INVENTION");
					newTask.setTaskType(Action.ETaskType.INVENTION);
					this.processRequest(newTask);
					return;
				}
				// If we request a T1 blueprint then we have to check if we can create copies. This is a default
				if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
					//					ArrayList<Asset> bpcs = getAsset4Type(newTask.getItem().getTypeId());
					//					// Search each blueprint to locate the BPO and then create the copies.
					//					for (Asset asset : bpcs) {
					//						Blueprint bp = industryAssetsManager.searchBlueprintByID(asset.getAssetId());
					//						if (bp.isBpo()) {
					newTask.setTaskType(Action.ETaskType.COPY);
//					this.processRequest(newTask);
					return;
					//						}
					//					}
					//					processBuy(newTask);
					//					return;
				}
			}

		// Check if the user has an action for this type of item. This can be tested for only some categories.
		// USER ACTIONS
		final Property action = actions4Item.get(new Long(newTask.getTypeId()));
		if (null != action) {
			// TODO This block of code is still pending review
//			// Store the action on the Task for later presentation.
//			currentAction.setUserAction(action.getStringValue());
//			// New code to handle reactions.
//			if (newTask.getItem().getIndustryGroup() == EIndustryGroup.REACTIONMATERIALS) {
//				this.processReaction(newTask);
//			}
//			if (category.equalsIgnoreCase("Planetary Commodities")) // Action is limited to PRODUCE or BUY
//				if (action.getStringValue().equalsIgnoreCase("PRODUCE")) {
//					final EveLocation planetaryLocation = pilot.getLocation4Role("PLANETARY PROCESSING", region);
//					newTask.setTaskType(ETaskType.PRODUCE);
//					if (null != planetaryLocation) {
//						newTask.setLocation(planetaryLocation);
//					}
//					newTask.setDestination(manufactureLocation);
//					this.registerTask(500, newTask);
//					return;
//				}
//			if (category.equalsIgnoreCase("Material")) // Action is limited to EXTRACT or BUY
//				if (action.getStringValue().equalsIgnoreCase("MATERIAL REFINE")) {
//					this.processRefine(newTask);
//					return;
//				}
//			if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
//				// There can be two types. Check the corresponding action.
//				final String tech = newTask.getItem().getTech();
//				if (tech.equalsIgnoreCase("Tech I")) {
//					final EveLocation copyLocation = pilot.getLocation4Role("COPY", region);
//					AbstractManufactureProcess.logger.info("-- COPY location searched at " + copyLocation);
//					newTask.setTaskType(ETaskType.COPY);
//					if (null != copyLocation) {
//						newTask.setLocation(copyLocation);
//					}
//					final EveLocation inventLocation = pilot.getLocation4Role("INVENT", region);
//					AbstractManufactureProcess.logger.info("-- INVENT location searched at " + inventLocation);
//					if (null != inventLocation) {
//						newTask.setDestination(manufactureLocation);
//					}
//					this.registerTask(400, newTask);
//					return;
//				}
//		if ((category.equalsIgnoreCase(ModelWideConstants.eveglobal.Module)) || (category.equalsIgnoreCase("Commodity"))
//				|| (category.equalsIgnoreCase("Charge"))) // Action is limited to BUILD.
//			if (action.getStringValue().equalsIgnoreCase("BUILD")) {
//				// Schedule a manufacture request.
//				 					this.processBuild(newTask);
//				return;
//			}
//		if (category.equalsIgnoreCase("Ship")) // Action is limited to BUILD.
//			if (action.getStringValue().equalsIgnoreCase("BUILD")) {
//				// Schedule a manufacture request.
//					this.processBuild(newTask);
//				return;
//			}
		}
		this.processBuy(newTask);
	}

	/**
	 * When a required resource is not found elsewhere we have to generate a BUY action. This is the final
	 * action but still there are two different BUY actions. If there is no Scheduled Buy request on list then
	 * the BUY is a real BUY request that should be differentiated from a BUY when the user has already
	 * requested a buy to the market. This is a visual aid to the user to remember that the buy has already been
	 * requested to the market.
	 *
	 * @param newTask
	 */
	protected void processBuy( final EveTask newTask ) {
//		final List<MarketOrder> scheduledOrders = this.accessScheduledOrders();
		// Search for an order for this type.
		for (final MarketOrder marketOrder : scheduledOrders)
			if (marketOrder.getTypeId() == newTask.getTypeId()) { //if (marketOrder.getQuantity() < newTask.getQty()) {
				final int taskQty = newTask.getQty();
				final int orderQty = marketOrder.getVolumeRemain();
				// Update the tasks depending on those two quantities.
				// Generate two orders, one with the covered buy and maybe other with the rest.
				newTask.setTaskType(Action.ETaskType.BUYCOVERED);
				try {
					newTask.setLocation(newTask.getResource().getItem().getLowestSellerPrice().getLocation());
				} catch (ExecutionException ee) {
					newTask.setLocation(new EveLocation(60003466));
				} catch (InterruptedException ie) {
					newTask.setLocation(new EveLocation(60003466));
				}
				newTask.setMarketCounterPart(marketOrder);
				newTask.setDestination(manufactureLocation);
				newTask.setQty(Math.min(taskQty, orderQty));
				this.registerTask(300, newTask);
				final int diff = taskQty - orderQty;
				if (diff > 0) {
					final EveTask partialTask = new EveTask(Action.ETaskType.BUY, newTask.getResource());
					try {
						partialTask.setLocation(newTask.getResource().getItem().getLowestSellerPrice().getLocation());
					} catch (ExecutionException ee) {
						partialTask.setLocation(new EveLocation(60003466));
					} catch (InterruptedException ie) {
						partialTask.setLocation(new EveLocation(60003466));
					}
					partialTask.setDestination(manufactureLocation);
					partialTask.setQty(diff);
					this.registerTask(300, partialTask);
				}
				// Complete the request and stop more processing for this task.
				return;
			}
		newTask.setTaskType(Action.ETaskType.BUY);
		try {
			newTask.setLocation(newTask.getResource().getItem().getLowestSellerPrice().getLocation());
		} catch (ExecutionException ee) {
			newTask.setLocation(new EveLocation(60003466));
		} catch (InterruptedException ie) {
			newTask.setLocation(new EveLocation(60003466));
		}
		newTask.setDestination(manufactureLocation);
		this.registerTask(300, newTask);
	}

	private List<MarketOrder> accessScheduledOrders() {
		// Search for an scheduled buy and get its quantity.
		try {
			final List<MarketOrder> orders = new ArrayList<MarketOrder>();
			final List<MarketOrder> allorders = GlobalDataManager.accessMarketOrders4Credential(this.credential);
			// Process the orders and filter just the ones we need.
			for (MarketOrder order : allorders) {
				if (order.getOrderState() == MarketOrder.EOrderStates.SCHEDULED) {
					orders.add(order);
				}
				if (order.getOrderState() == MarketOrder.EOrderStates.OPEN) {
					orders.add(order);
				}
			}
			return orders;
		} catch (SQLException sqle) {
			return new ArrayList();
		}
	}

	//-------------------------------------------------------------------------------------------------------

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
				.getBooleanPreference(PreferenceKeys.prefkey_AllowMoveRequests.name(), true);
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

	/**
	 * The correct search algorithm should get the most interesting Manufacture location in the case there is more than one
	 * identified as a Manufacture place. I should force it to be a single place but this specification is not clear to be useful
	 * . Anyway if there is a Manufacture Role Location we can choose it and if not then we should consider the Pilot home
	 * location as the designated place.
	 *
	 * @param credential the Pilot credential to be used to download or access any relevant information.
	 * @return a location to be used as the MANUFACTURE point. Research and other industry activities can be performed at other
	 * places.
	 */
	protected EveLocation searchManufactureLocation( final Credential credential ) {
		logger.info(">> [FittingProcessor.searchManufactureLocation]");
		for (Property prop : roles) {
			if (prop.getPropertyType() == EPropertyTypes.LOCATIONROLE)
				if (prop.getStringValue().equalsIgnoreCase("MANUFACTURE"))
					return new GlobalDataManager().searchLocation4Id(Double.valueOf(prop.getNumericValue()).intValue());
		}
		// Reaching this point means we have not a location selected.
		// TODO Use a mock place. This is the Singularity selected place to test.
		return new GlobalDataManager().searchLocation4Id(60006526);
	}

	private AssetsManager getAssetsManager() {
		if (null == assetsManager) assetsManager = new AssetsManager(credential);
		return assetsManager;
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

final class AssetsManager {
	private static Logger logger = LoggerFactory.getLogger("AssetsManager");

	public String jsonClass = "AssetsManager";
	private Credential currentPilotCredential = null;
	private final HashMap<Integer, List<NeoComAsset>> asset4TypeCache = new HashMap<Integer, List<NeoComAsset>>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsManager( final Credential credential ) {
//		super(credential);
		// Load the timestamp from the database to control the refresh status of all the assets.
//		this.readTimeStamps();
		currentPilotCredential = credential;
		jsonClass = "AssetsManager";
	}

	/**
	 * Return the set of assets for a character that have an specific item type id. We have to make a local copy
	 * of the assets because they are going to be modified during the task creation process. So we can implement
	 * a cache of those assets so we only read them from the database the first time and later we only use the
	 * local copy.
	 *
	 * @param typeId
	 * @return
	 */
	public List<NeoComAsset> getAssets4Type( final int typeId ) {
		logger.info(">> [AssetsManager.getAssets4Type]");
		List<NeoComAsset> hit = new ArrayList<>();
		try {
			// Search for the asset pack first at the instance cache.
			hit = asset4TypeCache.get(Integer.valueOf(typeId));
			if (null == hit) {
				final HashMap<String, Object> filterParameters = new HashMap();
				filterParameters.put("ownerID", currentPilotCredential.getAccountId());
				filterParameters.put("typeId", typeId);
				hit = new GlobalDataManager().getNeocomDBHelper().getAssetDao().queryForFieldValues(filterParameters);
				// Cache the new list of assets for the specified type.
				asset4TypeCache.put(Integer.valueOf(typeId), hit);
			}
			return hit;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return hit;
		} finally {
			logger.info("<< [AssetsManager.getAssets4Type]> List size: {}", hit.size());
		}
	}
}
// - UNUSED CODE ............................................................................................
//[01]
