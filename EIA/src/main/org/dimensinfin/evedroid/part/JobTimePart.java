//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.GregorianCalendar;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.render.JobTimeRender;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobTimePart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 3353359035798147679L;

	// - F I E L D - S E C T I O N ............................................................................
	private int								runTime						= 0;
	private int								runs							= 1;
	private int								jobActivity				= ModelWideConstants.activities.MANUFACTURING;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobTimePart(final Separator node) {
		super(node);
	}

	public Separator getCastedModel() {
		return (Separator) getModel();
	}

	public int getJobActivity() {
		return jobActivity;
	}

	@Override
	public long getModelID() {
		return GregorianCalendar.getInstance().getTimeInMillis();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getRuns() {
		return runs;
	}

	public int getRunTime() {
		return runTime;
	}

	@Override
	public boolean isExpanded() {
		if (getChildren().size() > 0)
			return false;
		else
			return false;
	}

	public void setActivity(final int activity) {
		jobActivity = activity;
	}

	public void setRunCount(final int runs) {
		this.runs = runs;
	}

	public void setRunTime(final int runTime) {
		this.runTime = runTime;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("ManufactureTimePart [");
		buffer.append("Runs: ").append(runs).append(" ");
		buffer.append(getRunTime()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	protected AbstractHolder selectHolder() {
		return new JobTimeRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
