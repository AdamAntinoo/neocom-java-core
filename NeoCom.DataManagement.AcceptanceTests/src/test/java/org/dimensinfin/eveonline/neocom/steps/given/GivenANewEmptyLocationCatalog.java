package org.dimensinfin.eveonline.neocom.steps.given;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.support.adapters.NeoComComponentFactory;
import org.dimensinfin.eveonline.neocom.support.location.LocationWorld;
import org.junit.Assert;

import cucumber.api.java.en.Given;

public class GivenANewEmptyLocationCatalog {
	private LocationWorld locationWorld;

	public GivenANewEmptyLocationCatalog( final LocationWorld locationWorld ) {
		this.locationWorld = locationWorld;
	}

	@Given("a new empty Location Catalog")
	public void aNewEmptyLocationCatalog() {
		final ISDEDatabaseAdapter sdeAdapter = NeoComComponentFactory.getSingleton().getSDEDatabaseAdapter();
		Assert.assertNotNull(sdeAdapter);
		this.locationWorld.setSdeDatabaseManager(sdeAdapter);
		this.locationWorld.getLocationRepository().deleteAll();
	}


	//	@Then("the memory cache is accessed with {string} result")
	public void theMemoryCacheIsAccessedWithResult( String arg0 ) {
	}

	//	@And("the persistence repository is accessed with {string} result")
	public void thePersistenceRepositoryIsAccessedWithResult( String arg0 ) {
	}

	//	@And("the calculated Location class is {string}")
	public void theCalculatedLocationClassIs( String arg0 ) {

	}

	//	@And("the SDE database is accessed with the next result")
	public void theSDEDatabaseIsAccessedWithTheNextResult() {

	}

}