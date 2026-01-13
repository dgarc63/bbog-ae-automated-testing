package com.bdb.ceropay.data;

public class TestData {

    private final String lastName;
    private final String identification;

    public TestData(String lastName, String identification) {
        this.lastName = lastName;
        this.identification = identification;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdentification() {
        return identification;
    }

    public static TestData defaults() {
        return new TestData("PRUEBA", "123456789");
    }
}
