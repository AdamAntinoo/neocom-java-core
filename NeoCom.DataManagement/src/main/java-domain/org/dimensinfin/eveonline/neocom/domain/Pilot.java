package org.dimensinfin.eveonline.neocom.domain;


import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import com.annimon.stream.Objects;

public class Pilot extends PilotV3 {
	private Integer pilotIdentifier;
	private GetCharactersCharacterIdOk characterPublicData;
	private GetUniverseRaces200Ok raceData;
	public int getPilotIdentifier() {
		return pilotIdentifier;
	}

	// - D E L E G A T E D
	public Integer getAllianceId() {
		return characterPublicData.getAllianceId();
	}

	public Integer getBloodlineId() {
		return characterPublicData.getBloodlineId();
	}

	public Integer getCorporationId() {
		return characterPublicData.getCorporationId();
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
		if (null != this.race) return this.race.getName();
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
			Objects.requireNonNull(this.onConstruction.characterPublicData);
			Objects.requireNonNull(this.onConstruction.pilotIdentifier);
			return instance;
		}

		public Pilot.Builder withPilotIdentifier( final Integer pilotIdentifier ) {
			this.onConstruction.pilotIdentifier = pilotIdentifier;
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
