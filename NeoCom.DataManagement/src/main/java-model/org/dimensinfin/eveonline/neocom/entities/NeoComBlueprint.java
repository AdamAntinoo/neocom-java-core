//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.entities;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdBlueprints200Ok;
import org.dimensinfin.eveonline.neocom.interfaces.ILocatableAsset;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

/**
 * Blueprints can be obtained separately from the Assets in a new CREST API call. Use that to speed up access
 * and then use internal queries to link blueprint instances to their corresponding assets. In the database
 * the blueprints are stored stacked to improve blueprint processing. The time to recover blueprints is much
 * more improved and the drawbacks can be circumvented with the use of more data storage.
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
@DatabaseTable(tableName = "Blueprints")
public class NeoComBlueprint extends ANeoComEntity implements ILocatableAsset {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -1284879453130050089L;
	private static Logger logger = LoggerFactory.getLogger("NeoComBlueprint");

	// - D A T A B A S E   F I E L D - S E C T I O N ..........................................................
	/**
	 * This is a generated identifier to allow having duplicated blueprints numbers when processing updates.
	 * This is the primary key identifier and it is generated by an incremental sequence.
	 */
	@DatabaseField(index = true, generatedIdSequence = "blueprint_id_seq")
	private final long id = -2;
	@DatabaseField(index = true)
	protected long blueprintId;
	@DatabaseField(index = true)
	private int typeId = -1;
	/** This identifier points to the matching asset location. This location is used to pack similar blueprints. */
	@DatabaseField(index = true)
	private long locationId = -1;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	private GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType = null;
	@DatabaseField(dataType = DataType.ENUM_STRING)
	private GetCharactersCharacterIdBlueprints200Ok.LocationFlagEnum locationFlag = null;
	/**
	 * A range of numbers with a minimum of -2 and no maximum value where -1 is an original and -2 is a copy. It can be a
	 * positive integer if it is a stack of blueprint originals fresh from the market (e.g. no activities performed on them yet).
	 * If the blueprints are stacked, then this is the number of packs on the stack, similar to other stacks.
	 */
	@DatabaseField
	private int quantity = 1;
	@DatabaseField
	private int materialEfficiency = 0;
	@DatabaseField
	private int timeEfficiency = 0;
	/** Number of runs remaining if the blueprint is a copy, -1 if it is an original. */
	@DatabaseField
	private int runs = 0;
	@DatabaseField
	private long ownerId = -1;
	@DatabaseField
	private long parentAssetId = -1;

	// --- D E R I V E D   D A T A   F R O M   R E F E R E N C E S
	@DatabaseField
	private String typeName = "-";
	@DatabaseField
	private boolean bpo = false;
	@DatabaseField
	private boolean packaged = false;
	@DatabaseField
	private EveItem.ItemTechnology tech = EveItem.ItemTechnology.Tech_1;
	/**
	 * Blueprints are packed. Most of the times the blueprints are multiple stacks of a single item with some many copies. This
	 * is not usable on lists so while processing we pack blueprints of equal characteristics (same type, same efficiency, same
	 * location) into a single item that packs a set of source assets that are the identifiers stored into this field.
	 */
	@DatabaseField(dataType = DataType.LONG_STRING)
	private String stackIdRefences = "";

	// --- I N D U S T R Y   F I E L D S
	/** The type ID of the matching module that can be manufactured with this blueprint. */
	@DatabaseField
	private int moduleTypeId = -1;
	@DatabaseField
	private int manufactureIndex = -1;
	@DatabaseField
	private final int inventionIndex = -1;
	@DatabaseField
	private double jobProductionCost = -1.0;
	@DatabaseField
	private int manufacturableCount = -1;

	// - F I E L D - S E C T I O N ............................................................................
	protected transient NeoComAsset parentAssetCache = null;
	protected transient EveLocation locationCache = null;
	protected transient EveItem blueprintItem = null;
	protected transient EveItem moduleItem = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComBlueprint() {
		super();
	}

	/**
	 * Instantiate a new blueprint but for an id. This blueprint has no associated asset and it is not present
	 * on the list of assets for the character so it has some limited features.
	 * @param blueprintType
	 */
	public NeoComBlueprint( final int blueprintType ) {
		super();
		this.jsonClass = "NeoComBlueprint";
		this.typeId = blueprintType;
		this.blueprintItem = accessGlobal().searchItem4Id(blueprintType);
		this.typeName = this.blueprintItem.getName();
		this.moduleTypeId = accessGlobal().searchModule4Blueprint(this.typeId);
		this.moduleItem = accessGlobal().searchItem4Id(this.moduleTypeId);
		this.tech = this.obtainTech();
	}

	/**
	 * The creation method is called during the parsing conversion from esi objects to out MVC model. We still do not connect to
	 * the owner asset because this creation call can be used while Industry processing to split stacks or processes som blueprints.
	 * @param blueprintId the unique resource identifier for an existing resource on the database. This blueprint has no
	 *                    associated asset and it is not present on the list of assets for the character so it has some limited
	 *                    features.
	 */
	public NeoComBlueprint( final long blueprintId, final int blueprintType ) {
		this(blueprintType);
		this.blueprintId = blueprintId;
//		try {
//			this.accessAssociatedAsset();
		// Create the processor depending on the blueprint technology
//			this.blueprintItem = accessGlobal().searchItem4Id(blueprintType);
//			this.typeId = this.blueprintItem.getTypeId();
//			this.typeName = this.blueprintItem.getName();
//			this.moduleTypeId = accessGlobal().searchModule4Blueprint(this.typeId);
//			this.moduleItem = accessGlobal().searchItem4Id(this.moduleTypeId);
//			this.tech = this.obtainTech();
//		} catch (final Exception ex) {
//			throw new RuntimeException("W> Blueprint.<init> - Asset <" + blueprintId + "> not found.");
//		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................

	/**
	 * Update the values at the database record.
	 */
	public NeoComBlueprint store() {
		try {
			final Dao<NeoComBlueprint, String> blueprintDao = accessGlobal().getNeocomDBHelper().getBlueprintDao();
			blueprintDao.update(this);
			logger.info("-- [NeoComBlueprint.store]> NeoComBlueprint data updated successfully.");
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public String getCategoryName() {
		return this.getItem().getCategoryName();
	}

	public String getGroupName() {
		return this.getItem().getGroupName();
	}

	public long getBlueprintId() {
		return this.blueprintId;
	}

	public EveItem getItem() {
		return this.blueprintItem;
	}

	/**
	 * Get access to the real location reference for the blueprint. It can be a default for prototypes and the
	 * location of the associated asset for normal blueprints. Check the access to null elements and cache the
	 * result.
	 */
	public EveLocation getLocation() {
		try {
			if (null == locationCache) {
				locationCache = accessGlobal().searchLocation4Id(locationId);
			}
		} catch (NeoComRuntimeException neoe) {
			locationCache = new EveLocation();
		}
		return locationCache;
	}

	public long getLocationId() {
		return locationId;
	}

//	public int getManufacturableCount() {
//		return manufacturableCount;
//	}
//	public double getJobProductionCost() {
//		return jobProductionCost;
//	}

	public int getManufactureIndex() {
		return this.manufactureIndex;
	}

	public int getMaterialEfficiency() {
		return this.materialEfficiency;
	}

	public String getModuleCategory() {
		return getModuleItem().getCategoryName();
	}

	public String getModuleGroup() {
		return getModuleItem().getGroupName();
	}

	public String getModuleGroupCategory() {
		return getModuleItem().getGroupName() + "/" + getModuleItem().getCategoryName();
	}

	public EveItem getModuleItem() {
		if (null == this.moduleItem) {
			this.moduleItem = accessGlobal().searchItem4Id(moduleTypeId);
		}
		return this.moduleItem;
	}

	public String getModuleName() {
		return getModuleItem().getName();
	}

	public int getModuleTypeId() {
		return moduleTypeId;
	}

	public String getName() {
		return this.getTypeName();
	}

//	public NeoComAsset getParentContainer() {
//		return this.getAssociatedAsset().getParentContainer();
//	}

	public int getQuantity() {
		return quantity;
	}

	public int getRuns() {
		return runs;
	}

	public String getStackId() {
		final StringBuffer stackid = new StringBuffer();
		stackid.append(typeId).append(".").append(this.getLocationId()).append(".").append(this.getRuns());
		return stackid.toString();
	}

	public String getStackIdRefences() {
		return stackIdRefences;
	}

	public EveItem.ItemTechnology getTech() {
		return tech;
	}

	public int getTimeEfficiency() {
		return timeEfficiency;
	}

	public int getTypeId() {
		return typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public boolean isBpo() {
		return bpo;
	}

	public boolean isPackaged() {
		return packaged;
	}

//	public boolean isPrototype() {
//		if (null == associatedAsset)
//			return true;
//		else
//			return false;
//	}

	/**
	 * Add the assetId reference to the string with the list of current references to keep track of stack
	 * contents.
	 * @param refid the asset reference to store.
	 */
	public void registerReference( final long refid ) {
		if (stackIdRefences == "") {
			stackIdRefences = Long.valueOf(refid).toString();
		} else {
			stackIdRefences = stackIdRefences + ModelWideConstants.STACKID_SEPARATOR + Long.valueOf(refid).toString();
		}
	}

//	public void resetOwner() {
//		ownerId = -1;
//	}

	public NeoComBlueprint setBpo( final boolean bpo ) {
		this.bpo = bpo;
		return this;
	}

	public NeoComBlueprint setLocationId( final long locationId ) {
		this.locationId = locationId;
		return this;
	}

	public NeoComBlueprint setLocationFlag( final GetCharactersCharacterIdBlueprints200Ok.LocationFlagEnum locationFlag ) {
		this.locationFlag = locationFlag;
		return this;
	}
//	public void setLocationFlag( final int flag ) {
//		this.flag = flag;
//	}

//	public void setJobProductionCost( final double jobProductionCost ) {
//		this.jobProductionCost = jobProductionCost;
//	}

//	public void setLocationId( final long locationId ) {
//		containerId = locationId;
//	}

//	public void setManufacturableCount( final int manufacturableCount ) {
//		this.manufacturableCount = manufacturableCount;
//	}

	public NeoComBlueprint setManufactureIndex( final int manufactureIndex ) {
		this.manufactureIndex = manufactureIndex;
		return this;
	}

	public NeoComBlueprint setMaterialEfficiency( final int materialEfficiency ) {
		this.materialEfficiency = materialEfficiency;
		return this;
	}

	public void setModuleTypeId( final int moduleID ) {
		moduleTypeId = moduleID;
	}

	public NeoComBlueprint setOwnerId( final long ownerId ) {
		this.ownerId = ownerId;
		return this;
	}

	public NeoComBlueprint setPackaged( final boolean packaged ) {
		this.packaged = packaged;
		return this;
	}

	public NeoComBlueprint setQuantity( final int quantity ) {
		this.quantity = quantity;
		return this;
	}

	public NeoComBlueprint setRuns( final int runs ) {
		this.runs = runs;
		return this;
	}

	public void setTech( final EveItem.ItemTechnology tech ) {
		this.tech = tech;
	}

	public NeoComBlueprint setTimeEfficiency( final int timeEfficiency ) {
		this.timeEfficiency = timeEfficiency;
		return this;
	}

	public NeoComBlueprint setTypeId( final int typeId ) {
		this.typeId = typeId;
		return this;
	}

	public void setTypeName( final String typeName ) {
		this.typeName = typeName;
	}

	// --- I L O C A T A B L E    I N T E R F A C E
	@Override
	public NeoComBlueprint setLocationType( final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType ) {
		this.locationType = locationType;
		return this;
	}

	@Override
	public NeoComBlueprint setLocationFlag( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum newFlag ) {
		this.locationFlag = GetCharactersCharacterIdBlueprints200Ok.LocationFlagEnum.valueOf(newFlag.name());
		return this;
	}

	/**
	 * This method usually return the parent container of an asset. This is valid when the asset is inside a
	 * container or a ship or any other asset, but when the asset is located on other corporation POCO or other
	 * unknown or unaccessible asset then this method fails to get the parent asset. <br>
	 * In that case we should replace the parent pointer to a new location pointer that gets stored into our new
	 * Locations table. With that change assets will be reallocated to a valid place and all code will
	 * understand the changes and behave correctly.
	 */
	@Override
	public NeoComAsset getParentContainer() {
		if (this.parentAssetId > 0) if (null == this.parentAssetCache) {
			// Search for the parent asset. If not found then go to the transformation method.
			try {
				this.parentAssetCache = accessGlobal().getNeocomDBHelper().getAssetDao()
						.queryForId(Long.valueOf(this.parentAssetId).toString());
			} catch (SQLException sqle) {
				return null;
			}
		}
		return this.parentAssetCache;
	}

	@Override
	public long getParentContainerId() {
		return this.parentAssetId;
	}

	@Override
	public boolean hasParent() {
		if (this.parentAssetId > 0)
			return true;
		else
			return false;
	}

	@Override
	public void setParentContainer( final NeoComAsset newParent ) {
		if (null != newParent) {
			this.parentAssetCache = newParent;
			this.parentAssetId = newParent.getAssetId();
			// Trigger an update of the record at the database.
			this.store();
		}
	}

	@Override
	public void setParentId( final long pid ) {
		this.parentAssetId = pid;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Blueprint [ ");
//		if (associatedAsset == null) {
//			buffer.append("[PROTO]").append(" ");
//		} else {
//			buffer.append("[").append(this.getAssociatedAsset().getAssetId()).append("]").append(" ")
		buffer.append(this.getStackIdRefences()).append(" ");
//		}
		buffer.append(typeName).append(" ");
		buffer.append("[").append(typeId).append("/").append(moduleTypeId).append("] ");
		buffer.append(tech).append(" ");
		buffer.append("Runs:").append(this.getRuns()).append(" ");
		buffer.append("MT:").append(this.getMaterialEfficiency()).append("/").append(this.getTimeEfficiency()).append(" ");
		buffer.append("Qty:").append(this.quantity).append(" ");
		if (null != this.locationCache) {
			buffer.append(this.locationCache.toString()).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

//	private void accessAssociatedAsset() {
//		try {
//			this.associatedAsset = accessGlobal().getNeocomDBHelper().getAssetDao().queryForEq(
//					"assetId"
//					, new Long(assetId).toString()).get(0);
//			// Also fill up the related fields.
//			this.locationId = this.associatedAsset.getLocationId();
//			this.locationCache = this.associatedAsset.getLocation();
//		} catch (final Exception ex) {
//		}
//	}

//	private NeoComAsset getAssociatedAsset() {
//		if (null == this.associatedAsset) {
//			this.accessAssociatedAsset();
//		}
//		return this.associatedAsset;
//	}

	/**
	 * Go to the database and get from the SDE the tech of the product manufactured with this blueprint.
	 * @return
	 */
	// TODO - We need to recode this call from the stack of SDE queries.
	private EveItem.ItemTechnology obtainTech() {
		return EveItem.ItemTechnology.lookupLabel(accessSDEDBHelper().searchTech4Blueprint(this.typeId));
	}
}

// - UNUSED CODE ............................................................................................
