package com.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import com.orm.dsl.Table;
import com.orm.dsl.Unique;
import com.orm.util.NamingHelper;
import com.orm.util.QueryBuilder;
import com.orm.util.ReflectionUtil;
import com.orm.util.SugarCursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.orm.SugarContext.getSugarContext;

public class SugarRecord {

	public static final String TAG = "SugarRecord";
	public static final boolean DEBUG_CURSOR = false;

	//@Column(name = BaseColumns._ID)
	private Long id = null;

	private static Configuration getConfiguration() {
		return getSugarContext()
				.getConfiguration();
	}

	private static SQLiteDatabase getSugarDataBase() {
		return getSugarContext().getSugarDb().getDB();
	}

	public static <T> int deleteAll(Class<T> type) {
		return deleteAll(type, null);
	}

	public static <T> int deleteAll(Class<T> type, String whereClause, String... whereArgs) {
		final int deleted = getSugarDataBase()
				.delete(NamingHelper.toSQLName(getConfiguration(), type), whereClause, whereArgs);
		if (deleted > 0) {
			getSugarContext().notifyChange(type);
		}
		return deleted;
	}



	public static <T> Cursor getCursor(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
		Cursor raw = getSugarDataBase()
				.query(NamingHelper.toSQLName(getConfiguration(), type), null, whereClause, whereArgs,
						groupBy, null, orderBy, limit);
		return new SugarCursor(idName(), raw);
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
			for (T object : objects) {
				save(object);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			Log.i(TAG, "Error in saving in transaction " + e.getMessage());
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
			for (T object : objects) {
				update(object);
			}
			sqLiteDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			Log.i(TAG, "Error in saving in transaction " + e.getMessage());
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
			Log.i(TAG, "Error in deleting in transaction " + e.getMessage());
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
		List<T> list = find(type, id(), new String[]{String.valueOf(id)}, null, null, "1");
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public static <T> T findById(Class<T> type, Integer id) {
		return findById(type, Long.valueOf(id));
	}

	public static <T> List<T> findById(Class<T> type, String[] ids) {

		String whereClause =
				idName() + " IN (" + QueryBuilder.generatePlaceholders(ids.length) + ")";
		return find(type, whereClause, ids);
	}
	
	private static String idName() {
		return getConfiguration().getIdColumnName();
	}
	
	public static <T> T first(Class<T> type) {
		List<T> list = findWithQuery(type,
				"SELECT * FROM " +
				NamingHelper.toSQLName(getConfiguration(), type) + " ORDER BY " +
				idName() +
				" ASC LIMIT 1");
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public static <T> T last(Class<T> type) {
		List<T> list = findWithQuery(type,
				"SELECT * FROM " +
				NamingHelper.toSQLName(getConfiguration(), type) + " ORDER BY " +
				idName() +
				" DESC LIMIT 1");
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
		return new CursorIterator<T>(type, cursor);
	}

	public static <T> Iterator<T> findAsIterator(Class<T> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
		Cursor cursor = getSugarDataBase()
				.query(NamingHelper.toSQLName(getConfiguration(), type), null, whereClause, whereArgs,
						groupBy, null, orderBy, limit);
		return new CursorIterator<T>(type, cursor);
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
		Cursor cursor = getSugarDataBase()
				.query(NamingHelper.toSQLName(getConfiguration(), type), null, whereClause, whereArgs,
						groupBy, null, orderBy, limit);

		return getEntitiesFromCursor(cursor, type);
	}

	public static <T> List<T> getEntitiesFromCursor(Cursor cursor, Class<T> type) {
		T entity;
		List<T> result = new ArrayList<T>();
		try {
			while (cursor.moveToNext()) {
				entity = type.getDeclaredConstructor().newInstance();
				inflate(cursor, entity, getSugarContext().getEntitiesMap());
				result.add(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return result;
	}

	public static <T> long count(Class<?> type) {
		return count(type, null, null, null, null, null);
	}

	public static <T> long count(Class<?> type, String whereClause, String[] whereArgs) {
		return count(type, whereClause, whereArgs, null, null, null);
	}

	public static <T> long count(Class<?> type, String whereClause, String[] whereArgs, String groupBy, String orderBy, String limit) {
		long result = -1;
		String filter = (!TextUtils.isEmpty(whereClause)) ? " where " + whereClause : "";
		SQLiteStatement sqliteStatement;
		try {
			sqliteStatement = getSugarDataBase().compileStatement(
					"SELECT count(*) FROM " +
					NamingHelper.toSQLName(getConfiguration(), type) + filter);
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

	public static long save(Object object) {
		return save(getSugarDataBase(), object);
	}

	static long save(SQLiteDatabase db, Object object) {
		Map<Object, Long> entitiesMap = getSugarContext().getEntitiesMap();
		List<Field> fields = ReflectionUtil.getTableFields(object.getClass());
		ContentValues values = new ContentValues(fields.size());
		Field idField = null;
		for (Field field : fields) {
			ReflectionUtil.addFieldValueToColumn(getConfiguration(), values, field, object, entitiesMap);
			if (field.getName().equals("id")) {
				idField = field;
			}
		}

		boolean isSugarEntity = isSugarEntity(object.getClass());
		if (isSugarEntity && entitiesMap.containsKey(object)) {
			//values.put("id", entitiesMap.get(object));
			values.put(idName(), entitiesMap.get(object));
		}

		long id = db.insertWithOnConflict(NamingHelper
				.toSQLName(getConfiguration(), object.getClass()), null, values,
				SQLiteDatabase.CONFLICT_REPLACE);

		if (object.getClass().isAnnotationPresent(Table.class)) {
			if (idField != null) {
				idField.setAccessible(true);
				try {
					idField.set(object, new Long(id));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				entitiesMap.put(object, id);
			}
		} else if (SugarRecord.class.isAssignableFrom(object.getClass())) {
			((SugarRecord) object).setId(id);
		}

		getSugarContext().notifyChange(object.getClass(), id);

		return id;
	}

	public static long update(Object object) {
		return update(getSugarDataBase(), object);
	}

	static long update(SQLiteDatabase db, Object object) {
		Map<Object, Long> entitiesMap = getSugarContext().getEntitiesMap();
		List<Field> fields = ReflectionUtil.getTableFields(object.getClass());
		ContentValues values = new ContentValues(fields.size());

		StringBuilder whereClause = new StringBuilder();
		List<String> whereArgs = new ArrayList<>();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Unique.class)) {
				try {
					field.setAccessible(true);
					String columnName = NamingHelper
							.toSQLName(getConfiguration(), field);
					Object columnValue = field.get(object);

					whereClause.append(column(columnName));
					whereArgs.add(String.valueOf(columnValue));
				} catch (IllegalAccessException e) {
					//e.printStackTrace();
					Log.w(TAG, "Could not access field: " + field.getName(), e);
				}
			} else {
				if (!field.getName().equals("id")) {
					ReflectionUtil.addFieldValueToColumn(getConfiguration(), values, field, object, entitiesMap);
				}
			}
		}

		String[] whereArgsArray = whereArgs.toArray(new String[whereArgs.size()]);
		// Get SugarRecord based on Unique values
		long rowsEffected = db.update(NamingHelper
				.toSQLName(getConfiguration(), object
						.getClass()), values, whereClause
				.toString(), whereArgsArray);

		if (rowsEffected == 0) {
			// FIXME This is WRONG. The save method returns an ID, but this update method should
			// FIXME return a count. I am not sure of the implications of changing the count to 1
			// FIXME at this moment, so I'll have to come back to it. -bpappin
			final long recordId = save(db, object);
			return recordId;
		} else {
			getSugarContext().notifyChange(object.getClass());
			return rowsEffected;
		}
	}


	public static boolean isSugarEntity(Class<?> objectClass) {
		return objectClass.isAnnotationPresent(Table.class) ||
			   SugarRecord.class.isAssignableFrom(objectClass);
	}

	private static void inflate(Cursor cursor, Object entity, Map<Object, Long> entitiesMap) {
		if (DEBUG_CURSOR) {
			Log.d(TAG, "Row Dump (inflate): " + DatabaseUtils.dumpCurrentRowToString(cursor));
		}
		List<Field> columns = ReflectionUtil.getTableFields(entity.getClass());
		if (!entitiesMap.containsKey(entity)) {
			entitiesMap.put(entity, cursor.getLong(cursor.getColumnIndex(idName())));
		}

		for (Field field : columns) {
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
			if (isSugarEntity(fieldType)) {
				try {
					long id = cursor.getLong(cursor.getColumnIndex(NamingHelper
							.toSQLName(getConfiguration(), field)));
					field.set(entity, (id > 0) ? findById(fieldType, id) : null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				ReflectionUtil.setFieldValueFromCursor(getConfiguration(), cursor, field, entity);
			}
		}
	}

	public boolean delete() {
		Long id = getId();
		Class<?> type = getClass();
		if (id != null && id > 0L) {

			final boolean deleted = getSugarDataBase().delete(NamingHelper
					.toSQLName(getConfiguration(), type), id(), new String[]{
					id.toString()
			}) == 1;
			if (deleted) {
				Log.d(TAG, type.getSimpleName() + " deleted : " + id);
				getSugarContext().notifyChange(type, id);
			} else {
				Log.w(TAG, type.getSimpleName() + " was not deleted : " + id);
			}

			return deleted;
		} else {
			Log.i(TAG, "Cannot delete object: " + type.getSimpleName() +
					   " - object has not been saved");
			return false;
		}
	}

	private static String id() {
		return column(idName());
	}

	private static String column(String name) {
		return name + " = ?";
	}

	public static boolean delete(Object object) {
		Class<?> type = object.getClass();
		if (type.isAnnotationPresent(Table.class)) {
			try {
				Field field = type.getDeclaredField("id");
				field.setAccessible(true);
				Long id = (Long) field.get(object);
				if (id != null && id > 0L) {
					boolean deleted = getSugarDataBase().delete(NamingHelper
							.toSQLName(getConfiguration(), type), id(), new String[]{
							id.toString()
					}) == 1;
					if (deleted) {
						Log.d(TAG, type.getSimpleName() + " deleted : " + id);
						getSugarContext().notifyChange(type, id);
					} else {
						Log.w(TAG, type.getSimpleName() + " was not deleted : " + id);
					}
					return deleted;
				} else {
					Log.i(TAG, "Cannot delete object: " + object.getClass().getSimpleName() +
							   " - object has not been saved");
					return false;
				}
			} catch (NoSuchFieldException e) {
				Log.i(TAG, "Cannot delete object: " + object.getClass().getSimpleName() +
						   " - annotated object has no id");
				return false;
			} catch (IllegalAccessException e) {
				Log.i(TAG, "Cannot delete object: " + object.getClass().getSimpleName() +
						   " - can't access id");
				return false;
			}
		} else if (SugarRecord.class.isAssignableFrom(type)) {
			return ((SugarRecord) object).delete();
		} else {
			Log.i(TAG, "Cannot delete object: " + object.getClass().getSimpleName() +
					   " - not persisted");
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
		inflate(cursor, this, getSugarContext().getEntitiesMap());
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
				inflate(cursor, entity, getSugarContext().getEntitiesMap());
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

}
