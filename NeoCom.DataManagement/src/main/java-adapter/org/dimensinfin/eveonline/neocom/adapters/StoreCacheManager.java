package org.dimensinfin.eveonline.neocom.adapters;

import java.io.IOException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.EsiItemV2;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import com.google.gson.Gson;
import com.nytimes.android.external.fs3.PathResolver;
import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;
import com.nytimes.android.external.store3.util.ParserException;
import io.reactivex.Single;
import okio.BufferedSource;

public class StoreCacheManager {
	// - C O M P O N E N T S
	private  ESIDataAdapter esiDataAdapter;

	// - C A C H E S
	private Store<EsiItemV2, Integer> esiItemStore;
	private Store<EsiItemV2, Integer> esiItemStore;

	// - C O N S T R U C T O R S
	private StoreCacheManager() { }

	private void createStores()  {
		this.createEsiItemStore();
	}

	private void createEsiItemStore() {
		this.esiItemStore = StoreBuilder.<Integer, BufferedSource, EsiItemV2>parsedWithKey()
				                    .fetcher((Fetcher)new EveItemFetcher(this.esiDataAdapter))
				                    .open();
	}
	private void createItemGroupStore() {
		this.esiItemStore = StoreBuilder.<Integer, BufferedSource, EsiItemV2>parsedWithKey()
				                    .fetcher((Fetcher)new EveItemFetcher(this.esiDataAdapter))
				                    .open();
	}

	// - B U I L D E R
	public static class Builder {
		private StoreCacheManager onConstruction;

		/**
		 * This Builder declares the mandatory components to be linked on construction so the Null validation is done as soon as possible.
		 */
		public Builder( final IConfigurationProvider configurationProvider
				, final IFileSystem fileSystemAdapter
				, final StoreCacheManager cacheManager ) {
			Objects.requireNonNull(configurationProvider);
			Objects.requireNonNull(fileSystemAdapter);
			Objects.requireNonNull(cacheManager);
			this.onConstruction = new StoreCacheManager();
		}
		public Builder withEsiDataAdapter( final ESIDataAdapter esiDataAdapter ) {
			this.onConstruction.esiDataAdapter = esiDataAdapter;
			return this;
		}

		public StoreCacheManager build() {
			Objects.requireNonNull(this.onConstruction.esiDataAdapter);
			this.onConstruction.createStores(); // Run the initialisation code.
			return this.onConstruction;
		}
	}

	// - F E T C H E R
	public static class EveItemFetcher implements Fetcher<EsiItemV2, Integer> {
		private  ESIDataAdapter esiDataAdapter;

		public EveItemFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<EsiItemV2> fetch( final Integer typeId ) {
			return Single.just(new EsiItemV2(this.esiDataAdapter.getUniverseTypeById(typeId)));
		}
	}

//	public static class NeoComPathResolver implements PathResolver<EsiItemV2> {
//		@Override
//		public String resolve( final EsiItemV2 key ) {
//			return Integer.valueOf(key.getTypeId()).toString();
//		}
//	}
//
//	public static class NeoComParser implements Parser<String, EsiItemV2> {
//		private static Gson gson = new Gson();
//
//		@Override
//		public EsiItemV2 apply( final String data ) throws ParserException {
//			final Parser<String, EsiItemV2> parser = GsonParserFactory.createStringParser(gson, EsiItemV2.class);
//			final EsiItemV2 result = parser.apply(data);
//
//			return result;
//		}
//	}
}
