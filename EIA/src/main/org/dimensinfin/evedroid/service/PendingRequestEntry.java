//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.service;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.core.ERequestClass;
import org.dimensinfin.evedroid.core.ERequestState;

// - CLASS IMPLEMENTATION ...................................................................................
public class PendingRequestEntry extends AbstractGEFNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7936869026974954271L;
	private static Logger			logger						= Logger.getLogger("PendingRequestEntry");

	// - F I E L D - S E C T I O N ............................................................................
	public ERequestClass			reqClass					= ERequestClass.UNDEFINED;
	public ERequestState			state							= ERequestState.EMPTY;
	private Number						content						= null;
	private int								priority					= 1;

	//	private Instant						timestamp					= new Instant(0);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * When creating a new card fill it with a new Market Data information so we can have the information that
	 * needs to be updated. There are two ways to create on request, an empty one (this is the default) and one
	 * that already exists but that we like to be updated.
	 * 
	 * @param id
	 *          item id related to the market data to be updated.
	 */
	public PendingRequestEntry(final long requestLocalizer) {
		content = new Long(requestLocalizer);
		reqClass = ERequestClass.MARKETDATA;
		state = ERequestState.PENDING;
		//		timestamp = new Instant();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Number getContent() {
		return content;
	}

	public String getIdentifier() {
		return content.toString();
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(final int priority) {
		this.priority = priority;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("PendingRequestEntry [");
		buffer.append("[").append(reqClass).append("] ");
		buffer.append("[").append(state).append("] ").append(priority);
		buffer.append(" - ").append(getIdentifier());
		buffer.append(" ]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
