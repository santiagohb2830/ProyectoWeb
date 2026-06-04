package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Al seleccionar un rol, se muestra el panel de detalle con sus permisos. */
class RoleSelectionTest extends BaseE2ETest {

    @Test
    void roleSelectionChangesContent() {
        login();
        irA("/app/permissions");

        // Espera la lista de roles y selecciona el primero.
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".role-item"))).click();

        // Al seleccionar un rol aparece el panel de detalle "Role permissions".
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".matriz-head"), "Role permissions"));

        assertTrue(
                driver.getPageSource().contains("Role permissions"),
                "El detalle del rol seleccionado deberia mostrar sus permisos");
    }
}
