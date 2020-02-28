package org.dimensinfin.eveonline.neocom.asset.domain;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersLocationFlag2EsiAssets200OkLocationFlag;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCharactersLocationType2EsiAssets200OkLocationType;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCorporationLocationType2EsiAssets200OkLocationType;
import org.dimensinfin.eveonline.neocom.asset.converter.GetCorporationsLocationFlag2EsiAssets200OkLocationFlag;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdAssets200Ok;

/**
 * This class is used to integrate the two different implementation for the EsiAsset that are generated by the Character or Corporation calls.
 * Because only a few fields of the original Esi instance are used this class only should reflect the used fields.
 */
public class EsiAssets200Ok {
	public enum LocationFlagEnum {
		@SerializedName("AssetSafety")
		ASSETSAFETY( "AssetSafety" ),

		@SerializedName("AutoFit")
		AUTOFIT( "AutoFit" ),

		@SerializedName("Bonus")
		BONUS( "Bonus" ),

		@SerializedName("Booster")
		BOOSTER( "Booster" ),

		@SerializedName("BoosterBay")
		BOOSTERBAY( "BoosterBay" ),

		@SerializedName("Capsule")
		CAPSULE( "Capsule" ),

		@SerializedName("Cargo")
		CARGO( "Cargo" ),

		@SerializedName("CorpseBay")
		CORPSEBAY( "CorpseBay" ),

		@SerializedName("CorpDeliveries")
		CORPDELIVERIES( "CorpDeliveries" ),

		@SerializedName("CorpSAG1")
		CORPSAG1( "CorpSAG1" ),

		@SerializedName("CorpSAG2")
		CORPSAG2( "CorpSAG2" ),

		@SerializedName("CorpSAG3")
		CORPSAG3( "CorpSAG3" ),

		@SerializedName("CorpSAG4")
		CORPSAG4( "CorpSAG4" ),

		@SerializedName("CorpSAG5")
		CORPSAG5( "CorpSAG5" ),

		@SerializedName("CorpSAG6")
		CORPSAG6( "CorpSAG6" ),

		@SerializedName("CorpSAG7")
		CORPSAG7( "CorpSAG7" ),

		@SerializedName("CrateLoot")
		CRATELOOT( "CrateLoot" ),

		@SerializedName("Deliveries")
		DELIVERIES( "Deliveries" ),

		@SerializedName("DroneBay")
		DRONEBAY( "DroneBay" ),

		@SerializedName("DustBattle")
		DUSTBATTLE( "DustBattle" ),

		@SerializedName("DustDatabank")
		DUSTDATABANK( "DustDatabank" ),

		@SerializedName("FighterBay")
		FIGHTERBAY( "FighterBay" ),

		@SerializedName("FighterTube0")
		FIGHTERTUBE0( "FighterTube0" ),

		@SerializedName("FighterTube1")
		FIGHTERTUBE1( "FighterTube1" ),

		@SerializedName("FighterTube2")
		FIGHTERTUBE2( "FighterTube2" ),

		@SerializedName("FighterTube3")
		FIGHTERTUBE3( "FighterTube3" ),

		@SerializedName("FighterTube4")
		FIGHTERTUBE4( "FighterTube4" ),

		@SerializedName("FleetHangar")
		FLEETHANGAR( "FleetHangar" ),

		@SerializedName("Hangar")
		HANGAR( "Hangar" ),

		@SerializedName("HangarAll")
		HANGARALL( "HangarAll" ),

		@SerializedName("HiSlot0")
		HISLOT0( "HiSlot0" ),

		@SerializedName("HiSlot1")
		HISLOT1( "HiSlot1" ),

		@SerializedName("HiSlot2")
		HISLOT2( "HiSlot2" ),

		@SerializedName("HiSlot3")
		HISLOT3( "HiSlot3" ),

		@SerializedName("HiSlot4")
		HISLOT4( "HiSlot4" ),

		@SerializedName("HiSlot5")
		HISLOT5( "HiSlot5" ),

		@SerializedName("HiSlot6")
		HISLOT6( "HiSlot6" ),

		@SerializedName("HiSlot7")
		HISLOT7( "HiSlot7" ),

		@SerializedName("HiddenModifiers")
		HIDDENMODIFIERS( "HiddenModifiers" ),

