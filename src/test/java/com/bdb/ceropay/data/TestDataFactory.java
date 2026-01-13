package com.bdb.ceropay.data;

public class TestDataFactory {

    private TestDataFactory() {}

    public static TestDataLoader create() {
        // Si no pasas nada, usa DEFAULTS
        // Para activar Excel: -DdataSource=EXCEL
        String ds = System.getProperty("dataSource", "DEFAULTS").toUpperCase();

        DataSourceType type;
        try {
            type = DataSourceType.valueOf(ds);
        } catch (Exception e) {
            type = DataSourceType.DEFAULTS;
        }

        return switch (type) {
            case EXCEL -> new ExcelTestDataLoader("testdata/authData.xlsx", "auth", 1);
            case DEFAULTS -> TestData::defaults;
        };
    }
}
