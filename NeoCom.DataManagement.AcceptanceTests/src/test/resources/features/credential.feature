Feature: Credential Updatable Entity

  Scenario: Read all the credentials stored on the persistence repository
    Given the next list of Credentials
    When I run the repository call
    Then I will get a list of 2 Credentials