package org.dimensinfin.eveonline.neocom.auth;

import java.io.IOException;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationService;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;
import org.dimensinfin.eveonline.neocom.utility.Base64;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_OAUTH_AUTHORIZATION_STATE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CLIENTID;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_CONTENT_TYPE;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY;
import static org.dimensinfin.eveonline.neocom.provider.PropertiesDefinitionsConstants.ESI_TRANQUILITY_AUTHORIZATION_SERVER;

public class NeoComOauth2Flow {
	private static final String V1_OAUTH = "oauth/authorize/";
	private static final String V2_OAUTH = "v2/oauth/authorize/";
	private static final String LOGIN_URL = "https://login.eveonline.com/" + V1_OAUTH +
			"?response_type=code&" +
			"redirect_uri=eveauth-neocom%3A%2F%2Fesiauthentication&" +
			"scope=publicData esi-location.read_location.v1 esi-location.read_ship_type.v1 esi-mail.read_mail.v1 esi-skills.read_skills.v1 esi-skills.read_skillqueue.v1 esi-wallet.read_character_wallet.v1 esi-wallet.read_corporation_wallet.v1 esi-search.search_structures.v1 esi-clones.read_clones.v1 esi-universe.read_structures.v1 esi-assets.read_assets.v1 esi-planets.manage_planets.v1 esi-fittings.read_fittings.v1 esi-industry.read_character_jobs.v1 esi-markets.read_character_orders.v1 esi-characters.read_blueprints.v1 esi-contracts.read_character_contracts.v1 esi-clones.read_implants.v1 esi-wallet.read_corporation_wallets.v1 esi-characters.read_notifications.v1 esi-corporations.read_divisions.v1 esi-assets.read_corporation_assets.v1 esi-corporations.read_blueprints.v1 esi-contracts.read_corporation_contracts.v1 esi-industry.read_corporation_jobs.v1 esi-markets.read_corporation_orders.v1 esi-industry.read_character_mining.v1 esi-industry.read_corporation_mining.v1";

	private TokenVerification tokenVerificationStore;
	// - C O M P O N E N T S
	private IConfigurationService configurationProvider;

	private NeoComOauth2Flow() {}

	public String generateLoginUrl( final String esiServer ) {
		final String state = Base64.encodeBytes(
				this.configurationProvider.getResourceString( ESI_OAUTH_AUTHORIZATION_STATE ).getBytes() );
		final String clientId = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
		return LOGIN_URL + "&client_id=" + clientId + "&state=" + state;
	}

	public void onStartFlow( final String code, final String state, final String dataSource ) {
		this.tokenVerificationStore = new TokenVerification()
				.setAuthCode( code )
				.setState( state )
				.setDataSource( dataSource.toLowerCase() );
	}

	public TokenVerification onTranslationStep() {
		this.tokenVerificationStore
				.setTokenTranslationResponse( this.getTokenTranslationResponse( this.tokenVerificationStore ) );
		tokenVerificationStore.setVerifyCharacterResponse( this.getVerifyCharacterResponse( this.tokenVerificationStore ) );
		return this.tokenVerificationStore;
	}

	/**
	 * Validates the encoded values of the state received to verify that the calling application is on the NeoCom set. There is
	 * a fixed value and in future implementations a variable value to improve security.
	 *
	 * @param state the state data received. Needs to mah the state generated locally.
	 */
	public boolean verifyState( final String state ) {
		final String testState = Base64.encodeBytes(
				this.configurationProvider.getResourceString( ESI_OAUTH_AUTHORIZATION_STATE ).getBytes()
		).replaceAll( "\n", "" );
		return state.equals( testState );
	}

