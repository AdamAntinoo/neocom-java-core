//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.INodeModel;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.EPropertyTypes;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.industry.AbstractManufactureProcess;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.manager.AssetsManager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class Fitting extends AbstractManufactureProcess implements INodeModel {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long				serialVersionUID	= 6740483226926234807L;
	private static Logger						logger						= Logger.getLogger("Fitting");

	// - F I E L D - S E C T I O N ............................................................................
	private String									name							= "-FIT-";
	private Resource								hull							= null;
	private final Vector<Resource>	modules						= new Vector<Resource>();
	private final Vector<Resource>	cargo							= new Vector<Resource>();
	private final Vector<Resource>	rigs							= new Vector<Resource>();
	//	private final int								runs		= 1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Initializes a fitting. A Fitting is a complex objects that performs manufactuting actions and that should
	 * have a reference to the current selected pilot because the resources required for manufacturing are
	 * associated with a pilot.
	 * 
	 * @param manager
	 */
	public Fitting(final AssetsManager manager) {
		super(manager);
		// Copy the manager pilot to the local pilot reference.
		pilot = manager.getPilot();
		runs = 1;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Adds items to the cargo hold of the ship fitting. Tis is used for charges, ammo and other fit items like
	 * scripts.
	 * 
	 * @param itemId
	 * @param times
	 */
	public void addCargo(final int itemId, int times) {
		if (times < 1) {
			times = 1;
		}
		cargo.add(new Resource(itemId, times));
	}

	/**
	 * Receives the hull item id that should match to a ship item type.
	 * 
	 * @param hullTypeId
	 */
	public void addHull(final int hullTypeId) {
		// Translate the if to an eve type.
		hull = new Resource(hullTypeId);
	}

	/**
	 * Main model creation method. This should return all the elements that are below the Fitting on the data
	 * model hierarchy. The contents of that list are the list of Resources (if the variant is the
	 * FITTING_MODULES) of the list of manufacturing actions if the variant is the FITTING_MANUFACTURE. <br>
	 * The list of Actions is created following the Industry Manufacture processor that will take on account the
	 * preferred user action for each item(BUY, MANUFACTURE, INVENT,etc).
	 * 
	 * @param variant
	 * @return
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		if (AppWideConstants.EFragment.valueOf(variant) == AppWideConstants.EFragment.FITTING_MANUFACTURE) {
			// Copy the list of actions to the result.
			for (Action node : getManufacturingResources()) {
				result.add(node);
			}
		}
		return result;
	}

	public void fitModule(final int moduleId) {
		this.fitModule(moduleId, 1);
	}

	/**
	 * Adds the selected module to the fit the number of times specified. By default it add the module once.
	 * 
	 * @param moduleId
	 * @param times
	 */
	public void fitModule(final int moduleId, int times) {
		if (times < 1) {
			times = 1;
		}
		modules.add(new Resource(moduleId, times));
	}

	/**
	 * Just the same to fit a module. Add a resource of quantity 1 to the list of rigs.
	 * 
	 * @param rigTypeId
	 */
	public void fitRig(final int rigTypeId) {
		rigs.add(new Resource(rigTypeId));
	}

	public String getName() {
		return name;
	}

	public void setName(final String newName) {
		name = newName;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Fitting [");
		buffer.append("Hull: ").append(hull);
		buffer.append("Modules: ").append(modules);
		buffer.append(super.toString()).append("]");
		return buffer.toString();
	}

	@Override
	protected ArrayList<Action> getActions() {
		final ArrayList<Action> result = new ArrayList<Action>();
		for (final Action action : actionsRegistered.values()) {
			result.add(action);
		}
		return result;
	}

	private ArrayList<Property> accessLocationRoles() {
		ArrayList<Property> roleList = new ArrayList<Property>();
		try {
			Dao<Property, String> propertyDao = AppConnector.getDBConnector().getPropertyDAO();
			QueryBuilder<Property, String> queryBuilder = propertyDao.queryBuilder();
			Where<Property, String> where = queryBuilder.where();
			where.eq("ownerID", getPilot().getCharacterID());
			where.and();
			where.eq("propertyType", EPropertyTypes.LOCATIONROLE);
			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();
			roleList = new ArrayList<Property>(propertyDao.query(preparedQuery));
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
		}
		return roleList;
	}

	/**
	 * THis function will replace the Character one on a near future. For this instance I will improve it to get
	 * the right Location role type.
	 */
	private EveLocation getLocation4Role(final EPropertyTypes matchingRole, final String locationType) {
		ArrayList<Property> locationRoles = accessLocationRoles();
		for (Property role : locationRoles) {
			//			String value = role.getPropertyType().name();
			if (role.getPropertyType() == matchingRole) {
				// Search for the location type we need. This is the FITTING place
				if (role.getPropertyValue().equalsIgnoreCase(locationType))
					return AppConnector.getDBConnector().searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
			}
			//				return AppConnector.getDBConnector().searchLocationbyID(Double.valueOf(role.getNumericValue()).longValue());
			//		Property currentRole = locationRoles.get(locID);
			//			if (matchingRole.equalsIgnoreCase(currentRole.getStringValue()))
			//				return AppConnector.getDBConnector().searchLocationbyID(locID);
		}
		return null;
	}

	/**
	 * This is the method that constructs the list of actions and resources required to complete the manufacture
	 * request. The process is a recursive and iterative process using the user item preferences for each item
	 * processing and dependency management.
	 * 
	 * @return
	 */
	private ArrayList<Action> getManufacturingResources() {
		logger.info(">> Fitting.getManufacturingResources");
		// Initialize models.
		// Set the location where to setup the manufacturing jobs. Detects if assets should move.
		// Manufacturing location set to the predefined location and defaults to current pilot location.
		manufactureLocation = getLocation4Role(EPropertyTypes.LOCATIONROLE, "FITTING");
		if (null == manufactureLocation) {
			manufactureLocation = pilot.getDefaultLocation();
		}
		region = manufactureLocation.getRegion();
		actions4Item = pilot.getActions();
		// Clear structures to be sure we have the right data.
		requirements.clear();
		actionsRegistered.clear();
		// Get the resources needed for the completion of this job.
		runs = 1;
		threads = 1;

		// Copy the fits contents to the list of requirements to start the processing.
		// TODO This point should be optimized to reuse resources from other iterations so the models will be cached.
		requirements.clear();
		requirements.add(new Resource(hull.getTypeID(), runs));
		// Copy the resources and do not use the original list because this is going to be changed on the process
		for (Resource r : modules) {
			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		}
		for (Resource r : cargo) {
			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		}
		//		for (Resource r : this.rigs) {
		//			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		//		}
		// Update the resource count depending on the sizing requirements for the job.
		for (Resource resource : requirements) {
			// Skills are treated differently.
			if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				resource.setStackSize(1);
			} else {
				resource.setAdaptiveStackSize(runs);
			}
			//	// If the resource being processed is the job blueprint reduce the number of runs and set the counter.
			//	if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			//		resource.setStackSize(threads);
			//	}
		}
		// Resource list completed. Dump report to the log and start action processing.
		Log.i("EVEI", "-- [Fitting.getManufacturingResources]-List of requirements > " + requirements);
		pointer = -1;
		try {
			do {
				pointer++;
				Resource resource = requirements.get(pointer);
				Log.i("EVEI", "-- [Fitting.getManufacturingResources]-Processing > " + resource);
				// Check resources that are Skills. Give them an special treatment.
				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
					currentAction = new Skill(resource);
					registerAction(currentAction);
					continue;
				}
				currentAction = new Action(resource);
				EveTask newTask = new EveTask(ETaskType.REQUEST, resource);
				newTask.setQty(resource.getQuantity());
				// We register the action before to get erased on restarts.
				// This has no impact on data since we use pointers to the global structures.
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

	//	private void test() {
	//		Log.i("EVEI", ">> T2ManufactureProcess.generateActions4Blueprint.");
	//		// Initialize global structures.
	//		manufactureLocation = blueprint.getLocation();
	//		region = manufactureLocation.getRegion();
	//		actions4Item = pilot.getActions();
	//		// Clear structures to be sure we have the right data.
	//		requirements.clear();
	//		actionsRegistered.clear();
	//		// Get the resources needed for the completion of this job.
	//		runs = blueprint.getRuns();
	//		threads = blueprint.getQuantity();
	//		// Copy the LOM received to not modify the original data during the job
	//		// processing.
	//		for (Resource r : getLOM()) {
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
	//					registerAction(currentAction);
	//					continue;
	//				}
	//				currentAction = new Action(resource);
	//				EveTask newTask = new EveTask(ETaskType.REQUEST, resource);
	//				newTask.setQty(resource.getQuantity());
	//				// We register the action before to get erased on restarts.
	//				// This has no impact on data since we use pointers to the
	//				// global structures.
	//				registerAction(currentAction);
	//				processRequest(newTask);
	//			} while (pointer < (requirements.size() - 1));
	//		} catch (RuntimeException rtex) {
	//			Log.e("RTEXCEPTION.CODE",
	//					"RT> T2ManufactureProcess.generateActions4Blueprint - Unexpected code behaviour. See stacktrace.");
	//			rtex.printStackTrace();
	//		}
	//		Log.i("EVEI", "<< T2ManufactureProcess.generateActions4Blueprint.");
	//		return getActions();
	//	}
}

// - UNUSED CODE ............................................................................................
