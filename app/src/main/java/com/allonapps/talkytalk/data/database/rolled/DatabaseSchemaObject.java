package com.allonapps.talkytalk.data.database.rolled;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by michael on 10/26/17.
 */

public abstract class DatabaseSchemaObject<T, Q> {

    public static final String TIME_STAMP = "timeStamp";

    public static final String PARENT_ID = "parentId";

    protected abstract String getDatabaseName();

    public abstract String getPrimaryKeyColumn();

    public abstract List<Data> getDataColumns();

    protected abstract Object getFieldObject(Data data, T object);

    protected abstract T createItem(Map<Data, Object> objectMap);

    protected abstract String getUniqueId(T object);

    // determines if we can create a new one of these objects
    // in the database if it has a different parent (essentially
    // it makes PARENT_ID a second primary key
    public boolean canReplicateForDifferentParent() {
        return false;
    }

    protected String parentTableName = null;

    public void setParentTableName(String parentTableName) {
        this.parentTableName = parentTableName;
    }

    public String getFullTableName() {
        if (parentTableName == null) {
            return getDatabaseName();
        }
        return parentTableName + "_" + getDatabaseName();
    }

    public void updateDatabaseList(SQLiteDatabase database, List<T> objectList, String parentId) {
        for (T obj : objectList) {
            updateDatabase(database, obj, parentId);
        }
    }

    public void updateDatabase(SQLiteDatabase database, T object, String parentId) {
        if (object == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(TIME_STAMP, System.currentTimeMillis());
        if (parentId != null) {
            contentValues.put(PARENT_ID, parentId);
        }
        for (Data data : getAllDataColumns()) {
            switch (data.dataType) {
                case STRING:
                    contentValues.put(data.name, (String) getFieldObject(data, object));
                    break;
                case INTEGER:
                    contentValues.put(data.name, (Integer) getFieldObject(data, object));
                    break;
                case LONG:
                    contentValues.put(data.name, (Long) getFieldObject(data, object));
                    break;
                case DOUBLE:
                    contentValues.put(data.name, (Double) getFieldObject(data, object));
                    break;
                case DATABASE_OBJECT:
                    Object obj = getFieldObject(data, object);
                    if (obj != null) {
                        data.databaseSchemaObject.setParentTableName(getFullTableName());
                        data.databaseSchemaObject.updateDatabase(database, obj, getUniqueId(object));
                        contentValues.put(data.name, data.databaseSchemaObject.getFullTableName());
                    }
                    break;
                case LIST_INTEGER:
                    List<Integer> integerList = (List<Integer>) getFieldObject(data, object);
                    contentValues.put(data.name, createIntegerListString(integerList));
                    break;
                case LIST_DOUBLE:
                    break;
                case LIST_LONG:
                    break;
                case LIST_DATABASE_OBJECT:
                    List<Object> objList = (List<Object>) getFieldObject(data, object);
                    data.databaseSchemaObject.setParentTableName(getFullTableName());
                    data.databaseSchemaObject.updateDatabaseList(database, objList, getUniqueId(object));
                    contentValues.put(data.name, data.databaseSchemaObject.getFullTableName());
                    break;
            }
        }
        database.replace(getFullTableName(), null, contentValues);
    }

    protected Query buildQuery(Q query) {
        // override in subclass for custom behaviour
        return null;
    }

    public long getLastUpdateTime(SQLiteDatabase database) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery("SELECT * FROM " + getFullTableName(), new String[]{});
            while (cursor.moveToNext()) {
                long timeStamp = safeGet(cursor, TIME_STAMP, 0L);
                return timeStamp;
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0L;
    }

    public T createItem(SQLiteDatabase database, Q query, String parentId) {
        Cursor cursor = null;
        try {
            Map<Data, Object> dataObjectMap = new HashMap<>();
            Query sqlQuery = null;
            if (query != null) {
                sqlQuery = buildQuery(query);
            }
            if (sqlQuery != null) {
                cursor = database.rawQuery(sqlQuery.getSql(), new String[]{});
            } else if (parentId == null || parentId.isEmpty()) {
                cursor = database.rawQuery("SELECT * FROM " + getFullTableName(), new String[]{});
            } else {
                cursor = database.rawQuery("SELECT * FROM " + getFullTableName() + " WHERE " + PARENT_ID + " = " + "'" + parentId + "'", new String[]{});
            }
            while (cursor.moveToNext()) {
                return createOneItem(database, dataObjectMap, cursor);
            }


        } catch (Exception ignore) {

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public List<T> createItemList(SQLiteDatabase database, Q query, String parentId) {
        List<T> itemList = new ArrayList<>();
        Cursor cursor = null;
        try {
            Map<Data, Object> dataObjectMap = new HashMap<>();
            Query sqlQuery = null;
            if (query != null) {
                sqlQuery = buildQuery(query);
            }
            if (sqlQuery != null) {
                cursor = database.rawQuery(sqlQuery.getSql(), new String[]{});
            } else if (parentId == null || parentId.isEmpty()) {
                cursor = database.rawQuery("SELECT * FROM " + getFullTableName(), new String[]{});
            } else {
                cursor = database.rawQuery("SELECT * FROM " + getFullTableName() + " WHERE " + PARENT_ID + " = " + "'" + parentId + "'", new String[]{});
            }
            while (cursor.moveToNext()) {
                itemList.add(createOneItem(database, dataObjectMap, cursor));
            }

        } catch (Exception ignore) {

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return itemList;
    }

    private List<Data> getAllDataColumns() {
        List<Data> dataList = getDataColumns();
        dataList.add(new Data(getPrimaryKeyColumn(), Data.DataType.STRING, null));
        return dataList;
    }

    private String getUniqueId(Cursor cursor) {
        return safeGet(cursor, getPrimaryKeyColumn(), "");
    }

    private T createOneItem(SQLiteDatabase database, Map<Data, Object> dataObjectMap, Cursor cursor) {
        String parentId = getUniqueId(cursor);
        for (Data data : getAllDataColumns()) {
            switch (data.dataType) {
                case STRING:
                    String str = safeGet(cursor, data.name, "");
                    dataObjectMap.put(data, str);
                    break;
                case INTEGER:
                    Integer integer = safeGet(cursor, data.name, 0);
                    dataObjectMap.put(data, integer);
                    break;
                case LONG:
                    Long lng = safeGet(cursor, data.name, 0L);
                    dataObjectMap.put(data, lng);
                    break;
                case DOUBLE:
                    Double dbl = safeGet(cursor, data.name, 0D);
                    dataObjectMap.put(data, dbl);
                    break;
                case DATABASE_OBJECT:
                    data.databaseSchemaObject.setParentTableName(getFullTableName());
                    Object obj = data.databaseSchemaObject.createItem(database, null, parentId);
                    dataObjectMap.put(data, obj);
                    break;
                case LIST_INTEGER:
                    List<Integer> integerList = safeGet(cursor, data.name, new ArrayList<Integer>());
                    dataObjectMap.put(data, integerList);
                    break;
                case LIST_DOUBLE:
                    break;
                case LIST_LONG:
                    break;
                case LIST_DATABASE_OBJECT:
                    data.databaseSchemaObject.setParentTableName(getFullTableName());
                    List<Object> objectList = data.databaseSchemaObject.createItemList(database, null, parentId);
                    dataObjectMap.put(data, objectList);
                    break;
            }
        }
        return createItem(dataObjectMap);
    }

    private static String safeGet(Cursor cursor, String columnName, String defaultVal) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0) {
            return defaultVal;
        }
        String val = cursor.getString(index);
        if (val != null) {
            return val;
        }
        return defaultVal;
    }

    private static int safeGet(Cursor cursor, String columnName, int defaultVal) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0) {
            return defaultVal;
        }
        return cursor.getInt(index);
    }

