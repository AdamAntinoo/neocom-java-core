package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.android.mvc.controller.ControllerFactory;
import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.app.controller.INeoComControllerFactory;
import org.dimensinfin.eveonline.neocom.core.updaters.CredentialUpdater;
import org.dimensinfin.eveonline.neocom.core.updaters.NeoComUpdater;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;

public class TestUpdaterFactory extends ControllerFactory implements INeoComControllerFactory {
	public TestUpdaterFactory( final String selectedVariant ) {
		super(selectedVariant);
	}

	@Override
	public NeoComUpdater buildUpdater( final ICollaboration model ) {
		if ( model instanceof Credential)
			return new CredentialUpdater((Credential) model);
		return null;
	}
}
