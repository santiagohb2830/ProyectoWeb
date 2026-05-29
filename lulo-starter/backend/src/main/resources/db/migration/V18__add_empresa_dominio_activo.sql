-- Dominio email asociado a la empresa (ej. "acme.com"). Los usuarios de
-- esta empresa deben tener correos terminados en "@<dominio>". El SUPERADMIN
-- usa "lulo.app".
ALTER TABLE empresa
    ADD COLUMN dominio VARCHAR(255);

-- Empresa activa o soft-deleted. activo=false oculta la empresa de listados
-- pero conserva los datos para trazabilidad.
ALTER TABLE empresa
    ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;

-- Seed: empresa interna Lulo usa lulo.app
UPDATE empresa SET dominio = 'lulo.app' WHERE nit = 'LULO-APP';

-- Para las empresas demo previamente cargadas, dominio se deriva del email
-- de contacto si existe; null si no.
UPDATE empresa
   SET dominio = split_part(email_contacto, '@', 2)
 WHERE dominio IS NULL AND email_contacto IS NOT NULL AND position('@' in email_contacto) > 0;

CREATE INDEX idx_empresa_activo ON empresa(activo);

-- Permisos granulares para gestión de empresas (los usa SUPERADMIN y
-- cualquier usuario con permisos asignados explícitamente).
INSERT INTO permiso (codigo, descripcion) VALUES
    ('EMPRESA_VER',      'Ver listado y detalle de empresas'),
    ('EMPRESA_CREAR',    'Crear nuevas empresas en la app'),
    ('EMPRESA_EDITAR',   'Editar nombre/dominio/admin de empresas'),
    ('EMPRESA_ELIMINAR', 'Desactivar (soft delete) empresas')
ON CONFLICT (codigo) DO NOTHING;
