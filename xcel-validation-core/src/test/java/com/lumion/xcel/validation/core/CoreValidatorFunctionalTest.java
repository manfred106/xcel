package com.lumion.xcel.validation.core;

import com.lumion.xcel.validation.core.config.FieldConfig;
import com.lumion.xcel.validation.core.config.ValidationConfig;
import com.lumion.xcel.validation.core.config.ValidatorConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreValidatorFunctionalTest {

    @Test
    void regexValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Code", validator("regex", Map.of("pattern", "^[A-Z]{3}$")));
        List<Map<String, Object>> rows = singleColumn("Code", "ABC", "123");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getRowIndex());
        assertEquals("Code", errors.get(0).getFieldName());
        assertEquals("REGEX", errors.get(0).getCode());
    }

    @Test
    void rangeValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Age", validator("range", Map.of("min", 10, "max", 20)));
        List<Map<String, Object>> rows = singleColumn("Age", 5, 15, 20);

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(0, errors.get(0).getRowIndex());
        assertEquals("Age", errors.get(0).getFieldName());
        assertEquals("RANGE", errors.get(0).getCode());
    }

    @Test
    void lengthValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Name", validator("length", Map.of("min", 3, "max", 5)));
        List<Map<String, Object>> rows = singleColumn("Name", "hi", "hello");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(0, errors.get(0).getRowIndex());
        assertEquals("Name", errors.get(0).getFieldName());
        assertEquals("LENGTH", errors.get(0).getCode());
    }

    @Test
    void dateTimeValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Date", validator("dateTime", Map.of("format", "yyyy-MM-dd")));
        List<Map<String, Object>> rows = singleColumn("Date", "2024-01-31", "31/01/2024");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getRowIndex());
        assertEquals("Date", errors.get(0).getFieldName());
        assertEquals("DATE_TIME", errors.get(0).getCode());
    }

    @Test
    void requiredValidatorValidatesNullAndBlank() {
        ValidationConfig config = configFor("Email", validator("required", Map.of()));
        List<Map<String, Object>> rows = singleColumn("Email", null, "  ", "ok");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(2, errors.size());
        assertEquals(0, errors.get(0).getRowIndex());
        assertEquals("Email", errors.get(0).getFieldName());
        assertEquals("REQUIRED", errors.get(0).getCode());
        assertEquals(1, errors.get(1).getRowIndex());
        assertEquals("Email", errors.get(1).getFieldName());
        assertEquals("REQUIRED", errors.get(1).getCode());
    }

    @Test
    void dataTypeValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Count", validator("dataType", Map.of("dataType", "Integer")));
        List<Map<String, Object>> rows = singleColumn("Count", 10, "oops");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getRowIndex());
        assertEquals("Count", errors.get(0).getFieldName());
        assertEquals("DATA_TYPE", errors.get(0).getCode());
    }

    @Test
    void fieldConfigRequiredAndDataTypeUseValidators() {
        FieldConfig fieldConfig = new FieldConfig()
            .setFieldName("Count")
            .setRequired(true)
            .setDataType("Integer");
        ValidationConfig config = new ValidationConfig().setFields(List.of(fieldConfig));
        List<Map<String, Object>> rows = singleColumn("Count", null, "oops");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(2, errors.size());
        assertEquals("REQUIRED", errors.get(0).getCode());
        assertEquals("DATA_TYPE", errors.get(1).getCode());
    }

    @Test
    void setValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Status", validator("set", Map.of("values", List.of("NEW", "DONE"))));
        List<Map<String, Object>> rows = singleColumn("Status", "NEW", "INVALID");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getRowIndex());
        assertEquals("Status", errors.get(0).getFieldName());
        assertEquals("SET", errors.get(0).getCode());
    }

    @Test
    void uniqueValidatorValidatesSingleColumn() {
        ValidationConfig config = configFor("Email", validator("unique", Map.of()));
        List<Map<String, Object>> rows = singleColumn("Email", "a@a.com", "b@b.com", "a@a.com");

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(2, errors.get(0).getRowIndex());
        assertEquals("Email", errors.get(0).getFieldName());
        assertEquals("UNIQUE", errors.get(0).getCode());
    }

    @Test
    void uniqueValidatorResetsPerValidationRun() {
        ValidationConfig config = configFor("Email", validator("unique", Map.of()));
        CoreValidator validator = new CoreValidator();

        List<ValidationError> first = validator.validate(config, singleColumn("Email", "a@a.com", "a@a.com"));
        List<ValidationError> second = validator.validate(config, singleColumn("Email", "a@a.com"));

        assertEquals(1, first.size());
        assertEquals(0, second.size());
    }

    @Test
    void conditionalValidatorValidatesMultipleFields() {
        ValidatorConfig conditionAge = new ValidatorConfig()
            .setType("dataType")
            .setOptions(Map.of("dataType", "Integer", "fieldName", "Age"));
        ValidatorConfig conditionStatus = new ValidatorConfig()
            .setType("set")
            .setOptions(Map.of("values", List.of("VIP"), "fieldName", "Status"));
        ValidatorConfig rangeValidator = new ValidatorConfig()
            .setType("range")
            .setOptions(Map.of("min", 1, "max", 10));
        ValidatorConfig conditional = new ValidatorConfig()
            .setType("conditional")
            .setOptions(Map.of(
                "mode", "all",
                "conditions", List.of(conditionAge, conditionStatus),
                "validators", List.of(rangeValidator)
            ));

        FieldConfig fieldConfig = new FieldConfig()
            .setFieldName("Discount")
            .setValidators(List.of(conditional));
        ValidationConfig config = new ValidationConfig().setFields(List.of(fieldConfig));

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(Map.of("Age", 20, "Status", "VIP", "Discount", 20));
        rows.add(Map.of("Age", 20, "Status", "REG", "Discount", 20));

        List<ValidationError> errors = new CoreValidator().validate(config, rows);

        assertEquals(1, errors.size());
        assertEquals(0, errors.get(0).getRowIndex());
        assertEquals("Discount", errors.get(0).getFieldName());
        assertEquals("RANGE", errors.get(0).getCode());
    }

    private ValidationConfig configFor(String fieldName, ValidatorConfig validatorConfig) {
        FieldConfig fieldConfig = new FieldConfig()
            .setFieldName(fieldName)
            .setValidators(List.of(validatorConfig));
        return new ValidationConfig().setFields(List.of(fieldConfig));
    }

    private ValidatorConfig validator(String type, Map<String, Object> options) {
        return new ValidatorConfig()
            .setType(type)
            .setOptions(options);
    }

    private List<Map<String, Object>> singleColumn(String header, Object... values) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object value : values) {
            Map<String, Object> row = new HashMap<>();
            row.put(header, value);
            rows.add(row);
        }
        return rows;
    }
}
