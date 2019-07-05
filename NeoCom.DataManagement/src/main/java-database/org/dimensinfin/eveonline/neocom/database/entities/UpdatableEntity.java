package org.dimensinfin.eveonline.neocom.database.entities;

import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import org.joda.time.DateTime;

import com.j256.ormlite.field.DatabaseField;

public abstract class UpdatableEntity extends NeoComNode {
	@DatabaseField
	private DateTime creationTime;
	@DatabaseField
	private DateTime lastUpdateTime;

	protected UpdatableEntity() {
		if (null == this.creationTime) this.creationTime = DateTime.now();
	}

	public DateTime getLastUpdateTime() {
		if (null == this.lastUpdateTime) this.lastUpdateTime = new DateTime(0);
		return this.lastUpdateTime;
	}

	/**
	 * Update the last time stamp to the current time. This should be called before any persistence update.
	 */
	public void timeStamp() {
		this.lastUpdateTime = DateTime.now();
	}
}
