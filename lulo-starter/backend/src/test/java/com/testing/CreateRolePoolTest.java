package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Abre el modal de creacion de rol desde la pagina de permisos. */
class CreateRolePoolTest extends BaseE2ETest {

    @Test
    void createRolePool() {
        login();
        irA("/app/permissions");

        // El boton de crear rol se llama "+ New role" en la UI actual.
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(.),'New role')]"))).click();

        // Al pulsarlo debe aparecer el modal de creacion.
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".modal-box")));
        assertTrue(
                driver.findElement(By.cssSelector(".modal-box")).isDisplayed(),
                "El modal de creacion de rol deberia abrirse");
    }
}
