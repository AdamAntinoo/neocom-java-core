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
import retrofit2.Retrofit;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ACCEPT_LANGUAGE;
import static org.dimensinfin.eveonline.neocom.provider.ESIDataProvider.DEFAULT_ESI_SERVER;

public class ESIUniverseDataProvider {
	protected static final Logger logger = LoggerFactory.getLogger( ESIDataProvider.class );
	private Retrofit universeRetrofit;
	// - C O M P O N E N T S
	protected IConfigurationProvider configurationProvider;
	protected IFileSystem fileSystemAdapter;
	private RetrofitFactory retrofitFactory;
	protected StoreCacheManager storeCacheManager;
	// - I N T E R N A L   C A C H E S
	private static final Map<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap<>( 1200 );

	protected ESIUniverseDataProvider() {}

	private Retrofit accessUniverseRetrofit() {
		if (null == this.universeRetrofit) {
//			try {
			this.universeRetrofit = this.retrofitFactory.accessUniverseConnector();
//			} catch (final IOException ioe) {
//				NeoComLogger.error( ioe );
//				throw new NeoComRuntimeException( ErrorInfoCatalog.FILESYSTEM_FAILURE_RETROFIT_CACHE_RELATED );
//			}
		}
		return this.universeRetrofit;
	}

	// - P R O V I D E R   A P I
	public GetUniverseStationsStationIdOk getUniverseStationById( final Integer stationId ) {
		NeoComLogger.enter( "stationId: {}", stationId.toString() );
		try {
			final Response<GetUniverseStationsStationIdOk> stationResponse = this.accessUniverseRetrofit()
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
			final Response<GetUniverseSystemsSystemIdOk> systemResponse = this.accessUniverseRetrofit()
					.create( UniverseApi.class )
					.getUniverseSystemsSystemId( systemId
							, DEFAULT_ACCEPT_LANGUAGE
							, DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			logger.info( "EX [ESIUniverseDataProvider.getUniverseSystemById]> IOException during ESI data access: {}",
					ioe.getMessage() );
		}
		return null;
	}

	public GetUniverseConstellationsConstellationIdOk getUniverseConstellationById( final Integer constellationId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseConstellationsConstellationIdOk> systemResponse = this.accessUniverseRetrofit()
					.create( UniverseApi.class )
					.getUniverseConstellationsConstellationId( constellationId,
							DEFAULT_ACCEPT_LANGUAGE,
							DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			NeoComLogger
					.info( "EX [ESIUniverseDataProvider.getUniverseConstellationById]> IOException during ESI data access: {}",
							ioe.getMessage() );
		}
		return null;
	}

	public GetUniverseRegionsRegionIdOk getUniverseRegionById( final Integer regionId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseRegionsRegionIdOk> systemResponse = this.accessUniverseRetrofit()
					.create( UniverseApi.class )
					.getUniverseRegionsRegionId( regionId,
							DEFAULT_ACCEPT_LANGUAGE,
							DEFAULT_ESI_SERVER.toLowerCase(), null, null )
					.execute();
			if (systemResponse.isSuccessful()) return systemResponse.body();
		} catch (IOException ioe) {
			NeoComLogger.info( "EX [ESIUniverseDataProvider.getUniverseRegionById]> IOException during ESI data access: {}",
					ioe.getMessage() );
		}
		return null;
	}

	// - S D E   I N T E R N A L   D A T A
	public double searchSDEMarketPrice( final int typeId ) {
		logger.info( "-- [ESIDataProvider.searchSDEMarketPrice]> price for: {}", typeId );
		if (marketDefaultPrices.containsKey( typeId )) return marketDefaultPrices.get( typeId ).getAdjustedPrice();
		else return -1.0;
	}

