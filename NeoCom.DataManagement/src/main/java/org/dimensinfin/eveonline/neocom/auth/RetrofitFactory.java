package org.dimensinfin.eveonline.neocom.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
	public static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter( DateTime.class, new GSONDateTimeDeserializer() )
							.registerTypeAdapter( LocalDate.class, new GSONLocalDateDeserializer() )
							.create() );

	private Map<String, Retrofit> connectors = new HashMap<>();
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;

	private RetrofitFactory() {}

	public Retrofit accessAuthenticatedConnector( final Credential credential ) {
		Retrofit hitConnector = this.connectors.get( credential.getUniqueId() );
		if (null == hitConnector) { // Create a new connector for this credential.
			final String esiDataServerLocation = this.configurationProvider.getResourceString(
					"P.esi.api.data.server.location",
					"https://esi.evetech.net/latest/" );
			hitConnector = new Retrofit.Builder()
					.baseUrl( esiDataServerLocation )
					.addConverterFactory( GSON_CONVERTER_FACTORY )
					.client( new HttpClientFactory.Builder().withCredential( credential ).generate() )
					.build();
			this.connectors.put( credential.getUniqueId(), hitConnector );
		}
		return hitConnector;
	}

	// - B U I L D E R
	public static class Builder {
		private RetrofitFactory onConstruction;

		public Builder() {
			this.onConstruction = new RetrofitFactory();
		}

		public RetrofitFactory.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public RetrofitFactory build() {
			return this.onConstruction;
		}
	}
}