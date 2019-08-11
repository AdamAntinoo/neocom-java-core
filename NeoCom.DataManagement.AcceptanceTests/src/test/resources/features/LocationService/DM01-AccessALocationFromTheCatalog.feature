@DM01 @LocationService
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
	And the location memory cache dirty state is "true"
	And the generated Location class is "REGION"
	And the location found has the next values
	  | regionId | regionName | classType |
	  | 10000031 | Impass     | REGION    |

  @DM01.03
  Scenario: [DM01.03] Access a Location in the range 20 to get a Constellation
	Given a new empty Location Catalog store and repository
	When requested to locate Location "20000008"
	Then the access result is "GENERATED"
	And the location memory cache dirty state is "true"
	And the generated Location class is "CONSTELLATION"
	And the location found has the next values
	  | regionId | regionName | constellationId | constellationName | classType     |
	  | 10000001 | Derelik    | 20000008        | Mossas            | CONSTELLATION |

  @DM01.04
  Scenario: [DM01.04] Access a Location in the range 30 to get a System
	Given a new empty Location Catalog store and repository
	When requested to locate Location "30001735"
	Then the access result is "GENERATED"
	And the location memory cache dirty state is "true"
	And the generated Location class is "SYSTEM"
	And the location found has the next values
	  | regionId | regionName  | constellationId | constellationName | systemId | systemName | classType |
	  | 10000020 | Tash-Murkon | 20000257        | Peges             | 30001735 | Uhodoh     | SYSTEM    |

  @DM01.05
  Scenario: [DM01.05] Check that a second access to the same location getS a MEMORY access
	Given a new empty Location Catalog store and repository
	When requested to locate Location "30001735"
	Then the access result is "GENERATED"
	And the location memory cache dirty state is "true"
	And the generated Location class is "SYSTEM"
	When the location is requested again
	Then the locations match
	And the access result is "MEMORY_ACCESS"
	And the location memory cache dirty state is "true"

  @DM01.06
  Scenario: [DM01.06] After some requests persist the memory cache to the storage and check that next requests do not report GENERATED
	Given a new empty Location Catalog store and repository
	When requested to locate Location "10000031"
	When requested to locate Location "20000008"
	When requested to locate Location "30001735"
	Then the access result is "GENERATED"
	And the location memory cache dirty state is "true"
	And the generated Location class is "SYSTEM"
	When we request to persist the memory cache
	And the location memory cache dirty state is "false"
	And requested to locate Location "20000008"
	Then the access result is "MEMORY_ACCESS"

  @DM01.07
  Scenario: [DM01.07] Using a repository persisted should return MEMORY_ACCESS on first requests
	Given a persisted repository
	When requested to locate Location "10000031"
	Then the access result is "MEMORY_ACCESS"
	And the location memory cache dirty state is "false"
	And the generated Location class is "REGION"
