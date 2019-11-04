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
	private Long spaceIdentifier;
	private Long structureIdentifier;
	private GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag;
	private GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType =
			GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER;
	private LocationIdentifierType type = LocationIdentifierType.UNKNOWN;

	private LocationIdentifier() { }

	public Long getSpaceIdentifier() {
		return this.spaceIdentifier;
	}

	public Long getStructureIdentifier() {
		return this.structureIdentifier;
	}

	public LocationIdentifierType getType() {
		return this.type;
	}

	public LocationIdentifier setType( final LocationIdentifierType type ) {
		this.type = type;
		return this;
	}

	public LocationIdentifier setStructureIdentifier( final Long structureIdentifier ) {
		this.structureIdentifier = structureIdentifier;
		return this;
	}

	private void classifyLocation() {
		if (this.spaceIdentifier < 20E6) {
			this.type = LocationIdentifierType.REGION;
			return;
		}
		if (this.spaceIdentifier < 30E6) {
			this.type = LocationIdentifierType.CONSTELLATION;
			return;
		}
		if (this.spaceIdentifier < 40E6) {
			this.type = LocationIdentifierType.SPACE;
			return;
		}
		if (this.spaceIdentifier < 61E6) {
			this.type = LocationIdentifierType.STATION;
			return;
		}
		if (null == this.locationType) this.locationType = GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER;
		if (this.locationType == GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.SOLAR_SYSTEM)
			this.type = LocationIdentifierType.SPACE;
		if (this.locationType == GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION)
			this.type = LocationIdentifierType.STATION;
		// Other types of locations. Use the id to extract the range
		if (this.locationType == GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER) {
			if (this.detectIfShipFittingSlot()) this.type = LocationIdentifierType.SHIP;
			if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.UNLOCKED)
				this.type = LocationIdentifierType.CONTAINER;
			if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.CARGO)
				this.type = LocationIdentifierType.SHIP;
		}
	}

	private boolean detectIfShipFittingSlot() {
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.DRONEBAY) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT0) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT1) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT2) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT3) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT4) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT5) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT6) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HISLOT7) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT0) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT1) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT2) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT3) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT4) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT5) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT6) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.MEDSLOT7) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT0) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT1) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT2) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT3) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT4) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT5) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT6) return true;
		if (this.locationFlag == GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.LOSLOT7) return true;
		return false;
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

		public LocationIdentifier.Builder withSpaceIdentifier( final Long spaceIdentifier ) {
			Objects.requireNonNull( spaceIdentifier );
			this.onConstruction.spaceIdentifier = spaceIdentifier;
			return this;
		}

		public LocationIdentifier.Builder withLocationFlag( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag ) {
			this.onConstruction.locationFlag = locationFlag;
			return this;
		}

		public LocationIdentifier.Builder withLocationType( final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType ) {
			this.onConstruction.locationType = locationType;
			return this;
		}

		public LocationIdentifier build() {
			Objects.requireNonNull( this.onConstruction.spaceIdentifier );
			this.onConstruction.classifyLocation();
			return this.onConstruction;
		}
	}
}