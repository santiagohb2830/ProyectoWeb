package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Verifica que la pagina de gestion de usuarios carga y lista usuarios. */
class UserManagementTest extends BaseE2ETest {

    @Test
    void userManagementLoads() {
        login();
        irA("/app/users");

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("h1")));

        assertTrue(
                title.getText().contains("Users"),
                "El titulo de la pagina deberia ser 'Users'");
        assertTrue(
                driver.getPageSource().contains("admin"),
                "Deberia listar al menos al usuario administrador");
    }
}
