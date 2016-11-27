//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.lab;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.nikr.eve.jeveasset.data.Citadel;

// - CLASS IMPLEMENTATION ...................................................................................
public class CheckersApplication {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("org.dimensinfin.lab");

	public static void main(final String[] args) {
		CheckersApplication checker = new CheckersApplication();
		checker.citadelLocationUpdate();
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public CheckersApplication() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	private void citadelLocationUpdate() {
		logger.info(">> [TimeTicketReceiver.citadelLocationUpdate]> Citadels updating");
		//		 CitadelSettings	citadelSettings	= new CitadelSettings();
		//		if (citadelSettings.getNextUpdate().after(new Date()) && true && true) { //Check if we can update now
		//			//				if (updateTask != null) {
		//			//					updateTask.addError(DialoguesUpdate.get().citadel(), "Not allowed yet.\r\n(Fix: Just wait a bit)");
		//			//				}
		//			logger.info("	Citadels failed to update (NOT ALLOWED YET)");
		//			return;
		//		}
		// Update citadel
		InputStream in = null;
		try { //Update from API
			ObjectMapper mapper = new ObjectMapper(); //create once, reuse
			URL url = new URL("https://stop.hammerti.me.uk/api/citadel/all");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept-Encoding", "gzip");

			long contentLength = con.getContentLength();
			String contentEncoding = con.getContentEncoding();
			InputStream inputStream = con.getInputStream();
			if ("gzip".equals(contentEncoding)) {
				in = new GZIPInputStream(inputStream);
			} else {
				in = inputStream;
			}
			Map<Long, Citadel> results = mapper.readValue(in, new TypeReference<Map<Long, Citadel>>() {
			});
			if (results != null) { //Updated OK
				for (Map.Entry<Long, Citadel> entry : results.entrySet()) {
					// Convert each Citadel to a new Location and update the database if needed.
					//					EveLocation loc = new EveLocation(entry.getKey(), entry.getValue());
					int dummy = 1;
					//					citadelSettings.put(entry.getKey(), entry.getValue());
					//					saveCitadel(entry.getKey(), entry.getValue());
				}
			}
			//			citadelSettings.setNextUpdate();
			//				saveCitadel(citadelSettings);
			logger.info("	Updated citadels for jEveAssets");
		} catch (IOException ex) {
			ex.printStackTrace();
			//				if (updateTask != null) {
			//					updateTask.addError(DialoguesUpdate.get().citadel(), ex.getMessage());
			//				}
			//				logger.("	Citadels failed to update", ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					//No problem...
				}
			}
		}
	}
}

// - UNUSED CODE ............................................................................................
