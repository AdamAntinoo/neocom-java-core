package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import org.dimensinfin.eveonline.neocom.domain.EveItem;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

@Entity(name = "Assets")
@DatabaseTable(tableName = "Assets")
public class NeoAsset extends UpdatableEntity {
	@Id
	@GeneratedValue(generator = "UUID_generator")
	@GenericGenerator(
			name = "UUID_generator",
			strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID uid;
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "assetDelegate", updatable = true, nullable = false)
	private GetCharactersCharacterIdAssets200Ok assetDelegate;
	// - A P I   C C P   F I E L D S
	@DatabaseField(index = true)
	@Column(name = "assetId", updatable = true, nullable = false)
	private Long assetId;
	//	@DatabaseField
//	@Column(name = "typeId", updatable = true, nullable = false)
//	private Integer typeId;
	@DatabaseField(index = true)
	@Type(type = "jsonb")
	@Column(name = "locationIdentifier", updatable = true, nullable = false)
	private LocationIdentifier locationId;

	@JsonIgnore
	private EveItem itemDelegate;

	private NeoAsset() {
	}

	// - G E T T E R S   &   S E T T E R S
	public UUID getUid() {
		return uid;
	}

	// - D E L E G A T E D   M E T H O D S
	public int getTypeId() {return this.assetDelegate.getTypeId();}

	public Integer getQuantity() {return assetDelegate.getQuantity();}

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
			Objects.requireNonNull( esiAsset );
			this.onConstruction.assetDelegate = esiAsset;
			this.onConstruction.itemDelegate = new EveItem( esiAsset.getTypeId() );
			Objects.requireNonNull( this.onConstruction.itemDelegate );
			return this.onConstruction;
		}
	}
}
