package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Verifica que la pagina de roles/permisos carga con su contenido. */
class PermissionsTest extends BaseE2ETest {

    @Test
    void permissionsPageLoads() {
        login();
        irA("/app/permissions");
        wait.until(ExpectedConditions.urlContains("/permissions"));

        // Espera a que carguen los roles (lista de roles o tarjetas KPI).
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".role-item")));

        assertTrue(
                driver.getCurrentUrl().contains("/permissions"),
                "Deberia estar en la ruta de permisos");
        assertTrue(
                driver.getPageSource().contains("Available permissions"),
                "Deberia mostrar las tarjetas de resumen de permisos");
        assertTrue(
                !driver.findElements(By.cssSelector(".role-item")).isEmpty(),
                "Deberia listar al menos un rol");
    }
}
