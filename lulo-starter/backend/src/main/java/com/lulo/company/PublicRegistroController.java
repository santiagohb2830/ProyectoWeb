package com.lulo.company;

import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Registro público de empresas (sin autenticación). Permite que cualquier
 * representante de una empresa cree su cuenta empresarial. Es el endpoint
 * que consume la landing pública "/register-empresa".
 *
 * NOTA: cumple HU-01. Crea empresa + admin inicial (ADMIN_EMPRESA) + pool
 * por defecto, todo en una transacción vía EmpresaService.registrar().
 */
@RestController
@RequestMapping("/api/public/empresas")
@Tag(name = "Registro público", description = "Auto-registro de empresas desde la landing")
public class PublicRegistroController {

    private final EmpresaService empresaService;

    public PublicRegistroController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Auto-registro de empresa nueva")
    public RegistroEmpresaResponse registrar(@Valid @RequestBody RegistroEmpresaRequest request) {
        return empresaService.registrar(request);
    }
}