		@SerializedName("Implant")
		IMPLANT( "Implant" ),

		@SerializedName("Impounded")
		IMPOUNDED( "Impounded" ),

		@SerializedName("JunkyardReprocessed")
		JUNKYARDREPROCESSED( "JunkyardReprocessed" ),

		@SerializedName("JunkyardTrashed")
		JUNKYARDTRASHED( "JunkyardTrashed" ),

		@SerializedName("LoSlot0")
		LOSLOT0( "LoSlot0" ),

		@SerializedName("LoSlot1")
		LOSLOT1( "LoSlot1" ),

		@SerializedName("LoSlot2")
		LOSLOT2( "LoSlot2" ),

		@SerializedName("LoSlot3")
		LOSLOT3( "LoSlot3" ),

		@SerializedName("LoSlot4")
		LOSLOT4( "LoSlot4" ),

		@SerializedName("LoSlot5")
		LOSLOT5( "LoSlot5" ),

		@SerializedName("LoSlot6")
		LOSLOT6( "LoSlot6" ),

		@SerializedName("LoSlot7")
		LOSLOT7( "LoSlot7" ),

		@SerializedName("Locked")
		LOCKED( "Locked" ),

		@SerializedName("MedSlot0")
		MEDSLOT0( "MedSlot0" ),

		@SerializedName("MedSlot1")
		MEDSLOT1( "MedSlot1" ),

		@SerializedName("MedSlot2")
		MEDSLOT2( "MedSlot2" ),

		@SerializedName("MedSlot3")
		MEDSLOT3( "MedSlot3" ),

		@SerializedName("MedSlot4")
		MEDSLOT4( "MedSlot4" ),

		@SerializedName("MedSlot5")
		MEDSLOT5( "MedSlot5" ),

		@SerializedName("MedSlot6")
		MEDSLOT6( "MedSlot6" ),

		@SerializedName("MedSlot7")
		MEDSLOT7( "MedSlot7" ),

		@SerializedName("OfficeFolder")
		OFFICEFOLDER( "OfficeFolder" ),

		@SerializedName("Pilot")
		PILOT( "Pilot" ),

		@SerializedName("PlanetSurface")
		PLANETSURFACE( "PlanetSurface" ),

		@SerializedName("QuafeBay")
		QUAFEBAY( "QuafeBay" ),

		@SerializedName("Reward")
		REWARD( "Reward" ),

		@SerializedName("RigSlot0")
		RIGSLOT0( "RigSlot0" ),

		@SerializedName("RigSlot1")
		RIGSLOT1( "RigSlot1" ),

		@SerializedName("RigSlot2")
		RIGSLOT2( "RigSlot2" ),

		@SerializedName("RigSlot3")
		RIGSLOT3( "RigSlot3" ),

		@SerializedName("RigSlot4")
		RIGSLOT4( "RigSlot4" ),

		@SerializedName("RigSlot5")
		RIGSLOT5( "RigSlot5" ),

		@SerializedName("RigSlot6")
		RIGSLOT6( "RigSlot6" ),

		@SerializedName("RigSlot7")
		RIGSLOT7( "RigSlot7" ),

		@SerializedName("SecondaryStorage")
		SECONDARYSTORAGE( "SecondaryStorage" ),

		@SerializedName("ServiceSlot0")
		SERVICESLOT0( "ServiceSlot0" ),

		@SerializedName("ServiceSlot1")
		SERVICESLOT1( "ServiceSlot1" ),

		@SerializedName("ServiceSlot2")
		SERVICESLOT2( "ServiceSlot2" ),

		@SerializedName("ServiceSlot3")
		SERVICESLOT3( "ServiceSlot3" ),

		@SerializedName("ServiceSlot4")
		SERVICESLOT4( "ServiceSlot4" ),

		@SerializedName("ServiceSlot5")
		SERVICESLOT5( "ServiceSlot5" ),

		@SerializedName("ServiceSlot6")
		SERVICESLOT6( "ServiceSlot6" ),

		@SerializedName("ServiceSlot7")
		SERVICESLOT7( "ServiceSlot7" ),

		@SerializedName("ShipHangar")
		SHIPHANGAR( "ShipHangar" ),

		@SerializedName("ShipOffline")
		SHIPOFFLINE( "ShipOffline" ),

