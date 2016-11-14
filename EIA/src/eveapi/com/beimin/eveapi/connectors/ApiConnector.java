package com.beimin.eveapi.connectors;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.beimin.eveapi.core.AbstractContentHandler;
import com.beimin.eveapi.core.ApiAuth;
import com.beimin.eveapi.core.ApiRequest;
import com.beimin.eveapi.core.ApiResponse;
import com.beimin.eveapi.exception.ApiException;

public class ApiConnector {
	private static final Logger							LOG										= LoggerFactory.getLogger(ApiConnector.class);
	private static HashMap<String, String>	recordedXMLResponses	= new HashMap<String, String>();
	static {
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/CharacterSheet.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444&version=1",
						"charCharacterSheet_94004444.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/AssetList.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444&version=2",
						"charAssetList_94004444.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/Blueprints.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444&version=2",
						"charBlueprints_94004444.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/IndustryJobs.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444&version=2",
						"charIndustryJobs_94004444.xml");
		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/MarketOrders.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94004444&version=2",
						"charMarketOrders_94004444.xml");

		recordedXMLResponses
				.put(
						"https://api.eveonline.com/char/AssetList.xml.aspx?keyID=900001&vCode=u2Idzk1ymAufjgwwrQoHl0uTRa7fKwWNxotzZiEoLm0NgEHCdqpBr1C8pjmWdbiy&characterID=94005555&version=2",
						"charAssetList_94005555.xml");

		recordedXMLResponses
				.put(
						"https://api.eveonline.com/account/AccountStatus.xml.aspx?keyID=4579233&vCode=hJ2yJ5p8mP8hCL3DmYi7oy2nGoMswsbXYycS3EQgbGrJRpUxjZRKY88VbeGwwQAo",
						"accountAccountStatus_900001.xml");

		//		recordedXMLResponses	= new HashMap<String, String>();
	}
	public static final String							EVE_API_URL						= "https://api.eveonline.com";
	private final String										baseUrl;

	public ApiConnector() {
		baseUrl = EVE_API_URL;
	}

	public ApiConnector(final String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public <E extends ApiResponse> E execute(final ApiRequest request, final AbstractContentHandler contentHandler,
			final Class<E> clazz) throws ApiException {
		try {
			return getApiResponse(contentHandler, getInputStream(getURL(request), getParams(request)), clazz);
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}

	public ApiConnector getInstance() {
		return new ApiConnector(baseUrl);
	}

	@SuppressWarnings("unchecked")
	protected <E> E getApiResponse(final AbstractContentHandler contentHandler, final InputStream inputStream,
			final Class<E> clazz) throws ApiException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(contentHandler);
			xr.parse(new InputSource(inputStream));
			return (E) contentHandler.getResponse();
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}

	protected String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Modified inside the EveDroid project to allow the use of offline resources from the assets storage like
	 * the pattern used on the application. Resources simulation are stored on this same class to reduce the
	 * number of classes modified.
	 * 
	 * @param requestUrl
	 * @param params
	 * @return
	 * @throws ApiException
	 */
	protected InputStream getInputStream(final URL requestUrl, final Map<String, String> params) throws ApiException {
		OutputStreamWriter wr = null;
		try {
			// Check if this resource is stored on the offline testing service.
			if (AppWideConstants.DEVELOPMENT) {
				// Try to locate the resource on the recorded list. If not go to network.
				String pattern = requestUrl.toExternalForm();
				StringBuilder patternparams = new StringBuilder();
				for (Entry<String, String> entry : params.entrySet()) {
					if (patternparams.length() > 0) {
						patternparams.append("&"); // to ensure that we don't append an '&' to the end.
					}
					String key = entry.getKey();
					String value = entry.getValue();
					patternparams.append(URLEncoder.encode(key, "UTF8"));
					patternparams.append("=");
					patternparams.append(URLEncoder.encode(value, "UTF8"));
				}
				pattern = pattern + "?" + patternparams.toString();
				String recordFileName = recordedXMLResponses.get(pattern);
				if (null != recordFileName) {
					// ANDROID This line cannot be changed because it is the way we get an asset on Android.
					BufferedInputStream is = new BufferedInputStream(EVEDroidApp.getSingletonApp().getApplicationContext()
							.getAssets().open(recordFileName));
					LOG.info("-- Using test file downloader.");
					return is;
				} else {
					Log.i("ApiConnector", "-- CCP API request: " + pattern);
				}
			}
			HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setDoOutput(true);
			wr = new OutputStreamWriter(conn.getOutputStream());
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
			if (conn.getResponseCode() == 200)
				return conn.getInputStream();
			else
				return conn.getErrorStream();
		} catch (Exception e) {
			throw new ApiException(e);
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					LOG.warn("Error closing the stream", e);
				}
			}
		}
	}

	protected Map<String, String> getParams(final ApiRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("version", Integer.toString(request.getVersion()));
		ApiAuth<?> auth = request.getAuth();
		if (auth != null) {
			result.putAll(auth.getParams());
		}
		Map<String, String> params = request.getParams();
		if (params != null) {
			result.putAll(params);
		}
		return result;
	}

	protected URL getURL(final ApiRequest request) throws ApiException {
		try {
			StringBuilder result = new StringBuilder(getBaseUrl());
			result.append(request.getPath().getPath());
			result.append("/").append(request.getPage().getPage());
			result.append(".xml.aspx");
			return new URL(result.toString());
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}

	protected URLConnection openConnection(final URL requestUrl) throws ApiException {
		try {
			return requestUrl.openConnection();
		} catch (Exception e) {
			throw new ApiException(e);
		}
	}
}