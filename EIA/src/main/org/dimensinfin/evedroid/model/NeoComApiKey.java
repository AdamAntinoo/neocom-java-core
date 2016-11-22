//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.core.AbstractNeoComNode;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.ApiKeyInfo;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComApiKey extends AbstractNeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 4162373120742984305L;
	private static Logger			logger						= Logger.getLogger("org.dimensinfin.evedroid.model");

	/**
	 * Constructor of the apikey. This static method will instantiate a key and be sure it can download the data
	 * from CCP using the eveapi library.
	 * 
	 * @param keynumber
	 * @param validationcode
	 * @return new instance of an eve apikey
	 * @throws ApiException
	 */
	public static NeoComApiKey build(final int keynumber, final String validationcode) throws ApiException {
		// Get the complete api information for CCP through the eveapi.
		logger.info("-- [NeoComApiKey.build]> Creating key: " + keynumber + "/" + validationcode);
		ApiAuthorization authorization = new ApiAuthorization(keynumber, validationcode);
		ApiKeyInfoParser parser = new ApiKeyInfoParser();
		ApiKeyInfoResponse response = parser.getResponse(authorization);
		if (null != response) {
			NeoComApiKey apiKey = new NeoComApiKey();
			apiKey.setDelegate(response);
			return apiKey;
		} else
			throw new ApiException("No response from CCP for key (" + keynumber + "/" + validationcode + ")");
	}

	// - F I E L D - S E C T I O N ............................................................................
	private ApiKeyInfo delegatedApiKey = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private NeoComApiKey() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
	}

	public long getAccessMask() {
		return delegatedApiKey.getAccessMask();
	}

	public Collection<Character> getEveCharacters() {
		return delegatedApiKey.getEveCharacters();
	}

	public Date getExpires() {
		return delegatedApiKey.getExpires();
	}

	public KeyType getType() {
		return delegatedApiKey.getType();
	}

	public boolean isAccountKey() {
		return delegatedApiKey.isAccountKey();
	}

	public boolean isCharacterKey() {
		return delegatedApiKey.isCharacterKey();
	}

	public boolean isCorporationKey() {
		return delegatedApiKey.isCorporationKey();
	}

	public void setDelegate(final ApiKeyInfoResponse response) {
		delegatedApiKey = response.getApiKeyInfo();
	}
}

// - UNUSED CODE ............................................................................................
