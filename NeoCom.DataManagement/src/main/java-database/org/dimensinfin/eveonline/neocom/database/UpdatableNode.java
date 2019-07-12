package org.dimensinfin.eveonline.neocom.database;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;

public class UpdatableNode extends NeoComNode {
	private DateTime lastUpdateTime;

	public void timeStamp() {
		this.lastUpdateTime = DateTime.now();
	}

	public DateTime getLastUpdateTime() {
		if (null == this.lastUpdateTime) this.lastUpdateTime = new DateTime(0);
		return this.lastUpdateTime;
	}

	// - C O R E
	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final UpdatableNode that = (UpdatableNode) o;
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
