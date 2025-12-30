package com.bdb.ceropay.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StgAuthenticationSmokeTest {

    private WebDriver driver;

    @Test
    void shouldLoadAuthenticationScreen() {
        driver = new ChromeDriver();
        driver.get("https://bnpl.labdigbdbstgae.com/origination");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> d.getTitle() != null && !d.getTitle().isBlank());

        boolean pageLoaded = driver.findElement(By.tagName("body")).isDisplayed();
        assertTrue(pageLoaded, "La pantalla de autenticación no se visualizó correctamente en STG");

        // Apellido
        WebElement lastNameBox = driver.findElement(By.cssSelector("[data-testid='input-lastname']"));
        WebElement apellidoInput = lastNameBox.getShadowRoot().findElement(By.cssSelector("input"));
        apellidoInput.sendKeys("PRUEBA");

        // Cédula
        WebElement idBox = driver.findElement(By.cssSelector("[data-testid='input-identificationNumber']"));
        WebElement cedulaInput = idBox.getShadowRoot().findElement(By.cssSelector("input"));
        cedulaInput.sendKeys("123456789");


        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement checkHost = (WebElement) js.executeScript(
                "return document.querySelector('sp-at-check-button');"
        );

        WebElement checkbox = (WebElement) js.executeScript(
                "return arguments[0].shadowRoot.querySelector(\"div[role='checkbox']\");",
                checkHost
        );

        js.executeScript("arguments[0].click();", checkbox);
        WebElement continuarBtn = wait.until(d ->
                d.findElement(By.xpath("//button[.//text()[contains(.,'Continuar')]]"))
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                continuarBtn
        );

        continuarBtn.click();

    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(15000); // 15 segundos
    }
}
