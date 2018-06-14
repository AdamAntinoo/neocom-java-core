//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt;

import org.dimensinfin.core.util.Chrono;
import org.dimensinfin.core.util.Chrono.ChronoOptions;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AllianceApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.CorporationApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.StatusApi;
import org.dimensinfin.eveonline.neocom.esiswagger.api.UniverseApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniversePlanetsPlanetIdOk;

import java.io.IOException;

import retrofit2.Response;

/**
 * This class downloads the OK data classes from the ESI api using the ESI authorization. It will then simply return the
 * results to the caller to be converted or to be used. There should be no exceptio interception so the caller can result any
 * unexpected situation.
 * The results should be stored on last access so if the network is down we can return last accessed data and not result into an
 * exception.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ESINetworkManager extends ESINetworkManagerCharacter {
	// - S T A T I C - S E C T I O N ..........................................................................
	// - S T A T I C   S W A G G E R   I N T E R F A C E - P U B L I C   A P I
	// --- S E R V E R
	public static GetStatusOk getStatus( final int identifier, final String refreshToken, final String
			server ) {
		logger.info(">> [ESINetworkManager.getUniversePlanetsPlanetId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetStatusOk> statusApiResponse = neocomRetrofit
					.create(StatusApi.class)
					.getStatus(datasource, null, null).execute();
			if (!statusApiResponse.isSuccessful()) {
				return null;
			} else return statusApiResponse.body();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniversePlanetsPlanetId]> [TIMING] Full elapsed: {}"
					, accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		logger.info("<<>>>> [ESINetworkManager.getUniversePlanetsPlanetId]");
		return null;
	}

	// - S T A T I C   S W A G G E R   I N T E R F A C E - U N I V E R S E   A P I
	// --- U N I V E R S E
	public static GetUniversePlanetsPlanetIdOk getUniversePlanetsPlanetId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getUniversePlanetsPlanetId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			//			final UniverseApi universeApiRetrofit = neocomRetrofit.create(UniverseApi.class);
			final Response<GetUniversePlanetsPlanetIdOk> universeApiResponse = neocomRetrofit
					.create(UniverseApi.class)
					.getUniversePlanetsPlanetId(identifier, datasource, null, null).execute();
			if (!universeApiResponse.isSuccessful()) {
				return null;
			} else return universeApiResponse.body();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getUniversePlanetsPlanetId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// - S T A T I C   S W A G G E R   I N T E R F A C E - C O R P O R A T I O N   A P I
	// --- C O R P O R A T I O N   P U B L I C   I N F O R M A T I O N
	public static GetCorporationsCorporationIdOk getCorporationsCorporationId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCorporationsCorporationId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetCorporationsCorporationIdOk> corporationResponse = neocomRetrofit
					.create(CorporationApi.class)
					.getCorporationsCorporationId(identifier, datasource, null, null).execute();
			if (corporationResponse.isSuccessful())
				return corporationResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}

	// --- A L L I A N C E   P U B L I C   I N F O R M A T I O N
	public static GetAlliancesAllianceIdOk getAlliancesAllianceId( final int identifier, final String refreshToken, final String server ) {
		logger.info(">> [ESINetworkManager.getCorporationsCorporationId]");
		final Chrono accessFullTime = new Chrono();
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.SERVER_DATASOURCE;
			// Use server parameter to override configuration server to use.
			if (null != server) datasource = server;
			// Create the request to be returned so it can be called.
			final Response<GetAlliancesAllianceIdOk> allianceResponse = neocomRetrofit
					.create(AllianceApi.class)
					.getAlliancesAllianceId(identifier, datasource, null, null).execute();
			if (allianceResponse.isSuccessful())
				return allianceResponse.body();
		} catch (IOException ioe) {
			logger.error("EX [ESINetworkManager.getCorporationsCorporationId]> [EXCEPTION]: {}", ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			logger.info("<< [ESINetworkManager.getCorporationsCorporationId]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(ChronoOptions.SHOWMILLIS));
		}
		return null;
	}
}
// - UNUSED CODE ............................................................................................
//[01]
