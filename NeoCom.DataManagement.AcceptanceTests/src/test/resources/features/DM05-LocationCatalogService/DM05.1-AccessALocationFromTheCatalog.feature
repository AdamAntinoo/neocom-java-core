@DM05
Feature: [DM05] Location catalog Service

  Control the access to the multilevel Location catalog cache. There is the memory cache level, the persistence cache
  level and then the SDE data repository and finally the internet citadel provider services.
  Each Location properly accessed is then stored on the persistence cache forever.

  @DM05.01
  Scenario: [DM05][01] Access a Location in the range 10 to get a Region
	Given a new empty Location Catalog
	When requested to locate Location "10000031"
	Then the memory cache is accessed with "MISS" result
	And the persistence repository is accessed with "MISS" result
	And the calculated Location class is "Region"
	And the SDE database is accessed with the next result
	  | regionID | regionName | factionID |
	  | 10000031 | Impass     |           |
