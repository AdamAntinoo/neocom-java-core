package org.dimensinfin.eveonline.neocom.auth;

import org.dimensinfin.eveonline.neocom.datamngmt.GlobalDataManager;

import com.github.scribejava.core.builder.api.DefaultApi20;

public class NeoComAuthApi20 extends DefaultApi20 {
	private static final NeoComAuthApi20 singleton = new NeoComAuthApi20();
	public static NeoComAuthApi20 getInstance(){
		return singleton;
	}

	@Override
	public String getAccessTokenEndpoint() {
		// Compose the endpoint from the configuration file.
		return GlobalDataManager.getResourceString("P.esi.authorization.authorizationserver"
				, "https://login.eveonline.com/") +
				       GlobalDataManager.getResourceString("P.esi.authorization.authapi.accesstokenresource"
						       , "oauth/token");
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return GlobalDataManager.getResourceString("P.esi.authorization.authorizationserver"
				, "https://login.eveonline.com/") +
				       GlobalDataManager.getResourceString("P.esi.authorization.authapi.authorizeurl"
						       , "oauth/authorize");
	}
}
