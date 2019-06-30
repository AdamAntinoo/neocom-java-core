package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.core.updaters.CredentialUpdater;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;

public class NeoComUpdaterFactory {
	public static NeoComUpdater buildUpdater( final ICollaboration model ) {
		if ( model instanceof Credential)
			return new CredentialUpdater((Credential) model);
		return null;
	}
}
