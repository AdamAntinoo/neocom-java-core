package org.dimensinfin.eveonline.neocom.domain;

import com.beimin.eveapi.model.eve.Station;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import net.nikr.eve.jeveasset.data.Citadel;

import org.dimensinfin.eveonline.neocom.enums.ELocationType;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

import java.sql.SQLException;

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
@DatabaseTable(tableName = "locations")
public class EsiLocation extends ANeoComEntity {
	private static final long serialVersionUID = 1522765618286937377L;
	private static final EsiLocation jita = new EsiLocation();

	static {
		jita.setId(60003760)
				.setRegionId(10000002).setRegion("The Forge")
				.setConstellationId(20000020).setConstellation("Kimotoro")
				.setSystemId(30000142).setSystem("Jita")
				.setStationId(60003760).setStation("Jita IV - Moon 4 - Caldari Navy Assembly Plant")
				.setSecurity("0.945913116664839").setSecurityValue(0.945913116664839)
				.setTypeId(ELocationType.CCPLOCATION)
				.setUrlLocationIcon("http://image.eveonline.com/Render/1529_64.png")
				.setName(jita.getRegion() + " - " + jita.getStation());
	}

	public static EsiLocation getJitaLocation() {
		return jita;
	}

	// - F I E L D - S E C T I O N ............................................................................
	@DatabaseField(id = true, index = true)
	protected long id = -2;
	@DatabaseField
	protected long stationId = -1;
	@DatabaseField
	private String station = "SPACE";
	@DatabaseField
	protected int systemId = -1;
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
	@DatabaseField(dataType = DataType.ENUM_STRING)
	protected ELocationType typeId = ELocationType.UNKNOWN;
	@DatabaseField
	public String urlLocationIcon = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EsiLocation() {
		super();
	}

	public EsiLocation( final long locationId ) {
		this();
		id = locationId;
		stationId = locationId;
	}

	public EsiLocation( final long citadelid, final Citadel cit ) {
		this();
		try {
			final Dao<EsiLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
			// calculate the ocationID from the sure item and update the rest of the fields.
			this.updateFromCitadel(citadelid, cit);
			id = citadelid;
			stationId = citadelid;
			typeId = ELocationType.CITADEL;
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.store();
		}
	}

