package org.dimensinfin.eveonline.neocom.app.adapters;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.datamngmt.ESIGlobalAdapter;

import com.apple.eawt.Application;

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
