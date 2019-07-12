package org.dimensinfin.eveonline.neocom.domain;


import com.annimon.stream.Objects;

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
		return corporationIconUrl;
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
		return allianceIconUrl;
	}

	public Pilot setAllianceIconUrl( final String allianceIconUrl ) {
		this.allianceIconUrl = allianceIconUrl;
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
