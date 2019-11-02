package org.dimensinfin.eveonline.neocom.steps.multisteps;

import org.dimensinfin.eveonline.neocom.adapter.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.support.location.CucumberTableToEsiLocationConverter;
import org.dimensinfin.eveonline.neocom.support.location.LocationWorld;
import org.dimensinfin.eveonline.neocom.test.support.NeoComComponentFactory;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DM01InitializeLocationCatalog {
	private static final String COUNTER_CLASS = "counterClass";
	private static final String COUNTER = "count";

	private LocationWorld locationWorld;
	private CucumberTableToEsiLocationConverter cucumberTableToEsiLocationConverter;
	private ESIDataAdapter esiDataAdapter;
	private Map<String, Integer> counters;
	private EsiLocation secondLocation;

	public DM01InitializeLocationCatalog( final LocationWorld locationWorld,
	                                      final CucumberTableToEsiLocationConverter cucumberTableToEsiLocationConverter ) {
		this.locationWorld = locationWorld;
		this.cucumberTableToEsiLocationConverter = cucumberTableToEsiLocationConverter;
		this.esiDataAdapter = NeoComComponentFactory.getSingleton().getEsiDataAdapter();
	}

	@Given("a new empty Location Catalog store and repository")
	public void aNewEmptyLocationCatalogStoreAndRepository() {
		final ISDEDatabaseAdapter sdeAdapter = NeoComComponentFactory.getSingleton().getSDEDatabaseAdapter();
		Assert.assertNotNull(sdeAdapter);
		this.locationWorld.setSdeDatabaseManager(sdeAdapter);
		this.locationWorld.getLocationRepository().deleteAll(); // Clear the records on the sde repository
		this.locationWorld.setLocationCatalogService(NeoComComponentFactory.getSingleton().getLocationCatalogService());
		this.locationWorld.getLocationCatalogService().cleanLocationsCache();
	}

	@Given("a persisted repository")
	public void aPersistedRepository() {
		final ISDEDatabaseAdapter sdeAdapter = NeoComComponentFactory.getSingleton().getSDEDatabaseAdapter();
		Assert.assertNotNull(sdeAdapter);
		this.locationWorld.setSdeDatabaseManager(sdeAdapter);
		this.locationWorld.getLocationRepository().deleteAll(); // Clear the records on the sde repository
		this.locationWorld.setLocationCatalogService(NeoComComponentFactory.getSingleton().getLocationCatalogService());
	}

	@When("the Location Catalog is checked")
	public void theLocationCatalogIsChecked() {
		this.counters = this.locationWorld.getLocationCatalogService().getLocationTypeCounters();
	}

	@Then("the number of records on the LocationCache table is")
	public void theNumberOfRecordsOnTheLocationCacheTableIs( final List<Map<String, String>> cucumberTable ) {
		for (Map<String, String> row : cucumberTable) {
			final Integer obtained = this.counters.get(row.get(COUNTER_CLASS));
			Assert.assertEquals(Integer.parseInt(row.get(COUNTER)), obtained.intValue());
		}
	}

	@When("requested to locate Location {string}")
	public void requestedToLocateLocation( final String locationId ) {
		final EsiLocation location = this.esiDataAdapter.searchLocation4Id(Long.parseLong(locationId));
		Assert.assertNotNull(location);
		this.locationWorld.setLocation(location);
	}

	@Then("the access result is {string}")
	public void theAccessResultIs( final String accessType ) {
		final LocationCatalogService.LocationCacheAccessType obtained = this.esiDataAdapter
				                                                                .lastSearchLocationAccessType();
		Assert.assertEquals(accessType, obtained.name());
	}

	@And("the location memory cache dirty state is {string}")
	public void theLocationMemoryCacheDirtyStateIs( final String cacheState ) {
		final boolean status = this.locationWorld.getLocationCatalogService().getMemoryStatus();
		Assert.assertTrue(Boolean.toString(status).equalsIgnoreCase(cacheState));
	}

	@And("the generated Location class is {string}")
	public void theGeneratedLocationClassIs( final String classType ) {
		Assert.assertEquals(classType, this.locationWorld.getLocation().getClassType().name());
	}

	@And("the location found has the next values")
	public void theLocationFoundHasTheNextValues( final List<Map<String, String>> cucumberTable ) {
		final EsiLocation expected = this.cucumberTableToEsiLocationConverter.convert(cucumberTable.get(0));
		Assert.assertTrue(expected.equals(this.locationWorld.getLocation()));
	}

	@When("the location is requested again")
	public void theLocationIsRequestedAgain() {
		this.secondLocation = this.esiDataAdapter.searchLocation4Id(
				this.locationWorld.getLocation().getId());
	}

	@Then("the locations match")
	public void theLocationsMatch() {
		Assert.assertTrue(this.secondLocation.equals(this.locationWorld.getLocation()));
	}

	@When("we request to persist the memory cache")
	public void weRequestToPersistTheMemoryCache() {
		this.locationWorld.getLocationCatalogService().writeLocationsDataCache();
	}

	@And("we clear the memory cache")
	public void weClearTheMemoryCache() {
		this.locationWorld.getLocationCatalogService().cleanLocationsCache();
	}
}