package com.example.sugartest;

import com.example.models.RelationshipAnnotatedModel;
import com.example.models.SimpleAnnotatedModel;
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
public class RelationshipAnnotatedTests {
    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
        //you other setup here
    }
    @Test
    public void emptyDatabaseTest() throws Exception {
        assertEquals(0L, SugarRecord.count(RelationshipAnnotatedModel.class));
        assertEquals(0L, SugarRecord.count(SimpleAnnotatedModel.class));
    }

    @Test
    public void oneSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        save(simple);
        save(new RelationshipAnnotatedModel(simple));
        assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        assertEquals(1L, SugarRecord.count(RelationshipAnnotatedModel.class));
    }

    @Test
    public void twoSameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        save(simple);
        save(new RelationshipAnnotatedModel(simple));
        save(new RelationshipAnnotatedModel(simple));
        assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        assertEquals(2L, SugarRecord.count(RelationshipAnnotatedModel.class));
    }

    @Test
    public void twoDifferentSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        save(simple);
        SimpleAnnotatedModel another_simple = new SimpleAnnotatedModel();
        save(another_simple);
        save(new RelationshipAnnotatedModel(simple));
        save(new RelationshipAnnotatedModel(another_simple));
        assertEquals(2L, SugarRecord.count(SimpleAnnotatedModel.class));
        assertEquals(2L, SugarRecord.count(RelationshipAnnotatedModel.class));
    }

    @Test
    public void manySameSaveTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        save(simple);
        for (int i = 1; i <= 100; i++) {
            save(new RelationshipAnnotatedModel(simple));
        }
        assertEquals(1L, SugarRecord.count(SimpleAnnotatedModel.class));
        assertEquals(100L, SugarRecord.count(RelationshipAnnotatedModel.class));
    }

    @Test
    public void manyDifferentSaveTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            save(simple);
            save(new RelationshipAnnotatedModel(simple));
        }
        assertEquals(100L, SugarRecord.count(SimpleAnnotatedModel.class));
        assertEquals(100L, SugarRecord.count(RelationshipAnnotatedModel.class));
    }

    @Test
    public void listAllSameTest() throws Exception {
        SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
        save(simple);
        for (int i = 1; i <= 100; i++) {
            save(new RelationshipAnnotatedModel(simple));
        }
        List<RelationshipAnnotatedModel> models =
                SugarRecord.listAll(RelationshipAnnotatedModel.class);
        assertEquals(100, models.size());
        for (RelationshipAnnotatedModel model : models) {
            assertEquals(simple.getId(), model.getSimple().getId());
        }
    }

    @Test
    public void listAllDifferentTest() throws Exception {
        for (int i = 1; i <= 100; i++) {
            SimpleAnnotatedModel simple = new SimpleAnnotatedModel();
            save(simple);
            save(new RelationshipAnnotatedModel(simple));
        }
        List<RelationshipAnnotatedModel> models =
                SugarRecord.listAll(RelationshipAnnotatedModel.class);
        assertEquals(100, models.size());
        for (RelationshipAnnotatedModel model : models) {
            assertEquals(model.getId(), model.getSimple().getId());
        }
    }
}
