//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.planetary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComExpandableNode;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ColonyStructure extends NeoComExpandableNode {
	public enum EPlanetaryStructureType {
		DEFAULT, COMMAND_CENTER, EXTRACTOR, BASIC_INDUSTRY, ADVANCED_INDUSTRY, HIGH_TECH_PRODUCTION, STORAGE, LAUNCHPAD
	}

	public static class ColonyExtractor extends NeoComNode {
		private List<ColonyStructure.ColonyExtractorHead> heads = new ArrayList<ColonyStructure.ColonyExtractorHead>();
		private Integer productTypeId = null;
		private transient EveItem item = null;
		private Integer cycleTime = null;
		private Float headRadius = null;
		private Integer qtyPerCycle = null;

		// --- G E T T E R S   &   S E T T E R S
		public List<ColonyStructure.ColonyExtractorHead> getHeads() {
			return heads;
		}

		public Integer getProductTypeId() {
			return productTypeId;
		}

		public Integer getCycleTime() {
			return cycleTime;
		}

		public Float getHeadRadius() {
			return headRadius;
		}

		public Integer getQtyPerCycle() {
			return qtyPerCycle;
		}

		public void setHeads( final List<ColonyStructure.ColonyExtractorHead> heads ) {
			this.heads = heads;
		}

		public void setCycleTime( final Integer cycleTime ) {
			this.cycleTime = cycleTime;
		}

		public void setHeadRadius( final Float headRadius ) {
			this.headRadius = headRadius;
		}

		public void setProductTypeId( final Integer productTypeId ) {
			this.productTypeId = productTypeId;
			// Update the production type with the Item data from the SDE.
			try {
				item = accessGlobal().searchItem4Id(productTypeId);
			} catch (NeoComRuntimeException neoe) {
				item = new EveItem();
			}
		}

		public void setQtyPerCycle( final Integer qtyPerCycle ) {
			this.qtyPerCycle = qtyPerCycle;
		}

		// --- D E L E G A T E D   M E T H O D S
//		@JsonIgnore
		public String getProductTypeName() {
			try {
				if (null == item) item = accessGlobal().searchItem4Id(productTypeId);
			} catch (NeoComRuntimeException neoe) {
				item = new EveItem();
			}
			return item.getName();
		}
	}

	public static class ColonyExtractorHead {
		private Integer headId = null;
		private Float latitude = null;
		private Float longitude = null;

		public Integer getHeadId() {
			return headId;
		}

		public void setHeadId( final Integer headId ) {
			this.headId = headId;
		}

		public Float getLatitude() {
			return latitude;
		}

		public void setLatitude( final Float latitude ) {
			this.latitude = latitude;
		}

		public Float getLongitude() {
			return longitude;
		}

		public void setLongitude( final Float longitude ) {
			this.longitude = longitude;
		}
	}

	public static class ColonyFactoryDetail {
		private Integer schematicId = null;

		public Integer getSchematicId() {
			return schematicId;
		}

		public void setSchematicId( final Integer schematicId ) {
			this.schematicId = schematicId;
		}
	}

	public static class ColonyContent extends NeoComNode {
		private Integer typeId = null;
		@JsonIgnore
		private transient EveItem item = null;
		private Long amount = null;

		// --- G E T T E R S   &   S E T T E R S
		public Integer getTypeId() {
			return typeId;
		}

		public Long getAmount() {
			return amount;
		}

		@JsonIgnore
		public EveItem getItem() {
			// Check if the item is loaded. If not try to get it from the SDE.
			try {
				if (null == item) item = accessGlobal().searchItem4Id(typeId);
			} catch (NeoComRuntimeException neoe) {
				item = new EveItem();
			}
			return item;
		}

		public void setAmount( final Long amount ) {
			this.amount = amount;
		}

		public void setTypeId( final Integer typeId ) {
			this.typeId = typeId;
			// Get the Eve item data from the SDE so we can perform calculations.
			try {
				item = accessGlobal().searchItem4Id(typeId);
			} catch (NeoComRuntimeException neoe) {
				item = new EveItem();
			}
		}

		@JsonIgnore
		public void setItem( final EveItem item ) {
			this.item = item;
		}

		// --- D E L E G A T E D   M E T H O D S
		//	@JsonIgnore
		public String getCategoryName() {
			return getItem().getCategoryName();
		}

		//	@JsonIgnore
		public String getGroupName() {
			return getItem().getGroupName();
		}

		//	@JsonIgnore
		public String getName() {
			return getItem().getName();
		}

		//	@JsonIgnore
		public double getVolume() {
			return getItem().getVolume();
		}

		//		@JsonIgnore
		public double getPrice() {
			return getItem().getPrice();
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ColonyStructure.class);

	// - F I E L D - S E C T I O N ............................................................................
	private Float latitude = null;
	private Float longitude = null;
	private Long pinId = null;
	private Integer typeId = null;
	private Integer schematicId = null;
	private ColonyExtractor extractorDetails = null;
	private ColonyFactoryDetail factoryDetails = null;
	private List<ColonyContent> contents = new ArrayList<ColonyContent>();
	private DateTime installTime = null;
	private DateTime expiryTime = null;
	private DateTime lastCycleStart = null;

	private EPlanetaryStructureType structureType = EPlanetaryStructureType.DEFAULT;
	// Add fields for Command, Storage and Launchpad contents.
	private double volumeUsed = 0.0;
	private double contentValue = 0.0;
	private double capacity = -1.0;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ColonyStructure() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public List<ICollaboration> collaborate2Model( final String s ) {
		List<ICollaboration> results = new ArrayList<>();
		if (null != contents) {
			if (contents.size() > 0) {
				results.addAll(contents);
			}
		}
		return results;
	}

	//	@JsonIgnore
	@Override
	public boolean isEmpty() {
		// Check this depending on the structure type.
		final EPlanetaryStructureType type = getStructureTypeCode();
		switch (type) {
			case COMMAND_CENTER:
			case STORAGE:
			case LAUNCHPAD:
				if ((null != contents) && (contents.size() > 0)) return false;
				break;
		}
		return true;
	}

	// --- G E T T E R S   &   S E T T E R S
	public Float getLatitude() {
		return latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public Long getPinId() {
		return pinId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public Integer getSchematicId() {
		return schematicId;
	}

	public DateTime getInstallTime() {
		return installTime;
	}

	public DateTime getExpiryTime() {
		return expiryTime;
	}

	public DateTime getLastCycleStart() {
		return lastCycleStart;
	}

	public List<ColonyContent> getContentList() {
		return contents;
	}

	//	@JsonIgnore
	public EPlanetaryStructureType getStructureTypeCode() {
		if (structureType == EPlanetaryStructureType.DEFAULT) structureType = calculateStructureTypeCode();
		return structureType;
	}

	public double getVolumeUsed() {
		return volumeUsed;
	}

	//	@JsonIgnore
	public double getVolumeUsedPct() {
		if (capacity < 0.0)
			switch (getStructureTypeCode()) {
				case COMMAND_CENTER:
					capacity = 500.0;
					break;
				case STORAGE:
					capacity = 12000.0;
					break;
				case LAUNCHPAD:
					capacity = 10000.0;
					break;
			}
		return volumeUsed / capacity * 100.0;
	}

	//	@JsonIgnore
	public double getCapacity() {
		if (capacity < 0.0)
			switch (getStructureTypeCode()) {
				case COMMAND_CENTER:
					capacity = 500.0;
					break;
				case STORAGE:
					capacity = 12000.0;
					break;
				case LAUNCHPAD:
					capacity = 10000.0;
					break;
			}
		return capacity;
	}

	public double getContentValue() {
		return contentValue;
	}

	public ColonyExtractor getExtractorDetails() {
		return extractorDetails;
	}

	public void setLatitude( final Float latitude ) {
		this.latitude = latitude;
	}

	public void setLongitude( final Float longitude ) {
		this.longitude = longitude;
	}

	public void setPinId( final Long pinId ) {
		this.pinId = pinId;
	}

	public void setTypeId( final Integer typeId ) {
		this.typeId = typeId;
	}

	public void setSchematicId( final Integer schematicId ) {
		this.schematicId = schematicId;
	}

	public void setExtractorDetails( final ColonyExtractor extractorDetails ) {
		this.extractorDetails = extractorDetails;
	}

	public void setFactoryDetails( final ColonyFactoryDetail factoryDetails ) {
		this.factoryDetails = factoryDetails;
	}

	public void setContents( final List<ColonyContent> contents ) {
		this.contents = contents;
		// Calculate the value and volume of the contents of the structure.
		volumeUsed = 0.0;
		contentValue = 0.0;
		for (ColonyContent content : contents) {
			volumeUsed += content.getAmount() * content.getItem().getVolume();
			try {
				contentValue += content.getAmount() * content.getItem().getHighestBuyerPrice().getPrice();
			} catch (ExecutionException ee) {
				contentValue+=content.getAmount() * content.getItem().getPrice();
			} catch (InterruptedException ie) {
				contentValue+=content.getAmount() * content.getItem().getPrice();
			}
		}
		// Cross use of other properties is desallowed because load order is not guaranteed.
		//		if ( capacity < 0 )
		//			switch (getStructureTypeCode()) {
		//				case COMMAND_CENTER:
		//					capacity = 500.0;
		//					break;
		//				case STORAGE:
		//					capacity = 12000.0;
		//					break;
		//				case LAUNCHPAD:
		//					capacity = 10000.0;
		//					break;
		//			}
	}

	public void setInstallTime( final DateTime installTime ) {
		this.installTime = installTime;
	}

	public void setExpiryTime( final DateTime expiryTime ) {
		this.expiryTime = expiryTime;
	}

	public void setLastCycleStart( final DateTime lastCycleStart ) {
		this.lastCycleStart = lastCycleStart;
	}

	public void setVolumeUsed( final double volumeUsed ) {
		this.volumeUsed = volumeUsed;
	}

	public void setContentValue( final double contentValue ) {
		this.contentValue = contentValue;
	}

	// --- D E L E G A T E D   M E T H O D S
	private EPlanetaryStructureType calculateStructureTypeCode() {
		// Barren structures
		if (getTypeId() == 2524) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2544) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2541) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 2848) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2473) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2474) return EPlanetaryStructureType.ADVANCED_INDUSTRY;
		if (getTypeId() == 2475) return EPlanetaryStructureType.HIGH_TECH_PRODUCTION;

		// Temperate structures
		if (getTypeId() == 2254) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2256) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2562) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3068) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2481) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2480) return EPlanetaryStructureType.ADVANCED_INDUSTRY;
		if (getTypeId() == 2482) return EPlanetaryStructureType.HIGH_TECH_PRODUCTION;

		// Lava structures
		if (getTypeId() == 2549) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2555) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2558) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3062) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2469) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2470) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Plasma structures
		if (getTypeId() == 2472) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2556) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2560) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3064) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2471) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2472) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Gas structures
		if (getTypeId() == 2534) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2543) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2536) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3060) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2492) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2494) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Oceanic structures
		if (getTypeId() == 2525) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2542) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2535) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3063) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2490) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2485) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Ice structures
		if (getTypeId() == 2533) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2552) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2257) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3061) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2493) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2491) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Storm structures
		if (getTypeId() == 2550) return EPlanetaryStructureType.COMMAND_CENTER;
		if (getTypeId() == 2557) return EPlanetaryStructureType.LAUNCHPAD;
		if (getTypeId() == 2561) return EPlanetaryStructureType.STORAGE;
		if (getTypeId() == 3067) return EPlanetaryStructureType.EXTRACTOR;
		if (getTypeId() == 2483) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if (getTypeId() == 2484) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		return EPlanetaryStructureType.DEFAULT;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("ColonyStructure [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
