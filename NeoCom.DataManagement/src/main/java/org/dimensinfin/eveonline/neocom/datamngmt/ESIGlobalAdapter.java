package org.dimensinfin.eveonline.neocom.datamngmt;

import java.util.HashMap;
import java.util.List;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;

public class ESIGlobalAdapter extends ESINetworkManager {
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(1000);

	protected void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = ESINetworkManager.getMarketsPrices(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadItemPrices]> Download market prices: {} items", marketPrices.size());
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put(price.getTypeId(), price);
		}
	}

	public void initialise() {
		super.initialise();
		this.cacheInitialisation();
	}

	private void cacheInitialisation() {
		this.downloadItemPrices();
	}
}
