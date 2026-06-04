package com.testing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Inicia sesion y verifica que se llega al dashboard. */
class LoginDashboardTest extends BaseE2ETest {

    @Test
    void loginExitoso() {
        login();
        assertTrue(
                driver.getCurrentUrl().contains("/app/dashboard"),
                "Tras el login deberia navegar al dashboard");
    }
}
