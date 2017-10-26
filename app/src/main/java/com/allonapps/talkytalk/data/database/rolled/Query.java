package com.allonapps.talkytalk.data.database.rolled;

/**
 * Created by michael on 10/26/17.
 */

public class Query {

    private String sql;

    public Query(Builder builder) {
        this.sql = builder.sql;
    }

    public String getSql() {
        return sql;
    }

    public static class Builder {

        private String sql = "";

        private final String tableName;

        private String where = "";

        public Builder(String tableName) {
            this.tableName = tableName;
        }

        public Builder selectAll() {
            sql = "SELECT * FROM " + tableName;
            return this;
        }

        public Builder whereEquals(String field, String value) {
            if (where.isEmpty()) {
                where += " WHERE " + field + " = '" + value + "' ";
            } else {
                where += "AND " + field + " = '" + value + "' ";
            }
            return this;
        }

        public Query build() {
            sql += where;
            return new Query(this);
        }


    }

}