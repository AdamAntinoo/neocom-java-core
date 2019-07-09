package org.dimensinfin.eveonline.neocom.auth.mock;

import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Adam on 15/01/2018.
 */
public class NeoComRetrofitMock /*extends NeoComRetrofitHTTP*/ {
	protected static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter(DateTime.class, new GSONDateTimeDeserializer())
							.registerTypeAdapter(LocalDate.class, new GSONLocalDateDeserializer())
							.create());
	protected long timeout = TimeUnit.SECONDS.toMillis(60);
	protected String agent;
	protected String esiDataServerLocation;

	public NeoComRetrofitMock() {
		super();
	}

	protected Retrofit build() {
		final OkHttpClient.Builder retrofitClient =
				new OkHttpClient.Builder()
						.addInterceptor(new MockInterceptor());
		//						.addInterceptor(chain -> {
		//							Request.Builder builder = chain.request().newBuilder()
		//									                          .addHeader("User-Agent", this.agent);
		//							Response r = chain.proceed(chain.request());
		//							if (r.isSuccessful()) {
		//								return r;
		//							}
		//							return r;
		//						}

		if (this.timeout != -1) {
			retrofitClient.readTimeout(this.timeout, TimeUnit.MILLISECONDS);
		}
		//		if (null != this.cacheDataFile) {
		//			retrofitClient.cache(new Cache(this.cacheDataFile, this.cacheSize));
		//		}
		return new Retrofit.Builder()
				       .baseUrl(this.esiDataServerLocation)
				       .addConverterFactory(GSON_CONVERTER_FACTORY)
				       .client(retrofitClient.build())
				       .build();
	}

	// - B U I L D E R
	public static class Builder {
		protected NeoComRetrofitMock onConstruction;

		public Builder() {
			this.onConstruction = new NeoComRetrofitMock();
		}

		//		public Builder withNeoComOAuth20( final NeoComOAuth20 neoComOAuth20 ) {
		//			this.onConstruction.neoComOAuth20 = neoComOAuth20;
		//			return this;
		//		}

		public Builder withEsiServerLocation( final String esiDataServerLocation ) {
			this.onConstruction.esiDataServerLocation = esiDataServerLocation;
			return this;
		}

		public Builder withAgent( final String agent ) {
			this.onConstruction.agent = agent;
			return this;
		}
		//
		//		public Builder withCacheDataFile( final File cacheDataFile ) {
		//			this.onConstruction.cacheDataFile = cacheDataFile;
		//			return this;
		//		}
		//
		//		public Builder withCacheSize( final long cacheSize ) {
		//			this.onConstruction.cacheSize = cacheSize;
		//			return this;
		//		}

		//		public Builder withTimeout( final long timeout ) {
		//			this.onConstruction.timeout = timeout;
		//			return this;
		//		}

		public Retrofit build() {
			return this.onConstruction.build();
		}
	}
}
