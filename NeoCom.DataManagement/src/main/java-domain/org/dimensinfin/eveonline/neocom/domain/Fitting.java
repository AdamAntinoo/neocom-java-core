package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;

public class Fitting extends NeoComNode {
	private static final long serialVersionUID = 2267335283642321303L;

	private GetCharactersCharacterIdFittings200Ok fittingDescription;

	//	private int fittingId = -1;
//	private String name = null;
//	private String description = null;
	private int shipTypeId = -1;
	private List<FittingItem> items = new ArrayList<>();
	private transient NeoItem shipItem = null;

	// - C O N S T R U C T O R S
	private Fitting() {}

	// - G E T T E R S   &   S E T T E R S

	/**
	 * During the transformation this method will be called with the original list of items that are encoded in location and in
	 * type. During the assignment we should process that list and expand them to a full list of enumerated ship locations and
	 * full eve items type.
	 *
	 * @param fittingData original ESI item data.
	 */
	private void downloadFittingItems( final GetCharactersCharacterIdFittings200Ok fittingData ) {
		this.items.clear();
		for (CharacterscharacterIdfittingsItems item : fittingData.getItems()) {
			final FittingItem newitem = new FittingItem.Builder( item ).build();
			this.items.add( newitem );
		}
	}


//	@Override
//	public String toString() {
//		return new StringBuffer("Fitting [")
//				.append("id: ").append(fittingId).append(" ")
//				.append("name: ").append(name).append(" ")
//				.append("]")
//				.append("->").append(super.toString())
//				.toString();
//	}

	// - B U I L D E R
	public static class Builder {
		private Fitting onConstruction;

		public Builder() {
			this.onConstruction = new Fitting();
		}

		public Fitting build() {
			return this.onConstruction;
		}

		public Fitting.Builder withFittingData( final GetCharactersCharacterIdFittings200Ok fittingData ) {
			Objects.requireNonNull( fittingData );
			this.onConstruction.fittingDescription = fittingData;
			// Download the items that are used on this fitting.
			this.onConstruction.downloadFittingItems(fittingData);
			return this;
		}
	}
}
