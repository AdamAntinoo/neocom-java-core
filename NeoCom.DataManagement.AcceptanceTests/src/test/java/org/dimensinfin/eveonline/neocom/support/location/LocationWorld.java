package org.dimensinfin.eveonline.neocom.support.location;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;

public class LocationWorld {
	private ISDEDatabaseAdapter sdeDatabaseManager;

	public ISDEDatabaseAdapter getSdeDatabaseManager() {
		return this.sdeDatabaseManager;
	}

	public LocationWorld setSdeDatabaseManager( final ISDEDatabaseAdapter sdeDatabaseManager ) {
		this.sdeDatabaseManager = sdeDatabaseManager;
		return this;
	}
}