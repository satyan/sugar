package com.example.sugartest;


import com.example.models.DoubleFieldAnnotatedModel;
import com.example.models.DoubleFieldExtendedModel;
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
public class DoubleFieldTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void nullDoubleExtendedTest() {
        save(new DoubleFieldExtendedModel());
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        assertNull(model.getDouble());
    }

    @Test
    public void nullRawDoubleExtendedTest() {
        save(new DoubleFieldExtendedModel());
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        assertEquals(0.0, model.getRawDouble(), 0.0000000001);
    }

    @Test
    public void nullDoubleAnnotatedTest() {
        save(new DoubleFieldAnnotatedModel());
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        assertNull(model.getDouble());
    }

    @Test
    public void nullRawDoubleAnnotatedTest() {
        save(new DoubleFieldAnnotatedModel());
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        assertEquals(0.0, model.getRawDouble(), 0.0000000001);
    }

    @Test
    public void objectDoubleExtendedTest() {
        Double objectDouble = new Double(25.0);
        save(new DoubleFieldExtendedModel(objectDouble));
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        assertEquals(objectDouble, model.getDouble());
    }

    @Test
    public void rawDoubleExtendedTest() {
        save(new DoubleFieldExtendedModel(25.0));
        DoubleFieldExtendedModel model = SugarRecord.findById(DoubleFieldExtendedModel.class, 1);
        assertEquals(25.0, model.getRawDouble(), 0.0000000001);
    }

    @Test
    public void objectDoubleAnnotatedTest() {
        Double objectDouble = new Double(25.0);
        save(new DoubleFieldAnnotatedModel(objectDouble));
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        assertEquals(objectDouble, model.getDouble());
    }

    @Test
    public void rawDoubleAnnotatedTest() {
        save(new DoubleFieldAnnotatedModel(25.0));
        DoubleFieldAnnotatedModel model = SugarRecord.findById(DoubleFieldAnnotatedModel.class, 1);
        assertEquals(25.0, model.getRawDouble(), 0.0000000001);
    }
}
