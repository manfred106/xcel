package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RangeValidator implements Validator {
    @Override
    public String getType() {
        return "range";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof String && ((String) value).isBlank()) {
            return Collections.emptyList();
        }

        Double min = parseNumber(options.get("min"));
        Double max = parseNumber(options.get("max"));
        if (min == null && max == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Range validator requires 'min' and/or 'max' option.", value));
        }

        Double numericValue = parseNumber(value);
        if (numericValue == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "RANGE",
                "Row " + (rowIndex + 1) + ": Value is not numeric.", value));
        }

        if (min != null && numericValue < min) {
            return List.of(new ValidationError(rowIndex, fieldName, "RANGE",
                "Row " + (rowIndex + 1) + ": Value is below minimum (" + min + ").", value));
        }

        if (max != null && numericValue > max) {
            return List.of(new ValidationError(rowIndex, fieldName, "RANGE",
                "Row " + (rowIndex + 1) + ": Value is above maximum (" + max + ").", value));
        }

        return Collections.emptyList();
    }

    private Double parseNumber(Object value) {
        if (value == null) {
            return null;
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
}
