package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Verifica que la aplicacion carga y muestra el formulario de login. */
class LoginTest extends BaseE2ETest {

    @Test
    void abrirAplicacion() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email']")));
        assertTrue(
                driver.findElement(By.cssSelector("input[type='email']")).isDisplayed(),
                "El formulario de login deberia mostrarse");
    }
}
