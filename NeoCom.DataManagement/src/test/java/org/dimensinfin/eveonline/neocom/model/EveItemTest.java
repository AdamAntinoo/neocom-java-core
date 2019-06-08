package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class EveItemTest {
	@Test
	public void accessorContract() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		PojoTestUtils.validateAccessors(EveItem.class);
	}

	@Test
	public void isBlueprint_false() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.searchItemCategory4Id(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Capsuleer Bases");
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(34);
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isBlueprint());
	}

	@Test
	public void isBlueprint_true() {
		final ESIDataAdapter esiDataAdapter = Mockito.mock(ESIDataAdapter.class);
		final GetUniverseCategoriesCategoryIdOk category = Mockito.mock(GetUniverseCategoriesCategoryIdOk.class);
		Mockito.when(esiDataAdapter.searchItemCategory4Id(Mockito.anyInt())).thenReturn(category);
		Mockito.when(category.getName()).thenReturn("Energy Neutralizer Blueprint");
		EveItem.injectEsiDataAdapter(esiDataAdapter);
		final EveItem item = new EveItem().setTypeId(15799);
		Assert.assertNotNull(item);
		Assert.assertFalse(item.isBlueprint());
	}
}
