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

import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Fitting;
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
	private Credential credential = null;


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
	protected String region = null;
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
	public void processFitting( final int credentialIdentifier, final Fitting target, final int copyCount ) {
		logger.info(">> [FittingProcessor.processFitting]");
		// Do the mandatory initialization such as getting a current list of assets or the current Industry location.
// Get the work place location.
		manufactureLocation = searchManufactureLocation(credentialIdentifier);
		logger.info("<< [FittingProcessor.processFitting]");
	}

	private EveLocation searchManufactureLocation( final int credentialIdentifier ) {
		logger.info(">> [FittingProcessor.searchManufactureLocation]");
		credential = DataManagementModelStore.activateCredential(credentialIdentifier);
		Assert.isNull(credential, "[FittingProcessor.searchManufactureLocation]> Credential " + credentialIdentifier + " not found.");

//if(	Assert.isNull(credential))throw new NeocomRuntimeException("[FittingProcessor.searchManufactureLocation]> Credential " +
//		""+credentialIdentifier+" not found.");
return null;
	}

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
		logger.info(">> [FittingProcessor.generateActions4Blueprint]");
		// Initialize global structures.
		manufactureLocation = blueprint.getLocation();
		region = manufactureLocation.getRegion();
//		actions4Item = pilot.getActions();
		// Clear structures to be sure we have the right data.
		requirements.clear();
		actionsRegistered.clear();
		// Get the resources needed for the completion of this job.
		runs = blueprint.getRuns();
		threads = blueprint.getQuantity();


//		// Copy the LOM received to not modify the original data during the job
//		// processing.
//		for (Resource r : this.getLOM()) {
//			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
//		}
//		// Update the resource count depending on the sizing requirements for the job.
//		for (Resource resource : requirements) {
//			// Skills are treated differently.
//			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
//				resource.setStackSize(1);
//			} else {
//				resource.setAdaptiveStackSize(runs * threads);
//			}
//			// If the resource being processed is the job blueprint reduce the
//			// number of runs and set the counter.
//			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
//				resource.setStackSize(threads);
//			}
//		}
//		// Resource list completed. Dump report to the log and start action processing.
//		Log.i("EVEI", "-- T2ManufactureProcess.generateActions4Blueprint.List of requirements" + requirements);
//		pointer = -1;
//		try {
//			do {
//				pointer++;
//				Resource resource = requirements.get(pointer);
//				Log.i("EVEI", "-- T2ManufactureProcess.generateActions4Blueprint.Processing resource " + resource);
//				// Check resources that are Skills. Give them an special
//				// treatment.
//				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
//					currentAction = new Skill(resource);
//					this.registerAction(currentAction);
//					continue;
//				}
//				currentAction = new Action(resource);
//				EveTask newTask = new EveTask(ETaskType.REQUEST, resource);
//				newTask.setQty(resource.getQuantity());
//				// We register the action before to get erased on restarts.
//				// This has no impact on data since we use pointers to the
//				// global structures.
//				this.registerAction(currentAction);
//				this.processRequest(newTask);
//			} while (pointer < (requirements.size() - 1));
//		} catch (RuntimeException rtex) {
//			Log.e("RTEXCEPTION.CODE",
//					"RT> T2ManufactureProcess.generateActions4Blueprint - Unexpected code behaviour. See stacktrace.");
//			rtex.printStackTrace();
//		}
//		Log.i("EVEI", "<< T2ManufactureProcess.generateActions4Blueprint.");
	//	return this.getActions();
		return null;
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
