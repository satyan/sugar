package com.orm.query;

import android.database.Cursor;

import com.orm.Configuration;
import com.orm.SugarRecord;
import com.orm.util.NamingHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Select<T> implements Iterable {
	private static final String SPACE = " ";
	private static final String SINGLE_QUOTE = "'";
	private static final String LEFT_PARENTHESIS = "(";
	private static final String RIGHT_PARENTHESIS = ")";
	private static final String SELECT_FROM = "SELECT * FROM ";
	private static final String WHERE = "WHERE ";
	private static final String ORDER_BY = "ORDER BY ";
	private static final String GROUP_BY = "GROUP BY ";
	private static final String LIMIT = "LIMIT ";
	private static final String OFFSET = "OFFSET ";

	private Class<T> record;
	private String[] arguments;
	private String whereClause = "";
	private String orderBy = "";
	private String groupBy = "";
	private String limit = "";
	private String offset = "";
	private List<String> args = new ArrayList<>();

	public Select(Class<T> record) {
		this.record = record;
	}

	public static <T> Select<T> from(Class<T> record) {
		return new Select<>(record);
	}

	public Select<T> orderBy(String prop) {
		this.orderBy = prop;
		return this;
	}

	public Select<T> groupBy(String prop) {
		this.groupBy = prop;
		return this;
	}

	public Select<T> limit(String limit) {
		this.limit = limit;
		return this;
	}

	public Select<T> offset(String offset) {
		this.offset = offset;
		return this;
	}

	public Select<T> where(String whereClause) {
		this.whereClause = whereClause;
		return this;
	}

	public Select<T> where(Condition... condition) {

		mergeConditions(condition, Condition.Type.AND);

		return this;
	}

	private void mergeConditions(Condition[] conditions, Condition.Type type) {
		StringBuilder toAppend = new StringBuilder();
		for (Condition condition : conditions) {
			if (toAppend.length() != 0) {
				toAppend.append(SPACE).append(type.name()).append(SPACE);
			}

			if (Condition.Check.LIKE.equals(condition.getCheck()) ||
				Condition.Check.NOT_LIKE.equals(condition.getCheck())) {
				toAppend
						.append(condition.getProperty())
						.append(condition.getCheckSymbol())
						.append(SINGLE_QUOTE)
						.append(condition.getValue().toString())
						.append(SINGLE_QUOTE);
			} else if (Condition.Check.IS_NULL.equals(condition.getCheck()) ||
					   Condition.Check.IS_NOT_NULL.equals(condition.getCheck())) {
				toAppend
						.append(condition.getProperty())
						.append(condition.getCheckSymbol());
			} else {
				toAppend
						.append(condition.getProperty())
						.append(condition.getCheckSymbol())
						.append("? ");
				args.add(condition.getValue().toString());
			}
		}

		if (!whereClause.isEmpty()) {
			whereClause += SPACE + type.name() + SPACE;
		}

		whereClause += LEFT_PARENTHESIS + toAppend + RIGHT_PARENTHESIS;
	}

	public Select<T> whereOr(Condition... args) {
		mergeConditions(args, Condition.Type.OR);
		return this;
	}

	public Select<T> and(Condition... args) {
		mergeConditions(args, Condition.Type.AND);
		return this;
	}

	public Select<T> or(Condition... args) {
		mergeConditions(args, Condition.Type.OR);
		return this;
	}

	public Select<T> where(String whereClause, String[] args) {
		this.whereClause = whereClause;
		this.arguments = args;
		return this;
	}

	public Cursor getCursor() {
		return SugarRecord.getCursor(record, whereClause, arguments, groupBy, orderBy, limit);
	}

	public List<T> list() {
		if (arguments == null) {
			arguments = convertArgs(args);
		}

		return SugarRecord.find(record, whereClause, arguments, groupBy, orderBy, limit);
	}

	public long count() {
		if (arguments == null) {
			arguments = convertArgs(args);
		}

		return SugarRecord.count(record, whereClause, arguments, groupBy, orderBy, limit);
	}

	public T first() {
		if (arguments == null) {
			arguments = convertArgs(args);
		}

		List<T> list = SugarRecord.find(record, whereClause, arguments, groupBy, orderBy, "1");
		return list.size() > 0 ? list.get(0) : null;
	}

	String toSql(Configuration configuration) {
		StringBuilder sql = new StringBuilder();
		sql.append(SELECT_FROM).append(NamingHelper.toSQLName(configuration, this.record))
		   .append(SPACE);

		if (!whereClause.isEmpty()) {
			sql.append(WHERE).append(whereClause).append(SPACE);
		}

		if (!orderBy.isEmpty()) {
			sql.append(ORDER_BY).append(orderBy).append(SPACE);
		}

		if (!groupBy.isEmpty()) {
			sql.append(GROUP_BY).append(groupBy).append(SPACE);
		}

		if (!limit.isEmpty()) {
			sql.append(LIMIT).append(limit).append(SPACE);
		}

		if (!offset.isEmpty()) {
			sql.append(OFFSET).append(offset).append(SPACE);
		}

		return sql.toString();
	}

	String getWhereCond() {
		return whereClause;
	}

	String[] getArgs() {
		return convertArgs(args);
	}

	private String[] convertArgs(List<String> argsList) {
		return argsList.toArray(new String[argsList.size()]);
	}

	@Override
	public Iterator<T> iterator() {
		if (arguments == null) {
			arguments = convertArgs(args);
		}

		return SugarRecord.findAsIterator(record, whereClause, arguments, groupBy, orderBy, limit);
	}

}
