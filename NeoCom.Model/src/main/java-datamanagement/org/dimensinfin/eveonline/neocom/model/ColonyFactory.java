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

import com.tlabs.eve.api.character.PlanetaryColony;
import com.tlabs.eve.api.character.PlanetaryPin;

import org.dimensinfin.core.interfaces.ICollaboration;

import java.util.List;
import java.util.Vector;

//- CLASS IMPLEMENTATION ...................................................................................
public class ColonyFactory {
	//- CLASS IMPLEMENTATION ...................................................................................
	public static class ColonyXML extends NeoComDownloadableNode implements Colony {
		// - S T A T I C - S E C T I O N ..........................................................................

		// - F I E L D - S E C T I O N ............................................................................
		private String jsonClass = "ColonyStructureXML";
		private PlanetaryColony _delegate = null;
		private List<ColonyStructure> colonyStuctures = new Vector<ColonyStructure>();

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public ColonyXML () {
			jsonClass = "ColonyXML";
		}

		public ColonyXML (final PlanetaryColony target) {
			this();
			_delegate = target;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public void addStructure (ColonyStructure structure) {
			if ( null != structure ) colonyStuctures.add(structure);
		}

		/**
		 * Next implementation will return the Colony Pins.
		 */
		@Override
		public List<ICollaboration> collaborate2Model (final String variation) {
			List<ICollaboration> result = new Vector();
			for (ColonyStructure structure : colonyStuctures) {
				result.add(structure);
			}
			return result;
		}

		public Colony setDelegate (final PlanetaryColony structure) {
			_delegate = structure;
			return this;
		}

		public boolean isEmpty () {
			if ( colonyStuctures.size() > 0 ) return false;
			else return true;
		}

		public int getStructureCount () {
			return colonyStuctures.size();
		}

		@Deprecated
		public PlanetaryColony getDelegate () {
			return _delegate;
		}

		public long getPlanetID () {
			return _delegate.getPlanetID();
		}

		public String getPlanetName () {
			return _delegate.getPlanetName();
		}

		public String getSolarSystemName () {
			return _delegate.getSolarSystemName();
		}

		public long getPlanetTypeID () {
			return _delegate.getPlanetTypeID();
		}

		public String getPlanetTypeName () {
			return _delegate.getPlanetTypeName();
		}

		public int getNumberOfPins () {
			return _delegate.getNumberOfPins();
		}

		public List<PlanetaryPin> getPins () {
			return _delegate.getPins();
		}

		@Override
		public String getJsonClass () {
			return jsonClass;
		}
		public String toString () {
			StringBuffer buffer = new StringBuffer("ColonyXML [");
			buffer.append("id: ").append(System.identityHashCode(this)).append(" ");
			buffer.append("]");
			//		buffer.append("->").append(super.toString());
			return buffer.toString();
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	public static Colony from (PlanetaryColony colony) {
		final Colony wrapper = new ColonyXML(colony);
		//		wrapper.setDelegate(colony);
		return wrapper;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
