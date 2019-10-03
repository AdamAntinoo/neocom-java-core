package org.dimensinfin.eveonline.neocom.auth;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.dimensinfin.eveonline.neocom.interfaces.IConfigurationProvider;

import org.apache.commons.lang3.StringUtils;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Created by Adam on 15/01/2018.
 */
public class NeoComRetrofitNoOAuthHTTP extends NeoComRetrofitHTTP {

	private NeoComRetrofitNoOAuthHTTP() {
		super();
	}

	// - M E T H O D - S E C T I O N
	public Retrofit build() {
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

							Response r = chain.proceed(chain.request());
							if (r.isSuccessful()) {
								return r;
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

	// - B U I L D E R
	public static class Builder {
		protected NeoComRetrofitNoOAuthHTTP onConstruction;

		public Builder() {
			this.onConstruction = new NeoComRetrofitNoOAuthHTTP();
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
