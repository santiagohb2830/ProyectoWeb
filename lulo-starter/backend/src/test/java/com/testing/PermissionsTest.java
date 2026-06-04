package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class PermissionsTest {

    @Test
    void permissionsPageLoads() throws Exception {

        WebDriver driver = new ChromeDriver();

        driver.get("http://localhost:4200/login");

        Thread.sleep(2000);

        driver.findElement(By.cssSelector("input[type='email']"))
                .sendKeys("admin@lulo.app");

        driver.findElement(By.cssSelector("input[type='password']"))
                .sendKeys("Admin123!");

        driver.findElement(By.tagName("button"))
                .click();

        Thread.sleep(3000);

        driver.get("http://localhost:4200/app/permissions");

        Thread.sleep(3000);

        assertTrue(
                driver.getCurrentUrl()
                        .contains("/permissions")
        );

        assertTrue(
                driver.getPageSource()
                        .contains("Auditor")
        );

        assertTrue(
                driver.getPageSource()
                        .contains("Module Access Matrix")
        );

        driver.quit();
    }
}