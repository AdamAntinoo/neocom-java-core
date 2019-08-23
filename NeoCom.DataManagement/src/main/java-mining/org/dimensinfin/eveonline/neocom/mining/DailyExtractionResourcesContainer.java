package org.dimensinfin.eveonline.neocom.mining;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

import java.util.ArrayList;
import java.util.List;

public class DailyExtractionResourcesContainer extends NeoComNode {
	private List<Resource> resources = new ArrayList<>();

	// - I C O L L A B O R A T I O N
	@Override
	public List<ICollaboration> collaborate2Model( final String variant ) {
		return new ArrayList<>(this.resources);
	}

	// - B U I L D E R
	public static class Builder {
		private DailyExtractionResourcesContainer onConstruction;

		public Builder() {
			this.onConstruction = new DailyExtractionResourcesContainer();
		}

		public Builder withResourceList( final List<Resource> resources ) {
			this.onConstruction.resources = resources;
			return this;
		}

		public DailyExtractionResourcesContainer build() {
			return this.onConstruction;
		}
	}
}
