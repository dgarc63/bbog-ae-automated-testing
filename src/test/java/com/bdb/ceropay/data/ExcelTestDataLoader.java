package com.bdb.ceropay.data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

public class ExcelTestDataLoader implements TestDataLoader {

    private final String resourcePath; // ej: "testdata/authData.xlsx"
    private final String sheetName;    // ej: "auth"
    private final int rowIndex;        // ej: 1 (fila 2, si 0 es header)

    public ExcelTestDataLoader(String resourcePath, String sheetName, int rowIndex) {
        this.resourcePath = resourcePath;
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
    }

    @Override
    public TestData load() {
        TestData defaults = TestData.defaults();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            // Si no existe el archivo -> defaults
            if (is == null) return defaults;

            try (Workbook wb = new XSSFWorkbook(is)) {
                Sheet sheet = wb.getSheet(sheetName);
                if (sheet == null) return defaults;

                Row header = sheet.getRow(0);
                Row row = sheet.getRow(rowIndex);
                if (header == null || row == null) return defaults;

                int colLastName = findCol(header, "lastName", "apellido");
                int colId = findCol(header, "identification", "cedula");

                String lastName = cellAsString(row.getCell(colLastName)).trim();
                String identification = cellAsString(row.getCell(colId)).trim();

                if (lastName.isBlank() || identification.isBlank()) return defaults;

                return new TestData(lastName, identification);
            }
        } catch (Exception e) {
            // Cualquier error leyendo Excel -> defaults (no rompe la ejecución)
            return defaults;
        }
    }

    private int findCol(Row header, String... names) {
        for (Cell c : header) {
            String v = c.getStringCellValue();
            if (v == null) continue;
            String normalized = v.trim().toLowerCase();
            for (String name : names) {
                if (normalized.equals(name.toLowerCase())) {
                    return c.getColumnIndex();
                }
            }
        }
        // Si no están las columnas, lanzamos y el catch devuelve defaults
        throw new IllegalArgumentException("No se encontraron columnas esperadas en el header.");
    }

    private String cellAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                // Para cédulas, usualmente es mejor evitar decimales
                long asLong = (long) cell.getNumericCellValue();
                yield String.valueOf(asLong);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
