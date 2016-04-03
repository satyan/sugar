package com.example.sugartest;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.example.models.SimpleAnnotatedModel;
import com.orm.Configuration;
import com.orm.SugarRecord;
import com.orm.util.NamingHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.orm.SugarRecord.save;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class SimpleAnnotatedModelTests {

	private Configuration config = Configuration.get(null);

	private String idName() {
		return config.getIdColumnName();
	}

	@NonNull
	private String id() {
		return idName() + " = ?";
	}

	@Before
	public void setUp() throws Exception {
		ShadowLog.stream = System.out;
		//you other setup here
	}




	@Test
	public void emptyDatabaseTest() throws Exception {
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void oneSaveTest() throws Exception {
		save(new SimpleAnnotatedModel());
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void twoSaveTest() throws Exception {
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void manySaveTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			save(new SimpleAnnotatedModel());
		}
		assertEquals(100L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void defaultIdTest() throws Exception {
		assertEquals(1L, save(new SimpleAnnotatedModel()));
	}

	@Test
	public void whereCountTest() throws Exception {
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		assertEquals(1L, SugarRecord
				.count(SimpleAnnotatedModel.class, id(), new String[]{"1"}));
	}

	@Test
	public void whereNoCountTest() throws Exception {
		assertEquals(0L, SugarRecord
				.count(SimpleAnnotatedModel.class, id(), new String[]{"1"}));
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		assertEquals(0L, SugarRecord
				.count(SimpleAnnotatedModel.class, id(), new String[]{"3"}));
		assertEquals(0L, SugarRecord
				.count(SimpleAnnotatedModel.class, id(), new String[]{"a"}));
	}

	@Test
	public void whereBrokenCountTest() throws Exception {
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		assertEquals(-1L, SugarRecord
				.count(SimpleAnnotatedModel.class, "di = ?", new String[]{"1"}));
	}

	@Test
	public void deleteTest() throws Exception {
		SimpleAnnotatedModel model = new SimpleAnnotatedModel();
		save(model);
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertTrue(SugarRecord.delete(model));
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void deleteUnsavedTest() throws Exception {
		SimpleAnnotatedModel model = new SimpleAnnotatedModel();
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertFalse(SugarRecord.delete(model));
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void deleteWrongTest() throws Exception {
		SimpleAnnotatedModel model = new SimpleAnnotatedModel();
		save(model);
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
		Field idField = model.getClass().getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(model, Long.MAX_VALUE);
		assertFalse(SugarRecord.delete(model));
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void deleteAllTest() throws Exception {
		int elementNumber = 100;
		for (int i = 1; i <= elementNumber; i++) {
			save(new SimpleAnnotatedModel());
		}
		assertEquals(elementNumber, SugarRecord.deleteAll(SimpleAnnotatedModel.class));
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void deleteAllWhereTest() throws Exception {
		int elementNumber = 100;
		for (int i = 1; i <= elementNumber; i++) {
			save(new SimpleAnnotatedModel());
		}
		assertEquals(elementNumber - 1, SugarRecord.deleteAll(SimpleAnnotatedModel.class,
				idName() + " > ?",
				new String[]{"1"}));
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
	}
	

	@Test
	public void deleteInTransactionFewTest() throws Exception {
		SimpleAnnotatedModel first = new SimpleAnnotatedModel();
		SimpleAnnotatedModel second = new SimpleAnnotatedModel();
		SimpleAnnotatedModel third = new SimpleAnnotatedModel();
		save(first);
		save(second);
		// Not saving last model
		assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(2, SugarRecord.deleteInTx(first, second, third));
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void deleteInTransactionManyTest() throws Exception {
		long elementNumber = 100;
		List<SimpleAnnotatedModel> models = new ArrayList<>();
		for (int i = 1; i <= elementNumber; i++) {
			SimpleAnnotatedModel model = new SimpleAnnotatedModel();
			models.add(model);
			// Not saving last model
			if (i < elementNumber) {
				save(model);
			}
		}
		assertEquals(elementNumber - 1, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(elementNumber - 1, SugarRecord.deleteInTx(models));
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void saveInTransactionTest() throws Exception {
		SugarRecord.saveInTx(new SimpleAnnotatedModel(), new SimpleAnnotatedModel());
		assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void listAllTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			save(new SimpleAnnotatedModel());
		}
		List<SimpleAnnotatedModel> models = SugarRecord.listAll(SimpleAnnotatedModel.class);
		assertEquals(100, models.size());
		for (long i = 1; i <= 100; i++) {
			assertEquals(new Long(i), models.get((int) i - 1).getId());
		}
	}

	@Test
	public void findTest() throws Exception {
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		List<SimpleAnnotatedModel> models =
				SugarRecord.find(SimpleAnnotatedModel.class, id(), "2");
		assertEquals(1, models.size());
		assertEquals(new Long(2L), models.get(0).getId());
	}

	@Test
	public void findWithQueryTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			save(new SimpleAnnotatedModel());
		}
		List<SimpleAnnotatedModel> models =
				SugarRecord.findWithQuery(SimpleAnnotatedModel.class, "Select * from " +
																	  NamingHelper
																			  .toSQLName(config, SimpleAnnotatedModel.class) +
																	  " where " + idName() +
																	  " >= ? ", "50");
		for (SimpleAnnotatedModel model : models) {
			assertEquals(new Long(75), model.getId(), 25L);
		}
	}

	@Test
	public void findByIdTest() throws Exception {
		save(new SimpleAnnotatedModel());
		assertEquals(new Long(1L), SugarRecord.findById(SimpleAnnotatedModel.class, 1L).getId());
	}

	@Test
	public void findByIdIntegerTest() throws Exception {
		save(new SimpleAnnotatedModel());
		assertEquals(new Long(1L), SugarRecord.findById(SimpleAnnotatedModel.class, 1).getId());
	}

	@Test
	public void findByIdStringsNullTest() throws Exception {
		save(new SimpleAnnotatedModel());
		assertEquals(0, SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{""}).size());
	}

	@Test
	public void findByIdStringsOneTest() throws Exception {
		save(new SimpleAnnotatedModel());
		List<SimpleAnnotatedModel> models =
				SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{"1"});
		assertEquals(1, models.size());
		assertEquals(new Long(1L), models.get(0).getId());
	}

	@Test
	public void findByIdStringsTwoTest() throws Exception {
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		save(new SimpleAnnotatedModel());
		List<SimpleAnnotatedModel> models =
				SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{"1", "3"});
		assertEquals(2, models.size());
		assertEquals(new Long(1L), models.get(0).getId());
		assertEquals(new Long(3L), models.get(1).getId());
	}

	@Test
	public void findByIdStringsManyTest() throws Exception {
		for (int i = 1; i <= 10; i++) {
			save(new SimpleAnnotatedModel());
		}
		List<SimpleAnnotatedModel> models =
				SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{"1", "3", "6", "10"});
		assertEquals(4, models.size());
		assertEquals(new Long(1L), models.get(0).getId());
		assertEquals(new Long(3L), models.get(1).getId());
		assertEquals(new Long(6L), models.get(2).getId());
		assertEquals(new Long(10L), models.get(3).getId());
	}

	@Test
	public void findByIdStringsOrderTest() throws Exception {
		for (int i = 1; i <= 10; i++) {
			save(new SimpleAnnotatedModel());
		}
		List<SimpleAnnotatedModel> models =
				SugarRecord.findById(SimpleAnnotatedModel.class, new String[]{"10", "6", "3", "1"});
		assertEquals(4, models.size());
		// The order of the query doesn't matter
		assertEquals(new Long(1L), models.get(0).getId());
		assertEquals(new Long(3L), models.get(1).getId());
		assertEquals(new Long(6L), models.get(2).getId());
		assertEquals(new Long(10L), models.get(3).getId());
	}

	@Test
	public void findByIdNullTest() throws Exception {
		save(new SimpleAnnotatedModel());
		assertNull(SugarRecord.findById(SimpleAnnotatedModel.class, 2L));
	}

	@Test
	public void findAllTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			save(new SimpleAnnotatedModel());
		}
		Iterator<SimpleAnnotatedModel> cursor = SugarRecord.findAll(SimpleAnnotatedModel.class);
		for (int i = 1; i <= 100; i++) {
			assertTrue(cursor.hasNext());
			SimpleAnnotatedModel model = cursor.next();
			assertNotNull(model);
			assertEquals(new Long(i), model.getId());
		}
	}

	@Test
	public void findAsIteratorTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			save(new SimpleAnnotatedModel());
		}
		Iterator<SimpleAnnotatedModel> cursor = SugarRecord
				.findAsIterator(SimpleAnnotatedModel.class,
						idName() + " >= ?", "50");
		for (int i = 50; i <= 100; i++) {
			assertTrue(cursor.hasNext());
			SimpleAnnotatedModel model = cursor.next();
			assertNotNull(model);
			assertEquals(new Long(i), model.getId());
		}
	}

	@Test
	public void findWithQueryAsIteratorTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			save(new SimpleAnnotatedModel());
		}
		Iterator<SimpleAnnotatedModel> cursor =
				SugarRecord.findWithQueryAsIterator(SimpleAnnotatedModel.class,
						"Select * from " +
						NamingHelper.toSQLName(config, SimpleAnnotatedModel.class) +
						" where " + idName() + " >= ? ", "50");
		for (int i = 50; i <= 100; i++) {
			assertTrue(cursor.hasNext());
			SimpleAnnotatedModel model = cursor.next();
			assertNotNull(model);
			assertEquals(new Long(i), model.getId());
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void findAsIteratorOutOfBoundsTest() throws Exception {
		save(new SimpleAnnotatedModel());
		Iterator<SimpleAnnotatedModel> cursor = SugarRecord
				.findAsIterator(SimpleAnnotatedModel.class,
						id(), "1");
		assertTrue(cursor.hasNext());
		SimpleAnnotatedModel model = cursor.next();
		assertNotNull(model);
		assertEquals(new Long(1), model.getId());
		// This should throw a NoSuchElementException
		cursor.next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void disallowRemoveCursorTest() throws Exception {
		save(new SimpleAnnotatedModel());
		Iterator<SimpleAnnotatedModel> cursor = SugarRecord
				.findAsIterator(SimpleAnnotatedModel.class,
						id(), "1");
		assertTrue(cursor.hasNext());
		SimpleAnnotatedModel model = cursor.next();
		assertNotNull(model);
		assertEquals(new Long(1), model.getId());
		// This should throw a UnsupportedOperationException
		cursor.remove();
	}



	@Test
	public void vacuumTest() throws Exception {
		SugarRecord.executeQuery("Vacuum");
	}
}