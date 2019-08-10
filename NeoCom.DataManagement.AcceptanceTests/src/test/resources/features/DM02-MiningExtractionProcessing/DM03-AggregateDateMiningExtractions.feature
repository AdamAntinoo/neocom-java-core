@DM03
Feature: [DM03] Read the extraction records and aggregate the results to be rendered. Fetch the list of extractions for the date
  of today and aggregate the resources mined on that period into a class that will control all these resources and the rendering.

  @DM03.01
  Scenario: [DM03][01] Aggregate the records from all the extractions of the date of today to ge the list of extracted resources
	Given the next records on the MiningRepository
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
	And the today date being "2019-08-07"
	And the pilot is "92223647"
	When the resource aggregator is initialized
	Then the aggregation results contain the next data
	  | typeId | quantity | price | value  |
	  | 17459  | 23576    | 10.0  | 235760 |
	  | 17464  | 30348    | 10.0  | 303480 |
	  | 17471  | 25432    | 10.0  | 254320 |

  @DM03.02
  Scenario: [DM03][02] Read today's extractions to ge the extraction timetable for the date
	Given the next records on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:14-30001740-17464-92223647 | 17464  | 30001740      | 843      | 0     | 2019-08-07         | 14             | 92223647 |
	  | 2019-08-07:14-30001740-17460-92223647 | 17460  | 30001740      | 8266     | 0     | 2019-08-07         | 14             | 92223647 |
	  | 2019-08-07:14-30001740-17453-92223647 | 17453  | 30001740      | 3345     | 0     | 2019-08-07         | 14             | 92223647 |
	  | 2019-08-07:13-30001735-17459-92223647 | 17459  | 30001735      | 38087    | 14511 | 2019-08-07         | 13             | 92223647 |
	  | 2019-08-07:12-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 12             | 92223647 |
	  | 2019-08-07:12-30001735-17464-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 12             | 92223647 |
	  | 2019-08-07:12-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 12             | 92223647 |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 1566     | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 421      | 0     | 2019-08-07         | 10             | 92223647 |
	And the today date being "2019-08-07"
	And the pilot is "92223647"
	When the request to get today extraction records is fired
	Then there are the next system aggregators
	  | solarSystemId | solarSystemName |
	  | 30001735      | Uhodoh          |
	  | 30001740      | Arakor          |
	And system aggregator "30001740" having the next hourly records
	  | typeId | solarSystemId | quantity | hour |
	  | 17459  | 30001735      | 38087    | 13   |
	  | 17471  | 30001735      | 25432    | 12   |
	  | 17464  | 30001735      | 30348    | 12   |
	  | 17459  | 30001735      | 23576    | 12   |
	  | 17471  | 30001735      | 19276    | 11   |
	  | 17459  | 30001735      | 5894     | 11   |
	  | 17471  | 30001735      | 1566     | 10   |
	  | 17459  | 30001735      | 421      | 10   |
	And system aggregator "30001735" having the next hourly records
	  | typeId | solarSystemId | quantity | hour |
	  | 17464  | 30001740      | 843      | 14   |
	  | 17460  | 30001740      | 8266     | 14   |
	  | 17453  | 30001740      | 3345     | 14   |