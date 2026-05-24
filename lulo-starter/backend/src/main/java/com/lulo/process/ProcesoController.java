package com.lulo.process;

import java.util.UUID;

import com.lulo.process.dto.CrearProcesoRequest;
import com.lulo.process.dto.EditarProcesoRequest;
import com.lulo.process.dto.EliminarProcesoRequest;
import com.lulo.process.dto.ProcesoDetalleResponse;
import com.lulo.process.dto.ProcesoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procesos")
@RequiredArgsConstructor
@Tag(name = "Procesos", description = "Gestión de procesos organizacionales")
public class ProcesoController {

    private final ProcesoService procesoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Listar procesos",
            description = "Lista paginada con filtros opcionales por estado, categoría y nombre")
    public Page<ProcesoResponse> listar(
            @RequestParam UUID empresaId,
            @RequestParam UUID usuarioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return procesoService.listar(empresaId, usuarioId, estado, categoria, nombre, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Obtener proceso con diagrama",
            description = "Retorna el proceso y todos sus elementos del diagrama: lanes, nodos y arcos")
    public ProcesoDetalleResponse obtener(
            @PathVariable UUID id,
            @RequestParam UUID empresaId,
            @RequestParam UUID usuarioId) {
        return procesoService.obtener(id, empresaId, usuarioId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_PROCESO_CREAR') or hasRole('ADMIN_EMPRESA')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear proceso",
            description = "Crea un proceso asociado a la empresa y al pool indicado")
    public ProcesoResponse crear(@Valid @RequestBody CrearProcesoRequest request) {
        return procesoService.crear(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PROCESO_EDITAR') or hasRole('ADMIN_EMPRESA')")
    @Operation(
            summary = "Editar proceso",
            description = "Actualiza los campos indicados del proceso y registra el cambio en el historial")
    public ProcesoResponse editar(
            @PathVariable UUID id,
            @Valid @RequestBody EditarProcesoRequest request) {
        return procesoService.editar(id, request);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PERM_PROCESO_PUBLICAR') or hasAuthority('PERM_PROCESO_EDITAR') or hasRole('ADMIN_EMPRESA')")
    @Operation(
            summary = "Cambiar estado de proceso",
            description = "Cambia el estado del proceso y lo registra en el dashboard")
    public org.springframework.http.ResponseEntity<ProcesoResponse> cambiarEstado(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        return org.springframework.http.ResponseEntity.ok(procesoService.cambiarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_PROCESO_ELIMINAR') or hasRole('ADMIN_EMPRESA')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Eliminar proceso",
            description = "Soft delete: el proceso queda inactivo en BD para mantener trazabilidad histórica")
    public void eliminar(
            @PathVariable UUID id,
            @Valid @RequestBody EliminarProcesoRequest request) {
        procesoService.archivar(id, request);
    }
}
