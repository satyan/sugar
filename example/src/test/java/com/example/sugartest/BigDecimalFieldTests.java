package com.example.sugartest;


import com.example.models.BigDecimalFieldAnnotatedModel;
import com.example.models.BigDecimalFieldExtendedModel;
import com.orm.SugarRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18)
public class BigDecimalFieldTests {

	@Before
	public void setUp() throws Exception {
		ShadowLog.stream = System.out;
		//you other setup here
	}

	@Test
	public void nullBigDecimalExtendedTest() {
		SugarRecord.save(new BigDecimalFieldExtendedModel());
		BigDecimalFieldExtendedModel model =
				SugarRecord.findById(BigDecimalFieldExtendedModel.class, 1);
		assertNull(model.getBigDecimal());
	}

	@Test
	public void nullBigDecimalAnnotatedTest() {
		SugarRecord.save(new BigDecimalFieldAnnotatedModel());
		BigDecimalFieldAnnotatedModel model =
				SugarRecord.findById(BigDecimalFieldAnnotatedModel.class, 1);
		assertNull(model.getBigDecimal());
	}

	@Test
	public void bigDecimalExtendedTest() {
		BigDecimal decimal = new BigDecimal(1234.5678901234567890123456789);
		SugarRecord.save(new BigDecimalFieldExtendedModel(decimal));
		BigDecimalFieldExtendedModel model = SugarRecord
				.findById(BigDecimalFieldExtendedModel.class, 1);
		assertEquals(decimal, model.getBigDecimal());
	}

	@Test
	public void bigDecimalAnnotatedTest() {
		BigDecimal decimal = new BigDecimal(1234.5678901234567890123456789);
		SugarRecord.save(new BigDecimalFieldAnnotatedModel(decimal));
		BigDecimalFieldAnnotatedModel model =
				SugarRecord.findById(BigDecimalFieldAnnotatedModel.class, 1);
		assertEquals(decimal, model.getBigDecimal());
	}
}
