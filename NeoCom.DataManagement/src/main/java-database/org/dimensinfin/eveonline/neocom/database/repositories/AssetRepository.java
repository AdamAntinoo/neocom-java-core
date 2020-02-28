package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

public class AssetRepository {
	private static final Logger logger = LoggerFactory.getLogger( AssetRepository.class );
	// - C O M P O N E N T S
	protected Dao<NeoAsset, UUID> assetDao;
	protected ConnectionSource connection4Transaction;

	protected AssetRepository() {}

	/**
	 * Get the complete list of the assets that belong to this owner.
	 */
	public List<NeoAsset> findAllByOwnerId( final Integer ownerId ) {
		Objects.requireNonNull( ownerId );
		try {
			QueryBuilder<NeoAsset, UUID> queryBuilder = this.assetDao.queryBuilder();
			Where<NeoAsset, UUID> where = queryBuilder.where();
			where.eq( "ownerId", ownerId );
			final List<NeoAsset> assetList = assetDao.query( queryBuilder.prepare() );
			NeoComLogger.info( "Assets read: {}", assetList.size() + "" );
			final List<NeoAsset> resultList = new ArrayList<>();
			return Stream.of( assetList )
					.map( this::assetReconstructor )
					.collect( Collectors.toList() );
		} catch (java.sql.SQLException sqle) {
			logger.error( "SQL [AssetRepository.findAllByOwnerId]> SQL Exception: {}", sqle.getMessage() );
			return new ArrayList<>();
		}
	}

	public NeoAsset findAssetById( final Long assetId ) {
		Objects.requireNonNull( assetId );
		try {
			final List<NeoAsset> assetList = this.assetDao.queryForEq( "assetId", assetId );
			NeoComLogger.info( "Assets read: {}", assetList.size() + "" );
			if (!assetList.isEmpty()) return assetList.get( 0 );
			else return null;
		} catch (java.sql.SQLException sqle) {
			logger.error( "SQL [AssetRepository.findAllByOwnerId]> SQL Exception: {}", sqle.getMessage() );
			return null;
		}
	}

	/**
	 * removes from the application database any asset and blueprint that contains the special -1 code as the
	 * owner identifier. Those records are from older downloads and have to be removed to avoid merging with the
	 * new download.
	 */
	public synchronized void clearInvalidRecords( final long pilotIdentifier ) {
		NeoComLogger.enter( "pilotIdentifier: {}", Long.toString( pilotIdentifier ) );
		synchronized (this.connection4Transaction) {
			try {
				TransactionManager.callInTransaction( this.connection4Transaction, (Callable<Void>) () -> {
					// Remove all assets that do not have a valid owner.
					final DeleteBuilder<NeoAsset, UUID> deleteBuilder = assetDao.deleteBuilder();
					deleteBuilder.where().eq( "ownerId", (pilotIdentifier * -1) );
					int count = deleteBuilder.delete();
					NeoComLogger.info( "Invalid assets cleared for owner {}: {}",
							(pilotIdentifier * -1) + "", count + "" );

					// Remove all blueprints that do not have a valid owner.
//					final DeleteBuilder<NeoComBlueprint, String> deleteBuilderBlueprint = getBlueprintDao().deleteBuilder();
//					deleteBuilderBlueprint.where().eq( "ownerId", (pilotIdentifier * -1) );
//					count = deleteBuilderBlueprint.delete();
//					logger.info(
//							"-- [NeoComAndroidDBHelper.clearInvalidRecords]> Invalid blueprints cleared for owner {}: {}",
//							(pilotIdentifier * -1),
//							count );
					return null;
				} );
			} catch (final SQLException ex) {
				logger.warn(
						"W> [NeoComAndroidDBHelper.clearInvalidRecords]> Problem clearing invalid records. " + ex.getMessage() );
			} finally {
				logger.info( "<< [NeoComAndroidDBHelper.clearInvalidRecords]" );
			}
		}
	}

	/**
	 * Changes the owner id for all records from a new download with the id of the current character. This
	 * completes the download and the assignment of the resources to the character without interrupting the
	 * processing of data by the application.
	 */
	public synchronized void replaceAssets( final long pilotid ) {
		logger.info( ">> [AndroidNeoComDBHelper.clearInvalidRecords]> pilotid: {}", pilotid );
		synchronized (this.connection4Transaction) {
			try {
				TransactionManager.callInTransaction( this.connection4Transaction, (Callable<Void>) () -> {
					// Remove all assets from this owner before adding the new set.
					final DeleteBuilder<NeoAsset, UUID> deleteBuilder = this.assetDao.deleteBuilder();
					deleteBuilder.where().eq( "ownerId", pilotid );
					int count = deleteBuilder.delete();
					logger.info( "-- [AndroidNeoComDBHelper.replaceAssets]> Invalid assets cleared for owner {}: {}", pilotid,
							count );

					// Replace the owner to vake the assets valid.
					final UpdateBuilder<NeoAsset, UUID> updateBuilder = this.assetDao.updateBuilder();
					updateBuilder.updateColumnValue( "ownerId", pilotid )
							.where().eq( "ownerId", (pilotid * -1) );
					count = updateBuilder.update();
					logger.info( "-- [AndroidNeoComDBHelper.replaceAssets]> Replace owner {} for assets: {}", pilotid,
							count );
					return null;
				} );
			} catch (final SQLException ex) {
				logger.warn( "W> [AndroidNeoComDBHelper.replaceAssets]> Problem replacing records. " + ex.getMessage() );
			} finally {
				logger.info( "<< [AndroidNeoComDBHelper.replaceAssets]" );
			}
		}
	}

	public void persist( final NeoAsset record ) throws SQLException {
		if (null != record) {
			record.timeStamp();
			record.generateUid();
			this.assetDao.createOrUpdate( record );
			NeoComLogger.info( "Wrote asset to database id [" + record.getAssetId() + "]" );
		}
	}

	private NeoAsset assetReconstructor( final NeoAsset target ) {
		NeoComLogger.enter( "Reconstructing asset: " + target.getAssetId() );
		target.setItemDelegate( new NeoItem( target.getTypeId() ) );
		if (target.hasParentContainer()) { // Search for the parent asset. If not found then report a warning.
			final NeoAsset parent = this.findAssetById( target.getParentContainerId() );
			if (null != parent) target.setParentContainer( this.assetReconstructor( parent ) );
			else {
				NeoComLogger.error( "Parent asset not found on post read action: " +
								target.getParentContainerId(),
						new NeoComRuntimeException( "If an asset has a parent identifier then it should have a matching asset " +
								"instance." ) );
			}
		}
		return target;
	}

	// - B U I L D E R
	public static class Builder {
		private AssetRepository onConstruction;

		public Builder() {
			this.onConstruction = new AssetRepository();
		}

		public AssetRepository.Builder withAssetDao( final Dao<NeoAsset, UUID> assetsDao ) {
			Objects.requireNonNull( assetsDao );
			this.onConstruction.assetDao = assetsDao;
			return this;
		}

		public AssetRepository.Builder withConnection4Transaction( final ConnectionSource connection ) {
			Objects.requireNonNull( connection );
			this.onConstruction.connection4Transaction = connection;
			return this;
		}

		public AssetRepository build() {
			Objects.requireNonNull( this.onConstruction.assetDao );
			Objects.requireNonNull( this.onConstruction.connection4Transaction );
			return this.onConstruction;
		}
	}
}
