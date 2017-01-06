//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.model;

// - IMPORT SECTION .........................................................................................
import java.io.Serializable;
import java.util.ArrayList;

import org.dimensinfin.core.interfaces.INeoComNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.eveonline.neocom.enums.ETaskType;
import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveTask extends AbstractComplexNode implements INeoComNode{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7187291497544861372L;

	// - F I E L D - S E C T I O N ............................................................................
	private ETaskType					type							= ETaskType.BUY;
	public Resource						resource					= null;
	private int								qty								= 0;
	private EveLocation				location					= null;
	private EveLocation				destination				= null;
	private String						action						= null;
	private NeoComAsset							assetRef					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveTask(final ETaskType newType, final Resource newresource) {
		this.type = newType;
		setResource(newresource);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addAction(final String action) {
		this.action = action;
	}

	public EveLocation getDestination() {
		return this.destination;
	}

	public EveItem getItem() {
		return this.resource.item;
	}

	public String getItemName() {
		return this.resource.item.getName();
	}

	public EveLocation getLocation() {
		if (null == this.location) return new EveLocation();
		return this.location;
	}

	public double getPrice() {
		return this.resource.item.getLowestSellerPrice().getPrice();
	}

	public int getQty() {
		return this.qty;
	}

	/**
	 * Sometimes the task may have associated an asset. This happens when the task is available. This is used
	 * later when interaction with the task.
	 * 
	 * @return
	 */
	public NeoComAsset getReferencedAsset() {
		return this.assetRef;
	}

	public Resource getResource() {
		return this.resource;
	}

	public ETaskType getTaskType() {
		return this.type;
	}

	public int getTypeID() {
		return this.resource.getTypeID();
	}

	public void registerAsset(final NeoComAsset targetAsset) {
		this.assetRef = targetAsset;
	}

	public void setDestination(final EveLocation newLocation) {
		this.destination = newLocation;
	}

	public void setLocation(final EveLocation newLocation) {
		this.location = newLocation;
	}

	public void setQty(final int plusqty) {
		this.qty = plusqty;
	}

	public void setResource(final Resource newresource) {
		this.resource = newresource;
	}

	public void setTaskType(final ETaskType newType) {
		this.type = newType;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveTask [");
		buffer.append(this.type).append(" [#").append(this.resource.getTypeID()).append(" ")
				.append(this.resource.getName()).append("] ");
		buffer.append("quantity: ").append(this.qty);
		if (null != this.location) buffer.append(" location").append(this.location);
		if (null != this.destination) buffer.append(" - ").append("destination").append(this.destination);
		buffer.append(" ").append("]");
		return buffer.toString();
	}

	public ArrayList<AbstractComplexNode> collaborate2Model(String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
//		results.addAll(getTasks());
		return results;
	}
}

// - UNUSED CODE ............................................................................................
