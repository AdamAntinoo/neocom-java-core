package org.dimensinfin.eveonline.neocom.utility;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;

public class FacetedLocationContainerTest {
	@Test
	public void buildComplete() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedLocationContainer<TestData> container = new FacetedLocationContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedLocationContainer<TestData> container = new FacetedLocationContainer.Builder<TestData>()
				.build();
		Assert.assertNotNull( container );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedLocationContainer<TestData> container = new FacetedLocationContainer.Builder<TestData>()
				.withFacet( null )
				.build();
		Assert.assertNotNull( container );
	}

	@Test
	public void getFacet() {
		final String expected = "-TEST-VALUE-";
		final TestData facet = new TestData( expected );
		final FacetedLocationContainer<TestData> container = new FacetedLocationContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
		Assert.assertNotNull( container.getFacet() );
		Assert.assertEquals( expected, container.getFacet().getValue() );
	}

	@Test
	public void addContent() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedLocationContainer<TestData> container = new FacetedLocationContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
		final SpaceLocation location = Mockito.mock( SpaceLocation.class );
		Assert.assertEquals( 1, container.addContent( location ) );
		Assert.assertEquals( 2, container.addContent( location ) );
	}

	@Test
	public void collaborate2Model() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedLocationContainer<TestData> container = new FacetedLocationContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
		final SpaceLocation location = Mockito.mock( SpaceLocation.class );
		Assert.assertEquals( 1, container.addContent( location ) );

		Assert.assertNotNull( container.collaborate2Model( "-TEST-VARIATION-" ) );
		Assert.assertEquals(1, container.collaborate2Model( "-TEST-VARIATION-" ).size() );
	}

	private static class TestData {
		private String value;

		public TestData( final String value ) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}