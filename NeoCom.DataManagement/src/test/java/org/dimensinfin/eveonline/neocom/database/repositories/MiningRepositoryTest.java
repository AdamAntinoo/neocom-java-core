package org.dimensinfin.eveonline.neocom.database.repositories;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.support.EsiDataAdapterSupportTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class MiningRepositoryTest extends EsiDataAdapterSupportTest {
	private static final List<MiningExtraction> miningExtractionList = new ArrayList();
	private static MiningExtraction miningExtraction;
	private static Dao<MiningExtraction, String> dao;

	@Before
	public void setUp() {
		super.setUp();
		miningExtractionList.clear();
		miningExtraction = new MiningExtraction.Builder()
				                   .withTypeId(34)
				                   .withSolarSystemId(30001647)
				                   .withQuantity(12345)
				                   .withOwnerId(92223647)
				                   .withExtractionDate(new LocalDate())
				                   .build();
		miningExtractionList.add(miningExtraction);
		dao = Mockito.mock(Dao.class);
		EveItem.injectEsiDataAdapter(this.esiDataAdapter);
	}

	@Test
	public void miningRepositoryBuilder() {
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Assert.assertNotNull(repository);
	}

	@Test(expected = NullPointerException.class)
	public void miningRepositoryBuilder_incomplete() {
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .build();
	}

	@Test
	public void accessTodayMiningExtractions4Pilot() throws SQLException {
		final QueryBuilder builder = Mockito.mock(QueryBuilder.class);
		final Where where = Mockito.mock(Where.class);
		final PreparedQuery query = Mockito.mock(PreparedQuery.class);
		final Credential credential = Mockito.mock(Credential.class);
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Mockito.when(dao.queryBuilder()).thenReturn(builder);
		Mockito.when(builder.where()).thenReturn(where);
		Mockito.when(builder.orderBy(any(String.class), any(Boolean.class))).thenReturn(builder);
		Mockito.doAnswer(( call ) -> {
			final Object parameter = call.getArgument(0);
			Assert.assertNotNull(parameter);
			return null;
		}).when(where).eq(any(String.class), any(Integer.class));
		Mockito.when(builder.prepare()).thenReturn(query);
		Mockito.when(dao.query(any(PreparedQuery.class))).thenReturn(miningExtractionList);
		Mockito.when(credential.getAccountId()).thenReturn(123);
		final List<MiningExtraction> obtained = repository.accessTodayMiningExtractions4Pilot(credential);
		Assert.assertEquals("The number of records is 1.", 1, obtained.size());
	}

	@Test
	public void accessMiningExtractions4Pilot() throws SQLException {
		final QueryBuilder builder = Mockito.mock(QueryBuilder.class);
		final Where where = Mockito.mock(Where.class);
		final PreparedQuery query = Mockito.mock(PreparedQuery.class);
		final Credential credential = Mockito.mock(Credential.class);
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Mockito.when(dao.queryBuilder()).thenReturn(builder);
		Mockito.when(builder.where()).thenReturn(where);
		Mockito.when(builder.orderBy(any(String.class), any(Boolean.class))).thenReturn(builder);
		Mockito.when(builder.prepare()).thenReturn(query);
		Mockito.when(dao.query(any(PreparedQuery.class))).thenReturn(miningExtractionList);
		final List<MiningExtraction> obtained = repository.accessMiningExtractions4Pilot(credential);
		Assert.assertEquals("The number of records is 1.", 1, obtained.size());
	}

	@Test
	public void accessMiningExtractionFindById_found() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Mockito.when(dao.queryForId(any(String.class))).thenReturn(miningExtraction);
		final MiningExtraction obtained = repository.accessMiningExtractionFindById("TEST-LOCATOR");
		Assert.assertEquals("The extraction is the same.", miningExtraction, obtained);
	}

	@Test
	public void accessMiningExtractionFindById_notfound() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Mockito.when(dao.queryForId(any(String.class))).thenReturn(null);
		final MiningExtraction obtained = repository.accessMiningExtractionFindById("TEST-LOCATOR");
		Assert.assertNull("The extraction was not found.", obtained);
	}

	@Test(expected = SQLException.class)
	public void accessMiningExtractionFindById_exception() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Mockito.when(dao.queryForId(any(String.class))).thenThrow(SQLException.class);
		final MiningExtraction obtained = repository.accessMiningExtractionFindById("TEST-LOCATOR");
	}

	@Test
	public void persist() throws SQLException {
		final MiningRepository repository = new MiningRepository.Builder()
				                                    .withMiningExtractionDao(dao)
				                                    .build();
		Mockito.doAnswer(( call ) -> {
			final MiningExtraction parameter = call.getArgument(0);
			Assert.assertNotNull(parameter);
			return null;
		}).when(dao).createOrUpdate(any(MiningExtraction.class));
		repository.persist(miningExtraction);
		Mockito.verify(dao, times(1)).createOrUpdate(any(MiningExtraction.class));
	}
}
