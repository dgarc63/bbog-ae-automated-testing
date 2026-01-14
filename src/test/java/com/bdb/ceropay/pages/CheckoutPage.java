package com.bdb.ceropay.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Host del componente checkbox (Shadow DOM)
    private final By termsHost =
            By.cssSelector("sp-at-check-button#user-data-to-change-checkBtn");

    // Botón continuar (real)
    private final By continuarBtn = By.id("user-data-continue");

    // Loader
    private final By loaderOpen = By.cssSelector("sp-ml-loader[is-open='true']");

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public CheckoutPage assertPageLoaded() {
        wait.until(d -> d.getCurrentUrl().contains("/checkout"));
        wait.until(ExpectedConditions.presenceOfElementLocated(termsHost));
        wait.until(ExpectedConditions.presenceOfElementLocated(continuarBtn));
        return this;
    }

    public CheckoutPage acceptTerms() {
    waitForNoLoader();

    WebElement host = wait.until(ExpectedConditions.presenceOfElementLocated(termsHost));
    JavascriptExecutor js = (JavascriptExecutor) driver;

    // Espera a que el shadowRoot esté listo y exista el checkbox interno
    wait.until(d -> (Boolean) js.executeScript(
            "return arguments[0].shadowRoot && arguments[0].shadowRoot.querySelector(\"div[role='checkbox']\") != null;",
            host
    ));

    // ✅ Selector correcto: contiene 'sp-at-checkbox__content__square'
    WebElement square = (WebElement) js.executeScript(
            "return arguments[0].shadowRoot.querySelector(\"div[class*='sp-at-checkbox__content__square']\");",
            host
    );

    if (square == null) {
        throw new NoSuchElementException("No se encontró el square del checkbox (class*='sp-at-checkbox__content__square') en el shadowRoot");
    }

    scrollCenter(square);

    // Click confiable
    js.executeScript("arguments[0].click();", square);

    // Validar que quedó marcado
    WebElement roleCheckbox = (WebElement) js.executeScript(
            "return arguments[0].shadowRoot.querySelector(\"div[role='checkbox']\");",
            host
    );

    wait.until(d -> "true".equalsIgnoreCase(roleCheckbox.getAttribute("aria-checked")));

    // Espera habilitación del botón
    wait.until(d -> {
        WebElement btn = d.findElement(continuarBtn);
        return btn.getAttribute("disabled") == null && btn.isEnabled();
    });

    return this;
}


    public BasicDataPage clickContinue() {
        waitForNoLoader();

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continuarBtn));
        scrollCenter(btn);
        btn.click();

        return new BasicDataPage(driver, wait);
    }

    private void waitForNoLoader() {
    // Espera a que NO haya loader visible (aunque exista en el DOM)
    wait.until(d -> {
        try {
            var openLoaders = d.findElements(loaderOpen); // sp-ml-loader[is-open='true']
            if (openLoaders.isEmpty()) return true;

            for (WebElement l : openLoaders) {
                try {
                    if (l.isDisplayed()) {
                        return false; // sigue visible, sigue bloqueando
                    }
                } catch (StaleElementReferenceException ignored) {
                    // si se refresca el DOM, lo tomamos como "no bloquea"
                }
            }
            return true; // están pero no visibles
        } catch (Exception e) {
            return true; // defensivo: no bloquees el flujo por glitches del DOM
        }
    });
    }

    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el
        );
    }
}
