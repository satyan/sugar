package com.example.sugartest;


import com.example.models.ByteArrayAnnotatedModel;
import com.example.models.ByteArrayExtendedModel;
import com.orm.SugarRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static com.orm.SugarRecord.save;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk=18)
public class ByteArrayFieldTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void nullByteArrayExtendedTest() {
        byte[] array = "".getBytes();
        save(new ByteArrayExtendedModel());
        ByteArrayExtendedModel model = SugarRecord.findById(ByteArrayExtendedModel.class, 1);
        assertEquals(new String(array), new String(model.getByteArray()));
        assertArrayEquals(array, model.getByteArray());
    }

    @Test
    public void nullByteArrayAnnotatedTest() {
        byte[] array = "".getBytes();
        save(new ByteArrayAnnotatedModel());
        ByteArrayAnnotatedModel model = SugarRecord.findById(ByteArrayAnnotatedModel.class, 1);
        assertEquals(new String(array), new String(model.getByteArray()));
        assertArrayEquals(array, model.getByteArray());
    }

    @Test
    public void byteArrayExtendedTest() {
        byte[] array = "hello".getBytes();
        save(new ByteArrayExtendedModel(array));
        ByteArrayExtendedModel model = SugarRecord.findById(ByteArrayExtendedModel.class, 1);
        assertEquals(new String(array), new String(model.getByteArray()));
        assertArrayEquals(array, model.getByteArray());
    }

    @Test
    public void byteArrayAnnotatedTest() {
        byte[] array = "hello".getBytes();
        save(new ByteArrayAnnotatedModel(array));
        ByteArrayAnnotatedModel model = SugarRecord.findById(ByteArrayAnnotatedModel.class, 1);
        assertEquals(new String(array), new String(model.getByteArray()));
        assertArrayEquals(array, model.getByteArray());
    }
}
