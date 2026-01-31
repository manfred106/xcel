package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DateTimeValidator implements Validator {
    @Override
    public String getType() {
        return "dateTime";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        Object formatValue = options.get("format");
        if (formatValue == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": DateTime validator requires 'format' option.", value));
        }

        if (value == null) {
            return Collections.emptyList();
        }

        if (value instanceof LocalDate) {
            return Collections.emptyList();
        }

        String text = value.toString().trim();
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        DateTimeFormatter formatter;
        try {
            formatter = DateTimeFormatter.ofPattern(formatValue.toString());
        } catch (IllegalArgumentException ex) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": DateTime format is invalid: " + ex.getMessage(), value));
        }

        try {
            LocalDate.parse(text, formatter);
        } catch (DateTimeParseException ex) {
            return List.of(new ValidationError(rowIndex, fieldName, "DATE_TIME",
                "Row " + (rowIndex + 1) + ": Value does not match date-time format (" + formatValue + ").", value));
        }

        return Collections.emptyList();
    }
}
