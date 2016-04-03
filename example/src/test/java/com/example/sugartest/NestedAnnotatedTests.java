package com.example.sugartest;

import com.example.models.NestedAnnotatedModel;
import com.example.models.RelationshipAnnotatedModel;
import com.example.models.SimpleAnnotatedModel;
import com.orm.SugarRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

import static com.orm.SugarRecord.save;
import static org.junit.Assert.assertEquals;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class NestedAnnotatedTests {
	@Before
	public void setUp() throws Exception {
		ShadowLog.stream = System.out;
		//you other setup here
	}

	@Test
	public void emptyDatabaseTest() throws Exception {
		assertEquals(0L, SugarRecord.count(NestedAnnotatedModel.class));
		assertEquals(0L, SugarRecord.count(RelationshipAnnotatedModel.class));
		assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
	}

	@Test
	public void oneSaveTest() throws Exception {
		SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
		save(simple);
		RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
		save(nested);
		save(new NestedAnnotatedModel(nested));
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
		assertEquals(1L, SugarRecord.count(NestedAnnotatedModel.class));
	}

	@Test
	public void twoSameSaveTest() throws Exception {
		SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
		save(simple);
		RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
		save(nested);
		save(new NestedAnnotatedModel(nested));
		save(new NestedAnnotatedModel(nested));
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
		assertEquals(2L, SugarRecord.count(NestedAnnotatedModel.class));
	}

	@Test
	public void twoDifferentSaveTest() throws Exception {
		SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
		save(simple);
		SimpleAnnotatedModel another_simple = new SimpleAnnotatedModel();
		save(another_simple);
		RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
		save(nested);
		RelationshipAnnotatedModel another_nested = new RelationshipAnnotatedModel(another_simple);
		save(another_nested);
		save(new NestedAnnotatedModel(nested));
		save(new NestedAnnotatedModel(another_nested));
		assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(2L, SugarRecord.count(RelationshipAnnotatedModel.class));
		assertEquals(2L, SugarRecord.count(NestedAnnotatedModel.class));
	}

	@Test
	public void manySameSaveTest() throws Exception {
		SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
		save(simple);
		RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
		save(nested);
		for (int i = 1; i <= 100; i++) {
			save(new NestedAnnotatedModel(nested));
		}
		assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
		assertEquals(100L, SugarRecord.count(NestedAnnotatedModel.class));
	}

	@Test
	public void manyDifferentSaveTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
			save(simple);
			RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
			save(nested);
			save(new NestedAnnotatedModel(nested));
		}
		assertEquals(100L, SugarRecord.count(SimpleAnnotatedModel.class));
		assertEquals(100L, SugarRecord.count(RelationshipAnnotatedModel.class));
		assertEquals(100L, SugarRecord.count(NestedAnnotatedModel.class));
	}

	@Test
	public void listAllSameTest() throws Exception {
		SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
		save(simple);
		RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
		save(nested);
		for (int i = 1; i <= 100; i++) {
			save(new NestedAnnotatedModel(nested));
		}
		List<NestedAnnotatedModel> models = SugarRecord.listAll(NestedAnnotatedModel.class);
		assertEquals(100, models.size());
		for (NestedAnnotatedModel model : models) {
			assertEquals(nested.getId(), model.getNested().getId());
			assertEquals(simple.getId(), model.getNested().getSimple().getId());
		}
	}

	@Test
	public void listAllDifferentTest() throws Exception {
		for (int i = 1; i <= 100; i++) {
			SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
			save(simple);
			RelationshipAnnotatedModel nested = new RelationshipAnnotatedModel(simple);
			save(nested);
			save(new NestedAnnotatedModel(nested));
		}
		List<NestedAnnotatedModel> models = SugarRecord.listAll(NestedAnnotatedModel.class);
		assertEquals(100, models.size());
		for (NestedAnnotatedModel model : models) {
			assertEquals(model.getId(), model.getNested().getId());
			assertEquals(model.getId(), model.getNested().getSimple().getId());
		}
	}
}
