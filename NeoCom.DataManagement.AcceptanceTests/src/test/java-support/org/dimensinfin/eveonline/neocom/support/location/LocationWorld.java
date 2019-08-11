package org.dimensinfin.eveonline.neocom.support.location;

import org.dimensinfin.eveonline.neocom.adapters.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.support.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.adapters.SupportLocationRepository;

public class LocationWorld {
	private ISDEDatabaseAdapter sdeDatabaseManager;
	private LocationCatalogService locationCatalogService;
	private SupportLocationRepository locationRepository;

	private EsiLocation location;

	public LocationWorld() {
		this.locationRepository = NeoComComponentFactory.getSingleton().getLocationRepository();
	}

	public ISDEDatabaseAdapter getSdeDatabaseManager() {
		return this.sdeDatabaseManager;
	}

	public LocationWorld setSdeDatabaseManager( final ISDEDatabaseAdapter sdeDatabaseManager ) {
		this.sdeDatabaseManager = sdeDatabaseManager;
		return this;
	}

	public LocationCatalogService getLocationCatalogService() {
		return this.locationCatalogService;
	}

	public LocationWorld setLocationCatalogService( final LocationCatalogService locationCatalogService ) {
		this.locationCatalogService = locationCatalogService;
		return this;
	}

	public SupportLocationRepository getLocationRepository() {
		return this.locationRepository;
	}

	public LocationWorld setLocationRepository( final SupportLocationRepository locationRepository ) {
		this.locationRepository = locationRepository;
		return this;
	}

	public EsiLocation getLocation() {
		return this.location;
	}

	public LocationWorld setLocation( final EsiLocation location ) {
		this.location = location;
		return this;
	}
}