		@SerializedName("Skill")
		SKILL( "Skill" ),

		@SerializedName("SkillInTraining")
		SKILLINTRAINING( "SkillInTraining" ),

		@SerializedName("SpecializedAmmoHold")
		SPECIALIZEDAMMOHOLD( "SpecializedAmmoHold" ),

		@SerializedName("SpecializedCommandCenterHold")
		SPECIALIZEDCOMMANDCENTERHOLD( "SpecializedCommandCenterHold" ),

		@SerializedName("SpecializedFuelBay")
		SPECIALIZEDFUELBAY( "SpecializedFuelBay" ),

		@SerializedName("SpecializedGasHold")
		SPECIALIZEDGASHOLD( "SpecializedGasHold" ),

		@SerializedName("SpecializedIndustrialShipHold")
		SPECIALIZEDINDUSTRIALSHIPHOLD( "SpecializedIndustrialShipHold" ),

		@SerializedName("SpecializedLargeShipHold")
		SPECIALIZEDLARGESHIPHOLD( "SpecializedLargeShipHold" ),

		@SerializedName("SpecializedMaterialBay")
		SPECIALIZEDMATERIALBAY( "SpecializedMaterialBay" ),

		@SerializedName("SpecializedMediumShipHold")
		SPECIALIZEDMEDIUMSHIPHOLD( "SpecializedMediumShipHold" ),

		@SerializedName("SpecializedMineralHold")
		SPECIALIZEDMINERALHOLD( "SpecializedMineralHold" ),

		@SerializedName("SpecializedOreHold")
		SPECIALIZEDOREHOLD( "SpecializedOreHold" ),

		@SerializedName("SpecializedPlanetaryCommoditiesHold")
		SPECIALIZEDPLANETARYCOMMODITIESHOLD( "SpecializedPlanetaryCommoditiesHold" ),

		@SerializedName("SpecializedSalvageHold")
		SPECIALIZEDSALVAGEHOLD( "SpecializedSalvageHold" ),

		@SerializedName("SpecializedShipHold")
		SPECIALIZEDSHIPHOLD( "SpecializedShipHold" ),

		@SerializedName("SpecializedSmallShipHold")
		SPECIALIZEDSMALLSHIPHOLD( "SpecializedSmallShipHold" ),

		@SerializedName("StructureActive")
		STRUCTUREACTIVE( "StructureActive" ),

		@SerializedName("StructureFuel")
		STRUCTUREFUEL( "StructureFuel" ),

		@SerializedName("StructureInactive")
		STRUCTUREINACTIVE( "StructureInactive" ),

		@SerializedName("StructureOffline")
		STRUCTUREOFFLINE( "StructureOffline" ),

		@SerializedName("SubSystemBay")
		SUBSYSTEMBAY( "SubSystemBay" ),

		@SerializedName("SubSystemSlot0")
		SUBSYSTEMSLOT0( "SubSystemSlot0" ),

		@SerializedName("SubSystemSlot1")
		SUBSYSTEMSLOT1( "SubSystemSlot1" ),

		@SerializedName("SubSystemSlot2")
		SUBSYSTEMSLOT2( "SubSystemSlot2" ),

		@SerializedName("SubSystemSlot3")
		SUBSYSTEMSLOT3( "SubSystemSlot3" ),

		@SerializedName("SubSystemSlot4")
		SUBSYSTEMSLOT4( "SubSystemSlot4" ),

		@SerializedName("SubSystemSlot5")
		SUBSYSTEMSLOT5( "SubSystemSlot5" ),

		@SerializedName("SubSystemSlot6")
		SUBSYSTEMSLOT6( "SubSystemSlot6" ),

		@SerializedName("SubSystemSlot7")
		SUBSYSTEMSLOT7( "SubSystemSlot7" ),

		@SerializedName("Unlocked")
		UNLOCKED( "Unlocked" ),

		@SerializedName("Wallet")
		WALLET( "Wallet" ),

		@SerializedName("Wardrobe")
		WARDROBE( "Wardrobe" );

		private String value;

