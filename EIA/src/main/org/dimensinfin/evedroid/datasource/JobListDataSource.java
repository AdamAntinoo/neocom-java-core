//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.datasource;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.model.Job;
import org.dimensinfin.evedroid.model.JobQueue;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.JobPart;
import org.dimensinfin.evedroid.part.QueueAnalyticsPart;
import org.dimensinfin.evedroid.part.QueuePart;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This DataSource will render the list of jobs that are defined for the current character. This list is
 * divided into two parts. The first one is the list of available queues and the jobs that are running on then
 * so the user will have a graphical representation of the time span used.<br>
 * Then it will follow the list of scheduled jobs, then the active jobs and finally the already completed
 * jobs.<br>
 * There are two flavors for the result of this DS. Depending on the fragment id the result may be the list of
 * active queues or the list of scheduled and historic jobs.
 * 
 * @author Adam Antinoo
 */
public class JobListDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long											serialVersionUID	= -199422577570222082L;

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore													_store						= null;
	private int																		_activityFilter		= ModelWideConstants.activities.MANUFACTURING;
	private DateTime															earliestEndTime		= new DateTime(DateTimeZone.UTC);
	private final ArrayList<AbstractAndroidPart>	headerContents		= new ArrayList<AbstractAndroidPart>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobListDataSource(final AppModelStore store) {
		if (null != store) {
			_store = store;
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Reads the list of jobs from the characters data structures and then process them to generate the
	 * controller parts that will represent the hierarchy. The first part of the list are the queues being used.
	 * <br>
	 * This list will add a Progress for each available queue. A character can have up to 10 manufacturing
	 * queues and another set of 10 Invention queues. I will only represent the queues in use or fro the jobs
	 * completed but still not delivered. The rest of the queues not used will be shown on the header as simple
	 * counters.
	 */
	@Override
	public void createContentHierarchy() {
		Log.i("DS", ">> JobListDataSource.createContentHierarchy");
		super.createContentHierarchy();

		// Get the character's jobs.
		ArrayList<Job> jobs = filterOutActivity(_store.getPilot().getIndustryJobs());
		ArrayList<JobQueue> queues = generateQueues(jobs);
		Log.i("EVEI", "-- JobListDataSource.createContentHierarchy - jobs count " + jobs.size());
		Log.i("EVEI", "-- JobListDataSource.createContentHierarchy - queues count " + queues.size());
		// Create the groups for the jobs.
		AbstractAndroidPart scheduledJobGroup = (AbstractAndroidPart) new GroupPart(new Separator("SCHEDULED JOBS"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPJOBSTATE);
		AbstractAndroidPart activeJobGroup = (AbstractAndroidPart) new GroupPart(new Separator("RUNNING JOBS"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPJOBSTATE);
		AbstractAndroidPart deliveryGroup = (AbstractAndroidPart) new GroupPart(new Separator("DELIVERY READY"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPJOBSTATE);
		AbstractAndroidPart completedJobGroup = (AbstractAndroidPart) new GroupPart(new Separator("COMPLETED JOBS"))
				.setRenderMode(AppWideConstants.rendermodes.RENDER_GROUPJOBSTATE);
		_root.add(scheduledJobGroup);
		_root.add(activeJobGroup);
		_root.add(deliveryGroup);
		_root.add(completedJobGroup);

		// Get the queue status information.
		int maxMan = _store.getPilot().calculateManufactureQueues();
		int maxInv = _store.getPilot().calculateInventionQueues();
		int queueManufacture = 0;
		int queueInvention = 0;
		for (Job currentjob : jobs) {
			JobPart jobpart = (JobPart) new JobPart(currentjob).setRenderMode(AppWideConstants.rendermodes.RENDER_JOB4LIST);
			// Add to the corresponding group.
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.ACTIVE) {
				activeJobGroup.addChild(jobpart);
				if (currentjob.getActivityID() == ModelWideConstants.activities.MANUFACTURING) {
					queueManufacture++;
				} else {
					queueInvention++;
				}
			}
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.SCHEDULED) {
				continue;
			}
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.READY) {
				deliveryGroup.addChild(jobpart);
			}
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.DELIVERED) {
				completedJobGroup.addChild(jobpart);
			}
		}
		for (Job currentjob : jobs) {
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.SCHEDULED) {
				JobPart jobpart = (JobPart) new JobPart(currentjob).setRenderMode(AppWideConstants.rendermodes.RENDER_JOB4LIST);
				// Adapt the scheduled if there are free queues.
				if (currentjob.getActivityID() == ModelWideConstants.activities.MANUFACTURING) {
					if (queueManufacture < maxMan) {
						jobpart.setLaunchable(true);
						jobpart.setFirstStartTime(earliestEndTime);
					}
				} else {
					if (queueInvention < maxInv) {
						jobpart.setLaunchable(true);
						jobpart.setFirstStartTime(earliestEndTime);
					}
				}
				scheduledJobGroup.addChild(jobpart);
			}
		}
		//		}
		Log.i("DS", "<< JobListDataSource.createContentHierarchy");
	}

	public int getActivityFilter() {
		return _activityFilter;
	}

	public ArrayList<AbstractAndroidPart> getHeaderPartHierarchy() {
		createHeaderContents();
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractAndroidPart node : headerContents) {
			result.add(node);
			// Check if the node is expanded. Then add its children.
			if (node.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
				result.addAll(grand);
			}
		}
		return result;
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		//		if (_flavor == AppWideConstants.fragment.FRAGMENT_QUEUESHEADER) return super.getPartHierarchy();
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		//		if (_flavor == AppWideConstants.fragment.FRAGMENT_JOBLISTBODY) {
		for (AbstractAndroidPart node : _root) {
			ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
			// Order jobs by end date.
			Collections.sort(grand, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NEWESTDATESORT));
			if (grand.size() > 0) {
				result.add(node);
				result.addAll(grand);
			}
			//			}
		}
		//		}
		_adapterData = result;
		return result;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}

	public void setActivityFilter(final int _activityFilter) {
		this._activityFilter = _activityFilter;
	}

	private void createHeaderContents() {
		// Get the character's jobs.
		ArrayList<Job> jobs = filterOutActivity(_store.getPilot().getIndustryJobs());
		ArrayList<JobQueue> queues = generateQueues(jobs);
		Log.i("EVEI", "-- JobListDataSource.createContentHierarchy - jobs count " + jobs.size());
		Log.i("EVEI", "-- JobListDataSource.createContentHierarchy - queues count " + queues.size());

		QueueAnalyticsPart queueAnalytics = new QueueAnalyticsPart(new Separator("QUEUES IN USE"));
		queueAnalytics.setJobActivity(_activityFilter);
		headerContents.add(queueAnalytics);
		Collections.sort(queues, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_TIMEPENDING));
		// There is a min number and a max number, the last one depending on skills trained.
		int maxMan = _store.getPilot().calculateManufactureQueues();
		int maxInv = _store.getPilot().calculateInventionQueues();
		int queueCounter = 1;
		int mnum = 0;
		int inum = 0;
		for (JobQueue jobQueue : queues) {
			// Separate Manufacturing (activity=1) from the rest of jobs.
			QueuePart qp = new QueuePart(jobQueue);
			// Check if the queue represents a ready job.
			if (qp.isQueueActive()) {
				qp.setNumber(queueCounter);
				headerContents.add(qp);
				queueCounter++;
				// Add the counter to the right queue type.
				if (jobQueue.getJob().getActivityID() == ModelWideConstants.activities.MANUFACTURING) {
					mnum++;
				} else {
					inum++;
				}
			}
		}
		queueAnalytics.setLimits(maxMan, maxInv);
		queueAnalytics.setValues(mnum, inum);
	}

	/**
	 * Remove from the output the jobs that do not fit on the activity set.
	 * 
	 * @param industryJobs
	 * @return
	 */
	private ArrayList<Job> filterOutActivity(final ArrayList<Job> industryJobs) {
		ArrayList<Job> result = new ArrayList<Job>();
		for (Job job : industryJobs) {
			if (_activityFilter == ModelWideConstants.activities.MANUFACTURING) {
				if (job.getActivityID() == ModelWideConstants.activities.MANUFACTURING) {
					result.add(job);
				}
			} else if (job.getActivityID() != ModelWideConstants.activities.MANUFACTURING) {
				result.add(job);
			}
		}
		return result;
	}

	/**
	 * Generates the list of job queue usage for the active jobs. Queues represent the next 36 hours of time and
	 * the used time for the active jobs. The ordering are the most used queues at the higher positions.
	 * 
	 * @param jobs
	 */
	private ArrayList<JobQueue> generateQueues(final ArrayList<Job> jobs) {
		ArrayList<JobQueue> queues = new ArrayList<JobQueue>();
		for (Job job : jobs) {
			if ((job.getStatus() == ModelWideConstants.jobstatus.ACTIVE)
					|| (job.getStatus() == ModelWideConstants.jobstatus.READY)) {
				// The job is running and using a queue or still pending for delivery.
				JobQueue queue = new JobQueue(job);
				queue.setJob(job);
				queues.add(queue);
				// Update the earliest end time
				if (earliestEndTime.isAfter(new DateTime(job.getEndDate()))) {
					earliestEndTime = new DateTime(job.getEndDate());
				}
			}
		}
		return queues;
	}
}

// - UNUSED CODE ............................................................................................
