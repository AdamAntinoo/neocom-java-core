package org.dimensinfin.eveonline.neocom.model;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;

public class EveItemV2 {
	private static EveItemProvider eveItemProvider;
	private GetUniverseTypesTypeIdOk item ;
	protected getItem () {
		if ( null == this.item){
			this.item = this.getEveItemProvider().
		}
	}
}
