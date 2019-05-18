package org.dimensinfin.eveonline.neocom.entities;

import org.junit.Assert;
import org.junit.Test;

public class CredentialTest {
	/**
	 * Be sure that the data is stored in lowercase because that is the format expected on the OAuth interaction.
	 */
	@Test
	public void dataSource_lowercase_BUG() {
		final Credential credential = new Credential(123)
				                              .setDataSource("Tranquility");
		final String obtained = credential.getDataSource();
		Assert.assertEquals("tranquility", obtained);
	}
}
