package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.dimensinfin.eveonline.neocom.core.support.GSONDateTimeDeserializer;
import org.dimensinfin.eveonline.neocom.core.support.GSONLocalDateDeserializer;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Adam on 15/01/2018.
 */
public class NeoComRetrofitHTTP {
	protected static final Converter.Factory GSON_CONVERTER_FACTORY =
			GsonConverterFactory.create(
					new GsonBuilder()
							.registerTypeAdapter(DateTime.class, new GSONDateTimeDeserializer())
							.registerTypeAdapter(LocalDate.class, new GSONLocalDateDeserializer())
							.create());
	private static String refreshToken = "";

	protected static String getRefreshToken() {
		return refreshToken;
	}

	public static void setRefeshToken( final String token ) {
		refreshToken = token;
	}

	// - F I E L D S
	protected NeoComOAuth20 neoComOAuth20;
	protected String esiDataServerLocation;
	protected String agent;
	protected File cacheDataFile;
	protected long cacheSize = 1024 * 1024;
	protected long timeout = TimeUnit.SECONDS.toMillis(60);

	// - C O N S T R U C T O R - S E C T I O N
	protected NeoComRetrofitHTTP() {
		super();
	}

	// - M E T H O D - S E C T I O N
	protected Retrofit build() {
		OkHttpClient.Builder retrofitClient =
				new OkHttpClient.Builder()
						.addInterceptor(chain -> {
							Request.Builder builder = chain.request().newBuilder()
									                          .addHeader("User-Agent", this.agent);
							return chain.proceed(builder.build());
						})
						.addInterceptor(chain -> {
							if (StringUtils.isBlank(getRefreshToken())) {
								return chain.proceed(chain.request());
							}

							Request.Builder builder = chain.request().newBuilder();
							final TokenTranslationResponse token = this.neoComOAuth20.fromRefresh(getRefreshToken());
							if (null != token) {
								builder.addHeader("Authorization", "Bearer " + token.getAccessToken());
							}
							return chain.proceed(builder.build());
						})
						.addInterceptor(chain -> {
							if (StringUtils.isBlank(getRefreshToken())) {
								return chain.proceed(chain.request());
							}

							Response r = chain.proceed(chain.request());
							if (r.isSuccessful()) {
								return r;
							}
							if (r.body().string().contains("invalid_token")) {
								this.neoComOAuth20.fromRefresh(getRefreshToken());
								r = chain.proceed(chain.request());
							}
							return r;
						});

		if (this.timeout != -1) {
			retrofitClient.readTimeout(this.timeout, TimeUnit.MILLISECONDS);
		}

		if (null != this.cacheDataFile) {
			retrofitClient.cache(new Cache(this.cacheDataFile, this.cacheSize));
		}

		OkHttpClient httpClient = retrofitClient
				                          .certificatePinner(
						                          new CertificatePinner.Builder()
								                          .add("login.eveonline.com", "sha256/075pvb1KMqiPud6f347Lhzb0ALOY+dX5G7u+Yx+b8U4=")
								                          .add("login.eveonline.com", "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=")
								                          .add("login.eveonline.com", "sha256/Vjs8r4z+80wjNcr1YKepWQboSIRi63WsWXhIMN+eWys=")
								                          .build())
				                          .build();
		return new Retrofit.Builder()
				       .baseUrl(this.esiDataServerLocation)
				       .addConverterFactory(GSON_CONVERTER_FACTORY)
				       .client(httpClient)
				       .build();
	}

	public String getAuthorizationUrl() {
		return this.neoComOAuth20.getAuthorizationUrl();
	}

	// - B U I L D E R
	public static class Builder {
		protected NeoComRetrofitHTTP onConstruction;

		public Builder() {
			this.onConstruction = new NeoComRetrofitHTTP();
		}

		public Builder withNeoComOAuth20( final NeoComOAuth20 neoComOAuth20 ) {
			this.onConstruction.neoComOAuth20 = neoComOAuth20;
			return this;
		}

		public Builder withEsiServerLocation( final String esiDataServerLocation ) {
			this.onConstruction.esiDataServerLocation = esiDataServerLocation;
			return this;
		}

		public Builder withAgent( final String agent ) {
			this.onConstruction.agent = agent;
			return this;
		}

		public Builder withCacheDataFile( final File cacheDataFile ) {
			this.onConstruction.cacheDataFile = cacheDataFile;
			return this;
		}

		public Builder withCacheSize( final long cacheSize ) {
			this.onConstruction.cacheSize = cacheSize;
			return this;
		}

		public Builder withTimeout( final long timeout ) {
			this.onConstruction.timeout = timeout;
			return this;
		}

		public Retrofit build() {
			return this.onConstruction.build();
		}
	}
}
