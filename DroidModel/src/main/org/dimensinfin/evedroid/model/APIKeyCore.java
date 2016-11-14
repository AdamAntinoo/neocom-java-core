//	PROJECT:        EveIndustrialistModel (EVEI-M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		JRE 1.7.
//	DESCRIPTION:		Data model to use on EVE related applications. Neutral code to be used in all enwironments.

package org.dimensinfin.evedroid.model;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.enums.EAPIKeyTypes;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// - CLASS IMPLEMENTATION ...................................................................................
public class APIKeyCore extends AbstractGEFNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 1891959457883171631L;
	private static Logger			logger						= Logger.getLogger("APIKey");

	// - F I E L D - S E C T I O N ............................................................................
	private final Instant			lastCCPAccessTime	= new Instant(0);
	private boolean						expanded					= true;

	// - P R O P E R T I E S
	protected int							keyID							= -1;
	protected String					verificationCode	= null;
	protected EAPIKeyTypes		type							= EAPIKeyTypes.Character;
	private Instant						paidUntil					= new Instant(0);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public APIKeyCore(final int keyID, final String verificationCode) {
		this.keyID = keyID;
		this.verificationCode = verificationCode;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getKeyID() {
		return keyID;
	}

	public Instant getTimeLeft() {
		return paidUntil;
	}

	public String getType() {
		return type.toString();
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	/**
	 * If the account is expired then return it on the collapsed state. Otherwise return the default and user
	 * set state.
	 * 
	 * @return expand value.
	 */
	public boolean isExpanded() {
		// Check if expired.
		final Instant expires = getTimeLeft();
		Instant now = new Instant();
		if (expires.isBefore(now))
			return false;
		else
			return expanded;
	}

	public void setExpanded(final boolean b) {
		expanded = b;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("[APIKey (");
		buffer.append("keyID=").append(keyID).append(" ");
		buffer.append("verificationCode='").append(verificationCode).append("' ");
		buffer.append("]");
		return super.toString() + buffer.toString();
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
}

// - UNUSED CODE ............................................................................................
