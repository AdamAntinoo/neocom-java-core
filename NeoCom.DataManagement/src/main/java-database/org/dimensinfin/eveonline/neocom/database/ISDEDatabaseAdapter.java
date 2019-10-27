package org.dimensinfin.eveonline.neocom.database;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

import java.sql.SQLException;

public interface ISDEDatabaseAdapter {
	RawStatement constructStatement( final String query, final String[] parameters ) throws SQLException;

	Integer getDatabaseVersion();

	Dao<EsiLocation, Long> getLocationDao() throws NeoComRuntimeException;
}
