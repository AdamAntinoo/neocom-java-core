package org.dimensinfin.eveonline.neocom.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.datamngmt.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;
import org.dimensinfin.eveonline.neocom.domain.EsiItemV2;
import org.dimensinfin.eveonline.neocom.esiswagger.api.MarketApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import io.reactivex.Single;
import retrofit2.Response;

/**
 * This class will be the base to access most of the non authenticated SDE data available though the ESI data service.
 *
 * The new data service allows to access many endpoints with data that do not require pilot authentication. With this endpoints I will try to remove
 * the required SDE database and remove the need to add that heavy resource to the application download when implemented in Android.
 *
 * This class will also use other components to be able to store downloaded SDE data into caches, be them temporal in memory or persisted on disk.
 */
public class ESIDataAdapter {
	public static final String DEFAULT_ESI_SERVER = "Tranquility";
	public static final String DEFAULT_ACCEPT_LANGUAGE = "en-us";
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	private static ESIDataAdapter singleton;
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(100);

	private static File storeDataFile;
	private static Gson gson = new Gson();
	//	private static Retrofit neocomRetrofitNoAuth;
	//	private static Store<EsiItemV2, Integer> esiItemStore;
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private NeoComRetrofitFactory retrofitFactory;
	private StoreCacheManager cacheManager;

	// - C O N S T R U C T O R S
	private ESIDataAdapter( final IConfigurationProvider newConfigurationProvider
			, final IFileSystem newFileSystemAdapter
			, final StoreCacheManager newCacheManager ) {
		configurationProvider = newConfigurationProvider;
		fileSystemAdapter = newFileSystemAdapter;
		cacheManager = newCacheManager;
	}

	public Single<EsiItemV2> getEsiItem4Id( final Integer itemId ) {
		return esiItemStore.fetch(itemId);
	}

