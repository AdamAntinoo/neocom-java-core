package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.industry.Resource;

import org.apache.commons.lang3.builder.ToStringBuilder;

//@DatabaseTable(tableName = "PlanetaryResource")
public class PlanetaryResource extends Resource {
	//	@DatabaseField(generatedId = true)
	//	private int id = -1;
	public String tier = "OTHER";
	//	@DatabaseField
	//	private int typeId;
	//	@DatabaseField
	//	private double				quantity;
	//	@DatabaseField
	//	private String				name		= "<NAME>";
	//	@DatabaseField(foreign = true)
	//	private ResourceList	ownerList;

	// - C O N S T R U C T O R S
	public PlanetaryResource( final int typeId ) {
		super(typeId);
	}

	public PlanetaryResource( final int typeId, final int qty ) {
		super(typeId, qty);
	}
	//		jsonClass = "PlanetaryResource";
	//		// Set the tier type depending on the type id lookup list.
	//		if (getItem().getCategoryName() == "Planetary Resources") tier = "RAW";
	//		else {
	//			if (getItem().getGroupName() == "Basic Commodities") tier = "TIER1";
	//			if (getItem().getGroupName() == "Refined Commodities") tier = "TIER2";
	//			if (getItem().getGroupName() == "Specialized Commodities") tier = "TIER3";
	//			if (getItem().getGroupName() == "Advanced Commodities") tier = "TIER4";
	//		}
	//	}
	//	@Deprecated
	//	public PlanetaryResource( final int typeId, final int newQty ) {
	//		super(typeId, newQty);
	//		//		this.baseQty = newQty;
	//	}
	//
	//	@Deprecated
	//	public PlanetaryResource( final int typeId, final int newQty, final int stackSize ) {
	//		this(typeId, newQty);
	//		this.stackSize = stackSize;
	//	}


	// - M E T H O D - S E C T I O N ..........................................................................

	public String getTier() {
		evaluateTier();
		return this.tier;
	}

	public PlanetaryResource setTier( final String tier ) {
		this.tier = tier;
		return this;
	}

	protected void evaluateTier() {
		// Set the tier type depending on the type id lookup list.
		if (getItem().getCategoryName().equalsIgnoreCase("Planetary Resources")) tier = "RAW";
		else {
			if (getItem().getGroupName().equalsIgnoreCase("Basic Commodities")) tier = "TIER1";
			if (getItem().getGroupName().equalsIgnoreCase("Refined Commodities")) tier = "TIER2";
			if (getItem().getGroupName().equalsIgnoreCase("Specialized Commodities")) tier = "TIER3";
			if (getItem().getGroupName().equalsIgnoreCase("Advanced Commodities")) tier = "TIER4";
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				       //				       .append("id", id)
				       .append("tier", tier)
				       .append("typeId", typeId)
				       //				       .append("baseQty", baseQty)
				       .append(super.toString())
				       .toString();
	}

	//	// - B U I L D E R
	//	public static class Builder extends NeoComNode.Builder<PlanetaryResource, PlanetaryResource.Builder> {
	//		protected PlanetaryResource getActual() {
	//			return new PlanetaryResource();
	//		}
	//
	//		protected PlanetaryResource.Builder getActualBuilder() {
	//			return this;
	//		}
	//
	//		public PlanetaryResource.Builder withEveItem( final EveItem eveItem ) {
	//			this.getActual().item = eveItem;
	//			return this;
	//		}
	//
	//		public PlanetaryResource.Builder withQuantity( final int quantity ) {
	//			this.getActual().baseQty = quantity;
	//			return this;
	//		}
	//
	//		public PlanetaryResource build() {
	//			return super.build();
	//		}
	//	}
}
