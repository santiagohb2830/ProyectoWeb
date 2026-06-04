package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class UserManagementTest {

    @Test
    void userManagementLoads() throws InterruptedException {

        WebDriver driver = new ChromeDriver();

        driver.get("http://localhost:4200/login");

        driver.findElement(
                By.cssSelector("input[type='email']")
        ).sendKeys("admin@lulo.app");

        driver.findElement(
                By.cssSelector("input[type='password']")
        ).sendKeys("Admin123!");

        driver.findElement(By.tagName("button"))
                .click();

        Thread.sleep(3000);

        driver.get("http://localhost:4200/app/users");

        Thread.sleep(2000);

        WebElement title =
                driver.findElement(By.tagName("h1"));

        assertTrue(
                title.getText().contains("Access Control")
        );

        assertTrue(
                driver.getPageSource().contains("admin")
        );

        driver.quit();
    }
}