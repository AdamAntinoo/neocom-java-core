package org.dimensinfin.eveonline.neocom.steps;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.mining.DailyExtractionResourcesContainer;
import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.support.miningExtractions.MiningExtractionsWorld;
import org.junit.Assert;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cucumber.api.java.en.Then;

public class ThenTheAggregationResultsContainTheNextData {
	private static final String TYPE_ID = "typeId";
	private static final String QUANTITY = "quantity";
	private MiningExtractionsWorld miningExtractionsWorld;

	@Autowired
	public ThenTheAggregationResultsContainTheNextData( final MiningExtractionsWorld miningExtractionsWorld ) {
		this.miningExtractionsWorld = miningExtractionsWorld;
	}

	@Then("the aggregation results contain the next data")
	public void theAggregationResultsContainTheNextData( final List<Map<String, String>> cucumberTable ) {
		final DailyExtractionResourcesContainer resourcesContainer = this.miningExtractionsWorld.getResourcesContainer();
		List<ICollaboration> resources = resourcesContainer.collaborate2Model("ACCEPTANCE");
		final EveItem item = Mockito.mock(EveItem.class);
		Mockito.when(item.getPrice()).thenReturn(10.0);
		Collections.sort(resources, new Comparator<ICollaboration>() {
			@Override
			public int compare( final ICollaboration c1, final ICollaboration c2 ) {
				final Resource r1 = (Resource) c1;
				final Resource r2 = (Resource) c2;
				return r1.getTypeId() - r2.getTypeId();
			}
		});
		int i = 0;
		for (Map<String, String> row : cucumberTable) {
			int expected = Integer.parseInt(row.get(TYPE_ID));
			int obtained = ((Resource) resources.get(i)).getTypeId();
			Assert.assertEquals(expected, obtained);
			expected = Integer.parseInt(row.get(QUANTITY));
			obtained = ((Resource) resources.get(i)).getQuantity();
			Assert.assertEquals(expected, obtained);
			i++;
		}
	}
}
