package org.dimensinfin.eveonline.neocom.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.adapters.AConfigurationProvider;

public class SupportConfigurationProvider extends AConfigurationProvider {
//	protected static Logger logger = LoggerFactory.getLogger(GlobalConfigurationProvider.class);

	// - C O N S T R U C T O R S
//	private SupportConfigurationProvider( final String propertiesFolder ) {
//		super(propertiesFolder);
//	}

//	@Override
//	public Integer getResourceInteger( final String key ) {
//		return new Integer( 100 );
//	}

	// - P L A T F O R M   S P E C I F I C   S E C T I O N
	protected void readAllProperties() {
		this.addProperty( "P.cache.root.storage.name", "src/test/NeoCom.UnitTest" );
		this.addProperty( "P.cache.directory.path", "NeoComCache/" );
		this.addProperty( "P.cache.directory.store.esiitem", "ESIData.cache.store" );
		this.addProperty( "P.cache.esiitem.timeout", "86400" );
		this.addProperty( "P.cache.esinetwork.filename", "ESINetworkManager.cache.store" );
		this.addProperty( "P.esi.tranquility.authorization.server", "http://localhost:6091" );
		this.addProperty( "P.esi.tranquility.authorization.clientid", "dbc9c2b1d18d49d8adacd23436c5281d" );
		this.addProperty( "P.esi.tranquility.authorization.secretkey", "QqnTLCqLQxZYHgHUuobkNA9g950vXVYDMg8ETTXM" );
		this.addProperty( "P.esi.tranquility.authorization.callback", "eveauth-neocom://esiauthentication" );
		this.addProperty( "P.esi.tranquility.authorization.agent", "org.dimensinfin.eveonline.neocom; Dimensinfin Industries; " +
				"Data Management Unit Testing" );
		this.addProperty( "P.esi.tranquility.authorization.scopes.filename", "esiconf/ESINetworkScopes.Tranquility.txt" );

	}

	protected List<String> getResourceFiles( String path ) throws IOException {
		return new ArrayList<>();
	}

	protected void addProperty( final String key, final String value ) {
		this.configurationProperties.setProperty( key, value );
	}

	// - B U I L D E R
	public static class Builder extends AConfigurationProvider.Builder<SupportConfigurationProvider,
			SupportConfigurationProvider.Builder> {
		private SupportConfigurationProvider onConstruction;

		@Override
		protected SupportConfigurationProvider getActual() {
			if (null == this.onConstruction) this.onConstruction = new SupportConfigurationProvider();
			return this.onConstruction;
		}

		@Override
		protected SupportConfigurationProvider.Builder getActualBuilder() {
			return this;
		}

		public SupportConfigurationProvider build() throws IOException {
			final SupportConfigurationProvider provider = (SupportConfigurationProvider) super.build();
			return this.onConstruction;
		}
	}
}
