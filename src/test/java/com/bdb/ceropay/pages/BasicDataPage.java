package com.bdb.ceropay.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasicDataPage {

    private final WebDriver driver;
    private final WebDriverWait wait;


    private final By body = By.tagName("body");

    public BasicDataPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public BasicDataPage assertPageLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(body));
        return this;
    }
}
