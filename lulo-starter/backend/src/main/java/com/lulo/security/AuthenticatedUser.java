package com.lulo.security;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUser(
        UUID usuarioId,
        String email,
        UUID empresaId,
        String tipoUsuario,
        List<String> permisos
) {
    public boolean isSuperadmin() {
        return "SUPERADMIN".equals(tipoUsuario);
    }

    public boolean isAdminEmpresa() {
        return "ADMIN_EMPRESA".equals(tipoUsuario);
    }

    public boolean hasPermiso(String codigo) {
        return permisos != null && permisos.contains(codigo);
    }
}
