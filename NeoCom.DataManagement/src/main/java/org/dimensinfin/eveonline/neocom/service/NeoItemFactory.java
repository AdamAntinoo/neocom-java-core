package org.dimensinfin.eveonline.neocom.service;

import org.dimensinfin.eveonline.neocom.domain.NeoItem;

public class NeoItemFactory {
	public static NeoItem getItemById( final Integer itemId ) {
		return new NeoItem( itemId );

	}

	private NeoItemFactory() {}
}