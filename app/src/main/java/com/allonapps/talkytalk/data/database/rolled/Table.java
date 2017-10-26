package com.allonapps.talkytalk.data.database.rolled;

/**
 * Created by michael on 10/26/17.
 */

public class Table {

    private String sql;

    private Table(Table.Builder builder) {
        this.sql = builder.sql;
    }

    public String getSql() {
        return sql;
    }

    public static class Builder {
        private String sql;

        private String primaryKey;
        private boolean firstField = true;

        public Builder(String tableName) {
            sql = "CREATE TABLE IF NOT EXISTS " + tableName
                    + " (";
        }

        public Builder primaryKey(String primaryKey) {
            this.primaryKey = primaryKey;
            sql += primaryKey + " TEXT PRIMARY KEY";
            return this;
        }

        public Builder addTextField(String name) {
            sql += createCommaSeparator() + name + " TEXT";
            firstField = false;
            return this;
        }

        public Builder addRealField(String name) {
            sql += createCommaSeparator() + name + " REAL";
            firstField = false;
            return this;
        }

        public Builder addIntegerField(String name) {
            sql += createCommaSeparator() + name + " INTEGER";
            firstField = false;
            return this;
        }

        public Builder uniqueKeys(String key1, String key2) {
            sql += ", UNIQUE (" + key1 + ", " + key2 + ") ON CONFLICT REPLACE";
            return this;
        }

        private String createCommaSeparator() {
            if (firstField && (primaryKey == null || primaryKey.isEmpty())) {
                return " ";
            } else {
                return ", ";
            }
        }

        public Table build() {
            // close the table and return
            sql += ")";
            return new Table(this);
        }
    }

}