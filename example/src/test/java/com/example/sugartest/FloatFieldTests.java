package com.example.sugartest;


import com.example.models.FloatFieldAnnotatedModel;
import com.example.models.FloatFieldExtendedModel;
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
public class FloatFieldTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void nullFloatExtendedTest() {
        save(new FloatFieldExtendedModel());
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        assertNull(model.getFloat());
    }

    @Test
    public void nullRawFloatExtendedTest() {
        save(new FloatFieldExtendedModel());
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        assertEquals(0F, model.getRawFloat(), 0.0000000001F);
    }

    @Test
    public void nullFloatAnnotatedTest() {
        save(new FloatFieldAnnotatedModel());
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        assertNull(model.getFloat());
    }

    @Test
    public void nullRawFloatAnnotatedTest() {
        save(new FloatFieldAnnotatedModel());
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        assertEquals(0F, model.getRawFloat(), 0.0000000001F);
    }

    @Test
    public void objectFloatExtendedTest() {
        Float objectFloat = new Float(25F);
        save(new FloatFieldExtendedModel(objectFloat));
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        assertEquals(objectFloat, model.getFloat());
    }

    @Test
    public void rawFloatExtendedTest() {
        save(new FloatFieldExtendedModel(25F));
        FloatFieldExtendedModel model = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        assertEquals(25F, model.getRawFloat(), 0.0000000001F);
    }

    @Test
    public void objectFloatAnnotatedTest() {
        Float objectFloat = new Float(25F);
        save(new FloatFieldAnnotatedModel(objectFloat));
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        assertEquals(objectFloat, model.getFloat());
    }

    @Test
    public void rawFloatAnnotatedTest() {
        save(new FloatFieldAnnotatedModel(25F));
        FloatFieldAnnotatedModel model = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        assertEquals(25F, model.getRawFloat(), 0.0000000001F);
    }
}
