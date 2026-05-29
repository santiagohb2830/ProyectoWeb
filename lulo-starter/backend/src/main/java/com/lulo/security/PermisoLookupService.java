package com.lulo.security;

import com.lulo.rbac.Permiso;
import com.lulo.rbac.PermisoRepository;
import com.lulo.rbac.RolPool;
import com.lulo.rbac.UsuarioRolPool;
import com.lulo.rbac.UsuarioRolPoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Resuelve los permisos efectivos de un usuario en cada request, a partir del
 * tipoUsuario que viaja en el JWT. Reemplaza el embedding de permisos en el
 * token: así los permisos son siempre frescos y el token queda más pequeño.
 *
 *   SUPERADMIN    -> todos los permisos del catálogo
 *   ADMIN_EMPRESA -> todos los permisos del catálogo (pero acotado a su empresa
 *                    vía aislamiento del TenantFilter)
 *   USUARIO       -> unión de permisos de sus roles activos en cualquier pool
 *                    de la empresa
 */
@Service
public class PermisoLookupService {

    private final PermisoRepository permisoRepository;
    private final UsuarioRolPoolRepository usuarioRolPoolRepository;

    public PermisoLookupService(PermisoRepository permisoRepository,
                                UsuarioRolPoolRepository usuarioRolPoolRepository) {
        this.permisoRepository = permisoRepository;
        this.usuarioRolPoolRepository = usuarioRolPoolRepository;
    }

    @Transactional(readOnly = true)
    public List<String> resolver(UUID usuarioId, String tipoUsuario) {
        if ("SUPERADMIN".equals(tipoUsuario) || "ADMIN_EMPRESA".equals(tipoUsuario)) {
            return permisoRepository.findAllByOrderByCodigoAsc().stream()
                    .map(Permiso::getCodigo)
                    .toList();
        }
        Set<String> set = usuarioRolPoolRepository.findByIdUsuarioId(usuarioId).stream()
                .map(UsuarioRolPool::getRolPool)
                .filter(RolPool::isActivo)
                .flatMap(r -> r.getPermisos().stream())
                .map(Permiso::getCodigo)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return List.copyOf(set);
    }
}
