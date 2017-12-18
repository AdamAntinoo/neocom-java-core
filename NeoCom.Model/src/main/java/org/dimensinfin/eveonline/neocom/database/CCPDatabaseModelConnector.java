//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.database;

// - IMPORT SECTION .........................................................................................
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// - CLASS IMPLEMENTATION ...................................................................................
public class CCPDatabaseModelConnector {
	final private class RawStatement {
		private PreparedStatement	prepStmt	= null;
		private ResultSet					cursor		= null;

		public RawStatement(final String query, final String[] parameters) throws SQLException {
			prepStmt = null;
			//ModelAppConnector.getSingleton().getCCPDBConnector().getCCPDatabase().prepareStatement(query);
			for (int i = 0; i < parameters.length; i++) {
				prepStmt.setString(i + 1, parameters[i]);
			}
			cursor = prepStmt.executeQuery();
			if (null == cursor) throw new SQLException("Invalid statement when processing query: " + query);
		}

		public void close() {
			// TODO Auto-generated method stub

		}

		//		public void a() {
		//			try {
		//				if (null != cursor) {
		//					while (cursor.next()) {
		//						target.setGroupId(cursor.getInt(CCPDatabaseModelConnector.ITEMGROUP_GROUPID_COLINDEX));
		//						target.setCategoryId(cursor.getInt(CCPDatabaseModelConnector.ITEMGROUP_CATEGORYID_COLINDEX));
		//						target.setGroupName(cursor.getString(CCPDatabaseModelConnector.ITEMGROUP_GROUPNAME_COLINDEX));
		//						target.setIconLinkName(cursor.getString(CCPDatabaseModelConnector.ITEMGROUP_ICONLINKNAME_COLINDEX));
		//					}
		//					return target;
		//				}
		//			} catch (final Exception ex) {
		//				CCPDatabaseModelConnector.logger.error("E- [CCPDatabaseModelConnector.searchItemGroup4Id]> Exception: "
		//						+ ex.getMessage());
		//			} finally {
		//				try {
		//					if (null != cursor) {
		//						cursor.close();
		//					}
		//					if (null != prepStmt) {
		//						prepStmt.close();
		//					}
		//
		//				} catch (SQLException ex) {
		//				}
		//			}
		//
		//		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger				logger															= LoggerFactory
																																			.getLogger(CCPDatabaseModelConnector.class);
	private static final String	CCPDATABASE_URL											= "jdbc:sqlite:src/main/resources/eve.db-this reference is platform specific-";

	// - F I E L D   I N D E X   D E F I N I T I O N S
	private static int					ITEMGROUP_GROUPID_COLINDEX					= 1;
	private static int					ITEMGROUP_CATEGORYID_COLINDEX				= 2;
	private static int					ITEMGROUP_GROUPNAME_COLINDEX				= 3;
	private static int					ITEMGROUP_ICONLINKNAME_COLINDEX			= 4;
	private static int					ITEMCATEGORY_CATEGORYID_COLINDEX		= 1;
	private static int					ITEMCATEGORY_CATEGORYNAME_COLINDEX	= 2;
	private static int					ITEMCATEGORY_ICONLINKNAME_COLINDEX	= 3;

	// - S Q L   C O M M A N D S
	private static final String	SELECT_ITEMGROUP										= "SELECT ig.groupID AS groupID"
																																			+ " , ig.categoryID AS categoryID"
																																			+ " , ig.groupName AS groupName"
																																			+ " , ei.iconFile AS iconLinkName"
																																			+ " FROM invGroups ig"
																																			+ " LEFT OUTER JOIN eveIcons ei ON ig.iconID = ei.iconID"
																																			+ " WHERE ig.groupID = ?";
	private static final String	SELECT_ITEMCATEGORY									= "SELECT ic.categoryID AS categoryID"
																																			+ " , ic.categoryName AS categoryName"
																																			+ " , ei.iconFile AS iconLinkName"
																																			+ " FROM invCategories ic"
																																			+ " LEFT OUTER JOIN eveIcons ei ON ic.iconID = ei.iconID"
																																			+ " WHERE ic.categoryID = ?";

