package com.bdb.ceropay.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ApprovalPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Ajusta este selector cuando veamos el HTML real de la pantalla de aprobación
    private final By marker = By.xpath("//*[contains(normalize-space(.),'Aprobación') or contains(normalize-space(.),'aprobación')]");

    public ApprovalPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public ApprovalPage assertPageLoaded() {
        // Por ahora: marcador genérico para que no quede “vacío”
        wait.until(ExpectedConditions.presenceOfElementLocated(marker));
        return this;
    }
}