	private TokenTranslationResponse getTokenTranslationResponse( final TokenVerification store ) {
		// Preload configuration variables.
		final String esiServer = store.getDataSource();
		final String authorizationServer = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SERVER );
		final String authorizationClientid = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CLIENTID );
		final String authorizationSecretKey = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_SECRETKEY );
		final String authorizationContentType = this.configurationProvider.getResourceString( ESI_TRANQUILITY_AUTHORIZATION_CONTENT_TYPE );
		final String esiServerLoginUrl = this.configurationProvider.getResourceString(
				ESI_TRANQUILITY_AUTHORIZATION_SERVER );
		// Get the request.
		final GetAccessToken serviceGetAccessToken = new Retrofit.Builder()
				.baseUrl( authorizationServer )
				.addConverterFactory( JacksonConverterFactory.create() )
				.build()
				.create( GetAccessToken.class );
		final TokenRequestBody tokenRequestBody = new TokenRequestBody().setCode( store.getAuthCode() );
		NeoComLogger.info( "Creating request call." );
		final String peckString = authorizationClientid + ":" + authorizationSecretKey;
		String peck = Base64.encodeBytes( peckString.getBytes() ).replaceAll( "\n", "" );
		store.setPeck( peck );
		final Call<TokenTranslationResponse> request = serviceGetAccessToken.getAccessToken(
				authorizationContentType,
				esiServerLoginUrl,
				"Basic " + peck,
				tokenRequestBody
		);
		// Getting the request response to be stored if valid.
		// TODO - This depends on the exception management implementation on the library.
		try {
			final Response<TokenTranslationResponse> response = request.execute();
			if (response.isSuccessful()) {
				NeoComLogger.info( "Response is 200 OK." );
				final TokenTranslationResponse token = response.body();
				return token;
			} else {
//				NeoComLogger.info( "Response is {} - {}.", HttpStatus.BAD_REQUEST, response.message() );
//				throw new NeoComSBException( ErrorInfo.AUTHORIZATION_TRANSLATION );
			}
		} catch (IOException ioe) {
//			NeoComLogger.info( "Response is {} - {}.", HttpStatus.BAD_REQUEST, ioe.getMessage() );
//			throw new NeoComSBException( ErrorInfo.AUTHORIZATION_TRANSLATION, ioe );
		}
		return null;
	}

	private VerifyCharacterResponse getVerifyCharacterResponse( final TokenVerification store ) {
		OkHttpClient.Builder verifyClient =
				new OkHttpClient.Builder()
						.certificatePinner(
								new CertificatePinner.Builder()
										.add( "login.eveonline.com", "sha256/5UeWOuDyX7IUmcKnsVdx+vLMkxEGAtzfaOUQT/caUBE=" )
										.add( "login.eveonline.com", "sha256/980Ionqp3wkYtN9SZVgMzuWQzJta1nfxNPwTem1X0uc=" )
										.add( "login.eveonline.com", "sha256/du6FkDdMcVQ3u8prumAo6t3i3G27uMP2EOhR8R0at/U=" )
										.build() )
						.addInterceptor( chain -> chain.proceed(
								chain.request()
										.newBuilder()
										.addHeader( "User-Agent", "org.dimensinfin" )
										.build() ) );
		// Verify the character authenticated.
		NeoComLogger.info( "-- [AuthorizationService.getVerifyCharacterResponse]> Creating character verification." );
		final VerifyCharacter verificationService = new Retrofit.Builder()
				.baseUrl( this.configurationProvider.getResourceString(
						"P.esi." +
								store.getDataSource().toLowerCase() +
								".authorization.server" ) )
				.addConverterFactory( JacksonConverterFactory.create() )
				.client( verifyClient.build() )
				.build()
				.create( VerifyCharacter.class );
		final String accessToken = store.getTokenTranslationResponse().getAccessToken();
		try {
			final Response<VerifyCharacterResponse> verificationResponse =
					verificationService.getVerification( "Bearer " + accessToken ).execute();
			if (verificationResponse.isSuccessful()) {
				NeoComLogger.info( "-- [AuthorizationService.getVerifyCharacterResponse]> Character verification OK." );
				return verificationResponse.body();
			} else {
//				logger.info( "-- [AuthorizationService.getVerifyCharacterResponse]> Response is {} - {}.",
//						HttpStatus.BAD_REQUEST, verificationResponse.message() );
//				throw new NeoComSBException( ErrorInfo.VERIFICATION_RESPONSE );

			}
		} catch (IOException ioe) {
//			logger.info( "-- [AuthorizationService.getVerifyCharacterResponse]> Response is {} - {}.",
//					HttpStatus.BAD_REQUEST, ioe.getMessage() );
//			throw new NeoComSBException( ErrorInfo.VERIFICATION_RESPONSE, ioe );
		}
		return null;
	}

	// - B U I L D E R
	public static class Builder {
		private NeoComOauth2Flow onConstruction;

		public Builder() {
			this.onConstruction = new NeoComOauth2Flow();
		}

		public NeoComOauth2Flow build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			return this.onConstruction;
		}

		public Builder withConfigurationProvider( final IConfigurationService configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}
	}
}
