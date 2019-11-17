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

import org.dimensinfin.eveonline.neocom.database.persister.GetCharactersCharacterIdAssets200OkPersister;
import org.dimensinfin.eveonline.neocom.database.persister.LocationIdentifierPersister;
import org.dimensinfin.eveonline.neocom.domain.LocationIdentifier;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

@Entity(name = "Assets")
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
	@Column(name = "ownerId", nullable = false)
	private Integer ownerId; // The id of the character or corporations that own the asset.
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
	@DatabaseField(persisterClass = GetCharactersCharacterIdAssets200OkPersister.class)
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "assetDelegate", nullable = false)
	private GetCharactersCharacterIdAssets200Ok assetDelegate;
	@DatabaseField(index = true, persisterClass = LocationIdentifierPersister.class)
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

	public NeoAsset() { }

	// - G E T T E R S   &   S E T T E R S
	public UUID getUid() {
		return this.uid;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	public Long getAssetId() {
		return this.assetId;
	}

	public LocationIdentifier getLocationId() {
		return this.locationId;
	}

	public Long getParentContainerId() {
		return this.parentContainerId;
	}

	public NeoAsset getParentContainer() {
		return this.parentContainer;
	}

	public boolean hasParentContainer() {
		// Check the exception case for STRUCTURE contents.
		if (this.locationId.getType() == LocationIdentifierType.STRUCTURE) return false;
		if (null != this.parentContainerId) return true;
		return false;
	}

	// - C O N T R U C T I O N   S E T T E R S
	public NeoAsset generateUid() {
		this.uid = UUID.randomUUID();
		return this;
	}

	public NeoAsset setOwnerId( final Integer ownerId ) {
		this.ownerId = ownerId;
		return this;
	}

	public NeoAsset setAssetDelegate( final GetCharactersCharacterIdAssets200Ok assetDelegate ) {
		this.assetDelegate = assetDelegate;
		return this;
	}

	public NeoAsset setParentContainer( final NeoAsset parentContainer ) {
		this.parentContainer = parentContainer;
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
		if (null != this.itemDelegate) {
			this.category = this.itemDelegate.getCategoryName();
			this.group = this.itemDelegate.getGroupName();
		}
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
}
