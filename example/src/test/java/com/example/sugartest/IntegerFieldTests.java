package com.example.sugartest;


import com.example.models.IntegerFieldAnnotatedModel;
import com.example.models.IntegerFieldExtendedModel;
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
public class IntegerFieldTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void nullIntegerExtendedTest() {
        save(new IntegerFieldExtendedModel());
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        assertNull(model.getInteger());
    }

    @Test
    public void nullIntExtendedTest() {
        save(new IntegerFieldExtendedModel());
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        assertEquals(0, model.getInt());
    }

    @Test
    public void nullIntegerAnnotatedTest() {
        save(new IntegerFieldAnnotatedModel());
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        assertNull(model.getInteger());
    }

    @Test
    public void nullIntAnnotatedTest() {
        save(new IntegerFieldAnnotatedModel());
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        assertEquals(0, model.getInt());
    }

    @Test
    public void integerExtendedTest() {
        Integer integer = new Integer(25);
        save(new IntegerFieldExtendedModel(integer));
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        assertEquals(integer, model.getInteger());
    }

    @Test
    public void intExtendedTest() {
        save(new IntegerFieldExtendedModel(25));
        IntegerFieldExtendedModel model = SugarRecord.findById(IntegerFieldExtendedModel.class, 1);
        assertEquals(25, model.getInt());
    }

    @Test
    public void integerAnnotatedTest() {
        Integer integer = new Integer(25);
        save(new IntegerFieldAnnotatedModel(integer));
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        assertEquals(integer, model.getInteger());
    }

    @Test
    public void intAnnotatedTest() {
        save(new IntegerFieldAnnotatedModel(25));
        IntegerFieldAnnotatedModel model = SugarRecord.findById(IntegerFieldAnnotatedModel.class, 1);
        assertEquals(25, model.getInt());
    }
}
