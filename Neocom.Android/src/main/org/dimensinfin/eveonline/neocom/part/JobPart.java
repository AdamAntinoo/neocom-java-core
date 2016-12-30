//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.part;

// - IMPORT SECTION .........................................................................................
import java.sql.SQLException;
import java.util.Date;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IMenuActionTarget;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.interfaces.IDateTimeComparator;
import org.dimensinfin.eveonline.neocom.interfaces.INamedPart;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.Job;
import org.dimensinfin.eveonline.neocom.render.JobRender;
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
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -216430970914075462L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean						canBeLaunched			= false;
	private DateTime					newStartTime			= new DateTime(DateTimeZone.UTC);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobPart(final AbstractComplexNode job) {
		super(job);
	}

	public boolean canBeLaunched() {
		return canBeLaunched;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_jobDuration() {
		return EveAbstractPart.generateTimeString(this.getCastedModel().getTimeInSeconds() * 1000);
	}

	public String get_jobEnd() {
		return this.generateDateString(this.getCastedModel().getEndDate().getTime());
	}

	public Spanned get_jobLocation() {
		StringBuffer htmlLocation = new StringBuffer();
		EveLocation loc = AppConnector.getDBConnector().searchLocationbyID(this.getCastedModel().getBlueprintLocationID());
		String security = loc.getSecurity();
		String secColor = EveAbstractPart.securityLevels.get(security);
		if (null == secColor) secColor = "#2FEFEF";
		// Append the Region -> system
		htmlLocation.append(loc.getRegion()).append(AppWideConstants.FLOW_ARROW_RIGHT).append(loc.getConstellation())
				.append(AppWideConstants.FLOW_ARROW_RIGHT);
		htmlLocation.append("<font color='").append(secColor).append("'>")
				.append(EveAbstractPart.securityFormatter.format(loc.getSecurityValue())).append("</font>");
		htmlLocation.append(" ").append(loc.getStation());
		return Html.fromHtml(htmlLocation.toString());
	}

	public String get_jobModule() {
		return this.getCastedModel().getModuleName();
	}

	public String get_jobStart() {
		return this.generateDateString(this.getCastedModel().getStartDate().getTime());
	}

	public String get_runCount() {
		return new Long(this.getCastedModel().getRuns()).toString();
	}

	public Job getCastedModel() {
		return (Job) this.getModel();
	}

	public DateTime getComparableDate() {
		return new DateTime(this.getCastedModel().getEndDate());
	}

	public Date getEndDate() {
		return this.getCastedModel().getEndDate();
	}

	public DateTime getFirstStartTime() {
		return newStartTime;
	}

	public int getJobActivity() {
		return this.getCastedModel().getActivityID();
	}

	public String getJobClass() {
		return this.getCastedModel().getJobType();
	}

	public double getJobCost() {
		return this.getCastedModel().getCost();
	}

	/**
	 * Return the real location id of the station where the job is located. On the CCP api the information about
	 * the job location is on some fields. The one that relates to us is the stationID. When the app creates
	 * virtual jobs it should set the station to the station where the blueprint is located and not the
	 * container for input or output that can be used on the new interface.
	 * 
	 * @return
	 */
	public EveLocation getJobLocation() {
		return this.getCastedModel().getJobLocation();
	}

	public int getJobStatus() {
		return this.getCastedModel().getStatus();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getBlueprintID();
	}

	public String getName() {
		return this.getCastedModel().getBlueprintName();
	}

	public int getRuns() {
		return this.getCastedModel().getRuns();
	}

	public Date getStartDate() {
		return this.getCastedModel().getStartDate();
	}

	public int getTimeInSeconds() {
		return this.getCastedModel().getTimeInSeconds();
	}

	public void onClick(final View target) {
		Log.i("EVEI", ">> JobPart.onClick");
		// Toggle location to show its contents.
		this.toggleExpanded();
		this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
		Log.i("EVEI", "<< JobPart.onClick");
	}

	/**
	 * Deleted the job from the database. Usually because of the user request or also can be fired when the app
	 * detects the launch of the same job in the game data.
	 */
	public boolean onContextItemSelected(final MenuItem item) {
		try {
			AppConnector.getDBConnector().getJobDAO().delete(this.getCastedModel());
			// Clear the cache in memory
			this.getPilot().cleanJobs();
			this.fireStructureChange(AppWideConstants.events.EVENTSTRUCTURE_NEEDSREFRESH, this, this);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return true;
	}

	public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenuInfo menuInfo) {
		Log.i("EVEI", ">> JobPart.onCreateContextMenu");
		// Create the menu only for user generated jobs.
		if (!this.getCastedModel().getJobType().equalsIgnoreCase("CCP"))
			this.getActivity().getMenuInflater().inflate(R.menu.jobactions_menu, menu);
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
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_JOB4LIST) return new JobRender(this, _activity);
		return new JobRender(this, _activity);
	}
}

// - UNUSED CODE
// ............................................................................................
