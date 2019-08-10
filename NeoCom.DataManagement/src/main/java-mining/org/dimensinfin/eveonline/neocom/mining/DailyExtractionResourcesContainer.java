package org.dimensinfin.eveonline.neocom.mining;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.repositories.MiningRepository;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DailyExtractionResourcesContainer extends NeoComNode {
	private Credential credential;
	private MiningRepository miningRepository;
	private LocalDate targetDate;
	private List<Resource> resources = new ArrayList<>();

	protected void fillResources() {
		if (null == this.targetDate) this.targetDate = LocalDate.now();
		final List<MiningExtraction> extractions = this.miningRepository.accessResources4Date(
				this.credential,
				this.targetDate);
		this.resources.clear();
		for (MiningExtraction extraction : extractions) {
			this.resources.add(new Resource(extraction.getTypeId(), extraction.getQuantity()));
		}
	}

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

		public Builder withCredential( final Credential credential ) {
			Objects.requireNonNull(credential);
			this.onConstruction.credential = credential;
			return this;
		}

		public Builder withMiningRepository( final MiningRepository miningRepository ) {
			Objects.requireNonNull(miningRepository);
			this.onConstruction.miningRepository = miningRepository;
			return this;
		}

		public Builder withTargetDate( final LocalDate targetDate ) {
			this.onConstruction.targetDate = targetDate;
			return this;
		}

		public DailyExtractionResourcesContainer build() {
			Objects.requireNonNull(this.onConstruction.miningRepository);
			this.onConstruction.fillResources();
			return this.onConstruction;
		}
	}
}
