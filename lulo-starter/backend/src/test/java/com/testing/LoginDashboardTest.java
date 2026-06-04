package com.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginDashboardTest {

    @Test
    public void loginExitoso() throws Exception {

        WebDriver driver = new ChromeDriver();

        driver.get("http://localhost:4200/login");

        Thread.sleep(2000);

        WebElement email = driver.findElement(By.cssSelector("input[type='email']"));
        email.clear();
        email.sendKeys("admin@lulo.app");

        WebElement password = driver.findElement(By.cssSelector("input[type='password']"));
        password.clear();
        password.sendKeys("Admin123!");

        driver.findElement(By.tagName("button")).click();

        Thread.sleep(3000);

        Assertions.assertTrue(
                driver.getPageSource().contains("Operational Command")
        );

        driver.quit();
    }
}