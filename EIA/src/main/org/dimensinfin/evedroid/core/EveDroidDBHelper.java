//  PROJECT:        EveDroid
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
import java.sql.SQLException;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.connector.AppConnector;
import org.dimensinfin.evedroid.model.NeoComAsset;
import org.dimensinfin.evedroid.model.NeoComBlueprint;
import org.dimensinfin.evedroid.model.EveLocation;
import org.dimensinfin.evedroid.model.Job;
import org.dimensinfin.evedroid.model.NeoComMarketOrder;
import org.dimensinfin.evedroid.model.Property;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

// - CLASS IMPLEMENTATION ...................................................................................
public class EveDroidDBHelper extends OrmLiteSqliteOpenHelper {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger							logger						= Logger.getLogger("EveDroidDBHelper");
	private static final String				DATABASE_NAME			= AppConnector.getStorageConnector()
			.accessAppStorage(AppConnector.getResourceString(R.string.appdatabasefilename)).getAbsolutePath();
	private static final int					DATABASE_VERSION	= new Integer(
			AppConnector.getResourceString(R.string.databaseversion)).intValue();

	// - F I E L D - S E C T I O N ............................................................................
	private Dao<NeoComAsset, String>				assetDao					= null;
	private Dao<NeoComBlueprint, String>		blueprintDao			= null;
	private Dao<Job, String>					jobDao						= null;
	private Dao<NeoComMarketOrder, String>	marketOrderDao		= null;
	private Dao<Property, String>			propertyDao				= null;
	private Dao<EveLocation, String>	locationDao				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveDroidDBHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		assetDao = null;
		blueprintDao = null;
	}

	public Dao<NeoComAsset, String> getAssetDAO() throws java.sql.SQLException {
		if (null == assetDao) {
			assetDao = DaoManager.createDao(this.getConnectionSource(), NeoComAsset.class);
		}
		return assetDao;
	}

	public Dao<NeoComBlueprint, String> getBlueprintDAO() throws java.sql.SQLException {
		if (null == blueprintDao) {
			blueprintDao = DaoManager.createDao(this.getConnectionSource(), NeoComBlueprint.class);
		}
		return blueprintDao;
	}

	public Dao<Job, String> getJobDAO() throws java.sql.SQLException {
		if (null == jobDao) {
			jobDao = DaoManager.createDao(this.getConnectionSource(), Job.class);
		}
		return jobDao;
	}

	public Dao<EveLocation, String> getLocationDAO() throws java.sql.SQLException {
		if (null == locationDao) {
			locationDao = DaoManager.createDao(this.getConnectionSource(), EveLocation.class);
		}
		return locationDao;
	}

	public Dao<NeoComMarketOrder, String> getMarketOrderDAO() throws java.sql.SQLException {
		if (null == marketOrderDao) {
			marketOrderDao = DaoManager.createDao(this.getConnectionSource(), NeoComMarketOrder.class);
		}
		return marketOrderDao;
	}

	public Dao<Property, String> getPropertyDAO() throws java.sql.SQLException {
		if (null == propertyDao) {
			propertyDao = DaoManager.createDao(this.getConnectionSource(), Property.class);
		}
		return propertyDao;
	}

	@Override
	public void onCreate(final SQLiteDatabase database, final ConnectionSource databaseConnection) {
		try {
			// Now open the DAO connector and create tables if they not exist
			TableUtils.createTableIfNotExists(databaseConnection, NeoComAsset.class);
			TableUtils.createTableIfNotExists(databaseConnection, NeoComBlueprint.class);
			TableUtils.createTableIfNotExists(databaseConnection, Job.class);
			TableUtils.createTableIfNotExists(databaseConnection, NeoComMarketOrder.class);
			TableUtils.createTableIfNotExists(databaseConnection, Property.class);
			TableUtils.createTableIfNotExists(databaseConnection, EveLocation.class);
		} catch (SQLException sqle) {
			logger.severe("E> Error creating the initial table on the app database.");
			sqle.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(final SQLiteDatabase database, final ConnectionSource databaseConnection, final int oldVersion,
			final int newVersion) {
		int i = 1;
		// Execute different actions depending the version.
		if (oldVersion < 4) {
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, NeoComAsset.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, NeoComBlueprint.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, Job.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 6) {
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, NeoComAsset.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, NeoComBlueprint.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, Job.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, Property.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion == 6) {
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, NeoComBlueprint.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
			try {
				// Delete all the CCP data tables to create then again on open.
				TableUtils.dropTable(databaseConnection, Property.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 47) {
			try {
				// Delete all the CCP data tables to create then again on open.
				EVEDroidApp.getSingletonApp().getApplicationContext().deleteDatabase(DATABASE_NAME);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			}
		}
		if (oldVersion < 48) {
			try {
				TableUtils.dropTable(databaseConnection, NeoComAsset.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 49) {
			try {
				TableUtils.dropTable(databaseConnection, NeoComAsset.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 50) {
			try {
				TableUtils.dropTable(databaseConnection, Job.class, true);
				TableUtils.dropTable(databaseConnection, NeoComMarketOrder.class, true);
				TableUtils.dropTable(databaseConnection, Property.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 51) {
			try {
				TableUtils.dropTable(databaseConnection, NeoComBlueprint.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 52) {
			try {
				TableUtils.dropTable(databaseConnection, NeoComBlueprint.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 53) {
			try {
				TableUtils.dropTable(databaseConnection, Property.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 64) {
			try {
				TableUtils.createTableIfNotExists(databaseConnection, EveLocation.class);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		if (oldVersion < 65) {
			try {
				TableUtils.dropTable(databaseConnection, NeoComAsset.class, true);
				TableUtils.dropTable(databaseConnection, EveLocation.class, true);
			} catch (RuntimeException rtex) {
				logger.severe("E> Error dropping table on Database new version.");
				rtex.printStackTrace();
			} catch (SQLException sqle) {
				logger.severe("E> Error dropping table on Database new version.");
				sqle.printStackTrace();
			}
		}
		onCreate(database, databaseConnection);
	}
}
// - UNUSED CODE ............................................................................................
