package org.dimensinfin.eveonline.neocom.database.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entities.NeoAsset;

public class AssetRepository {
	private static final Logger logger = LoggerFactory.getLogger( AssetRepository.class );
	// - C O M P O N E N T S
	protected Dao<NeoAsset, String> assetsDao;

	/**
	 * Get the complete list of the assets that belong to this owner.
	 */
	public List<NeoAsset> findAllByOwnerId( final Integer ownerId ) {
		Objects.requireNonNull( ownerId );
		try {
			QueryBuilder<NeoAsset, String> queryBuilder = this.assetsDao.queryBuilder();
			Where<NeoAsset, String> where = queryBuilder.where();
			where.eq( "ownerID", ownerId );
			final List<NeoAsset> assetList = assetsDao.query( queryBuilder.prepare() );
			logger.info( "-- Assets read: " + assetList.size() );
			return assetList;
		} catch (java.sql.SQLException sqle) {
			logger.error( "SQL [AssetRepository.findAllByOwnerId]> SQL Exception: {}", sqle.getMessage() );
			return new ArrayList<>();
		}
	}

	public Optional<NeoAsset> findAssetById( final Long assetId ) {
		Objects.requireNonNull( assetId );
		try {
			final List<NeoAsset> assetList = this.assetsDao.queryForEq( "assetId", assetId );
			logger.info( "-- Assets read: " + assetList.size() );
			if (assetList.size() > 0) return Optional.ofNullable( assetList.get( 0 ) );
			else return Optional.empty();
		} catch (java.sql.SQLException sqle) {
			logger.error( "SQL [AssetRepository.findAllByOwnerId]> SQL Exception: {}", sqle.getMessage() );
			return Optional.empty();
		}
	}

	// - B U I L D E R
	public static class Builder {
		private AssetRepository onConstruction;

		public Builder() {
			this.onConstruction = new AssetRepository();
		}

		public AssetRepository.Builder withAssetDao( final Dao<NeoAsset, String> assetsDao ) {
			Objects.requireNonNull( assetsDao );
			this.onConstruction.assetsDao = assetsDao;
			return this;
		}

		public AssetRepository build() {
			Objects.requireNonNull( this.onConstruction.assetsDao );
			return this.onConstruction;
		}
	}
}