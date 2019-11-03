package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

/**
 * The new NeoLocation uses a multipart identifier to be able to select between space locations, other spaces and also to reach
 * the definition of the structure hangars already defined on the flags.
 */
public class LocationIdentifier {
	private Integer spaceIdentifier;
	private GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag;
	private GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType;
	private LocationIdentifierType type = LocationIdentifierType.UNKNOWN;

	private LocationIdentifier() {
	}

	public Integer getSpaceIdentifier() {
		return spaceIdentifier;
	}
	public LocationIdentifierType getType() {
		return this.type;
	}

	private void classifyLocation() {
		if (this.locationType == GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM)
			this.type = LocationIdentifierType.SPACE;
		if (this.locationType == GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION)
			this.type = LocationIdentifierType.STATION;
		// Other types of locations. Use the id to extract the range
		if (this.spaceIdentifier < 61E6)
			this.type = LocationIdentifierType.STATION;
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "spaceIdentifier", this.spaceIdentifier )
				.append( "locationFlag", this.locationFlag )
				.append( "locationType", this.locationType )
				.append( "type", this.type )
				.toString();
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

		public LocationIdentifier.Builder withLocationFlag( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag ) {
			Objects.requireNonNull( locationFlag );
			this.onConstruction.locationFlag = locationFlag;
			return this;
		}

		public LocationIdentifier.Builder withLocationType( final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType ) {
			Objects.requireNonNull( locationType );
			this.onConstruction.locationType = locationType;
			return this;
		}

		public LocationIdentifier build() {
			Objects.requireNonNull( this.onConstruction.spaceIdentifier );
			Objects.requireNonNull( this.onConstruction.locationFlag );
			Objects.requireNonNull( this.onConstruction.locationType );
			this.onConstruction.classifyLocation();
			return this.onConstruction;
		}
	}
}