	public EsiLocation( final Station station ) {
		this();
		try {
			final Dao<EsiLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
			// Calculate the locationID from the source item and update the rest of the fields.
			this.updateFromSystem(station.getSolarSystemID());
			id = station.getStationID();
			stationId = station.getStationID();
			typeId = ELocationType.OUTPOST;
			this.setStation(station.getStationName());
			// Try to create the pair. It fails then  it was already created.
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
			this.store();
		}
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public EsiLocation store() {
		try {
			final Dao<EsiLocation, String> locationDao = accessGlobal().getNeocomDBHelper().getLocationDao();
			locationDao.createOrUpdate(this);
		} catch (final SQLException sqle) {
			sqle.printStackTrace();
		}
		return this;
	}

	// --- G E T T E R S   &   S E T T E R S
	public long getId() {
		long newid = Math.max(Math.max(Math.max(stationId, systemId), constellationId), regionId);
		this.id = newid;
		return newid;
	}

	public EsiLocation setId( final long id ) {
		this.id = id;
		this.stationId = id;
		return this;
	}

	public long getStationId() {
		return this.stationId;
	}

	public EsiLocation setStationId( final long stationId ) {
		this.stationId = stationId;
		return this;
	}

	public String getStation() {
		return this.station;
	}

	public EsiLocation setStation( final String station ) {
		this.station = station;
		return this;
	}

	public int getSystemId() {
		return this.systemId;
	}

	public EsiLocation setSystemId( final int systemId ) {
		this.systemId = systemId;
		return this;
	}

	public String getSystem() {
		return this.system;
	}

	public EsiLocation setSystem( final String system ) {
		this.system = system;
		return this;
	}

	public long getConstellationId() {
		return this.constellationId;
	}

	public EsiLocation setConstellationId( final long constellationId ) {
		this.constellationId = constellationId;
		return this;
	}

	public String getConstellation() {
		return this.constellation;
	}

	public EsiLocation setConstellation( final String constellation ) {
		this.constellation = constellation;
		return this;
	}

	public long getRegionId() {
		return this.regionId;
	}

	public EsiLocation setRegionId( final long regionId ) {
		this.regionId = regionId;
		return this;
	}

	public String getRegion() {
		return this.region;
	}

	public EsiLocation setRegion( final String region ) {
		this.region = region;
		return this;
	}

	public String getSecurity() {
		return this.security;
	}

	public EsiLocation setSecurity( final String security ) {
		this.security = security;
		return this;
	}

	public ELocationType getTypeId() {
		return this.typeId;
	}

	public EsiLocation setTypeId( final ELocationType typeId ) {
		this.typeId = typeId;
		return this;
	}

	/**
	 * Downloads and caches the item icon from the CCP server. The new implementation check for special cases
	 * such as locations. Stations on locations have an image that can be downloaded from the same place.
	 */
	public String getUrlLocationIcon() {
		if (null == urlLocationIcon) {
			//			if (id == -2) {
			//				urlLocationIcon = new StringBuffer()
			//						.append("http://image.eveonline.com/Render/")
			//						.append(id)
			//						.append("_64.png")
			//						.toString();
			//			} else
			try {
				urlLocationIcon = new StringBuffer()
						                  .append("http://image.eveonline.com/Render/")
						                  .append(accessGlobal().searchStationType(stationId))
						                  .append("_64.png")
						                  .toString();
			} catch (NeoComRuntimeException neoe) {
				urlLocationIcon = new StringBuffer()
						                  .append("http://image.eveonline.com/Render/")
						                  .append(stationId)
						                  .append("_64.png")
						                  .toString();
			}
		}
		return urlLocationIcon;
	}

	public EsiLocation setUrlLocationIcon( final String urlLocationIcon ) {
		this.urlLocationIcon = urlLocationIcon;
		return this;
	}

	//--- V I R T U A L   A C C E S S O R S
	@JsonIgnore
	public final boolean isCitadel() {
		if (this.getTypeId() == ELocationType.CITADEL) return true;
		return false;
	}

	@JsonIgnore
	public final boolean isRegion() {
		return ((this.getStationId() == 0) && (this.getSystemId() == 0) && (this.getRegionId() != 0));
	}

	@JsonIgnore
	public final boolean isStation() {
		return ((this.getStationId() != 0) && (this.getSystemId() != 0) && (this.getRegionId() != 0));
	}

	@JsonIgnore
	public final boolean isSystem() {
		return ((this.getStationId() == 0) && (this.getSystemId() != 0) && (this.getRegionId() != 0));
	}

	@JsonIgnore
	public final boolean isUnknown() {
		return (this.getStationId() < 1);
	}

	/**
	 * This return some understandable location name. This is not valid for most locations that are not
	 * stations.
	 */
	public String getName() {
		return system + " - " + station;
	}

	public EsiLocation setName( final String dummy ) {
		return this;
	}

	public double getSecurityValue() {
		try {
			return Double.parseDouble(security);
		} catch (final RuntimeException rtex) {
		}
		return 0.0;
	}

	public EsiLocation setSecurityValue( final double _newvalue ) {
		this.security = Double.valueOf(_newvalue).toString();
		return this;
	}

	//--- N O N   E X P O R T A B L E   F I E L D S
	@JsonIgnore
	public String getFullLocation() {
		return "[" + security + "] " + station + " - " + region + " > " + system;
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("NeoComLocation [");
		buffer.append("#").append(this.getId()).append(" ");
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

	//--- P R I V A T E   F I E L D S
	private void updateFromCitadel( final long newid, final Citadel cit ) {
		this.updateFromSystem(cit.systemId);
		// Copy the data from the citadel location.
		stationId = newid;
		station = cit.name;
		systemId = Long.valueOf(cit.systemId).intValue();
	}

	private void updateFromSystem( final long newid ) {
		// Get the system information from the CCP location tables.
		EsiLocation systemLocation;
//		try {
//			systemLocation = accessGlobal().searchLocation4Id(newid);
//		} catch (NeoComRuntimeException newe) {
			systemLocation = new EsiLocation();
//		}
		systemId = systemLocation.getSystemId();
		system = systemLocation.getSystem();
		constellationId = Long.valueOf(systemLocation.getConstellationId()).intValue();
		constellation = systemLocation.getConstellation();
		regionId = Long.valueOf(systemLocation.getRegionId()).intValue();
		region = systemLocation.getRegion();
		security = systemLocation.getSecurity();
	}

	/**
	 * Two Locations are equal if they have the same locations codes.
	 *
	 * @param obj the target EsiLocation to compare.
	 */
	@Override
	public boolean equals( final Object obj ) {
		if (stationId != ((EsiLocation) obj).getStationId()) return false;
		if (systemId != ((EsiLocation) obj).getSystemId()) return false;
		if (constellationId != ((EsiLocation) obj).getConstellationId()) return false;
		if (regionId != ((EsiLocation) obj).getRegionId()) return false;
		return true;
	}

	public boolean equals( final EsiLocation obj ) {
		if (!this.getRegion().equalsIgnoreCase(obj.getRegion())) return false;
		if (!this.getSystem().equalsIgnoreCase(obj.getSystem())) return false;
		if (!this.getStation().equalsIgnoreCase(obj.getStation())) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}

// - UNUSED CODE ............................................................................................
