package org.dimensinfin.eveonline.neocom;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"src/test/resources/features"},
		tags = {"not @skip_scenario", "not @front", "not @duplication"}
)
public class RunAcceptanceTests {
	// Prepare components that need injection.
//	CredentialUpdater. .  .injectsEsiDataAdapter( NeoComComponentFactory.getSi )

//	new CredentialUpdater()
}
