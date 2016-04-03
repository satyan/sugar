package com.orm.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.orm.Configuration;
import com.orm.util.NamingHelper;

import java.util.List;

import static com.orm.util.ReflectionUtil.getDomainClasses;

/**
 * This schema generator does not try to alter the table, it simply drop all existing databases and
 * recreates them if the version changes.
 * <p>
 * Since the process is not complex, it can handle downgrades as well as upgrades.
 */
public class DropTableSchemaGenerator extends SugarSchemaGenerator {
	
	private static final String TAG = "DropSchemaGenerator";
	
	/**
	 * @param configuration
	 */
	public DropTableSchemaGenerator(Configuration configuration) {
		super(configuration);
	}

	/**
	 * If this constructor is used, you *must* call setConfiguration(Configuration) before any
	 * other operations is performed.
	 * <p>
	 * This is done automatically if passed to a Configuration object.
	 */
	public DropTableSchemaGenerator() {
		super();
	}

	public void dropAllTables(SQLiteDatabase sqLiteDatabase) {
		Log.i(TAG, "Dropping all database tables...");
		List<Class> tables = getDomainClasses(getConfiguration());
		for (Class table : tables) {
			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NamingHelper.toSQLName(getConfiguration(), table));
		}
	}

	/**
	 * Called when the database needs to be upgraded. The implementation
	 * should use this method to drop tables, add tables, or do anything else it
	 * needs to upgrade to the new schema version.
	 * <p>
	 * <p>
	 * The SQLite ALTER TABLE documentation can be found
	 * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
	 * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
	 * you can use ALTER TABLE to rename the old table, then create the new table and then
	 * populate the new table with the contents of the old table.
	 * </p><p>
	 * This method executes within a transaction.  If an exception is thrown, all changes
	 * will automatically be rolled back.
	 * </p>
	 *
	 * @param db
	 * 		The database.
	 * @param oldVersion
	 * 		The old database version.
	 * @param newVersion
	 * 		The new database version.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Upgrading database from " + oldVersion + " to " + newVersion + "...");
		dropAllTables(db);
		onCreate(db);
	}

	/**
	 * Called when the database needs to be downgraded. This is strictly similar to
	 * {@link #onUpgrade} method, but is called whenever current version is newer than requested
	 * one.
	 * However, this method is not abstract, so it is not mandatory for a customer to
	 * implement it. If not overridden, default implementation will reject downgrade and
	 * throws SQLiteException
	 * <p>
	 * <p>
	 * This method executes within a transaction.  If an exception is thrown, all changes
	 * will automatically be rolled back.
	 * </p>
	 *
	 * @param db
	 * 		The database.
	 * @param oldVersion
	 * 		The old database version.
	 * @param newVersion
	 * 		The new database version.
	 */
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "Downgrading database from " + oldVersion + " to " + newVersion + "...");
		dropAllTables(db);
		onCreate(db);
	}
}
