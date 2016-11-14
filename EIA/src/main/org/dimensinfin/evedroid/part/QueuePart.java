//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.Date;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.model.JobQueue;
import org.dimensinfin.evedroid.render.QueueRender;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
public class QueuePart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 2802585321127135905L;

	// - F I E L D - S E C T I O N ............................................................................
	private int								number						= 1;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public QueuePart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public JobQueue getCastedModel() {
		return (JobQueue) getModel();
	}

	public Date getEndDate() {
		return getCastedModel().getJob().getEndDate();
	}

	public int getJobActivity() {
		return getCastedModel().getJob().getActivityID();
	}

	public long getModelID() {
		return getCastedModel().getJob().getJobID();
	}

	public int getNumber() {
		return number;
	}

	public Date getStartDate() {
		return getCastedModel().getJob().getStartDate();
	}

	public int getTime() {
		return getCastedModel().getJob().getTimeInSeconds();
	}

	public boolean isQueueActive() {
		DateTime now = new DateTime(DateTimeZone.UTC);
		final Instant endinstant = new Instant(getEndDate());
		long togomillis = endinstant.getMillis() - now.getMillis();
		if (togomillis < 1)
			return false;
		else
			return true;
	}

	public void setNumber(final int qnum) {
		number = qnum;
	}

	protected AbstractHolder selectHolder() {
		return new QueueRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
