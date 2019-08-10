@DM02 @MiningExtraction
Feature: [DM02] Download and process pilot mining extractions.

  Fetch a new update for the pilot mining extractions each 10 minutes and process the received data against the
  Mining Extraction Repository where the records are stored. Mining extractions are updated on each download and
  new records are generated every hour with the latest amount of resources. If the downloaded data is from previous
  dates the recors are marked with a special hour indicator.

  @DM02.01
  Scenario: [DM02][01] Download a set of extractions by first time and store the processed data on the repository. Records downloaded match with the processing date.
	Given an empty Mining Extraction repository
	And the next set of mining extractions for pilot "92223647"
	  | date       | quantity | solar_system_id | type_id |
	  | 2019-08-07 | 1566     | 30001735        | 17471   |
	  | 2019-08-07 | 421      | 30001735        | 17459   |
	When the mining data is processed on date "2019-08-07" and hour "10"
	Then the next records are set on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 1566     | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 421      | 0     | 2019-08-07         | 10             | 92223647 |

  @DM02.02
  Scenario: [DM02][02] Download another set of extractions on the same hour and store the processed data on the repository. Update records because download date and processing dates match.
	Given the next records on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 421      | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 1566     | 0     | 2019-08-07         | 10             | 92223647 |
	And the next set of mining extractions for pilot "92223647"
	  | date       | quantity | solar_system_id | type_id |
	  | 2019-08-07 | 19276    | 30001735        | 17471   |
	  | 2019-08-07 | 5894     | 30001735        | 17459   |
	When the mining data is processed on date "2019-08-07" and hour "10"
	Then the next records are set on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |

  @DM02.03
  Scenario: [DM02][03] Download another set of extractions on another hour and store the processed data on the repository
	Given the next records on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	And the next set of mining extractions for pilot "92223647"
	  | date       | quantity | solar_system_id | type_id |
	  | 2019-08-07 | 25432    | 30001735        | 17471   |
	  | 2019-08-07 | 23576    | 30001735        | 17459   |
	  | 2019-08-07 | 30348    | 30001735        | 17464   |
	When the mining data is processed on date "2019-08-07" and hour "11"
	Then the next records are set on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |

  @DM02.04
  Scenario: [DM02][04] Download another set of extractions covering more that one date
	Given the next records on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 11             | 92223647 |
	And the next set of mining extractions for pilot "92223647"
	  | date       | quantity | solar_system_id | type_id |
	  | 2019-08-07 | 25432    | 30001735        | 17471   |
	  | 2019-08-07 | 23576    | 30001735        | 17459   |
	  | 2019-08-07 | 30348    | 30001735        | 17464   |
	  | 2019-08-08 | 14511    | 30001735        | 17459   |
	When the mining data is processed on date "2019-08-08" and hour "12"
	Then the next records are set on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-08:12-30001735-17459-92223647 | 17459  | 30001735      | 14511    | 0     | 2019-08-08         | 12             | 92223647 |
	  | 2019-08-07:24-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:24-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:24-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |

  @DM02.05
  Scenario: [DM02][05] Download a set of extractions but from older dates that today
	Given the next records on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:24-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:24-30001735-17459-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:24-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-08:12-30001735-17459-92223647 | 17459  | 30001735      | 14511    | 0     | 2019-08-08         | 12             | 92223647 |
	And the next set of mining extractions for pilot "92223647"
	  | date       | quantity | solar_system_id | type_id |
	  | 2019-08-07 | 25432    | 30001735        | 17471   |
	  | 2019-08-07 | 23576    | 30001735        | 17459   |
	  | 2019-08-07 | 30348    | 30001735        | 17464   |
	  | 2019-08-08 | 14511    | 30001735        | 17459   |
	When the mining data is processed on date "2019-08-09" and hour "13"
	Then the next records are set on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-08:24-30001735-17459-92223647 | 17459  | 30001735      | 14511    | 0     | 2019-08-08         | 24             | 92223647 |
	  | 2019-08-08:12-30001735-17459-92223647 | 17459  | 30001735      | 14511    | 0     | 2019-08-08         | 12             | 92223647 |
	  | 2019-08-07:24-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:24-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:24-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 0     | 2019-08-07         | 24             | 92223647 |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |
