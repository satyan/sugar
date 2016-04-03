package com.orm.helper;

import com.orm.Configuration;
import com.orm.models.EmptyModel;
import com.orm.models.IntUniqueModel;
import com.orm.models.MultiColumnUniqueModel;
import com.orm.models.StringFieldAnnotatedModel;
import com.orm.models.StringFieldExtendedModel;
import com.orm.models.StringFieldExtendedModelAnnotatedColumn;
import com.orm.query.DummyContext;
import com.orm.util.NamingHelper;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SchemaGeneratorTest {
	private Configuration config = Configuration
			.manifest(new DummyContext());

	@Test
	public void testEmptyTableCreation() throws Exception {
		ClassicSchemaGenerator schemaGenerator = new ClassicSchemaGenerator(config);
		String createSQL = schemaGenerator.createTableSQL(EmptyModel.class);
		assertEquals(
				"CREATE TABLE IF NOT EXISTS " + NamingHelper.toSQLName(config, EmptyModel.class) +
				" ( " + idName() + " INTEGER PRIMARY KEY AUTOINCREMENT  ) ",
				createSQL);
	}
	
	private String idName() {
		return config.getIdColumnName();
	}
	
	@Test
	public void testSimpleColumnTableCreation() throws Exception {
		ClassicSchemaGenerator schemaGenerator = new ClassicSchemaGenerator(Configuration
				.manifest(new DummyContext()));
		String createSQL = schemaGenerator.createTableSQL(StringFieldExtendedModel.class);
		assertEquals(
				"CREATE TABLE IF NOT EXISTS " +
				NamingHelper.toSQLName(config, StringFieldExtendedModel.class) +
				" ( " + idName() + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
				"NAME TEXT ) ",
				createSQL);

		String createSQL2 = schemaGenerator.createTableSQL(StringFieldAnnotatedModel.class);

		assertEquals(
				"CREATE TABLE IF NOT EXISTS " +
				NamingHelper.toSQLName(config, StringFieldAnnotatedModel.class) +
				" ( " + idName() + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
				"NAME TEXT ) ",
				createSQL2);

		String createSQL3 = schemaGenerator
				.createTableSQL(StringFieldExtendedModelAnnotatedColumn.class);

		assertEquals(
				"CREATE TABLE IF NOT EXISTS " +
				NamingHelper.toSQLName(config, StringFieldExtendedModelAnnotatedColumn.class) +
				" ( " + idName() + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
				"anyName TEXT ) ",
				createSQL3);
	}

	@Test
	public void testUniqueTableCreation() {
		ClassicSchemaGenerator schemaGenerator = new ClassicSchemaGenerator(Configuration
				.manifest(new DummyContext()));
		String createSQL = schemaGenerator.createTableSQL(IntUniqueModel.class);
		assertEquals(
				"CREATE TABLE IF NOT EXISTS " +
				NamingHelper.toSQLName(config, IntUniqueModel.class) +
				" ( " + idName() + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
				"VALUE INTEGER UNIQUE ) ",
				createSQL);
	}

	@Test
	public void testMultiColumnUniqueTableCreation() {
		ClassicSchemaGenerator schemaGenerator = new ClassicSchemaGenerator(Configuration
				.manifest(new DummyContext()));
		String createSQL = schemaGenerator.createTableSQL(MultiColumnUniqueModel.class);
		assertEquals(
				"CREATE TABLE IF NOT EXISTS " +
				NamingHelper.toSQLName(config, MultiColumnUniqueModel.class) +
				" ( " + idName() + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
				"A INTEGER, B INTEGER, " +
				"UNIQUE(A, B) ON CONFLICT REPLACE ) ",
				createSQL);
	}
}
