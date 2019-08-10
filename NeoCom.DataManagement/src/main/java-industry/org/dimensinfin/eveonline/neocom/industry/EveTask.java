//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.industry;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.entities.MarketOrder;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import java.util.concurrent.ExecutionException;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveTask extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 7187291497544861372L;

	// - F I E L D - S E C T I O N ............................................................................
	private Action.ETaskType type = Action.ETaskType.BUY;
	private Resource resource = null;
	private int qty = 0;
	private EsiLocation location = null;
	private EsiLocation destination = null;
	private String action = null;

	private NeoComAsset assetRef = null;
	private MarketOrder marketCounterPart =null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveTask( final Action.ETaskType newType, final Resource newresource ) {
		super();
//		jsonClass = "EveTask";
		type = newType;
		this.setResource(newresource);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addAction( final String action ) {
		this.action = action;
	}

	public EsiLocation getDestination() {
		return destination;
	}

	public EveItem getItem() {
		return resource.getItem();
	}

	public String getItemName() {
		return resource.getItem().getName();
	}

	public EsiLocation getLocation() {
		if (null == location) return EsiLocation.getJitaLocation();
		return location;
	}

	public double getPrice() {
		try {
			return resource.getItem().getLowestSellerPrice().getPrice();
		} catch (ExecutionException ee) {
			return resource.getItem().getPrice();
		} catch (InterruptedException ie) {
			return resource.getItem().getPrice();
		}
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

	public Action.ETaskType getTaskType() {
		return type;
	}

	public int getTypeId() {
		return resource.getTypeId();
	}

	public void registerAsset( final NeoComAsset targetAsset ) {
		assetRef = targetAsset;
	}

	public EveTask setDestination( final EsiLocation newLocation ) {
		destination = newLocation;
		return this;
	}

	public EveTask setLocation( final EsiLocation newLocation ) {
		location = newLocation;
		return this;
	}

	public EveTask setQty( final int plusqty ) {
		qty = plusqty;
		return this;
	}

	public EveTask setResource( final Resource newresource ) {
		resource = newresource;
		return this;
	}

	public EveTask setTaskType( final Action.ETaskType newType ) {
		type = newType;
		return this;
	}

	public EveTask setMarketCounterPart( final MarketOrder marketCounterPart ) {
		this.marketCounterPart = marketCounterPart;
		return this;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("EveTask [");
		buffer.append(type).append(" [#").append(resource.getTypeId()).append(" ").append(resource.getName()).append("] ");
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
