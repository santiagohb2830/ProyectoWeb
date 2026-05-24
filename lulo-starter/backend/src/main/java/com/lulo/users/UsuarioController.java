package com.lulo.users;

import com.lulo.security.AuthContext;
import com.lulo.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService    usuarioService;

    /**
     * Lista usuarios. SUPERADMIN ve todos; ADMIN_EMPRESA/USUARIO solo de su empresa.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Map<String, Object>> listar() {
        AuthenticatedUser caller = AuthContext.require();
        List<Usuario> usuarios = caller.isSuperadmin()
                ? usuarioRepository.findAll()
                : usuarioRepository.findByEmpresaId(caller.empresaId());
        return usuarios.stream().map(u -> {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", u.getId().toString());
            m.put("email", u.getEmail());
            m.put("estado", u.getEstado());
            m.put("empresaId", u.getEmpresa().getId().toString());
            m.put("tipoUsuario", u.getTipoUsuario().name());
            return m;
        }).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_EMPRESA')")
    public ResponseEntity<CrearUsuarioDirectoResponse> crear(
            @RequestBody CrearUsuarioDirectoRequest request) {
        return ResponseEntity.status(201).body(usuarioService.crearDirecto(request));
    }

    @PatchMapping("/{usuarioId}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN_EMPRESA')")
    public ResponseEntity<ActualizarUsuarioResponse> actualizar(
            @PathVariable UUID usuarioId,
            @RequestBody ActualizarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(usuarioId, request));
    }
}
