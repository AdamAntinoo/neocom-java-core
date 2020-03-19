package org.dimensinfin.eveonline.neocom.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.adapter.StoreCacheManager;
import org.dimensinfin.eveonline.neocom.annotation.TimeElapsed;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AllianceApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdIconsOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseConstellationsConstellationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRegionsRegionIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseStationsStationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import retrofit2.Response;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class ESIUniverseDataProvider {
	protected static final Logger logger = LoggerFactory.getLogger( ESIUniverseDataProvider.class );
	// - I N T E R N A L   C A C H E S
	private static final Map<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap<>( 1200 );
	// - C O M P O N E N T S
	protected IConfigurationService configurationProvider;
	protected IFileSystem fileSystemAdapter;
	protected StoreCacheManager storeCacheManager;
	protected RetrofitFactory retrofitFactory;

	protected ESIUniverseDataProvider() {}

	/**
	 * Go to the ESI api to get the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 * To access the public data it will use the current unauthorized retrofit connection.
	 */
	@TimeElapsed
	private List<GetMarketsPrices200Ok> getUniverseMarketsPrices() {
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( MarketApi.class )
					.getMarketsPrices( DEFAULT_ESI_SERVER.toLowerCase(), null )
					.execute();
			if (marketApiResponse.isSuccessful())
				return marketApiResponse.body();
		} catch (final IOException | RuntimeException ioe) {
			NeoComLogger.error( ioe );
		}
		return new ArrayList<>();
	}

	// - A L L I A N C E   P U B L I C   I N F O R M A T I O N
	public GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier ) {
		try {
			final Response<GetAlliancesAllianceIdOk> allianceResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( AllianceApi.class )
					.getAlliancesAllianceId( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	public GetAlliancesAllianceIdIconsOk getAlliancesAllianceIdIcons( final int identifier ) {
		try {
			final Response<GetAlliancesAllianceIdIconsOk> allianceResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( AllianceApi.class )
					.getAlliancesAllianceIdIcons( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	// - C O R P O R A T I O N   P U B L I C   I N F O R M A T I O N
	public GetCorporationsCorporationIdOk getCorporationsCorporationId( final int identifier ) {
		try {
			final Response<GetCorporationsCorporationIdOk> corporationResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( CorporationApi.class )
					.getCorporationsCorporationId( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	public GetCorporationsCorporationIdIconsOk getCorporationsCorporationIdIcons( final int identifier ) {
		try {
			final Response<GetCorporationsCorporationIdIconsOk> corporationResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( CorporationApi.class )
					.getCorporationsCorporationIdIcons( identifier, DEFAULT_ESI_SERVER, null )
					.execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (final IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	public GetUniverseConstellationsConstellationIdOk getUniverseConstellationById( final Integer constellationId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseConstellationsConstellationIdOk> systemResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseConstellationsConstellationId( constellationId,
							DEFAULT_ACCEPT_LANGUAGE,
							DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (systemResponse.isSuccessful())
				return systemResponse.body();
		} catch (IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	public GetUniverseRegionsRegionIdOk getUniverseRegionById( final Integer regionId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseRegionsRegionIdOk> systemResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseRegionsRegionId( regionId,
							DEFAULT_ACCEPT_LANGUAGE,
							DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	// - P R O V I D E R   A P I
	public GetUniverseStationsStationIdOk getUniverseStationById( final Integer stationId ) {
		NeoComLogger.enter( "stationId: {}", stationId.toString() );
		try {
			final Response<GetUniverseStationsStationIdOk> stationResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseStationsStationId( stationId
							, DEFAULT_ESI_SERVER.toLowerCase(), null )
					.execute();
			if (stationResponse.isSuccessful()) return stationResponse.body();
		} catch (final IOException ioe) {
			NeoComLogger.error( "IOException during ESI data access.", ioe );
		}
		return null;
	}

	public GetUniverseSystemsSystemIdOk getUniverseSystemById( final Integer systemId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseSystemsSystemIdOk> systemResponse = this.retrofitFactory
					.accessUniverseConnector()
					.create( UniverseApi.class )
					.getUniverseSystemsSystemId( systemId
							, DEFAULT_ACCEPT_LANGUAGE
							, DEFAULT_ESI_SERVER, null, null )
					.execute();
			if (systemResponse.isSuccessful())
				return systemResponse.body();
		} catch (IOException ioe) {
			NeoComLogger.error( ioe );
		}
		return null;
	}

	// - C A C H E D   A P I
	public GetUniverseTypesTypeIdOk searchEsiItem4Id( final int itemId ) {
		return this.storeCacheManager.accessItem( itemId ).blockingGet();
	}

	@TimeElapsed
	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		NeoComLogger.info( "CategoryId: {}", categoryId + "" );
		return this.storeCacheManager.accessCategory( categoryId ).blockingGet();
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		NeoComLogger.info( "GroupId: {}", groupId + "" );
		return this.storeCacheManager.accessGroup( groupId ).blockingGet();
	}

	// - S D E   I N T E R N A L   D A T A
	public double searchSDEMarketPrice( final int typeId ) {
		NeoComLogger.info( "Price for: {}", typeId + "" );
		if (marketDefaultPrices.isEmpty()) // First download the family data.
			this.downloadItemPrices();
		if (marketDefaultPrices.containsKey( typeId )) return marketDefaultPrices.get( typeId ).getAdjustedPrice();
		else return -1.0;
	}

	@TimeElapsed
	public GetUniverseSystemsSystemIdOk searchSolarSystem4Id( final int solarSystemId ) {
		NeoComLogger.info( "SolarSystem: {}", solarSystemId + "" );
		return this.storeCacheManager.accessSolarSystem( solarSystemId ).blockingGet();
	}

	private void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getUniverseMarketsPrices();
		logger.info( ">> [ESIDataProvider.downloadItemPrices]> Download market prices: {} items", marketPrices.size() );
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put( price.getTypeId(), price );
		}
	}

	// - B U I L D E R
	public static class Builder {
		private ESIUniverseDataProvider onConstruction;

		public Builder() {
			this.onConstruction = new ESIUniverseDataProvider();
		}

		public Builder( final ESIUniverseDataProvider preInstance ) {
			if (null != preInstance) this.onConstruction = preInstance;
			else this.onConstruction = new ESIUniverseDataProvider();
		}

		public ESIUniverseDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			NeoItem.injectEsiUniverseDataAdapter( this.onConstruction );
			return this.onConstruction;
		}

		public ESIUniverseDataProvider.Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public ESIUniverseDataProvider.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public ESIUniverseDataProvider.Builder withRetrofitFactory( final RetrofitFactory retrofitFactory ) {
			Objects.requireNonNull( retrofitFactory );
			this.onConstruction.retrofitFactory = retrofitFactory;
			return this;
		}

		public ESIUniverseDataProvider.Builder withStoreCacheManager( final StoreCacheManager storeCacheManager ) {
			Objects.requireNonNull( storeCacheManager );
			this.onConstruction.storeCacheManager = storeCacheManager;
			return this;
		}
	}
}
