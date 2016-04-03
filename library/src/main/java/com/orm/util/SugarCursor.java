package com.orm.util;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.provider.BaseColumns;

public class SugarCursor extends CursorWrapper {
	private String idName = BaseColumns._ID;

	public SugarCursor(String idName, Cursor cursor) {
		super(cursor);

		if (idName != null) {
			this.idName = idName;
		}
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		try {
			return super.getColumnIndexOrThrow(columnName);
		} catch (IllegalArgumentException e) {
			if (columnName.equals("_id") || columnName.equalsIgnoreCase("ID")) {
				return super.getColumnIndexOrThrow(idName);
			} else {
				throw e;
			}
		}
	}

	@Override
	public int getColumnIndex(String columnName) {
		if (columnName.equals("_id") || columnName.equalsIgnoreCase("ID")) {
			columnName = idName;
		}
		return super.getColumnIndex(columnName);
	}
}
