package org.dimensinfin.eveonline.neocom.database.repositories;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.RawStatement;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;

/**
 * This class will interface with the local repository for SDE data (first option) and possibly on the future for the local persistence repository.
 * The functionality exposed is related to the Planetary Interaction area covering from the schematics decomposition to planet data
 * storage to process optimizations.
 */
public class PlanetaryRepository {
	private static Logger logger = LoggerFactory.getLogger(PlanetaryRepository.class);

	// - C O M P O N E N T S
	private ISDEDatabaseAdapter sdeDatabaseAdapter;

	// - S C H E M A T I C S 4 O U T P U T
	private static int SCHEMATICS4OUTPUT_TYPEID_COLINDEX = 1;
	private static int SCHEMATICS4OUTPUT_QUANTITY_COLINDEX = 2;
	private static int SCHEMATICS4OUTPUT_ISINPUT_COLINDEX = 3;
	private static final String SELECT_SCHEMATICS4OUTPUT = "SELECT pstms.typeId, pstms.quantity, pstms.isInput"
			                                                       + " FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms"
			                                                       + " WHERE  pstmt.typeId = ?"
			                                                       + " AND    pstmt.isInput = 0"
			                                                       + " AND    pstms.schematicID = pstmt.schematicID";

	public List<Schematics> searchSchematics4Output( final int targetId ) {
				logger.info(">< [PlanetaryRepository.searchSchematics4Output]> typeId: {}", targetId);
		List<Schematics> scheList = new Vector<Schematics>();
		try {
			final RawStatement cursor = this.sdeDatabaseAdapter.constructStatement(SELECT_SCHEMATICS4OUTPUT, new String[]{Integer.valueOf(targetId).toString()});
			while (cursor.moveToNext()) {
				scheList.add(new Schematics().addData(cursor.getInt(SCHEMATICS4OUTPUT_TYPEID_COLINDEX),
						cursor.getInt(SCHEMATICS4OUTPUT_QUANTITY_COLINDEX),
						(cursor.getInt(SCHEMATICS4OUTPUT_ISINPUT_COLINDEX) == 1) ? true : false));
			}
			cursor.close();
		} catch (final Exception ex) {
			logger.error("E [PlanetaryRepository.searchSchematics4Output]> Exception processing statement: {}" + ex.getMessage());
		} finally {
			logger.info("<< [PlanetaryRepository.searchSchematics4Output]");
			return scheList;
		}
	}

	// - B U I L D E R
	public static class Builder {
		private PlanetaryRepository onConstruction;

		public Builder() {
			this.onConstruction = new PlanetaryRepository();
		}

		public PlanetaryRepository.Builder withSDEDatabaseAdapter( final ISDEDatabaseAdapter sdeDatabaseAdapter ) {
			this.onConstruction.sdeDatabaseAdapter = sdeDatabaseAdapter;
			return this;
		}

		public PlanetaryRepository build() {
			Objects.requireNonNull(this.onConstruction.sdeDatabaseAdapter);
			return this.onConstruction;
		}
	}
}
