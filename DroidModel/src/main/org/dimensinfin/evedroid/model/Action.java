//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.model;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.core.EIndustryGroup;
import org.dimensinfin.evedroid.enums.ETaskCompletion;
import org.dimensinfin.evedroid.enums.ETaskType;
import org.dimensinfin.evedroid.industry.Resource;

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
public class Action extends AbstractComplexNode {
	private class TaskBundle {
		protected int			priority	= 999;
		protected EveTask	task			= null;

		public TaskBundle(final int pri, final EveTask task) {
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

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger								logger							= Logger.getLogger("EVEI-M");

	// - F I E L D - S E C T I O N ............................................................................
	private Resource										resource						= null;
	private int													requestQty					= 0;
	private int													completedQty				= 0;
	private int													lowerPriority				= 9999;
	private final ArrayList<TaskBundle>	tasksRegistered			= new ArrayList<TaskBundle>();
	private ETaskCompletion							completed						= ETaskCompletion.COMPLETED;
	private String											userPreferredAction	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Action(final Resource res) {
		resource = res;
		requestQty = resource.getQuantity();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(final Resource rs) {
		resource.setStackSize(resource.getStackSize() + rs.getStackSize());
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

	public int getTypeID() {
		return resource.getTypeID();
	}

	public EIndustryGroup getItemIndustryGroup() {
		return resource.item.getIndustryGroup();
	}

	public String getItemName() {
		return resource.item.getName();
	}

	public double getPrice() {
		return resource.item.getPrice();
	}

	public int getPriority() {
		return lowerPriority;
	}

	public int getRequestQty() {
		return requestQty;
	}

	public synchronized ArrayList<EveTask> getTasks() {
		// Order the tasks and then extract them to a list.
		Comparator<TaskBundle> orderbyPriority = new Comparator<TaskBundle>() {
			public int compare(final TaskBundle left, final TaskBundle right) {
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
	 * @param pri
	 *          priority of the task being registered.
	 * @param task
	 *          the task that completes the request or part of the request.
	 */
	public synchronized void registerTask(final int pri, final EveTask task) {
		TaskBundle bundle = new TaskBundle(pri, task);
		try {
			// REFACTOR This causes problems of aggregations not desired. test if the removal is operative.
			//			// Check for aggregation.
			//			for (TaskBundle current : tasksRegistered) {
			//				EveTask currentTask = current.task;
			//				if ((task.getTypeID() == currentTask.getTypeID()) && (task.getTaskType() == currentTask.getTaskType()))
			//					if (task.getLocation().getID() == currentTask.getLocation().getID()) {
			//						//						currentTask.setQty(currentTask.getQty() + task.getQty());
			//						return;
			//					}
			//			}
			tasksRegistered.add(bundle);
			// Update the global priority for this Action
			if (pri < lowerPriority) lowerPriority = pri;
			logger.info("-- Action.registerTask. [" + tasksRegistered.size() + "] " + task);
		} catch (RuntimeException rtex) {
			logger.severe("E> Detected Runtime Exception white registering task. " + rtex.getMessage());
			rtex.printStackTrace();
		}
	}

	public void setCompleted(final ETaskCompletion flag, final int qty) {
		completed = flag;
		if (flag == ETaskCompletion.COMPLETED) completedQty += qty;
	}

	public void setUserAction(final String userPreferredAction) {
		this.userPreferredAction = userPreferredAction;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("Action [");
		buffer.append(resource).append(" ");
		if (tasksRegistered.size() > 0) buffer.append("\n\t").append(tasksRegistered).append("\n");
		buffer.append("]");
		return buffer.toString();
	}

	private void performTask(final EveTask task, final Asset targetAsset) {
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
	 * @param pri
	 *          priority of the task being registered.
	 * @param task
	 *          the task that completes the request or part of the request.
	 * @param targetAsset
	 *          the asset used to complete the task when this action requires movement or transformation of
	 *          other resources. This is used to change the memory copy of the asset so next actions will found
	 *          an scenery similar to the one in real life and not an infinite number of resources.
	 */
	private synchronized void registerTask(final int pri, final EveTask task, final Asset targetAsset) {
		logger.info("-- Registering task request [" + pri + "] " + task);
		performTask(task, targetAsset);
		// Filter out assets already on the final location
		if (task.getTaskType() == ETaskType.MOVE) {
			if (task.getLocation().getID() != task.getDestination().getID()) registerTask(pri, task);
		} else
			registerTask(pri, task);
	}

	public Resource getResource() {
		return resource;
	}
}

// - UNUSED CODE ............................................................................................
