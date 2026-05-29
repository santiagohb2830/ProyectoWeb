-- V20__seed_demo_data.sql
-- Snapshot de datos demo (empresas, pools, usuarios, roles, permisos, asignaciones)
-- Generado 2026-05-25 desde DB remota grupo12.
-- Idempotente: todas las filas usan ON CONFLICT DO NOTHING.
-- Permite que un dev clone el repo y arranque con datos demo (Lulo + Postobon)
-- sin tener que crearlos manualmente desde la UI.

--
--




--
-- Data for Name: empresa; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.empresa VALUES ('00000000-0000-0000-0000-000000000001', 'Lulo (App Owner)', 'LULO-APP', 'admin@lulo.app', '2026-05-24 20:10:26.625285', 'lulo.app', true) ON CONFLICT DO NOTHING;
INSERT INTO public.empresa VALUES ('acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'Postobon', '122323213-2', 'admin@postobon.com', '2026-05-25 19:12:34.576518', 'postobon.com', true) ON CONFLICT DO NOTHING;


--
-- Data for Name: pool; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.pool VALUES ('baad088a-a58d-4883-8d76-e548a8bb79fa', '00000000-0000-0000-0000-000000000001', 'Lulo Console', '{}', '2026-05-24 21:26:58.929219') ON CONFLICT DO NOTHING;
INSERT INTO public.pool VALUES ('d2d70f91-d314-4cf3-8a7e-1242b631505f', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'Principal', NULL, '2026-05-25 19:12:34.797637') ON CONFLICT DO NOTHING;


--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.usuario VALUES ('fe5f437d-ceb8-4630-ada1-be5644ea470e', '00000000-0000-0000-0000-000000000001', 'admin@lulo.app', '$2a$12$208LTKok5r7StELp0emQsOj7HSDghboWXUggHd1w4ttvtThyeilDy', 'activo', '2026-05-24 20:10:30.027873', 'SUPERADMIN') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('55403dff-ff92-4b67-9fd5-4bc5441d897d', '00000000-0000-0000-0000-000000000001', 'admin2@lulo.app', '$2a$12$h9alMu3fGOIPa6N9gwZCOOo/LS5tT0n79xonQP/v9yEQCS3ckeqZ.', 'activo', '2026-05-24 23:00:27.136222', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('0673722e-e659-4627-ae92-a4961ef00cf9', '00000000-0000-0000-0000-000000000001', 'admin3@lulo.app', '$2a$12$d6WIpdHn6zyuE7wNIZNPx.8F2DipeCr.BlAxhUyC9BWeKcoXo37NS', 'activo', '2026-05-25 03:39:56.279327', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('f237cb3a-f94f-4a8c-9d75-7f2651f863bd', '00000000-0000-0000-0000-000000000001', 'admin4@lulo.app', '$2a$12$0HY/2lwDzffWIObIJ51TBOKBDnYy15uAMwqQFcezyKMytamYuv5GW', 'activo', '2026-05-25 03:40:25.432729', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('59f8d1f2-4255-4e3a-9b74-5dd9bfcc0948', '00000000-0000-0000-0000-000000000001', 'admin5@lulo.app', '$2a$12$P.gRISYmJJA0b5IaF6L1K.c0JJ5kUmOHihKgL6e2ipI3WsMCJSegS', 'activo', '2026-05-25 03:41:17.047851', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('51a725e4-6402-476e-8c8a-3fe11faf3b08', '00000000-0000-0000-0000-000000000001', 'admin6@lulo.app', '$2a$12$c11DIIaub3F1tG9A.EBsqOY8ibEKXPzy99AKuKmq/15HonY/PdP8e', 'activo', '2026-05-25 03:41:43.659264', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('cab1d496-c4e0-4b62-b937-bb8367106c4a', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin@postobon.com', '$2a$12$TV4r7jfHRIfe9KlzNoPj2.Fw.jbGD3Ri749cEjYbndCqIbdvY6A.C', 'activo', '2026-05-25 19:12:34.797176', 'ADMIN_EMPRESA') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('7ab1072e-c522-42cd-b44a-85edce2885ed', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin2@postobon.com', '$2a$12$Y7OgV5hBEuuuHSBegrynreWT3jPl2hXA96dsKLtL7TEzhFxToPFQu', 'activo', '2026-05-25 21:47:04.746541', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('b0a9051a-118c-4c8d-8c0f-e8828c7caa76', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin3@postobon.com', '$2a$12$bujJhuOsYiWuzhpdAtjJP.Tlh4RrNaW6Gk1lDeKoZTqRfE7BsfoL6', 'activo', '2026-05-25 22:05:26.265722', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('f9511006-d15e-4f2c-8469-af1e4dbb9c78', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin4@postobon.com', '$2a$12$W6PLsJB72yOvkJwRDDdO.us.STXZ7uYrg7dSR7ceJgoBuVOuJPjg6', 'activo', '2026-05-25 22:05:41.726978', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('c1f050d0-c0c7-4ef2-9d60-15f54f70a437', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin5@postobon.com', '$2a$12$5Q4Bf1zbNaIRuoi7c8iELuTJFAq6YBo..aJYxQ7ncIKk0.YeOhSAm', 'activo', '2026-05-25 22:05:57.11572', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('b635dc43-9ad9-4138-8bf3-853f656d8918', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin6@postobon.com', '$2a$12$OjlVU6wYFKQMp7xLE7SewubdSDt4nK.3SCqo8lJFfxGvSx389aPQa', 'activo', '2026-05-25 22:06:11.836447', 'USUARIO') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario VALUES ('0b1338c4-50ea-4cda-a150-c16bf3e51b8d', 'acf7f3d6-597c-414a-90e7-0a3ff774de0c', 'admin7@postobon.com', '$2a$12$R.pdJF3Pn1BHbHFARBmxJOrDhPc/wYpevJiN.sHcBwb8OiK1qjyl2', 'activo', '2026-05-25 22:06:26.404626', 'USUARIO') ON CONFLICT DO NOTHING;


--
-- Data for Name: proceso; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: rol_proceso; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: lane; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: nodo; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: actividad; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: arco; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: audits; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: caso; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: caso_actividad; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: caso_log; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: mensaje_proceso; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: suscripcion_mensaje; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: entrega_mensaje; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: gateway; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: rol_pool; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.rol_pool VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', 'baad088a-a58d-4883-8d76-e548a8bb79fa', 'Gestor de Empresas', 'Crea, edita, suspende y reactiva empresas clientes', true, false, '2026-05-24 21:31:59.98583') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('6143c2ff-aeb6-4fc6-987d-dfeb672edb64', 'baad088a-a58d-4883-8d76-e548a8bb79fa', 'Gestor de Usuarios Lulo', 'Crea y edita usuarios internos de Lulo y sus roles', true, false, '2026-05-24 21:32:00.173757') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('f57214f4-9dae-473d-bb10-128d5e9e7d38', 'baad088a-a58d-4883-8d76-e548a8bb79fa', 'Auditor', 'Lectura global: audit log y métricas de toda la app', true, false, '2026-05-24 21:32:00.314163') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('6f4e5b9b-af68-4026-90f0-bf691af7dd59', 'baad088a-a58d-4883-8d76-e548a8bb79fa', 'Soporte', 'Lectura de procesos cliente para soporte técnico', true, false, '2026-05-24 21:32:00.474934') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', 'baad088a-a58d-4883-8d76-e548a8bb79fa', 'SuperAdmin', 'Acceso total a la administración de Lulo', true, true, '2026-05-24 21:26:59.056219') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('63138c3e-7d4e-4de6-bf46-f8bfb574a4b1', 'baad088a-a58d-4883-8d76-e548a8bb79fa', 'Consultor de Empresas', NULL, true, false, '2026-05-24 22:09:23.366619') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Administrador', 'Rol con acceso completo al pool', true, true, '2026-05-25 19:12:34.885439') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('9a0a6a2c-7a5a-43f8-ab79-5a7d9523a236', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Auditor', NULL, true, false, '2026-05-25 19:18:17.265531') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Gestor de Procesos', NULL, true, false, '2026-05-25 21:46:07.539563') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('59543c11-e0b8-4652-be47-206eee10f49d', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Diagramador', NULL, true, false, '2026-05-25 21:59:35.268954') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Editor de Procesos', NULL, true, false, '2026-05-25 22:00:35.632091') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('0a2774c7-ca6e-408e-bdc4-c28ffb2e3027', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Gestor de Empresa', NULL, true, false, '2026-05-25 22:00:59.752745') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool VALUES ('5e1a79d4-c671-48d6-848f-894695af14da', 'd2d70f91-d314-4cf3-8a7e-1242b631505f', 'Administrador de Pools', NULL, true, false, '2026-05-25 22:01:30.271472') ON CONFLICT DO NOTHING;


--
-- Data for Name: invitacion_usuario; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: notificacion_externa; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: permiso; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.permiso VALUES ('3b4a512b-91ee-48b3-9ccb-a984a39e74d9', 'PROCESO_VER', 'Ver procesos del pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('978e4476-c7ad-4fae-8b66-b500c1de27b2', 'PROCESO_CREAR', 'Crear nuevos procesos') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('cf0dabda-9c15-4e3f-b228-eee58e866c0d', 'PROCESO_EDITAR', 'Editar procesos existentes') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('d738bda5-33a7-4e53-a496-8042f4434afc', 'PROCESO_ELIMINAR', 'Eliminar procesos (soft delete)') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('83eea5a9-887c-4b16-922e-fd4bf0ed62f0', 'PROCESO_PUBLICAR', 'Publicar procesos en estado borrador') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('cce2ba85-4365-41f4-892e-e592af7a40d3', 'PROCESO_COMPARTIR', 'Compartir procesos con otros pools') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('d630be69-4d78-4e4c-9e67-03972c8378b8', 'DIAGRAMA_VER', 'Ver diagrama del proceso') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('15ef6492-f236-4b79-87c8-fb71c2a68f17', 'DIAGRAMA_EDITAR', 'Editar nodos, arcos y lanes del diagrama') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('ad656ca7-b813-4918-bdd4-4cbcec9f04af', 'ROL_VER', 'Ver roles del pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('062696fe-380a-40fe-ad35-28f6cabdc415', 'ROL_CREAR', 'Crear roles en el pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('314eab0b-20f0-4e96-a4d8-231bd528bde5', 'ROL_EDITAR', 'Editar roles del pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('fca6acb8-8b7a-41f5-96a6-066e1b3752a2', 'ROL_ELIMINAR', 'Eliminar roles del pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('62e3c6f3-3b0d-4c90-82cd-8696dad3a5d7', 'USUARIO_VER', 'Ver usuarios del pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('a06485d6-4347-45e5-9f0b-f574097cdbb7', 'USUARIO_INVITAR', 'Invitar usuarios al pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('77f11dd7-4084-495e-9e5e-32baa4d74f3c', 'USUARIO_REVOCAR', 'Revocar acceso de usuarios') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('0d2fc783-20b8-4a0c-8545-5283bad5d41b', 'POOL_ADMINISTRAR', 'Administrar configuración del pool') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('91bda8b2-4ff5-46f9-83b3-2cfa92ab579a', 'AUDIT_VER', 'Ver log de auditoría') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('6b63547c-66ee-4073-9c71-28c0bcdcff6a', 'EMPRESA_VER', 'Ver listado y detalle de empresas') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('768c1020-67cc-40b3-8024-27a194878708', 'EMPRESA_CREAR', 'Crear nuevas empresas en la app') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('6c8dbf7e-5d97-435f-be09-fcd039bbf276', 'EMPRESA_EDITAR', 'Editar nombre/dominio/admin de empresas') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('383d8db6-18c0-4b5c-8528-5c184982ca22', 'EMPRESA_ELIMINAR', 'Desactivar (soft delete) empresas') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('c1b3f9b4-8fe2-4669-9828-5948bb56285b', 'EMPRESA_SUSPENDER', 'Suspender temporalmente una empresa cliente') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('7fac858e-e5c1-4773-8172-9b2c96263960', 'EMPRESA_REACTIVAR', 'Reactivar una empresa suspendida') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('4cc21337-025c-4364-9b8d-04e200bfe912', 'LULO_USUARIO_VER', 'Ver usuarios internos de Lulo') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('2315908e-2ba9-4ab0-b491-1863a835f0e7', 'LULO_USUARIO_CREAR', 'Crear usuarios internos de Lulo') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('c5d5a12d-6e0d-428d-8f27-5af3409cd1de', 'LULO_USUARIO_EDITAR', 'Editar usuarios internos de Lulo') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('b374cc2d-fcb2-4aee-9cf1-625d62723737', 'LULO_USUARIO_ELIMINAR', 'Eliminar usuarios internos de Lulo') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('d83ec5ff-9854-4170-bbf2-20164a0bfc5a', 'LULO_ROL_GESTIONAR', 'Crear, editar y asignar roles internos de Lulo') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('2fa3dea1-6715-4930-b16f-db18e66d2f63', 'METRICAS_VER', 'Ver métricas globales de la aplicación') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('61f9eccb-ec66-4f0d-9046-2ce228e5ca20', 'AUDIT_GLOBAL_VER', 'Ver audit log de todas las empresas') ON CONFLICT DO NOTHING;
INSERT INTO public.permiso VALUES ('42ae9806-0087-41b2-b6d1-f98d4cdecaf3', 'SOPORTE_PROCESOS_VER', 'Ver procesos de cualquier empresa (solo lectura)') ON CONFLICT DO NOTHING;


--
-- Data for Name: proceso_compartido; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: rol_pool_permiso; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '3b4a512b-91ee-48b3-9ccb-a984a39e74d9') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '4cc21337-025c-4364-9b8d-04e200bfe912') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '42ae9806-0087-41b2-b6d1-f98d4cdecaf3') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '768c1020-67cc-40b3-8024-27a194878708') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'd630be69-4d78-4e4c-9e67-03972c8378b8') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '314eab0b-20f0-4e96-a4d8-231bd528bde5') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '77f11dd7-4084-495e-9e5e-32baa4d74f3c') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '83eea5a9-887c-4b16-922e-fd4bf0ed62f0') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'cf0dabda-9c15-4e3f-b228-eee58e866c0d') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '62e3c6f3-3b0d-4c90-82cd-8696dad3a5d7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'fca6acb8-8b7a-41f5-96a6-066e1b3752a2') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '2fa3dea1-6715-4930-b16f-db18e66d2f63') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'd83ec5ff-9854-4170-bbf2-20164a0bfc5a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '15ef6492-f236-4b79-87c8-fb71c2a68f17') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'd738bda5-33a7-4e53-a496-8042f4434afc') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '7fac858e-e5c1-4773-8172-9b2c96263960') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '383d8db6-18c0-4b5c-8528-5c184982ca22') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '978e4476-c7ad-4fae-8b66-b500c1de27b2') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'cce2ba85-4365-41f4-892e-e592af7a40d3') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '91bda8b2-4ff5-46f9-83b3-2cfa92ab579a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '2315908e-2ba9-4ab0-b491-1863a835f0e7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'ad656ca7-b813-4918-bdd4-4cbcec9f04af') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '6c8dbf7e-5d97-435f-be09-fcd039bbf276') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '062696fe-380a-40fe-ad35-28f6cabdc415') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'b374cc2d-fcb2-4aee-9cf1-625d62723737') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'c1b3f9b4-8fe2-4669-9828-5948bb56285b') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '6b63547c-66ee-4073-9c71-28c0bcdcff6a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '61f9eccb-ec66-4f0d-9046-2ce228e5ca20') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'a06485d6-4347-45e5-9f0b-f574097cdbb7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', 'c5d5a12d-6e0d-428d-8f27-5af3409cd1de') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('258b209a-c302-48ac-b700-a48102adb2a5', '0d2fc783-20b8-4a0c-8545-5283bad5d41b') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', 'd738bda5-33a7-4e53-a496-8042f4434afc') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', 'cf0dabda-9c15-4e3f-b228-eee58e866c0d') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', '978e4476-c7ad-4fae-8b66-b500c1de27b2') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', 'cce2ba85-4365-41f4-892e-e592af7a40d3') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', '83eea5a9-887c-4b16-922e-fd4bf0ed62f0') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('28bf2b13-e5b4-417d-b184-429d72f89b68', '3b4a512b-91ee-48b3-9ccb-a984a39e74d9') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('0a2774c7-ca6e-408e-bdc4-c28ffb2e3027', '77f11dd7-4084-495e-9e5e-32baa4d74f3c') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('0a2774c7-ca6e-408e-bdc4-c28ffb2e3027', 'a06485d6-4347-45e5-9f0b-f574097cdbb7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('0a2774c7-ca6e-408e-bdc4-c28ffb2e3027', '62e3c6f3-3b0d-4c90-82cd-8696dad3a5d7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5e1a79d4-c671-48d6-848f-894695af14da', '0d2fc783-20b8-4a0c-8545-5283bad5d41b') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('59543c11-e0b8-4652-be47-206eee10f49d', '978e4476-c7ad-4fae-8b66-b500c1de27b2') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('59543c11-e0b8-4652-be47-206eee10f49d', 'cf0dabda-9c15-4e3f-b228-eee58e866c0d') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('9a0a6a2c-7a5a-43f8-ab79-5a7d9523a236', '91bda8b2-4ff5-46f9-83b3-2cfa92ab579a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('59543c11-e0b8-4652-be47-206eee10f49d', '15ef6492-f236-4b79-87c8-fb71c2a68f17') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('59543c11-e0b8-4652-be47-206eee10f49d', 'd630be69-4d78-4e4c-9e67-03972c8378b8') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('59543c11-e0b8-4652-be47-206eee10f49d', '3b4a512b-91ee-48b3-9ccb-a984a39e74d9') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', '3b4a512b-91ee-48b3-9ccb-a984a39e74d9') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', '978e4476-c7ad-4fae-8b66-b500c1de27b2') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', 'cf0dabda-9c15-4e3f-b228-eee58e866c0d') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', '83eea5a9-887c-4b16-922e-fd4bf0ed62f0') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', 'cce2ba85-4365-41f4-892e-e592af7a40d3') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', 'd630be69-4d78-4e4c-9e67-03972c8378b8') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('5d64b2e4-4e03-40b6-97b6-86fd501b3033', '15ef6492-f236-4b79-87c8-fb71c2a68f17') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', 'c1b3f9b4-8fe2-4669-9828-5948bb56285b') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', '383d8db6-18c0-4b5c-8528-5c184982ca22') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', '6c8dbf7e-5d97-435f-be09-fcd039bbf276') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', '7fac858e-e5c1-4773-8172-9b2c96263960') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', '6b63547c-66ee-4073-9c71-28c0bcdcff6a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('e5974ba3-ed38-4483-b845-9abd42d41fdf', '768c1020-67cc-40b3-8024-27a194878708') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6143c2ff-aeb6-4fc6-987d-dfeb672edb64', '4cc21337-025c-4364-9b8d-04e200bfe912') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6143c2ff-aeb6-4fc6-987d-dfeb672edb64', 'd83ec5ff-9854-4170-bbf2-20164a0bfc5a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6143c2ff-aeb6-4fc6-987d-dfeb672edb64', '2315908e-2ba9-4ab0-b491-1863a835f0e7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6143c2ff-aeb6-4fc6-987d-dfeb672edb64', 'b374cc2d-fcb2-4aee-9cf1-625d62723737') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6143c2ff-aeb6-4fc6-987d-dfeb672edb64', 'c5d5a12d-6e0d-428d-8f27-5af3409cd1de') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('f57214f4-9dae-473d-bb10-128d5e9e7d38', '61f9eccb-ec66-4f0d-9046-2ce228e5ca20') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('f57214f4-9dae-473d-bb10-128d5e9e7d38', '91bda8b2-4ff5-46f9-83b3-2cfa92ab579a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('f57214f4-9dae-473d-bb10-128d5e9e7d38', '4cc21337-025c-4364-9b8d-04e200bfe912') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('f57214f4-9dae-473d-bb10-128d5e9e7d38', '2fa3dea1-6715-4930-b16f-db18e66d2f63') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('f57214f4-9dae-473d-bb10-128d5e9e7d38', '6b63547c-66ee-4073-9c71-28c0bcdcff6a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6f4e5b9b-af68-4026-90f0-bf691af7dd59', '91bda8b2-4ff5-46f9-83b3-2cfa92ab579a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6f4e5b9b-af68-4026-90f0-bf691af7dd59', '3b4a512b-91ee-48b3-9ccb-a984a39e74d9') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6f4e5b9b-af68-4026-90f0-bf691af7dd59', '42ae9806-0087-41b2-b6d1-f98d4cdecaf3') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('6f4e5b9b-af68-4026-90f0-bf691af7dd59', '6b63547c-66ee-4073-9c71-28c0bcdcff6a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '42ae9806-0087-41b2-b6d1-f98d4cdecaf3') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', 'c1b3f9b4-8fe2-4669-9828-5948bb56285b') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '4cc21337-025c-4364-9b8d-04e200bfe912') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', 'd83ec5ff-9854-4170-bbf2-20164a0bfc5a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '6c8dbf7e-5d97-435f-be09-fcd039bbf276') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '91bda8b2-4ff5-46f9-83b3-2cfa92ab579a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '768c1020-67cc-40b3-8024-27a194878708') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '2315908e-2ba9-4ab0-b491-1863a835f0e7') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '2fa3dea1-6715-4930-b16f-db18e66d2f63') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', 'b374cc2d-fcb2-4aee-9cf1-625d62723737') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '6b63547c-66ee-4073-9c71-28c0bcdcff6a') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '383d8db6-18c0-4b5c-8528-5c184982ca22') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '61f9eccb-ec66-4f0d-9046-2ce228e5ca20') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', '7fac858e-e5c1-4773-8172-9b2c96263960') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('c027aa37-e096-4d17-aa38-2f0cababe30c', 'c5d5a12d-6e0d-428d-8f27-5af3409cd1de') ON CONFLICT DO NOTHING;
INSERT INTO public.rol_pool_permiso VALUES ('63138c3e-7d4e-4de6-bf46-f8bfb574a4b1', '6b63547c-66ee-4073-9c71-28c0bcdcff6a') ON CONFLICT DO NOTHING;


