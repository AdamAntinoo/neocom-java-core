package org.dimensinfin.eveonline.neocom.support;

import java.util.Objects;

import org.dimensinfin.eveonline.neocom.adapters.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.adapters.IFileSystem;
import org.dimensinfin.eveonline.neocom.adapters.NeoComRetrofitFactory;
import org.dimensinfin.eveonline.neocom.auth.NeoComRetrofitNoOAuthHTTP;

import retrofit2.Retrofit;

public class SupportNeoComRetrofitFactory extends NeoComRetrofitFactory {
	@Override
	public Retrofit accessNoAuthRetrofit() {
		if (null == this.neocomRetrofitNoAuth) this.neocomRetrofitNoAuth = this.generateNoAuthRetrofit();
		return this.neocomRetrofitNoAuth;
	}

	private Retrofit generateNoAuthRetrofit() {
		final String agent = "Default agent" ;
		return new NeoComRetrofitNoOAuthHTTP.Builder()
				.withEsiServerLocation( "http://localhost:6092" )
				.withAgent( agent )
				.build();
	}

	// - B U I L D E R
	public static class Builder extends NeoComRetrofitFactory.Builder{
		private SupportNeoComRetrofitFactory onConstruction;

		public Builder() {
			this.onConstruction = new SupportNeoComRetrofitFactory();
		}

		public SupportNeoComRetrofitFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public SupportNeoComRetrofitFactory.Builder withFileSystemAdapter( final IFileSystem fileSystemAdapter ) {
			Objects.requireNonNull( fileSystemAdapter );
			this.onConstruction.fileSystemAdapter = fileSystemAdapter;
			return this;
		}

		public SupportNeoComRetrofitFactory build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			Objects.requireNonNull( this.onConstruction.fileSystemAdapter );
			return this.onConstruction;
		}
	}
}