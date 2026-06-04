package com.testing;

import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base para las pruebas End-to-End con Selenium.
 *
 * <p>Responsabilidades:
 * <ul>
 *   <li>Arranca Chrome en modo <b>headless</b> (no abre ventanas) y lo cierra
 *       siempre al terminar cada prueba ({@link #tearDownDriver()}), evitando
 *       navegadores/perfiles colgados.</li>
 *   <li>Si el frontend no responde en {@link #BASE_URL} la prueba se <b>omite</b>
 *       (no falla): así {@code mvn test} sigue verde en entornos sin frontend
 *       (CI) y no afecta el resto del backend.</li>
 *   <li>Provee el helper {@link #login()} y esperas explícitas reutilizables.</li>
 * </ul>
 *
 * <p>La URL del frontend se puede sobrescribir con
 * {@code -De2e.baseUrl=http://localhost:4201}. Por defecto es
 * {@code http://localhost:4200}.
 */
public abstract class BaseE2ETest {

    /** URL del frontend a probar (configurable con -De2e.baseUrl). */
    protected static final String BASE_URL =
            System.getProperty("e2e.baseUrl", "http://localhost:4200");

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpDriver() {
        // Si el frontend no esta levantado, omitimos la prueba en lugar de fallar.
        Assumptions.assumeTrue(
                frontendDisponible(),
                "Frontend no disponible en " + BASE_URL
                        + " — se omiten las pruebas E2E (levanta el frontend para ejecutarlas).");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");        // sin ventana visible
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            driver.quit();   // cierra el navegador y libera el perfil temporal
            driver = null;
        }
    }

    /** Inicia sesion como superadmin y espera la llegada al dashboard. */
    protected void login() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email']"))).sendKeys("admin@lulo.app");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("Admin123!");
        // El primer <button> de la pagina es el de mostrar/ocultar contrasena;
        // el de enviar es el de tipo submit.
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/app/dashboard"));
    }

    /** Navega a una ruta del SPA (recarga completa, la sesion vive en localStorage). */
    protected void irA(String ruta) {
        driver.get(BASE_URL + ruta);
    }

    /** Comprueba con una peticion rapida si el frontend responde. */
    private static boolean frontendDisponible() {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) URI.create(BASE_URL).toURL().openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.setRequestMethod("GET");
            int code = con.getResponseCode();
            return code >= 200 && code < 500;
        } catch (Exception e) {
            return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
