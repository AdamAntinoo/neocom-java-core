package org.dimensinfin.eveonline.neocom.utility;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;

public class FacetedAssetContainerTest {
	@Test
	public void buildComplete() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedAssetContainer<TestData> container = new FacetedAssetContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedAssetContainer<TestData> container = new FacetedAssetContainer.Builder<TestData>()
				.build();
		Assert.assertNotNull( container );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureB() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedAssetContainer<TestData> container = new FacetedAssetContainer.Builder<TestData>()
				.withFacet( null )
				.build();
		Assert.assertNotNull( container );
	}

	@Test
	public void getFacet() {
		final String expected = "-TEST-VALUE-";
		final TestData facet = new TestData( expected );
		final FacetedAssetContainer<TestData> container = new FacetedAssetContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
//		Assert.assertNotNull( container.getFacet() );
//		Assert.assertEquals( expected, container.getFacet().getValue() );
	}

	@Test
	public void addContent() {
		final TestData facet = Mockito.mock( TestData.class );
		final FacetedAssetContainer<TestData> container = new FacetedAssetContainer.Builder<TestData>()
				.withFacet( facet )
				.build();
		Assert.assertNotNull( container );
		final NeoAsset asset = Mockito.mock( NeoAsset.class );
		Assert.assertEquals( 1, container.addContent( asset ) );
		Assert.assertEquals( 2, container.addContent( asset ) );
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