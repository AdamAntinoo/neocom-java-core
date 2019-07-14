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

public class Pilot extends PilotV3 {
	private Integer pilotIdentifier;
	private Credential credential;
	private transient GetCharactersCharacterIdOk characterPublicData;
	private transient GetUniverseRaces200Ok raceData;
	private transient GetUniverseBloodlines200Ok bloodlineData;
	private transient GetUniverseAncestries200Ok ancestryData;
	private transient GetCorporationsCorporationIdOk corporationData;
	private String corporationIconUrl;
	private transient GetAlliancesAllianceIdOk allianceData;
	private String allianceIconUrl;

	private Pilot() {
	}

	public int getPilotIdentifier() {
		return this.pilotIdentifier;
	}

	public GetCorporationsCorporationIdOk getCorporationData() {
		return this.corporationData;
	}

	public Pilot setCorporationData( final GetCorporationsCorporationIdOk corporationData ) {
		this.corporationData = corporationData;
		return this;
	}

	public String getCorporationIconUrl() {
		return this.corporationIconUrl;
	}

	public Pilot setCorporationIconUrl( final String corporationIconUrl ) {
		this.corporationIconUrl = corporationIconUrl;
		return this;
	}

	public GetAlliancesAllianceIdOk getAllianceData() {
		return this.allianceData;
	}

	public Pilot setAllianceData( final GetAlliancesAllianceIdOk allianceData ) {
		this.allianceData = allianceData;
		return this;
	}

	public String getAllianceIconUrl() {
		return this.allianceIconUrl;
	}

	public Pilot setAllianceIconUrl( final String allianceIconUrl ) {
		this.allianceIconUrl = allianceIconUrl;
		return this;
	}

	public Credential getCredential() {
		return credential;
	}

	public Pilot setRaceData( final GetUniverseRaces200Ok raceData ) {
		this.raceData = raceData;
		return this;
	}

	// - D E L E G A T E D
	public Integer getAllianceId() {
		return this.characterPublicData.getAllianceId();
	}

	public Integer getBloodlineId() {
		return this.characterPublicData.getBloodlineId();
	}

	public Integer getCorporationId() {
		return this.characterPublicData.getCorporationId();
	}

	public String getName() {
		return this.characterPublicData.getName();
	}

	public GetCharactersCharacterIdOk.GenderEnum getGender() {
		return this.characterPublicData.getGender();
	}

	public Integer getRaceId() {
		if (null != this.characterPublicData)
			return this.characterPublicData.getRaceId();
		else return 0;
	}

	public String getRaceName() {
		if (null != this.raceData) return this.raceData.getName();
		else return "-";
	}

	// - C O R E

	@Override
	public boolean equals( final Object o ) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Pilot pilot = (Pilot) o;
		return new EqualsBuilder()
				.appendSuper(super.equals(o))
				.append(pilotIdentifier, pilot.pilotIdentifier)
				.append(credential, pilot.credential)
				.append(characterPublicData, pilot.characterPublicData)
				.append(raceData, pilot.raceData)
				.append(bloodlineData, pilot.bloodlineData)
				.append(ancestryData, pilot.ancestryData)
				.append(corporationData, pilot.corporationData)
				.append(corporationIconUrl, pilot.corporationIconUrl)
				.append(allianceData, pilot.allianceData)
				.append(allianceIconUrl, pilot.allianceIconUrl)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.appendSuper(super.hashCode())
				.append(pilotIdentifier)
				.append(credential)
				.append(characterPublicData)
				.append(raceData)
				.append(bloodlineData)
				.append(ancestryData)
				.append(corporationData)
				.append(corporationIconUrl)
				.append(allianceData)
				.append(allianceIconUrl)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("pilotIdentifier", pilotIdentifier)
				.append("credential", credential)
				.append("characterPublicData", characterPublicData)
				.append("raceData", raceData)
				.append("corporationData", corporationData)
				.append("allianceData", allianceData)
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
			Objects.requireNonNull(this.onConstruction.pilotIdentifier);
			Objects.requireNonNull(this.onConstruction.characterPublicData);
			Objects.requireNonNull(this.onConstruction.credential);
			return instance;
		}

		public Pilot.Builder withPilotIdentifier( final Integer pilotIdentifier ) {
			Objects.requireNonNull(pilotIdentifier);
			this.onConstruction.pilotIdentifier = pilotIdentifier;
			return this;
		}

		public Pilot.Builder withCredential( final Credential credential ) {
			Objects.requireNonNull(credential);
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
	}
}
