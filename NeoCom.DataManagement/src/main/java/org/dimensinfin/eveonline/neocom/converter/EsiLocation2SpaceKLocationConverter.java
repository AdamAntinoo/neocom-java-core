package org.dimensinfin.eveonline.neocom.converter;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.SpaceKLocation;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

import retrofit2.Converter;

public class EsiLocation2SpaceKLocationConverter implements Converter<EsiLocation, SpaceKLocation> {
	private ESIUniverseDataProvider esiUniverseDataProvider;

	@Override
	public SpaceKLocation convert( final EsiLocation value ) {
		return new SpaceKLocation.Builder()
				.withRegion( this.esiUniverseDataProvider.getUniverseRegionById( value.getRegionId()) )
				.withConstellation( this.esiUniverseDataProvider.getUniverseConstellationById( value.getConstellationId() ))
				.build();
	}

	// - B U I L D E R
	public static class Builder {
		private EsiLocation2SpaceKLocationConverter onConstruction;

		public Builder() {
			this.onConstruction = new EsiLocation2SpaceKLocationConverter();
		}

		public EsiLocation2SpaceKLocationConverter.Builder withESIUniverseDataProvider( final ESIUniverseDataProvider esiUniverseDataProvider ) {
			Objects.requireNonNull( esiUniverseDataProvider );
			this.onConstruction.esiUniverseDataProvider = esiUniverseDataProvider;
			return this;
		}

		public EsiLocation2SpaceKLocationConverter build() {
			return this.onConstruction;
		}
	}
}