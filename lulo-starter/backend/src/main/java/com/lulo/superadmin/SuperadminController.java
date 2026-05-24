package com.lulo.superadmin;

import com.lulo.company.EmpresaService;
import com.lulo.company.dto.EditarEmpresaRequest;
import com.lulo.company.dto.EmpresaDetalleResponse;
import com.lulo.company.dto.EmpresaListItemResponse;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints exclusivos del SUPERADMIN o de usuarios con permisos
 * granulares EMPRESA_*. SecurityConfig protege /api/superadmin con ROLE_SUPERADMIN;
 * adicionalmente, los endpoints aceptan usuarios que tengan los PERM_ correspondientes.
 */
@RestController
@RequestMapping("/api/superadmin")
@Tag(name = "Superadmin", description = "Operaciones de dueño de la aplicación")
public class SuperadminController {

    private final EmpresaService empresaService;

    public SuperadminController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/empresas")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('PERM_EMPRESA_VER')")
    public List<EmpresaListItemResponse> listarEmpresas() {
        return empresaService.listar();
    }

    @PostMapping("/empresas")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('PERM_EMPRESA_CREAR')")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroEmpresaResponse crearEmpresa(@Valid @RequestBody RegistroEmpresaRequest request) {
        return empresaService.registrar(request);
    }

    @GetMapping("/empresas/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('PERM_EMPRESA_VER')")
    public EmpresaDetalleResponse detalleEmpresa(@PathVariable UUID id) {
        return empresaService.obtener(id);
    }

    @PatchMapping("/empresas/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('PERM_EMPRESA_EDITAR')")
    public EmpresaListItemResponse editarEmpresa(@PathVariable UUID id,
                                                 @Valid @RequestBody EditarEmpresaRequest request) {
        return empresaService.editar(id, request);
    }

    /**
     * Soft delete. Requiere body con "confirmNombre": nombre exacto de la empresa.
     */
    @DeleteMapping("/empresas/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('PERM_EMPRESA_ELIMINAR')")
    public ResponseEntity<?> eliminarEmpresa(@PathVariable UUID id,
                                             @RequestBody Map<String, String> body) {
        String confirm = body == null ? null : body.get("confirmNombre");
        empresaService.desactivar(id, confirm);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa desactivada"));
    }
}
