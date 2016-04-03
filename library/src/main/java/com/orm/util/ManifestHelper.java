package com.orm.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.orm.helper.ClassicSchemaGenerator;
import com.orm.helper.SugarDatabaseHelper;

/**
 * Helper class for accessing properties in the AndroidManifest
 */
public class ManifestHelper {

	/**
	 * Key for the database name meta data.
	 */
	public final static String METADATA_DATABASE = "SUGAR_DATABASE";
	/**
	 * Key for the database verison meta data.
	 */
	public final static String METADATA_VERSION = "SUGAR_VERSION";
	public final static String METADATA_SUGAR_AUTHORITY = "SUGAR_AUTHORITY";
	public final static String METADATA_DOMAIN_PACKAGE_NAME = "SUGAR_DOMAIN_PACKAGE_NAME";
	public final static String METADATA_QUERY_LOG = "SUGAR_QUERY_LOG";
	private static final String METADATA_HELPER_CLASS = "SUGAR_SCHEMA_HELPER_CLASS";
	private static final String METADATA_ID_COLUMN_NAME = "SUGAR_ID_COLUMN_NAME";
	/**
	 * The default name for the database unless specified in the AndroidManifest.
	 */
	public final static String DATABASE_DEFAULT_NAME = "sugar.db";
	private static final String DEFAULT_HELPER_CLASS_NAME = ClassicSchemaGenerator.class.getName();

	
	/**
	 * Grabs the database version from the manifest.
	 *
	 * @param context
	 * 		the {@link android.content.Context} of the Android application
	 * @return the database version as specified by the {@link #METADATA_VERSION} version or 1 of
	 * not present
	 */
	public static int getDatabaseVersion(Context context) {
		Integer databaseVersion = getMetaDataInteger(context, METADATA_VERSION);

		if ((databaseVersion == null) || (databaseVersion == 0)) {
			databaseVersion = 1;
		}

		return databaseVersion;
	}

	/**
	 * Grabs the domain name of the model classes from the manifest.
	 *
	 * @param context
	 * 		the {@link android.content.Context} of the Android application
	 * @return the package String that Sugar uses to search for model classes
	 */
	public static String getDomainPackageName(Context context) {
		String domainPackageName = getMetaDataString(context, METADATA_DOMAIN_PACKAGE_NAME);

		if (domainPackageName == null) {
			domainPackageName = "";
		}

		return domainPackageName;
	}

	/**
	 * Defaults tot eh applicationId (package name).
	 *
	 * @param context
	 * @return
	 */
	public static String getAuthority(Context context) {
		String sugarAuthority = getMetaDataString(context, METADATA_SUGAR_AUTHORITY);

		if (sugarAuthority == null) {
			sugarAuthority = context.getApplicationContext().getPackageName();
		}

		return sugarAuthority;
	}

	/**
	 * Grabs the name of the database file specified in the manifest.
	 *
	 * @param context
	 * 		the {@link android.content.Context} of the Android application
	 * @return the value for the {@value #METADATA_DATABASE} meta data in the AndroidManifest or
	 * {@link #DATABASE_DEFAULT_NAME} if not present
	 */
	public static String getDatabaseName(Context context) {
		String databaseName = getMetaDataString(context, METADATA_DATABASE);

		if (databaseName == null) {
			databaseName = DATABASE_DEFAULT_NAME;
		}

		return databaseName;
	}

	/**
	 * Grabs the debug flag from the manifest.
	 *
	 * @param context
	 * 		the {@link android.content.Context} of the Android application
	 * @return true if the debug flag is enabled
	 */
	public static boolean getDebugEnabled(Context context) {
		return getMetaDataBoolean(context, METADATA_QUERY_LOG);
	}

	private static String getMetaDataString(Context context, String name) {
		String value = null;

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			value = ai.metaData.getString(name);
		} catch (Exception e) {
			Log.d("sugar", "Couldn't find config value: " + name);
		}

		return value;
	}

	private static Integer getMetaDataInteger(Context context, String name) {
		Integer value = null;

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			value = ai.metaData.getInt(name);
		} catch (Exception e) {
			Log.d("sugar", "Couldn't find config value: " + name);
		}

		return value;
	}

	private static Boolean getMetaDataBoolean(Context context, String name) {
		Boolean value = false;

		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			value = ai.metaData.getBoolean(name);
		} catch (Exception e) {
			Log.d("sugar", "Couldn't find config value: " + name);
		}

		return value;
	}
	
	public static SugarDatabaseHelper getHelper(Context context) {
		String helperClassName = getMetaDataString(context, METADATA_HELPER_CLASS);

		if (helperClassName == null) {
			helperClassName = DEFAULT_HELPER_CLASS_NAME;
		}

		try {
			Class<?> clazz = Class.forName(helperClassName);
			return (SugarDatabaseHelper) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not load schema helper class", e);
		}
	}
	
	public static String getIdColumnName(Context context) {
		String idColName = getMetaDataString(context, METADATA_ID_COLUMN_NAME);

		if (idColName == null) {
			idColName = BaseColumns._ID;
		}

		return idColName;
	}
}
