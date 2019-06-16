package org.dimensinfin.eveonline.neocom.adapters;

import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.support.TestConfigurationProvider;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.Single;

public class StoreCacheManagerTest {
	@Test
	public void builder_alldependencies() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("test-properties").build();
		final StoreCacheManager cacheManager = new StoreCacheManager.Builder()
				                                       .withEsiDataAdapter(esiDataAdapter)
				                                       .withConfigurationProvider(configurationProvider)
				                                       .build();
		Assert.assertNotNull(cacheManager);
	}

	@Test
	public void accessItem() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseTypesTypeIdOk item = new GetUniverseTypesTypeIdOk();
		Mockito.when(esiDataAdapter.getUniverseTypeById(Mockito.anyInt())).thenReturn(item);
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("test-properties").build();
		final StoreCacheManager cacheManager = new StoreCacheManager.Builder()
				                                       .withEsiDataAdapter(esiDataAdapter)
				                                       .withConfigurationProvider(configurationProvider)
				                                       .build();
		final Single<GetUniverseTypesTypeIdOk> itemRestored = cacheManager.accessItem(34);
		Assert.assertNotNull(itemRestored);
		final GetUniverseTypesTypeIdOk itemCached = cacheManager.accessItem(34).blockingGet();
		Assert.assertNotNull(itemCached);
	}

	@Test
	public void accessGroup() throws InterruptedException {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseGroupsGroupIdOk group = Mockito.mock(GetUniverseGroupsGroupIdOk.class);
		Mockito.when(esiDataAdapter.getUniverseGroupById(Mockito.anyInt())).thenReturn(group);
		Mockito.when(group.getName()).thenReturn("Capsuleer Bases");
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("test-properties").build();
		final StoreCacheManager cacheManager = new StoreCacheManager.Builder()
				                                       .withEsiDataAdapter(esiDataAdapter)
				                                       .withConfigurationProvider(configurationProvider)
				                                       .build();

		final Single<GetUniverseGroupsGroupIdOk> groupSingle = cacheManager.accessGroup(1082);
//		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Assert.assertNotNull(groupSingle);
		GetUniverseGroupsGroupIdOk obtained = groupSingle.blockingGet();
		Assert.assertNotNull(obtained);
		Assert.assertEquals("Check the value of the downloaded category.", "Capsuleer Bases", obtained.getName());

		Mockito.when(esiDataAdapter.getUniverseGroupById(Mockito.anyInt())).thenReturn(null);
		final Single<GetUniverseGroupsGroupIdOk> groupSingleCached = cacheManager.accessGroup(1082);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Assert.assertNotNull(groupSingleCached);
		obtained = groupSingleCached.blockingGet();
		Assert.assertNotNull(obtained);
		Assert.assertEquals("Check the value of the downloaded category already cached.", "Capsuleer Bases", obtained.getName());
	}

	@Test
	public void accessCategory() throws InterruptedException {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final TestConfigurationProvider configurationProvider = new TestConfigurationProvider.Builder("test-properties").build();
		final StoreCacheManager cacheManager = new StoreCacheManager.Builder()
				                                       .withEsiDataAdapter(esiDataAdapter)
				                                       .withConfigurationProvider(configurationProvider)
				                                       .build();
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.getUniverseCategoryById(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Planetary Interaction");

		final Single<GetUniverseCategoriesCategoryIdOk> categorySingle = cacheManager.accessCategory(41);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Assert.assertNotNull(categorySingle);
		GetUniverseCategoriesCategoryIdOk obtained = categorySingle.blockingGet();
		Assert.assertNotNull(obtained);
		Assert.assertEquals("Check the value of the downloaded category.", "Planetary Interaction", obtained.getName());

		Mockito.when(esiDataAdapter.getUniverseCategoryById(Mockito.anyInt())).thenReturn(null);
		final Single<GetUniverseCategoriesCategoryIdOk> categorySingleCached = cacheManager.accessCategory(41);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		Assert.assertNotNull(categorySingleCached);
		obtained = categorySingleCached.blockingGet();
		Assert.assertNotNull(obtained);
		Assert.assertEquals("Check the value of the downloaded category.", "Planetary Interaction", obtained.getName());
	}
}
