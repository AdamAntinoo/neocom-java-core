package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;

import java.sql.SQLException;

public interface ISDEDatabaseAdapter {
	RawStatement constructStatement( final String query, final String[] parameters ) throws SQLException;

	Dao<EsiLocation, Long> getLocationDao() throws SQLException;
}
