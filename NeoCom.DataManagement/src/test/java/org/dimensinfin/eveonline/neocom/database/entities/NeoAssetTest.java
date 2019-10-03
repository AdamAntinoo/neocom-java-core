package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.support.ESIDataAdapterSupportTest;

public class NeoAssetTest extends ESIDataAdapterSupportTest {
//	@Test
	public void gettersContract() {
		EveItem.injectEsiDataAdapter(this.esiDataAdapter);
		final GetCharactersCharacterIdAssets200Ok esiAsset = new GetCharactersCharacterIdAssets200Ok();
		esiAsset.setItemId(1234567L);
		esiAsset.setTypeId(34);
		esiAsset.setQuantity(10);
		final NeoAsset asset = new NeoAsset.Builder().fromEsiAsset(esiAsset);
		Assert.assertNotNull(asset);
		Assert.assertEquals(34, asset.getTypeId());
		Assert.assertEquals(4, asset.getCategoryId());
		Assert.assertEquals("Material", asset.getCategoryName());
		Assert.assertEquals(18, asset.getGroupId());
		Assert.assertEquals("Mineral", asset.getGroupName());
		Assert.assertEquals(10, asset.getQuantity().longValue());
		Assert.assertEquals("Tritanium", asset.getName());
		Assert.assertEquals("Tech I", asset.getTech());
		Assert.assertEquals(0.01, asset.getVolume(), 0.01);
		Assert.assertEquals(false, asset.isBlueprint());
		Assert.assertEquals("http://image.eveonline.com/Type/34_64.png", asset.getURLForItem());
	}

	@Test
	public void build_complete() {
		final GetCharactersCharacterIdAssets200Ok esiAsset = new GetCharactersCharacterIdAssets200Ok();
		esiAsset.setItemId(1234567L);
		esiAsset.setTypeId(34);
		esiAsset.setQuantity(10);
		final NeoAsset asset = new NeoAsset.Builder().fromEsiAsset(esiAsset);
		Assert.assertNotNull(asset);
	}
}