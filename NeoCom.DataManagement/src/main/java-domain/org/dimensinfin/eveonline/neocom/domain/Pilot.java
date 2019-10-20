package org.dimensinfin.eveonline.neocom.domain;


import com.annimon.stream.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

public class Pilot extends PilotV2 {
	private static final long PILOT_CACHE_TIME = TimeUnit.HOURS.toMillis( 12 );

	private transient GetCharactersCharacterIdOk characterPublicData;
	private transient GetUniverseRaces200Ok raceData;
	private transient GetUniverseAncestries200Ok ancestryData;
	private transient GetUniverseBloodlines200Ok bloodlineData;

	private Credential credential;
//	@Deprecated
//	private transient GetCorporationsCorporationIdOk corporationData;
//	@Deprecated
//	private String corporationIconUrl;
//	@Deprecated
//	private transient GetAlliancesAllianceIdOk allianceData;
//	@Deprecated
//	private String allianceIconUrl;

	// - C O N S T R U C T O R S
	private Pilot() {
	}

	// - G E T T E R S   &   S E T T E R S
	public Credential getCredential() {
		return credential;
	}

	public GetUniverseRaces200Ok getRace() {
		return this.raceData;
	}

	public GetUniverseAncestries200Ok getAncestry() {
		return this.ancestryData;
	}

	public GetUniverseBloodlines200Ok getBloodline() {
		return this.bloodlineData;
	}

	public Pilot setCharacterPublicData( final GetCharactersCharacterIdOk characterPublicData ) {
		this.characterPublicData = characterPublicData;
		return this;
	}

	public Pilot setRaceData( final GetUniverseRaces200Ok raceData ) {
		this.raceData = raceData;
		return this;
	}

	public Pilot setAncestryData( final GetUniverseAncestries200Ok ancestryData ) {
		this.ancestryData = ancestryData;
		return this;
	}

	public Pilot setBloodlineData( final GetUniverseBloodlines200Ok bloodlineData ) {
		this.bloodlineData = bloodlineData;
		return this;
	}

	// - V I R T U A L S
	public String getUrl4Icon() {
		return "http://image.eveonline.com/character/" + this.pilotId + "_256.jpg";
	}
// [01]

	// - D E L E G A T E D
	public DateTime getBirthday() {
		return this.characterPublicData.getBirthday();
	}

	public Integer getRaceId() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getRaceId();
		else return -1;
	}

	public Integer getAncestryId() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getAncestryId();
		else return -1;
	}

	public Integer getBloodlineId() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getBloodlineId();
		else return -1;
	}

	public String getDescription() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getDescription();
		else return "-";
	}

	public String getGender() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getGender().name();
		else return "-";
	}

	public String getName() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getName();
		else return "-";
	}

	public Float getSecurityStatus() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getSecurityStatus();
		else return -1.0F;
	}

	// - I U P D A T A B L E
	@Override
	public boolean needsRefresh() {
		if (this.getLastUpdateTime().plus( PILOT_CACHE_TIME ).isBefore( DateTime.now() ))
			return true;
		return false;
	}

	// - C O R E
	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Pilot pilot = (Pilot) o;
		return new EqualsBuilder()
				.appendSuper( super.equals( o ) )
				.append( pilotId, pilot.pilotId )
//				.append( credential, pilot.credential )
				.append( characterPublicData, pilot.characterPublicData )
				.append( raceData, pilot.raceData )
				.append( bloodlineData, pilot.bloodlineData )
				.append( ancestryData, pilot.ancestryData )
//				.append( corporationData, pilot.corporationData )
//				.append( corporationIconUrl, pilot.corporationIconUrl )
//				.append( allianceData, pilot.allianceData )
//				.append( allianceIconUrl, pilot.allianceIconUrl )
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder( 17, 37 )
				.appendSuper( super.hashCode() )
				.append( pilotId )
//				.append( credential )
				.append( characterPublicData )
				.append( raceData )
				.append( bloodlineData )
				.append( ancestryData )
