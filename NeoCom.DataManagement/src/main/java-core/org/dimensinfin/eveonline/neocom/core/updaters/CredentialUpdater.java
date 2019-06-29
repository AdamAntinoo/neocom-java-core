package org.dimensinfin.eveonline.neocom.core.updaters;

import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;

import org.joda.time.DateTime;

public class CredentialUpdater extends NeoComUpdater<Credential> {
	private static final long CREDENTIAL_CACHE_TIME = TimeUnit.MINUTES.toMillis(15);

	@Override
	public boolean needsRefresh() {
		if (this.getModel().getLastUpdateTime().plus(CREDENTIAL_CACHE_TIME).isBefore(DateTime.now()))
			return true;
		return false;
	}

	@Override
	public String getIdentifier() {
		return "CRD:" + this.getModel().getAccountId();
	}
}