	// - F I E L D - S E C T I O N ............................................................................
	private Connection					ccpDatabase													= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	//	public CCPDatabaseModelConnector() {
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public boolean openCCPDataBase() {
		if (null == ccpDatabase) {
			try {
				Class.forName("org.sqlite.JDBC");
				ccpDatabase = DriverManager.getConnection(CCPDatabaseModelConnector.CCPDATABASE_URL);
				ccpDatabase.setAutoCommit(false);
			} catch (Exception sqle) {
				CCPDatabaseModelConnector.logger.warn("W- [CCPDatabaseModelConnector.openCCPDataBase]> "
						+ sqle.getClass().getSimpleName() + ": " + sqle.getMessage());
			}
			CCPDatabaseModelConnector.logger
					.info("-- [CCPDatabaseModelConnector.openCCPDataBase]> Opened CCP database successfully.");
		}
		return true;
	}

	//	private RawStatement newRawStatement (String query, String[] parameters) {
	//		return new RawStatement
	//		final Cursor cursor = this.getCCPDatabase().rawQuery(query, parameters);
	//		if ( null != cursor ) {
	//			final RawStatement statement = new RawStatement(cursor);
	//			return statement;
	//		} else throw new NeocomRuntimeException("Invalid cursor when processing query: "+query);
	//	}

	/**
	 * Search on the eve.db database for the item group information. This new select is used to get access to
	 * the icon information that should be stored to be correlated to the resource list.
	 */
	public ItemGroup searchItemGroup4Id(final int targetGroupId) {
		//		ArrayList<Resource> results = new ArrayList<Resource>();
		ItemGroup target = new ItemGroup();
		RawStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = new RawStatement(CCPDatabaseModelConnector.SELECT_ITEMGROUP, new String[] { Integer.valueOf(
					targetGroupId).toString() });
			//			prepStmt.setString(1, Integer.valueOf(targetGroupId).toString());
			//			cursor = prepStmt.executeQuery();
			if (null != cursor) {
				while (cursor.next()) {
					target.setGroupId(cursor.getInt(CCPDatabaseModelConnector.ITEMGROUP_GROUPID_COLINDEX));
					target.setCategoryId(cursor.getInt(CCPDatabaseModelConnector.ITEMGROUP_CATEGORYID_COLINDEX));
					target.setGroupName(cursor.getString(CCPDatabaseModelConnector.ITEMGROUP_GROUPNAME_COLINDEX));
					target.setIconLinkName(cursor.getString(CCPDatabaseModelConnector.ITEMGROUP_ICONLINKNAME_COLINDEX));
				}
				return target;
			}
		} catch (final Exception ex) {
			CCPDatabaseModelConnector.logger.error("E- [CCPDatabaseModelConnector.searchItemGroup4Id]> Exception: "
					+ ex.getMessage());
		} finally {
			try {
				if (null != cursor) {
					cursor.close();
				}
				if (null != prepStmt) {
					prepStmt.close();
				}

			} catch (SQLException ex) {
			}
		}
		return target;
	}

	/**
	 * Search on the eve.db database for the item category information. This new select is used to get access to
	 * the icon information that should be stored to be correlated to the resource list.
	 */
	public ItemCategory searchItemCategory4Id(final int targetCategoryId) {
		//		ArrayList<Resource> results = new ArrayList<Resource>();
		ItemCategory target = new ItemCategory();
		PreparedStatement prepStmt = null;
		ResultSet cursor = null;
		try {
			prepStmt = this.getCCPDatabase().prepareStatement(CCPDatabaseModelConnector.SELECT_ITEMCATEGORY);
			prepStmt.setString(1, Integer.valueOf(targetCategoryId).toString());
			cursor = prepStmt.executeQuery();
			if (null != cursor) {
				while (cursor.next()) {
					target.setCategoryId(cursor.getInt(CCPDatabaseModelConnector.ITEMCATEGORY_CATEGORYID_COLINDEX));
					target.setCategoryName(cursor.getString(CCPDatabaseModelConnector.ITEMCATEGORY_CATEGORYNAME_COLINDEX));
					target.setIconLinkName(cursor.getString(CCPDatabaseModelConnector.ITEMCATEGORY_ICONLINKNAME_COLINDEX));
				}
				return target;
			}
		} catch (final Exception ex) {
			CCPDatabaseModelConnector.logger.error("E- [CCPDatabaseModelConnector.searchItemCategory4Id]> Exception: "
					+ ex.getMessage());
		} finally {
			try {
				if (null != cursor) {
					cursor.close();
				}
				if (null != prepStmt) {
					prepStmt.close();
				}

			} catch (SQLException ex) {
			}
		}
		return target;
	}

	private Connection getCCPDatabase() {
		if (null == ccpDatabase) {
			this.openCCPDataBase();
		}
		return ccpDatabase;
	}
}

// - UNUSED CODE ............................................................................................
