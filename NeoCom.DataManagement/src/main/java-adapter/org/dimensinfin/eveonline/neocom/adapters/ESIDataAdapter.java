package org.dimensinfin.eveonline.neocom.adapters;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.datamngmt.ESINetworkManager;
import org.dimensinfin.eveonline.neocom.domain.EsiItemV2;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.nytimes.android.external.fs3.FileSystemPersister;
import com.nytimes.android.external.fs3.PathResolver;
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import com.nytimes.android.external.store3.util.ParserException;
import io.reactivex.Single;
import okio.BufferedSource;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This class will be the base to access most of the non authenticated SDE data available though the ESI data service.
 *
 * The new data service allows to access many endpoints with data that do not require pilot authentication. With this endpoints I will try to remove
 * the required SDE database and remove the need to add that heavy resource to the application download when implemented in Android.
 *
 * This class will also use other components to be able to store downloaded SDE data into caches, be them temporal in memory or persisted on disk.
 */
public class ESIDataAdapter implements Fetcher<EsiItemV2, Integer> {
	protected static Logger logger = LoggerFactory.getLogger(ESINetworkManager.class);
	private static ESIDataAdapter singleton;
	private static File storeDataFile;
	private static Gson gson = new Gson();
	private static Retrofit neocomRetrofitNoAuth;
	private static Store<EsiItemV2, Integer> esiItemStore;
	// - C O M P O N E N T S
	private static IConfigurationProvider configurationProvider;
	private static IFileSystem fileSystemAdapter;
	private static NeoComRetrofitFactory retrofitFactory;

	private ESIDataAdapter( final IConfigurationProvider newConfigurationProvider, final IFileSystem newFileSystemAdapter ) {
		configurationProvider = newConfigurationProvider;
		fileSystemAdapter = newFileSystemAdapter;
	}

	private void createStore() throws IOException {
		// Create persistence store area
		final String storeFilePath = configurationProvider.getResourceString("P.cache.directory.path")
				                             + "/"
				                             + configurationProvider.getResourceString("P.cache.store.filename");
		storeDataFile = new File(fileSystemAdapter.accessResource4Path(storeFilePath));
		final NeoComParser parser = new NeoComParser();
		;

		// Create store
		esiItemStore = StoreBuilder.<Integer, BufferedSource, EsiItemV2>parsedWithKey()
				               .fetcher((Fetcher) this)  // OkHttp responseBody.source()
				               .persister(FileSystemPersister.create(FileSystemFactory.create(storeDataFile), new NeoComPathResolver()))
				               .parser(GsonParserFactory.createSourceParser(gson, String.class))
				               .open();
		// Create retrofit
		if (null == neocomRetrofitNoAuth) neocomRetrofitNoAuth = retrofitFactory.generateNoAuthRetrofit();
	}

	public Single<EsiItemV2> getEsiItem4Id( final Integer itemId ) {
		return esiItemStore.fetch(itemId);
	}

	// - B U I L D E R
	public static class Builder {
		private ESIDataAdapter onConstruction;

		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as possible.
		 */
		public Builder( final IConfigurationProvider configurationProvider, final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull(configurationProvider);
			Objects.requireNonNull(fileSystemAdapter);
			this.onConstruction = new ESIDataAdapter(configurationProvider, fileSystemAdapter);
		}

		public ESIDataAdapter build() throws IOException {
			retrofitFactory = new NeoComRetrofitFactory.Builder(configurationProvider, fileSystemAdapter).build();
			singleton = this.onConstruction;
			this.onConstruction.createStore(); // Run the initialisation code.
			return this.onConstruction;
		}
	}

	// - F E T C H E R
	@Override
	public Single<EsiItemV2> fetch( final Integer typeId ) {
		return Single.just(new EsiItemV2(this.getUniverseTypeById(typeId)));
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
			final Response<GetUniverseTypesTypeIdOk> itemListResponse = neocomRetrofitNoAuth
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

final class NeoComPathResolver implements PathResolver<EsiItemV2> {
	@Override
	public String resolve( final EsiItemV2 key ) {
		return Integer.valueOf(key.getTypeId()).toString();
	}
}

final class NeoComParser implements Parser<String, EsiItemV2> {
	private static Gson gson = new Gson();

	@Override
	public EsiItemV2 apply( final String data ) throws ParserException {
		final Parser<String, EsiItemV2> parser = GsonParserFactory.createStringParser(gson, EsiItemV2.class);
		final EsiItemV2 result = parser.apply(data);

		return result;
	}
}
