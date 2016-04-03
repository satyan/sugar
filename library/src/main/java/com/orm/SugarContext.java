package com.orm;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.util.Log;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class SugarContext {
	
	private static final String TAG = "SugarContext";
	private static SugarContext instance = null;
	private final Configuration configuration;
	private SugarDb sugarDb;
	private Map<Object, Long> entitiesMap;

	//private SugarContext(Context context) {
	//    this.sugarDb = new SugarDb(context);
	//    this.entitiesMap = Collections.synchronizedMap(new WeakHashMap<Object, Long>());
	//}

	private SugarContext(Configuration configuration) {
		this.configuration = configuration;
		this.sugarDb = new SugarDb(configuration);
		this.entitiesMap = Collections.synchronizedMap(new WeakHashMap<Object, Long>());
	}

	public static SugarContext getSugarContext() {
		if (instance == null) {
			throw new NullPointerException("SugarContext has not been initialized properly. Call SugarContext.init(Context) of SugarContext.init(Configuration) in your Application.onCreate() method and SugarContext.terminate() in your Application.onTerminate() method.");
		}
		return instance;
	}

	public static void init(Context context) {
		instance = new SugarContext(Configuration.manifest(context));
	}

	public static void init(Configuration configuration) {
		instance = new SugarContext(configuration);
	}

	//public static void initMaybe(Configuration configuration) {
	//	instance = new SugarContext(configuration);
	//}

	public static void terminate() {
		if (instance == null) {
			return;
		}
		instance.doTerminate();
	}
	
	public static boolean isDebug() {
		return getSugarContext().configuration.isDebug();
	}
	
	/*
	 * Per issue #106 on Github, this method won't be called in
	 * any real Android device. This method is used purely in
	 * emulated process environments such as an emulator or
	 * Robolectric Android mock.
	 */
	private void doTerminate() {
		if (this.sugarDb != null) {
			this.sugarDb.getDB().close();
		}
	}

	public SugarDb getSugarDb() {
		return sugarDb;
	}

	Map<Object, Long> getEntitiesMap() {
		return entitiesMap;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Shortcut to ContentResolver.notifyChange(Uri uri, ContentObserver observer, boolean
	 * syncToNetwork)
	 *
	 * @param uri
	 * @param observer
	 * @param syncToNetwork
	 */
	public void notifyChange(Uri uri, ContentObserver observer, boolean syncToNetwork) {
		if (configuration != null) {
			if (configuration.isDebug()) {
				Log.d(TAG, "Notify of data change: " + uri);
			}
			configuration.getContext().getApplicationContext().getContentResolver()
						 .notifyChange(uri, observer, syncToNetwork);
		} else {
			Log.w(TAG, "Configuration not set. Unable to notify of data change: " + uri);
		}
	}

	public <T> void notifyChange(Class<T> type) {
		notifyChange(SugarContentProvider.createUri(type, null), null, false);
	}
	
	public <T> void notifyChange(Class<T> type, ContentObserver observer, boolean syncToNetwork) {
		notifyChange(SugarContentProvider.createUri(type, null), observer, syncToNetwork);
	}

	public <T> void notifyChange(Class<T> type, Long id) {
		notifyChange(SugarContentProvider.createUri(type, id), null, false);
	}

	public <T> void notifyChange(Class<T> type, Long id, ContentObserver observer, boolean syncToNetwork) {
		notifyChange(SugarContentProvider.createUri(type, id), observer, syncToNetwork);
	}

}
