package org.dimensinfin.eveonline.neocom.domain;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;

public class FittingItem extends NeoComNode {
	private transient CharacterscharacterIdfittingsItems fittingDefinition;
	private transient NeoItem itemDetails;

	// - C O N S T R U C T O R S
	private FittingItem() {}

	// - G E T T E R S   &   S E T T E R S
	public CharacterscharacterIdfittingsItems.FlagEnum getFlag() {return fittingDefinition.getFlag();}

	public Integer getQuantity() {return fittingDefinition.getQuantity();}

	public Integer getTypeId() {return fittingDefinition.getTypeId();}

	private void downloadItem() {
		Objects.requireNonNull( this.fittingDefinition );
		this.itemDetails = new NeoItem( this.fittingDefinition.getTypeId() );
	}

	// - B U I L D E R
	public static class Builder {
		private FittingItem onConstruction;

		public Builder( final CharacterscharacterIdfittingsItems fittingItem ) {
			Objects.requireNonNull( fittingItem );
			this.onConstruction = new FittingItem();
			this.onConstruction.fittingDefinition = fittingItem;
		}

		public FittingItem build() {
			this.onConstruction.downloadItem();
			return this.onConstruction;
		}
	}
}
