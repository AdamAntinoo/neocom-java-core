//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.part;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.render.QueueaAnalyticsRender;
import org.joda.time.DateTime;

// - CLASS IMPLEMENTATION ...................................................................................
public class QueueAnalyticsPart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -2509960203875961703L;

	// - F I E L D - S E C T I O N ............................................................................
	private int								maxManufacture;
	private int								manufacture;
	private int								maxInvention;
	private int								invention;
	private int								jobActivity				= ModelWideConstants.activities.MANUFACTURING;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public QueueAnalyticsPart(final AbstractComplexNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getInvention() {
		return invention;
	}

	public int getJobActivity() {
		return jobActivity;
	}

	public int getManufacture() {
		return manufacture;
	}

	public int getMaxInvention() {
		return maxInvention;
	}

	public int getMaxManufacture() {
		return maxManufacture;
	}

	@Override
	public long getModelID() {
		return new DateTime().getMillis();
	}

	public void setJobActivity(final int filter) {
		jobActivity = filter;
	}

	public void setLimits(final int maxMan, final int maxInv) {
		maxManufacture = maxMan;
		maxInvention = maxInv;
	}

	public void setValues(final int man, final int inv) {
		manufacture = man;
		invention = inv;
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new QueueaAnalyticsRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
