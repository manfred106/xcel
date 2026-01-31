package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LengthValidator implements Validator {
    @Override
    public String getType() {
        return "length";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        if (value == null) {
            return Collections.emptyList();
        }
        String text = value.toString();
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Integer min = parseInteger(options.get("min"));
        Integer max = parseInteger(options.get("max"));
        if (min == null && max == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Length validator requires 'min' and/or 'max' option.", value));
        }

        int length = text.length();
        if (min != null && length < min) {
            return List.of(new ValidationError(rowIndex, fieldName, "LENGTH",
                "Row " + (rowIndex + 1) + ": Value length is below minimum (" + min + ").", value));
        }

        if (max != null && length > max) {
            return List.of(new ValidationError(rowIndex, fieldName, "LENGTH",
                "Row " + (rowIndex + 1) + ": Value length is above maximum (" + max + ").", value));
        }

        return Collections.emptyList();
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
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
}
