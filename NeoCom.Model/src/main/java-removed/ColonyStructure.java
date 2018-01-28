package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.model.NeoComNode;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ColonyStructure extends NeoComNode {
	public static class ColonyLink{
		private Long sourcePinId = null;
		private Long destinationPinId = null;
		private Integer linkLevel = null;

		public Long getSourcePinId () {
			return sourcePinId;
		}

		public void setSourcePinId (final Long sourcePinId) {
			this.sourcePinId = sourcePinId;
		}

		public Long getDestinationPinId () {
			return destinationPinId;
		}

		public void setDestinationPinId (final Long destinationPinId) {
			this.destinationPinId = destinationPinId;
		}

		public Integer getLinkLevel () {
			return linkLevel;
		}

		public void setLinkLevel (final Integer linkLevel) {
			this.linkLevel = linkLevel;
		}
	}
	public static class ColonyPin{
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

		public Long getPinId () {
			return pinId;
		}

		public void setPinId (final Long pinId) {
			this.pinId = pinId;
		}

		public Integer getTypeId () {
			return typeId;
		}

		public void setTypeId (final Integer typeId) {
			this.typeId = typeId;
		}

		public Integer getSchematicId () {
			return schematicId;
		}

		public void setSchematicId (final Integer schematicId) {
			this.schematicId = schematicId;
		}

		public ColonyExtractor getExtractorDetails () {
			return extractorDetails;
		}

		public void setExtractorDetails (final ColonyExtractor extractorDetails) {
			this.extractorDetails = extractorDetails;
		}

		public ColonyFactoryDetail getFactoryDetails () {
			return factoryDetails;
		}

		public void setFactoryDetails (final ColonyFactoryDetail factoryDetails) {
			this.factoryDetails = factoryDetails;
		}

		public List<ColonyContent> getContents () {
			return contents;
		}

		public void setContents (final List<ColonyContent> contents) {
			this.contents = contents;
		}

		public DateTime getInstallTime () {
			return installTime;
		}

		public void setInstallTime (final DateTime installTime) {
			this.installTime = installTime;
		}

		public DateTime getExpiryTime () {
			return expiryTime;
		}

		public void setExpiryTime (final DateTime expiryTime) {
			this.expiryTime = expiryTime;
		}

		public DateTime getLastCycleStart () {
			return lastCycleStart;
		}

		public void setLastCycleStart (final DateTime lastCycleStart) {
			this.lastCycleStart = lastCycleStart;
		}
	}
	public static class ColonyRoute{
		private Long routeId = null;
		private Long sourcePinId = null;
		private Long destinationPinId = null;
		private Integer contentTypeId = null;
		private Float quantity = null;
		private List<Long> waypoints = new ArrayList<Long>();

		public Long getRouteId () {
			return routeId;
		}

		public void setRouteId (final Long routeId) {
			this.routeId = routeId;
		}

		public Long getSourcePinId () {
			return sourcePinId;
		}

		public void setSourcePinId (final Long sourcePinId) {
			this.sourcePinId = sourcePinId;
		}

		public Long getDestinationPinId () {
			return destinationPinId;
		}

		public void setDestinationPinId (final Long destinationPinId) {
			this.destinationPinId = destinationPinId;
		}

		public Integer getContentTypeId () {
			return contentTypeId;
		}

		public void setContentTypeId (final Integer contentTypeId) {
			this.contentTypeId = contentTypeId;
		}

		public Float getQuantity () {
			return quantity;
		}

		public void setQuantity (final Float quantity) {
			this.quantity = quantity;
		}

		public List<Long> getWaypoints () {
			return waypoints;
		}

		public void setWaypoints (final List<Long> waypoints) {
			this.waypoints = waypoints;
		}
	}
	public static class ColonyContent{
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
	public static class ColonyExtractor{
		private List<ColonyExtractorHead> heads = new ArrayList<ColonyExtractorHead>();
		private Integer productTypeId = null;
		private Integer cycleTime = null;
		private Float headRadius = null;
		private Integer qtyPerCycle = null;

		public List<ColonyExtractorHead> getHeads () {
			return heads;
		}

		public void setHeads (final List<ColonyExtractorHead> heads) {
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
	public static class ColonyExtractorHead{
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
	public static class ColonyFactoryDetail{
		private Integer schematicId = null;

		public Integer getSchematicId () {
			return schematicId;
		}

		public void setSchematicId (final Integer schematicId) {
			this.schematicId = schematicId;
		}
	}
	private List<ColonyLink> links = new ArrayList<ColonyLink>();
	private List<ColonyPin> pins = new ArrayList<ColonyPin>();
	private List<ColonyRoute> routes = new ArrayList<ColonyRoute>();
}
