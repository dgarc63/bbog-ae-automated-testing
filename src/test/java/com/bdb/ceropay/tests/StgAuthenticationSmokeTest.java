package com.bdb.ceropay.tests;

import com.bdb.ceropay.pages.AuthenticationPage;
import com.bdb.ceropay.pages.CheckoutPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.bdb.ceropay.data.TestData;
import com.bdb.ceropay.data.TestDataFactory;

import java.time.Duration;

public class StgAuthenticationSmokeTest {

    private WebDriver driver;

    @Test
    void shouldNavigateFromAuthenticationToBasicData() {
        driver = new ChromeDriver();
        driver.get("https://bnpl.labdigbdbstgae.com/origination");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));


    TestData data = TestData.defaults();

new AuthenticationPage(driver, wait)
    .typeLastName(data.getLastName())
    .typeIdentification(data.getIdentification())
    .acceptTerms()
    .clickContinue()
    .assertPageLoaded()
    .clickStartRequest()
    .assertPageLoaded()
    .acceptTerms()
    .clickContinue()                 // <- ahora devuelve PersonalInformationPage
    .assertPageLoaded()
    .fillFirstName(data.getFirstName())
    .fillBirthDate(
    data.getBirthDay(),
    data.getBirthMonth(),
    data.getBirthYear())
    .selectAsalariado()
    .selectAsalariado()
    .fillMonthlyIncome(data.getMonthlyIncome())
    .clickContinue();



    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(15000); // temporal para ver la pantalla
        if (driver != null) {
            driver.quit();
        }
    }
}
