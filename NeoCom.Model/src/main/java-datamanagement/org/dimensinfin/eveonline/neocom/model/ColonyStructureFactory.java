//  PROJECT:     NeoCom.DataManagement(NEOC.DM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java JRE 1.5 Specification.
//  DESCRIPTION: NeoCom pure Java library to maintain and manage all the data streams and
//                 connections. It will use the Models as the building blocks for the data
//                 and will isolate to the most the code from any platform implementation.
//               It will contain the Model Generators and use the external facilities for
//                 network connections to CCP XML api, CCP ESI api and Database storage. It
//                 will also make use of Cache facilities that will be glued at compilation
//                 time depending on destination platform.
package org.dimensinfin.eveonline.neocom.model;

import com.tlabs.eve.api.character.PlanetaryPin;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

//- CLASS IMPLEMENTATION ...................................................................................
public class ColonyStructureFactory {
	public enum EPlanetaryStructureType {
		COMMAND_CENTER, EXTRACTOR, BASIC_INDUSTRY, ADVANCED_INDUCTRY, HIGH_TECH_PRODUCTION, STORAGE, LAUNCHPAD, DEFAULT
	}

	//- CLASS IMPLEMENTATION ...................................................................................
	public static class ColonyStructureXML implements ColonyStructure {
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private String jsonClass = "ColonyStructureXML";
		private PlanetaryPin _xmldelegate = null;
		private EPlanetaryStructureType _contentType;

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ColonyStructureXML () {
			jsonClass = "ColonyStructureXML";
		}

		@Deprecated
		public ColonyStructureXML (final PlanetaryPin target) {
			this();
			_xmldelegate = target;
		}

		public ColonyStructureXML (final PlanetaryPin target, final EPlanetaryStructureType type) {
			this();
			_contentType = type;
			_xmldelegate = target;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		@Override
		public List<ICollaboration> collaborate2Model (final String variation) {
			return new Vector();
		}

		@Deprecated
		public PlanetaryPin getXMLDelegate () {
			return _xmldelegate;
		}

		public ColonyStructure setXMLDelegate (final PlanetaryPin structure) {
			_xmldelegate = structure;
			return this;
		}

		public ColonyStructure setStructureType (final EPlanetaryStructureType type) {
			_contentType = type;
			return this;
		}

		public EPlanetaryStructureType getStructureType () {
			return _contentType;
		}

		public String getPlanetName () {
			return _xmldelegate.getPlanetName();
		}

		public long getTypeID () {
			return _xmldelegate.getTypeID();
		}

		public String getTypeName () {
			return _xmldelegate.getTypeName();
		}

		public long getSchematicID () {
			return _xmldelegate.getSchematicID();
		}

		public long getLastLaunchTime () {
			return _xmldelegate.getLastLaunchTime();
		}

		public long getCycleTime () {
			return _xmldelegate.getCycleTime();
		}

		public long getInstallTime () {
			return _xmldelegate.getInstallTime();
		}

		public long getExpiryTime () {
			return _xmldelegate.getExpiryTime();
		}

		@Override
		public String getJsonClass () {
			return jsonClass;
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ColonyStructureXML.class);

	/**
	 * Create the structure adapter and wrapper from the XML instantiated object.
	 *
	 * @param structure eve-api xml instance rom the XML CCP api call.
	 * @return MVC wrapper for the core data.
	 */
	public static ColonyStructure from (PlanetaryPin structure) {
		logger.info(">> [ColonyStructure.from]");
		// Detect the type of structure previously tp create the item.
		final EPlanetaryStructureType structureType = filterStructure(structure);
		logger.debug(">> [ColonyStructure.from]> structureType: ", structureType);
		final ColonyStructure wrapper = new ColonyStructureXML(structure, structureType);
		logger.info("<< [ColonyStructure.from]");
		return wrapper;
	}

	private static EPlanetaryStructureType filterStructure (PlanetaryPin structure) {
		if ( structure.getTypeID() == 2524 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( structure.getTypeID() == 2525 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( structure.getTypeID() == 2526 ) return EPlanetaryStructureType.COMMAND_CENTER;
		if ( structure.getTypeID() == 2541 ) return EPlanetaryStructureType.STORAGE;
		if ( structure.getTypeID() == 2544 ) return EPlanetaryStructureType.LAUNCHPAD;
		if ( structure.getTypeID() == 2848 ) return EPlanetaryStructureType.EXTRACTOR;
		if ( structure.getTypeID() == 2473 ) return EPlanetaryStructureType.BASIC_INDUSTRY;
		if ( structure.getTypeID() == 2474 ) return EPlanetaryStructureType.ADVANCED_INDUCTRY;
		return EPlanetaryStructureType.DEFAULT;
	}

	//	/**
	//	 * Create the structure adapter and wrapper but this time from the ESI instantionated Json deserailization.
	//	 *
	//	 * @param structure ESI json deserialized CCP data.
	//	 * @return MVC wrapper for the core data.
	//	 */
	//	public static ColonyStructure from (GetCharactersCharacterIdPlanetsPlanetIdOkPins structure) {
	//		final ColonyStructure wrapper = new ColonyStructure();
	//		wrapper.setESIDelegate(structure);
	//		return wrapper;
	//	}

	// - F I E L D - S E C T I O N ............................................................................
	// - C O N S T R U C T O R - S E C T I O N ................................................................
	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
