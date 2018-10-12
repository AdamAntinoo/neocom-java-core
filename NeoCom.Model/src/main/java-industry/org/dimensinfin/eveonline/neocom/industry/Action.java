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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComExpandableNode;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This class will receive a <code>Resource</code> and generate all the tasks to cover the quantity requested
 * with the different options like moving assets or buys orders. This will concentrate all the information
 * being retrieved during that covering activity.<br>
 * The process will be triggered during the creation of the object even that is not really recommended but the
 * other option would be to trigger the calculations when the information is asked on the model part
 * recreation.
 *
 * @author Adam Antinoo
 */
public class Action extends NeoComExpandableNode {
	public enum ETaskCompletion {
		NOTPROCESSED, COMPLETED, PARTIAL,PENDING, MARKET
	}
	public enum ETaskType {
		REACTION, REQUEST, MOVE, PRODUCE, REFINE, COPY, GET, AVAILABLE, BUILD, BUY, BUYCOVERED, SELL, RESEARCH_ME, RESEARCH_PE, INVENTION, EXTRACT
	}
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 1733515746949020056L;
	private static Logger logger = LoggerFactory.getLogger("EVEI-M");

	// - F I E L D - S E C T I O N ............................................................................
	private Resource resource = null;
	private int requestQty = 0;
	private int completedQty = 0;
	private int lowerPriority = 9999;
	private final ArrayList<TaskBundle> tasksRegistered = new ArrayList<TaskBundle>();
	private ETaskCompletion completed = ETaskCompletion.NOTPROCESSED;
	private String userPreferredAction = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Action( final Resource res ) {
		super();
		jsonClass="Action";
		resource = res;
		requestQty = resource.getQuantity();
		// Default expand state on initialization is collapsed.
		this.collapse();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	// --- IEXPANDABLE INTERFACE
	@Override
	public boolean isEmpty() {
	return	(tasksRegistered.size()>0)? true: false;
	}

	public void addResource( final Resource rs ) {
		resource.setStackSize(resource.getStackSize() + rs.getStackSize());
	}

	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		final ArrayList<ICollaboration> results = new ArrayList<ICollaboration>();
		results.addAll(this.getTasks());
		return results;
	}

	public String getCategory() {
		return resource.getCategory();
	}

	public int getCompletedQty() {
		return completedQty;
	}

	public String getGroupName() {
		return resource.getGroupName();
	}

	public EIndustryGroup getItemIndustryGroup() {
		return resource.getItem().getIndustryGroup();
	}

	public String getItemName() {
		return resource.getItem().getName();
	}

	public double getPrice() {
		return resource.getItem().getPrice();
	}

	public int getPriority() {
		return lowerPriority;
	}

	public int getRequestQty() {
		return requestQty;
	}

	public Resource getResource() {
		return resource;
	}

	public synchronized ArrayList<EveTask> getTasks() {
		// Order the tasks and then extract them to a list.
		Comparator<TaskBundle> orderbyPriority = new Comparator<TaskBundle>() {
			@Override
			public int compare( final TaskBundle left, final TaskBundle right ) {
				int leftField = left.priority;
				int rightField = right.priority;
				if (leftField == rightField) return 0;
				if (leftField < rightField)
					return -1;
				else
					return 1;
			}
		};
		Collections.sort(tasksRegistered, orderbyPriority);

		ArrayList<EveTask> result = new ArrayList<EveTask>();
		for (TaskBundle bundle : tasksRegistered) {
			result.add(bundle.task);
		}
		return result;
	}

	public int getTypeId() {
		return resource.getTypeId();
	}

	public String getUserAction() {
		return userPreferredAction;
	}

	public ETaskCompletion isCompleted() {
		return completed;
	}

	/**
	 * Get the resources used to complete totally or partially the request and add all them to the list of tasks
	 * related to this resource action. Aggregates the new task to the list of tasks. Before adding the task to
	 * the list it checks if there is a task of the same item and type to accumulate the quantities instead of
	 * generating different tasks.<br>
	 * The version of the method does not use any external assets that would have to be modified to complete the
	 * task.
	 *
	 * @param pri  priority of the task being registered.
	 * @param task the task that completes the request or part of the request.
	 */
	public synchronized void registerTask( final int pri, final EveTask task ) {
		TaskBundle bundle = new TaskBundle(pri, task);
		try {
			// REFACTOR This causes problems of aggregations not desired. test if the removal is operative.
			//			// Check for aggregation.
			//			for (TaskBundle current : tasksRegistered) {
			//				EveTask currentTask = current.task;
			//				if ((task.getTypeId() == currentTask.getTypeId()) && (task.getTaskType() == currentTask.getTaskType()))
			//					if (task.getLocation().getLocationId() == currentTask.getLocation().getLocationId()) {
			//						//						currentTask.setQty(currentTask.getQty() + task.getQty());
			//						return;
			//					}
			//			}
			tasksRegistered.add(bundle);
			// Update the global priority for this Action
			if (pri < lowerPriority) {
				lowerPriority = pri;
			}
			Action.logger.info("-- Action.registerTask. [" + tasksRegistered.size() + "] " + task);
		} catch (RuntimeException rtex) {
			Action.logger.error("E> Detected Runtime Exception white registering task. " + rtex.getMessage());
			rtex.printStackTrace();
		}
	}

	public void setCompleted( final ETaskCompletion flag, final int qty ) {
		completed = flag;
		if (flag == ETaskCompletion.COMPLETED) {
			completedQty += qty;
		}
	}

	public void setUserAction( final String userPreferredAction ) {
		this.userPreferredAction = userPreferredAction;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Action [");
		buffer.append(resource).append(" ");
		if (tasksRegistered.size() > 0) {
			buffer.append("\n\t").append(tasksRegistered).append("\n");
		}
		buffer.append("]");
		return buffer.toString();
	}

	private void performTask( final EveTask task, final NeoComAsset targetAsset ) {
		ETaskType type = task.getTaskType();
		switch (type) {
			case MOVE:
				targetAsset.setQuantity(targetAsset.getQuantity() - task.getQty());
				break;
		}
	}

	/**
	 * Get the resources used to complete totally or partially the request and add all them to the list of tasks
	 * related to this resource action. This addition will aggregate to any other task for the same resource and
	 * type and also will change the priority level of the Action.
	 *
	 * @param pri         priority of the task being registered.
	 * @param task        the task that completes the request or part of the request.
	 * @param targetAsset the asset used to complete the task when this action requires movement or transformation of
	 *                    other resources. This is used to change the memory copy of the asset so next actions will found
	 *                    an scenery similar to the one in real life and not an infinite number of resources.
	 */
	private synchronized void registerTask( final int pri, final EveTask task, final NeoComAsset targetAsset ) {
		Action.logger.info("-- Registering task request [" + pri + "] " + task);
		this.performTask(task, targetAsset);
		// Filter out assets already on the final location
		if (task.getTaskType() == ETaskType.MOVE) {
			if (task.getLocation().getId() != task.getDestination().getId()) {
				this.registerTask(pri, task);
			}
		} else {
			this.registerTask(pri, task);
		}
	}

	private static class TaskBundle implements Serializable {
		private static final long serialVersionUID = -5450309773660347151L;
		protected int priority = 999;
		protected EveTask task = null;

		public TaskBundle( final int pri, final EveTask task ) {
			priority = pri;
			this.task = task;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer("TaskManager.TaskBundle [");
			buffer.append("[").append(priority).append("] ").append(task);
			buffer.append(" ]");
			return buffer.toString();
		}
	}

}

// - UNUSED CODE ............................................................................................
