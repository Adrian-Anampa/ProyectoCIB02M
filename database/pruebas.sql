-- Pruebas de la BD. Todo corre en una transacción que se revierte al final,
-- así que no deja datos. Falla (código de salida 1) si alguna prueba falla.
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO
USE ObjetosPerdidosUNI;
GO

DECLARE @r TABLE (id INT IDENTITY, prueba NVARCHAR(70), resultado VARCHAR(40));
DECLARE @hash VARCHAR(60) = REPLICATE('x', 60);
DECLARE @id INT, @reclamo INT, @entregado INT, @n INT, @t0 DATETIME2(3);

BEGIN TRANSACTION;

-- ===== Datos esperados =====
SELECT @n = COUNT(*) FROM dbo.objeto;
INSERT @r VALUES (N'Hay 1000 objetos', IIF(@n = 1000, 'OK', 'FALLO'));

SELECT @n = COUNT(*) FROM dbo.usuario WHERE rol = 'ADMIN';
INSERT @r VALUES (N'Hay un administrador', IIF(@n = 1, 'OK', 'FALLO'));

SELECT @n = COUNT(*) FROM dbo.objeto o JOIN dbo.reclamo r ON r.id_objeto = o.id_objeto
WHERE (o.estado = 'RECLAMADO' AND r.estado <> 'PENDIENTE')
   OR (o.estado = 'ENTREGADO' AND r.estado <> 'APROBADO')
   OR o.estado = 'PUBLICADO' OR r.id_usuario = o.id_publicador;
INSERT @r VALUES (N'Reclamos coherentes con objetos', IIF(@n = 0, 'OK', 'FALLO'));

