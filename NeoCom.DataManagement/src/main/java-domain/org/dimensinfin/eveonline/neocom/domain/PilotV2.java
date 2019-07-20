package org.dimensinfin.eveonline.neocom.domain;

import org.dimensinfin.eveonline.neocom.entities.Property;
import org.dimensinfin.eveonline.neocom.enums.EPropertyTypes;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOkHomeLocation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.model.AllianceV1;
import org.dimensinfin.eveonline.neocom.model.CorporationV1;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Adam Antinoo
 */
public abstract class PilotV2 extends UpdatableNode {
	public int characterId = -1;
	public String name = "-NOT-KNOWN-";
	public long birthday = 0;
	public String gender = "-undefined-";
	public double securityStatus = 0.0;
	public CorporationV1 corporation = null;
	public AllianceV1 alliance = null;
//	public GetUniverseRaces200Ok race = null;
	public GetUniverseBloodlines200Ok bloodline = null;
	public GetUniverseAncestries200Ok ancestry = null;
	public double accountBalance = -1.0;
	public EveLocation lastKnownLocation = null;

	protected GetCharactersCharacterIdOk publicData = null;
	protected int totalAssetsNumber = -1;
	private GetCharactersCharacterIdClonesOk cloneInformation = null;
	private GetCharactersCharacterIdClonesOkHomeLocation homeLocation = null;
	private List<Property> locationRoles = new ArrayList<>();
	private HashMap<Integer, Property> actions4Pilot = new HashMap<>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
//	public PilotV2() {
//		super();
//		//		this.jsonClass = "Pilot";
//	}

	// - G E T T E R S   &   S E T T E R S
	public int getCharacterId() {
		return characterId;
	}

	public PilotV2 setCharacterId( final int characterIdentifier ) {
		this.characterId = characterIdentifier;
		return this;
	}

//	public String getName() {
//		return name;
//	}

	public long getBirthday() {
		return birthday;
	}

//	public String getGender() {
//		return gender;
//	}

	public double getSecurityStatus() {
		return securityStatus;
	}

	public CorporationV1 getCorporation() {
		return corporation;
	}

	public PilotV2 setCorporation( final CorporationV1 corporation ) {
		this.corporation = corporation;
		return this;
	}

	public AllianceV1 getAlliance() {
		return alliance;
	}

	public PilotV2 setAlliance( final AllianceV1 alliance ) {
		this.alliance = alliance;
		return this;
	}

//	public GetUniverseRaces200Ok getRace() {
//		return race;
//	}

//	public PilotV2 setRace( final GetUniverseRaces200Ok race ) {
//		this.race = race;
//		return this;
//	}

	public GetUniverseBloodlines200Ok getBloodline() {
		return bloodline;
	}

	public PilotV2 setBloodline( final GetUniverseBloodlines200Ok bloodline ) {
		this.bloodline = bloodline;
		return this;
	}

	public GetUniverseAncestries200Ok getAncestry() {
		return ancestry;
	}

	public PilotV2 setAncestry( final GetUniverseAncestries200Ok ancestry ) {
		this.ancestry = ancestry;
		return this;
	}

	public double getAccountBalance() {
		return accountBalance;
	}

	public PilotV2 setAccountBalance( final double accountBalance ) {
		this.accountBalance = accountBalance;
		return this;
	}

	public List<Property> getLocationRoles() {
		return locationRoles;
	}

	public HashMap<Integer, Property> getActions4Pilot() {
		return actions4Pilot;
	}

	public String getJsonClass() {
		return "Pilot";
	}

	//--- D E R I V E D   G E T T E R S
//	public EveLocation getLastKnownLocation() {
//		if (null != lastKnownLocation) return lastKnownLocation;
//		else if (null == homeLocation) return new EveLocation();
//		else {
//			try {
//				lastKnownLocation = accessGlobal().searchLocation4Id(homeLocation.getLocationId());
//			} catch (NeoComRuntimeException neoe) {
//				lastKnownLocation = new EveLocation();
//			}
//			return lastKnownLocation;
//		}
//	}
//
//	public long getTotalAssetsNumber() {
//		if (this.totalAssetsNumber < 0) {
//			final List<org.dimensinfin.eveonline.neocom.entities.NeoComAsset> pilotAssets;
//			try {
//				pilotAssets = accessGlobal().getNeocomDBHelper().getAssetDao()
//						              .queryForEq("ownerId", this.characterId);
//				this.totalAssetsNumber = pilotAssets.size();
//			} catch (SQLException sqle) {
//				this.totalAssetsNumber = 0;
//			}
//		}
//		return totalAssetsNumber;
//	}

//	public String getUrlforAvatar() {
//		return "http://image.eveonline.com/character/" + this.getCharacterId() + "_256.jpg";
//	}

