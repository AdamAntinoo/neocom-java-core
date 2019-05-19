package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetMarketsPrices200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

import retrofit2.Response;

public class ESIGlobalAdapter extends ESINetworkManager {
	private static final HashMap<Integer, GetMarketsPrices200Ok> marketDefaultPrices = new HashMap(1000);
	private static Map<Integer, GetUniverseRaces200Ok> racesCache = new HashMap<>();
	private static Map<Integer, GetUniverseAncestries200Ok> ancestriesCache = new HashMap<>();
	private static Map<Integer, GetUniverseBloodlines200Ok> bloodLinesCache = new HashMap<>();

	// - D O W N L O A D   S T A R T E R S
	public void downloadItemPrices() {
		// Initialize and process the list of market process form the ESI full market data.
		final List<GetMarketsPrices200Ok> marketPrices = this.getMarketsPrices(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadItemPrices]> Download market prices: {} items", marketPrices.size());
		for (GetMarketsPrices200Ok price : marketPrices) {
			marketDefaultPrices.put(price.getTypeId(), price);
		}
	}

	public void downloadPilotFamilyData() {
		// Download race, bloodline and other pilot data.
		final List<GetUniverseRaces200Ok> racesList = this.getRaces(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadItemPrices]> Download race: {} items", racesList.size());
		for (GetUniverseRaces200Ok race : racesList) {
			racesCache.put(race.getRaceId(), race);
		}
		final List<GetUniverseAncestries200Ok> ancestriesList = this.getAncestries(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadItemPrices]> Download ancestries: {} items", racesList.size());
		for (GetUniverseAncestries200Ok ancestry : ancestriesList) {
			ancestriesCache.put(ancestry.getId(), ancestry);
		}
		final List<GetUniverseBloodlines200Ok> bloodLineList = this.getBloodlines(GlobalDataManager.TRANQUILITY_DATASOURCE);
		logger.info(">> [ESIGlobalAdapter.downloadItemPrices]> Download blood lines: {} items", racesList.size());
		for (GetUniverseBloodlines200Ok bloodLine : bloodLineList) {
			bloodLinesCache.put(bloodLine.getBloodlineId(), bloodLine);
		}
	}

	private List<GetUniverseRaces200Ok> getRaces( final String datasource ) {
		logger.info(">> [ESIGlobalAdapter.getRaces]");
		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseRaces200Ok>> racesList = neocomRetrofitNoAuth.create(UniverseApi.class)
					                                                        .getUniverseRaces("en-us", datasource, null, "en-us")
					                                                        .execute();
			if (racesList.isSuccessful()) return racesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getRaces]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	private static List<GetUniverseAncestries200Ok> getAncestries( final String datasource ) {
		logger.info(">> [ESIGlobalAdapter.getAncestries]");
		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseAncestries200Ok>> ancestriesList = neocomRetrofitNoAuth.create(UniverseApi.class)
					                                                                  .getUniverseAncestries("en-us", datasource, null, "en-us")
					                                                                  .execute();
			if (ancestriesList.isSuccessful()) return ancestriesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getAncestries]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	private static List<GetUniverseBloodlines200Ok> getBloodlines( final String datasource ) {
		logger.info(">> [ESIGlobalAdapter.getBloodlines]");
		final Chrono accessFullTime = new Chrono();
		try {
			final Response<List<GetUniverseBloodlines200Ok>> bloodLinesList = neocomRetrofitNoAuth.create(UniverseApi.class)
					                                                                  .getUniverseBloodlines("en-us", datasource, null, "en-us")
					                                                                  .execute();
			if (bloodLinesList.isSuccessful()) return bloodLinesList.body();
			else return new ArrayList<>();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getBloodlines]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return new ArrayList<>();
	}

	public double searchSDEMarketPrice( final int typeId ) {
		if (marketDefaultPrices.containsKey(typeId)) return marketDefaultPrices.get(typeId).getAdjustedPrice();
		else return -1.0;
	}

	public GetUniverseRaces200Ok searchSDERace( final int identifier ) {
//		GetUniverseRaces200Ok hit = racesCache.get(identifier);
//		if (null == hit) hit = new GetUniverseRaces200Ok();
//		return hit;
		return racesCache.get(identifier);
	}

	public GetUniverseAncestries200Ok searchSDEAncestry( final int identifier ) {
//		GetUniverseAncestries200Ok hit = ancestriesCache.get(identifier);
//		if (null == hit) hit = new GetUniverseAncestries200Ok();
//		return hit;
		return ancestriesCache.get(identifier);
	}

	public GetUniverseBloodlines200Ok searchSDEBloodline( final int identifier ) {
//		GetUniverseBloodlines200Ok hit = bloodLinesCache.get(identifier);
//		if (null == hit) hit = new GetUniverseBloodlines200Ok();
//		return hit;
		return bloodLinesCache.get(identifier);
	}

	public void initialise() {
		super.initialise();
		//		this.cacheInitialisation();
	}

	private void cacheInitialisation() {
		this.downloadItemPrices();
	}
}
