package com.bdb.ceropay.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AuthenticationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By lastNameHost = By.cssSelector("[data-testid='input-lastname']");
    private final By idHost = By.cssSelector("[data-testid='input-identificationNumber']");
    private final By checkButtonHost = By.cssSelector("sp-at-check-button");
    private final By continuarButton = By.xpath("//button[.//text()[contains(.,'Continuar')]]");

    public AuthenticationPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public AuthenticationPage typeLastName(String lastName) {
        WebElement host = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameHost));
        WebElement input = host.getShadowRoot().findElement(By.cssSelector("input"));
        input.clear();
        input.sendKeys(lastName);
        return this;
    }

    public AuthenticationPage typeIdentification(String id) {
        WebElement host = wait.until(ExpectedConditions.visibilityOfElementLocated(idHost));
        WebElement input = host.getShadowRoot().findElement(By.cssSelector("input"));
        input.clear();
        input.sendKeys(id);
        return this;
    }

    public AuthenticationPage acceptTerms() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement host = wait.until(d -> d.findElement(checkButtonHost));
        WebElement checkbox = (WebElement) js.executeScript(
                "return arguments[0].shadowRoot.querySelector(\"div[role='checkbox']\");", host
        );
        js.executeScript("arguments[0].click();", checkbox);

        return this;
    }

    public StartRequestPage clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continuarButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
        btn.click();
        return new StartRequestPage(driver, wait);
    }
}

