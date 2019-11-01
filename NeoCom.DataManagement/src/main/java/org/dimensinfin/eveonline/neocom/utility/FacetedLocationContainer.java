package org.dimensinfin.eveonline.neocom.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.domain.SpaceKLocation;

public class FacetedLocationContainer<F> implements ICollaboration {
	private F facet;
	private List<SpaceKLocation> contents = new ArrayList<>();

	private FacetedLocationContainer() { }

	public F getFacet() {
		return this.facet;
	}

	// -  C O N T E N T
	public int addContent( final SpaceKLocation item ) {
		this.contents.add( item );
		return this.contents.size();
	}

	public int getContentCount() {
		return this.contents.size();
	}

	public boolean isEmpty() {
		return this.contents.size() > 0;
	}

	// - I C O L L A B O R A T I O N
	@Override
	public List<ICollaboration> collaborate2Model( final String variation ) {
		return new ArrayList<>( this.contents );
	}

	@Override
	public int compareTo( @NotNull final Object o ) {
		return 0;
	}

	// - B U I L D E R
	public static class Builder<F> {
		private FacetedLocationContainer<F> onConstruction;

		public Builder() {
			this.onConstruction = new FacetedLocationContainer<F>();
		}

		public FacetedLocationContainer.Builder<F> withFacet( final F facet ) {
			Objects.requireNonNull( facet );
			this.onConstruction.facet = facet;
			return this;
		}

		public FacetedLocationContainer<F> build() {
			Objects.requireNonNull( this.onConstruction.facet );
			return this.onConstruction;
		}
	}
}