package org.dimensinfin.eveonline.neocom.adapters;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.domain.EsiItemV2;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import io.reactivex.Single;
import okio.BufferedSource;

public class StoreCacheManager {
	// - C O M P O N E N T S
	private ESIDataAdapter esiDataAdapter;

	// - C A C H E S
	private Store<EsiItemV2, Integer> esiItemStore;
	private Store<GetUniverseGroupsGroupIdOk, Integer> itemGroupStore;
	private Store<GetUniverseCategoriesCategoryIdOk, Integer> categoryStore;

	// - C O N S T R U C T O R S
	private StoreCacheManager() { }

	private void createStores() {
		this.createEsiItemStore();
		this.createItemGroupStore();
		this.createItemCategoryStore();
	}

	private void createEsiItemStore() {
		this.esiItemStore = StoreBuilder.<Integer, BufferedSource, EsiItemV2>parsedWithKey()
				                    .fetcher((Fetcher) new EveItemFetcher(this.esiDataAdapter))
				                    .open();
	}

	private void createItemGroupStore() {
		this.itemGroupStore = StoreBuilder.<Integer, BufferedSource, GetUniverseGroupsGroupIdOk>parsedWithKey()
				                      .fetcher((Fetcher) new ItemGroupFetcher(this.esiDataAdapter))
				                      .open();
	}

	private void createItemCategoryStore() {
		this.categoryStore = StoreBuilder.<Integer, BufferedSource, GetUniverseCategoriesCategoryIdOk>parsedWithKey()
				                     .fetcher((Fetcher) new ItemCategoryFetcher(this.esiDataAdapter))
				                     .open();
	}

	// - C A C H E   E X P O R T E D   A P I
	public Single<GetUniverseGroupsGroupIdOk> accessGroup( final Integer groupId ) {
		return this.itemGroupStore.get(groupId);
	}

	public Single<GetUniverseCategoriesCategoryIdOk> accessCategory( final Integer categoryId ) {
		return this.categoryStore.get(categoryId);
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

	// - E V E I T E M F E T C H E R
	public static class EveItemFetcher implements Fetcher<EsiItemV2, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public EveItemFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<EsiItemV2> fetch( final Integer typeId ) {
			return Single.just(new EsiItemV2(this.esiDataAdapter.getUniverseTypeById(typeId)));
		}
	}

	// - I T E M G R O U P F E T C H E R
	public static class ItemGroupFetcher implements Fetcher<GetUniverseGroupsGroupIdOk, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public ItemGroupFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<GetUniverseGroupsGroupIdOk> fetch( final Integer typeId ) {
			return Single.just(this.esiDataAdapter.getUniverseGroupById(typeId));
		}
	}

	// - I T E M C A T E G O R Y F E T C H E R
	public static class ItemCategoryFetcher implements Fetcher<GetUniverseCategoriesCategoryIdOk, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public ItemCategoryFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<GetUniverseCategoriesCategoryIdOk> fetch( final Integer typeId ) {
			return Single.just(this.esiDataAdapter.getUniverseCategoryById(typeId));
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
