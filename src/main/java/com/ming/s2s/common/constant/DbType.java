package com.ming.s2s.common.constant;

/**
 * Database type constants
 */
public enum DbType {
    MYSQL("mysql"),
    POSTGRESQL("postgresql"),
    ORACLE("oracle");

    private final String value;

    DbType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

