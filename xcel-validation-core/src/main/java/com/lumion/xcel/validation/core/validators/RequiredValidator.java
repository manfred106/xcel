package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequiredValidator implements Validator {
    @Override
    public String getType() {
        return "required";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        if (value == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "REQUIRED",
                "Row " + (rowIndex + 1) + ": Value is required.", value));
        }
        if (value instanceof String && ((String) value).isBlank()) {
            return List.of(new ValidationError(rowIndex, fieldName, "REQUIRED",
                "Row " + (rowIndex + 1) + ": Value is required.", value));
        }
        return Collections.emptyList();
    }
}