		LocationFlagEnum( String value ) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf( value );
		}
	}

	public enum LocationTypeEnum {
		@SerializedName("station")
		STATION( "station" ),

		@SerializedName("solar_system")
		SOLAR_SYSTEM( "solar_system" ),

		@SerializedName("other")
		OTHER( "other" );

		private String value;

		LocationTypeEnum( String value ) {
			this.value = value;
		}

		@Override
		public String toString() {
			return String.valueOf( value );
		}
	}

	private Integer typeId = null;
	private Long itemId = null;
	private Integer quantity = null;
	private Boolean isBlueprintCopy = null;
	private Boolean isSingleton = null;
	private Long locationId = null;
	private LocationFlagEnum locationFlag = null;
	private LocationTypeEnum locationType = null;

	private EsiAssets200Ok() {}

	public Integer getTypeId() {
		return this.typeId;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public boolean getIsBlueprintCopy() {
		return this.isBlueprintCopy;
	}

	public boolean getIsSingleton() {
		return this.isSingleton;
	}

	public Long getItemId() {
		return this.itemId;
	}

	public long getLocationId() {
		return this.locationId;
	}

	public EsiAssets200Ok.LocationFlagEnum getLocationFlag() {
		return this.locationFlag;
	}

	public LocationTypeEnum getLocationType() {
		return this.locationType;
	}

	// - B U I L D E R
	public static class Builder {
		private EsiAssets200Ok onConstruction;

		public Builder() {
			this.onConstruction = new EsiAssets200Ok();
		}

		public EsiAssets200Ok build() {
			Objects.requireNonNull( this.onConstruction.typeId );
			Objects.requireNonNull( this.onConstruction.itemId );
			Objects.requireNonNull( this.onConstruction.locationId );
			return this.onConstruction;
		}

		public EsiAssets200Ok.Builder withIsBlueprintCopy( final Boolean isBlueprintCopy ) {
			if (null == isBlueprintCopy) this.onConstruction.isBlueprintCopy = false;
			else this.onConstruction.isBlueprintCopy = isBlueprintCopy;
			return this;
		}

		public EsiAssets200Ok.Builder withIsSingleton( final Boolean isSingleton ) {
			if (null == isSingleton) this.onConstruction.isSingleton = false;
			this.onConstruction.isSingleton = isSingleton;
			return this;
		}

		public EsiAssets200Ok.Builder withItemId( final Long itemId ) {
			Objects.requireNonNull( itemId );
			this.onConstruction.itemId = itemId;
			return this;
		}

		public EsiAssets200Ok.Builder withLocationFlag( final GetCorporationsCorporationIdAssets200Ok.LocationFlagEnum locationFlag ) {
			Objects.requireNonNull( locationFlag );
			this.onConstruction.locationFlag = new GetCorporationsLocationFlag2EsiAssets200OkLocationFlag().convert( locationFlag );
			return this;
		}

		public EsiAssets200Ok.Builder withLocationFlag( final GetCharactersCharacterIdAssets200Ok.LocationFlagEnum locationFlag ) {
			Objects.requireNonNull( locationFlag );
			this.onConstruction.locationFlag = new GetCharactersLocationFlag2EsiAssets200OkLocationFlag().convert( locationFlag );
			return this;
		}

		public EsiAssets200Ok.Builder withLocationId( final Long locationId ) {
			Objects.requireNonNull( locationId );
			this.onConstruction.locationId = locationId;
			return this;
		}

		public EsiAssets200Ok.Builder withLocationType( final GetCharactersCharacterIdAssets200Ok.LocationTypeEnum locationType ) {
			Objects.requireNonNull( locationType );
			this.onConstruction.locationType = new GetCharactersLocationType2EsiAssets200OkLocationType().convert( locationType );
			return this;
		}

		public EsiAssets200Ok.Builder withLocationType( final GetCorporationsCorporationIdAssets200Ok.LocationTypeEnum locationType ) {
			Objects.requireNonNull( locationType );
			this.onConstruction.locationType = new GetCorporationLocationType2EsiAssets200OkLocationType().convert( locationType );
			return this;
		}

		public EsiAssets200Ok.Builder withQuantity( final Integer quantity ) {
			Objects.requireNonNull( quantity );
			this.onConstruction.quantity = quantity;
			return this;
		}

		public EsiAssets200Ok.Builder withTypeId( final Integer typeId ) {
			Objects.requireNonNull( typeId );
			this.onConstruction.typeId = typeId;
			return this;
		}
	}
}
