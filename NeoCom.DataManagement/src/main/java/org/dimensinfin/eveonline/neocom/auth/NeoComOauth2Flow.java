package org.dimensinfin.eveonline.neocom.auth;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import org.dimensinfin.eveonline.neocom.provider.IConfigurationProvider;
import org.dimensinfin.eveonline.neocom.service.logger.NeoComLogger;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class NeoComOauth2Flow {
	private TokenVerification tokenVerificationStore;
	// - C O M P O N E N T S
	private IConfigurationProvider configurationProvider;

	private NeoComOauth2Flow() {}

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
	private boolean validateStateMatch( final String state ) {
		final String testState = Base64.getEncoder().encodeToString(
				this.configurationProvider.getResourceString( "P.esi.authorization.state" ).getBytes()
		).replaceAll( "\n", "" );
		return state.equals( testState );
	}
//	public ResponseEntity<ValidateAuthorizationTokenResponse> validateAuthorizationToken( final ValidateAuthorizationTokenRequest validateAuthorizationTokenRequest ) {
////		logger.info( ">> [AuthorizationService.validateAuthorizationToken]" );
////		final Instant timer = Instant.now();
////		this.validateStateMatch( validateAuthorizationTokenRequest.getState() );
//
//		// Create the authentication process data store
////		String dataSource = ESIDataProvider.DEFAULT_ESI_SERVER; // Set the default server identifier.
////		if (validateAuthorizationTokenRequest.getDataSource().isPresent())
////			dataSource = validateAuthorizationTokenRequest.getDataSource().get();
////		final TokenVerification tokenVerificationStore = new TokenVerification()
////				.setAuthCode( validateAuthorizationTokenRequest.getCode() )
////				.setDataSource( dataSource.toLowerCase() );
//
//		// Create the conversion call.
////		logger.info( "-- [AuthorizationService.validateAuthorizationToken]> Preparing Verification HTTP request." );
////		logger.info( "-- [AuthorizationService.validateAuthorizationToken]> Creating access token request." );
////		tokenVerificationStore.setTokenTranslationResponse( this.getTokenTranslationResponse( tokenVerificationStore ) );
//		// Create a security verification instance.
////		tokenVerificationStore.setVerifyCharacterResponse( this.getVerifyCharacterResponse( tokenVerificationStore ) );
////		final GetCharactersCharacterIdOk pilotData = this.esiDataProvider.getCharactersCharacterId(
////				tokenVerificationStore.getAccountIdentifier() );
//
////		logger.info( "-- [AuthorizationService.validateAuthorizationToken]> Creating Credential..." );
////		final TokenTranslationResponse token = tokenVerificationStore.getTokenTranslationResponse();
////		final Credential credential = new Credential.Builder( tokenVerificationStore.getAccountIdentifier() )
////				.withAccountId( tokenVerificationStore.getAccountIdentifier() )
////				.withAccountName( tokenVerificationStore.getVerifyCharacterResponse().getCharacterName() )
////				.withCorporationId( pilotData.getCorporationId() )
////				.withTokenType( token.getTokenType() )
////				.withAccessToken( token.getAccessToken() )
////				.withRefreshToken( token.getRefreshToken() )
////				.withDataSource( tokenVerificationStore.getDataSource() )
////				.withScope( this.getVerifyCharacterResponse(tokenVerificationStore).getScopes() )
////				.build();
////		try {
////			logger.info( "-- [AuthorizationService.validateAuthorizationToken]> accountName length {}-{}",
////					credential.getAccountName().length(), credential.getAccountName() );
////			logger.info( "-- [AuthorizationService.validateAuthorizationToken]> accessToken length {}-{}",
////					credential.getAccessToken().length(), credential.getAccessToken() );
////			logger.info( "-- [AuthorizationService.validateAuthorizationToken]> scope length {}-{}",
////					credential.getScope().length(), credential.getScope() );
////			logger.info( "-- [AuthorizationService.validateAuthorizationToken]> refreshToken length {}-{}",
////					credential.getRefreshToken().length(), credential.getRefreshToken() );
////			this.credentialRepository.persist( credential );
////
////			logger.info( "-- [AuthorizationService.validateAuthorizationToken]> Credential #{}-{} created successfully.",
////					credential.getAccountId(), credential.getAccountName() );
////			// TODO - Seems the updated enters endless loop. Review later.
//////			UpdaterJobManager.submit( new CredentialUpdater( credential ) ); // Post the update request to the scheduler.
////			final String jwtToken = JWT.create()
////					.withIssuer( ISSUER )
////					.withSubject( SUBJECT )
////					.withClaim( TOKEN_UNIQUE_IDENTIFIER_FIELD_NAME, credential.getUniqueId() )
////					.withClaim( TOKEN_ACCOUNT_NAME_FIELD_NAME, credential.getAccountName() )
////					.withClaim( TOKEN_CORPORATION_ID_FIELD_NAME, pilotData.getCorporationId() )
////					.withClaim( TOKEN_PILOT_ID_FIELD_NAME, credential.getAccountId() )
////					.sign( Algorithm.HMAC512( SECRET ) );
////			return new ResponseEntity( new ValidateAuthorizationTokenResponse.Builder()
////					.withCredential( credential )
////					.withJwtToken( jwtToken )
////					.build(), HttpStatus.OK );
////		} catch (final SQLException | UnsupportedEncodingException sqle) {
////			sqle.printStackTrace();
////			logger.info( "-- [AuthorizationService.validateAuthorizationToken]> Response is {} - {}.",
////					HttpStatus.BAD_REQUEST, sqle.getMessage() );
////			throw new NeoComSBException( sqle );
////		}
//	}

	private TokenTranslationResponse getTokenTranslationResponse( final TokenVerification store ) {
		// Preload configuration variables.
		final String esiServer = store.getDataSource();
		final String authorizationServer = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.server" );
		final String authorizationClientid = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.clientid" );
		final String authorizationSecretKey = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.secretkey" );
		final String authorizationContentType = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.content.type" );
		final String esiServerLoginUrl = this.configurationProvider.getResourceString(
				"P.esi." + esiServer + ".authorization.server.url" );
		// Get the request.
		final GetAccessToken serviceGetAccessToken = new Retrofit.Builder()
				.baseUrl( authorizationServer )
				.addConverterFactory( JacksonConverterFactory.create() )
				.build()
				.create( GetAccessToken.class );
		final TokenRequestBody tokenRequestBody = new TokenRequestBody().setCode( store.getAuthCode() );
		NeoComLogger.info( "Creating request call." );
		final String peckString = authorizationClientid + ":" + authorizationSecretKey;
		String peck = Base64.getEncoder().encodeToString( peckString.getBytes() ).replaceAll( "\n", "" );
		store.setPeck( peck );
		final Call<TokenTranslationResponse> request = serviceGetAccessToken.getAccessToken(
				"Basic " + peck, authorizationContentType,
				esiServerLoginUrl,
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

		public NeoComOauth2Flow.Builder withConfigurationProvider( final IConfigurationProvider configurationProvider ) {
			Objects.requireNonNull( configurationProvider );
			this.onConstruction.configurationProvider = configurationProvider;
			return this;
		}

		public NeoComOauth2Flow build() {
			Objects.requireNonNull( this.onConstruction.configurationProvider );
			return this.onConstruction;
		}
	}
}