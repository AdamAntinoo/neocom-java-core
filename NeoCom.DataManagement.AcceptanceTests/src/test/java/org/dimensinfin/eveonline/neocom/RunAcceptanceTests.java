package org.dimensinfin.eveonline.neocom;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/features"},
		glue = {"org.dimensinfin.eveonline.neocom.steps"},
		plugin = {"pretty", "json:target/cucumber_report.json"},
		tags = {"not @skip_scenario", "not @front", "not @duplication","@DM02"})
public class RunAcceptanceTests {
}
