package org.dimensinfin.eveonline.neocom.database.entities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class CredentialTest {
	private Credential credential4Test;

	@Before
	public void setUp() throws Exception {
		credential4Test = new Credential.Builder(123456)
				.withAccountId(234567)
				.withAccountName("TEST CREDENTIAL")
				.withCorporationId( 4321987 )
				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
				.withDataSource("Tranquility")
				.withScope("SCOPE")
				.withAssetsCount(98)
				.withWalletBalance(876567.54)
				.withMiningResourcesEstimatedValue(123456789.98)
				.withRaceName("TEST RACE")
				.build();
	}

//	@Test
	public void gettersContract() {
		Assert.assertNotNull(credential4Test);
		Assert.assertEquals(234567, credential4Test.getAccountId().intValue());
		Assert.assertEquals("TEST CREDENTIAL", credential4Test.getAccountName());
		Assert.assertEquals(4321987, credential4Test.getCorporationId());
		Assert.assertEquals("-TEST INVALID ACCESS TOKEN-", credential4Test.getAccessToken());
		Assert.assertEquals("-TEST INVALID ACCESS TOKEN-", credential4Test.getRefreshToken());
		Assert.assertEquals("Tranquility".toLowerCase(), credential4Test.getDataSource());
		Assert.assertEquals("SCOPE", credential4Test.getScope());
		Assert.assertEquals(98, credential4Test.getAssetsCount());
		Assert.assertEquals(876567.54, credential4Test.getWalletBalance(), 0.1);
		Assert.assertEquals(123456789.98, credential4Test.getMiningResourcesEstimatedValue(), 0.1);
		Assert.assertEquals("TEST RACE", credential4Test.getRaceName());

		Assert.assertEquals("tranquility/234567", credential4Test.getUniqueCredential());
		Assert.assertEquals("TEST CREDENTIAL", credential4Test.getName());
	}

	@Test
	public void setterContract() {
		credential4Test.setAccountId(654321);
		Assert.assertEquals(654321, credential4Test.getAccountId().intValue());
		credential4Test.setDataSource("Testing");
		Assert.assertEquals("Testing".toLowerCase(), credential4Test.getDataSource());
		credential4Test.setWalletBalance(123456789.98);
		Assert.assertEquals(123456789.98, credential4Test.getWalletBalance(),0.1);
		credential4Test.setMiningResourcesEstimatedValue(123456789.98);
		Assert.assertEquals(123456789.98, credential4Test.getMiningResourcesEstimatedValue(),0.1);
		credential4Test.setAssetsCount(12);
		Assert.assertEquals(12, credential4Test.getAssetsCount(),0.1);
		credential4Test.setRaceName("Amarr");
		Assert.assertEquals("Amarr", credential4Test.getRaceName());
	}

//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Credential.class)
		              .withIgnoredFields("creationTime","lastUpdateTime")
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
		final String expected = "{\"jsonClass\":\"Credential\",\"uniqueCredential\":\"tranquility.123456\",\"walletBalance\":876567.54," +
				"\"assetsCount\":98,\"miningResourcesEstimatedValue\":0.0,\"accountName\":\"TEST CREDENTIAL\",\"raceName\":\"TEST RACE\"}";
		final String obtained = credential.toString();
		Assert.assertEquals("The instance print result should match.", expected, obtained);
	}

	@Test
	public void constructor() {
		final Credential credential = new Credential();
		Assert.assertNotNull(credential);
	}

//	@Test
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
		Assert.assertEquals("Verify some credential fields. uniqueId", "tranquility/234567", credential.getUniqueCredential());
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

