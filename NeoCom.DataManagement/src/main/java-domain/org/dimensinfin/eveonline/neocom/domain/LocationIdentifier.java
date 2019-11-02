package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

/**
 * The new NeoLocation uses a multipart identifier to be able to select between space locations, other spaces and also to reach
 * the definition of the structure hangars already defined on the flags.
 */
public class LocationIdentifier {
	private Integer spaceIdentifier;
//	private Integer stationIdentifier;
	private Long containerIdentifier;
	private LocationIdentifierType type = LocationIdentifierType.UNKNOWN;

	private LocationIdentifier() {
	}

	public Integer getSpaceIdentifier() {
		return spaceIdentifier;
	}

//	public Integer getStationIdentifier() {
//		return stationIdentifier;
//	}

	public Long getContainerIdentifier() {
		return containerIdentifier;
	}

	public LocationIdentifierType getType() {
		return this.type;
	}

	// - B U I L D E R
	public static class Builder {
		private LocationIdentifier onConstruction;

		public Builder() {
			this.onConstruction = new LocationIdentifier();
		}

		public LocationIdentifier.Builder withSpaceIdentifier( final Integer spaceIdentifier ) {
			Objects.requireNonNull( spaceIdentifier );
			this.onConstruction.spaceIdentifier = spaceIdentifier;
			return this;
		}

		public LocationIdentifier build() {
			return this.onConstruction;
		}
	}
}