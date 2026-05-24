package com.lulo.company;

import com.lulo.common.exception.ApiException;
import com.lulo.company.dto.EmpresaDetalleResponse;
import com.lulo.company.dto.EmpresaListItemResponse;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.security.AuthContext;
import com.lulo.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresa", description = "Registro y gestión de empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Listar empresas (solo SUPERADMIN)")
    public List<EmpresaListItemResponse> listar() {
        return empresaService.listar();
    }

    /**
     * Registro de empresa: lo crea SUPERADMIN.
     * El endpoint legacy /registro queda como alias del superadmin.
     */
    @PostMapping("/registro")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar empresa con admin inicial (solo SUPERADMIN)")
    public RegistroEmpresaResponse registrar(@Valid @RequestBody RegistroEmpresaRequest request) {
        return empresaService.registrar(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de empresa (SUPERADMIN o miembro de la empresa)")
    public EmpresaDetalleResponse obtener(@PathVariable UUID id) {
        AuthenticatedUser user = AuthContext.require();
        if (!user.isSuperadmin() && (user.empresaId() == null || !user.empresaId().equals(id))) {
            throw new ApiException("No autorizado para ver esta empresa", HttpStatus.FORBIDDEN);
        }
        return empresaService.obtener(id);
    }
}
