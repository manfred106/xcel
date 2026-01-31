package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SetValidator implements Validator {
    @Override
    public String getType() {
        return "set";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        Object valuesOption = options.get("values");
        if (valuesOption == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Set validator requires 'values' option.", value));
        }

        if (!(valuesOption instanceof Collection<?>)) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Set validator 'values' must be a collection.", value));
        }

        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof String && ((String) value).isBlank()) {
            return Collections.emptyList();
        }

        Collection<?> allowedValues = (Collection<?>) valuesOption;
        if (!allowedValues.contains(value)) {
            return List.of(new ValidationError(rowIndex, fieldName, "SET",
                "Row " + (rowIndex + 1) + ": Value is not in allowed set " + allowedValues + ".", value));
        }

        return Collections.emptyList();
    }
}
