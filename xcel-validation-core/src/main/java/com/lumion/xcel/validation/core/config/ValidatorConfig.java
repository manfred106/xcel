package com.lumion.xcel.validation.core.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ValidatorConfig {
    private String type;
    private Map<String, Object> options = new HashMap<>();
}
