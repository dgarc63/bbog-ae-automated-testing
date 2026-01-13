package com.bdb.ceropay.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Selector estable del checkbox (sin depender de --uncheck/--check)
    private static final String TERMS_SQUARE_STABLE = "div.sp-at-checkbox__content__square";

    // Botón continuar por texto (más estable que clases)
    private final By continuarBtn = By.xpath("//button[contains(.,'Continuar')]");

    // Loader/overlay que intercepta clicks
    private final By loaderOpen = By.cssSelector("sp-ml-loader[is-open='true']");

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public CheckoutPage assertPageLoaded() {
        wait.until(d -> d.getCurrentUrl().contains("/checkout"));
        // Señal mínima de que el contenido cargó: ya existe el checkbox (aunque esté dentro de shadow)
        // Usamos deepQuery para confirmarlo
        WebElement square = findElementSmart(TERMS_SQUARE_STABLE);
        if (square == null) throw new NoSuchElementException("No cargó el checkbox de términos en Checkout.");
        return this;
    }

    public CheckoutPage acceptTerms() {
        waitForNoLoader();
        WebElement checkboxSquare = findElementSmart(TERMS_SQUARE_STABLE);
        clickSmart(checkboxSquare);
        return this;
    }

    public BasicDataPage clickContinue() {
        waitForNoLoader();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continuarBtn));
        clickSmart(btn);
        return new BasicDataPage(driver, wait);
    }

    /**
     * Busca un elemento por CSS:
     * - Primero en DOM normal
     * - Si no aparece, busca dentro de shadowRoots (deep query)
     */
    private WebElement findElementSmart(String cssSelector) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
        } catch (TimeoutException ignored) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement el = (WebElement) js.executeScript(shadowDeepQueryScript(), cssSelector);
            if (el == null) {
                throw new NoSuchElementException("No se encontró (DOM ni Shadow DOM): " + cssSelector);
            }
            return el;
        }
    }

    private void clickSmart(WebElement element) {
        waitForNoLoader();

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element
        );

        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            waitForNoLoader();
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (StaleElementReferenceException e) {
            // Si se re-renderizó el DOM, reintenta por JS click directo (último recurso)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void waitForNoLoader() {
        // Si no existe loader, pasa de una.
        wait.until(d -> d.findElements(loaderOpen).isEmpty());
    }

    /**
     * Script para buscar un selector dentro del DOM y dentro de shadowRoots recursivamente.
     */
    private String shadowDeepQueryScript() {
        return """
            const selector = arguments[0];
            function deepQuery(root) {
              if (!root) return null;

              const found = root.querySelector ? root.querySelector(selector) : null;
              if (found) return found;

              const base = (root instanceof Document) ? root.documentElement : root;
              const treeWalker = document.createTreeWalker(base, NodeFilter.SHOW_ELEMENT);

              while (treeWalker.nextNode()) {
                const node = treeWalker.currentNode;
                if (node && node.shadowRoot) {
                  const inside = deepQuery(node.shadowRoot);
                  if (inside) return inside;
                }
              }
              return null;
            }
            return deepQuery(document);
        """;
    }
}