--
-- Data for Name: usuario_rol_pool; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.usuario_rol_pool VALUES ('fe5f437d-ceb8-4630-ada1-be5644ea470e', 'c027aa37-e096-4d17-aa38-2f0cababe30c', '2026-05-24 21:26:59.951572') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('55403dff-ff92-4b67-9fd5-4bc5441d897d', '6143c2ff-aeb6-4fc6-987d-dfeb672edb64', '2026-05-24 23:00:27.164046') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('0673722e-e659-4627-ae92-a4961ef00cf9', '63138c3e-7d4e-4de6-bf46-f8bfb574a4b1', '2026-05-25 03:39:56.363351') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('f237cb3a-f94f-4a8c-9d75-7f2651f863bd', 'f57214f4-9dae-473d-bb10-128d5e9e7d38', '2026-05-25 03:40:25.468636') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('59f8d1f2-4255-4e3a-9b74-5dd9bfcc0948', 'e5974ba3-ed38-4483-b845-9abd42d41fdf', '2026-05-25 03:41:17.089881') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('51a725e4-6402-476e-8c8a-3fe11faf3b08', '6f4e5b9b-af68-4026-90f0-bf691af7dd59', '2026-05-25 03:41:43.889425') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('cab1d496-c4e0-4b62-b937-bb8367106c4a', '258b209a-c302-48ac-b700-a48102adb2a5', '2026-05-25 19:12:34.922162') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('7ab1072e-c522-42cd-b44a-85edce2885ed', '28bf2b13-e5b4-417d-b184-429d72f89b68', '2026-05-25 21:47:04.797511') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('b0a9051a-118c-4c8d-8c0f-e8828c7caa76', '5e1a79d4-c671-48d6-848f-894695af14da', '2026-05-25 22:05:26.301211') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('f9511006-d15e-4f2c-8469-af1e4dbb9c78', '9a0a6a2c-7a5a-43f8-ab79-5a7d9523a236', '2026-05-25 22:05:41.763333') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('c1f050d0-c0c7-4ef2-9d60-15f54f70a437', '59543c11-e0b8-4652-be47-206eee10f49d', '2026-05-25 22:05:57.155538') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('b635dc43-9ad9-4138-8bf3-853f656d8918', '5d64b2e4-4e03-40b6-97b6-86fd501b3033', '2026-05-25 22:06:11.863522') ON CONFLICT DO NOTHING;
INSERT INTO public.usuario_rol_pool VALUES ('0b1338c4-50ea-4cda-a150-c16bf3e51b8d', '0a2774c7-ca6e-408e-bdc4-c28ffb2e3027', '2026-05-25 22:06:26.430839') ON CONFLICT DO NOTHING;


--
--