//				.append( corporationData )
//				.append( corporationIconUrl )
//				.append( allianceData )
//				.append( allianceIconUrl )
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder( this, ToStringStyle.JSON_STYLE )
				.append( "pilotIdentifier", pilotId )
//				.append( "credential", credential )
				.append( "characterPublicData", characterPublicData )
//				.append( "raceData", raceData )
//				.append( "corporationData", corporationData )
//				.append( "allianceData", allianceData )
				.toString();
	}

	// - B U I L D E R
	public static class Builder extends NeoComNode.Builder<Pilot, Pilot.Builder> {
		private Pilot onConstruction;

		@Override
		protected Pilot getActual() {
			if (null == this.onConstruction) this.onConstruction = new Pilot();
			return this.onConstruction;
		}

		@Override
		protected Builder getActualBuilder() {
			return this;
		}

		public Pilot build() {
			final Pilot instance = super.build();
			Objects.requireNonNull( this.onConstruction.pilotId );
			Objects.requireNonNull( this.onConstruction.characterPublicData );
//			Objects.requireNonNull( this.onConstruction.credential );
			return instance;
		}

		public Pilot.Builder withPilotIdentifier( final Integer pilotIdentifier ) {
			Objects.requireNonNull( pilotIdentifier );
			this.onConstruction.pilotId = pilotIdentifier;
			return this;
		}

		public Pilot.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull( credential );
			this.onConstruction.credential = credential;
			return this;
		}

		public Pilot.Builder withCharacterPublicData( final GetCharactersCharacterIdOk characterPublicData ) {
			if (null != characterPublicData)
				this.onConstruction.characterPublicData = characterPublicData;
			return this;
		}

		public Pilot.Builder withRaceData( final GetUniverseRaces200Ok raceData ) {
			if (null != raceData)
				this.onConstruction.raceData = raceData;
			return this;
		}
		public Pilot.Builder withAncestryData( final GetUniverseAncestries200Ok ancestryData ) {
			if (null != ancestryData)
				this.onConstruction.ancestryData = ancestryData;
			return this;
		}
		public Pilot.Builder withBloodlineData( final GetUniverseBloodlines200Ok bloodlineData ) {
			if (null != bloodlineData)
				this.onConstruction.bloodlineData = bloodlineData;
			return this;
		}
	}
}
// [01]
//	public GetCorporationsCorporationIdOk getCorporationData() {
//		return this.corporationData;
//	}
//
//	public Pilot setCorporationData( final GetCorporationsCorporationIdOk corporationData ) {
//		this.corporationData = corporationData;
//		return this;
//	}

//	public String getCorporationIconUrl() {
//		return this.corporationIconUrl;
//	}
//
//	public Pilot setCorporationIconUrl( final String corporationIconUrl ) {
//		this.corporationIconUrl = corporationIconUrl;
//		return this;
//	}
//
//	public GetAlliancesAllianceIdOk getAllianceData() {
//		return this.allianceData;
//	}
//
//	public Pilot setAllianceData( final GetAlliancesAllianceIdOk allianceData ) {
//		this.allianceData = allianceData;
//		return this;
//	}
//
//	public String getAllianceIconUrl() {
//		return this.allianceIconUrl;
//	}
//
//	public Pilot setAllianceIconUrl( final String allianceIconUrl ) {
//		this.allianceIconUrl = allianceIconUrl;
//		return this;
//	}

//	public Credential getCredential() {
//		return credential;
//	}

//	public Pilot setRaceData( final GetUniverseRaces200Ok raceData ) {
//		this.raceData = raceData;
//		return this;
//	}
//	public Integer getAllianceId() {
//		return this.characterPublicData.getAllianceId();
//	}
//
//
//	public Integer getCorporationId() {
//		return this.characterPublicData.getCorporationId();
//	}
//
//
//	public GetCharactersCharacterIdOk.GenderEnum getGender() {
//		return this.characterPublicData.getGender();
//	}
//
//
