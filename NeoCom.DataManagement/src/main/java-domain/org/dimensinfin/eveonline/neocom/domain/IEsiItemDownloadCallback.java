package org.dimensinfin.eveonline.neocom.domain;

import org.dimensinfin.eveonline.neocom.services.DataDownloaderService;

public interface IEsiItemDownloadCallback {
	int getTypeId();

	void signalCompletion( DataDownloaderService.EsiItemSections section, Object item );
}
