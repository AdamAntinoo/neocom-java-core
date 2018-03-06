//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.auth;

import com.github.scribejava.core.builder.api.DefaultApi20;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComAuthApi20 extends DefaultApi20{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final String AUTHORIZE_URL = "https://login.eveonline.com/oauth/authorize";
	private static final String ACCESS_TOKEN_RESOURCE = "https://login.eveonline.com/oauth/token";
//	public static final String PECK =
//			"ZWY2ODI5OGQ1ODJjNGRmZWJiNjc4ODZlMzBkMDg4YTg6emdQZnhPNjNRUFIyZUgxMXJOcGdQQUwwSjJtQjZmWko3SFFDcmkwcQ==";
//	public static final String CONTENT_TYPE = "application/json";
	private static class InstanceHolder {
		private static final NeoComAuthApi20 INSTANCE = new NeoComAuthApi20();
	}
	public static NeoComAuthApi20 instance () {
		return NeoComAuthApi20.InstanceHolder.INSTANCE;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	protected NeoComAuthApi20 () {
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	@Override
	public String getAccessTokenEndpoint () {
		return ACCESS_TOKEN_RESOURCE;
	}

	@Override
	protected String getAuthorizationBaseUrl () {
		return AUTHORIZE_URL;
	}
}
// - UNUSED CODE ............................................................................................
//[01]
