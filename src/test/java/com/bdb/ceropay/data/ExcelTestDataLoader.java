package com.bdb.ceropay.data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.Locale;

public class ExcelTestDataLoader implements TestDataLoader {

    private final String resourcePath; // ej: "testdata/authData.xlsx"
    private final String sheetName;    // ej: "auth"
    private final int rowIndex;        // ej: 1 (fila 2, si 0 es header)

    private final DataFormatter formatter = new DataFormatter(Locale.US);

    public ExcelTestDataLoader(String resourcePath, String sheetName, int rowIndex) {
        this.resourcePath = resourcePath;
        this.sheetName = sheetName;
        this.rowIndex = rowIndex;
    }

    @Override
    public TestData load() {
        TestData defaults = TestData.defaults();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return defaults;

            try (Workbook wb = new XSSFWorkbook(is)) {
                Sheet sheet = wb.getSheet(sheetName);
                if (sheet == null) return defaults;

                Row header = sheet.getRow(0);
                Row row = sheet.getRow(rowIndex);
                if (header == null || row == null) return defaults;

                // --- columnas (con alias) ---
                Integer colLastName = findColOrNull(header, "lastname", "apellido");
                Integer colId = findColOrNull(header, "identification", "identificacion", "cedula", "cédula");

                Integer colFirstName = findColOrNull(header, "firstname", "primernombre", "nombre");
                Integer colBirthDay = findColOrNull(header, "birthday", "dianacimiento", "dia");
                Integer colBirthMonth = findColOrNull(header, "birthmonth", "mesnacimiento", "mes");
                Integer colBirthYear = findColOrNull(header, "birthyear", "anionacimiento", "año", "anio");

                Integer colMonthlyIncome = findColOrNull(header, "monthlyincome", "ingresosmensuales", "ingresos");

                // --- valores (si no hay col -> defaults) ---
                String lastName = readCell(row, colLastName, defaults.getLastName());
                String identification = readCell(row, colId, defaults.getIdentification());

                String firstName = readCell(row, colFirstName, defaults.getFirstName());
                String birthDay = readCell(row, colBirthDay, defaults.getBirthDay());
                String birthMonth = readCell(row, colBirthMonth, defaults.getBirthMonth());
                String birthYear = readCell(row, colBirthYear, defaults.getBirthYear());

                String monthlyIncome = readCell(row, colMonthlyIncome, defaults.getMonthlyIncome());

                // Validación mínima
                if (isBlank(lastName) || isBlank(identification)) return defaults;

                return new TestData(
                        lastName,
                        identification,
                        firstName,
                        birthDay,
                        birthMonth,
                        birthYear,
                        monthlyIncome
                );
            }
        } catch (Exception e) {
            // cualquier error -> defaults
            return defaults;
        }
    }

    // ---------------- helpers ----------------

    private Integer findColOrNull(Row header, String... names) {
        for (Cell c : header) {
            String v = formatter.formatCellValue(c);
            if (v == null) continue;

            String normalized = normalize(v);
            for (String name : names) {
                if (normalized.equals(normalize(name))) {
                    return c.getColumnIndex();
                }
            }
        }
        return null;
    }

    private String readCell(Row row, Integer colIndex, String fallback) {
        if (colIndex == null) return fallback;

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return fallback;

        String value = formatter.formatCellValue(cell);
        value = value == null ? "" : value.trim();

        return value.isBlank() ? fallback : value;
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replace(" ", "");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