	private void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getUniverseMarketsPrices();
		logger.info( ">> [ESIDataProvider.downloadItemPrices]> Download market prices: {} items", marketPrices.size() );
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put( price.getTypeId(), price );
		}
	}

	/**
	 * Go to the ESI api to get the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 * To access the public data it will use the current unauthorized retrofit connection.
	 */
	@TimeElapsed
	private List<GetMarketsPrices200Ok> getUniverseMarketsPrices() {
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = this.accessUniverseRetrofit()
					.create( MarketApi.class )
					.getMarketsPrices( DEFAULT_ESI_SERVER.toLowerCase(), null )
					.execute();
			if (!marketApiResponse.isSuccessful()) {
				return new ArrayList<>();
			} else return marketApiResponse.body();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new ArrayList<>();
		} catch (RuntimeException rte) {
			rte.printStackTrace();
			return new ArrayList<>();
		}
	}

	// - C A C H E D   A P I
	public GetUniverseTypesTypeIdOk searchEsiItem4Id( final int itemId ) {
		return this.storeCacheManager.accessItem( itemId ).blockingGet();
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		logger.info( "-- [ESIUniverseDataProvider.searchItemGroup4Id]> targetGroupId: {}", groupId );
		return this.storeCacheManager.accessGroup( groupId ).blockingGet();
	}

	@TimeElapsed
	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		logger.info( "-- [ESIUniverseDataProvider.searchItemCategory4Id]> categoryId: {}", categoryId );
		return this.storeCacheManager.accessCategory( categoryId ).blockingGet();
	}

	@TimeElapsed
	public GetUniverseSystemsSystemIdOk searchSolarSystem4Id( final int solarSystemId ) {
		logger.info( "-- [ESIUniverseDataProvider.searchItemCategory4Id]> categoryId: {}", solarSystemId );
		return this.storeCacheManager.accessSolarSystem( solarSystemId ).blockingGet();
	}

	// - C O R P O R A T I O N   P U B L I C   I N F O R M A T I O N
	public GetCorporationsCorporationIdOk getCorporationsCorporationId( final int identifier ) {
		logger.info( ">> [ESIDataProvider.getCorporationsCorporationId]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
//			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			// Use server parameter to override configuration server to use.
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCorporationsCorporationIdOk> corporationResponse = this.accessUniverseRetrofit()
					.create( CorporationApi.class )
					.getCorporationsCorporationId(
							identifier,
							DEFAULT_ESI_SERVER.toLowerCase(), null )
					.execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
//		} finally {
//			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	public GetCorporationsCorporationIdIconsOk getCorporationsCorporationIdIcons( final int identifier ) {
		logger.info( ">> [ESIDataProvider.getCorporationsCorporationIdIcons]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
//			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
//			String datasource = DEFAULT_ESI_SERVER;
			// Use server parameter to override configuration server to use.
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCorporationsCorporationIdIconsOk> corporationResponse = this.accessUniverseRetrofit()
					.create( CorporationApi.class )
					.getCorporationsCorporationIdIcons( identifier,
							DEFAULT_ESI_SERVER.toLowerCase(), null )
					.execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCorporationsCorporationIdIcons]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
//		} finally {
//			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// - A L L I A N C E   P U B L I C   I N F O R M A T I O N
	public GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier ) {
		logger.info( ">> [ESIDataProvider.getCorporationsCorporationId]" );
//		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
//			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = DEFAULT_ESI_SERVER;
			// Use server parameter to override configuration server to use.
//			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetAlliancesAllianceIdOk> allianceResponse = this.accessUniverseRetrofit()
					.create( AllianceApi.class )
					.getAlliancesAllianceId( identifier,
							datasource,
							null )
					.execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
//		} finally {
//			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	public GetAlliancesAllianceIdIconsOk getAlliancesAllianceIdIcons( final int identifier ) {
		logger.info( ">> [ESIDataProvider.getAlliancesAllianceIdIcons]" );
		try {
			final Response<GetAlliancesAllianceIdIconsOk> allianceResponse = this.accessUniverseRetrofit()
					.create( AllianceApi.class )
					.getAlliancesAllianceIdIcons(
							identifier,
							DEFAULT_ESI_SERVER, null )
					.execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			logger.error( "EX [ESIDataProvider.getAlliancesAllianceIdIcons]> [EXCEPTION]: {}", ioe.getMessage() );
			ioe.printStackTrace();
		}
		return null;
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

		public ESIUniverseDataProvider.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
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
//
//		public ESIUniverseDataProvider.Builder withRetrofitUniverseConnector( final RetrofitUniverseConnector retrofitUniverseConnector ) {
//			Objects.requireNonNull( retrofitUniverseConnector );
//			this.onConstruction.retrofitUniverseConnector = retrofitUniverseConnector;
//			return this;
//		}

		public ESIUniverseDataProvider build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			Objects.requireNonNull( this.onConstruction.retrofitFactory );
			Objects.requireNonNull( this.onConstruction.storeCacheManager );
			NeoItem.injectEsiUniverseDataAdapter( this.onConstruction );
			this.onConstruction.downloadItemPrices();
			return this.onConstruction;
		}
	}
}
