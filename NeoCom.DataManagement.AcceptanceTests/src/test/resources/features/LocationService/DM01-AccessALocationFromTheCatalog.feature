@DM01
Feature: [DM01] Location catalog Service

  Control the access to the multilevel Location catalog cache. There is the memory cache level, the persistence cache
  level and then the SDE data repository and finally the internet citadel provider services.
  Each Location properly accessed is then stored on the persistence cache forever.

  @DM01.01
  Scenario: [DM01] During the initialization phase check that the SDE repository is available and verify the number and type
  of the locations stored on it.
	Given a new empty Location Catalog store and repository
	When the Location Catalog is checked
	Then the number of records on the LocationCache table is
	  | counterClass | count |
	  | TOTAL        | 0     |

  @DM01.02
  Scenario: [DM01] Access a Location in the range 10 to get a Region
	Given a new empty Location Catalog store and repository
	When requested to locate Location "10000031"
	Then the access result is "GENERATED"
	And the generated Location class is "REGION"

#	And the SDE database is accessed with the next result
#	  | regionID | regionName | factionID |
#	  | 10000031 | Impass     |           |

  @DM01.03
  Scenario: [DM01] After accessing a location hat should be stored on the respository stop the service and persist cached locations
	Given a new empty Location Catalog
	When requested to locate Location "10000031"
	And after getting a "Region" location
	And verify that the obtained location is persisted on the repository
	Then stop the Location catalog service
	And start the Location catalog service
	And verify that the obtained location is persisted on the repository