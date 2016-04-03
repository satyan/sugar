package com.example.sugartest;

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
public class RelationshipExtendedTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void emptyDatabaseTest() throws Exception {
        assertEquals(0L, SugarRecord.count(RelationshipExtendedModel.class));
        assertEquals(0L, SugarRecord.count(SimpleExtendedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        save(new RelationshipExtendedModel(simple));
        assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(1L, SugarRecord.count(RelationshipExtendedModel.class));
    }

    @Test
    public void twoSameSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        save(new RelationshipExtendedModel(simple));
        save(new RelationshipExtendedModel(simple));
        assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(2L, SugarRecord.count(RelationshipExtendedModel.class));
    }

    @Test
    public void twoDifferentSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        SimpleExtendedModel another_simple = new SimpleExtendedModel();
        save(another_simple);
        save(new RelationshipExtendedModel(simple));
        save(new RelationshipExtendedModel(another_simple));
        assertEquals(2L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(2L, SugarRecord.count(RelationshipExtendedModel.class));
    }

    @Test
    public void manySameSaveTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        for (int i = 1; i <= 100; i++) {
            save(new RelationshipExtendedModel(simple));
        }
        assertEquals(1L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(100L, SugarRecord.count(RelationshipExtendedModel.class));
    }

    @Test
    public void manyDifferentSaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleExtendedModel simple = new SimpleExtendedModel();
            save(simple);
            save(new RelationshipExtendedModel(simple));
        }
        assertEquals(100L, SugarRecord.count(SimpleExtendedModel.class));
        assertEquals(100L, SugarRecord.count(RelationshipExtendedModel.class));
    }

    @Test
    public void listAllSameTest() throws Exception {
        SimpleExtendedModel simple = new SimpleExtendedModel();
        save(simple);
        for (int i = 1; i <= 100; i++) {
            save(new RelationshipExtendedModel(simple));
        }
        List<RelationshipExtendedModel> models =
                SugarRecord.listAll(RelationshipExtendedModel.class);
        assertEquals(100, models.size());
        for (RelationshipExtendedModel model : models) {
            assertEquals(simple.getId(), model.getSimple().getId());
        }
    }

    @Test
    public void listAllDifferentTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleExtendedModel simple = new SimpleExtendedModel();
            save(simple);
            save(new RelationshipExtendedModel(simple));
        }
        List<RelationshipExtendedModel> models =
                SugarRecord.listAll(RelationshipExtendedModel.class);
        assertEquals(100, models.size());
        for (RelationshipExtendedModel model : models) {
            assertEquals(model.getId(), model.getSimple().getId());
        }
    }
}
