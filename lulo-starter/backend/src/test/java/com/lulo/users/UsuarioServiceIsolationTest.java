package com.lulo.users;

import com.lulo.common.exception.ApiException;
import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.security.AuthenticatedUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Verifica el aislamiento multi-empresa en UsuarioService.crearDirecto:
 * un ADMIN_EMPRESA NO puede crear usuarios en otra empresa.
 * Es la prueba pedida por la entrega final: "garantizando el aislamiento
 * de la información entre organizaciones".
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceIsolationTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EmpresaRepository empresaRepository;
    @Mock private RolPoolRepository rolPoolRepository;
    @Mock private UsuarioRolPoolRepository usuarioRolPoolRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UsuarioService usuarioService;

    private final UUID empresaA = UUID.randomUUID();
    private final UUID empresaB = UUID.randomUUID();

    private void loginAdminEmpresa(UUID empresaId) {
        AuthenticatedUser u = new AuthenticatedUser(
                UUID.randomUUID(), "admin@empresa.com", empresaId,
                "ADMIN_EMPRESA", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, "tk", List.of()));
    }

    private void loginSuperadmin() {
        AuthenticatedUser u = new AuthenticatedUser(
                UUID.randomUUID(), "admin@lulo.app", null,
                "SUPERADMIN", List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, "tk", List.of()));
    }

    @AfterEach
    void clean() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void noOp() { /* hooks */ }

    @Test
    void adminEmpresaNoPuedeCrearUsuarioEnOtraEmpresa() {
        loginAdminEmpresa(empresaA);

        CrearUsuarioDirectoRequest req = new CrearUsuarioDirectoRequest();
        req.setEmpresaId(empresaB); // ← intenta crear en empresa ajena
        req.setEmail("x@b.com");
        req.setPassword("Pass123!");
        req.setRolPoolId(UUID.randomUUID());

        ApiException ex = assertThrows(ApiException.class,
                () -> usuarioService.crearDirecto(req));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void superadminPuedeCrearEnCualquierEmpresa() {
        loginSuperadmin();

        Empresa emp = new Empresa();
        emp.setNombre("Empresa B");
        emp.setDominio("b.com"); // el email x@b.com debe coincidir con el dominio de la empresa
        when(empresaRepository.findById(empresaB)).thenReturn(java.util.Optional.of(emp));
        // Resto de mocks no se ejercitan: validamos solo que pasa la barrera de empresa.
        when(rolPoolRepository.findById(any())).thenReturn(java.util.Optional.empty());

        CrearUsuarioDirectoRequest req = new CrearUsuarioDirectoRequest();
        req.setEmpresaId(empresaB);
        req.setEmail("x@b.com");
        req.setPassword("Pass123!");
        req.setRolPoolId(UUID.randomUUID());

        ApiException ex = assertThrows(ApiException.class,
                () -> usuarioService.crearDirecto(req));
        // Falla por "Rol no encontrado", NO por aislamiento — eso confirma
        // que superadmin atravesó la validación de empresa.
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getMessage().toLowerCase().contains("rol"));
    }
}
