//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;

import org.dimensinfin.core.interfaces.IViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.enums.ETaskType;
import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveTask extends NeoComNode implements IViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7187291497544861372L;

	// - F I E L D - S E C T I O N ............................................................................
	private ETaskType					type							= ETaskType.BUY;
	public Resource						resource					= null;
	private int								qty								= 0;
	private EveLocation				location					= null;
	private EveLocation				destination				= null;
	private String						action						= null;
	private NeoComAsset				assetRef					= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveTask(final ETaskType newType, final Resource newresource) {
		type = newType;
		this.setResource(newresource);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addAction(final String action) {
		this.action = action;
	}

	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		//		results.addAll(getTasks());
		return results;
	}

	public EveLocation getDestination() {
		return destination;
	}

	public EveItem getItem() {
		return resource.item;
	}

	public String getItemName() {
		return resource.item.getName();
	}

	public EveLocation getLocation() {
		if (null == location) return new EveLocation();
		return location;
	}

	public double getPrice() {
		return resource.item.getLowestSellerPrice().getPrice();
	}

	public int getQty() {
		return qty;
	}

	/**
	 * Sometimes the task may have associated an asset. This happens when the task is available. This is used
	 * later when interaction with the task.
	 * 
	 * @return
	 */
	public NeoComAsset getReferencedAsset() {
		return assetRef;
	}

	public Resource getResource() {
		return resource;
	}

	public ETaskType getTaskType() {
		return type;
	}

	public int getTypeID() {
		return resource.getTypeID();
	}

	public void registerAsset(final NeoComAsset targetAsset) {
		assetRef = targetAsset;
	}

	public void setDestination(final EveLocation newLocation) {
		destination = newLocation;
	}

	public void setLocation(final EveLocation newLocation) {
		location = newLocation;
	}

	public void setQty(final int plusqty) {
		qty = plusqty;
	}

	public void setResource(final Resource newresource) {
		resource = newresource;
	}

	public void setTaskType(final ETaskType newType) {
		type = newType;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveTask [");
		buffer.append(type).append(" [#").append(resource.getTypeID()).append(" ").append(resource.getName()).append("] ");
		buffer.append("quantity: ").append(qty);
		if (null != location) {
			buffer.append(" location").append(location);
		}
		if (null != destination) {
			buffer.append(" - ").append("destination").append(destination);
		}
		buffer.append(" ").append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
