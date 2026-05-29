-- Tipo de usuario a nivel global de la app.
--   SUPERADMIN    -> dueño/operador de la aplicación (Lulo). Ve todas las empresas.
--   ADMIN_EMPRESA -> administrador de una empresa cliente. Crea usuarios y roles de su empresa.
--   USUARIO       -> usuario regular. Acceso según roles de pool.
ALTER TABLE usuario
    ADD COLUMN tipo_usuario VARCHAR(20) NOT NULL DEFAULT 'USUARIO';

ALTER TABLE usuario
    ADD CONSTRAINT chk_usuario_tipo
    CHECK (tipo_usuario IN ('SUPERADMIN', 'ADMIN_EMPRESA', 'USUARIO'));

CREATE INDEX idx_usuario_tipo ON usuario(tipo_usuario);

-- Empresa "Lulo (App Owner)" cuyo único propósito es albergar al SUPERADMIN.
-- El usuario superadmin se crea en código (DataInitializer) para no hardcodear bcrypt.
INSERT INTO empresa (id, nombre, nit, email_contacto)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Lulo (App Owner)',
    'LULO-APP',
    'admin@lulo.app'
)
ON CONFLICT (nit) DO NOTHING;
