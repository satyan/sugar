package com.example.sugartest;


import com.example.models.LongFieldAnnotatedModel;
import com.example.models.LongFieldExtendedModel;
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
@Config(sdk=18)
public class LongFieldTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void nullLongExtendedTest() {
        save(new LongFieldExtendedModel());
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        assertNull(model.getLong());
    }

    @Test
    public void nullRawLongExtendedTest() {
        save(new LongFieldExtendedModel());
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        assertEquals(0L, model.getRawLong());
    }

    @Test
    public void nullLongAnnotatedTest() {
        save(new LongFieldAnnotatedModel());
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        assertNull(model.getLong());
    }

    @Test
    public void nullRawLongAnnotatedTest() {
        save(new LongFieldAnnotatedModel());
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        assertEquals(0L, model.getRawLong());
    }

    @Test
    public void objectLongExtendedTest() {
        Long objectLong = new Long(25L);
        save(new LongFieldExtendedModel(objectLong));
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        assertEquals(objectLong, model.getLong());
    }

    @Test
    public void rawLongExtendedTest() {
        save(new LongFieldExtendedModel(25L));
        LongFieldExtendedModel model = SugarRecord.findById(LongFieldExtendedModel.class, 1);
        assertEquals(25L, model.getRawLong());
    }

    @Test
    public void objectLongAnnotatedTest() {
        Long objectLong = new Long(25L);
        save(new LongFieldAnnotatedModel(objectLong));
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        assertEquals(objectLong, model.getLong());
    }

    @Test
    public void rawLongAnnotatedTest() {
        save(new LongFieldAnnotatedModel(25L));
        LongFieldAnnotatedModel model = SugarRecord.findById(LongFieldAnnotatedModel.class, 1);
        assertEquals(25L, model.getRawLong());
    }
}
