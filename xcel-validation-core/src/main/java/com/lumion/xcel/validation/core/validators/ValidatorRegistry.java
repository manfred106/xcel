package com.lumion.xcel.validation.core.validators;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ValidatorRegistry {
    private final Map<String, Validator> validators = new HashMap<>();

    public ValidatorRegistry() {
        this(true);
    }

    private ValidatorRegistry(boolean withDefaults) {
        if (withDefaults) {
        register(new RegexValidator());
        register(new RangeValidator());
        register(new LengthValidator());
        register(new DateTimeValidator());
        register(new SetValidator());
        register(new RequiredValidator());
        register(new DataTypeValidator());
        register(new ConditionalValidator(this));
        register(new UniqueValidator());
        }
    }

    public void register(Validator validator) {
        if (validator == null || validator.getType() == null) {
            return;
        }
        validators.put(validator.getType().toLowerCase(Locale.ROOT), validator);
    }

    public Validator get(String type) {
        if (type == null) {
            return null;
        }
        return validators.get(type.toLowerCase(Locale.ROOT));
    }

    public ValidatorRegistry createSession() {
        ValidatorRegistry session = new ValidatorRegistry(false);
        for (Validator validator : validators.values()) {
            if (validator instanceof ConditionalValidator) {
                session.register(new ConditionalValidator(session));
            } else if (validator instanceof StatefulValidator) {
                session.register(((StatefulValidator) validator).newInstance());
            } else {
                session.register(validator);
            }
        }
        return session;
    }
}
