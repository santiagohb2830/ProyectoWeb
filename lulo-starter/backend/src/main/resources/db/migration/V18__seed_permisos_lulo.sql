-- Permisos granulares para la consola interna de Lulo (dueños de la app).
-- Un usuario que tenga TODOS estos permisos puede operar como SUPERADMIN
-- de hecho, aunque su tipo_usuario siga siendo USUARIO.

INSERT INTO permiso (codigo, descripcion) VALUES
    -- Empresas (extiende los EMPRESA_VER/CREAR/EDITAR/ELIMINAR ya seedeados en V17)
    ('EMPRESA_SUSPENDER', 'Suspender temporalmente una empresa cliente'),
    ('EMPRESA_REACTIVAR', 'Reactivar una empresa suspendida'),

    -- Usuarios internos de la consola Lulo
    ('LULO_USUARIO_VER',      'Ver usuarios internos de Lulo'),
    ('LULO_USUARIO_CREAR',    'Crear usuarios internos de Lulo'),
    ('LULO_USUARIO_EDITAR',   'Editar usuarios internos de Lulo'),
    ('LULO_USUARIO_ELIMINAR', 'Eliminar usuarios internos de Lulo'),

    -- Roles internos de Lulo
    ('LULO_ROL_GESTIONAR',    'Crear, editar y asignar roles internos de Lulo'),

    -- Observabilidad de la app entera
    ('METRICAS_VER',          'Ver métricas globales de la aplicación'),
    ('AUDIT_GLOBAL_VER',      'Ver audit log de todas las empresas'),

    -- Soporte: ver datos de empresas cliente sin modificar
    ('SOPORTE_PROCESOS_VER',  'Ver procesos de cualquier empresa (solo lectura)')
ON CONFLICT (codigo) DO NOTHING;
