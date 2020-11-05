package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;

public class TestDataConstants {
	public static class ESIDataServiceConstants {
		public static final Integer TEST_CHARACTER_IDENTIFIER = 92223647;
	}

	public static class CredentialConstants {
		public static final Integer TEST_CREDENTIAL_ACCOUNT_ID = 92223647;
	}

	public static class RetrofitFactoryConstants {
		public static final String TEST_RETROFIT_BASE_URL = "http://localhost";
		public static final String TEST_RETROFIT_AGENT = "-TEST_RETROFIT_AGENT-";
		public static final Integer TEST_RETROFIT_TIMEOUT = 10;
	}

	public static class EsiUniverseTestDataConstants {
		public static final GetCharactersCharacterIdOk TEST_ESI_CHARACTER_DATA = new GetCharactersCharacterIdOk();
		public static final String TEST_ESI_CHARACTER_NAME = "-TEST_ESI_CHARACTER_NAME-";
		public static final String TEST_ESI_CHARACTER_DESCRIPTION = "-TEST_ESI_CHARACTER_DESCRIPTION-";
		public static final Integer TEST_ESI_CHARACTER_CORPORATION_IDENTIFIER = 98384726;
		public static final Integer TEST_ESI_CHARACTER_RACE_IDENTIFIER = 100;
		public static final Integer TEST_ESI_CHARACTER_ANCESTRY_IDENTIFIER = 200;
		public static final Integer TEST_ESI_CHARACTER_BLOODLINE_IDENTIFIER = 300;
		public static final Float TEST_ESI_CHARACTER_SECURITY_STATUS = 0.5F;

		static {
			TEST_ESI_CHARACTER_DATA.setName( TEST_ESI_CHARACTER_NAME );
			TEST_ESI_CHARACTER_DATA.setCorporationId( TEST_ESI_CHARACTER_CORPORATION_IDENTIFIER );
			//			TEST_ESI_CHARACTER_DATA.setBirthday( new DateTime() );
			TEST_ESI_CHARACTER_DATA.setRaceId( TEST_ESI_CHARACTER_RACE_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setAncestryId( TEST_ESI_CHARACTER_ANCESTRY_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setBloodlineId( TEST_ESI_CHARACTER_BLOODLINE_IDENTIFIER );
			TEST_ESI_CHARACTER_DATA.setDescription( TEST_ESI_CHARACTER_DESCRIPTION );
			TEST_ESI_CHARACTER_DATA.setGender( GetCharactersCharacterIdOk.GenderEnum.MALE );
			TEST_ESI_CHARACTER_DATA.setSecurityStatus( TEST_ESI_CHARACTER_SECURITY_STATUS );
		}
	}
}
