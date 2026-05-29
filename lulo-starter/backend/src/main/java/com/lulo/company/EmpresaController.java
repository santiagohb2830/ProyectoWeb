package com.lulo.company;

import com.lulo.common.exception.ApiException;
import com.lulo.company.dto.EmpresaDetalleResponse;
import com.lulo.company.dto.EmpresaListItemResponse;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.company.dto.EmpresaDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

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
    @Operation(summary = "Detalle de empresa")
    public EmpresaDetalleResponse obtener(@PathVariable UUID id) {
        AuthenticatedUser user = AuthContext.require();
        boolean esMiembro = user.empresaId() != null && user.empresaId().equals(id);
        boolean tienePermisoLulo = user.hasPermiso("EMPRESA_VER");
        if (!user.isSuperadmin() && !esMiembro && !tienePermisoLulo) {
            throw new ApiException("No autorizado para ver esta empresa", HttpStatus.FORBIDDEN);
        }
        return empresaService.obtener(id);
    @Operation(summary = "Obtener empresa por ID", description = "Retorna los detalles de la empresa especificada por ID")
    public EmpresaDetailResponse obtenerPorId(@PathVariable UUID id) {
        return empresaService.obtenerDetallePorId(id);
    }
}
