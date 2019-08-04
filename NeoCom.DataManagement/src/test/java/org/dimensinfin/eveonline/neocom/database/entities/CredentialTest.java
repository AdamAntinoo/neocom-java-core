package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Assert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CredentialTest {
	@Test
	public void gettersContract() {
		final Credential credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
				.withDataSource("Tranquility")
				.withScope("SCOPE")
				.withAssetsCount(98)
				.withWalletBalance(876567.54)
				.withMiningResourcesEstimatedValue(123456789.98)
				.withRaceName("TEST RACE")
				.build();
		Assert.assertNotNull(credential);
		Assert.assertEquals(234567, credential.getAccountId());
		Assert.assertEquals("TEST CREDENTIAL", credential.getAccountName());
		Assert.assertEquals("-TEST INVALID ACCESS TOKEN-", credential.getAccessToken());
		Assert.assertEquals("-TEST INVALID ACCESS TOKEN-", credential.getRefreshToken());
		Assert.assertEquals("Tranquility".toLowerCase(), credential.getDataSource());
		Assert.assertEquals("SCOPE", credential.getScope());
		Assert.assertEquals(98, credential.getAssetsCount());
		Assert.assertEquals(876567.54, credential.getWalletBalance(), 0.1);
		Assert.assertEquals(123456789.98, credential.getMiningResourcesEstimatedValue(), 0.1);
		Assert.assertEquals("TEST RACE", credential.getRaceName());

		Assert.assertEquals("tranquility/234567", credential.getUniqueId());
		Assert.assertEquals("TEST CREDENTIAL", credential.getName());
	}

	@Test
	public void setterContract() {
		final Credential credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
				.withDataSource("Tranquility")
				.withScope("SCOPE")
				.withAssetsCount(98)
				.withWalletBalance(876567.54)
				.withMiningResourcesEstimatedValue(123456789.98)
				.withRaceName("TEST RACE")
				.build();
		credential.setAccountId(654321);
		Assert.assertEquals(654321, credential.getAccountId());
		credential.setDataSource("Testing");
		Assert.assertEquals("Testing".toLowerCase(), credential.getDataSource());
		credential.setWalletBalance(123456789.98);
		Assert.assertEquals(123456789.98, credential.getWalletBalance(),0.1);
		credential.setMiningResourcesEstimatedValue(123456789.98);
		Assert.assertEquals(123456789.98, credential.getMiningResourcesEstimatedValue(),0.1);
		credential.setAssetsCount(12);
		Assert.assertEquals(12, credential.getAssetsCount(),0.1);
		credential.setRaceName("Amarr");
		Assert.assertEquals("Amarr", credential.getRaceName());
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Credential.class)
		              .withIgnoredFields("creationTime")
		              .usingGetClass().verify();
	}

	@Test
	public void checkToStringContract() {
		final Credential credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withDataSource("TESTING")
				.withAssetsCount(98)
				.withWalletBalance(876567.54)
				.withRaceName("TEST RACE")
				.build();
		Assert.assertNotNull(credential);
		final String expected = "{\"jsonClass\":\"Credential\",\"uniqueCredential\":\"tranquility/234567\"," +
				"\"walletBalance\":876567.54,\"assetsCount\":98,\"miningResourcesEstimatedValue\":0.0,\"accountName\":\"TEST " +
				"CREDENTIAL\",\"raceName\":\"TEST RACE\"}";
		final String obtained = credential.toString();
		Assert.assertEquals("The instance print result should match.", expected, obtained);
	}

	@Test
	public void constructor() {
		final Credential credential = new Credential();
		Assert.assertNotNull(credential);
	}

	@Test
	public void build_complete() {
		final Credential credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
				.withTokenType("Bearer")
				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
				.withDataSource("Tranquility")
				.withScope("SCOPE")
				.withAssetsCount(98)
				.withWalletBalance(876567.54)
				.withMiningResourcesEstimatedValue(123456789.98)
				.withRaceName("TEST RACE")
				.build();
		Assert.assertNotNull(credential);
		Assert.assertEquals("Verify some credential fields. uniqueId", "tranquility/234567", credential.getUniqueId());
		Assert.assertEquals("Verify some credential fields. assets count", 98, credential.getAssetsCount());
		Assert.assertEquals("Verify some credential fields. wallet balance", new Double(876567.54), credential.getWalletBalance());
		Assert.assertEquals("Verify some credential fields. mining resources estimated value", new Double(123456789.98),
				credential.getMiningResourcesEstimatedValue());
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
	public void isValid_ok() {
		final Credential credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
				.withDataSource("Tranquility")
				.build();
		Assert.assertNotNull(credential);
		Assert.assertTrue(credential.isValid());
	}

	@Test
	public void isValid_failure() {
		final Credential credential = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.build();
		Assert.assertNotNull(credential);
		Assert.assertFalse(credential.isValid());
	}
}

