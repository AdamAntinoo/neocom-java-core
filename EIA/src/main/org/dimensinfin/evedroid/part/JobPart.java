//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.sql.SQLException;
import java.util.Date;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.core.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.core.IDateTimeComparator;
import org.dimensinfin.evedroid.core.INamedPart;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.Job;
import org.dimensinfin.evedroid.render.JobRender;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobPart extends EveAbstractPart implements INamedPart, IMenuActionTarget, IDateTimeComparator {
	// - S T A T I C - S E C T I O N
	// ..........................................................................
	private static final long serialVersionUID = -216430970914075462L;

	// - F I E L D - S E C T I O N
	// ............................................................................
	private boolean canBeLaunched = false;
	private DateTime newStartTime = new DateTime(DateTimeZone.UTC);

	// - C O N S T R U C T O R - S E C T I O N
	// ................................................................
	public JobPart(final AbstractGEFNode job) {
		super(job);
	}

	public boolean canBeLaunched() {
		return canBeLaunched;
	}

	// - M E T H O D - S E C T I O N
	// ..........................................................................
	@Override
	public boolean expand() {
		expanded = !expanded;
		return expanded;
	}

	public String get_jobDuration() {
		return generateTimeString(getCastedModel().getTimeInSeconds() * 1000);
	}

	public String get_jobEnd() {
		return generateDateString(getCastedModel().getEndDate().getTime());
	}

	public Spanned get_jobLocation() {
		StringBuffer htmlLocation = new StringBuffer();
		EveLocation loc = AppConnector.getDBConnector().searchLocationbyID(getCastedModel().getBlueprintLocationID());
		String security = loc.getSecurity();
		String secColor = securityLevels.get(security);
		if (null == secColor) {
			secColor = "#2FEFEF";
		}
		// Append the Region -> system
		htmlLocation.append(loc.getRegion()).append(AppWideConstants.FLOW_ARROW_RIGHT).append(loc.getConstellation())
				.append(AppWideConstants.FLOW_ARROW_RIGHT);
		htmlLocation.append("<font color='").append(secColor).append("'>")
				.append(securityFormatter.format(loc.getSecurityValue())).append("</font>");
		htmlLocation.append(" ").append(loc.getStation());
		return Html.fromHtml(htmlLocation.toString());
	}

	public String get_jobModule() {
		return getCastedModel().getModuleName();
	}

	public String get_jobStart() {
		return generateDateString(getCastedModel().getStartDate().getTime());
	}

	public String get_runCount() {
		return new Long(getCastedModel().getRuns()).toString();
	}

	public Job getCastedModel() {
		return (Job) getModel();
	}

	public Date getEndDate() {
		return getCastedModel().getEndDate();
	}

	public DateTime getFirstStartTime() {
		return newStartTime;
	}

	public int getJobActivity() {
		return getCastedModel().getActivityID();
	}

	public String getJobClass() {
		return getCastedModel().getJobType();
	}

	public double getJobCost() {
		return getCastedModel().getCost();
	}

	/**
	 * Return the real location id of the station where the job is located. On
	 * the CCP api the information about the job location is on some fields. The
	 * one that relates to us is the stationID. When the app creates virtual
	 * jobs it should set the station to the station where the blueprint is
	 * located and not the container for input or output that can be used on the
	 * new interface.
	 * 
	 * @return
	 */
	public EveLocation getJobLocation() {
		return getCastedModel().getJobLocation();
	}

	public int getJobStatus() {
		return getCastedModel().getStatus();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getBlueprintID();
	}

	public String getName() {
		return getCastedModel().getBlueprintName();
	}

	public int getRuns() {
		return getCastedModel().getRuns();
	}

	public Date getStartDate() {
		return getCastedModel().getStartDate();
	}

	public int getTimeInSeconds() {
		return getCastedModel().getTimeInSeconds();
	}

	public void onClick(final View target) {
		Log.i("EVEI", ">> JobPart.onClick");
		// Toggle location to show its contents.
		toggleExpanded();
		fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
		Log.i("EVEI", "<< JobPart.onClick");
	}

	/**
	 * Deleted the job from the database. Usually because of the user request or
	 * also can be fired when the app detects the launch of the same job in the
	 * game data.
	 */
	public boolean onContextItemSelected(final MenuItem item) {
		try {
			AppConnector.getDBConnector().getJobDAO().delete(getCastedModel());
			// Clear the cache in memory
			getPilot().cleanJobs();
			fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH, this, this);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return true;
	}

	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("EVEI", ">> JobPart.onCreateContextMenu");
		// Create the menu only for user generated jobs.
		if (!getCastedModel().getJobType().equalsIgnoreCase("CCP")) {
			getActivity().getMenuInflater().inflate(R.menu.jobactions_menu, menu);
		}
		Log.i("EVEI", "<< JobPart.onCreateContextMenu");
	}

	public void setFirstStartTime(final DateTime earliestEndTime) {
		newStartTime = earliestEndTime;
	}

	public void setLaunchable(final boolean state) {
		canBeLaunched = state;
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_JOB4LIST)
			return new JobRender(this, _activity);
		return new JobRender(this, _activity);
	}

	public DateTime getComparableDate() {
		return new DateTime(getCastedModel().getEndDate());
	}
}

// - UNUSED CODE
// ............................................................................................
