//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.eveonline.neocom.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.parser.ApiPage;
import com.beimin.eveapi.parser.ApiRequest;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComConnector extends ApiConnector {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger	= Logger.getLogger("NeoComConnector");
	// - F I E L D - S E C T I O N ............................................................................
	private final ApiConnector	baseConnector;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComConnector() {
		baseConnector = null;
	}

	public NeoComConnector(final ApiConnector baseConnector) {
		this.baseConnector = baseConnector;
	}

	public boolean getAssetsFormat() {
		return true;
	}

	@Override
	public ApiConnector getNewInstance() {
		return new NeoComConnector(baseConnector);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	protected InputStream getInputStream(final URL requestUrl, final Map<String, String> params) throws ApiException {
		OutputStreamWriter wr = null;
		try {
			HttpsURLConnection conn = this.getSecureURLConnection(requestUrl, params);
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			StringBuilder data = new StringBuilder();
			for (Entry<String, String> entry : params.entrySet()) {
				if (data.length() > 0) {
					data.append("&"); // to ensure that we don't append an '&' to the end.
				}
				String key = entry.getKey();
				String value = entry.getValue();
				data.append(URLEncoder.encode(key, "UTF8"));
				data.append("=");
				data.append(URLEncoder.encode(value, "UTF8"));
			}
			wr.write(data.toString());
			wr.flush();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// FIXME Read the input stream data to check its values
				boolean flag = false;
				if (flag) {
					StringBuffer output = new StringBuffer("");
					InputStream stream = conn.getInputStream();

					BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						output.append(s);
					}
				}
				return conn.getInputStream();
			} else
				return conn.getErrorStream();
		} catch (Exception e) {
			throw new ApiException(e);
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					NeoComConnector.logger.warning("Error closing the stream");
				}
			}
		}
	}

	/**
	 * Add the flat flag when the page requested is related to the assets. This will generate a new format for
	 * the list of assets. New variation. The format of the assets, hierarchy or flat can be decided at the
	 * Application core so this should be implemented by the AppConnector.
	 */
	@Override
	protected Map<String, String> getParams(final ApiRequest request) {
		Map<String, String> par = super.getParams(request);
		if (this.getAssetsFormat()) if (request.getPage() == ApiPage.ASSET_LIST) {
			par.put("flat", "1");
		}
		return par;
	}

	private ApiConnector getConnector() {
		if (baseConnector != null) return baseConnector.getNewInstance();
		return super.getNewInstance();
	}

	private HttpsURLConnection getSecureURLConnection(final URL requestUrl, final Map<String, String> params) {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
			}

			public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
			}

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };
		// Install the all-trusting trust manager
		SSLContext sc;
		InputStream in = null;
		HttpsURLConnection con = null;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new Verifier();

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			con = (HttpsURLConnection) requestUrl.openConnection();
			//			in = con.getInputStream();
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					//No problem...
				}
			}
		}
		return con;
	}
}

final class Verifier implements HostnameVerifier {
	public boolean verify(final String hostname, final SSLSession session) {
		return true;
	}
}

// - UNUSED CODE ............................................................................................
