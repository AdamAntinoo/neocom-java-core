package org.dimensinfin.eveonline.neocom.database.entities;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import org.dimensinfin.eveonline.neocom.domain.NeoComNode;

public abstract class UpdatableEntity extends NeoComNode {
	@DatabaseField(dataType = DataType.TIME_STAMP)
	@JsonIgnore
	private Timestamp creationTime;
	@DatabaseField(dataType = DataType.TIME_STAMP)
	@JsonIgnore
	private Timestamp lastUpdateTime;

	protected UpdatableEntity() {
		this.creationTime = new Timestamp( DateTime.now().getMillis() );
	}

	public Timestamp getLastUpdateTime() {
		if (null == this.lastUpdateTime) this.lastUpdateTime = new Timestamp( 0 );
		return this.lastUpdateTime;
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final UpdatableEntity that = (UpdatableEntity) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.isEquals();
	}

	/**
	 * Update the last time stamp to the current time. This should be called before any persistence update.
	 */
	public void timeStamp() {
		this.lastUpdateTime = new Timestamp( DateTime.now().getMillis() );
	}
}
