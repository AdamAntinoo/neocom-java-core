package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.EveItemProvider;

public class EsiItemV2 {
	private static EveItemProvider eveItemProvider;
	private GetUniverseTypesTypeIdOk item;

	public static void injectEveItemProvider( final EveItemProvider newEveItemProvider ) {
		eveItemProvider = newEveItemProvider;
	}

	protected GetUniverseTypesTypeIdOk getItem() {
		if (null == this.item) {
			this.item = this.getEveItemProvider().getPrice();
		}
	}

	private EveItemProvider getEveItemProvider() {
		Objects.requireNonNull(eveItemProvider);
		return eveItemProvider;
	}
}
