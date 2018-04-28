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
package org.dimensinfin.eveonline.neocom.model;

import java.sql.SQLException;

import net.nikr.eve.jeveasset.data.Citadel;

import com.beimin.eveapi.model.eve.Station;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.dimensinfin.eveonline.neocom.core.NeocomRuntimeException;
import org.dimensinfin.eveonline.neocom.enums.ELocationType;

// - CLASS IMPLEMENTATION ...................................................................................

/**
 * This class encapsulates the concept of Eve Location. There are different types of locations and the new CCP
 * release is adding even more. The first concept is Region. This is when the constellation and system are not
 * defined. The second level is constellation, then system and inside a system we can find some more elements. <br>
 * Once we have the system id we have to check if the Location is a point in space, a NPC station (in the CCP
 * database catalog), a corporation Outpost (in the list of outposts) or it can be a Player Structure. Now we
 * have to differentiate the player structures and also this can change because POS are going to be replaced
 * by Citadels. I have a reference to get the list of Citadels until that API entry point is available on the
 * new CCP api. <br>
 * Once we know the type then we check if on the database cache and add or update as needed.
 *
 * @author Adam Antinoo
 */
@DatabaseTable(tableName = "Locations")
public class EveLocation extends NeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 1522765618286937377L;

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	protected long id = -2;
	@DatabaseField
	protected long stationId = -1;
	@DatabaseField
	private String station = "SPACE";
	@DatabaseField
	protected long systemId = -1;
	@DatabaseField
	private String system = "UNKNOWN";
	@DatabaseField
	protected long constellationId = -1;
	@DatabaseField
	private String constellation = "Echo Cluster";
	@DatabaseField
	protected long regionId = -1;
	@DatabaseField
	private String region = "-DEEP SPACE-";
	@DatabaseField
	private String security = "0.0";
	@DatabaseField
	protected String typeId = ELocationType.UNKNOWN.name();
	public String urlLocationIcon = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveLocation() {
		super();
		jsonClass = "EveLocation";
	}

	public EveLocation( final long locationID ) {
		this();
		id = locationID;
		stationId = locationID;
	}

	public EveLocation( final long citadelid, final Citadel cit ) {
		this();
		try {
			final Dao<EveLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
			// calculate the ocationID from the sure item and update the rest of the fields.
			this.updateFromCitadel(citadelid, cit);
			id = citadelid;
			typeId = ELocationType.CITADEL.name();
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.store();
		}
	}

