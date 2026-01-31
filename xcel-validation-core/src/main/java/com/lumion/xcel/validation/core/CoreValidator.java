package com.lumion.xcel.validation.core;

public class CoreValidator {
    public boolean isValid(String input) {
        return input != null && !input.isBlank();
    }
}
