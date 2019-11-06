package org.dimensinfin.eveonline.neocom.database.entities;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;

@Entity(name = "Assets")
//@DatabaseTable(tableName = "Assets")
public class NeoAsset extends UpdatableEntity {
	@Id
	@GeneratedValue(generator = "UUID_generator")
	@GenericGenerator(
			name = "UUID_generator",
			strategy = "org.hibernate.id.UUIDGenerator"
	)
	@Column(name = "id", updatable = false, nullable = false)
	private UUID uid;
	// - A P I   C C P   F I E L D S
	@DatabaseField(index = true)
	@Column(name = "assetId", nullable = false)
	private Long assetId;
	@DatabaseField
	@Column(name = "typeId", nullable = false)
	private Integer typeId;
	@DatabaseField
	@Column(name = "blueprintFlag")
	private boolean blueprintFlag = false;
	@DatabaseField
	@Column(name = "shipFlag")
	private boolean shipFlag = false;
	@DatabaseField
	@Column(name = "containerFlag")
	private boolean containerFlag = false;
	@DatabaseField(index = true)
	@Column(name = "group")
	private String group;
	@DatabaseField(index = true)
	@Column(name = "category")
	private String category;
	@DatabaseField
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "assetDelegate", nullable = false)
	private GetCharactersCharacterIdAssets200Ok assetDelegate;
	@DatabaseField(index = true)
	@Type(type = "jsonb")
	@Column(name = "locationIdentifier", nullable = false)
	private LocationIdentifier locationId;
	@DatabaseField
	@Column(name = "userLabel")
	private String userLabel;
	@DatabaseField
	@Column(name = "parentContainer", nullable = true)
	private Long parentContainerId;
	// - C O O P E R A T I V E   F I E L D S
	@JsonIgnore
	@Transient
	private transient NeoItem itemDelegate;
	@JsonIgnore
	@Transient
	private transient NeoAsset parentContainer;

	public NeoAsset() {
	}

	// - G E T T E R S   &   S E T T E R S
	public UUID getUid() {
		return uid;
	}

	public Long getAssetId() {
		return assetId;
	}

	public LocationIdentifier getLocationId() {
		return locationId;
	}

	public Long getParentContainerId() {
		return this.parentContainerId;
	}

	public boolean hasParentContainer() {
		if (null != this.parentContainerId) return true;
		return false;
	}

	// - C O N T R U C T I O N   S E T T E R S
	public NeoAsset setAssetDelegate( final GetCharactersCharacterIdAssets200Ok assetDelegate ) {
		this.assetDelegate = assetDelegate;
		return this;
	}

	public NeoAsset setAssetId( final Long assetId ) {
		this.assetId = assetId;
		return this;
	}

	public NeoAsset setTypeId( final Integer typeId ) {
		this.typeId = typeId;
		return this;
	}

	public NeoAsset setLocationId( final LocationIdentifier locationId ) {
		this.locationId = locationId;
		return this;
	}

	public NeoAsset setUserLabel( final String userLabel ) {
		this.userLabel = userLabel;
		return this;
	}

	public NeoAsset setItemDelegate( final NeoItem itemDelegate ) {
		this.itemDelegate = itemDelegate;
		return this;
	}

	public NeoAsset setBlueprintFlag( final boolean blueprintFlag ) {
		this.blueprintFlag = blueprintFlag;
		return this;
	}

	public NeoAsset setShipFlag( final boolean shipFlag ) {
		this.shipFlag = shipFlag;
		return this;
	}

	public NeoAsset setContainerFlag( final boolean containerFlag ) {
		this.containerFlag = containerFlag;
		return this;
	}

	public NeoAsset setParentContainerId( final Long parentContainerId ) {
		this.parentContainerId = parentContainerId;
		return this;
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

	public boolean isStructure() {
		return false;
	}

//	// - B U I L D E R
//	public static class Builder {
//		private NeoAsset onConstruction;
//
//		public Builder() {
//			this.onConstruction = new NeoAsset();
//		}
//
//		public NeoAsset.Builder fromEsiAsset( final GetCharactersCharacterIdAssets200Ok esiAsset ) {
//			Objects.requireNonNull( esiAsset );
//			EsiAssetTransformer.transform( esiAsset, this.onConstruction );
//			Objects.requireNonNull( this.onConstruction.itemDelegate );
//			return this;
//		}
//
//		public NeoAsset.Builder withPublicStructure( final Long structureId ) {
//			Objects.requireNonNull( structureId );
//			Objects.requireNonNull( this.onConstruction.itemDelegate ); // Protect the order. This should be after the esi.
//			this.onConstruction.locationId.setType( LocationIdentifierType.STRUCTURE );
//			this.onConstruction.locationId.setStructureIdentifier( structureId );
//			return this;
//		}
//
//		public NeoAsset build() {
//			return this.onConstruction;
//		}
//	}

//	private static class EsiAssetTransformer {
//		private static void transform( final GetCharactersCharacterIdAssets200Ok esiAsset,
//		                               NeoAsset asset ) {
////			asset.assetId = esiAsset.getItemId();
////			asset.typeId = esiAsset.getTypeId();
////			asset.assetDelegate = esiAsset;
////			asset.itemDelegate = new NeoItem( esiAsset.getTypeId() );
////			asset.locationId = transformLocation( esiAsset.getLocationId(),
////					esiAsset.getLocationFlag(),
////					esiAsset.getLocationType() );
////			if (asset.getCategoryName().equalsIgnoreCase( ModelWideConstants.eveglobal.Ship )) {
////				asset.shipFlag = true;
////			}
////			asset.containerFlag = checkIfContainer( asset );
//			if (esiAsset.getLocationId() > 61E6) // The asset is contained into another asset. Set the parent.
//				asset.parentContainerId = esiAsset.getLocationId();
//		}

//		private static LocationIdentifier transformLocation( final Long locationId,
//		                                                     final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag,
//		                                                     final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType ) {
//			return new LocationIdentifier.Builder()
//					.withSpaceIdentifier( locationId )
//					.withLocationFlag( locationFlag )
//					.withLocationType( locationType )
//					.build();
//		}

//		private static boolean checkIfContainer( final NeoAsset asset ) {
//			if (asset.isBlueprint()) return false;
//			if (asset.isShip()) return true;
//			// Use a list of types to set what is a container
//			if (asset.getTypeId() == 11488) return true;
//			if (asset.getTypeId() == 11489) return true;
//			if (asset.getTypeId() == 11490) return true;
//			if (asset.getTypeId() == 17363) return true;
//			if (asset.getTypeId() == 17364) return true;
//			if (asset.getTypeId() == 17365) return true;
//			if (asset.getTypeId() == 17366) return true;
//			if (asset.getTypeId() == 17367) return true;
//			if (asset.getTypeId() == 17368) return true;
//			if (asset.getTypeId() == 2263) return true;
//			if (asset.getTypeId() == 23) return true;
//			if (asset.getTypeId() == 24445) return true;
//			if (asset.getTypeId() == 28570) return true;
//			if (asset.getTypeId() == 3293) return true;
//			if (asset.getTypeId() == 3296) return true;
//			if (asset.getTypeId() == 3297) return true;
//			if (asset.getTypeId() == 33003) return true;
//			if (asset.getTypeId() == 33005) return true;
//			if (asset.getTypeId() == 33007) return true;
//			if (asset.getTypeId() == 33009) return true;
//			if (asset.getTypeId() == 33011) return true;
//			if (asset.getTypeId() == 3465) return true;
//			if (asset.getTypeId() == 3466) return true;
//			if (asset.getTypeId() == 3467) return true;
//			if (asset.getTypeId() == 3468) return true;
//			if (asset.getTypeId() == 41567) return true;
//			if (asset.getTypeId() == 60) return true; // Asset Safety Wrap
//			if (asset.getName().contains( "Container" )) return true;
//			return asset.getName().contains( "Wrap" );
//		}
	}
}
