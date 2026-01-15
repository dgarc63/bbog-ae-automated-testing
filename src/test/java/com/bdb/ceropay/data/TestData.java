package com.bdb.ceropay.data;

public class TestData {

    // --- Autenticación ---
    private final String lastName;
    private final String identification;

    // --- Información personal ---
    private final String firstName;
    private final String birthDay;
    private final String birthMonth;
    private final String birthYear;
    private final String monthlyIncome;

    /**
     * Constructor corto (retrocompatible).
     */
    public TestData(String lastName, String identification) {
        TestData d = TestData.defaults();
        this.lastName = normalize(lastName);
        this.identification = normalize(identification);

        this.firstName = d.firstName;
        this.birthDay = d.birthDay;
        this.birthMonth = d.birthMonth;
        this.birthYear = d.birthYear;
        this.monthlyIncome = d.monthlyIncome;
    }

    /**
     * Constructor completo.
     */
    public TestData(
            String lastName,
            String identification,
            String firstName,
            String birthDay,
            String birthMonth,
            String birthYear,
            String monthlyIncome
    ) {
        this.lastName = normalize(lastName);
        this.identification = normalize(identification);
        this.firstName = normalize(firstName);

        this.birthDay = normalizeTwoDigits(birthDay);
        this.birthMonth = normalizeTwoDigits(birthMonth);
        this.birthYear = normalizeYear(birthYear);

        this.monthlyIncome = normalize(monthlyIncome);
    }

    // --- getters ---
    public String getLastName() { return lastName; }
    public String getIdentification() { return identification; }

    public String getFirstName() { return firstName; }
    public String getBirthDay() { return birthDay; }
    public String getBirthMonth() { return birthMonth; }
    public String getBirthYear() { return birthYear; }

    public String getMonthlyIncome() { return monthlyIncome; }

    // --- defaults seguros ---
    public static TestData defaults() {
        return new TestData(
                "PRUEBA",
                "123456789",
                "Paola",
                "01",
                "01",
                "1995",
                "3000000"
        );
    }

    // ================= helpers =================

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeTwoDigits(String value) {
        if (value == null) return "";
        value = value.trim();
        if (value.length() == 1) return "0" + value;
        return value;
    }

    private static String normalizeYear(String value) {
        return value == null ? "" : value.trim();
    }
}
