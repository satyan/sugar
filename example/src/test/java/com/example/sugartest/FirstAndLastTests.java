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
public class FirstAndLastTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void firstExtendedTest() {
        Float firstObjectFloat = new Float(25F);
        Float lastObjectFloat = new Float(50F);
        save(new FloatFieldExtendedModel(firstObjectFloat));
        save(new FloatFieldExtendedModel(lastObjectFloat));
        FloatFieldExtendedModel model = SugarRecord.first(FloatFieldExtendedModel.class);
        assertEquals(firstObjectFloat, model.getFloat());
    }

    @Test
    public void firstDeletedRecordExtendedTest() {
        Float firstObjectFloat = new Float(15F);
        Float secondObjectFloat = new Float(25F);
        Float thirdObjectFloat = new Float(35F);
        Float fourthObjectFloat = new Float(45F);
        save(new FloatFieldExtendedModel(firstObjectFloat));
        save(new FloatFieldExtendedModel(secondObjectFloat));
        save(new FloatFieldExtendedModel(thirdObjectFloat));
        save(new FloatFieldExtendedModel(fourthObjectFloat));
        FloatFieldExtendedModel firstRecord = SugarRecord.findById(FloatFieldExtendedModel.class, 1);
        firstRecord.delete();
        FloatFieldExtendedModel model = SugarRecord.first(FloatFieldExtendedModel.class);
        assertEquals(secondObjectFloat, model.getFloat());
    }

    @Test
    public void lastExtendedTest() {
        Float firstObjectFloat = new Float(25F);
        Float lastObjectFloat = new Float(50F);
        save(new FloatFieldExtendedModel(firstObjectFloat));
        save(new FloatFieldExtendedModel(lastObjectFloat));
        FloatFieldExtendedModel model = SugarRecord.last(FloatFieldExtendedModel.class);
        assertEquals(lastObjectFloat, model.getFloat());
    }

    @Test
    public void lastDeletedRecordExtendedTest() {
        Float firstObjectFloat = new Float(15F);
        Float secondObjectFloat = new Float(25F);
        Float thirdObjectFloat = new Float(35F);
        Float fourthObjectFloat = new Float(45F);
        save(new FloatFieldExtendedModel(firstObjectFloat));
        save(new FloatFieldExtendedModel(secondObjectFloat));
        save(new FloatFieldExtendedModel(thirdObjectFloat));
        save(new FloatFieldExtendedModel(fourthObjectFloat));
        FloatFieldExtendedModel lastRecord = SugarRecord.findById(FloatFieldExtendedModel.class, 4);
        lastRecord.delete();
        FloatFieldExtendedModel model = SugarRecord.last(FloatFieldExtendedModel.class);
        assertEquals(thirdObjectFloat, model.getFloat());
    }

    @Test
    public void nullFirstExtendedTest() {
        assertNull(SugarRecord.first(FloatFieldExtendedModel.class));
    }

    @Test
    public void nullLastExtendedTest() {
        assertNull(SugarRecord.last(FloatFieldExtendedModel.class));
    }

    @Test
    public void oneItemExtendedTest() {
        save(new FloatFieldExtendedModel(new Float(25F)));
        FloatFieldExtendedModel firstModel = SugarRecord.first(FloatFieldExtendedModel.class);
        FloatFieldExtendedModel lastModel = SugarRecord.last(FloatFieldExtendedModel.class);
        assertEquals(firstModel.getFloat(), lastModel.getFloat());
    }

    @Test
    public void firstAnnotatedTest() {
        Float firstObjectFloat = new Float(25F);
        Float lastObjectFloat = new Float(50F);
        save(new FloatFieldAnnotatedModel(firstObjectFloat));
        save(new FloatFieldAnnotatedModel(lastObjectFloat));
        FloatFieldAnnotatedModel model = SugarRecord.first(FloatFieldAnnotatedModel.class);
        assertEquals(firstObjectFloat, model.getFloat());
    }

    @Test
    public void firstDeletedRecordAnnotatedTest() {
        Float firstObjectFloat = new Float(15F);
        Float secondObjectFloat = new Float(25F);
        Float thirdObjectFloat = new Float(35F);
        Float fourthObjectFloat = new Float(45F);
        save(new FloatFieldAnnotatedModel(firstObjectFloat));
        save(new FloatFieldAnnotatedModel(secondObjectFloat));
        save(new FloatFieldAnnotatedModel(thirdObjectFloat));
        save(new FloatFieldAnnotatedModel(fourthObjectFloat));
        FloatFieldAnnotatedModel firstRecord = SugarRecord.findById(FloatFieldAnnotatedModel.class, 1);
        SugarRecord.delete(firstRecord);
        FloatFieldAnnotatedModel model = SugarRecord.first(FloatFieldAnnotatedModel.class);
        assertEquals(secondObjectFloat, model.getFloat());
    }

    @Test
    public void lastAnnotatedTest() {
        Float firstObjectFloat = new Float(25F);
        Float lastObjectFloat = new Float(50F);
        save(new FloatFieldAnnotatedModel(firstObjectFloat));
        save(new FloatFieldAnnotatedModel(lastObjectFloat));
        FloatFieldAnnotatedModel model = SugarRecord.last(FloatFieldAnnotatedModel.class);
        assertEquals(lastObjectFloat, model.getFloat());
    }

    @Test
    public void lastDeletedRecordAnnotatedTest() {
        Float firstObjectFloat = new Float(15F);
        Float secondObjectFloat = new Float(25F);
        Float thirdObjectFloat = new Float(35F);
        Float fourthObjectFloat = new Float(45F);
        save(new FloatFieldAnnotatedModel(firstObjectFloat));
        save(new FloatFieldAnnotatedModel(secondObjectFloat));
        save(new FloatFieldAnnotatedModel(thirdObjectFloat));
        save(new FloatFieldAnnotatedModel(fourthObjectFloat));
        FloatFieldAnnotatedModel lastRecord = SugarRecord.findById(FloatFieldAnnotatedModel.class, 4);
        SugarRecord.delete(lastRecord);
        FloatFieldAnnotatedModel model = SugarRecord.last(FloatFieldAnnotatedModel.class);
        assertEquals(thirdObjectFloat, model.getFloat());
    }

    @Test
    public void nullFirstAnnotatedTest() {
        assertNull(SugarRecord.first(FloatFieldAnnotatedModel.class));
    }

    @Test
    public void nullLastAnnotatedTest() {
        assertNull(SugarRecord.last(FloatFieldAnnotatedModel.class));
    }

    @Test
    public void oneItemAnnotatedTest() {
        save(new FloatFieldAnnotatedModel(new Float(25F)));
        FloatFieldAnnotatedModel firstModel = SugarRecord.first(FloatFieldAnnotatedModel.class);
        FloatFieldAnnotatedModel lastModel = SugarRecord.last(FloatFieldAnnotatedModel.class);
        assertEquals(firstModel.getFloat(), lastModel.getFloat());
    }
}
