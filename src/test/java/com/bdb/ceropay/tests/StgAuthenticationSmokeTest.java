package com.bdb.ceropay.tests;

import com.bdb.ceropay.pages.AuthenticationPage;
import com.bdb.ceropay.pages.CheckoutPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class StgAuthenticationSmokeTest {

    private WebDriver driver;

    @Test
    void shouldNavigateFromAuthenticationToCheckout() {
        driver = new ChromeDriver();
        driver.get("https://bnpl.labdigbdbstgae.com/origination");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        new AuthenticationPage(driver, wait)
                .typeLastName("PRUEBA")
                .typeIdentification("123456789")
                .acceptTerms()
                .clickContinue()          // -> StartRequestPage
                .assertPageLoaded()
                .clickStartRequest()      // -> CheckoutPage
                .assertPageLoaded()
                .acceptTerms()
                .clickContinue()          // -> BasicDataPage (pantalla grande)
                .assertPageLoaded();


        // Si quieres, puedes dejar esto explícito (opcional):
        // checkoutPage.assertPageLoaded();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(15000); // temporal para ver la pantalla
        if (driver != null) {
            driver.quit();
        }
    }
}
