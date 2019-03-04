package com.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;
import com.orm.annotation.Table;
import com.orm.annotation.Unique;
import com.orm.helper.ManifestHelper;
import com.orm.helper.NamingHelper;
import com.orm.inflater.EntityInflater;
import com.orm.util.QueryBuilder;
import com.orm.util.ReflectionUtil;
import com.orm.util.SugarCursor;

import java.lang.reflect.Field;
import java.util.*;

import static com.orm.SugarContext.getSugarContext;

public class SugarRecord {
    public static final String SUGAR = "Sugar";

    private Long id = null;

    private static SQLiteDatabase getSugarDataBase() {
        return getSugarContext().getSugarDb().getDB();
    }

    public static <T> int deleteAll(Class<T> type) {
        return deleteAll(type, null);
    }

    public static <T> int deleteAll(Class<T> type, String whereClause, String... whereArgs) {
        return getSugarDataBase().delete(NamingHelper.toTableName(type), whereClause, whereArgs);
    }

    public static <T> Cursor getCursor(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
        Cursor raw = getSugarDataBase().query(NamingHelper.toTableName(type), null, whereClause, whereArgs,
                groupBy, null, orderBy, limit);
        return new SugarCursor(raw);
    }

    @SuppressWarnings("deprecation")
    public static <T> void saveInTx(T... objects) {
        saveInTx(Arrays.asList(objects));
    }

    @SuppressWarnings("deprecation")
    public static <T> void saveInTx(Collection<T> objects) {
        SQLiteDatabase sqLiteDatabase = getSugarDataBase();
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setLockingEnabled(false);
            for (T object: objects) {
                save(object);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            if (ManifestHelper.isDebugEnabled()) {
                Log.i(SUGAR, "Error in saving in transaction " + e.getMessage());
            }
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.setLockingEnabled(true);
        }
    }

    @SuppressWarnings("deprecation")
    public static <T> void updateInTx(T... objects) {
        updateInTx(Arrays.asList(objects));
    }

    @SuppressWarnings("deprecation")
    public static <T> void updateInTx(Collection<T> objects) {
        SQLiteDatabase sqLiteDatabase = getSugarDataBase();
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setLockingEnabled(false);
            for (T object: objects) {
                update(object);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            if (ManifestHelper.isDebugEnabled()) {
                Log.i(SUGAR, "Error in saving in transaction " + e.getMessage());
            }
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.setLockingEnabled(true);
        }
    }

    @SuppressWarnings("deprecation")
    public static <T> int deleteInTx(T... objects) {
        return deleteInTx(Arrays.asList(objects));
    }

