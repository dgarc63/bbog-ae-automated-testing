package com.bdb.ceropay.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenBrowserTest {
    private WebDriver driver;
    @Test
    void shouldOpenChromeAndLoadPage(){
        driver = new ChromeDriver();
        driver.get("https://google.com");


        assertTrue(driver.getTitle() !=null && !driver.getTitle().isBlank(),"el titulo no deberia estar vacio" );

    }
    @AfterEach
    void tearDown(){
        if (driver != null ){
            driver.quit();;
        }
    }
}
