package org.dimensinfin.eveonline.neocom.database.entities;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.model.EveItem;

import java.util.Objects;

public class NeoAsset {
	private GetCharactersCharacterIdAssets200Ok assetDelegate;
	private EveItem itemDelegate;

	// - D E L E G A T E D   M E T H O D S
	public Integer getQuantity() {return assetDelegate.getQuantity();}

	public int getTypeId() {return this.itemDelegate.getTypeId();}

	public String getName() {return this.itemDelegate.getName();}

	public double getPrice() {return this.itemDelegate.getPrice();}

	public int getGroupId() {return this.itemDelegate.getGroupId();}

	public int getCategoryId() {return this.itemDelegate.getCategoryId();}

	public String getTech() {return this.itemDelegate.getTech();}

	public double getVolume() {return this.itemDelegate.getVolume();}

	public boolean isBlueprint() {return this.itemDelegate.isBlueprint();}

	public String getCategoryName() {return this.itemDelegate.getCategoryName();}

	public String getGroupName() {return this.itemDelegate.getGroupName();}

	public String getURLForItem() {return this.itemDelegate.getURLForItem();}

	// - B U I L D E R
	public static class Builder {
		private NeoAsset onConstruction;

		public Builder() {
			this.onConstruction = new NeoAsset();
		}

		public NeoAsset fromEsiAsset( final GetCharactersCharacterIdAssets200Ok esiAsset ) {
			this.onConstruction.assetDelegate = esiAsset;
			this.onConstruction.itemDelegate = new EveItem(esiAsset.getTypeId());
			Objects.requireNonNull(this.onConstruction.itemDelegate);
			return this.onConstruction;
		}
	}
}
