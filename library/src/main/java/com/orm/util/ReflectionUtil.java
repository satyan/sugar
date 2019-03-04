package com.orm.util;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.annotation.Ignore;
import com.orm.annotation.Table;
import com.orm.helper.ManifestHelper;
import com.orm.helper.MultiDexHelper;
import com.orm.helper.NamingHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ReflectionUtil {

    //Prevent instantiation..
    private ReflectionUtil() { }

    public static List<Field> getTableFields(Class table) {
        List<Field> fieldList = SugarConfig.getFields(table);
        if (fieldList != null) return fieldList;

        if (ManifestHelper.isDebugEnabled()) {
            Log.d("Sugar", "Fetching properties");
        }
        List<Field> typeFields = new ArrayList<>();

        getAllFields(typeFields, table);

        List<Field> toStore = new ArrayList<>();
        for (Field field : typeFields) {
            if (!field.isAnnotationPresent(Ignore.class) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                toStore.add(field);
            }
        }

        SugarConfig.setFields(table, toStore);
        return toStore;
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        Collections.addAll(fields, type.getDeclaredFields());

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static void addFieldValueToColumn(ContentValues values, Field column, Object object,
                                             Map<Object, Long> entitiesMap) {
        column.setAccessible(true);
        Class<?> columnType = column.getType();
        try {
            String columnName = NamingHelper.toColumnName(column);
            Object columnValue = column.get(object);

            if (columnType.isAnnotationPresent(Table.class)) {
                Field field;
                try {
                    field = columnType.getDeclaredField("id");
                    field.setAccessible(true);
                    if(columnValue != null) {
                        values.put(columnName,String.valueOf(field.get(columnValue)));
                    } else {
                        values.putNull(columnName);
                    }
                } catch (NoSuchFieldException e) {
                    if (entitiesMap.containsKey(columnValue)) {
                        values.put(columnName, entitiesMap.get(columnValue));
                    }
                }
            } else if (SugarRecord.class.isAssignableFrom(columnType)) {
                values.put(columnName,
                        (columnValue != null)
                                ? String.valueOf(((SugarRecord) columnValue).getId())
                                : "0");
            } else {
                if (columnType.equals(Short.class) || columnType.equals(short.class)) {
                    values.put(columnName, (Short) columnValue);
                } else if (columnType.equals(Integer.class) || columnType.equals(int.class)) {
                    values.put(columnName, (Integer) columnValue);
                } else if (columnType.equals(Long.class) || columnType.equals(long.class)) {
                    values.put(columnName, (Long) columnValue);
                } else if (columnType.equals(Float.class) || columnType.equals(float.class)) {
                    values.put(columnName, (Float) columnValue);
                } else if (columnType.equals(Double.class) || columnType.equals(double.class)) {
                    values.put(columnName, (Double) columnValue);
                } else if (columnType.equals(Boolean.class) || columnType.equals(boolean.class)) {
                    values.put(columnName, (Boolean) columnValue);
                } else if (columnType.equals(BigDecimal.class)) {
                    try {
                        values.put(columnName, column.get(object).toString());
                    } catch (NullPointerException e) {
                        values.putNull(columnName);
                    }
                } else if (Timestamp.class.equals(columnType)) {
                    try {
                        values.put(columnName, ((Timestamp) column.get(object)).getTime());
                    } catch (NullPointerException e) {
                        values.put(columnName, (Long) null);
                    }
                } else if (Date.class.equals(columnType)) {
                    try {
                        values.put(columnName, ((Date) column.get(object)).getTime());
                    } catch (NullPointerException e) {
                        values.put(columnName, (Long) null);
                    }
                } else if (Calendar.class.equals(columnType)) {
                    try {
                        values.put(columnName, ((Calendar) column.get(object)).getTimeInMillis());
                    } catch (NullPointerException e) {
                        values.put(columnName, (Long) null);
                    }
                } else if (columnType.equals(byte[].class)) {
                    if (columnValue == null) {
                        values.put(columnName, "".getBytes());
                    } else {
                        values.put(columnName, (byte[]) columnValue);
                    }
                } else if (columnType.equals(List.class)) {
                    //ignore
                } else {
                    if (columnValue == null) {
                        values.putNull(columnName);
                    } else if (columnType.isEnum()) {
                        values.put(columnName, ((Enum) columnValue).name());
                    } else {
                        values.put(columnName, String.valueOf(columnValue));
                    }
                }
            }

        } catch (IllegalAccessException e) {
            if (ManifestHelper.isDebugEnabled()) {
                Log.e("Sugar", e.getMessage());
            }
        }
    }

    public static void setFieldValueFromCursor(Cursor cursor, Field field, Object object) {
        field.setAccessible(true);
        try {
            Class fieldType = field.getType();
            String colName = NamingHelper.toColumnName(field);

            int columnIndex = cursor.getColumnIndex(colName);

            //TODO auto upgrade to add new columns
            if (columnIndex < 0) {
                if (ManifestHelper.isDebugEnabled()) {
                    Log.e("SUGAR", "Invalid colName, you should upgrade database");
                }
                return;
            }

            if (cursor.isNull(columnIndex)) {
                return;
            }

            if (colName.equalsIgnoreCase("id")) {
                long cid = cursor.getLong(columnIndex);
                field.set(object, cid);
            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                field.set(object,
                        cursor.getLong(columnIndex));
            } else if (fieldType.equals(String.class)) {
                String val = cursor.getString(columnIndex);
                field.set(object, val != null && val.equals("null") ? null : val);
            } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                field.set(object,
                        cursor.getDouble(columnIndex));
            } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                field.set(object,
                        cursor.getString(columnIndex).equals("1"));
            } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                field.set(object,
                        cursor.getInt(columnIndex));
            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                field.set(object,
                        cursor.getFloat(columnIndex));
            } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
                field.set(object,
                        cursor.getShort(columnIndex));
            } else if (fieldType.equals(BigDecimal.class)) {
                String val = cursor.getString(columnIndex);
                field.set(object, val != null && val.equals("null") ? null : new BigDecimal(val));
            } else if (fieldType.equals(Timestamp.class)) {
                long l = cursor.getLong(columnIndex);
                field.set(object, new Timestamp(l));
            } else if (fieldType.equals(Date.class)) {
                long l = cursor.getLong(columnIndex);
                field.set(object, new Date(l));
            } else if (fieldType.equals(Calendar.class)) {
                long l = cursor.getLong(columnIndex);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(l);
                field.set(object, c);
            } else if (fieldType.equals(byte[].class)) {
                byte[] bytes = cursor.getBlob(columnIndex);
                if (bytes == null) {
                    field.set(object, "".getBytes());
                } else {
                    field.set(object, cursor.getBlob(columnIndex));
                }
            } else if (Enum.class.isAssignableFrom(fieldType)) {
                try {
                    Method valueOf = field.getType().getMethod("valueOf", String.class);
                    String strVal = cursor.getString(columnIndex);
                    Object enumVal = valueOf.invoke(field.getType(), strVal);
                    field.set(object, enumVal);
                } catch (Exception e) {
                    if (ManifestHelper.isDebugEnabled()) {
                        Log.e("Sugar", "Enum cannot be read from Sqlite3 database. Please check the type of field " + field.getName());
                    }
                }
            } else {
                if (ManifestHelper.isDebugEnabled()) {
                    Log.e("Sugar", "Class cannot be read from Sqlite3 database. Please check the type of field " + field.getName() + "(" + field.getType().getName() + ")");
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            if (ManifestHelper.isDebugEnabled()) {
                Log.e("field set error", e.getMessage());
            }
        }
    }

    private static Field getDeepField(String fieldName, Class<?> type) throws NoSuchFieldException {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superclass = type.getSuperclass();
            if (superclass != null) {
                return getDeepField(fieldName, superclass);
            } else {
                throw e;
            }
        }
    }

    public static void setFieldValueForId(Object object, Long value) {
        try {
            Field field = getDeepField("id", object.getClass());
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Class> getDomainClasses() {
        List<Class> domainClasses = new ArrayList<>();
        try {
            for (String className : getAllClasses()) {
                Class domainClass = getDomainClass(className);
                if (domainClass != null) domainClasses.add(domainClass);
            }
        } catch (IOException | PackageManager.NameNotFoundException  e) {
            if (ManifestHelper.isDebugEnabled()) {
                Log.e("Sugar", e.getMessage());
            }
        }

        return domainClasses;
    }


    private static Class getDomainClass(String className) {
        Class<?> discoveredClass = null;
        try {
            discoveredClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (Throwable e) {
            String error = (e.getMessage() == null) ? "getDomainClass " + className + " error" : e.getMessage();
            if (ManifestHelper.isDebugEnabled()) {
                Log.e("Sugar", error);
            }
        }

        if ((discoveredClass != null) &&
                ((SugarRecord.class.isAssignableFrom(discoveredClass) &&
                        !SugarRecord.class.equals(discoveredClass)) ||
                        discoveredClass.isAnnotationPresent(Table.class)) &&
                !Modifier.isAbstract(discoveredClass.getModifiers())) {

            if (ManifestHelper.isDebugEnabled()) {
                Log.i("Sugar", "domain class : " + discoveredClass.getSimpleName());
            }
            return discoveredClass;

        } else {
            return null;
        }
    }


    private static List<String> getAllClasses() throws PackageManager.NameNotFoundException, IOException {
        String packageName = ManifestHelper.getDomainPackageName();
        List<String> classNames = new ArrayList<>();
        try {
            List<String> allClasses = MultiDexHelper.getAllClasses();
            for (String classString : allClasses) {
                if (classString.startsWith(packageName)) classNames.add(classString);
            }

            // remove duplicates
            Set<String> hs = new LinkedHashSet<>(classNames);
            classNames.clear();
            classNames.addAll(hs);
        } catch (NullPointerException e) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources("");
            while (urls.hasMoreElements()) {
                List<String> fileNames = new ArrayList<>();
                String classDirectoryName = urls.nextElement().getFile();
                if (classDirectoryName.contains("bin") || classDirectoryName.contains("classes")
                        || classDirectoryName.contains("retrolambda")) {
                    File classDirectory = new File(classDirectoryName);
                    for (File filePath : classDirectory.listFiles()) {
                        populateFiles(filePath, fileNames, "");
                    }
                    for (String fileName : fileNames) {
                        if (fileName.startsWith(packageName)) classNames.add(fileName);
                    }
                }
            }
        }
//        } finally {
//            if (null != dexfile) dexfile.close();
//        }

        return classNames;
    }

    private static void populateFiles(File path, List<String> fileNames, String parent) {
        if (path.isDirectory()) {
            for (File newPath : path.listFiles()) {
                if ("".equals(parent)) {
                    populateFiles(newPath, fileNames, path.getName());
                } else {
                    populateFiles(newPath, fileNames, parent + "." + path.getName());
                }
            }
        } else {
            String pathName = path.getName();
            String classSuffix = ".class";
            pathName = pathName.endsWith(classSuffix) ?
                    pathName.substring(0, pathName.length() - classSuffix.length()) : pathName;
            if ("".equals(parent)) {
                fileNames.add(pathName);
            } else {
                fileNames.add(parent + "." + pathName);
            }
        }
    }

    private static String getSourcePath() throws PackageManager.NameNotFoundException {
        return ContextUtil.getPackageManager().getApplicationInfo(ContextUtil.getPackageName(), 0).sourceDir;
    }
}
