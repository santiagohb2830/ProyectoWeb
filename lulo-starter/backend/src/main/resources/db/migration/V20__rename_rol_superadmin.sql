-- Renombra el rol propietario interno de Lulo a un nombre más reconocible.
UPDATE rol_pool
   SET nombre = 'SuperAdmin',
       descripcion = 'Acceso total a la administración de Lulo'
 WHERE nombre = 'Operador Lulo';
