package com.lumion.xcel.validation.core;

public class ValidationError {
    private final int rowIndex;
    private final String fieldName;
    private final String code;
    private final String message;
    private final Object value;

    public ValidationError(int rowIndex, String fieldName, String code, String message, Object value) {
        this.rowIndex = rowIndex;
        this.fieldName = fieldName;
        this.code = code;
        this.message = message;
        this.value = value;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getValue() {
        return value;
    }
}
