package com.orm.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orm.Configuration;
import com.orm.util.NamingHelper;

import java.util.List;

import static com.orm.util.ReflectionUtil.getDomainClasses;

/**
 * This is the classic SugarORM database schema generator.
 */
public class ClassicSchemaGenerator extends SugarSchemaGenerator {


	/**
	 * If this constructor is used, you *must* call setConfiguration(Configuration) before any
	 * other operations is performed.
	 * <p>
	 * This is done automatically if passed to a Configuration object.
	 */
	public ClassicSchemaGenerator() {
		super();
	}

	/**
	 * @param configuration
	 */
	public ClassicSchemaGenerator(Configuration configuration) {
		super(configuration);
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
		List<Class> domainClasses = getDomainClasses(getConfiguration());
		String sql = "select count(*) from sqlite_master where type='table' and name='%s';";

		for (Class domain : domainClasses) {
			String tableName = NamingHelper.toSQLName(getConfiguration(), domain);
			Cursor c = db.rawQuery(String.format(sql, tableName), null);
			if (c.moveToFirst() && c.getInt(0) == 0) {
				createTable(domain, db);
			} else {
				addColumns(domain, db);
			}
		}
		executeSugarUpgrade(db, oldVersion, newVersion);
	}
}
