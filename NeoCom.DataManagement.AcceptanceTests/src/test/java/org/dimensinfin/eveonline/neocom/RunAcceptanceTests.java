package org.dimensinfin.eveonline.neocom;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/features"},
		plugin = {"pretty", "json:target/cucumber_report.json"},
		tags = {"not @skip_scenario", "not @front", "not @duplication"}
)
public class RunAcceptanceTests {
}
