package org.dimensinfin.eveonline.neocom.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

/**
 * This is a provider for Eve item data and for other related Eve item information like price or special characteristics.
 * While the main purpose if to ease the access to item data, most of the item data itself is not usable so I will use this provider
 * as a gateway for other data that is highly connected to the item fields such as the item price or the item market data.
 *
 * @author Adam Antinoo
 */
public class EveItemProvider {
	private static final GetUniverseTypesTypeIdOk DUMMY_ITEM = new GetUniverseTypesTypeIdOk();
	private static Map<Integer, GetUniverseTypesTypeIdOk> itemCache = new HashMap<>(100);

	private ESIGlobalAdapter esiAdapter;

	// - C O N S T R U C T O R S
	private EveItemProvider( final ESIGlobalAdapter esiAdapter ) {
		this.esiAdapter = esiAdapter;
	}

	/**
	 * Get the Universe ESI data for a Eve item type. If the type is not found or there is a network problem then return a dummy item.
	 *
	 * @param typeId the unique eve identifier for a eve item type.
	 * @return a ESI data block with the item data or a dummy item if not found or network error.
	 */
	public GetUniverseTypesTypeIdOk search( final Integer typeId ) {
		try {
			if (itemCache.containsKey(typeId)) return itemCache.get(typeId);
			else {
				final GetUniverseTypesTypeIdOk item = this.esiAdapter.getUniverseTypeById(typeId, "tranquility");
				if (null != item) {
					itemCache.put(typeId, item);
					return item;
				} else return DUMMY_ITEM;
			}
		} catch (RuntimeException rtex) {
			return DUMMY_ITEM;
		} catch (Exception ex) {
			return DUMMY_ITEM;
		}
	}

	public GetUniverseTypesTypeIdOk delete( final Integer typeId ) {
		return itemCache.remove(typeId);
	}

	/**
	 * Search for the price on the ESI data service.
	 *
	 * @param typeId the item type we like to get the price.
	 * @return the ESI market price or -1.0 if the price is not found.
	 */
	public double getPrice( final int typeId ) {
		return this.esiAdapter.searchSDEMarketPrice(typeId);
	}

	// - B U I L D E R
	public static class Builder {
		private EveItemProvider onConstruction;

		public Builder( final ESIGlobalAdapter esiAdapter ) {
			this.onConstruction = new EveItemProvider(esiAdapter);
		}

		public EveItemProvider build() {
			Objects.requireNonNull(this.onConstruction.esiAdapter);
			return this.onConstruction;
		}
	}
}
