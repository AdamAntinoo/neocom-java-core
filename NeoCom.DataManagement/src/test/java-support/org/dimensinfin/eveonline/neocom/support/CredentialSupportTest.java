package org.dimensinfin.eveonline.neocom.support;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.junit.Before;

public class CredentialSupportTest extends EsiDataAdapterSupportTest {
	protected Credential credential;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this.credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
				.withDataSource("Tranquility")
				.withScope("SCOPE")
				.withAssetsCount(98)
				.withWalletBalance(876567.54)
				.withRaceName("TEST RACE")
				.build();
	}
}