	//--- D A T A   T R A N S F O R M A T I O N

	/**
	 * Use the public data to get access to more other Pilot information and to copy relevant data to the public fields. Public
	 * data is not used on the normal instance use but the accessed data blocks from the public identifiers.
	 *
	 * @param publicData ESI data model with all public identifiers.
	 */
	public PilotV2 setPublicData( final GetCharactersCharacterIdOk publicData ) {
		// Keep a local copy of the data.
		this.publicData = publicData;
		// Copy the relative public fields.
		name = publicData.getName();
		birthday = publicData.getBirthday().getMillis();
		gender = publicData.getGender().name().toLowerCase();
		securityStatus = publicData.getSecurityStatus();
		return this;
	}

	/**
	 * From the database list of properties for this Pilot we separate the sets and create the list of Locations Roles and the
	 * list of Actions for Item to be used on Manufacturing activities.
	 */
	public PilotV2 setProperties( final List<Property> properties ) {
		locationRoles.clear();
		actions4Pilot.clear();
		for (Property prop : properties) {
			if (prop.getPropertyType() == EPropertyTypes.MANUFACTUREACTION)
				actions4Pilot.put(Double.valueOf(prop.getNumericValue()).intValue(), prop);
			if (prop.getPropertyType() == EPropertyTypes.LOCATIONROLE)
				locationRoles.add(prop);
		}
		return this;
	}

	public Property addLocationRole( final EveLocation theSelectedLocation, final String locationrole ) {
		Property hit = new Property(EPropertyTypes.LOCATIONROLE)
				               .setOwnerId(getCharacterId())
				               .setTargetId(theSelectedLocation.getId())
				               .setNumericValue(theSelectedLocation.getId())
				               .setStringValue(locationrole)
				               .store();
		locationRoles.add(hit);
		return hit;
	}

//	public void deleteRole( final Property target ) {
//		try {
//			final Dao<Property, String> dao = accessGlobal().getNeocomDBHelper().getPropertyDao();
//			dao.deleteById(Long.valueOf(target.getId()).toString());
//			locationRoles.remove(target);
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//	}

//	/**
//	 * Removes the records that define the association of roles to the selected location. This clears all the
//	 * roles for a location and if the user only wants to clear one he/she has to activate the others again
//	 * since all get removed.
//	 */
//	public void clearLocationRoles( final EveLocation theSelectedLocation ) {
//		//		if (null == locationRoles) accessLocationRoles();
//		for (Property role : locationRoles) {
//			if (role.getNumericValue() == Double.valueOf(theSelectedLocation.getId())) {
//				//		Property hit = locationRoles.get(theSelectedLocation.getID());
//				//		if (null != hit) {
//				try {
//					Dao<Property, String> propertyDao = accessGlobal().getNeocomDBHelper().getPropertyDao();
//					propertyDao.delete(role);
//					locationRoles.remove(role);
//				} catch (final SQLException sqle) {
//					sqle.printStackTrace();
//				}
//			}
//		}
//	}

	public void addAction4Item( final int typeId, final String taskName ) {
		Property hit = actions4Pilot.get(typeId);
		if (null == hit) {
			hit = new Property(EPropertyTypes.MANUFACTUREACTION)
					      .setOwnerId(getCharacterId())
					      .setNumericValue(typeId)
					      .setStringValue(taskName)
					      .store();
			actions4Pilot.put(typeId, hit);
		}
		hit.setStringValue(taskName);
	}


	//-------------------------------------------------------------------------------------------
//	public PilotV2 setHomeLocation( final GetCharactersCharacterIdClonesOkHomeLocation homeLocation ) {
//		this.homeLocation = homeLocation;
//		// Convert this location pointer to a NeoCom location.
//		try {
//			lastKnownLocation = accessGlobal().searchLocation4Id(homeLocation.getLocationId());
//		} catch (NeoComRuntimeException neoe) {
//			lastKnownLocation = new EveLocation();
//		}
//		return this;
//	}


	public PilotV2 setCloneInformation( final GetCharactersCharacterIdClonesOk cloneInformation ) {
		this.cloneInformation = cloneInformation;
		return this;
	}

	// - D E L E G A T E D   M E T H O D S
	public int compareTo( final PilotV2 o ) {
//		if (o.getCharacterId() == getCharacterId()) return 0;
//		else return o.getName().compareTo(getName());
		return 0;
	}

	@Override
	public String toString() {
		return new StringBuffer("PilotV2 [")
				       .append("[#").append(characterId).append("] ")
				       .append("]")
				       //				.append("->").append(super.toString())
				       .toString();
	}
}

// - UNUSED CODE ............................................................................................
//[01]
