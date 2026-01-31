package com.lumion.xcel.validation.core.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class FieldConfig {
    private String fieldName;
    private String dataType;
    private boolean required = false;
    private List<ValidatorConfig> validators = new ArrayList<>();
}