    private static double safeGet(Cursor cursor, String columnName, double defaultVal) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0) {
            return defaultVal;
        }
        return cursor.getDouble(index);
    }

    private static long safeGet(Cursor cursor, String columnName, long defaultVal) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0) {
            return defaultVal;
        }
        return cursor.getLong(index);
    }

    private static List<Integer> safeGet(Cursor cursor, String columnName, List<Integer> defaultVal) {
        int index = cursor.getColumnIndex(columnName);
        if (index < 0) {
            return defaultVal;
        }
        String str = cursor.getString(index);
        return createIntegerList(str);
    }

    public void flush(SQLiteDatabase database) {
        for (Data data : getAllDataColumns()) {
            if (Data.DataType.DATABASE_OBJECT.equals(data.dataType)
                    || Data.DataType.LIST_DATABASE_OBJECT.equals(data.dataType)) {
                data.databaseSchemaObject.setParentTableName(getFullTableName());
                data.databaseSchemaObject.flush(database);
            }
        }
        database.delete(getFullTableName(), null, null);
    }

    public static class Data {

        public enum DataType {
            STRING,
            INTEGER,
            LONG,
            DOUBLE,
            DATABASE_OBJECT,
            LIST_INTEGER,
            LIST_DOUBLE,
            LIST_LONG,
            LIST_DATABASE_OBJECT
        }

        public final String name;
        public final DataType dataType;
        public
        @Nullable
        final DatabaseSchemaObject databaseSchemaObject;

        public Data(String name, DataType dataType) {
            this(name, dataType, null);
        }

        public Data(String name, DataType dataType, @Nullable DatabaseSchemaObject databaseSchemaObject) {
            this.name = name;
            this.dataType = dataType;
            this.databaseSchemaObject = databaseSchemaObject;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (getClass() != obj.getClass()) {
                return false;
            }

            Data that = (Data) obj;

            return name.equals(that.name);
        }


    }

    private static String createIntegerListString(List<Integer> integerList) {
        if (integerList == null) {
            return "";
        }
        String intListString = "";
        for (int i = 0; i < integerList.size(); i++) {
            Object obj = integerList.get(i);
            if (obj instanceof Integer) {
                Integer integer = (Integer) obj;
                if (i == integerList.size() - 1) {
                    intListString += String.valueOf(integer);
                } else {
                    intListString += String.valueOf(integer) + ",";
                }
            }
        }

        return intListString;
    }

    private static List<Integer> createIntegerList(String str) {
        String[] splitInt = str.split(",");
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < splitInt.length; i++) {
            try {
                integerList.add(Integer.parseInt(splitInt[i]));
            } catch (NumberFormatException ignore) {
                // do nothing
            }
        }
        return integerList;
    }

}