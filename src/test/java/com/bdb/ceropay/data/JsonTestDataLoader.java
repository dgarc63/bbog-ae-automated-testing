package com.bdb.ceropay.data;

public class JsonTestDataLoader implements TestDataLoader {
    @Override
    public TestData load() {
        // Deshabilitado por seguridad
        return TestData.defaults();
    }
}

