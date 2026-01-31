package com.lumion.xcel.validation.poi;

import com.lumion.xcel.validation.core.ValidationError;
import com.lumion.xcel.validation.core.config.FieldConfig;
import com.lumion.xcel.validation.core.config.ValidationConfig;
import com.lumion.xcel.validation.core.config.ValidatorConfig;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoiValidatorFunctionalTest {

    @Test
    void validatesSheetUsingHeaderRow() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Data");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Age");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(12);

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue(200);

            ValidationConfig config = new ValidationConfig().setFields(List.of(
                new FieldConfig()
                    .setFieldName("Age")
                    .setValidators(List.of(
                        new ValidatorConfig()
                            .setType("range")
                            .setOptions(Map.of("min", 10, "max", 150))
                    ))
            ));

            List<ValidationError> errors = new PoiValidator().validate(sheet, config);

            assertEquals(1, errors.size());
            assertEquals(2, errors.get(0).getRowIndex());
            assertEquals("Age", errors.get(0).getFieldName());
            assertEquals("RANGE", errors.get(0).getCode());
        }
    }
}
