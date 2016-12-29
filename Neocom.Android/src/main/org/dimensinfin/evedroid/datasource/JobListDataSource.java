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
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.part.JobPart;
import org.dimensinfin.evedroid.part.QueueAnalyticsPart;
import org.dimensinfin.evedroid.part.QueuePart;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.Job;
import org.dimensinfin.eveonline.neocom.model.JobQueue;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Pilot;
import org.dimensinfin.eveonline.neocom.model.Separator;
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
		if (null != store) _store = store;
	}

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
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
		ArrayList<Job> jobs = this.filterOutActivity(_store.getPilot().getIndustryJobs());
		ArrayList<JobQueue> queues = this.generateQueues(jobs);
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
		// Check that the character is a pilot and then get the number of queues from the skills trained
		NeoComCharacter pilot = _store.getPilot();
		int maxMan = 0;
		int maxInv = 0;
		if (pilot instanceof Pilot) {
			maxMan = ((Pilot) pilot).calculateManufactureQueues();
			maxInv = ((Pilot) pilot).calculateInventionQueues();
		}
		int queueManufacture = 0;
		int queueInvention = 0;
		for (Job currentjob : jobs) {
			JobPart jobpart = (JobPart) new JobPart(currentjob).setRenderMode(AppWideConstants.rendermodes.RENDER_JOB4LIST);
			// Add to the corresponding group.
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.ACTIVE) {
				activeJobGroup.addChild(jobpart);
				if (currentjob.getActivityID() == ModelWideConstants.activities.MANUFACTURING)
					queueManufacture++;
				else
					queueInvention++;
			}
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.SCHEDULED) continue;
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.READY) deliveryGroup.addChild(jobpart);
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.DELIVERED) completedJobGroup.addChild(jobpart);
		}
		for (Job currentjob : jobs)
			if (currentjob.getStatus() == ModelWideConstants.jobstatus.SCHEDULED) {
				JobPart jobpart = (JobPart) new JobPart(currentjob).setRenderMode(AppWideConstants.rendermodes.RENDER_JOB4LIST);
				// Adapt the scheduled if there are free queues.
				if (currentjob.getActivityID() == ModelWideConstants.activities.MANUFACTURING) {
					if (queueManufacture < maxMan) {
						jobpart.setLaunchable(true);
						jobpart.setFirstStartTime(earliestEndTime);
					}
				} else if (queueInvention < maxInv) {
					jobpart.setLaunchable(true);
					jobpart.setFirstStartTime(earliestEndTime);
				}
				scheduledJobGroup.addChild(jobpart);
			}
		//		}
		Log.i("DS", "<< JobListDataSource.createContentHierarchy");
	}

	public int getActivityFilter() {
		return _activityFilter;
	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		//		if (_flavor == AppWideConstants.fragment.FRAGMENT_QUEUESHEADER) return super.getPartHierarchy();
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		//		if (_flavor == AppWideConstants.fragment.FRAGMENT_JOBLISTBODY) {
		for (AbstractAndroidPart node : _root) {
			ArrayList<IPart> grand = node.collaborate2View();
			// Order jobs by end date.
			Collections.sort(grand, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NEWESTDATESORT));
			if (grand.size() > 0) {
				result.add(node);
				for (IPart part : grand)
					result.add((AbstractAndroidPart) part);
			}
		}
		_adapterData = result;
		return result;
	}

	public ArrayList<AbstractAndroidPart> getHeaderPartHierarchy() {
		this.createHeaderContents();
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractAndroidPart node : headerContents) {
			result.add(node);
			// Check if the node is expanded. Then add its children.
			if (node.isExpanded()) for (IPart part : node.collaborate2View())
				result.add((AbstractAndroidPart) part);
		}
		return result;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		if (event.getPropertyName().equalsIgnoreCase(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
	}

	public void setActivityFilter(final int _activityFilter) {
		this._activityFilter = _activityFilter;
	}

	private void createHeaderContents() {
		// Get the character's jobs.
		ArrayList<Job> jobs = this.filterOutActivity(_store.getPilot().getIndustryJobs());
		ArrayList<JobQueue> queues = this.generateQueues(jobs);
		Log.i("EVEI", "-- JobListDataSource.createContentHierarchy - jobs count " + jobs.size());
		Log.i("EVEI", "-- JobListDataSource.createContentHierarchy - queues count " + queues.size());

		QueueAnalyticsPart queueAnalytics = new QueueAnalyticsPart(new Separator("QUEUES IN USE"));
		queueAnalytics.setJobActivity(_activityFilter);
		headerContents.add(queueAnalytics);
		Collections.sort(queues, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_TIMEPENDING));
		// There is a min number and a max number, the last one depending on skills trained.
		// Check that the character is a pilot and then get the number of queues from the skills trained
		NeoComCharacter pilot = _store.getPilot();
		int maxMan = 0;
		int maxInv = 0;
		if (pilot instanceof Pilot) {
			maxMan = ((Pilot) pilot).calculateManufactureQueues();
			maxInv = ((Pilot) pilot).calculateInventionQueues();
		}
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
				if (jobQueue.getJob().getActivityID() == ModelWideConstants.activities.MANUFACTURING)
					mnum++;
				else
					inum++;
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
		for (Job job : industryJobs)
			if (_activityFilter == ModelWideConstants.activities.MANUFACTURING) {
				if (job.getActivityID() == ModelWideConstants.activities.MANUFACTURING) result.add(job);
			} else if (job.getActivityID() != ModelWideConstants.activities.MANUFACTURING) result.add(job);
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
		for (Job job : jobs)
			if ((job.getStatus() == ModelWideConstants.jobstatus.ACTIVE)
					|| (job.getStatus() == ModelWideConstants.jobstatus.READY)) {
				// The job is running and using a queue or still pending for delivery.
				JobQueue queue = new JobQueue(job);
				queue.setJob(job);
				queues.add(queue);
				// Update the earliest end time
				if (earliestEndTime.isAfter(new DateTime(job.getEndDate()))) earliestEndTime = new DateTime(job.getEndDate());
			}
		return queues;
	}
}

// - UNUSED CODE ............................................................................................
