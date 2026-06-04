package com.testing;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginTest {

    @Test
    public void abrirAplicacion() throws Exception {

        WebDriver driver = new ChromeDriver();

        driver.get("http://localhost:4200");

        Thread.sleep(10000);

        driver.quit();
    }
}