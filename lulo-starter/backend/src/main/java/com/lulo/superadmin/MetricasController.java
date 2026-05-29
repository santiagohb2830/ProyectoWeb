package com.lulo.superadmin;

import com.lulo.audit.AuditLogRepository;
import com.lulo.company.EmpresaRepository;
import com.lulo.pool.PoolRepository;
import com.lulo.process.ProcesoRepository;
import com.lulo.rbac.RolPoolRepository;
import com.lulo.users.UsuarioRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Métricas agregadas de la app. Solo para usuarios con METRICAS_VER o
 * SUPERADMIN. Combina conteos de las entidades principales para alimentar
 * el dashboard de Lulo.
 */
@RestController
@RequestMapping("/api/superadmin/metricas")
@PreAuthorize("hasRole('SUPERADMIN') or hasAuthority('PERM_METRICAS_VER')")
@Tag(name = "Métricas", description = "Stats agregadas de la plataforma")
public class MetricasController {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProcesoRepository procesoRepository;
    private final PoolRepository poolRepository;
    private final RolPoolRepository rolPoolRepository;
    private final AuditLogRepository auditLogRepository;

    public MetricasController(EmpresaRepository empresaRepository,
                              UsuarioRepository usuarioRepository,
                              ProcesoRepository procesoRepository,
                              PoolRepository poolRepository,
                              RolPoolRepository rolPoolRepository,
                              AuditLogRepository auditLogRepository) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.procesoRepository = procesoRepository;
        this.poolRepository = poolRepository;
        this.rolPoolRepository = rolPoolRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public Map<String, Object> resumen() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("empresas",      empresaRepository.count());
        m.put("usuarios",      usuarioRepository.count());
        m.put("procesos",      procesoRepository.count());
        m.put("pools",         poolRepository.count());
        m.put("rolesPool",     rolPoolRepository.count());
        m.put("eventosAudit",  auditLogRepository.count());
        m.put("timestamp",     java.time.Instant.now().toString());
        return m;
    }
}
