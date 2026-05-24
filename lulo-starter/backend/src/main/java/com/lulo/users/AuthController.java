package com.lulo.users;

import com.lulo.security.AuthContext;
import com.lulo.security.AuthenticatedUser;
import com.lulo.security.JwtService;
import com.lulo.security.SessionDataLoader;
import com.lulo.security.SessionDataLoader.SessionData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SessionDataLoader sessionDataLoader;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          SessionDataLoader sessionDataLoader) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.sessionDataLoader = sessionDataLoader;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Faltan credenciales"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        if (!"activo".equalsIgnoreCase(usuario.getEstado())) {
            return ResponseEntity.status(403).body(Map.of("error", "Usuario inactivo o suspendido"));
        }
        return ResponseEntity.ok(buildSessionResponse(usuario));
    }

    /**
     * Renueva el token con datos frescos: el frontend lo llama desde un guard
     * en cada navegación. Si los permisos cambiaron en backend, se reflejan aquí.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh() {
        AuthenticatedUser current = AuthContext.require();
        Usuario usuario = usuarioRepository.findById(current.usuarioId())
                .orElse(null);
        if (usuario == null || !"activo".equalsIgnoreCase(usuario.getEstado())) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no válido"));
        }
        return ResponseEntity.ok(buildSessionResponse(usuario));
    }

    /** Devuelve datos del usuario autenticado sin renovar token. */
    @GetMapping("/me")
    public ResponseEntity<?> me() {
        AuthenticatedUser current = AuthContext.require();
        Usuario usuario = usuarioRepository.findById(current.usuarioId())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
        }
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuario.getId().toString());
        body.put("email", usuario.getEmail());
        body.put("empresaId", usuario.getEmpresa().getId().toString());
        body.put("empresaNombre", usuario.getEmpresa().getNombre());
        body.put("tipoUsuario", usuario.getTipoUsuario().name());
        body.put("estado", usuario.getEstado());
        return ResponseEntity.ok(body);
    }

    private Map<String, Object> buildSessionResponse(Usuario usuario) {
        SessionData data = sessionDataLoader.load(usuario);
        UUID empresaIdParaToken = data.esSuperadmin() ? null : usuario.getEmpresa().getId();

        String token = jwtService.generate(
                usuario.getId(),
                usuario.getEmail(),
                empresaIdParaToken,
                data.tipoUsuario(),
                data.permisos()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("expiresInMs", jwtService.getExpiryMs());
        response.put("email", usuario.getEmail());
        response.put("usuarioId", usuario.getId().toString());
        response.put("empresaId", usuario.getEmpresa().getId().toString());
        response.put("empresaNombre", data.esSuperadmin()
                ? "Lulo (Dueño App)"
                : usuario.getEmpresa().getNombre());
        response.put("poolId", data.poolId());
        response.put("rol", data.rol());
        response.put("tipoUsuario", data.tipoUsuario());
        response.put("esSuperadmin", data.esSuperadmin());
        response.put("esAdminEmpresa", "ADMIN_EMPRESA".equals(data.tipoUsuario()));
        // True si el usuario pertenece a la empresa interna de Lulo (NIT LULO-APP).
        // Lo usa el frontend para mostrar la "Consola Lulo" en lugar del menú normal.
        response.put("empresaEsLulo", "LULO-APP".equals(usuario.getEmpresa().getNit()));
        response.put("permisos", data.permisos());
        return response;
    }
}
