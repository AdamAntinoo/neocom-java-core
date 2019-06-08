package org.dimensinfin.eveonline.neocom.app.adapters;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;

public class NeoComComponentFactory {
	public static final String DEFAULT_ESI_SERVER = "Tranquility";

	private static NeoComComponentFactory singleton;

	// - C O M P O N E N T S
	private ESIDataAdapter esiDataAdapter;

	public ESIDataAdapter getEsiAdapter() {
		if (null == this.esiDataAdapter) {
			//			esiDataAdapter = new ESIDataAdapter.Builder(this.getConfigurationProvider(), this.getFileSystemAdapter())
			//					             .build();
		}
		return this.esiDataAdapter;
	}

}
