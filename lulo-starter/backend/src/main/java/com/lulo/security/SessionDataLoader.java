package com.lulo.security;

import com.lulo.pool.Pool;
import com.lulo.pool.PoolRepository;
import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import com.lulo.users.TipoUsuario;
import com.lulo.users.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Calcula los datos de sesión (rol, pool por defecto, permisos efectivos)
 * para un usuario. Lo usan login y refresh para devolver siempre el estado
 * actualizado (si un admin cambia permisos, en el próximo refresh se reflejan).
 */
@Service
public class SessionDataLoader {

    private final PoolRepository poolRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;
    private final PermisoRepository permisoRepository;

    public SessionDataLoader(PoolRepository poolRepository,
                             UsuarioRolPoolRepository usuarioRolPoolRepository,
                             PermisoRepository permisoRepository) {
        this.poolRepository = poolRepository;
        this.usuarioRolPoolRepository = usuarioRolPoolRepository;
        this.permisoRepository = permisoRepository;
    }

    public SessionData load(Usuario usuario) {
        TipoUsuario tipo = usuario.getTipoUsuario();

        // SUPERADMIN: todos los permisos del catálogo + pool interno de Lulo
        // (necesario para que el frontend pueda crear usuarios y asignarles roles
        // pre-configurados en la Consola Lulo).
        if (tipo == TipoUsuario.SUPERADMIN) {
            List<String> permisos = permisoRepository.findAllByOrderByCodigoAsc().stream()
                    .map(Permiso::getCodigo)
                    .collect(Collectors.toCollection(ArrayList::new));
            String poolIdLulo = poolRepository
                    .findByEmpresaIdOrderByNombreAsc(usuario.getEmpresa().getId())
                    .stream().findFirst().map(p -> p.getId().toString()).orElse(null);
            return new SessionData(poolIdLulo, "SUPERADMIN", tipo.name(), permisos, true);
        }

        // Pool por defecto de la empresa (necesario para el frontend builder).
        List<Pool> pools = poolRepository.findByEmpresaIdOrderByNombreAsc(
                usuario.getEmpresa().getId());
        Pool targetPool;
        if (pools.isEmpty()) {
            Pool nuevo = new Pool();
            nuevo.setNombre("Main Pool");
            nuevo.setEmpresa(usuario.getEmpresa());
            nuevo.setConfigJson("{}");
            targetPool = poolRepository.save(nuevo);
        } else {
            targetPool = pools.get(0);
        }

        boolean esPropietarioPool = usuarioRolPoolRepository
                .findByIdUsuarioId(usuario.getId()).stream()
                .anyMatch(urp -> urp.getRolPool().isEsPropietario());

        Set<String> permisosSet;
        if (tipo == TipoUsuario.ADMIN_EMPRESA || esPropietarioPool) {
            permisosSet = permisoRepository.findAllByOrderByCodigoAsc().stream()
                    .map(Permiso::getCodigo)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            permisosSet = usuarioRolPoolRepository
                    .findByUsuarioIdAndPoolId(usuario.getId(), targetPool.getId()).stream()
                    .map(UsuarioRolPool::getRolPool)
                    .filter(RolPool::isActivo)
                    .flatMap(rol -> rol.getPermisos().stream())
                    .map(Permiso::getCodigo)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        String rolLegado;
        if (tipo == TipoUsuario.ADMIN_EMPRESA) rolLegado = "ADMIN_EMPRESA";
        else if (esPropietarioPool) rolLegado = "PROPIETARIO";
        else rolLegado = "COLABORADOR";

        return new SessionData(
                targetPool.getId().toString(),
                rolLegado,
                tipo.name(),
                new ArrayList<>(permisosSet),
                false
        );
    }

    public record SessionData(
            String poolId,
            String rol,
            String tipoUsuario,
            List<String> permisos,
            boolean esSuperadmin
    ) {}
}
