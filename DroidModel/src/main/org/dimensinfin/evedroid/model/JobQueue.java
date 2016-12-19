//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.model;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
public class JobQueue extends AbstractComplexNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 8572698611282203983L;

	// - F I E L D - S E C T I O N ............................................................................
	private Job								job								= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public JobQueue(final Job relatedJob) {
		job = relatedJob;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Job getJob() {
		return job;
	}

	/**
	 * Calculates the time still left for the job. This time in minutes is the number used to calcualte the
	 * queue occupation.
	 * 
	 * @return
	 */
	public int getTimeUsed() {
		Instant now = new Instant();
		final Instant endinstant = new Instant(job.getEndDate());
		long millis = endinstant.getMillis() - now.getMillis();
		return Double.valueOf(millis / ModelWideConstants.MINUTES1).intValue();
	}

	public void setJob(final Job relatedJob) {
		job = relatedJob;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("JobQueue [");
		if (null != job) buffer.append(job.toString());
		buffer.append("]");
		return super.toString();
	}
}

// - UNUSED CODE ............................................................................................
