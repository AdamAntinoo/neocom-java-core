//  PROJECT:     NeoCom.DataManagement(NEOC.DM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java JRE 1.5 Specification.
//  DESCRIPTION: NeoCom pure Java library to maintain and manage all the data streams and
//                 connections. It will use the Models as the building blocks for the data
//                 and will isolate to the most the code from any platform implementation.
//               It will contain the Model Generators and use the external facilities for
//                 network connections to CCP XML api, CCP ESI api and Database storage. It
//                 will also make use of Cache facilities that will be glued at compilation
//                 time depending on destination platform.
package org.dimensinfin.eveonline.neocom.network;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tlabs.eve.EveNetwork;
import com.tlabs.eve.EveRequest;
import com.tlabs.eve.EveResponse;
import com.tlabs.eve.api.character.PlanetaryColoniesRequest;
import com.tlabs.eve.api.character.PlanetaryPinsRequest;
import com.tlabs.eve.net.DefaultEveNetwork;

import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This class will adapt the XML Eve api libraries results to a common api so different libraries will be used
 * as if sharing a common api.
 *
 * @author Adam Antinoo
 */
public class XMLNetworkAdapter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(XMLNetworkAdapter.class);

	// - F I E L D - S E C T I O N ............................................................................
	@JsonIgnore
	private transient NeoComCharacter _pilot = null;
//	private final ExecutorService _executor = Executors.newSingleThreadExecutor();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public XMLNetworkAdapter () {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public PlanetaryColoniesRequest PlanetaryColoniesRequest () {
		final PlanetaryColoniesRequest request = new PlanetaryColoniesRequest(Long.valueOf(_pilot.getCharacterID())
		                                                                          .toString());
		request.putParam("keyID", Integer.valueOf(_pilot.getAuthorization().getKeyID()).toString());
		request.putParam("vCode", _pilot.getAuthorization().getVCode());
		return request;
	}

	public PlanetaryPinsRequest PlanetaryPinsRequest (long planetid) {
		final PlanetaryPinsRequest request = new PlanetaryPinsRequest(Long.valueOf(_pilot.getCharacterID()).toString()
				, Long.valueOf(planetid).toString());
		request.putParam("keyID", Integer.valueOf(_pilot.getAuthorization().getKeyID()).toString());
		request.putParam("vCode", _pilot.getAuthorization().getVCode());
		return request;
	}

	public void setPilot (final NeoComCharacter pilot) {
		_pilot = pilot;
	}

	@Override
	public String toString () {
		final StringBuffer buffer = new StringBuffer("XMLNetworkAdapter [");
//		if ( null != _pilot ) buffer.append("Character: ").append(_pilot.getName());
		buffer.append(" ]");
		return buffer.toString();
	}

	public <T extends EveResponse> T execute (final EveRequest<T> request) {
		final EveNetwork eve = new DefaultEveNetwork();
		return eve.execute(request);
	}

//	public ExecutorService getActiveExecutor () {
//		return _executor;
//	}
//
//	public void shutdownExecutor () {
//		try {
//			System.out.println("attempt to shutdown executor");
//			_executor.shutdown();
//			_executor.awaitTermination(1, TimeUnit.MINUTES);
//		} catch (final InterruptedException iee) {
//			logger.info("W- [XMLNetworkAdapter.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
//		} finally {
//			if ( !_executor.isTerminated() ) {
//				logger.info("W- [XMLNetworkAdapter.shutdownExecutor]> Cancelling tasks. Grace time elapsed.");
//			}
//			_executor.shutdownNow();
//			logger.info("-- [XMLNetworkAdapter.shutdownExecutor]> Shutdown completed.");
//		}
//	}
}

// - UNUSED CODE ............................................................................................