//	/**
//	 * Create a location from an Outpost read on the current list of player outposts.
//	 *
//	 * @param out
//	 */
//	public EveLocation( final Outpost out ) {
//		this();
//		try {
//			final Dao<EveLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
//			// Calculate the locationID from the source item and update the rest of the fields.
//			this.updateFromSystem(out.getSolarSystem());
//			id = out.getFacilityID();
//			typeId = ELocationType.OUTPOST.name();
//			this.setStation(out.getName());
//			// Try to create the pair. It fails then  it was already created.
//			locationDao.createOrUpdate(this);
//		} catch (final SQLException sqle) {
//			sqle.printStackTrace();
//			this.store();
//		} catch (NeoComException neoe) {
//		}
//	}

	public EveLocation( final Station station ) {
		this();
		try {
			final Dao<EveLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
			// Calculate the locationID from the source item and update the rest of the fields.
			this.updateFromSystem(station.getSolarSystemID());
			id = station.getStationID();
			typeId = ELocationType.OUTPOST.name();
			this.setStation(station.getStationName());
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean equals( final EveLocation obj ) {
		if (!this.getRegion().equalsIgnoreCase(obj.getRegion())) return false;
		if (!this.getSystem().equalsIgnoreCase(obj.getSystem())) return false;
		if (!this.getStation().equalsIgnoreCase(obj.getStation())) return false;
		return true;
	}

	public String getConstellation() {
		return constellation;
	}

	public long getConstellationId() {
		return constellationId;
	}

	public String getFullLocation() {
		return "[" + security + "] " + station + " - " + region + " > " + system;
	}

	public long getID() {
		return Math.max(Math.max(Math.max(stationId, systemId), constellationId), regionId);
	}

	/**
	 * This return some understandable location name. This is not valid for most locations that are not
	 * stations.
	 *
	 * @return
	 */
	public String getName() {
		return system + " - " + station;
	}

	public long getRealId() {
		return id;
	}

	public String getRegion() {
		return region;
	}

	public long getRegionId() {
		return regionId;
	}

	public String getSecurity() {
		return security;
	}

	public double getSecurityValue() {
		try {
			return Double.parseDouble(security);
		} catch (final RuntimeException rtex) {
		}
		return 0.0;
	}

	public String getStation() {
		return station;
	}

	public long getStationId() {
		return stationId;
	}

	public String getSystem() {
		return system;
	}

	public long getSystemId() {
		return systemId;
	}

	public ELocationType getTypeId() {
		return ELocationType.valueOf(typeId);
	}

	/**
	 * Downloads and caches the item icon from the CCP server. The new implementation check for special cases
	 * such as locations. Stations on locations have an image that can be downloaded from the same place.
	 */
	public String getUrlLocationIcon() {
		if (null == urlLocationIcon) {
			if (id == -2) {
				urlLocationIcon = new StringBuffer()
						.append("http://image.eveonline.com/Render/")
						.append(id)
						.append("_64.png")
						.toString();
			} else
				try {
					urlLocationIcon = new StringBuffer()
							.append("http://image.eveonline.com/Render/")
							.append(accessGlobal().searchStationType(id))
							.append("_64.png")
							.toString();
				} catch (NeocomRuntimeException neoe) {
					urlLocationIcon = new StringBuffer()
							.append("http://image.eveonline.com/Render/")
							.append(id)
							.append("_64.png")
							.toString();
				}
		}
		return urlLocationIcon;
	}

	public final boolean isCitadel() {
		if (this.getTypeId() == ELocationType.CITADEL) return true;
		return false;
	}

	public final boolean isRegion() {
		return ((this.getStationId() == 0) && (this.getSystemId() == 0) && (this.getRegionId() != 0));
	}

	public final boolean isStation() {
		return ((this.getStationId() != 0) && (this.getSystemId() != 0) && (this.getRegionId() != 0));
	}

	public final boolean isSystem() {
		return ((this.getStationId() == 0) && (this.getSystemId() != 0) && (this.getRegionId() != 0));
	}

	public final boolean isUnknown() {
		return (id == -2);
	}

	public void setConstellation( final String constellation ) {
		this.constellation = constellation;
	}

	public void setConstellationId( final long constellationId ) {
		this.constellationId = constellationId;
	}

	public EveLocation store() {
		try {
			final Dao<EveLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}

	public void setId( final long newid ) {
		id = newid;
	}

	public void setLocationID( final long stationID ) {
		this.stationId = stationID;
	}

	public void setRegion( final String region ) {
		this.region = region;
	}

	public void setRegionId( final long regionId ) {
		this.regionId = regionId;
	}

	public void setSecurity( final String security ) {
		this.security = security;
	}

	public void setStation( final String station ) {
		this.station = station;
	}

	public void setStationId( final long stationId ) {
		this.stationId = stationId;
	}

	public void setSystem( final String system ) {
		this.system = system;
	}

	public void setSystemId( final long systemId ) {
		this.systemId = systemId;
	}

	public void setTypeId( final ELocationType typeId ) {
		this.typeId = typeId.name();
	}

	public void setUrlLocationIcon( final String urlLocationIcon ) {
		this.urlLocationIcon = urlLocationIcon;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComLocation [");
		buffer.append("#").append(this.getID()).append(" ");
		//		buffer.append("(").append(this.getContents(false).size()).append(") ");
		buffer.append("[").append(this.getRegion()).append("] ");
		if (null != system) {
			buffer.append("system: ").append(system).append(" ");
		}
		if (null != station) {
			buffer.append("station: ").append(station).append(" ");
		}
		buffer.append("]");
		return buffer.toString();
	}

	private void updateFromCitadel( final long newid, final Citadel cit ) {
		this.updateFromSystem(cit.systemId);
		// Copy the data from the citadel location.
		stationId = newid;
		station = cit.name;
		systemId = cit.systemId;
		//		citadel = true;
	}

	private void updateFromSystem( final long newid ) {
		// Get the system information from the CCP location tables.
		EveLocation systemLocation;
		try {
			systemLocation = accessGlobal().searchLocation4Id(newid);
		} catch (NeocomRuntimeException newe) {
			systemLocation = new EveLocation();
		}
		systemId = systemLocation.getSystemId();
		system = systemLocation.getSystem();
		constellationId = systemLocation.getConstellationId();
		constellation = systemLocation.getConstellation();
		regionId = systemLocation.getRegionId();
		region = systemLocation.getRegion();
		security = systemLocation.getSecurity();
	}

	/**
	 * Two Locations are equal if they have the same locations codes.
	 *
	 * @param obj the target EveLocation to compare.
	 * @return
	 */
	@Override
	public boolean equals( final Object obj ) {
		if (stationId != ((EveLocation) obj).getStationId()) return false;
		if (systemId != ((EveLocation) obj).getSystemId()) return false;
		if (constellationId != ((EveLocation) obj).getConstellationId()) return false;
		if (regionId != ((EveLocation) obj).getRegionId()) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}

// - UNUSED CODE ............................................................................................
