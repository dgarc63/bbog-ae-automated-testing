package com.bdb.ceropay.pages;

import org.openqa.selenium.*;
import com.bdb.ceropay.data.TestData;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PersonalInformationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By loaderOpen = By.cssSelector("sp-ml-loader[is-open='true']");

    // Primer nombre
    private static final String FIRST_NAME_HOST_CSS = "sp-at-input[id-el='first-name-input']";

    // Fecha nacimiento: ojo con los id-el reales que viste en el HTML
    private static final String BDAY_DAY_HOST_CSS   = "sp-at-input[id-el='day']";
    private static final String BDAY_MONTH_HOST_CSS = "sp-at-input[id-el='month']";
    private static final String BDAY_YEAR_HOST_CSS  = "sp-at-input[id-el='anio']";
    // Ocupación - Asalariado (primer card)
private static final String OCC_ASALARIADO_HOST_CSS = "sp-at-radio-button#radio__0";

// Ingresos mensuales
private static final String INCOME_HOST_CSS =
        "sp-at-input[id-el='income-input']";



    public PersonalInformationPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }
public PersonalInformationPage fillPersonalInfo(TestData data) {
    return this
            .fillFirstName(data.getFirstName())
            .fillBirthDate(data.getBirthDay(), data.getBirthMonth(), data.getBirthYear());
}

    public PersonalInformationPage assertPageLoaded() {
        waitForNoLoader();

        // Confirma que llegaste a la vista (evita buscar en página equivocada)
        wait.until(d -> d.getCurrentUrl().contains("basic-data")
                || d.getPageSource().contains("Completa tus datos"));

        // Elemento clave
        wait.until(d -> deepFindElement(FIRST_NAME_HOST_CSS) != null);

        return this;
    }

    public PersonalInformationPage fillFirstName(String firstName) {
        waitForNoLoader();

        WebElement host = wait.until(d -> deepFindElement(FIRST_NAME_HOST_CSS));
        if (host == null) {
            throw new NoSuchElementException("No se encontró el host: " + FIRST_NAME_HOST_CSS);
        }

        scrollCenter(host);

        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].shadowRoot.querySelector('input#first-name-input');",
                host
        );

        if (input == null) {
            throw new NoSuchElementException("No se encontró input#first-name-input dentro del shadowRoot.");
        }

        clearAndType(input, firstName);
        input.sendKeys(Keys.TAB);

        return this;
    }

    /**
     * Fecha de nacimiento (Día/Mes/Año) según tu HTML:
     * - Día:  sp-at-input[id-el='day']   -> input#day
     * - Mes:  sp-at-input[id-el='month'] -> input#month
     * - Año:  sp-at-input[id-el='anio']  -> input#anio
     */
    public PersonalInformationPage fillBirthDate(String day, String month, String year) {
        waitForNoLoader();

        WebElement dayHost = wait.until(d -> deepFindElement(BDAY_DAY_HOST_CSS));
        WebElement monthHost = wait.until(d -> deepFindElement(BDAY_MONTH_HOST_CSS));
        WebElement yearHost = wait.until(d -> deepFindElement(BDAY_YEAR_HOST_CSS));

        if (dayHost == null) throw new NoSuchElementException("No se encontró el host Día: " + BDAY_DAY_HOST_CSS);
        if (monthHost == null) throw new NoSuchElementException("No se encontró el host Mes: " + BDAY_MONTH_HOST_CSS);
        if (yearHost == null) throw new NoSuchElementException("No se encontró el host Año: " + BDAY_YEAR_HOST_CSS);

        WebElement dayInput = shadowInput(dayHost, "day");
        WebElement monthInput = shadowInput(monthHost, "month");
        WebElement yearInput = shadowInput(yearHost, "anio");

        scrollCenter(dayHost);

        clearAndType(dayInput, day);
        clearAndType(monthInput, month);
        clearAndType(yearInput, year);

        // Dispara la validación del componente
        yearInput.sendKeys(Keys.TAB);

        return this;
    }

    // ---------------- helpers ----------------

    private WebElement shadowInput(WebElement host, String inputId) {
        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].shadowRoot.querySelector('input#" + inputId + "');",
                host
        );
        if (input == null) {
            throw new NoSuchElementException("No se encontró input#" + inputId + " dentro del shadowRoot.");
        }
        return input;
    }

    private void clearAndType(WebElement input, String value) {
        input.click();
        input.sendKeys(Keys.chord(Keys.COMMAND, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(value);
    }

    private WebElement deepFindElement(String cssSelector) {
        Object result = ((JavascriptExecutor) driver).executeScript(
                "const sel = arguments[0];" +
                "function findDeep(root) {" +
                "  if (!root) return null;" +
                "  const direct = root.querySelector(sel);" +
                "  if (direct) return direct;" +
                "  const all = root.querySelectorAll('*');" +
                "  for (const el of all) {" +
                "    if (el.shadowRoot) {" +
                "      const found = findDeep(el.shadowRoot);" +
                "      if (found) return found;" +
                "    }" +
                "  }" +
                "  return null;" +
                "}" +
                "return findDeep(document);",
                cssSelector
        );
        return (result instanceof WebElement) ? (WebElement) result : null;
    }
    public PersonalInformationPage selectAsalariado() {
    waitForNoLoader();

    WebElement host = wait.until(d -> deepFindElement(OCC_ASALARIADO_HOST_CSS));
    if (host == null) {
        throw new NoSuchElementException("No se encontró el host de Asalariado: " + OCC_ASALARIADO_HOST_CSS);
    }

    scrollCenter(host);

    JavascriptExecutor js = (JavascriptExecutor) driver;

    // Dentro del shadowRoot buscamos el elemento clickeable del radio
    WebElement radio = (WebElement) js.executeScript(
            "const host = arguments[0];" +
            "const root = host.shadowRoot;" +
            "if (!root) return null;" +
            // preferimos el role=radio
            "return root.querySelector(\"div[role='radio']\") || " +
            "       root.querySelector(\"#sp-at-radio-button__0\") || " +
            "       root.querySelector(\"#check_0\");",
            host
    );

    if (radio == null) {
        throw new NoSuchElementException("No se encontró el radio clickeable dentro del shadowRoot de Asalariado.");
    }

    // click robusto con loader
    try {
        waitForNoLoader();
        radio.click();
    } catch (ElementClickInterceptedException e) {
        waitForNoLoader();
        js.executeScript("arguments[0].click();", radio);
    }

    // Validación: esperamos a que quede seleccionado (aria-checked=true)
    wait.until(d -> {
        try {
            Object checked = ((JavascriptExecutor) d).executeScript(
                    "const host = arguments[0];" +
                    "const root = host.shadowRoot;" +
                    "if (!root) return false;" +
                    "const el = root.querySelector(\"div[role='radio']\");" +
                    "if (!el) return false;" +
                    "return el.getAttribute('aria-checked') === 'true';",
                    host
            );
            return Boolean.TRUE.equals(checked);
        } catch (Exception ex) {
            return false;
        }
    });

    return this;
}
    public PersonalInformationPage fillMonthlyIncome(String income) {
    waitForNoLoader();

    WebElement host = wait.until(d -> deepFindElement(INCOME_HOST_CSS));
    if (host == null) {
        throw new NoSuchElementException("No se encontró el host de ingresos: " + INCOME_HOST_CSS);
    }

    scrollCenter(host);

    WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
            "return arguments[0].shadowRoot.querySelector('input#income-input');",
            host
    );

    if (input == null) {
        throw new NoSuchElementException("No se encontró input#income-input dentro del shadowRoot.");
    }

    clearAndType(input, income);
    input.sendKeys(Keys.TAB); // dispara validación

    return this;


}
private final By continueBtn =
        By.cssSelector(".basic-data__button button");

public CheckoutPage clickContinue() {
    waitForNoLoader();

    WebElement btn = wait.until(d ->
            d.findElement(continueBtn)
    );

    wait.until(d -> btn.isEnabled());

    scrollCenter(btn);
    btn.click();

    return new CheckoutPage(driver, wait);
}


    private void waitForNoLoader() {
        wait.until(d -> {
            try {
                var loaders = d.findElements(loaderOpen);
                if (loaders.isEmpty()) return true;

                for (WebElement l : loaders) {
                    try {
                        if (l.isDisplayed()) return false;
                    } catch (StaleElementReferenceException ignored) {}
                }
                return true;
            } catch (Exception e) {
                return true;
            }
        });
    }

    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});",
                el
        );
    }
}
