package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;

import java.util.List;
import java.util.Map;

public interface Validator {
    String getType();

    List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                   Map<String, Object> options);
}
