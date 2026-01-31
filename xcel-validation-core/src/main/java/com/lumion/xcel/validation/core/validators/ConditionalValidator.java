package com.lumion.xcel.validation.core.validators;

import com.lumion.xcel.validation.core.ValidationError;
import com.lumion.xcel.validation.core.config.ValidatorConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConditionalValidator implements Validator {
    private final ValidatorRegistry registry;

    public ConditionalValidator(ValidatorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getType() {
        return "conditional";
    }

    @Override
    public List<ValidationError> validate(int rowIndex, String fieldName, Object value, Map<String, Object> row,
                                          Map<String, Object> options) {
        Object conditionsOption = options.get("conditions");
        Object validatorsOption = options.get("validators");
        if (conditionsOption == null || validatorsOption == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Conditional validator requires 'conditions' and 'validators'.", value));
        }

        List<ValidatorConfig> conditions = toValidatorConfigs(conditionsOption, rowIndex, fieldName, value);
        List<ValidatorConfig> validators = toValidatorConfigs(validatorsOption, rowIndex, fieldName, value);
        if (conditions == null || validators == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "INVALID_VALIDATOR_CONFIG",
                "Row " + (rowIndex + 1) + ": Conditional validator options are invalid.", value));
        }

        Mode mode = Mode.from(options.get("mode"));
        boolean conditionMet = evaluateConditions(mode, conditions, rowIndex, fieldName, value, row);
        if (!conditionMet) {
            return Collections.emptyList();
        }

        return runValidators(validators, rowIndex, fieldName, value, row);
    }

    private boolean evaluateConditions(Mode mode, List<ValidatorConfig> conditions, int rowIndex, String fieldName,
                                       Object value, Map<String, Object> row) {
        if (conditions.isEmpty()) {
            return true;
        }

        if (mode == Mode.ANY) {
            for (ValidatorConfig condition : conditions) {
                if (condition == null || condition.getType() == null) {
                    continue;
                }
                ValidationTarget target = resolveTarget(condition, fieldName, value, row);
                if (runValidator(condition, rowIndex, target.fieldName, target.value, row).isEmpty()) {
                    return true;
                }
            }
            return false;
        }

        for (ValidatorConfig condition : conditions) {
            if (condition == null || condition.getType() == null) {
                continue;
            }
            ValidationTarget target = resolveTarget(condition, fieldName, value, row);
            if (!runValidator(condition, rowIndex, target.fieldName, target.value, row).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private List<ValidationError> runValidators(List<ValidatorConfig> validators, int rowIndex, String fieldName,
                                                Object value, Map<String, Object> row) {
        List<ValidationError> errors = new ArrayList<>();
        for (ValidatorConfig validatorConfig : validators) {
            if (validatorConfig == null || validatorConfig.getType() == null) {
                continue;
            }
            ValidationTarget target = resolveTarget(validatorConfig, fieldName, value, row);
            errors.addAll(runValidator(validatorConfig, rowIndex, target.fieldName, target.value, row));
        }
        return errors;
    }

    private List<ValidationError> runValidator(ValidatorConfig config, int rowIndex, String fieldName, Object value,
                                               Map<String, Object> row) {
        Validator validator = registry.get(config.getType());
        if (validator == null) {
            return List.of(new ValidationError(rowIndex, fieldName, "UNKNOWN_VALIDATOR",
                "Row " + (rowIndex + 1) + ": Unknown validator type: " + config.getType(), value));
        }
        Map<String, Object> options = config.getOptions();
        if (options == null) {
            options = Collections.emptyMap();
        }
        return validator.validate(rowIndex, fieldName, value, row, options);
    }

    private ValidationTarget resolveTarget(ValidatorConfig config, String defaultFieldName, Object defaultValue,
                                           Map<String, Object> row) {
        String fieldOverride = null;
        if (config.getOptions() != null) {
            Object fieldOption = config.getOptions().get("fieldName");
            if (fieldOption != null) {
                fieldOverride = fieldOption.toString();
            }
        }
        String fieldName = fieldOverride == null || fieldOverride.isBlank() ? defaultFieldName : fieldOverride;
        Object value = defaultValue;
        if (fieldOverride != null && row != null) {
            value = row.get(fieldName);
        }
        return new ValidationTarget(fieldName, value);
    }

    private List<ValidatorConfig> toValidatorConfigs(Object option, int rowIndex, String fieldName, Object value) {
        if (option instanceof List<?>) {
            List<?> list = (List<?>) option;
            List<ValidatorConfig> configs = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof ValidatorConfig) {
                    configs.add((ValidatorConfig) item);
                } else if (item instanceof Map<?, ?>) {
                    ValidatorConfig config = fromMap((Map<?, ?>) item);
                    if (config != null) {
                        configs.add(config);
                    }
                }
            }
            return configs;
        }
        return null;
    }

    private ValidatorConfig fromMap(Map<?, ?> map) {
        Object type = map.get("type");
        if (type == null) {
            return null;
        }
        ValidatorConfig config = new ValidatorConfig().setType(type.toString());
        Object options = map.get("options");
        Map<String, Object> optionMap;
        if (options instanceof Map<?, ?>) {
            optionMap = new java.util.HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) options).entrySet()) {
                optionMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        } else {
            optionMap = new java.util.HashMap<>();
        }
        Object fieldName = map.get("fieldName");
        if (fieldName != null) {
            optionMap.put("fieldName", fieldName.toString());
        }
        config.setOptions(optionMap);
        return config;
    }

    private static class ValidationTarget {
        private final String fieldName;
        private final Object value;

        private ValidationTarget(String fieldName, Object value) {
            this.fieldName = fieldName;
            this.value = value;
        }
    }

    private enum Mode {
        ALL,
        ANY;

        private static Mode from(Object raw) {
            if (raw == null) {
                return ALL;
            }
            String value = raw.toString().trim().toUpperCase();
            for (Mode mode : values()) {
                if (mode.name().equals(value)) {
                    return mode;
                }
            }
            return ALL;
        }
    }
}
