package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UniqueValidator implements Validator, StatefulValidator {
    private final Map<String, Set<Object>> seenByField = new HashMap<>();

    @Override
    public String getType() {
        return "unique";
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

        Set<Object> seen = seenByField.computeIfAbsent(fieldName, key -> new HashSet<>());
        if (seen.contains(value)) {
            return List.of(new ValidationError(rowIndex, fieldName, "UNIQUE",
                "Row " + (rowIndex + 1) + ": Value must be unique.", value));
        }

        seen.add(value);
        return Collections.emptyList();
    }

    @Override
    public Validator newInstance() {
        return new UniqueValidator();
    }
}
