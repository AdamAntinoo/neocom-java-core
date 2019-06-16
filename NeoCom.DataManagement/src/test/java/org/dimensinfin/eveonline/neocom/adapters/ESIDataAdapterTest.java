package org.dimensinfin.eveonline.neocom.adapters;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.support.TestAdapterReadyUp;

import org.junit.Assert;
import org.junit.Test;

public class ESIDataAdapterTest extends TestAdapterReadyUp {
	@Test
	public void builder_complete() throws IOException {
		final ESIDataAdapter adapter = this.setupRealAdapter();
		Assert.assertNotNull(adapter);
	}

	@Test
	public void fetchItem_notcached() throws IOException {
		final ESIDataAdapter adapter = this.setupRealAdapter();
		final GetUniverseTypesTypeIdOk item = adapter.searchEsiItem4Id(34);
		Assert.assertNotNull(item);
	}
}
