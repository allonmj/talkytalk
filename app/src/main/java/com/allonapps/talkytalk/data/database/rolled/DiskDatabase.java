package com.allonapps.talkytalk.data.database.rolled;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by michael on 10/26/17.
 */

public class DiskDatabase<T, Q> extends SQLiteOpenHelper {

    private final DatabaseSchemaObject<T, Q> databaseSchemaObject;

    public DiskDatabase(Context context, String databaseName, int version, DatabaseSchemaObject<T, Q> databaseSchemaObject) {
        super(context, databaseName, null, version);
        this.databaseSchemaObject = databaseSchemaObject;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        buildTable(databaseSchemaObject, db);
    }

    private void buildTable(DatabaseSchemaObject schemaObject, SQLiteDatabase database) {
        Table.Builder tableBuilder = new Table.Builder(schemaObject.getFullTableName());

        if (!schemaObject.canReplicateForDifferentParent()) {
            tableBuilder.primaryKey(schemaObject.getPrimaryKeyColumn());
        }

        List<DatabaseSchemaObject.Data> dataColumnList = schemaObject.getDataColumns();
        for (DatabaseSchemaObject.Data data : dataColumnList) {
            tableBuilder = addField(database, tableBuilder, data, schemaObject.getFullTableName());
        }
        tableBuilder.addIntegerField(DatabaseSchemaObject.TIME_STAMP);
        tableBuilder.addTextField(DatabaseSchemaObject.PARENT_ID);

        if (schemaObject.canReplicateForDifferentParent()) {
            tableBuilder.addTextField(schemaObject.getPrimaryKeyColumn());
            tableBuilder.uniqueKeys(schemaObject.getPrimaryKeyColumn(), DatabaseSchemaObject.PARENT_ID);
        }

        try {
            database.execSQL(tableBuilder.build().getSql());
        } catch (Exception ignore) {
        }
    }

    public T getItem(Q query) {
        SQLiteDatabase database = getReadableDatabase();
        return databaseSchemaObject.createItem(database, query, null);
    }

    public List<T> getItemList(Q query) {
        SQLiteDatabase database = getReadableDatabase();
        return databaseSchemaObject.createItemList(database, query, null);
    }

    public long getLastUpdateTime() {
        SQLiteDatabase database = getReadableDatabase();
        return databaseSchemaObject.getLastUpdateTime(database);
    }

    public void updateItem(T item) {
        SQLiteDatabase database = null;
        try {
            database = getWritableDatabase();
            database.beginTransaction();

            databaseSchemaObject.updateDatabase(database, item, null);

            database.setTransactionSuccessful();
        } finally {
            if (database != null) {
                database.endTransaction();
                database.close();
            }
        }
    }

    public void updateItemList(List<T> itemList) {
        SQLiteDatabase database = null;
        try {
            database = getWritableDatabase();
            database.beginTransaction();
            for (T item : itemList) {
                databaseSchemaObject.updateDatabase(database, item, null);
            }
            database.setTransactionSuccessful();
        } finally {
            if (database != null) {
                database.endTransaction();
                database.close();
            }
        }
    }


    private Table.Builder addField(SQLiteDatabase sqLiteDatabase, Table.Builder tableBuilder, DatabaseSchemaObject.Data data, String tableName) {
        switch (data.dataType) {
            case STRING:
                tableBuilder.addTextField(data.name);
                break;
            case INTEGER:
            case LONG:
                tableBuilder.addIntegerField(data.name);
                break;
            case DOUBLE:
                tableBuilder.addRealField(data.name);
                break;
            case DATABASE_OBJECT:
            case LIST_DATABASE_OBJECT:
                // add a text field which will have an identifier for the db object
                tableBuilder.addTextField(data.name);

                // recursively build the table for the db object
                data.databaseSchemaObject.setParentTableName(tableName);
                buildTable(data.databaseSchemaObject, sqLiteDatabase);
                break;
            case LIST_INTEGER:
                tableBuilder.addTextField(data.name);
                break;
        }
        return tableBuilder;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void flush() {
        SQLiteDatabase database = getWritableDatabase();
        databaseSchemaObject.flush(database);
    }
}