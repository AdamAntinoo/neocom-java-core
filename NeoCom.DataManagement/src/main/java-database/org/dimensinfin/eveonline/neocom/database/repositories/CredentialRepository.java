package org.dimensinfin.eveonline.neocom.database.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;

import com.j256.ormlite.dao.Dao;

public class CredentialRepository {
	protected Dao<Credential, String> credentialDao;

	public List<Credential> accessAllCredentials() {
		List<Credential> credentialList = new ArrayList<>();
		try {
			return this.credentialDao.queryForAll();
			//					       .queryForEq("dataSource", sourceServer);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			//			logger.warn("W [GlobalDataManagerDataAccess.accessAllCredentials]> Exception reading all Credentials. " + sqle.getMessage());
		}
		return credentialList;

	}

	public void persist( final Credential record ) throws SQLException {
		this.credentialDao.createOrUpdate(record);
	}

	// - B U I L D E R
	public static class Builder {
		private CredentialRepository onConstruction;

		public Builder() {
			this.onConstruction = new CredentialRepository();
		}

		public Builder withCredentialDao( final Dao<Credential, String> credentialDao ) {
			this.onConstruction.credentialDao = credentialDao;
			return this;
		}

		public CredentialRepository build() {
			Objects.requireNonNull(this.onConstruction.credentialDao);
			return this.onConstruction;
		}
	}
}
