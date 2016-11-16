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
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.industry.AbstractManufactureProcess;
import org.dimensinfin.evedroid.industry.Resource;
import org.dimensinfin.evedroid.manager.AssetsManager;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class Fitting extends AbstractManufactureProcess implements INodeModel {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger						logger		= Logger.getLogger("org.dimensinfin.evedroid.model");

	// - F I E L D - S E C T I O N ............................................................................
	private final Asset							hull			= null;
	private final Vector<Resource>	contents	= new Vector<Resource>();
	private final int								runs			= 1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Fitting(final AssetsManager manager) {
		super(manager);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		for (Action node : getManufacturingResources()) {
			result.add(node);
		}
		return result;
	}

	public ArrayList<Action> getManufacturingResources() {
		Vector<Resource> requirements = new Vector<Resource>(8 * 4);
		requirements.add(new Resource(hull.getTypeID(), runs));
		// Copy the resources and do not use the original list because this is going to be changed on the process
		for (Resource r : contents) {
			requirements.add(new Resource(r.getTypeID(), r.getQuantity()));
		}
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
		Log.i("EVEI", "-- Fitting.getManufacturingResources.List of requirements" + requirements);
		pointer = -1;
		try {
			do {
				pointer++;
				Resource resource = requirements.get(pointer);
				Log.i("EVEI", "-- Fitting.getManufacturingResources.Processing resource " + resource);
				// Check resources that are Skills. Give them an special treatment.
				//				if (resource.getCategory().equalsIgnoreCase(ModelWideConstants.eveglobal.Skill)) {
				//					currentAction = new Skill(resource);
				//					registerAction(currentAction);
				//					continue;
				//				}
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

	@Override
	protected ArrayList<Action> getActions() {
		final ArrayList<Action> result = new ArrayList<Action>();
		for (final Action action : this.actionsRegistered.values()) {
			result.add(action);
		}
		return result;
	}
}

// - UNUSED CODE ............................................................................................
