package org.dimensinfin.eveonline.neocom.database.entities;

import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import org.joda.time.DateTime;

import com.j256.ormlite.field.DatabaseField;

public abstract class UpdatableEntity extends NeoComNode {
	@DatabaseField
	private DateTime creationTime;
	@DatabaseField
	private DateTime lastUpdateTime;

//	public DateTime getCreationTime() {
//		return this.creationTime;
//	}

	public DateTime getLastUpdateTime() {
		if (null == this.lastUpdateTime) this.lastUpdateTime = new DateTime(0);
		return this.lastUpdateTime;
	}
}
