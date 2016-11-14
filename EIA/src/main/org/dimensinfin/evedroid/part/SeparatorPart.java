//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.holder.SeparatorHolder;
import org.dimensinfin.evedroid.model.Separator;

// - CLASS IMPLEMENTATION ...................................................................................
public class SeparatorPart extends AbstractAndroidPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -8085543451527813221L;
	private static Logger			logger						= Logger.getLogger("SeparatorPart");

	// - F I E L D - S E C T I O N ............................................................................
	//	private AbstractPilotBasedActivity	activity					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public SeparatorPart(final AbstractComplexNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_title() {
		return getCastedModel().getTitle();
	}

	public Separator getCastedModel() {
		return (Separator) getModel();
	}

	@Override
	public long getModelID() {
		return GregorianCalendar.getInstance().getTimeInMillis();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new SeparatorHolder(this, _activity);
	}

}

// - UNUSED CODE ............................................................................................
