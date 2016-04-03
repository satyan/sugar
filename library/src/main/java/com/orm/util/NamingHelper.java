package com.orm.util;

import com.orm.Configuration;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import java.lang.reflect.Field;

public class NamingHelper {

	/**
	 * Converts a given CamelCasedString to UPPER_CASE_UNDER_SCORE.
	 *
	 * @param camelCased
	 * 		a non empty camelCased string
	 * @return the equivalent string converted to UPPER_CASE_UNDER_SCORE unless camelCased equals
	 * "_id" (not case sensitive) in which case "_id" is returned
	 */
	public static String toSQLNameDefault(Configuration config, String camelCased) {
		if (camelCased.equalsIgnoreCase("_id") || camelCased.equalsIgnoreCase("id")) {
			return config.getIdColumnName();
		}

		StringBuilder sb = new StringBuilder();
		char[] buf = camelCased.toCharArray();

		for (int i = 0; i < buf.length; i++) {
			char prevChar = (i > 0) ? buf[i - 1] : 0;
			char c = buf[i];
			char nextChar = (i < buf.length - 1) ? buf[i + 1] : 0;
			boolean isFirstChar = (i == 0);

			if (isFirstChar || Character.isLowerCase(c) || Character.isDigit(c)) {
				sb.append(Character.toUpperCase(c));
			} else if (Character.isUpperCase(c)) {
				if (Character.isLetterOrDigit(prevChar)) {
					if (Character.isLowerCase(prevChar)) {
						sb.append('_').append(c);
					} else if (nextChar > 0 && Character.isLowerCase(nextChar)) {
						sb.append('_').append(c);
					} else {
						sb.append(c);
					}
				} else {
					sb.append(c);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Maps a Java Field object to the database's column name.
	 *
	 * @param field
	 * 		the {@link java.lang.reflect.Field} that will be mapped
	 * @return the name of the given Field as represented in the database. If the Field is annotated
	 * with {@link com.orm.dsl.Column} then the {@link com.orm.dsl.Column#name()} will be
	 * returned. Else, the Field's {@link java.lang.reflect.Field#getName()} will be
	 * converted from CamelCase to UNDER_SCORE notation
	 */
	public static String toSQLName(Configuration config, Field field) {
		if (field.isAnnotationPresent(Column.class)) {
			Column annotation = field.getAnnotation(Column.class);
			return annotation.name();
		}

		return toSQLNameDefault(config, field.getName());
	}

	/**
	 * Maps a Java Class to the name of the class.
	 *
	 * @param table
	 * 		the generic {@link java.lang.Class<T>} that defines a database table
	 * @return if the given class is annotated with {@link com.orm.dsl.Table} then the value for
	 * {@link com.orm.dsl.Table#name()} will be returned. Else, the class' simple name will
	 * be converted from CamelCase to UNDER_SCORE notation
	 */
	public static String toSQLName(Configuration config, Class<?> table) {
		if (table.isAnnotationPresent(Table.class)) {
			Table annotation = table.getAnnotation(Table.class);
			if ("".equals(annotation.name())) {
				return NamingHelper.toSQLNameDefault(config, table.getSimpleName());
			}
			return annotation.name();
		}

		return NamingHelper.toSQLNameDefault(config, table.getSimpleName());
	}

}
