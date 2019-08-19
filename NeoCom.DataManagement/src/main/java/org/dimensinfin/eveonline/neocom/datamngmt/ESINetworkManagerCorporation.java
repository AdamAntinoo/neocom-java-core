//  PROJECT:     NeoCom.Microservices (NEOC.MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.datamngmt;

import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitHTTP;
import org.dimensinfin.eveonline.neocom.esiswagger.api.AssetsApi;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class ESINetworkManagerCorporation extends ESINetworkManagerZBase{
	// - S T A T I C - S E C T I O N ..........................................................................
	// - S T A T I C   S W A G G E R   I N T E R F A C E - C O R P O R A T I O N   A P I
	// --- A S S E T S
	public static List<GetCorporationsCorporationIdAssets200Ok> getCorporationsCorporationIdAssets( final int identifier
			, final String refreshToken
			, final String server ) {
		logger.info(">> [ESINetworkManagerCorporation.getCorporationsCorporationIdAssets]");
//		final Chrono accessFullTime = new Chrono();
		List<GetCorporationsCorporationIdAssets200Ok> returnAssetList = new ArrayList<>(1000);
		try {
			// Set the refresh to be used during the request.
			NeoComRetrofitHTTP.setRefeshToken(refreshToken);
			String datasource = GlobalDataManager.TRANQUILITY_DATASOURCE;
			if ( null != server ) datasource = server;
			// This request is paged. There can be more pages than one. The size limit seems to be 1000 but test for error.
			boolean morePages = true;
			int pageCounter = 1;
			while ( morePages ) {
				final Response<List<GetCorporationsCorporationIdAssets200Ok>> assetsApiResponse = neocomRetrofit
						.create(AssetsApi.class)
						.getCorporationsCorporationIdAssets(identifier, datasource, null, pageCounter, null).execute();
				if ( !assetsApiResponse.isSuccessful() ) {
					// Or error or we have reached the end of the list.
					return returnAssetList;
				} else {
					// Copy the assets to the result list.
					returnAssetList.addAll(assetsApiResponse.body());
					pageCounter++;
					// Check for out of page running.
					if ( assetsApiResponse.body().size() < 1 ) morePages = false;
				}
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( RuntimeException rtex ) {
			// Check if the problem is a connection reset.
			if ( rtex.getMessage().toLowerCase().contains("connection reset") ) {
				// Recreate the retrofit.
				logger.info("EX [ESINetworkManager.getCharactersCharacterIdMining]> Exception: {}", rtex.getMessage());
				//				neocomRetrofit = NeoComRetrofitHTTP.build(neocomAuth20, AGENT, cacheDataFile, cacheSize, timeout);
			}
		} finally {
//			logger.info("<< [ESINetworkManagerCorporation.getCorporationsCorporationIdAssets]> [TIMING] Full elapsed: {}", accessFullTime.printElapsed(Chrono.ChronoOptions.SHOWMILLIS));
		}
		return returnAssetList;
	}

}

