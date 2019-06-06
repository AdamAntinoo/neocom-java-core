package org.dimensinfin.eveonline.neocom.entities;

import java.sql.SQLException;

import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.interfaces.IGlobalConnector;
import org.dimensinfin.eveonline.neocom.model.ANeoComEntity;

import org.junit.Assert;
import org.mockito.Mockito;

import com.j256.ormlite.dao.Dao;

public class CredentialTest {
	/**
	 * Be sure that the data is stored in lowercase because that is the format expected on the OAuth interaction.
	 */
//	@Test
	public void dataSource_lowercase_BUG() throws SQLException {
//		final ANeoComEntity entity = Mockito.mock(ANeoComEntity.class);
		final IGlobalConnector global = Mockito.mock(IGlobalConnector.class);
		final INeoComDBHelper helper = Mockito.mock(INeoComDBHelper.class);
		final Dao dao = Mockito.mock(Dao.class);
		ANeoComEntity.connectGlobal(global);
		final org.dimensinfin.eveonline.neocom.database.entities.Credential credential = new Credential.Builder(123).build()
				                              .setDataSource("Tranquility");
		Mockito.when(ANeoComEntity.accessGlobal()).thenReturn(global);
		Mockito.when(global.getNeocomDBHelper()).thenReturn(helper);
		Mockito.when(helper.getCredentialDao()).thenReturn(dao);
		final String obtained = credential.getDataSource();
		Assert.assertEquals("tranquility", obtained);
	}
}
