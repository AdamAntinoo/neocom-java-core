//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.core.INeoComNode;

import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.ApiKeyInfo;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComApiKey implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 4162373120742984305L;
	private static Logger			logger						= Logger.getLogger("NeoComApiKey");

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
			apiKey.setKey(keynumber);
			apiKey.setValidationCode(validationcode);
			apiKey.setCachedUntil(response.getCachedUntil());
			// TODO get the error and the version to complete the api and check the Error for any kind of problem.
			return apiKey;
		} else
			throw new ApiException("No response from CCP for key (" + keynumber + "/" + validationcode + ")");
	}

	// - F I E L D - S E C T I O N ............................................................................
	private ApiKeyInfo	delegatedApiKey	= null;
	private int					key							= -1;
	private String			validationCode	= "<INVALID>";
	private Date				cachedUntil			= GregorianCalendar.getInstance().getTime();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private NeoComApiKey() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		return new ArrayList<AbstractComplexNode>();
	}

	public long getAccessMask() {
		return delegatedApiKey.getAccessMask();
	}

	public Date getCachedUntil() {
		return cachedUntil;
	}

	public Collection<Character> getEveCharacters() {
		return delegatedApiKey.getEveCharacters();
	}

	public Date getExpires() {
		return delegatedApiKey.getExpires();
	}

	public int getKey() {
		return key;
	}

	public KeyType getType() {
		return delegatedApiKey.getType();
	}

	public String getValidationCode() {
		return validationCode;
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

	public void setCachedUntil(Date cachedUntil) {
		this.cachedUntil = cachedUntil;
	}

	public void setDelegate(final ApiKeyInfoResponse response) {
		delegatedApiKey = response.getApiKeyInfo();
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setValidationCode(String validationcode) {
		this.validationCode = validationcode;
	}
}

// - UNUSED CODE ............................................................................................
