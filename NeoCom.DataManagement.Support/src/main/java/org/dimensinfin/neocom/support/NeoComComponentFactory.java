package org.dimensinfin.neocom.support;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapters.ESIDataAdapter;
import org.dimensinfin.eveonline.neocom.adapters.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapters.IFileSystem;
import org.dimensinfin.eveonline.neocom.database.repositories.CredentialRepository;
import org.dimensinfin.neocom.support.adapters.FileSystemSBImplementation;
import org.dimensinfin.neocom.support.adapters.NeoComSupportDBAdapter;
import org.dimensinfin.neocom.support.adapters.implementers.SBConfigurationProvider;
import org.dimensinfin.neocom.support.adapters.implementers.SBFileSystemAdapter;
import org.dimensinfin.neocom.support.adapters.implementers.SBNeoComDBAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a singleton with global access that will contain application component references so they can be injected to other
 * components. The creation
 * of those components will be down internally on demand and with the knowledge of what components depend on other components.
 *
 * @author Adam Antinoo
 */
public class NeoComComponentFactory {
	public static final String DEFAULT_ESI_SERVER = "Tranquility";
	protected static Logger logger = LoggerFactory.getLogger( NeoComComponentFactory.class );
	private static NeoComComponentFactory singleton;

	private IConfigurationProvider configurationProvider;
	private IFileSystem fileSystemAdapter;
	private ESIDataAdapter esiDataAdapter;
	private SBNeoComDBAdapter neocomDBAdapter;
	private CredentialRepository credentialRepository;

	public static NeoComComponentFactory getSingleton() {
		if (null == singleton) singleton = new NeoComComponentFactory();
		return singleton;
	}

	// - A C C E S S O R S
	public CredentialRepository getCredentialRepository() {
		if (null == this.credentialRepository) {
			try {
				credentialRepository = new CredentialRepository.Builder()
						.withCredentialDao( this.getNeoComDBAdapter().getCredentialDao() )
						.build();
			} catch (SQLException sqle) {
				credentialRepository = null;
				Objects.requireNonNull( credentialRepository );
			}
		}
		return this.credentialRepository;
	}

	public SBNeoComDBAdapter getNeoComDBAdapter() {
		if (null == this.neocomDBAdapter) {
			try {
				// Create the neocom databasea adapter from configuration properties.
				final String databaseHost = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasehost" );
				final String databaseName = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasename" );
//				final String user = this.getConfigurationProvider().getResourceString( "P.database.neocom.databaseuser" );
//				final String password = this.getConfigurationProvider().getResourceString( "P.database.neocom.databasepassword" );
				final String locator = databaseHost+databaseName;
				this.neocomDBAdapter = new SBNeoComDBAdapter.Builder()
						.optionalDatabaseUrl( locator )
						.build();


				//			try {
//			final String databaseType = this.getConfigurationProvider()
//					.getResourceString( "P.database.neocom.databasetype", "sqlite" );
//			if (databaseType.equalsIgnoreCase( "postgres" )) {
//				// Postgres means Heroku and then configuration for connection from environment
//				final String localConnectionDescriptor = System.getenv( "JDBC_DATABASE_URL" );
//				neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
//						.withDatabaseConnection( localConnectionDescriptor )
//						.build();
//			}
//			if (databaseType.equalsIgnoreCase( "sqlite" )) {
//				// Postgres means Heroku and then configuration for connection from environment
//				final String localConnectionDescriptor = System.getenv( "JDBC_DATABASE_URL" );
//				neocomDBAdapter = new NeoComSupportDBAdapter.Builder()
//						.withDatabaseConnection( localConnectionDescriptor )
//						.build();
//			}
			} catch (SQLException sqle) {
				neocomDBAdapter = null;
				Objects.requireNonNull( neocomDBAdapter );
			}
		}
		return this.neocomDBAdapter;
	}

	//	public static NeoComComponentFactory initialiseSingleton( final Application newApplication ) {
	//		application = newApplication;
	//		if (null == singleton) singleton = new NeoComComponentFactory();
	//		return singleton;
	//	}

	public IFileSystem getFileSystemAdapter() {
		if (null == this.fileSystemAdapter) {
			this.fileSystemAdapter = new SBFileSystemAdapter.Builder()
					.withRootDirectory( "NeoCom.Support" )
					.build();
		}
		return this.fileSystemAdapter;
	}

	//	public Application getApplication() {
	//		if (null == application)
	//			throw new NeoComRuntimeException("NeoCom global singleton is not instantiated. Please complete initialisation.");
	//		return application;
	//	}

	public IConfigurationProvider getConfigurationProvider() {
		if (null == this.configurationProvider) {
			try {
				this.configurationProvider =
						new SBConfigurationProvider.Builder(  )
						.withPropertiesDirectory( "properties" )
						.build();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.configurationProvider;
	}

	//	@Deprecated
	//	public IGlobalPreferencesManager getPreferencesProvider() {
	//		if (null == this.preferencesProvider) {
	//			preferencesProvider = new GlobalAndroidPreferencesProvider.Builder()
	//					                      .withApplication(this.getApplication())
	//					                      .build();
	//		}
	//		return this.preferencesProvider;
	//	}
	//
	//
	//	@Deprecated
	//	public ESIGlobalAdapter getEsiAdapter() {
	//		if (null == this.esiAdapter) {
	//			esiAdapter = new ESIGlobalAdapter.Builder(this.getConfigurationProvider(), this.getFileSystemAdapter())
	//					             .build();
	//		}
	//		return this.esiAdapter;
	//	}

	//	public ESIDataAdapter getEsiDataAdapter() {
	//		if (null == this.esiDataAdapter)
	//			esiDataAdapter = new ESIDataAdapter.Builder(this.getConfigurationProvider(), this.getFileSystemAdapter())
	//					                 .build();
	//		NeoItem.injectEsiDataAdapter(this.esiDataAdapter);
	//		return this.esiDataAdapter;
	//	}
	//
	//	public DataDownloaderService getDownloaderService() {
	//		if (null == this.downloaderService) {
	//			downloaderService = new DataDownloaderService.Builder(this.getEsiDataAdapter())
	//					                    .build();
	//		}
	//		return this.downloaderService;
	//	}


	//	public MiningRepository getMiningRepository() {
	//		if (null == this.miningRepository) {
	//			try {
	//				miningRepository = new MiningRepository.Builder()
	//						                   .withMiningExtractionDao(this.getNeoComDBHelper().getMiningExtractionDao())
	//						                   .build();
	//			} catch (SQLException sqle) {
	//				miningRepository = null;
	//				Objects.requireNonNull(miningRepository);
	//			}
	//		}
	//		return this.miningRepository;
	//	}


	//	public ISDEDatabaseAdapter getSDEDatabaseAdapter() {
	//		if (null == this.sdeDatabaseAdapter) {
	//			this.sdeDatabaseAdapter = new SDEAndroidDBHelper.Builder()
	//					                          .withFileSystemAdapter(this.getFileSystemAdapter())
	//					                          .build();
	//		}
	//		return this.sdeDatabaseAdapter;
	//	}
	//
	//	public PlanetaryRepository getPlanetaryRepository() {
	//		if (null == this.planetaryRepository) {
	//			this.planetaryRepository = new PlanetaryRepository.Builder()
	//					                           .withSDEDatabaseAdapter(this.getSDEDatabaseAdapter())
	//					                           .build();
	//		}
	//		return this.planetaryRepository;
	//	}
}
