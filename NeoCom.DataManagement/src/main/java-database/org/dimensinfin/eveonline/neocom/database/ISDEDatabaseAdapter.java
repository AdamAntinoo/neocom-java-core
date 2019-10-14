package org.dimensinfin.eveonline.neocom.database;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;

public interface ISDEDatabaseAdapter {
	RawStatement constructStatement( final String query, final String[] parameters ) throws SQLException;

	Dao<EsiLocation, Long> getLocationDao() throws NeoComRuntimeException;
}
