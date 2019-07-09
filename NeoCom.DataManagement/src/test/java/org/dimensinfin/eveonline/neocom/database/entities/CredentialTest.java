package org.dimensinfin.eveonline.neocom.database.entities;

import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CredentialTest {
	//	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors(Credential.class);
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Credential.class).usingGetClass().verify();
	}

	@Test
	public void build_complete() {
		final Credential credential = new Credential.Builder(123456)
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
		Assert.assertNotNull(credential);
		Assert.assertEquals("Verify some credential fields. uniqueId", "tranquility/234567", credential.getUniqueId());
		Assert.assertEquals("Verify some credential fields. wallet balance", new Double(876567.54), credential.getWalletBalance());
	}

	@Test(expected = NullPointerException.class)
	public void build_incomplete() {
		final Credential credential = new Credential.Builder(4321)
				                              .build();
		Assert.assertNotNull(credential);
	}

	@Test
	public void build_checkclass() {
		final Credential credential = new Credential.Builder(123456)
				                              .withAccountId(234567)
				                              .withAccountName("TEST CREDENTIAL")
				                              .build();
		Assert.assertNotNull(credential);
		Assert.assertEquals("The instance class should be: Credential", "Credential", credential.getJsonClass());
	}

	@Test
	public void checkToString() {
		final Credential credential = new Credential.Builder(123456)
				                              .withAccountId(234567)
				                              .withAccountName("TEST CREDENTIAL")
				                              .withDataSource("TESTING")
				                              .withAssetsCount(98)
				                              .withWalletBalance(876567.54)
				                              .withRaceName("TEST RACE")
				                              .build();
		Assert.assertNotNull(credential);
		final String expected = "{\"jsonClass\":\"Credential\",\"uniqueCredential\":\"tranquility/234567\",\"walletBalance\":876567.54,\"assetsCount\":98,\"accountName\":\"TEST CREDENTIAL\",\"raceName\":\"TEST RACE\"}";
		final String obtained = credential.toString();
		Assert.assertEquals("The instance print result should match.", expected, obtained);
	}
}

