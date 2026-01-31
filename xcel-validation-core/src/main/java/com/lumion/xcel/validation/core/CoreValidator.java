package com.lumion.xcel.validation.core;

import com.lumion.xcel.validation.core.config.FieldConfig;
import com.lumion.xcel.validation.core.config.ValidationConfig;
import com.lumion.xcel.validation.core.config.ValidatorConfig;
import com.lumion.xcel.validation.core.validators.Validator;
import com.lumion.xcel.validation.core.validators.ValidatorRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CoreValidator {
    private final ValidatorRegistry validatorRegistry = new ValidatorRegistry();

    public ValidationConfig fromJson(String json, com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        if (objectMapper == null) {
            throw new IllegalArgumentException("ObjectMapper is required.");
        }
        if (json == null || json.isBlank()) {
            return new ValidationConfig();
        }
        try {
            return objectMapper.readValue(json, ValidationConfig.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JSON config: " + ex.getMessage(), ex);
        }
    }

    public List<ValidationError> validate(ValidationConfig config, List<Map<String, Object>> rows) {
        if (config == null || config.getFields() == null || config.getFields().isEmpty() || rows == null) {
            return Collections.emptyList();
        }

        ValidatorRegistry sessionRegistry = validatorRegistry.createSession();
        List<ValidationError> errors = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            int rowIndex = i;
            errors.addAll(validateRowInternal(config, rows.get(i), rowIndex, sessionRegistry));
        }

        return errors;
    }

    public ValidationSession startSession(ValidationConfig config) {
        return new ValidationSession(this, config, validatorRegistry.createSession());
    }

    public List<ValidationError> validateRow(ValidationConfig config, Map<String, Object> row, int rowIndex) {
        ValidatorRegistry sessionRegistry = validatorRegistry.createSession();
        return validateRowInternal(config, row, rowIndex, sessionRegistry);
    }

    List<ValidationError> validateRowInternal(ValidationConfig config, Map<String, Object> row, int rowIndex,
                                              ValidatorRegistry sessionRegistry) {
        if (config == null || config.getFields() == null || config.getFields().isEmpty()) {
            return Collections.emptyList();
        }

        List<ValidationError> errors = new ArrayList<>();
        for (FieldConfig field : config.getFields()) {
            if (field.getFieldName() == null || field.getFieldName().isBlank()) {
                continue;
            }
            Object value = row == null ? null : row.get(field.getFieldName());

            List<ValidatorConfig> validators = new ArrayList<>();
            if (field.isRequired()) {
                validators.add(new ValidatorConfig().setType("required"));
            }
            if (field.getDataType() != null && !field.getDataType().isBlank()) {
                validators.add(new ValidatorConfig()
                    .setType("dataType")
                    .setOptions(Map.of("dataType", field.getDataType())));
            }
            if (field.getValidators() != null) {
                validators.addAll(field.getValidators());
            }

            for (ValidatorConfig validatorConfig : validators) {
                if (validatorConfig == null || validatorConfig.getType() == null) {
                    continue;
                }
                Validator validator = sessionRegistry.get(validatorConfig.getType());
                if (validator == null) {
                    errors.add(new ValidationError(rowIndex, field.getFieldName(), "UNKNOWN_VALIDATOR",
                        "Row " + (rowIndex + 1) + ": Unknown validator type: " + validatorConfig.getType(), value));
                    continue;
                }
                Map<String, Object> options = validatorConfig.getOptions();
                if (options == null) {
                    options = Collections.emptyMap();
                }
                    errors.addAll(validator.validate(rowIndex, field.getFieldName(), value, row, options));
                }
            }

        return errors;
    }
}
