package com.lumion.xcel.validation.core.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ValidationConfig {
    private List<FieldConfig> fields = new ArrayList<>();
}
