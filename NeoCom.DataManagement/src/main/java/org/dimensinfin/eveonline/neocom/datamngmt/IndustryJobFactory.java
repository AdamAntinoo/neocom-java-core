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
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.NeoComAppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.database.entity.Job;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.enums.EJobClasses;
import org.dimensinfin.eveonline.neocom.interfaces.IJobProcess;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.Action;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.part.BlueprintPart;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Job Manager will be the application element responsible to get all the structures required to create an
 * Industry job and create the right IJob component that will control all the data required by the user and
 * the UI to show the job requirements.<br>
 * <ul>
 * <li>The elements required start by the Owner (a <code>EveChar</code>) of the resources. This is needed to
 * get access to the list of assets, the locations and skill information between other required data.</li>
 * <li>Then the <code>Blueprint</code> that will be used on the job. From the blueprint we get the location
 * and any other relevant information to start the job.</li>
 * <li>Next comes the action we want to perform with the blueprint. The range allows to choose between
 * Manufacture, Invention, Time Research and others that can also depend on the particular blueprint. For
 * example for BPC there is no other possibility than Manufacture.</li>
 * </ul>
 * The result for all this is a new <code>IJob</code> that will implement the requested Action. The action is
 * not an object and may be a parameter but the result is the object we will use to create the element used by
 * the interface like List Of Materials, durations, costs and more.
 *
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class IndustryJobFactory /*implements Serializable*/ {
	// - S T A T I C - S E C T I O N ..........................................................................
//	private static final long													serialVersionUID			= 8549982228327471340L;
	private static Logger logger = LoggerFactory.getLogger("NeoComAsset");

	private static final HashMap<String, IJobProcess>	jobprocesscache				= new HashMap<String, IJobProcess>();
	private static AssetsManager											industryAssetsManager	= null;

	public static void clearCache() {
		Log.i("CACHE", "-- CLEARING job process cache");
		JobManager.jobprocesscache.clear();
		JobManager.industryAssetsManager = null;
	}

	public static IJobProcess generateJobProcess(final NeoComCharacter thePilot, final NeoComBlueprint target,
			final EJobClasses action) {
		if (null == thePilot) throw new RuntimeException("E> JobManager cannot complete an incomplete request");
		if (null == target) throw new RuntimeException("E> JobManager cannot complete an incomplete request");
		switch (action) {
			case MANUFACTURE:
				// Get the tech of the blueprint to generate the correct job processor.
				final String tech = target.getTech();
				if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechI)) {
					IJobProcess job = JobManager.checkCache("T1", thePilot, target);
					if (null == job) {
						job = new T1ManufactureProcess(JobManager.industryAssetsManager);
						job.setAssetsManager(JobManager.industryAssetsManager);
						job.setPilot(thePilot);
						job.setBlueprint(target);
						final String jobid = "T1." + Long.valueOf(thePilot.getCharacterID()).toString() + "."
								+ Long.valueOf(target.getAssetID()).toString();
						JobManager.jobprocesscache.put(jobid, job);
						Log.i("CACHE", "-- Adding new process " + jobid);
					}
					return job;
				}
				if (tech.equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
					IJobProcess job = JobManager.checkCache("T2", thePilot, target);
					if (null == job) {
						job = new T2ManufactureProcess(JobManager.industryAssetsManager);
						job.setAssetsManager(JobManager.industryAssetsManager);
						job.setPilot(thePilot);
						job.setBlueprint(target);
						final String jobid = "T2." + Long.valueOf(thePilot.getCharacterID()).toString() + "."
								+ Long.valueOf(target.getAssetID()).toString();
						JobManager.jobprocesscache.put(jobid, job);
						Log.i("CACHE", "-- Adding new process " + jobid);
					}
					return job;
				}
				break;
			case INVENTION:
				IJobProcess job = JobManager.checkCache("INV", thePilot, target);
				if (null == job) {
					job = new InventionProcess(JobManager.industryAssetsManager);
					job.setAssetsManager(JobManager.industryAssetsManager);
					job.setPilot(thePilot);
					job.setBlueprint(target);
					final String jobid = "INV." + Long.valueOf(thePilot.getCharacterID()).toString() + "."
							+ Long.valueOf(target.getAssetID()).toString();
					JobManager.jobprocesscache.put(jobid, job);
					Log.i("CACHE", "-- Adding new process " + jobid);
				}
				return job;
		}
		throw new RuntimeException("E> JobManager cannot complete an incomplete request");
	}

	public static int getIconIdentifier(final EIndustryGroup reference) {
		if (reference == EIndustryGroup.OUTPUT) return R.drawable.reverseengineering;
		if (reference == EIndustryGroup.SKILL) return R.drawable.leveltrained;
		if (reference == EIndustryGroup.BLUEPRINT) return R.drawable.blueprintdirector;
		if (reference == EIndustryGroup.COMPONENTS) return R.drawable.groupcomponents;
		if (reference == EIndustryGroup.DATACORES) return R.drawable.groupdatacores;
		if (reference == EIndustryGroup.DATAINTERFACES) return R.drawable.groupdatainterfaces;
		if (reference == EIndustryGroup.DECRIPTORS) return R.drawable.groupdecryptors;
		if (reference == EIndustryGroup.ITEMS) return R.drawable.groupitems;
		if (reference == EIndustryGroup.MINERAL) return R.drawable.groupmineral;
		if (reference == EIndustryGroup.PLANETARYMATERIALS) return R.drawable.groupplantarymaterials;
		if (reference == EIndustryGroup.REACTIONMATERIALS) return R.drawable.groupreactionmaterials;
		if (reference == EIndustryGroup.REFINEDMATERIAL) return R.drawable.grouprefinedmaterials;
		if (reference == EIndustryGroup.SALVAGEDMATERIAL) return R.drawable.groupsalvagematerials;
		if (reference == EIndustryGroup.OREMATERIALS) return R.drawable.groupmineral;
		return R.drawable.defaultitemicon;
	}

	/**
	 * Reinitializes the local copy of database assets to account for resources already consumed in user jobs.
	 * It will get all the scheduled user jobs and removed that used resources from the current list of assets
	 * so next action will not found "reserved" resources as available. Blueprints are an exception because they
	 * are already segregated into different virtual stacks, some of them visible to Industry and some not.
	 */
	public static void initializeAssets(final NeoComCharacter pilot) {
		Log.i("EVEI", ">> JobManager.initializeAssets");
		if (null != pilot) {
			JobManager.industryAssetsManager = new AssetsManager(pilot);

			// Get the user jobs and start processing their resources.
			final ArrayList<Job> userjobs = (ArrayList<Job>) NeoComAppConnector.getSingleton().getDBConnector()
					.searchJob4Class(pilot.getCharacterID(), "UJOB");
			Log.i("EVEI", "-- JobManager.initializeAssets.userjobs:" + userjobs);
			for (final Job job : userjobs) {
				// Get the unique blueprint used on the job and generate the jobs tasks.
				final NeoComBlueprint blueprint = JobManager.industryAssetsManager.searchBlueprintByID(job.getBlueprintID());
				// If the blueprint if not found then the job has been started on real. Drop the job
				if (null == blueprint) {
					try {
						NeoComAppConnector.getSingleton().getDBConnector().getJobDAO().delete(job);
						// Clear the cache in memory
						pilot.cleanJobs();
					} catch (final SQLException e) {
					}
					continue;
				}
				// Create a new blueprint for the processing adjusting the thread count to 1.
				final NeoComBlueprint bp = new NeoComBlueprint(blueprint.getAssetID());
				bp.setRuns(job.getRuns());
				final IJobProcess process = JobManager.generateJobProcess(pilot, bp,
						EJobClasses.decodeActivity(job.getActivityID()));
				// Process the action so all used resources will be removed from the stores.
				final ArrayList<Action> actions = process.generateActions4Blueprint();
			}
		}
		Log.i("EVEI", "<< JobManager.initializeAssets");
	}

	/**
	 * We receive apart that contains the stack of blueprints of the same characteristics that we like to
	 * manufacture. Then we also get the number of copies to build. This will require one or more blueprints
	 * (runs/blueprint runs) and so we have to "destack" the part on the former unique blueprints to be able to
	 * launch each job with the corresponding blueprint.<br>
	 * We can locate the list of blueprints with a selected typeID and then filter them to the location. It
	 * would also be possible to pack the single blueprint instances inside the children list of the stack
	 * during the stacking.
	 *
	 * @param pilot
	 *          the character that is going to launch the manufacture job
	 * @param part
	 *          the component to build. It contains the blueprint information.
	 * @param runs
	 *          the number of copies to produce
	 * @param activityID
	 *          the code of the activity. Maybe manufacture or invention or whatever.
	 */
	public static void launchJob(final NeoComCharacter pilot, final BlueprintPart part, final int runs,
			final int activityID) {
		Log.i("EVEI", ">> JobManager.launchJob.Blueprint:" + part + " [" + runs + "]");
		// Get the list of blueprint assets stacked on this part.
		final NeoComBlueprint blueprint = part.getCastedModel();
		final String refList = blueprint.getStackIDRefences();
		final String[] refs = refList.split(ModelWideConstants.STACKID_SEPARATOR);
		int refPosition = 0;

		int pendingRuns = runs;
		while (pendingRuns > 0) {
			final Job newJob = new Job(new Instant().getMillis());
			try {
				final long assetID = Long.parseLong(refs[refPosition]);
				newJob.setJobType("UJOB");
				newJob.setOwnerID(pilot.getCharacterID());
				final EveLocation loc = blueprint.getLocation();
				newJob.setFacilityID(blueprint.getLocation().getID()); // Invalid if a container
				newJob.setActivityID(activityID);
				newJob.setBlueprintID(assetID);
				newJob.setBlueprintTypeID(blueprint.getTypeID());
				newJob.setBlueprintLocationID(blueprint.getLocationID()); // If has parent then it is the container. otherwise the location
				final int jobRuns = Math.min(pendingRuns, blueprint.getRuns());
				newJob.setRuns(jobRuns);
				pendingRuns -= jobRuns;
				newJob.setCost(-1.0);
				newJob.setLicensedRuns(blueprint.getRuns());
				newJob.setProductTypeID(blueprint.getModuleTypeID());
				newJob.setStatus(10); // Define this new status
				newJob.setTimeInSeconds(part.getRunTime() * jobRuns);
				// Dates are in GMT format.
				final DateTime now = new DateTime(DateTimeZone.UTC);
				newJob.setStartDate(now.toDate());
				newJob.setEndDate(now.plus(part.getRunTime() * jobRuns * 1000).toDate());
				try {
					final Dao<Job, String> jobDao = NeoComAppConnector.getSingleton().getDBConnector().getJobDAO();
					jobDao.create(newJob);
					refPosition++;
					Log.i("EVEI", "-- JobManager.launchJob.Wrote [" + newJob + "]");
				} catch (final SQLException sqle) {
					Log.e("EVEI", "E> JobManager.launchJob.Unable to create Job [" + newJob + "]. " + sqle.getMessage());
					sqle.printStackTrace();
					pendingRuns = -1;
				}
			} catch (final RuntimeException rtex) {
				Log.e("EVEI", "E> JobManager.launchJob.Unable to create Job [" + newJob + "]. " + rtex.getMessage());
				rtex.printStackTrace();
				pendingRuns = -1;
			}
		}
		// Clear job cache.
		pilot.cleanJobs();
		Log.i("EVEI", "<< JobManager.launchJob");
	}

	//	/**
	//	 * Searches in the SDE database the blueprint type id used to invent this T2 blueprint.
	//	 *
	//	 * @param typeID
	//	 * @return
	//	 */
	//	public static int searchBlueprint4Blueprint(final int typeID) {
	//		return null;
	//	}

	public static ArrayList<Resource> searchListOfMaterials4Manufacture(final int bpid) {
		return NeoComAppConnector.getSingleton().getDBConnector().searchListOfMaterials(bpid);
	}

	/**
	 * Check the existence of this precise job process in the cache. If found it will return that instance. This
	 * will speed up most of the process because if the job is already created all the initialization code can
	 * be removed from the access and moved to the creation.
	 *
	 * @param tech
	 *
	 * @param pilot
	 * @param target
	 * @return
	 */
	private static IJobProcess checkCache(final String tech, final NeoComCharacter pilot, final NeoComBlueprint target) {
		final String jobid = tech + "." + Long.valueOf(pilot.getCharacterID()).toString() + "."
				+ Long.valueOf(target.getTypeID()).toString();
		final IJobProcess hit = JobManager.jobprocesscache.get(jobid);
		return hit;
	}
	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