    @SuppressWarnings("deprecation")
    public static <T> int deleteInTx(Collection<T> objects) {
        SQLiteDatabase sqLiteDatabase = getSugarDataBase();
        int deletedRows = 0;
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setLockingEnabled(false);
            for (T object : objects) {
                if (delete(object)) {
                    ++deletedRows;
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            deletedRows = 0;
            if(ManifestHelper.isDebugEnabled()) {
                Log.i(SUGAR, "Error in deleting in transaction " + e.getMessage());
            }
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.setLockingEnabled(true);
        }
        return deletedRows;
    }

    public static <T> List<T> listAll(Class<T> type) {
        return find(type, null, null, null, null, null);
    }

    public static <T> List<T> listAll(Class<T> type, String orderBy) {
        return find(type, null, null, null, orderBy, null);
    }

    public static <T> T findById(Class<T> type, Long id) {
        List<T> list = find(type, "id=?", new String[]{String.valueOf(id)}, null, null, "1");
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public static <T> T findById(Class<T> type, Integer id) {
        return findById(type, Long.valueOf(id));
    }

    public static <T> List<T> findById(Class<T> type, String... ids) {
        String whereClause = "id IN (" + QueryBuilder.generatePlaceholders(ids.length) + ")";
        return find(type, whereClause, ids);
    }

    public static <T> T first(Class<T>type) {
        List<T> list = findWithQuery(type,
                "SELECT * FROM " + NamingHelper.toTableName(type) + " ORDER BY ID ASC LIMIT 1");
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static <T> T last(Class<T>type) {
        List<T> list = findWithQuery(type,
                "SELECT * FROM " + NamingHelper.toTableName(type) + " ORDER BY ID DESC LIMIT 1");
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static <T> Iterator<T> findAll(Class<T> type) {
        return findAsIterator(type, null, null, null, null, null);
    }

    public static <T> Iterator<T> findAsIterator(Class<T> type, String whereClause, String... whereArgs) {
        return findAsIterator(type, whereClause, whereArgs, null, null, null);
    }

    public static <T> Iterator<T> findWithQueryAsIterator(Class<T> type, String query, String... arguments) {
        Cursor cursor = getSugarDataBase().rawQuery(query, arguments);
        return new CursorIterator<>(type, cursor);
    }

    public static <T> Iterator<T> findAsIterator(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
        Cursor cursor = getSugarDataBase().query(NamingHelper.toTableName(type), null, whereClause, whereArgs,
                groupBy, null, orderBy, limit);
        return new CursorIterator<>(type, cursor);
    }

    public static <T> List<Long>  findIds(Class<T> type, String whereClause, String... whereArgs) {
        return findIds(type, whereClause, whereArgs, null, null, null);
    }

    public static <T> List<Long> findIds(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
        List<Long> result = new ArrayList<>( );

        Cursor cursor = getSugarDataBase().query(NamingHelper.toTableName(type), new String[]{"id"}, whereClause, whereArgs,
                groupBy, null, orderBy, limit);
        if ( cursor.moveToFirst() )
        {
            do
            {
                result.add( cursor.getLong(0) );
            }
            while (cursor.moveToNext());
        }

        cursor.close( );

        return result;
    }

    public static <T> List<T> find(Class<T> type, String whereClause, String... whereArgs) {
        return find(type, whereClause, whereArgs, null, null, null);
    }

    public static <T> List<T> findWithQuery(Class<T> type, String query, String... arguments) {
        Cursor cursor = getSugarDataBase().rawQuery(query, arguments);

        return getEntitiesFromCursor(cursor, type);
    }

    public static void executeQuery(String query, String... arguments) {
        getSugarDataBase().execSQL(query, arguments);
    }

    public static <T> List<T> find(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {

        String args[];
        args = (whereArgs == null) ? null : replaceArgs(whereArgs);

        Cursor cursor = getSugarDataBase().query(NamingHelper.toTableName(type), null, whereClause, args,
                groupBy, null, orderBy, limit);

        return getEntitiesFromCursor(cursor, type);
    }

    public static <T> List<T> findOneToMany(Class<T> type, String relationFieldName, Object relationObject, Long relationObjectId) {
        String args[] = { String.valueOf(relationObjectId) };
        String whereClause = NamingHelper.toSQLNameDefault(relationFieldName) + " = ?";

        Cursor cursor = getSugarDataBase().query(NamingHelper.toTableName(type), null, whereClause, args,
                null, null, null, null);

        return getEntitiesFromCursor(cursor, type, relationFieldName, relationObject);
    }

    public static <T> List<T> getEntitiesFromCursor(Cursor cursor, Class<T> type){
        return getEntitiesFromCursor(cursor, type, null, null);
    }

    public static <T> List<T> getEntitiesFromCursor(Cursor cursor, Class<T> type, String relationFieldName, Object relationObject){
        T entity;
        List<T> result = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                entity = type.getDeclaredConstructor().newInstance();
                new EntityInflater().withCursor(cursor)
                        .withObject(entity)
                        .withEntitiesMap(getSugarContext().getEntitiesMap())
                        .withRelationFieldName(relationFieldName)
                        .withRelationObject(relationObject)
                        .inflate();
                result.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return result;
    }

    public static <T> long count(Class<T> type) {
        return count(type, null, null, null, null, null);
    }

    public static <T> long count(Class<T> type, String whereClause, String... whereArgs) {
    	return count(type, whereClause, whereArgs, null, null, null);
    }

    public static <T> long count(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
        long result = -1;
        String filter = (!TextUtils.isEmpty(whereClause)) ? " where "  + whereClause : "";
        SQLiteStatement sqliteStatement;
        try {
            sqliteStatement = getSugarDataBase().compileStatement("SELECT count(*) FROM " + NamingHelper.toTableName(type) + filter);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return result;
        }

        if (whereArgs != null) {
            for (int i = whereArgs.length; i != 0; i--) {
                sqliteStatement.bindString(i, whereArgs[i - 1]);
            }
        }

        try {
            result = sqliteStatement.simpleQueryForLong();
        } finally {
            sqliteStatement.close();
        }

        return result;
    }

    public static <T> long sum(Class<T> type, String field) {
        return sum(type, field, null, (String) null );
    }

    public static <T> long sum(Class<T> type, String field, String whereClause, String... whereArgs) {
        long result = -1;
        String filter = (!TextUtils.isEmpty(whereClause)) ? " where " + whereClause : "";
        SQLiteStatement sqLiteStatement;
        try {
            sqLiteStatement = getSugarDataBase().compileStatement("SELECT sum(" + field + ") FROM " + NamingHelper.toTableName(type) + filter);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return result;
        }

        if (whereArgs != null) {
            for (int i = whereArgs.length; i != 0; i--) {
                sqLiteStatement.bindString(i, whereArgs[i - 1]);
            }
        }

        try {
            result = sqLiteStatement.simpleQueryForLong();
        } finally {
            sqLiteStatement.close();
        }

        return result;
    }

    public static long save(Object object) {
        return save(getSugarDataBase(), object);
    }

    static long save(SQLiteDatabase db, Object object) {
        Map<Object, Long> entitiesMap = getSugarContext().getEntitiesMap();
        List<Field> columns = ReflectionUtil.getTableFields(object.getClass());
        ContentValues values = new ContentValues(columns.size());
        Field idField = null;
        for (Field column : columns) {
            ReflectionUtil.addFieldValueToColumn(values, column, object, entitiesMap);
            if (column.getName().equals("id")) {
                idField = column;
            }
        }

        boolean isSugarEntity = isSugarEntity(object.getClass());
        if (isSugarEntity && entitiesMap.containsKey(object)) {
                values.put("id", entitiesMap.get(object));
        }

        long id = db.insertWithOnConflict(NamingHelper.toTableName(object.getClass()), null, values,
                SQLiteDatabase.CONFLICT_REPLACE);

        if (object.getClass().isAnnotationPresent(Table.class)) {
            if (idField != null) {
                idField.setAccessible(true);
                try {
                    idField.set(object, id);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                entitiesMap.put(object, id);
            }
        } else if (SugarRecord.class.isAssignableFrom(object.getClass())) {
            ((SugarRecord) object).setId(id);
        }

        if (ManifestHelper.isDebugEnabled()) {
            Log.i(SUGAR, object.getClass().getSimpleName() + " saved : " + id);
        }

        return id;
    }

    public static long update(Object object) {
        return update(getSugarDataBase(), object);
    }

    static long update(SQLiteDatabase db, Object object) {
        Map<Object, Long> entitiesMap = getSugarContext().getEntitiesMap();
        List<Field> columns = ReflectionUtil.getTableFields(object.getClass());
        ContentValues values = new ContentValues(columns.size());

        StringBuilder whereClause = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        for (Field column : columns) {
            if(column.isAnnotationPresent(Unique.class)) {
                try {
                    column.setAccessible(true);
                    String columnName = NamingHelper.toColumnName(column);
                    Object columnValue = column.get(object);

                    whereClause.append(columnName).append(" = ?");
                    whereArgs.add(String.valueOf(columnValue));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                if (!column.getName().equals("id")) {
                    ReflectionUtil.addFieldValueToColumn(values, column, object, entitiesMap);
                }
            }
        }

        String[] whereArgsArray = whereArgs.toArray(new String[whereArgs.size()]);
        // Get SugarRecord based on Unique values
        long rowsEffected = db.update(NamingHelper.toTableName(object.getClass()), values, whereClause.toString(), whereArgsArray);

        if (rowsEffected == 0) {
            return save(db, object);
        } else {
            return rowsEffected;
        }
    }

    public static boolean isSugarEntity(Class<?> objectClass) {
        return objectClass.isAnnotationPresent(Table.class) || SugarRecord.class.isAssignableFrom(objectClass);
    }

    public boolean delete() {
        Long id = getId();
        Class<?> type = getClass();
        if (id != null && id > 0L) {
            if(ManifestHelper.isDebugEnabled()) {
                Log.i(SUGAR, type.getSimpleName() + " deleted : " + id);
            }
            return getSugarDataBase().delete(NamingHelper.toTableName(type), "Id=?", new String[]{id.toString()}) == 1;
        } else {
            if(ManifestHelper.isDebugEnabled()) {
                Log.i(SUGAR, "Cannot delete object: " + type.getSimpleName() + " - object has not been saved");
            }
            return false;
        }
    }

    public static boolean delete(Object object) {
        Class<?> type = object.getClass();
        if (type.isAnnotationPresent(Table.class)) {
            try {
                Field field = type.getDeclaredField("id");
                field.setAccessible(true);
                Long id = (Long) field.get(object);
                if (id != null && id > 0L) {
                    boolean deleted = getSugarDataBase().delete(NamingHelper.toTableName(type), "Id=?", new String[]{id.toString()}) == 1;
                    if(ManifestHelper.isDebugEnabled()) {
                        Log.i(SUGAR, type.getSimpleName() + " deleted : " + id);
                    }
                    return deleted;
                } else {
                    if(ManifestHelper.isDebugEnabled()) {
                        Log.i(SUGAR, "Cannot delete object: " + object.getClass().getSimpleName() + " - object has not been saved");
                    }
                    return false;
                }
            } catch (NoSuchFieldException e) {
                if(ManifestHelper.isDebugEnabled()) {
                    Log.i(SUGAR, "Cannot delete object: " + object.getClass().getSimpleName() + " - annotated object has no id");
                }
                return false;
            } catch (IllegalAccessException e) {
                if(ManifestHelper.isDebugEnabled()) {
                    Log.i(SUGAR, "Cannot delete object: " + object.getClass().getSimpleName() + " - can't access id");
                }
                return false;
            }
        } else if (SugarRecord.class.isAssignableFrom(type)) {
            return ((SugarRecord) object).delete();
        } else {
            if(ManifestHelper.isDebugEnabled()) {
                Log.i(SUGAR, "Cannot delete object: " + object.getClass().getSimpleName() + " - not persisted");
            }
            return false;
        }
    }

    public long save() {
        return save(getSugarDataBase(), this);
    }

    public long update() {
        return update(getSugarDataBase(), this);
    }

    @SuppressWarnings("unchecked")
    void inflate(Cursor cursor) {
        new EntityInflater()
                .withCursor(cursor)
                .withObject(this)
                .withEntitiesMap(getSugarContext().getEntitiesMap())
                .inflate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    static class CursorIterator<E> implements Iterator<E> {
        Class<E> type;
        Cursor cursor;

        public CursorIterator(Class<E> type, Cursor cursor) {
            this.type = type;
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return cursor != null && !cursor.isClosed() && !cursor.isAfterLast();
        }

        @Override
        public E next() {
            E entity = null;
            if (cursor == null || cursor.isAfterLast()) {
                throw new NoSuchElementException();
            }

            if (cursor.isBeforeFirst()) {
                cursor.moveToFirst();
            }

            try {
                entity = type.getDeclaredConstructor().newInstance();
                new EntityInflater()
                        .withCursor(cursor)
                        .withObject(entity)
                        .withEntitiesMap(getSugarContext().getEntitiesMap())
                        .inflate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.moveToNext();
                if (cursor.isAfterLast()) {
                    cursor.close();
                }
            }

            return entity;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static String[] replaceArgs(String[] args){

        String [] replace = new String[args.length];
        for (int i=0; i<args.length; i++){

            replace[i]= (args[i].equals("true")) ? replace[i]="1" : (args[i].equals("false")) ? replace[i]="0" : args[i];

        }

        return replace;

    }

}
