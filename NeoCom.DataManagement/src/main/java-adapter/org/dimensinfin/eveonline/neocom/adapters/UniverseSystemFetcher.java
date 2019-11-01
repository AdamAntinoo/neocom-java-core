package org.dimensinfin.eveonline.neocom.adapters;

import com.nytimes.android.external.store3.base.Fetcher;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseSystemsSystemIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIUniverseDataProvider;

import io.reactivex.Single;

public class UniverseSystemFetcher implements Fetcher<GetUniverseSystemsSystemIdOk, Integer> {
	private ESIUniverseDataProvider esiUniverseDataProvider;

	public UniverseSystemFetcher( final ESIUniverseDataProvider esiUniverseDataProvider ) {
		this.esiUniverseDataProvider = esiUniverseDataProvider;
	}

	@Override
	public Single<GetUniverseSystemsSystemIdOk> fetch( final Integer systemId ) {
		return Single.just( this.esiUniverseDataProvider.getUniverseSystemById( systemId ) );
	}
}