-- ===== Restricciones: cada inserción debe ser rechazada =====
BEGIN TRY
    INSERT dbo.usuario (nombre, apellido, correo, password_hash) VALUES (N'X', N'X', N'admin@uni.pe', @hash);
    INSERT @r VALUES (N'Rechaza correo duplicado', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza correo duplicado', IIF(ERROR_NUMBER() = 2627, 'OK', 'FALLO')); END CATCH

BEGIN TRY
    INSERT dbo.usuario (nombre, apellido, correo, password_hash) VALUES (N'X', N'X', N'ADMIN@UNI.PE', @hash);
    INSERT @r VALUES (N'Rechaza correo duplicado en mayúsculas', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza correo duplicado en mayúsculas', IIF(ERROR_NUMBER() = 2627, 'OK', 'FALLO')); END CATCH

BEGIN TRY
    INSERT dbo.usuario (nombre, apellido, correo, password_hash, rol) VALUES (N'X', N'X', N'x@uni.pe', @hash, 'SUPER');
    INSERT @r VALUES (N'Rechaza rol inválido', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza rol inválido', IIF(ERROR_NUMBER() = 547, 'OK', 'FALLO')); END CATCH

BEGIN TRY
    INSERT dbo.objeto (titulo, categoria, lugar, fecha_hallazgo, estado, id_publicador) VALUES (N'X', N'X', N'X', '2026-01-01', 'PERDIDO', 2);
    INSERT @r VALUES (N'Rechaza estado de objeto inválido', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza estado de objeto inválido', IIF(ERROR_NUMBER() = 547, 'OK', 'FALLO')); END CATCH

BEGIN TRY
    INSERT dbo.objeto (titulo, categoria, lugar, fecha_hallazgo, id_publicador) VALUES (N'X', N'X', N'X', '2026-01-01', 999999);
    INSERT @r VALUES (N'Rechaza publicador inexistente', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza publicador inexistente', IIF(ERROR_NUMBER() = 547, 'OK', 'FALLO')); END CATCH

BEGIN TRY
    INSERT dbo.foto (id_objeto, ruta_archivo) VALUES (999999, N'data/images/x.jpg');
    INSERT @r VALUES (N'Rechaza foto de objeto inexistente', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza foto de objeto inexistente', IIF(ERROR_NUMBER() = 547, 'OK', 'FALLO')); END CATCH

BEGIN TRY
    INSERT dbo.reclamo (id_objeto, id_usuario, estado) VALUES (1, 2, 'EN_REVISION');
    INSERT @r VALUES (N'Rechaza estado de reclamo inválido', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza estado de reclamo inválido', IIF(ERROR_NUMBER() = 547, 'OK', 'FALLO')); END CATCH

SELECT TOP 1 @entregado = id_objeto FROM dbo.reclamo WHERE estado = 'APROBADO' ORDER BY id_objeto;
BEGIN TRY
    INSERT dbo.reclamo (id_objeto, id_usuario, estado) VALUES (@entregado, 1, 'APROBADO');
    INSERT @r VALUES (N'Rechaza segundo reclamo aprobado', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza segundo reclamo aprobado', IIF(ERROR_NUMBER() = 2601, 'OK', 'FALLO')); END CATCH

-- ===== Flujo completo: debe funcionar =====
BEGIN TRY
    INSERT dbo.objeto (titulo, categoria, lugar, fecha_hallazgo, id_publicador)
    VALUES (N'Mochila Adidas', N'Mochilas', N'Biblioteca', '2026-10-30', 2);
    SET @id = SCOPE_IDENTITY();
    INSERT dbo.foto (id_objeto, ruta_archivo) VALUES (@id, N'data/images/prueba.jpg');
    INSERT dbo.custodia (id_objeto, pregunta_verificacion, caracteristica_privada)
    VALUES (@id, N'¿Qué tiene en el bolsillo delantero?', N'Un llavero rojo');
    INSERT dbo.codigo_qr (id_objeto, codigo) VALUES (@id, 'OBJ-PRUEBA');
    INSERT dbo.reclamo (id_objeto, id_usuario, respuesta_verificacion) VALUES (@id, 3, N'Un llavero rojo');
    SET @reclamo = SCOPE_IDENTITY();
    UPDATE dbo.reclamo SET estado = 'APROBADO', fecha_respuesta = SYSDATETIME() WHERE id_reclamo = @reclamo;
    UPDATE dbo.objeto SET estado = 'ENTREGADO' WHERE id_objeto = @id;
    INSERT dbo.aviso (id_usuario, id_reclamo, mensaje) VALUES (3, @reclamo, N'Tu reclamo fue aprobado.');
    INSERT dbo.bitacora (id_usuario, accion, id_objeto) VALUES (1, 'RECLAMO_APROBADO', @id);
    INSERT @r VALUES (N'Flujo publicar, reclamar, aprobar y avisar', 'OK');
END TRY BEGIN CATCH INSERT @r VALUES (N'Flujo publicar, reclamar, aprobar y avisar', 'FALLO'); END CATCH

BEGIN TRY
    INSERT dbo.custodia (id_objeto, pregunta_verificacion, caracteristica_privada) VALUES (@id, N'X', N'X');
    INSERT @r VALUES (N'Rechaza segunda custodia del objeto', 'FALLO');
END TRY BEGIN CATCH INSERT @r VALUES (N'Rechaza segunda custodia del objeto', IIF(ERROR_NUMBER() = 2627, 'OK', 'FALLO')); END CATCH

-- ===== Búsquedas =====
SELECT @n = COUNT(*) FROM dbo.objeto WHERE lugar = N'cafeteria' AND titulo LIKE N'%celular%';
INSERT @r VALUES (N'Búsqueda ignora mayúsculas y tildes', IIF(@n > 0, 'OK', 'FALLO'));

SET @t0 = SYSDATETIME();
SELECT @n = COUNT(*) FROM dbo.objeto
WHERE categoria = N'Celulares' AND lugar = N'Biblioteca' AND estado = 'PUBLICADO'
  AND fecha_hallazgo >= '2026-01-01' AND titulo LIKE N'%Samsung%';
INSERT @r VALUES (N'Filtro combinado: filas / ms',
                  CONCAT(@n, ' filas / ', DATEDIFF(MILLISECOND, @t0, SYSDATETIME()), ' ms'));

ROLLBACK TRANSACTION;

SELECT prueba, resultado FROM @r ORDER BY id;

IF EXISTS (SELECT 1 FROM @r WHERE resultado = 'FALLO')
    THROW 50000, N'Hay pruebas fallidas', 1;
GO
