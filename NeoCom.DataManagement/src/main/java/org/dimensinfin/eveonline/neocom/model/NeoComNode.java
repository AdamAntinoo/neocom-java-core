package org.dimensinfin.eveonline.neocom.model;

import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IJsonAngular;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This model class will serve as the base placeholder for the NeoCom application nodes. Will define the
 * common methods and implement the default behavior for nodes.
 */
public abstract class NeoComNode implements ICollaboration, IJsonAngular {
	protected static final long serialVersionUID = 6506043294337948561L;
	protected static Logger logger = LoggerFactory.getLogger(NeoComNode.class);

	public static String capitalizeFirstLetter( String original ) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	// - C O N S T R U C T O R S
	public NeoComNode() { }

	// - I C O L L A B O R A T I O N
	public List<ICollaboration> collaborate2Model( final String variant ) {
		return new ArrayList<>();
	}

	public String getJsonClass() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int compareTo( final Object target ) {
		return 0;
	}

	// - C O R E
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				       .append(this.getJsonClass())
				       .toHashCode();
	}

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final NeoComNode that = (NeoComNode) o;
		return new EqualsBuilder()
				       .append(this.getJsonClass(), that.getJsonClass())
				       .isEquals();
	}

	// - B U I L D E R
	public static abstract class Builder<T, B extends Builder> {
		protected T actualClass;
		protected B actualClassBuilder;

		public Builder() {
			this.actualClass = this.getActual();
			this.actualClassBuilder = this.getActualBuilder();
		}

		protected abstract T getActual();

		protected abstract B getActualBuilder();

		public T build() {
			return this.getActual();
		}
	}
}
