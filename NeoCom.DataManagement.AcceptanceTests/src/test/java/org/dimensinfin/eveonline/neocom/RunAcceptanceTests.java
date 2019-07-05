package org.dimensinfin.eveonline.neocom;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/features"},
		tags = {"not @skip_scenario", "not @front", "not @duplication"}
)
public class RunAcceptanceTests { }
