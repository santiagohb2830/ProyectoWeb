package com.lulo.company;

import com.lulo.company.dto.EmpresaDetalleResponse;
import com.lulo.company.dto.EmpresaListItemResponse;
import com.lulo.company.dto.RegistroEmpresaRequest;
import com.lulo.company.dto.RegistroEmpresaResponse;
import com.lulo.company.dto.UsuarioBasicoResponse;
import com.lulo.common.exception.ApiException;
import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolId;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.TipoUsuario;
import com.lulo.users.Usuario;
import com.lulo.users.UsuarioRepository;
import com.lulo.process.ProcesoRepository;
import com.lulo.company.dto.EmpresaDetailResponse;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaService {

    private static final String POOL_DEFAULT_NOMBRE = "Principal";
    private static final String ROL_ADMIN_NOMBRE    = "Administrador";

    @Autowired
    private EmpresaRepository        empresaRepository;
    @Autowired
    private UsuarioRepository        usuarioRepository;
    @Autowired
    private com.lulo.process.ProcesoRepository procesoRepository;
    @Autowired
    private PoolRepository           poolRepository;
    @Autowired
    private PermisoRepository        permisoRepository;
    @Autowired
    private RolPoolRepository        rolPoolRepository;
    @Autowired
    private UsuarioRolPoolRepository usuarioRolPoolRepository;
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    private com.lulo.users.UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<EmpresaListItemResponse> listar() {
        // Oculta la empresa propietaria Lulo del listado: no es un cliente,
        // es la empresa dueña de la app. Aparecería sólo si se accede al endpoint
        // de detalle por ID explícitamente.
        return empresaRepository.findAll().stream()
                .filter(Empresa::isActivo)
                .filter(e -> !"LULO-APP".equals(e.getNit()))
                .map(this::toListItem)
                .toList();
    }
    private ProcesoRepository        procesoRepository;

    private EmpresaListItemResponse toListItem(Empresa empresa) {
        return EmpresaListItemResponse.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .nit(empresa.getNit())
                .emailContacto(empresa.getEmailContacto())
                .dominio(empresa.getDominio())
                .createdAt(empresa.getCreatedAt())
                .totalUsuarios(usuarioRepository.findByEmpresaId(empresa.getId()).size())
                .totalProcesos(procesoRepository.countByEmpresaIdAndActivoTrue(empresa.getId()))
                .totalPools(poolRepository.findByEmpresaIdOrderByNombreAsc(empresa.getId()).size())
                .build();
    }

    private void validarDominio(String dominio) {
        if (dominio == null || dominio.isBlank() ||
                dominio.contains("@") || dominio.contains(" ") ||
                !dominio.contains(".")) {
            throw new ApiException(
                    "Dominio inválido. Use formato 'empresa.com'",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validarEmailDominio(String email, String dominio) {
        if (email == null || !email.toLowerCase().endsWith("@" + dominio.toLowerCase())) {
            throw new ApiException(
                    "El correo del admin debe terminar en @" + dominio,
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public RegistroEmpresaResponse registrar(RegistroEmpresaRequest request) {

        // ── Validaciones de unicidad y dominio ────────────────────────────────
        validarDominio(request.getDominio());
        validarEmailDominio(request.getEmailAdmin(), request.getDominio());

        if (empresaRepository.existsByNit(request.getNit())) {
            throw new ApiException(
                    "Ya existe una empresa registrada con el NIT: " + request.getNit(),
                    HttpStatus.CONFLICT);
        }

        if (usuarioRepository.existsByEmail(request.getEmailAdmin())) {
            throw new ApiException(
                    "El correo de administrador ya está en uso: " + request.getEmailAdmin(),
                    HttpStatus.CONFLICT);
        }

        // ── 1. Crear Empresa ──────────────────────────────────────────────────
        Empresa empresa = new Empresa();
        empresa.setNombre(request.getNombreEmpresa());
        empresa.setNit(request.getNit());
        empresa.setEmailContacto(request.getEmailContacto());
        empresa.setDominio(request.getDominio().toLowerCase());
        empresa.setActivo(true);
        empresa = empresaRepository.save(empresa);

        // ── 2. Crear Usuario administrador inicial ────────────────────────────
        Usuario admin = new Usuario();
        admin.setEmpresa(empresa);
        admin.setEmail(request.getEmailAdmin());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setEstado("activo");
        admin.setTipoUsuario(TipoUsuario.ADMIN_EMPRESA);
        admin = usuarioRepository.save(admin);

        // ── 3. Crear Pool por defecto ─────────────────────────────────────────
        Pool pool = new Pool();
        pool.setEmpresa(empresa);
        pool.setNombre(POOL_DEFAULT_NOMBRE);
        pool = poolRepository.save(pool);

        // ── 4. Crear Rol propietario con todos los permisos ───────────────────
        List<Permiso> todosLosPermisos = permisoRepository.findAll();

        RolPool rolAdmin = new RolPool();
        rolAdmin.setPool(pool);
        rolAdmin.setNombre(ROL_ADMIN_NOMBRE);
        rolAdmin.setDescripcion("Rol con acceso completo al pool");
        rolAdmin.setActivo(true);
        rolAdmin.setEsPropietario(true);
        rolAdmin.setPermisos(new HashSet<>(todosLosPermisos));
        rolAdmin = rolPoolRepository.save(rolAdmin);

        // ── 5. Asignar usuario → rol en el pool ──────────────────────────────
        UsuarioRolPool asignacion = new UsuarioRolPool();
        asignacion.setId(new UsuarioRolPoolId(admin.getId(), rolAdmin.getId()));
        asignacion.setUsuario(admin);
        asignacion.setRolPool(rolAdmin);
        usuarioRolPoolRepository.save(asignacion);

        // ── Respuesta ─────────────────────────────────────────────────────────
        return RegistroEmpresaResponse.builder()
                .empresaId(empresa.getId())
                .empresaNombre(empresa.getNombre())
                .usuarioId(admin.getId())
                .emailAdmin(admin.getEmail())
                .poolDefault(pool.getNombre())
                .mensaje("Empresa registrada exitosamente")
                .build();
    }

    @Transactional(readOnly = true)
    public EmpresaDetalleResponse obtener(java.util.UUID id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));

        List<Usuario> usuarios = usuarioRepository.findByEmpresaId(id);

        List<UsuarioBasicoResponse> usuariosDto = usuarios.stream().map(u -> {
            List<UsuarioRolPool> roles = usuarioRolPoolRepository.findByUsuarioIdAndEmpresaId(u.getId(), id);
            String rolPrincipal = roles.stream()
                    .map(urp -> urp.getRolPool().getNombre())
                    .findFirst()
                    .orElse("Sin rol");
            return UsuarioBasicoResponse.builder()
                    .id(u.getId())
                    .email(u.getEmail())
                    .estado(u.getEstado())
                    .rolPrincipal(rolPrincipal)
                    .createdAt(u.getCreatedAt())
                    .protegido(usuarioService.esProtegido(u))
                    .build();
        }).collect(Collectors.toList());

        long totalPools = poolRepository.findByEmpresaIdOrderByNombreAsc(id).size();
        long totalRoles = poolRepository.findByEmpresaIdOrderByNombreAsc(id).stream()
                .mapToLong(p -> rolPoolRepository.findByPoolId(p.getId()).size())
                .sum();

        return EmpresaDetalleResponse.builder()
        List<Pool> pools = poolRepository.findByEmpresaIdOrderByNombreAsc(id);

        long totalRolesPool = pools.stream()
                .mapToLong(p -> rolPoolRepository.findByPoolId(p.getId()).size())
                .sum();

        long totalProcesos = pools.stream()
                .mapToLong(p -> procesoRepository.findByPoolIdAndActivoTrue(p.getId()).size())
                .sum();

        List<EmpresaDetailResponse.UsuarioBasicoResponse> usuarioResponses = usuarios.stream()
                .map(u -> EmpresaDetailResponse.UsuarioBasicoResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .estado(u.getEstado())
                        .rolPrincipal(obtenerRolPrincipal(u.getId(), id))
                        .createdAt(u.getCreatedAt())
                        .build())
                .toList();

        return EmpresaDetailResponse.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .nit(empresa.getNit())
                .emailContacto(empresa.getEmailContacto())
                .createdAt(empresa.getCreatedAt())
                .totalUsuarios(usuariosDto.size())
                .totalProcesos(procesoRepository.countByEmpresaIdAndActivoTrue(empresa.getId()))
                .totalPools(totalPools)
                .totalRolesPool(totalRoles)
                .usuarios(usuariosDto)
                .dominio(empresa.getDominio())
                .activo(empresa.isActivo())
                .build();
    }

    @Transactional
    public EmpresaListItemResponse editar(java.util.UUID id,
            com.lulo.company.dto.EditarEmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));
        if (!empresa.isActivo()) {
            throw new ApiException("Empresa inactiva", HttpStatus.CONFLICT);
        }
        // La empresa propietaria de la app (Lulo) NO se edita por esta vía.
        // Protege al SUPERADMIN de cambios accidentales que podrían bloquear
        // el acceso al sistema.
        if ("LULO-APP".equals(empresa.getNit())) {
            throw new ApiException(
                    "La empresa propietaria (Lulo) no se puede editar desde aquí",
                    HttpStatus.FORBIDDEN);
        }
        if (request.getNombreEmpresa() != null && !request.getNombreEmpresa().isBlank()) {
            empresa.setNombre(request.getNombreEmpresa());
        }
        if (request.getEmailContacto() != null && !request.getEmailContacto().isBlank()) {
            empresa.setEmailContacto(request.getEmailContacto());
        }
        if (request.getDominio() != null && !request.getDominio().isBlank()) {
            validarDominio(request.getDominio());
            empresa.setDominio(request.getDominio().toLowerCase());
        }

        // Cambio/creación de admin: si se envía nuevoEmailAdmin, actualizar o crear.
        if (request.getNuevoEmailAdmin() != null && !request.getNuevoEmailAdmin().isBlank()) {
            validarEmailDominio(request.getNuevoEmailAdmin(), empresa.getDominio());

            // Busca admin actual de la empresa
            Usuario adminActual = usuarioRepository.findByEmpresaId(empresa.getId()).stream()
                    .filter(u -> u.getTipoUsuario() == TipoUsuario.ADMIN_EMPRESA)
                    .findFirst().orElse(null);

            if (adminActual != null) {
                // Si el email cambia, valida unicidad global
                if (!adminActual.getEmail().equalsIgnoreCase(request.getNuevoEmailAdmin())
                        && usuarioRepository.existsByEmail(request.getNuevoEmailAdmin())) {
                    throw new ApiException("Correo ya en uso: " + request.getNuevoEmailAdmin(),
                            HttpStatus.CONFLICT);
                }
                adminActual.setEmail(request.getNuevoEmailAdmin());
                if (request.getNuevoPasswordAdmin() != null && request.getNuevoPasswordAdmin().length() >= 6) {
                    adminActual.setPasswordHash(passwordEncoder.encode(request.getNuevoPasswordAdmin()));
                }
                usuarioRepository.save(adminActual);
            } else {
                // No hay admin: crear uno
                if (usuarioRepository.existsByEmail(request.getNuevoEmailAdmin())) {
                    throw new ApiException("Correo ya en uso: " + request.getNuevoEmailAdmin(),
                            HttpStatus.CONFLICT);
                }
                if (request.getNuevoPasswordAdmin() == null || request.getNuevoPasswordAdmin().length() < 6) {
                    throw new ApiException("Password del nuevo admin debe tener al menos 6 caracteres",
                            HttpStatus.BAD_REQUEST);
                }
                Usuario nuevoAdmin = new Usuario();
                nuevoAdmin.setEmpresa(empresa);
                nuevoAdmin.setEmail(request.getNuevoEmailAdmin());
                nuevoAdmin.setPasswordHash(passwordEncoder.encode(request.getNuevoPasswordAdmin()));
                nuevoAdmin.setEstado("activo");
                nuevoAdmin.setTipoUsuario(TipoUsuario.ADMIN_EMPRESA);
                usuarioRepository.save(nuevoAdmin);
            }
        }

        empresa = empresaRepository.save(empresa);
        return toListItem(empresa);
    }

    /**
     * Soft delete: marca empresa como inactiva.
     * Requiere que el caller envíe el nombre exacto de la empresa como
     * confirmación, evitando borrados accidentales.
     */
    @Transactional
    public void desactivar(java.util.UUID id, String nombreConfirmacion) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Empresa no encontrada", HttpStatus.NOT_FOUND));
        if (nombreConfirmacion == null || !nombreConfirmacion.equals(empresa.getNombre())) {
            throw new ApiException(
                    "Debe escribir el nombre exacto de la empresa para confirmar el borrado",
                    HttpStatus.BAD_REQUEST);
        }
        if ("LULO-APP".equals(empresa.getNit())) {
            throw new ApiException("No se puede desactivar la empresa propietaria de la app",
                    HttpStatus.FORBIDDEN);
        }
        empresa.setActivo(false);
        empresaRepository.save(empresa);
        // Suspende usuarios de la empresa para que no puedan ingresar.
        usuarioRepository.findByEmpresaId(id).forEach(u -> {
            u.setEstado("suspendido");
            usuarioRepository.save(u);
        });
    }
}