	// - D O W N L O A D   S T A R T E R S
	public void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getMarketsPrices(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIDataAdapter.downloadItemPrices]> Download market prices: {} items", marketPrices.size());
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put(price.getTypeId(), price);
		}
	}

	// - S D E   D A T A
	public double searchSDEMarketPrice( final int typeId ) {
		if (marketDefaultPrices.containsKey(typeId)) return marketDefaultPrices.get(typeId).getAdjustedPrice();
		else return -1.0;
	}

	public GetUniverseGroupsGroupIdOk searchItemGroup4Id( final int groupId ) {
		logger.info("-- [ESIDataAdapter.searchItemGroup4Id]> targetGroupId: {}", groupId);
		return this.cacheManager.accessGroup(groupId).blockingGet();
	}

	public GetUniverseCategoriesCategoryIdOk searchItemCategory4Id( final int categoryId ) {
		logger.info("-- [ESIDataAdapter.searchItemCategory4Id]> targetGroupId: {}", categoryId);
		return this.cacheManager.accessCategory(categoryId).blockingGet();
	}

	// - U N I V E R S E

	/**
	 * Go to the ESI api to get the list of market prices. This method does not use other server than the Tranquility
	 * because probably there is not valid market price information at other servers.
	 * To access the public data it will use the current unauthorized retrofit connection.
	 */
	public List<GetMarketsPrices200Ok> getMarketsPrices() {
		try {
			// Create the request to be returned so it can be called.
			final Response<List<GetMarketsPrices200Ok>> marketApiResponse = retrofitFactory.accessNoAuthRetrofit().create(MarketApi.class)
					                                                                .getMarketsPrices(DEFAULT_ESI_SERVER.toLowerCase(), null)
					                                                                .execute();
			if (!marketApiResponse.isSuccessful()) {
				return new ArrayList<>();
			} else return marketApiResponse.body();
		} catch (IOException ioe) {
			return new ArrayList<>();
		}
	}

	public GetUniverseGroupsGroupIdOk getUniverseGroupById( final Integer groupId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseGroupsGroupIdOk> groupResponse = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                           .getUniverseGroupsGroupId(groupId
							                                                           , DEFAULT_ACCEPT_LANGUAGE
							                                                           , DEFAULT_ESI_SERVER, null, null)
					                                                           .execute();
			if (!groupResponse.isSuccessful()) {
				return null;
			} else return groupResponse.body();
		} catch (IOException ioe) {
			return null;
		}
	}

	public GetUniverseCategoriesCategoryIdOk getUniverseCategoryById( final Integer categoryId ) {
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseCategoriesCategoryIdOk> groupResponse = retrofitFactory.accessNoAuthRetrofit().create(UniverseApi.class)
					                                                                  .getUniverseCategoriesCategoryId(categoryId
							                                                                  , DEFAULT_ACCEPT_LANGUAGE
							                                                                  , DEFAULT_ESI_SERVER, null, null)
					                                                                  .execute();
			if (!groupResponse.isSuccessful()) {
				return null;
			} else return groupResponse.body();
		} catch (IOException ioe) {
			return null;
		}
	}


	//	private void createStore() throws IOException {
	//		// Create persistence store area
	//		final String storeFilePath = configurationProvider.getResourceString("P.cache.directory.path")
	//				                             + "/"
	//				                             + configurationProvider.getResourceString("P.cache.store.filename");
	//		storeDataFile = new File(fileSystemAdapter.accessResource4Path(storeFilePath));
	//		final NeoComParser parser = new NeoComParser();
	//		;
	//
	//		// Create store
	//		esiItemStore = StoreBuilder.<Integer, BufferedSource, EsiItemV2>parsedWithKey()
	//				               .fetcher((Fetcher) this)  // OkHttp responseBody.source()
	//				               .persister(FileSystemPersister.create(FileSystemFactory.create(storeDataFile), new NeoComPathResolver()))
	//				               .parser(GsonParserFactory.createSourceParser(gson, String.class))
	//				               .open();
	//		// Create retrofit
	//		//		if (null == neocomRetrofitNoAuth) neocomRetrofitNoAuth = retrofitFactory.generateNoAuthRetrofit();
	//	}

	// - B U I L D E R
	public static class Builder {
		private ESIDataAdapter onConstruction;

		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as possible.
		 */
		public Builder( final IConfigurationProvider configurationProvider
				, final IFileSystem fileSystemAdapter
				, final StoreCacheManager cacheManager ) {
			Objects.requireNonNull(configurationProvider);
			Objects.requireNonNull(fileSystemAdapter);
			Objects.requireNonNull(cacheManager);
			this.onConstruction = new ESIDataAdapter(configurationProvider, fileSystemAdapter, cacheManager);
		}

		public ESIDataAdapter build() throws IOException {
			//			retrofitFactory = new NeoComRetrofitFactory.Builder(configurationProvider, fileSystemAdapter).build();
			singleton = this.onConstruction;
			//			this.onConstruction.createStore(); // Run the initialisation code.
			return this.onConstruction;
		}
	}

	/**
	 * Search for the item on the current downloaded items cache. If not found then go for it to the network.
	 */
	public GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId ) {
		final GetUniverseTypesTypeIdOk item = this.getUniverseTypeById(typeId, "tranquility");
		//		return getUniverseTypeById("tranquility", typeId);
		return item;
	}

	@Deprecated
	private GetUniverseTypesTypeIdOk getUniverseTypeById( final int typeId, final String server ) {
		//		logger.info(">> [ESINetworkManagerMock.getUniverseTypeById]");
		final DateTime startTimePoint = DateTime.now();
		try {
			// Create the request to be returned so it can be called.
			final Response<GetUniverseTypesTypeIdOk> itemListResponse = retrofitFactory.accessNoAuthRetrofit()
					                                                            .create(UniverseApi.class)
					                                                            .getUniverseTypesTypeId(typeId
							                                                            , "en-us"
							                                                            , server
							                                                            , null
							                                                            , null)
					                                                            .execute();
			if (!itemListResponse.isSuccessful()) {
				return null;
			} else {
				logger.info("-- [ESINetworkManager.getUniverseTypeById]> Downloading: {}-{}"
						, itemListResponse.body().getTypeId()
						, itemListResponse.body().getName());
				return itemListResponse.body();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException runtime) {
			runtime.printStackTrace();
		} finally {
			//			logger.info("<< [ESINetworkManager.getUniverseTypeById]> [TIMING] Full elapsed: {}"
			//					, new Duration(startTimePoint, DateTime.now()).getMillis() + "ms");
		}
		return null;
	}
}
