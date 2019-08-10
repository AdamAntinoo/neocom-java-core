package org.dimensinfin.eveonline.neocom.support.adapters;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapters.SDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.ISDEDatabaseAdapter;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.interfaces.IFileSystem;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

/**
 * This is a singleton with global access that will contain application component references so they can be injected to other components. The creation
 * of those components will be down internally on demand and with the knowledge of what components depend on other components.
 *
 * @author Adam Antinoo
 */
public class NeoComComponentFactory {
	public static final String DEFAULT_ESI_SERVER = "Tranquility";
	protected static Logger logger = LoggerFactory.getLogger(NeoComComponentFactory.class);
	private static NeoComComponentFactory singleton;

	public static NeoComComponentFactory getSingleton() {
		if (null == singleton) singleton = new NeoComComponentFactory();
		return singleton;
	}

	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private ESIDataAdapter esiDataAdapter;
	private NeoComSupportDBAdapter neocomDBAdapter;
	private CredentialRepository credentialRepository;
	private SupportMiningRepository miningRepository;
	private ISDEDatabaseAdapter sdeDatabaseAdapter;
	private SupportLocationRepository locationRepository;

	// - A C C E S S O R S
	public SupportLocationRepository getLocationRepository() {
		if (null == this.locationRepository) {
			try {
				this.locationRepository = new SupportLocationRepository.Builder()
						                          .withLocationDao(this.getNeoComDBAdapter().getLocationDao())
						                          .build();
			} catch (SQLException sqle) {
				this.locationRepository = null;
				Objects.requireNonNull(this.locationRepository);
			}
		}
		return this.locationRepository;
	}

	public ISDEDatabaseAdapter getSDEDatabaseAdapter() {
		if (null == this.sdeDatabaseAdapter) {
			this.sdeDatabaseAdapter = new SDEDatabaseAdapter.Builder()
					                          .withFileSystemAdapter(this.getFileSystemAdapter())
					                          .withDatabasePath("")
					                          .withDatabaseName("sde.db")
					                          .build();
		}
		return this.sdeDatabaseAdapter;
	}

	public CredentialRepository getCredentialRepository() {
		if (null == this.credentialRepository) {
			try {
				credentialRepository = new CredentialRepository.Builder()
						                       .withCredentialDao(this.getNeoComDBAdapter().getCredentialDao())
						                       .build();
			} catch (SQLException sqle) {
				credentialRepository = null;
				Objects.requireNonNull(credentialRepository);
			}
		}
		return this.credentialRepository;
	}

	public SupportMiningRepository getMiningRepository() {
		if (null == this.miningRepository) {
			try {
				this.miningRepository = new SupportMiningRepository.Builder()
						                        .withMiningExtractionDao(this.getNeoComDBAdapter().getMiningExtractionDao())
						                        .build();
			} catch (SQLException sqle) {
				this.miningRepository = null;
				Objects.requireNonNull(this.miningRepository);
			}
		}
		return this.miningRepository;
	}

	public NeoComSupportDBAdapter getNeoComDBAdapter() {
		if (null == this.neocomDBAdapter) {
			//			try {
			final String databaseType = this.getConfigurationProvider().getResourceString("P.database.neocom.databasetype",
			                                                                              "sqlite");
			final String runtimePlatform = this.getConfigurationProvider().getResourceString("P.runtime.platform", "Java");
			if (databaseType.equalsIgnoreCase("postgres")) {
				// Postgres means Heroku and then configuration for connection from environment
				final String localConnectionDescriptor = System.getenv("JDBC_DATABASE_URL");
				neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
						                  .withDatabaseConnection(localConnectionDescriptor)
						                  .build();
			}
			if (runtimePlatform.equalsIgnoreCase("java")) {
				if (databaseType.equalsIgnoreCase("sqlite")) {
					// Postgres means Heroku and then configuration for connection from environment
					final String localConnectionDescriptor = this.getConfigurationProvider().getResourceString(
							"P.database.neocom.database.sqlite.connection");
					neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
							                  .withDatabaseConnection(localConnectionDescriptor)
							                  .build();
				}
			}
			if (runtimePlatform.equalsIgnoreCase("heroku"))
				if (databaseType.equalsIgnoreCase("sqlite")) {
					// Postgres means Heroku and then configuration for connection from environment
					final String localConnectionDescriptor = System.getenv("JDBC_DATABASE_URL");
					neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
							                  .withDatabaseConnection(localConnectionDescriptor)
							                  .build();
				}
			//			} catch (SQLException sqle) {
			//				neocomDBAdapter = null;
			//				Objects.requireNonNull(neocomDBAdapter);
			//			}
		}
		return this.neocomDBAdapter;
	}

	public IFileSystem getFileSystemAdapter() {
		if (null == this.fileSystemAdapter) {
			fileSystemAdapter = new FileSystemSBImplementation.Builder()
					                    .withRootDirectory("AcceptanceTests")
					                    .build();
		}
		return this.fileSystemAdapter;
	}

	public IConfigurationProvider getConfigurationProvider() {
		if (null == this.configurationProvider) {
			this.configurationProvider = new SBConfigurationProvider.Builder("acceptancetests.properties").build();
		}
		return this.configurationProvider;
	}

	public ESIDataAdapter getEsiDataAdapter() {
		if (null == this.esiDataAdapter)
			esiDataAdapter = new ESIDataAdapter.Builder(this.getConfigurationProvider(), this.getFileSystemAdapter())
					                 .build();
		EveItem.injectEsiDataAdapter(this.esiDataAdapter);
		return this.esiDataAdapter;
	}
}
