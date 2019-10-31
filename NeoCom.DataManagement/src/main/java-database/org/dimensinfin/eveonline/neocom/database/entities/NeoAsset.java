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

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
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
	@DatabaseField
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "assetDelegate", nullable = false)
	private GetCharactersCharacterIdAssets200Ok assetDelegate;
	// - A P I   C C P   F I E L D S
	@DatabaseField(index = true)
	@Column(name = "assetId", nullable = false)
	private Long assetId;
	@DatabaseField
	@Column(name = "typeId", nullable = false)
	private Integer typeId;
	@DatabaseField(index = true)
	@Type(type = "jsonb")
	@Column(name = "locationIdentifier", nullable = false)
	private LocationIdentifier locationId;
	@DatabaseField
	@Column(name = "userLabel")
	private String userLabel;
	@DatabaseField
	@Column(name = "blueprintFlag")
	private boolean blueprintFlag = false;
	@DatabaseField
	@Column(name = "shipFlag")
	private boolean shipFlag = false;
	@DatabaseField
	@Column(name = "containerFlag")
	private boolean containerFlag = false;
	@DatabaseField
	@Column(name = "parentContainer", nullable = true)
	private Long parentContainer;

	@JsonIgnore
	private EveItem itemDelegate;

	private NeoAsset() {
	}

	// - G E T T E R S   &   S E T T E R S
	public UUID getUid() {
		return uid;
	}

	public Long getAssetId() {
		return assetId;
	}

	public Long getParentContainer() {
		return this.parentContainer;
	}

	public NeoAsset setParentContainer( final Long parentContainer ) {
		this.parentContainer = parentContainer;
		return this;
	}

	public boolean hasParentContainer() {
		if (null != this.parentContainer) return true;
		return false;
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

	public boolean isBlueprintCopy() {return this.assetDelegate.getIsBlueprintCopy();}

	public String getCategoryName() {return this.itemDelegate.getCategoryName();}

	public String getGroupName() {return this.itemDelegate.getGroupName();}

	public String getURLForItem() {return this.itemDelegate.getURLForItem();}

	public String getUserLabel() {
		return this.userLabel;
	}

	public boolean isContainer() {
		return this.containerFlag;
	}

	public boolean isPackaged() {
		return this.assetDelegate.getIsSingleton();
	}

	public boolean isShip() {
		return this.shipFlag;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoAsset onConstruction;

		public Builder() {
			this.onConstruction = new NeoAsset();
		}

		public NeoAsset fromEsiAsset( final GetCharactersCharacterIdAssets200Ok esiAsset ) {
			Objects.requireNonNull( esiAsset );
			EsiAssetTransformer.transform( esiAsset, this.onConstruction );
			Objects.requireNonNull( this.onConstruction.itemDelegate );
			return this.onConstruction;
		}
	}

	private static class EsiAssetTransformer {
		private static void transform( final GetCharactersCharacterIdAssets200Ok esiAsset,
		                               NeoAsset asset ) {
			asset.assetId = esiAsset.getItemId();
			asset.typeId = esiAsset.getTypeId();
			asset.assetDelegate = esiAsset;
			asset.itemDelegate = new EveItem( esiAsset.getTypeId() );
			asset.locationId = transformLocation( esiAsset.getLocationId(),
					esiAsset.getLocationFlag(),
					esiAsset.getLocationType() );
			if (asset.getCategoryName().equalsIgnoreCase( ModelWideConstants.eveglobal.Ship )) {
				asset.shipFlag = true;
			}
			asset.containerFlag = checkIfContainer( asset );
		}

		private static LocationIdentifier transformLocation( final Long locationId,
		                                                     final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag,
		                                                     final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType ) {
			return new LocationIdentifier.Builder()
					.withSpaceIdentifier( locationId )
					.build();
		}

		private static boolean checkIfContainer( final NeoAsset asset ) {
			if (asset.isBlueprint()) return false;
			if (asset.isShip()) return true;
			// Use a list of types to set what is a container
			if (asset.getTypeId() == 11488) return true;
			if (asset.getTypeId() == 11489) return true;
			if (asset.getTypeId() == 11490) return true;
			if (asset.getTypeId() == 17363) return true;
			if (asset.getTypeId() == 17364) return true;
			if (asset.getTypeId() == 17365) return true;
			if (asset.getTypeId() == 17366) return true;
			if (asset.getTypeId() == 17367) return true;
			if (asset.getTypeId() == 17368) return true;
			if (asset.getTypeId() == 2263) return true;
			if (asset.getTypeId() == 23) return true;
			if (asset.getTypeId() == 24445) return true;
			if (asset.getTypeId() == 28570) return true;
			if (asset.getTypeId() == 3293) return true;
			if (asset.getTypeId() == 3296) return true;
			if (asset.getTypeId() == 3297) return true;
			if (asset.getTypeId() == 33003) return true;
			if (asset.getTypeId() == 33005) return true;
			if (asset.getTypeId() == 33007) return true;
			if (asset.getTypeId() == 33009) return true;
			if (asset.getTypeId() == 33011) return true;
			if (asset.getTypeId() == 3465) return true;
			if (asset.getTypeId() == 3466) return true;
			if (asset.getTypeId() == 3467) return true;
			if (asset.getTypeId() == 3468) return true;
			if (asset.getTypeId() == 41567) return true;
			if (asset.getTypeId() == 60) return true; // Asset Safety Wrap
			if (asset.getName().contains( "Container" )) return true;
			return asset.getName().contains( "Wrap" );
		}

	}
}
