package com.orm;

import android.content.Context;
import android.provider.BaseColumns;

import com.orm.helper.ClassicSchemaGenerator;
import com.orm.helper.SugarDatabaseHelper;
import com.orm.util.ManifestHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpappin on 16-03-29.
 */
public class Configuration {

	private Context context;
	private String databaseName;
	private String authority;
	private int databaseVersion;
	private final List<Class<?>> modelClasses = new ArrayList<Class<?>>();
	private SugarDatabaseHelper databaseHelper;
	private boolean debug = false;
	private String domain;
	private String idColumnName = BaseColumns._ID;


	public Configuration(Context context) {
		this.context = context;
	}

	public static final Configuration manifest(Context context) {
		return Configuration.get(context).name(ManifestHelper.getDatabaseName(context))
							.version(ManifestHelper.getDatabaseVersion(context))
							.debug(ManifestHelper.getDebugEnabled(context)).helper()
							.domain(ManifestHelper.getDomainPackageName(context))
							.authority(ManifestHelper.getAuthority(context))
							.helper(ManifestHelper.getHelper(context)).idColumnName(ManifestHelper.getIdColumnName(context));
	}

	public static final Configuration get(Context context) {
		return new Configuration(context);
	}

	public Configuration name(String name) {
		this.databaseName = name;
		return this;
	}

	public Configuration version(int version) {
		this.databaseVersion = version;
		return this;
	}

	public Configuration model(Class<?>... models) {
		for (int i = 0; i < models.length; i++) {
			modelClasses.add(models[i]);
		}
		return this;
	}

	public Configuration domain(String domain) {
		this.domain = domain;
		return this;
	}

	public Configuration debug(boolean debug) {
		this.debug = debug;
		return this;
	}

	public Configuration authority(String authority) {
		this.authority = authority;
		return this;
	}

	public Configuration helper(SugarDatabaseHelper helper) {
		helper.setConfiguration(this);
		this.databaseHelper = helper;
		return this;
	}

	public Configuration idColumnName(String name) {
		this.idColumnName = name;
		return this;
	}

	/**
	 * Uses the classic helper.
	 *
	 * @return
	 */
	public Configuration helper() {
		this.databaseHelper = new ClassicSchemaGenerator(this);
		return this;
	}

	public SugarDatabaseHelper getDatabaseHelper() {
		return databaseHelper;
	}

	public Context getContext() {
		return context;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getAuthority() {
		return authority;
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	public List<Class<?>> getModelClasses() {
		return modelClasses;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getDomain() {
		return domain;
	}
}
