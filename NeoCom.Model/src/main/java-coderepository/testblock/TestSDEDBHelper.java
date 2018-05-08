//  PROJECT:     Neocom.Microservices (NEOC-MS)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2017-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 / SpringBoot-1.3.5 / Angular 5.0
//  DESCRIPTION: This is the SpringBoot MicroServices module to run the backend services to complete the web
//               application based on Angular+SB. This is the web version for the NeoCom Android native
//               application. Most of the source code is common to both platforms and this module includes
//               the source for the specific functionality for the backend services.
package org.dimensinfin.eveonline.neocom.utilityblock.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.ISDEDBHelper;
import org.dimensinfin.eveonline.neocom.database.SDEDatabaseManager;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class TestSDEDBHelper extends SDEDatabaseManager implements ISDEDBHelper {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(TestSDEDBHelper.class);

	// - F I E L D - S E C T I O N ............................................................................
	private String schema = "jdbc:sqlite";
	private String databasePath = "src/main/resources/";
	private String databaseName = "sde.sqlite";
	private int databaseVersion = 0;
	private boolean databaseValid = false;
	private boolean isOpen = false;
	private Connection connectionSource = null;

	private final Hashtable<Integer, EveItem> itemCache = new Hashtable<Integer, EveItem>(1000);
	private final Hashtable<Long, EveLocation> locationsCache = new Hashtable<Long, EveLocation>(200);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public TestSDEDBHelper() {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ISDEDBHelper setDatabaseSchema( final String newschema ) {
		this.schema = newschema;
		return this;
	}

	public ISDEDBHelper setDatabasePath( final String newpath ) {
		this.databasePath = newpath;
		return this;
	}

	public ISDEDBHelper setDatabaseName( final String instanceName ) {
		this.databaseName = instanceName;
		return this;
	}

	public ISDEDBHelper setDatabaseVersion( final int newVersion ) {
		this.databaseVersion = newVersion;
		return this;
	}

	public ISDEDBHelper build() throws SQLException {
		if (StringUtils.isEmpty(schema))
			throw new SQLException("Cannot create connection: 'schema' is empty.");
		if (StringUtils.isEmpty(databasePath))
			throw new SQLException("Cannot create connection: 'databasePath' is empty.");
		if (StringUtils.isEmpty(databaseName))
			throw new SQLException("Cannot create connection: 'databaseName' is empty.");
		databaseValid = true;
		openSDEDB();
		return this;
	}

	public String getConnectionDescriptor(){
		return schema + ":" + databasePath + databaseName;
	}
	public boolean databaseIsValid(){
		if(isOpen)
			if(databaseValid)
				if(null!=connectionSource)return true;
		return false;
	}
	/**
	 * Open a new pooled JDBC datasource connection list and stores its reference for use of the whole set of
	 * services. Being a pooled connection it can create as many connections as required to do requests in
	 * parallel to the database instance. This only is effective for MySql databases.
	 *
	 * @return
	 */
	private boolean openSDEDB() {
		logger.info(">> [TestSDEDBHelper.openSDEDB]");
		if (!isOpen) if (null == connectionSource) {
			// Open and configure the connection datasource for hand written SQL queries.
			try {
//				final String localConnectionDescriptor = schema + ":" + databasePath + databaseName;
				createConnectionSource();
				logger.info("-- [TestSDEDBHelper.openSDEDB]> Opened database {} successfully with version {}.",getConnectionDescriptor(),
						databaseVersion);
				isOpen = true;
			} catch (Exception sqle) {
				logger.error("E> [TestSDEDBHelper.openSDEDB]> " + sqle.getClass().getName() + ": " + sqle.getMessage());
			}
		}
		logger.info("<< [TestSDEDBHelper.openSDEDB]");
		return isOpen;
	}

	private void createConnectionSource() throws SQLException {
//		final String localConnectionDescriptor = schema + ":" + databasePath + databaseName;
		if (databaseValid) {
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException cnfe) {
				throw new SQLException("Cannot create connection. {}.", cnfe.getMessage());
			}
			connectionSource = DriverManager.getConnection(getConnectionDescriptor());
			connectionSource.setAutoCommit(false);
		} else throw new SQLException("Cannot create connection, database validation not passed.");
	}

	private Connection getSDEConnection() throws SQLException {
		if (null != connectionSource) return connectionSource;
		else throw new SQLException("Cannot create connection, database validation not passed.");
	}

	/**
	 * This is the specific SpringBoot implementation for the SDE database adaptation. We can create compatible
	 * <code>RawStatements</code> that can isolate the generic database access code from the platform specific. This stetement
	 * uses the database connection to create a generic JDBC Java statement.
	 *
	 * @param query
	 * @param parameters
	 * @return
	 */
	protected SBRawStatement constructStatement( final String query, final String[] parameters ) throws SQLException {
		return new SBRawStatement(getSDEConnection(), query, parameters);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("NeoComSBDBHelper [");
		final String localConnectionDescriptor = schema + ":" + databasePath + databaseName;
		buffer.append("Descriptor: ").append(localConnectionDescriptor);
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}

	public static class SBRawStatement extends RawStatement {
		private PreparedStatement prepStmt = null;
		private ResultSet cursor = null;

		public SBRawStatement( final Connection privateConnection, final String query, final String[] parameters ) throws
				SQLException {
			if (null != privateConnection) {
				prepStmt =privateConnection.prepareStatement(query);
				for (int i = 0; i < parameters.length; i++) {
					prepStmt.setString(i + 1, parameters[i]);
				}
				cursor = prepStmt.executeQuery();
				if (null == cursor) throw new SQLException("Invalid statement when processing query: " + query);
			} else throw new SQLException("No valid connection to database to create statement. {}", query);
		}

//		@Override
//		public int getCount() {
//			return 1;
//		}
//
//		@Override
//		public int getPosition() {
//			return 1;
//		}

		@Override
		public boolean moveToFirst() {
			try {
				return cursor.first();
			} catch (SQLException sqle) {
				return false;
			}
		}

		@Override
		public boolean moveToLast() {
			try {
				return cursor.last();
			} catch (SQLException sqle) {
				return false;
			}
		}

		@Override
		public boolean moveToNext() {
			try {
				return cursor.next();
			} catch (SQLException sqle) {
				return false;
			}
		}

		@Override
		public boolean isFirst() {
			try {
				return cursor.isFirst();
			} catch (SQLException sqle) {
				return false;
			}
		}

		@Override
		public boolean isLast() {
			try {
				return cursor.isLast();
			} catch (SQLException sqle) {
				return false;
			}
		}

//		@Override
//		public int getColumnIndex( String columnName ) {
//			return prepStmt.getMetaData().get
//		}

		@Override
		public String getString( int colindex ) {
			try {
				return cursor.getString(colindex);
			} catch (SQLException sqle) {
				return "";
			}
		}

		@Override
		public short getShort( int colindex ) {
			try {
				return cursor.getShort(colindex);
			} catch (SQLException sqle) {
				return 0;
			}
		}

		@Override
		public int getInt( int colindex ) {
			try {
				return cursor.getInt(colindex);
			} catch (SQLException sqle) {
				return 0;
			}
		}

		@Override
		public long getLong( int colindex ) {
			try {
				return cursor.getLong(colindex);
			} catch (SQLException sqle) {
				return 0;
			}
		}

		@Override
		public float getFloat( int colindex ) {
			try {
				return cursor.getFloat(colindex);
			} catch (SQLException sqle) {
				return 0;
			}
		}

		@Override
		public double getDouble( int colindex ) {
			try {
				return cursor.getDouble(colindex);
			} catch (SQLException sqle) {
				return 0;
			}
		}

//		@Override
//		public int getType( int colindex ) {
//			return cursor.getType();
//		}

		public void close() {
			try {
				if (null != cursor) cursor.close();
				if (null != prepStmt) prepStmt.close();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
}

// - UNUSED CODE ............................................................................................
//[01]
