package com.example.sugartest;


import android.provider.BaseColumns;

import com.example.models.IntegerFieldExtendedModel;
import com.orm.Configuration;
import com.orm.SugarRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

import static com.orm.SugarRecord.save;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class ListAllOrderByTests {

	private Configuration config = Configuration.get(null);

	@Before
	public void setUp() throws Exception {
		ShadowLog.stream = System.out;
		//you other setup here
	}

	@Test
	public void listAllOrderByEmptyTest() {

		assertEquals(0L, SugarRecord.listAll(IntegerFieldExtendedModel.class, config.getIdColumnName()).size());
	}

	@Test
	public void listAllOrderByIdTest() {
		for (int i = 1; i <= 100; i++) {
			save(new IntegerFieldExtendedModel(i));
		}
		List<IntegerFieldExtendedModel> models =
				SugarRecord.listAll(IntegerFieldExtendedModel.class, config.getIdColumnName());
		assertEquals(100L, models.size());
		Long id = models.get(0).getId();
		for (int i = 1; i < 100; i++) {
			assertTrue(id < models.get(i).getId());
		}
	}

	@Test
	public void listAllOrderByFieldTest() {
		for (int i = 1; i <= 100; i++) {
			save(new IntegerFieldExtendedModel(i));
		}
		List<IntegerFieldExtendedModel> models =
				SugarRecord.listAll(IntegerFieldExtendedModel.class, "raw_integer");
		assertEquals(100L, models.size());
		int raw = models.get(0).getInt();
		for (int i = 1; i < 100; i++) {
			assertTrue(raw < models.get(i).getInt());
		}
	}
}
