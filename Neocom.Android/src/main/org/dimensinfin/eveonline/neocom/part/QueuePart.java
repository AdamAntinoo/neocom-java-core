//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.part;

// - IMPORT SECTION .........................................................................................
import java.util.Date;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.model.JobQueue;
import org.dimensinfin.eveonline.neocom.render.QueueRender;
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
	public QueuePart(final JobQueue node) {
		super(node);
	}

	//	public QueuePart(final AbstractGEFNode node) {
	//		super(node);
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public JobQueue getCastedModel() {
		return (JobQueue) this.getModel();
	}

	public Date getEndDate() {
		return this.getCastedModel().getJob().getEndDate();
	}

	public int getJobActivity() {
		return this.getCastedModel().getJob().getActivityID();
	}

	@Override
	public long getModelID() {
		return this.getCastedModel().getJob().getJobID();
	}

	public int getNumber() {
		return number;
	}

	public Date getStartDate() {
		return this.getCastedModel().getJob().getStartDate();
	}

	public int getTime() {
		return this.getCastedModel().getJob().getTimeInSeconds();
	}

	public boolean isQueueActive() {
		DateTime now = new DateTime(DateTimeZone.UTC);
		final Instant endinstant = new Instant(this.getEndDate());
		long togomillis = endinstant.getMillis() - now.getMillis();
		if (togomillis < 1)
			return false;
		else
			return true;
	}

	public void setNumber(final int qnum) {
		number = qnum;
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new QueueRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
