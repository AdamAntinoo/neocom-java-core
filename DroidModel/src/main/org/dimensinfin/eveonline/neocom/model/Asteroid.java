//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.model;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.joda.time.Instant;

// - CLASS IMPLEMENTATION ...................................................................................
public class Asteroid extends Resource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 288022169611633861L;
	private static Logger			logger						= Logger.getLogger("Asteroid");

	// - F I E L D - S E C T I O N ............................................................................
	private final Instant			start							= new Instant();
	private final EveItem			item							= null;
	private final int					qty								= 0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Asteroid(final int itemId, final int newQty) {
		super(itemId, newQty);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Instant getStartInstant() {
		return start;
	}

}

// - UNUSED CODE ............................................................................................
