package com.example.sugartest;

import com.example.models.NestedExtendedModel;
import com.example.models.RelationshipExtendedModel;
import com.example.models.SimpleExtendedModel;
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
@Config(sdk=18)
public class NestedExtendedTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void emptyDatabaseTest() throws Exception {
        assertEquals(0L, SugarRecord.count(NestedExtendedModel.class));
        assertEquals(0L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
        save(nested);
        save(new NestedExtendedModel(nested));
        assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(1L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(1L, SugarRecord.count(NestedExtendedModel.class));
    }

    @Test
    public void twoSameSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
        save(nested);
        save(new NestedExtendedModel(nested));
        save(new NestedExtendedModel(nested));
        assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(1L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(2L, SugarRecord.count(NestedExtendedModel.class));
    }

    @Test
    public void twoDifferentSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        SimpleExtendedModel another_simple = new SimpleExtendedModel();
        save(another_simple);
        RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
        save(nested);
        RelationshipExtendedModel another_nested = new RelationshipExtendedModel(another_simple);
        save(another_nested);
        save(new NestedExtendedModel(nested));
        save(new NestedExtendedModel(another_nested));
        assertEquals(2L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(2L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(2L, SugarRecord.count(NestedExtendedModel.class));
    }

    @Test
    public void manySameSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
        save(nested);
        for (int i = 1; i <= 100; i++) {
            save(new NestedExtendedModel(nested));
        }
        assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(1L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(100L, SugarRecord.count(NestedExtendedModel.class));
    }

    @Test
    public void manyDifferentSaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleExtendedModel simple = new SimpleExtendedModel();
            save(simple);
            RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
            save(nested);
            save(new NestedExtendedModel(nested));
        }
        assertEquals(100L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(100L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(100L, SugarRecord.count(NestedExtendedModel.class));
    }

    @Test
    public void listAllSameTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
        save(nested);
        for (int i = 1; i <= 100; i++) {
            save(new NestedExtendedModel(nested));
        }
        List<NestedExtendedModel> models = SugarRecord.listAll(NestedExtendedModel.class);
        assertEquals(100, models.size());
        for (NestedExtendedModel model : models) {
            assertEquals(nested.getId(), model.getNested().getId());
            assertEquals(simple.getId(), model.getNested().getSimple().getId());
        }
    }

    @Test
    public void listAllDifferentTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleExtendedModel simple = new SimpleExtendedModel();
            save(simple);
            RelationshipExtendedModel nested = new RelationshipExtendedModel(simple);
            save(nested);
            save(new NestedExtendedModel(nested));
        }
        List<NestedExtendedModel> models = SugarRecord.listAll(NestedExtendedModel.class);
        assertEquals(100, models.size());
        for (NestedExtendedModel model : models) {
            assertEquals(model.getId(), model.getNested().getId());
            assertEquals(model.getId(), model.getNested().getSimple().getId());
        }
    }
}
