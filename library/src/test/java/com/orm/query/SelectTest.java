package com.orm.query;

import com.orm.Configuration;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SelectTest {
	private Configuration config = Configuration
			.manifest(new DummyContext());

	@Test
	public void testMergeCondition() {
		Select where = Select.from(TestRecord.class).where(Condition.prop("test").eq("satya"));
		assertEquals("(test = ? )", where.getWhereCond());
		assertEquals(1, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);

		where = Select.from(TestRecord.class)
					  .where(Condition.prop("test").eq("satya"), Condition.prop("prop").eq(2));
		assertEquals("(test = ?  AND prop = ? )", where.getWhereCond());
		assertEquals(2, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);
		assertEquals("2", where.getArgs()[1]);
	}


	@Test
	public void testWhere() {
		Select where = Select.from(TestRecord.class).where(Condition.prop("test").eq("satya"));
		assertEquals("(test = ? )", where.getWhereCond());
		assertEquals(1, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);

		where = Select.from(TestRecord.class)
					  .where(Condition.prop("test").eq("satya"), Condition.prop("prop").eq(2));
		assertEquals("(test = ?  AND prop = ? )", where.getWhereCond());
		assertEquals(2, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);
		assertEquals("2", where.getArgs()[1]);
	}

	@Test
	public void toSqlAllClauses() {
		String toSql = Select.from(TestRecord.class)
							 .where("foo")
							 .orderBy("doe")
							 .groupBy("john")
							 .limit("5")
							 .offset("10")
							 .toSql(config);
		assertEquals("SELECT * FROM TEST_RECORD WHERE foo ORDER BY doe GROUP BY john LIMIT 5 OFFSET 10 ", toSql);
	}

	@Test
	public void toSqlNoClauses() {
		String toSql = Select.from(TestRecord.class)
							 .toSql(config);
		assertEquals("SELECT * FROM TEST_RECORD ", toSql);
	}

	@Test
	public void toSqlWhereLimitClauses() {
		String toSql = Select.from(TestRecord.class)
							 .where("foo")
							 .limit("10")
							 .toSql(config);
		assertEquals("SELECT * FROM TEST_RECORD WHERE foo LIMIT 10 ", toSql);
	}


	@Test
	public void testWhereOr() {
		Select where = Select.from(TestRecord.class).whereOr(Condition.prop("test").eq("satya"));
		assertEquals("(test = ? )", where.getWhereCond());
		assertEquals(1, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);

		where = Select.from(TestRecord.class)
					  .whereOr(Condition.prop("test").eq("satya"), Condition.prop("prop").eq(2));
		assertEquals("(test = ?  OR prop = ? )", where.getWhereCond());
		assertEquals(2, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);
		assertEquals("2", where.getArgs()[1]);
	}

	@Test
	public void testAnd() {
		Select where = Select.from(TestRecord.class).whereOr(Condition.prop("test").eq("satya"));
		assertEquals("(test = ? )", where.getWhereCond());
		assertEquals(1, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);

		where.and(Condition.prop("prop").eq(2));

		assertEquals("(test = ? ) AND (prop = ? )", where.getWhereCond());
		assertEquals(2, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);
		assertEquals("2", where.getArgs()[1]);
	}

	@Test
	public void testOr() {
		Select where = Select.from(TestRecord.class).whereOr(Condition.prop("test").eq("satya"));
		assertEquals("(test = ? )", where.getWhereCond());
		assertEquals(1, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);

		where.or(Condition.prop("prop").eq(2));

		assertEquals("(test = ? ) OR (prop = ? )", where.getWhereCond());
		assertEquals(2, where.getArgs().length);
		assertEquals("satya", where.getArgs()[0]);
		assertEquals("2", where.getArgs()[1]);
	}

	@Test
	public void testIsNull() {
		Select where = Select.from(TestRecord.class).where(Condition.prop("test").isNull());
		assertEquals("(test IS NULL )", where.getWhereCond());
		assertEquals(0, where.getArgs().length);

		where = Select.from(TestRecord.class).where(Condition.prop("test").eq(null));
		assertEquals("(test IS NULL )", where.getWhereCond());
		assertEquals(0, where.getArgs().length);
	}

	@Test
	public void testIsNotNull() {
		Select where = Select.from(TestRecord.class).where(Condition.prop("test").isNotNull());
		assertEquals("(test IS NOT NULL )", where.getWhereCond());
		assertEquals(0, where.getArgs().length);

		where = Select.from(TestRecord.class).where(Condition.prop("test").notEq(null));
		assertEquals("(test IS NOT NULL )", where.getWhereCond());
		assertEquals(0, where.getArgs().length);
	}
}