package org.dimensinfin.eveonline.neocom.domain.container;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.NeoComNode;

class FacetedExpandableContainerTest {
	@Test
	void buildComplete() {
		final TestFacet facet = new TestFacet();
		final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<TestFacet, NeoAsset>()
				.withFacet( facet )
				.build();
		Assertions.assertNotNull( container );
	}
	@Test
	void buildFailure() {
		final TestFacet facet = new TestFacet();
		final FacetedExpandableContainer container = new FacetedExpandableContainer.Builder<TestFacet, NeoAsset>()
				.withFacet( null )
				.build();
		Assertions.assertNotNull( container );
	}

	@Test
	void getFacet() {
	}
}
final  class TestFacet extends NeoComNode {

}
