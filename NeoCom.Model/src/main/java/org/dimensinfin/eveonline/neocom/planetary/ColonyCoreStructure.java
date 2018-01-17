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

import org.dimensinfin.eveonline.neocom.model.NeoComExpandableNode;
import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ColonyCoreStructure extends NeoComExpandableNode {
	public enum EPlanetaryStructureType {
		DEFAULT, COMMAND_CENTER, EXTRACTOR, BASIC_INDUSTRY, ADVANCED_INDUSTRY, HIGH_TECH_PRODUCTION, STORAGE, LAUNCHPAD
	}
	public static class ColonyExtractor extends NeoComNode{
		private List<ColonyStructure.ColonyExtractorHead> heads = new ArrayList<ColonyStructure.ColonyExtractorHead>();
		private Integer productTypeId = null;
		private Integer cycleTime = null;
		private Float headRadius = null;
		private Integer qtyPerCycle = null;

		public List<ColonyStructure.ColonyExtractorHead> getHeads () {
			return heads;
		}

		public void setHeads (final List<ColonyStructure.ColonyExtractorHead> heads) {
			this.heads = heads;
		}

		public Integer getProductTypeId () {
			return productTypeId;
		}

		public void setProductTypeId (final Integer productTypeId) {
			this.productTypeId = productTypeId;
		}

		public Integer getCycleTime () {
			return cycleTime;
		}

		public void setCycleTime (final Integer cycleTime) {
			this.cycleTime = cycleTime;
		}

		public Float getHeadRadius () {
			return headRadius;
		}

		public void setHeadRadius (final Float headRadius) {
			this.headRadius = headRadius;
		}

		public Integer getQtyPerCycle () {
			return qtyPerCycle;
		}

		public void setQtyPerCycle (final Integer qtyPerCycle) {
			this.qtyPerCycle = qtyPerCycle;
		}
	}

	public static class ColonyExtractorHead {
		private Integer headId = null;
		private Float latitude = null;
		private Float longitude = null;

		public Integer getHeadId () {
			return headId;
		}

		public void setHeadId (final Integer headId) {
			this.headId = headId;
		}

		public Float getLatitude () {
			return latitude;
		}

		public void setLatitude (final Float latitude) {
			this.latitude = latitude;
		}

		public Float getLongitude () {
			return longitude;
		}

		public void setLongitude (final Float longitude) {
			this.longitude = longitude;
		}
	}

	public static class ColonyFactoryDetail {
		private Integer schematicId = null;

		public Integer getSchematicId () {
			return schematicId;
		}

		public void setSchematicId (final Integer schematicId) {
			this.schematicId = schematicId;
		}
	}

	public static class ColonyContent extends NeoComNode{
		private Integer typeId = null;
		private Long amount = null;

		public Integer getTypeId () {
			return typeId;
		}

		public void setTypeId (final Integer typeId) {
			this.typeId = typeId;
		}

		public Long getAmount () {
			return amount;
		}

		public void setAmount (final Long amount) {
			this.amount = amount;
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ColonyCoreStructure.class);

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

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ColonyCoreStructure () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Float getLatitude () {
		return latitude;
	}public Float getLongitude () {
		return longitude;
	}public Long getPinId () {
		return pinId;
	}public Integer getTypeId () {
		return typeId;
	}public Integer getSchematicId () {
		return schematicId;
	}public DateTime getInstallTime () {
		return installTime;
	}public DateTime getExpiryTime () {
		return expiryTime;
	}public DateTime getLastCycleStart () {
		return lastCycleStart;
	}

	public void setLatitude (final Float latitude) {
		this.latitude = latitude;
	}

	public void setLongitude (final Float longitude) {
		this.longitude = longitude;
	}

	public void setPinId (final Long pinId) {
		this.pinId = pinId;
	}

	public void setTypeId (final Integer typeId) {
		this.typeId = typeId;
	}

	public void setSchematicId (final Integer schematicId) {
		this.schematicId = schematicId;
	}

	public void setExtractorDetails (final ColonyExtractor extractorDetails) {
		this.extractorDetails = extractorDetails;
	}

	public void setFactoryDetails (final ColonyFactoryDetail factoryDetails) {
		this.factoryDetails = factoryDetails;
	}

	public void setContents (final List<ColonyContent> contents) {
		this.contents = contents;
	}

	public void setInstallTime (final DateTime installTime) {
		this.installTime = installTime;
	}

	public void setExpiryTime (final DateTime expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setLastCycleStart (final DateTime lastCycleStart) {
		this.lastCycleStart = lastCycleStart;
	}

	private EPlanetaryStructureType structureType = EPlanetaryStructureType.DEFAULT;

	public EPlanetaryStructureType getStructureTypeCode() {
		if ( structureType == EPlanetaryStructureType.DEFAULT ) structureType=calculateStructureTypeCode();
		return structureType;
	}

	public EPlanetaryStructureType calculateStructureTypeCode() {
		// Barren structures
		if ( getTypeId() == 2524 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( getTypeId() == 2544 ) return EPlanetaryStructureType.LAUNCHPAD;
		if ( getTypeId() == 2541 ) return EPlanetaryStructureType.STORAGE;
		if ( getTypeId() == 2848 ) return EPlanetaryStructureType.EXTRACTOR;
		if ( getTypeId() == 2473 ) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if ( getTypeId() == 2474 ) return EPlanetaryStructureType.ADVANCED_INDUSTRY;
		if ( getTypeId() == 2475 ) return EPlanetaryStructureType.HIGH_TECH_PRODUCTION;

		// Temperate structures
		if ( getTypeId() == 2254 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( getTypeId() == 2256 ) return EPlanetaryStructureType.LAUNCHPAD;
		if ( getTypeId() == 2562 ) return EPlanetaryStructureType.STORAGE;
		if ( getTypeId() == 3068 ) return EPlanetaryStructureType.EXTRACTOR;
		if ( getTypeId() == 2481 ) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if ( getTypeId() == 2480 ) return EPlanetaryStructureType.ADVANCED_INDUSTRY;
		if ( getTypeId() == 2482 ) return EPlanetaryStructureType.HIGH_TECH_PRODUCTION;

		// Lava structures
		if ( getTypeId() == 2549 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( getTypeId() == 2555 ) return EPlanetaryStructureType.LAUNCHPAD;
		if ( getTypeId() == 2558 ) return EPlanetaryStructureType.STORAGE;
		if ( getTypeId() == 3062 ) return EPlanetaryStructureType.EXTRACTOR;
		if ( getTypeId() == 2469 ) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if ( getTypeId() == 2470 ) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Plasma structures
		if ( getTypeId() == 2472 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( getTypeId() == 2556 ) return EPlanetaryStructureType.LAUNCHPAD;
		if ( getTypeId() == 2560 ) return EPlanetaryStructureType.STORAGE;
		if ( getTypeId() == 3064 ) return EPlanetaryStructureType.EXTRACTOR;
		if ( getTypeId() == 2471 ) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if ( getTypeId() == 2472 ) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		// Gas structures
		if ( getTypeId() == 2534 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( getTypeId() == 2543 ) return EPlanetaryStructureType.LAUNCHPAD;
		if ( getTypeId() == 2536 ) return EPlanetaryStructureType.STORAGE;
		if ( getTypeId() == 3060 ) return EPlanetaryStructureType.EXTRACTOR;
		if ( getTypeId() == 2492 ) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if ( getTypeId() == 2494 ) return EPlanetaryStructureType.ADVANCED_INDUSTRY;

		return EPlanetaryStructureType.DEFAULT;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ColonyCoreStructure [");
		buffer.append("name: ").append(0);
		buffer.append("]");
		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
