package com.example.sugartest;


import com.example.models.StringFieldAnnotatedModel;
import com.example.models.StringFieldAnnotatedNoIdModel;
import com.example.models.StringFieldExtendedModel;
import com.orm.SugarRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static com.orm.SugarRecord.save;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class MultipleSaveTests {
	@Before
	public void setUp() throws Exception {
		ShadowLog.stream = System.out;
		//you other setup here
	}

	@Test
	public void stringMultipleSaveOriginalExtendedTest() {
		String string = "Test String";
		StringFieldExtendedModel model = new StringFieldExtendedModel(string);
		long id = save(model);
		StringFieldExtendedModel query = SugarRecord.findById(StringFieldExtendedModel.class, id);
		assertEquals(string, query.getString());
		model.setString("Another test");
		assertEquals(id, save(model));
		assertNull(SugarRecord.findById(StringFieldExtendedModel.class, 2));
	}

	@Test
	public void stringMultipleSaveQueriedExtendedTest() {
		String string = "Test String";
		StringFieldExtendedModel model = new StringFieldExtendedModel(string);
		long id = save(model);
		StringFieldExtendedModel query = SugarRecord.findById(StringFieldExtendedModel.class, id);
		assertEquals(string, query.getString());
		query.setString("Another test");
		assertEquals(id, save(query));
		assertNull(SugarRecord.findById(StringFieldExtendedModel.class, 2));
	}

	@Test
	public void stringMultipleSaveOriginalAnnotatedTest() {
		String string = "Test String";
		StringFieldAnnotatedModel model = new StringFieldAnnotatedModel(string);
		long id = save(model);
		StringFieldAnnotatedModel query = SugarRecord.findById(StringFieldAnnotatedModel.class, id);
		assertEquals(string, query.getString());
		model.setString("Another test");
		assertEquals(id, save(model));
		assertNull(SugarRecord.findById(StringFieldAnnotatedModel.class, 2));
	}

	@Test
	public void stringMultipleSaveQueriedAnnotatedTest() {
		String string = "Test String";
		StringFieldAnnotatedModel model = new StringFieldAnnotatedModel(string);
		long id = save(model);
		StringFieldAnnotatedModel query = SugarRecord.findById(StringFieldAnnotatedModel.class, id);
		assertEquals(string, query.getString());
		query.setString("Another test");
		assertEquals(id, save(query));
		assertNull(SugarRecord.findById(StringFieldAnnotatedModel.class, 2));
	}

	@Test
	public void stringMultipleSaveOriginalAnnotatedNoIdTest() {
		String string = "Test String";
		StringFieldAnnotatedNoIdModel model = new StringFieldAnnotatedNoIdModel(string);
		long id = save(model);
		StringFieldAnnotatedNoIdModel query =
				SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, id);
		assertEquals(string, query.getString());
		model.setString("Another test");
		assertEquals(id, save(model));
		assertNull(SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, 2));
	}

	@Test
	public void stringMultipleSaveQueriedAnnotatedNoIdTest() {
		String string = "Test String";
		StringFieldAnnotatedNoIdModel model = new StringFieldAnnotatedNoIdModel(string);
		long id = save(model);
		StringFieldAnnotatedNoIdModel query =
				SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, id);
		assertEquals(string, query.getString());
		query.setString("Another test");
		assertEquals(id, save(query));
		assertNull(SugarRecord.findById(StringFieldAnnotatedNoIdModel.class, 2));
	}
}
