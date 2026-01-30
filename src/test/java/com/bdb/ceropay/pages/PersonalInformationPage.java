package com.bdb.ceropay.pages;

import com.bdb.ceropay.data.TestData;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PersonalInformationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By loaderOpen = By.cssSelector("sp-ml-loader[is-open='true']");

    // Primer nombre
    private static final String FIRST_NAME_HOST_CSS = "sp-at-input[id-el='first-name-input']";

    // Fecha nacimiento
    private static final String BDAY_DAY_HOST_CSS   = "sp-at-input[id-el='day']";
    private static final String BDAY_MONTH_HOST_CSS = "sp-at-input[id-el='month']";
    private static final String BDAY_YEAR_HOST_CSS  = "sp-at-input[id-el='anio']";

    // Ocupación - Asalariado
    private static final String OCC_ASALARIADO_HOST_CSS = "sp-at-radio-button#radio__0";

    // Ingresos mensuales
    private static final String INCOME_HOST_CSS = "sp-at-input[id-el='income-input']";

    // Botón continuar
    private final By continueBtn = By.cssSelector(".basic-data__button button");

    public PersonalInformationPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public PersonalInformationPage fillPersonalInfo(TestData data) {
        return this
                .fillFirstName(data.getFirstName())
                .fillBirthDate(data.getBirthDay(), data.getBirthMonth(), data.getBirthYear())
                .selectAsalariado()
                .fillMonthlyIncome(data.getMonthlyIncome());
    }

    public PersonalInformationPage assertPageLoaded() {
        waitForNoLoader();

        wait.until(d -> d.getCurrentUrl().contains("basic-data")
                || d.getPageSource().contains("Completa tus datos"));

        wait.until(d -> deepFindElement(FIRST_NAME_HOST_CSS) != null);

        return this;
    }

    public PersonalInformationPage fillFirstName(String firstName) {
        waitForNoLoader();

        WebElement host = wait.until(d -> deepFindElement(FIRST_NAME_HOST_CSS));
        if (host == null) throw new NoSuchElementException("No se encontró el host: " + FIRST_NAME_HOST_CSS);

        scrollCenter(host);

        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].shadowRoot.querySelector('input#first-name-input');",
                host
        );
        if (input == null) throw new NoSuchElementException("No se encontró input#first-name-input dentro del shadowRoot.");

        // CLAVE: NO click (loader lo intercepta). Set por JS + eventos
        setValueJs(input, firstName);

        return this;
    }

    public PersonalInformationPage fillBirthDate(String day, String month, String year) {
        waitForNoLoader();

        WebElement dayHost   = wait.until(d -> deepFindElement(BDAY_DAY_HOST_CSS));
        WebElement monthHost = wait.until(d -> deepFindElement(BDAY_MONTH_HOST_CSS));
        WebElement yearHost  = wait.until(d -> deepFindElement(BDAY_YEAR_HOST_CSS));

        if (dayHost == null) throw new NoSuchElementException("No se encontró el host Día: " + BDAY_DAY_HOST_CSS);
        if (monthHost == null) throw new NoSuchElementException("No se encontró el host Mes: " + BDAY_MONTH_HOST_CSS);
        if (yearHost == null) throw new NoSuchElementException("No se encontró el host Año: " + BDAY_YEAR_HOST_CSS);

        WebElement dayInput   = shadowInput(dayHost, "day");
        WebElement monthInput = shadowInput(monthHost, "month");
        WebElement yearInput  = shadowInput(yearHost, "anio");

        scrollCenter(dayHost);

        setValueJs(dayInput, day);
        setValueJs(monthInput, month);
        setValueJs(yearInput, year);

        return this;
    }

    public PersonalInformationPage selectAsalariado() {
        waitForNoLoader();

        WebElement host = wait.until(d -> deepFindElement(OCC_ASALARIADO_HOST_CSS));
        if (host == null) throw new NoSuchElementException("No se encontró el host de Asalariado: " + OCC_ASALARIADO_HOST_CSS);

        scrollCenter(host);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement radio = (WebElement) js.executeScript(
                "const host = arguments[0];" +
                        "const root = host.shadowRoot;" +
                        "if (!root) return null;" +
                        "return root.querySelector(\"div[role='radio']\") || " +
                        "       root.querySelector(\"#sp-at-radio-button__0\") || " +
                        "       root.querySelector(\"#check_0\");",
                host
        );

        if (radio == null) throw new NoSuchElementException("No se encontró el radio clickeable dentro del shadowRoot de Asalariado.");

        clickSafe(radio);

        // espera selección
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
        if (host == null) throw new NoSuchElementException("No se encontró el host de ingresos: " + INCOME_HOST_CSS);

        scrollCenter(host);

        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].shadowRoot.querySelector('input#income-input');",
                host
        );
        if (input == null) throw new NoSuchElementException("No se encontró input#income-input dentro del shadowRoot.");

        setValueJs(input, income);

        return this;
    }

    // CLAVE: debe devolver AuthenticatorMockPage
    public AuthenticatorMockPage clickContinue() {
        waitForNoLoader();

        // 1) espera que exista
        wait.until(ExpectedConditions.presenceOfElementLocated(continueBtn));

        // 2) espera que esté habilitado (y sin disabled)
        wait.until(d -> {
            try {
                WebElement b = d.findElement(continueBtn);
                return b.isDisplayed() && b.isEnabled() && b.getAttribute("disabled") == null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return false;
            }
        });

        // 3) reobtener y click seguro
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(continueBtn));
        scrollCenter(btn);
        clickSafe(btn);

        return new AuthenticatorMockPage(driver, wait);
    }

    // ---------------- helpers ----------------

    private WebElement shadowInput(WebElement host, String inputId) {
        WebElement input = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].shadowRoot.querySelector('input#" + inputId + "');",
                host
        );
        if (input == null) throw new NoSuchElementException("No se encontró input#" + inputId + " dentro del shadowRoot.");
        return input;
    }

    /**
     * CLAVE: set value por JS + eventos para evitar click interceptado por loader.
     */
    private void setValueJs(WebElement input, String value) {
        waitForNoLoader();

        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];" +
                        "const val = arguments[1] ?? '';" +
                        "el.focus();" +
                        "el.value = '';" +
                        "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "el.value = val;" +
                        "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));" +
                        "el.blur();",
                input, value
        );
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

    private void clickSafe(WebElement el) {
        waitForNoLoader();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(el)).click();
        } catch (ElementClickInterceptedException | TimeoutException | StaleElementReferenceException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    /**
     * Espera a que NO haya loader visible/abierto.
     * (Si el loader se vuelve stale, lo tolera.)
     */
    private void waitForNoLoader() {
        wait.until(d -> {
            try {
                var loaders = d.findElements(loaderOpen);
                if (loaders.isEmpty()) return true;

                for (WebElement l : loaders) {
                    try {
                        String isOpen = l.getAttribute("is-open");
                        if ("true".equalsIgnoreCase(isOpen) && l.isDisplayed()) return false;
                    } catch (StaleElementReferenceException ignored) { }
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
