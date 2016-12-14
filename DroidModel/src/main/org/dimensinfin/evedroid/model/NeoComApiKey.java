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
import org.dimensinfin.evedroid.core.NeoComConnector;
import org.dimensinfin.evedroid.interfaces.INeoComNode;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.connectors.CachingConnector;
import com.beimin.eveapi.connectors.LoggingConnector;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.AccountStatus;
import com.beimin.eveapi.model.account.ApiKeyInfo;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.KeyType;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.AccountStatusParser;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.response.account.AccountStatusResponse;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComApiKey extends AbstractComplexNode implements INeoComNode {
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
		NeoComApiKey.logger.info("-- [NeoComApiKey.build]> Creating key: " + keynumber + "/" + validationcode);
		// FIXME Change the eveapi connector to a logger cached so I can trace the SSL problem.
		EveApi.setConnector(new NeoComConnector(new CachingConnector(new LoggingConnector())));
		// Remove the secure XML access and configure the ApiConnector.
		ApiConnector.setSecureXmlProcessing(false);
		ApiAuthorization authorization = new ApiAuthorization(keynumber, validationcode);
		ApiKeyInfoParser infoparser = new ApiKeyInfoParser();
		ApiKeyInfoResponse inforesponse = infoparser.getResponse(authorization);
		NeoComApiKey apiKey = null;
		if (null != inforesponse) {
			apiKey = new NeoComApiKey();
			apiKey.setAuthorization(authorization);
			apiKey.setDelegate(inforesponse);
			apiKey.setKey(keynumber);
			apiKey.setValidationCode(validationcode);
			apiKey.setCachedUntil(inforesponse.getCachedUntil());
			// TODO get the error and the version to complete the api and check the Error for any kind of problem.
			// TODO move the download of the characters to a new location and force its caching.
			apiKey.getApiCharacters();
		} else
			throw new ApiException("No response from CCP for key (" + keynumber + "/" + validationcode + ")");
		AccountStatusParser statusparser = new AccountStatusParser();
		AccountStatusResponse statusresponse = statusparser.getResponse(authorization);
		if (null != statusresponse)
			apiKey.setDelegateStatus(statusresponse);
		else
			throw new ApiException("No response from CCP for key (" + keynumber + "/" + validationcode + ")");
		return apiKey;
	}

	// - F I E L D - S E C T I O N ............................................................................
	private ApiAuthorization						authorization					= null;
	private transient ApiKeyInfo				delegatedApiKey				= null;
	private transient AccountStatus			delegateStatus				= null;
	private ArrayList<NeoComCharacter>	neocomCharactersCache	= null;
	private int													key										= -1;
	private String											validationCode				= "<INVALID>";
	private Date												cachedUntil						= GregorianCalendar.getInstance().getTime();
	private Instant											paidUntil							= new Instant(0);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	private NeoComApiKey() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The model elements exported by this class are the characters or corporations defined for it. We should
	 * check the different yypes but the elements exported will all share a common interface.
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> result = new ArrayList<AbstractComplexNode>();
		try {
			for (NeoComCharacter node : this.getApiCharacters())
				result.add(node);
		} catch (ApiException apiex) {
			apiex.printStackTrace();
		}
		return result;
	}

	public long getAccessMask() {
		return delegatedApiKey.getAccessMask();
	}

	/**
	 * Get the list of characters. Force the caching of this information to avoid getting them from the main
	 * thread and be sure we can get on initialization.
	 * 
	 * @return
	 * @throws ApiException
	 */
	public ArrayList<NeoComCharacter> getApiCharacters() throws ApiException {
		if (null == neocomCharactersCache) {
			neocomCharactersCache = new ArrayList<NeoComCharacter>();
			Collection<Character> coreList = delegatedApiKey.getEveCharacters();
			for (Character character : coreList) {
				NeoComCharacter neochar = NeoComCharacter.build(character, this);
				neocomCharactersCache.add(neochar);
			}
		}
		return neocomCharactersCache;
	}

	public ApiAuthorization getAuthorization() {
		return authorization;
	}

	public Date getCachedUntil() {
		return cachedUntil;
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

	public void setCachedUntil(final Date cachedUntil) {
		this.cachedUntil = cachedUntil;
	}

	public void setDelegate(final ApiKeyInfoResponse response) {
		delegatedApiKey = response.getApiKeyInfo();
	}

	public void setKey(final int key) {
		this.key = key;
	}

	public void setValidationCode(final String validationcode) {
		validationCode = validationcode;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("NeoComApiKey [");
		buffer.append("keyID=").append(this.getKey()).append(" ");
		buffer.append("verificationCode='").append(this.getValidationCode()).append("' ");
		buffer.append("type='").append(this.getType());
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	protected void setPaidUntil(final String text) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd' 'HH:mm:ss");
		try {
			String source = text.replace(" ", "'T'") + ".00000";
			DateTime dt = fmt.parseDateTime(text);
			paidUntil = new Instant(dt);
		} catch (Exception ex) {
			paidUntil = new Instant();
		}
	}

	private void setAuthorization(final ApiAuthorization auth) {
		authorization = auth;
	}

	private void setDelegateStatus(final AccountStatusResponse statusresponse) {
		delegateStatus = statusresponse.get();
	}
}

// - UNUSED CODE ............................................................................................
