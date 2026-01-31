package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidator implements Validator {
    @Override
    public String getType() {
        return "regex";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        Object patternValue = options.get("pattern");
        if (patternValue == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Regex validator requires 'pattern' option.", value));
        }

        Pattern pattern;
        try {
            pattern = Pattern.compile(patternValue.toString());
        } catch (PatternSyntaxException ex) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Regex pattern is invalid: " + ex.getMessage(), value));
        }

        if (value == null) {
            return Collections.emptyList();
        }
        String text = value.toString();
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        if (!pattern.matcher(text).matches()) {
            return List.of(new ValidationError(rowIndex, fieldName, "REGEX",
                "Row " + (rowIndex + 1) + ": Value does not match regex pattern (" + pattern.pattern() + ").", value));
        }

        return Collections.emptyList();
    }
}
