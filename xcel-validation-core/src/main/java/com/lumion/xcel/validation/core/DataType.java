package com.lumion.xcel.validation.core;

public enum DataType {
    STRING("String"),
    INTEGER("Integer"),
    DOUBLE("Double"),
    FLOAT("Float"),
    BOOLEAN("Boolean");

    private final String displayName;

    DataType(String displayName) {
        this.displayName = displayName;
    }

    public static DataType fromName(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        for (DataType type : values()) {
            if (type.displayName.equalsIgnoreCase(trimmed) || type.name().equalsIgnoreCase(trimmed)) {
                return type;
            }
        }
        return null;
    }

    public Object coerce(Object value) {
        if (value == null) {
            return null;
        }
        switch (this) {
            case STRING:
                return String.valueOf(value);
            case INTEGER:
                return coerceInteger(value);
            case DOUBLE:
                return coerceDouble(value);
            case FLOAT:
                return coerceFloat(value);
            case BOOLEAN:
                return coerceBoolean(value);
            default:
                return value;
        }
    }

    private Integer coerceInteger(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Double coerceDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Float coerceFloat(Object value) {
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        try {
            return Float.parseFloat(value.toString().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Boolean coerceBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String normalized = value.toString().trim().toLowerCase();
        if ("true".equals(normalized)) {
            return true;
        }
        if ("false".equals(normalized)) {
            return false;
        }
        return null;
    }
}
