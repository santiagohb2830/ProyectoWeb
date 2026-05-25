package com.lulo.users;

import com.lulo.common.exception.ApiException;
import com.lulo.company.EmpresaRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolId;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.security.AuthContext;
import com.lulo.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository       usuarioRepository;
    private final EmpresaRepository       empresaRepository;
    private final RolPoolRepository       rolPoolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PasswordEncoder         passwordEncoder;
    private final com.lulo.audit.AuditService auditService;

    @Transactional
    public CrearUsuarioDirectoResponse crearDirecto(CrearUsuarioDirectoRequest request) {

        AuthenticatedUser caller = AuthContext.require();
        // ADMIN_EMPRESA solo puede crear usuarios en SU empresa.
        if (!caller.isSuperadmin() && !caller.empresaId().equals(request.getEmpresaId())) {
            throw new ApiException("No puede crear usuarios en otra empresa", HttpStatus.FORBIDDEN);
        }

        var empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        // El email debe pertenecer al dominio de la empresa destino.
        // SUPERADMIN creando en empresa "Lulo (App Owner)" exige @lulo.app;
        // ADMIN_EMPRESA creando en su empresa exige @<dominio_empresa>.
        validarEmailContraDominioEmpresa(request.getEmail(), empresa);

        RolPool rolPool = rolPoolRepository.findById(request.getRolPoolId())
                .orElseThrow(() -> new ApiException("Rol no encontrado", HttpStatus.NOT_FOUND));

        if (!rolPool.getPool().getEmpresa().getId().equals(request.getEmpresaId())) {
            throw new ApiException("El rol no pertenece a esta empresa", HttpStatus.FORBIDDEN);
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(
                    "El correo ya está registrado: " + request.getEmail(),
                    HttpStatus.CONFLICT);
        }

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado("activo");
        usuario = usuarioRepository.save(usuario);

        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(usuario.getId(), rolPool.getId()));
        asignacion.setUsuario(usuario);
        asignacion.setRolPool(rolPool);
        usuarioRolPoolRepository.save(asignacion);

        auditService.registrar(empresa, usuario, "USUARIO", usuario.getId(), "CREAR",
                null,
                java.util.Map.of("email", usuario.getEmail(),
                                 "rol", rolPool.getNombre()));

        return CrearUsuarioDirectoResponse.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .rolAsignado(rolPool.getNombre())
                .empresaNombre(empresa.getNombre())
                .mensaje("Usuario creado exitosamente")
                .build();
    }

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("activo", "suspendido", "inactivo", "pendiente");

    @Transactional
    public void resetPassword(UUID usuarioId, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            throw new ApiException("Password mínimo 6 caracteres", HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = requireMutable(usuarioId);
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        auditService.registrar(usuario.getEmpresa(), usuario, "USUARIO",
                usuario.getId(), "RESET_PASSWORD", null,
                java.util.Map.of("email", usuario.getEmail()));
    }

    @Transactional
    public void cambiarEmail(UUID usuarioId, String nuevoEmail) {
        if (nuevoEmail == null || !nuevoEmail.contains("@")) {
            throw new ApiException("Email inválido", HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = requireMutable(usuarioId);
        validarEmailContraDominioEmpresa(nuevoEmail, usuario.getEmpresa());
        if (!usuario.getEmail().equalsIgnoreCase(nuevoEmail)
                && usuarioRepository.existsByEmail(nuevoEmail)) {
            throw new ApiException("Correo ya en uso", HttpStatus.CONFLICT);
        }
        String anterior = usuario.getEmail();
        usuario.setEmail(nuevoEmail);
        usuarioRepository.save(usuario);
        auditService.registrar(usuario.getEmpresa(), usuario, "USUARIO",
                usuario.getId(), "CAMBIAR_EMAIL",
                java.util.Map.of("email", anterior),
                java.util.Map.of("email", nuevoEmail));
    }

    /** Soft delete: marca usuario como inactivo. No se puede borrar protegidos. */
    @Transactional
    public void eliminar(UUID usuarioId) {
        Usuario usuario = requireMutable(usuarioId);
        usuario.setEstado("inactivo");
        usuarioRepository.save(usuario);
        auditService.registrar(usuario.getEmpresa(), usuario, "USUARIO",
                usuario.getId(), "ELIMINAR",
                java.util.Map.of("email", usuario.getEmail()),
                null);
    }

    /**
     * Valida que el usuario destino se pueda modificar:
     *  - existe
     *  - caller es SUPERADMIN o del mismo empresa
     *  - no es SUPERADMIN ni el admin original de la empresa.
     */
    private Usuario requireMutable(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        AuthenticatedUser caller = AuthContext.require();
        if (!caller.isSuperadmin() && !caller.empresaId().equals(usuario.getEmpresa().getId())) {
            throw new ApiException("No puede modificar usuarios de otra empresa",
                    HttpStatus.FORBIDDEN);
        }
        if (esProtegido(usuario)) {
            throw new ApiException(
                    "Este usuario está protegido y no puede modificarse ni eliminarse",
                    HttpStatus.FORBIDDEN);
        }
        return usuario;
    }

    /**
     * Usuario protegido: SUPERADMIN dueño de la app, o el primer usuario que
     * se creó en cada empresa (el admin original, "papá de los pollitos").
     * Estos usuarios no se pueden editar ni eliminar desde la UI.
     */
    public boolean esProtegido(Usuario usuario) {
        if (usuario.getTipoUsuario() == TipoUsuario.SUPERADMIN) return true;
        Usuario primero = usuarioRepository.findByEmpresaId(usuario.getEmpresa().getId())
                .stream()
                .min(java.util.Comparator.comparing(Usuario::getCreatedAt))
                .orElse(null);
        return primero != null && primero.getId().equals(usuario.getId());
    }

    private void validarEmailContraDominioEmpresa(String email, com.lulo.company.Empresa empresa) {
        if (email == null || !email.contains("@")) {
            throw new ApiException("Email inválido", HttpStatus.BAD_REQUEST);
        }
        String dominio = empresa.getDominio();
        if (dominio == null || dominio.isBlank()) {
            throw new ApiException(
                    "La empresa no tiene dominio configurado. Edita la empresa primero.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!email.toLowerCase().endsWith("@" + dominio.toLowerCase())) {
            throw new ApiException(
                    "El correo debe terminar en @" + dominio,
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Edita un usuario: cambia su estado y/o reemplaza su rol.
     * Reemplazar el rol elimina todas las asignaciones previas del usuario
     * y deja únicamente la indicada (modelo de un rol por usuario).
     */
    @Transactional
    public ActualizarUsuarioResponse actualizar(UUID usuarioId, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        AuthenticatedUser caller = AuthContext.require();
        if (!caller.isSuperadmin() && !caller.empresaId().equals(usuario.getEmpresa().getId())) {
            throw new ApiException("No puede editar usuarios de otra empresa", HttpStatus.FORBIDDEN);
        }
        // El SUPERADMIN no se modifica desde la administración de usuarios.
        // Protege contra suspender/desactivar al dueño de la app.
        if (usuario.getTipoUsuario() == TipoUsuario.SUPERADMIN) {
            throw new ApiException(
                    "El SUPERADMIN no puede ser modificado desde la gestión de usuarios",
                    HttpStatus.FORBIDDEN);
        }

        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            String estado = request.getEstado().trim().toLowerCase();
            if (!ESTADOS_VALIDOS.contains(estado)) {
                throw new ApiException("Estado inválido: " + request.getEstado(), HttpStatus.BAD_REQUEST);
            }
            usuario.setEstado(estado);
        }

        String rolAsignado = null;
        if (request.getRolPoolId() != null) {
            RolPool rolPool = rolPoolRepository.findById(request.getRolPoolId())
                    .orElseThrow(() -> new ApiException("Rol no encontrado", HttpStatus.NOT_FOUND));
            if (!rolPool.getPool().getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
                throw new ApiException("El rol no pertenece a la empresa del usuario", HttpStatus.FORBIDDEN);
            }
            // Reemplaza el rol: borra las asignaciones previas y deja solo la nueva.
            usuarioRolPoolRepository.deleteAll(usuarioRolPoolRepository.findByIdUsuarioId(usuarioId));
            usuarioRolPoolRepository.flush();
            UsuarioRolPool asignacion = new UsuarioRolPool();
            asignacion.setId(new UsuarioRolPoolId(usuario.getId(), rolPool.getId()));
            asignacion.setUsuario(usuario);
            asignacion.setRolPool(rolPool);
            usuarioRolPoolRepository.save(asignacion);
            rolAsignado = rolPool.getNombre();
        }

        usuario = usuarioRepository.save(usuario);

        if (rolAsignado == null) {
            rolAsignado = usuarioRolPoolRepository.findByIdUsuarioId(usuarioId).stream()
                    .map(urp -> urp.getRolPool().getNombre())
                    .findFirst()
                    .orElse("Sin rol");
        }

        auditService.registrar(usuario.getEmpresa(), usuario, "USUARIO",
                usuario.getId(), "EDITAR",
                null,
                java.util.Map.of("estado", usuario.getEstado(),
                                 "rol", rolAsignado));

        return ActualizarUsuarioResponse.builder()
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .estado(usuario.getEstado())
                .rolAsignado(rolAsignado)
                .build();
    }
}
