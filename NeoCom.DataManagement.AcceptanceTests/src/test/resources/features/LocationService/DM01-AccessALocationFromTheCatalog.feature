@DM01
Feature: [DM01] Location catalog Service

  Control the access to the multilevel Location catalog cache. There is the memory cache level, the persistence cache
  level and then the SDE data repository and finally the internet citadel provider services.
  Each Location properly accessed is then stored on the persistence cache forever.

  @DM01.01
  Scenario: [DM01.01] During the initialization phase check that the SDE repository is available and verify the number and type
  of the locations stored on it.
	Given a new empty Location Catalog store and repository
	When the Location Catalog is checked
	Then the number of records on the LocationCache table is
	  | counterClass | count |
	  | TOTAL        | 0     |

  @DM01.02
  Scenario: [DM01.02] Access a Location in the range 10 to get a Region
	Given a new empty Location Catalog store and repository
	When requested to locate Location "10000031"
	Then the access result is "GENERATED"
	And the generated Location class is "REGION"
	And the location found has the next values
	  | regionId | regionName | classType |
	  | 10000031 | Impass     | REGION    |

  @DM01.03
  Scenario: [DM01.03] Access a Location in the range 20 to get a Constellation
	Given a new empty Location Catalog store and repository
	When requested to locate Location "20000008"
	Then the access result is "GENERATED"
	And the generated Location class is "CONSTELLATION"
	And the location found has the next values
	  | regionId | regionName | constellationId | constellationName | classType |
	  | 10000001 | Derelik    | 20000008        | Mossas            | CONSTELLATION    |

  @DM01.04
  Scenario: [DM01.04] Access a Location in the range 30 to get a System
	Given a new empty Location Catalog store and repository
	When requested to locate Location "30000071"
	Then the access result is "GENERATED"
	And the generated Location class is "SYSTEM"

  @DM01.05
  Scenario: [DM01.02] Access a Location in the range 10 to get a Region
	Given a new empty Location Catalog store and repository
	When requested to locate Location "10000031"
	Then the access result is "GENERATED"
	And the generated Location class is "REGION"

  @DM01.06
  Scenario: [DM01.03] After accessing a location that should be stored on the repository stop the service and persist cached locations
	Given a new empty Location Catalog
	When requested to locate Location "10000031"
	And after getting a "Region" location
	And verify that the obtained location is persisted on the repository
	Then stop the Location catalog service
	And start the Location catalog service
	And verify that the obtained location is persisted on the repository