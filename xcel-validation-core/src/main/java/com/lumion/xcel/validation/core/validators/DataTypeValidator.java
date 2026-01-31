package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.DataType;
import com.lumion.xcel.validation.core.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataTypeValidator implements Validator {
    @Override
    public String getType() {
        return "dataType";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        Object typeOption = options.get("dataType");
        if (typeOption == null) {
            typeOption = options.get("type");
        }
        if (typeOption == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": DataType validator requires 'dataType' option.", value));
        }

        DataType dataType = DataType.fromName(typeOption.toString());
        if (dataType == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Unknown data type: " + typeOption + ".", value));
        }

        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof String && ((String) value).isBlank()) {
            return Collections.emptyList();
        }

        Object coerced = dataType.coerce(value);
        if (coerced == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "DATA_TYPE",
                "Row " + (rowIndex + 1) + ": Value does not match data type (" + typeOption + ").", value));
        }

        return Collections.emptyList();
    }
}
