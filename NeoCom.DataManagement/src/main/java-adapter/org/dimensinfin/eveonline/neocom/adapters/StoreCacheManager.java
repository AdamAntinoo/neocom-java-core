package org.dimensinfin.eveonline.neocom.adapters;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.model.EveItem;

import com.nytimes.android.external.store3.base.Fetcher;
import com.nytimes.android.external.store3.base.impl.Store;
import com.nytimes.android.external.store3.base.impl.StoreBuilder;
import io.reactivex.Single;
import okio.BufferedSource;

public class StoreCacheManager {
	// - C O M P O N E N T S
	private ESIDataAdapter esiDataAdapter;

	// - C A C H E S
	private Store<EveItem, Integer> esiItemStore;
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
		this.esiItemStore = StoreBuilder.<Integer, BufferedSource, EveItem>parsedWithKey()
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
	public Single<EveItem> accessItem( final Integer itemId ) {
		return this.esiItemStore.get(itemId);
	}

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
		public Builder() {
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
	public static class EveItemFetcher implements Fetcher<EveItem, Integer> {
		private ESIDataAdapter esiDataAdapter;

		public EveItemFetcher( final ESIDataAdapter esiDataAdapter ) {
			this.esiDataAdapter = esiDataAdapter;
		}

		@Override
		public Single<EveItem> fetch( final Integer typeId ) {
			return Single.just(new EveItem(this.esiDataAdapter.getUniverseTypeById(typeId)));
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
}
