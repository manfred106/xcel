package com.lumion.xcel.validation.core;

import com.lumion.xcel.validation.core.config.ValidationConfig;
import com.lumion.xcel.validation.core.validators.ValidatorRegistry;

import java.util.List;
import java.util.Map;

public class ValidationSession {
    private final CoreValidator coreValidator;
    private final ValidationConfig config;
    private final ValidatorRegistry registry;

    ValidationSession(CoreValidator coreValidator, ValidationConfig config, ValidatorRegistry registry) {
        this.coreValidator = coreValidator;
        this.config = config;
        this.registry = registry;
    }

    public List<ValidationError> validateRow(Map<String, Object> row, int rowIndex) {
        return coreValidator.validateRowInternal(config, row, rowIndex, registry);
    }
}
