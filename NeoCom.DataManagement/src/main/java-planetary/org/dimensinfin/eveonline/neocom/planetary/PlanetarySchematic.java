package org.dimensinfin.eveonline.neocom.planetary;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;

/**
 * Defines all the data related to a planetary transformation schematic. On creation generates and accesses the resource for the inputs and outputs.
 * Adds method to operate with the schematic.
 */
public class PlanetarySchematic {
	private ISDEDatabaseAdapter sdeDatabaseAdapter;

	private int schematicId;
	private int cycleTime = 3600;
	private String schematicName = "N/A";

	public PlanetarySchematic( final Integer schematicId ) {
		this.schematicId = schematicId;
	}

	// - B U I L D E R
	public static class Builder {
		private PlanetarySchematic onConstruction;

		public Builder( final Integer schematicId ) {
			this.onConstruction = new PlanetarySchematic(schematicId);
		}

		public Builder withSDEDatabaseAdapter( final ISDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}

		public PlanetarySchematic build() {
			return this.onConstruction;
		}
	}
}
