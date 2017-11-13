//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.services;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.enums.ERequestClass;
import org.dimensinfin.eveonline.neocom.enums.ERequestState;

// - CLASS IMPLEMENTATION ...................................................................................
public class PendingRequestEntry extends AbstractComplexNode implements Comparable<PendingRequestEntry> {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7936869026974954271L;
	//	private static Logger			logger						= Logger.getLogger("PendingRequestEntry");

	// - F I E L D - S E C T I O N ............................................................................
	public ERequestClass			reqClass					= ERequestClass.UNDEFINED;
	public ERequestState			state							= ERequestState.EMPTY;
	private Number						content						= null;
	private int								priority					= 30;

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
	}

	//- M E T H O D - S E C T I O N ..........................................................................
	@Override
	public int compareTo(final PendingRequestEntry o) {
		if (null == o) return 1;
		if ((reqClass == o.reqClass) && (state == o.state) && (content == o.content)) return 0;
		return ((priority < o.priority) ? -1 : 1);
	}

	public Number getContent() {
		return content;
	}

	public String getIdentifier() {
		return content.toString();
	}

	public int getPriority() {
		return priority;
	}

	public void setClass(final ERequestClass newClass) {
		reqClass = newClass;
	}

	public void setPriority(final int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("PendingRequestEntry [");
		buffer.append("[").append(reqClass).append("] ");
		buffer.append("[").append(state).append("] ").append(priority);
		buffer.append(" - ").append(this.getIdentifier());
		buffer.append(" ]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
