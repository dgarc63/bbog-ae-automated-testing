package com.bdb.ceropay.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StartRequestPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Botón "Iniciar solicitud"
    private final By approvalBtn = By.cssSelector("[data-testid='approvalBtn']");

    public StartRequestPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public StartRequestPage assertPageLoaded() {
        // Espera a que el botón exista y sea clickeable
        wait.until(ExpectedConditions.presenceOfElementLocated(approvalBtn));
        wait.until(ExpectedConditions.elementToBeClickable(approvalBtn));
        return this;
    }

    public CheckoutPage clickStartRequest() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(approvalBtn));

        // Asegura visibilidad y clic confiable
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();

        // Espera navegación a la pantalla 3 (URL cambia a /checkout?uuid=...)
        wait.until(d -> d.getCurrentUrl().contains("/checkout") && d.getCurrentUrl().contains("uuid="));

        return new CheckoutPage(driver, wait);
    }
}
