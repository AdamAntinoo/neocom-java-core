package org.dimensinfin.eveonline.neocom.database.entities;

import com.j256.ormlite.field.DatabaseField;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;

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

	// - C O R E
	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final UpdatableEntity that = (UpdatableEntity) o;
		return new EqualsBuilder()
				       .appendSuper(super.equals(o))
				       .append(lastUpdateTime, that.lastUpdateTime)
				       .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				       .appendSuper(super.hashCode())
				       .append(lastUpdateTime)
				       .toHashCode();
	}
}
