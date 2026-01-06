package com.bdb.ceropay.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Tus clases:
    private static final String TERMS_UNCHECK_CLASS = ".sp-at-checkbox__content__square--uncheck";
    private static final String CONTINUAR_BTN_CLASS = ".sp-at-btn.sp-at-btn--primary.sp-at-btn--lg";

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public CheckoutPage assertPageLoaded() {
        // Esto te asegura que ya estás en /checkout (porque dijiste que cambia a /checkout?uuid=...)
        wait.until(d -> d.getCurrentUrl().contains("/checkout"));
        return this;
    }

    public CheckoutPage acceptTerms() {
        WebElement checkboxSquare = findElementSmart(TERMS_UNCHECK_CLASS);
        clickSmart(checkboxSquare);
        return this;
    }

    public BasicDataPage clickContinue() {
        WebElement btn = findElementSmart(CONTINUAR_BTN_CLASS);

        // Ojo: esa clase puede estar en más de un botón. Por eso validamos que contenga el texto.
        // Si falla, me dices y lo hacemos por data-testid/id cuando lo tengas.
        if (!btn.getText().toLowerCase().contains("continuar")) {
            // busca el que diga continuar
            btn = wait.until(d -> {
                for (WebElement b : d.findElements(By.cssSelector(CONTINUAR_BTN_CLASS))) {
                    if (b.getText() != null && b.getText().toLowerCase().contains("continuar")) return b;
                }
                return null;
            });
        }

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
            // fallback shadow DOM
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement el = (WebElement) js.executeScript(shadowDeepQueryScript(), cssSelector);
            if (el == null) {
                throw new NoSuchElementException("No se encontró el elemento (DOM ni Shadow DOM) con selector: " + cssSelector);
            }
            return el;
        }
    }

    private void clickSmart(WebElement element) {
        wait.until(d -> element != null);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            // Fallback robusto si el click normal falla por overlays/Shadow/etc.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Script para buscar un selector dentro del DOM y dentro de shadowRoots recursivamente.
     */
    private String shadowDeepQueryScript() {
        return """
            const selector = arguments[0];
            function deepQuery(root) {
              if (!root) return null;
              // intenta query normal
              const found = root.querySelector ? root.querySelector(selector) : null;
              if (found) return found;

              // recorre todos los nodos para entrar a shadowRoots
              const treeWalker = document.createTreeWalker(
                root instanceof Document ? root.documentElement : root,
                NodeFilter.SHOW_ELEMENT
              );

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
