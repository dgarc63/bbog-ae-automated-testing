package com.bdb.ceropay.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasicDataPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    
    private final By uniqueMarker = By.cssSelector("body"); // placeholder temporal

    public BasicDataPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public BasicDataPage assertPageLoaded() {
        // 1) Asegura que saliste de /checkout base si aplica
        // (ajusta a lo que veas realmente)
        wait.until(d -> d.getCurrentUrl().contains("/checkout"));

        // 2) Espera algo único de la pantalla
        wait.until(ExpectedConditions.visibilityOfElementLocated(uniqueMarker));

        return this;
    }
}
