package com.lulo.config;

import com.lulo.company.Empresa;
import com.lulo.company.EmpresaRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String SUPERADMIN_EMAIL = "admin@lulo.app";
    private static final String SUPERADMIN_PASSWORD = "Admin123!";
    private static final String SUPERADMIN_EMPRESA_NIT = "LULO-APP";
    private static final String LULO_POOL_NOMBRE = "Lulo Console";
    private static final String LULO_ROL_NOMBRE = "SuperAdmin";

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PoolRepository poolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPoolRepository rolPoolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           EmpresaRepository empresaRepository,
                           PoolRepository poolRepository,
                           PermisoRepository permisoRepository,
                           RolPoolRepository rolPoolRepository,
                           UsuarioRolPoolRepository usuarioRolPoolRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.poolRepository = poolRepository;
        this.permisoRepository = permisoRepository;
        this.rolPoolRepository = rolPoolRepository;
        this.usuarioRolPoolRepository = usuarioRolPoolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Empresa empresa = empresaRepository.findByNit(SUPERADMIN_EMPRESA_NIT)
                .orElseGet(() -> {
                    Empresa nueva = new Empresa();
                    nueva.setNombre("Lulo (App Owner)");
                    nueva.setNit(SUPERADMIN_EMPRESA_NIT);
                    nueva.setEmailContacto(SUPERADMIN_EMAIL);
                    return empresaRepository.save(nueva);
                });

        // Pool interno de Lulo: contiene los roles que el SUPERADMIN puede
        // crear y asignar a usuarios internos (operadores, soporte, etc.).
        // Sin este pool, la página /app/permissions no muestra catálogo.
        Pool pool = poolRepository.findByEmpresaIdOrderByNombreAsc(empresa.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Pool nuevoPool = new Pool();
                    nuevoPool.setEmpresa(empresa);
                    nuevoPool.setNombre(LULO_POOL_NOMBRE);
                    nuevoPool.setConfigJson("{}");
                    return poolRepository.save(nuevoPool);
                });

        // Catálogo completo de permisos (para el rol propietario).
        List<Permiso> todos = permisoRepository.findAll();
        Map<String, Permiso> permisoPorCodigo = new java.util.HashMap<>();
        todos.forEach(p -> permisoPorCodigo.put(p.getCodigo(), p));

        // Rol propietario: todos los permisos ADMINISTRATIVOS de Lulo
        // (Lulo no opera procesos ni diagramas, esos son de empresas cliente).
        Set<Permiso> permisosAdminLulo = todos.stream()
                .filter(p -> p.getCodigo().startsWith("EMPRESA_")
                        || p.getCodigo().startsWith("LULO_")
                        || p.getCodigo().startsWith("SOPORTE_")
                        || p.getCodigo().equals("METRICAS_VER")
                        || p.getCodigo().equals("AUDIT_GLOBAL_VER")
                        || p.getCodigo().equals("AUDIT_VER"))
                .collect(java.util.stream.Collectors.toSet());

        RolPool rolOperador = ensureRol(pool, LULO_ROL_NOMBRE,
                "Acceso total a la administración de Lulo",
                true,
                permisosAdminLulo);

        // Garantiza que el rol propietario solo tenga permisos administrativos
        // (limpia permisos operacionales legados como PROCESO_*, USUARIO_*).
        if (!rolOperador.getPermisos().equals(permisosAdminLulo)) {
            rolOperador.setPermisos(permisosAdminLulo);
            rolPoolRepository.save(rolOperador);
        }

        // Roles predefinidos de operación interna que el SUPERADMIN puede asignar
        // a otros usuarios admin@lulo.app (ej. admin2@lulo.app, soporte@lulo.app).
        // Cada rol agrupa un subconjunto coherente del catálogo de permisos.
        ensureRol(pool, "Gestor de Empresas",
                "Crea, edita, suspende y reactiva empresas clientes",
                false,
                pickPerms(permisoPorCodigo,
                        "EMPRESA_VER", "EMPRESA_CREAR", "EMPRESA_EDITAR",
                        "EMPRESA_ELIMINAR", "EMPRESA_SUSPENDER", "EMPRESA_REACTIVAR"));

        ensureRol(pool, "Gestor de Usuarios Lulo",
                "Crea y edita usuarios internos de Lulo y sus roles",
                false,
                pickPerms(permisoPorCodigo,
                        "LULO_USUARIO_VER", "LULO_USUARIO_CREAR",
                        "LULO_USUARIO_EDITAR", "LULO_USUARIO_ELIMINAR",
                        "LULO_ROL_GESTIONAR"));

        ensureRol(pool, "Auditor",
                "Lectura global: audit log y métricas de toda la app",
                false,
                pickPerms(permisoPorCodigo,
                        "AUDIT_GLOBAL_VER", "AUDIT_VER", "METRICAS_VER",
                        "EMPRESA_VER", "LULO_USUARIO_VER"));

        ensureRol(pool, "Soporte",
                "Lectura de procesos cliente para soporte técnico",
                false,
                pickPerms(permisoPorCodigo,
                        "SOPORTE_PROCESOS_VER", "EMPRESA_VER",
                        "AUDIT_VER", "PROCESO_VER"));

        // Superadmin user
        Usuario superadmin = usuarioRepository.findByEmail(SUPERADMIN_EMAIL).orElseGet(() -> {
            Usuario s = new Usuario();
            s.setEmail(SUPERADMIN_EMAIL);
            s.setPasswordHash(passwordEncoder.encode(SUPERADMIN_PASSWORD));
            s.setEstado("activo");
            s.setEmpresa(empresa);
            s.setTipoUsuario(TipoUsuario.SUPERADMIN);
            log.info("Superadmin creado: {} / {} (cambiar password tras primer login)",
                    SUPERADMIN_EMAIL, SUPERADMIN_PASSWORD);
            return usuarioRepository.save(s);
        });

        // Asignación superadmin → rol operador (idempotente)
        UsuarioRolPoolId asignId = new UsuarioRolPoolId(superadmin.getId(), rolOperador.getId());
        if (!usuarioRolPoolRepository.existsById(asignId)) {
            UsuarioRolPool asign = new UsuarioRolPool();
            asign.setId(asignId);
            asign.setUsuario(superadmin);
            asign.setRolPool(rolOperador);
            usuarioRolPoolRepository.save(asign);
        }
    }

    /** Crea el rol si no existe; si existe, deja sus permisos en paz. */
    private RolPool ensureRol(Pool pool, String nombre, String descripcion,
                              boolean esPropietario, Set<Permiso> permisos) {
        return rolPoolRepository.findByPoolId(pool.getId()).stream()
                .filter(r -> nombre.equals(r.getNombre()))
                .findFirst()
                .orElseGet(() -> {
                    RolPool r = new RolPool();
                    r.setPool(pool);
                    r.setNombre(nombre);
                    r.setDescripcion(descripcion);
                    r.setActivo(true);
                    r.setEsPropietario(esPropietario);
                    r.setPermisos(permisos);
                    return rolPoolRepository.save(r);
                });
    }

    private Set<Permiso> pickPerms(Map<String, Permiso> catalogo, String... codigos) {
        Set<Permiso> set = new HashSet<>();
        for (String c : codigos) {
            Permiso p = catalogo.get(c);
            if (p != null) set.add(p);
        }
        return set;
    }
}
