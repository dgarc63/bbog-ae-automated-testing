package com.bdb.ceropay.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AuthenticatorMockPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Campo cédula
    private final By documentNumberInput = By.cssSelector("input#documentNumber");

    // Tipo de cliente: Cliente actual (según tu HTML: id="customerType0")
    private final By customerTypeActualRadio = By.cssSelector("input#customerType0");

    // Canal: Web (según tu HTML: id="channelValue0")
    private final By channelWebRadio = By.cssSelector("input#channelValue0");

    // Tipo de autenticación: Tarjeta débito (según tu HTML: id="clientLoginType0")
    private final By debitCardRadio = By.cssSelector("input#clientLoginType0");

    // Botón Siguiente (en tu HTML es <button class="btn btn-primary">Siguiente ></button>)
    private final By nextBtn = By.xpath("//button[contains(normalize-space(.),'Siguiente')]");

    public AuthenticatorMockPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public AuthenticatorMockPage assertPageLoaded() {
        wait.until(ExpectedConditions.presenceOfElementLocated(documentNumberInput));
        return this;
    }

    public AuthenticatorMockPage typeDocumentNumber(String documentNumber) {
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(documentNumberInput));
        scrollCenter(input);
        clickSafe(input);
        clearAndType(input, documentNumber);
        input.sendKeys(Keys.TAB);
        return this;
    }

    public AuthenticatorMockPage selectClienteActual() {
        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(customerTypeActualRadio));
        scrollCenter(radio);
        clickSafe(radio);
        wait.until(d -> d.findElement(customerTypeActualRadio).isSelected());
        return this;
    }

    public AuthenticatorMockPage selectCanalWeb() {
        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(channelWebRadio));
        scrollCenter(radio);
        clickSafe(radio);
        wait.until(d -> d.findElement(channelWebRadio).isSelected());
        return this;
    }

    public AuthenticatorMockPage selectTarjetaDebito() {
        WebElement radio = wait.until(ExpectedConditions.presenceOfElementLocated(debitCardRadio));
        scrollCenter(radio);
        clickSafe(radio);
        wait.until(d -> d.findElement(debitCardRadio).isSelected());
        return this;
    }

    public AuthenticatorMockPage clickSiguiente() {
        // 1) Espera a que exista
        wait.until(ExpectedConditions.presenceOfElementLocated(nextBtn));

        // 2) Espera a que esté habilitado sin capturar una variable (evita el error de Java)
        wait.until(d -> {
            try {
                WebElement b = d.findElement(nextBtn);
                return b.isDisplayed() && b.isEnabled() && b.getAttribute("disabled") == null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return false;
            }
        });

        // 3) Ya estable: buscarlo de nuevo y hacer click seguro
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(nextBtn));
        scrollCenter(btn);
        clickSafe(btn);

        return this;
    }

    // ---------- helpers ----------

    private void clickSafe(WebElement el) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(el)).click();
        } catch (ElementClickInterceptedException | TimeoutException | StaleElementReferenceException e) {
            // si se vuelve stale o lo interceptan, reintenta con JS click
            jsClick(el);
        }
    }

    private void clearAndType(WebElement input, String value) {
        // Nota: aquí NO hago input.click() para evitar intercept del loader;
        // el click se hace afuera con clickSafe() antes de llamar esto.
        input.sendKeys(Keys.chord(Keys.COMMAND, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        if (value != null && !value.isBlank()) {
            input.sendKeys(value);
        }
    }

    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                el
        );
    }
}
