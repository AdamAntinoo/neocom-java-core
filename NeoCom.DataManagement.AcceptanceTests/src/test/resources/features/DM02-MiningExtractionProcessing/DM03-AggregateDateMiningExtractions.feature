@DM03
Feature: [DM03] Read the extraction records and aggregate the results to be rendered. Fetch the list of extractions for the date
  of today and aggregate the resources mined on that period into a class that will control all these resources.

  @DM03.01
  Scenario: [DM03][01] Aggregate the records from all the extractions of the date of today
	Given the next records on the MiningRepository
	  | id                                    | typeId | solarSystemId | quantity | delta | extractionDateName | extractionHour | ownerId  |
	  | 2019-08-07:10-30001735-17459-92223647 | 17459  | 30001735      | 5894     | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:10-30001735-17471-92223647 | 17471  | 30001735      | 19276    | 0     | 2019-08-07         | 10             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17459  | 30001735      | 23576    | 17682 | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17459-92223647 | 17464  | 30001735      | 30348    | 0     | 2019-08-07         | 11             | 92223647 |
	  | 2019-08-07:11-30001735-17471-92223647 | 17471  | 30001735      | 25432    | 6156  | 2019-08-07         | 11             | 92223647 |
	And the today date being "2019-08-07"
	And the pilot is "92223647"
	When the records for today are read
	Then the aggregation results contain the next data
	  | typeId | quantity | price | value  |
	  | 17459  | 23576    | 10    | 235760 |
	  | 17464  | 30348    | 10    | 303480 |
	  | 17471  | 25432    | 10    | 254320 |