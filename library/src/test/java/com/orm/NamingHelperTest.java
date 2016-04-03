package com.orm;

import com.orm.query.DummyContext;
import com.orm.util.NamingHelper;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class NamingHelperTest {
	private static Configuration config = Configuration
			.manifest(new DummyContext());

	@Test
	public void testToSQLNameCaseConversion() throws Exception {
		assertToSqlNameEquals("TESTLOWERCASE", "testlowercase");
		assertToSqlNameEquals("TESTUPPERCASE", "TESTUPPERCASE");
	}

	@Test
	public void testToSQLNameUnderscore() {
		assertToSqlNameEquals("TEST_UNDERSCORE", "testUnderscore");
		assertToSqlNameEquals("AB_CD", "AbCd");
		assertToSqlNameEquals("AB_CD", "ABCd");
		assertToSqlNameEquals("AB_CD", "AbCD");
		assertToSqlNameEquals("SOME_DETAILS_OBJECT", "SomeDetailsObject");
		assertToSqlNameEquals("H_OL_A", "hOlA");
		assertToSqlNameEquals("A", "a");
	}

	/**
	 * Helper method that asserts a CamelCaseString is converted to UPPER_CASE_UNDER_SCORE.
	 *
	 * @param expected
	 * 		a CamelCaseString
	 * @param actual
	 * 		the expected UPPER_CASE_UNDER_SCORE string
	 */
	private static void assertToSqlNameEquals(String expected, String actual) {
		assertEquals(expected, NamingHelper.toSQLNameDefault(config, actual));
	}

}
