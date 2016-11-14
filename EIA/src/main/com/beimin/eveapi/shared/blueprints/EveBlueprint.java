package com.beimin.eveapi.shared.blueprints;

public class EveBlueprint<A extends EveBlueprint<?>> {
	private long		itemID;
	private Long		locationID;
	private int			typeID;
	private String	typeName;
	private int			flag;
	private int			quantity;
	private int			timeEfficiency;
	private int			materialEfficiency;
	private int			runs;

	private Integer	rawQuantity;
	private boolean	singleton;
	private boolean	bpo	= false;

	public int getFlag() {
		return flag;
	}

	public long getItemID() {
		return itemID;
	}

	public Long getLocationID() {
		return locationID;
	}

	public int getMaterialEfficiency() {
		return materialEfficiency;
	}

	/**
	 * This will return 1 because the field quantity is never used for that functionality. A -2 means that this
	 * is a BPC and a -1 means a BPO. The use of the quantity is for my own tests and stacks creation.
	 * 
	 * @return 1 if the value is negative or the stored value otherwise
	 */
	public int getQuantity() {
		if (quantity < 1)
			return 1;
		else
			return quantity;
	}

	public Integer getRawQuantity() {
		return rawQuantity;
	}

	public int getRuns() {
		return runs;
	}

	public boolean getSingleton() {
		return singleton;
	}

	public int getTimeEfficiency() {
		return timeEfficiency;
	}

	public int getTypeID() {
		return typeID;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setFlag(final int flag) {
		this.flag = flag;
	}

	public void setItemID(final long itemID) {
		this.itemID = itemID;
	}

	public void setLocationID(final Long locationID) {
		this.locationID = locationID;
	}

	public void setMaterialEfficiency(final int materialEfficiency) {
		this.materialEfficiency = materialEfficiency;
	}

	public void setQuantity(final int quantity) {
		this.quantity = quantity;
		if (quantity == -2) bpo = true;
	}

	public void setRawQuantity(final Integer rawQuantity) {
		this.rawQuantity = rawQuantity;
		if (this.rawQuantity == null) this.rawQuantity = 0;
	}

	public void setRuns(final int runs) {
		this.runs = runs;
	}

	public void setSingleton(final boolean singleton) {
		this.singleton = singleton;
	}

	public void setTimeEfficiency(final int timeEfficiency) {
		this.timeEfficiency = timeEfficiency;
	}

	public void setTypeID(final int typeID) {
		this.typeID = typeID;
	}

	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

}