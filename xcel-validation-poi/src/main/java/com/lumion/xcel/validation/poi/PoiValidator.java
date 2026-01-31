package com.lumion.xcel.validation.poi;

import com.lumion.xcel.validation.core.CoreValidator;

public class PoiValidator {
    private final CoreValidator coreValidator = new CoreValidator();

    public boolean isWorkbookNameValid(String name) {
        return coreValidator.isValid(name);
    }
}
