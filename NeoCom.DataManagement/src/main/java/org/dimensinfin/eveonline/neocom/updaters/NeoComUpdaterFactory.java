package org.dimensinfin.eveonline.neocom.adapters;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.core.updaters.ColonyPackUpdater;
import org.dimensinfin.eveonline.neocom.core.updaters.CredentialUpdater;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.core.updaters.PilotUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.domain.Pilot;
import org.dimensinfin.eveonline.neocom.planetary.ColonyPack;

public class NeoComUpdaterFactory {
	public static NeoComUpdater buildUpdater( final ICollaboration model ) {
		if ( model instanceof Credential)
			return new CredentialUpdater((Credential) model);
		if ( model instanceof Pilot)
			return new PilotUpdater((Pilot) model);
		if ( model instanceof ColonyPack)
			return new ColonyPackUpdater((ColonyPack) model);
		return null;
	}
}
