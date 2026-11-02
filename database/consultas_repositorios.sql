-- Consultas base para los repositorios Java. Las variables @ se convierten en
-- parámetros ? de PreparedStatement. rebuild.sh ejecuta este archivo con el
-- login objetos_app; los cambios se revierten al final.
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO
USE ObjetosPerdidosUNI;
GO

DECLARE @correo NVARCHAR(150) = N'admin@uni.pe',
        @id_objeto INT = 1,
        @categoria NVARCHAR(50) = N'Celulares',
        @lugar NVARCHAR(100) = NULL,
        @estado VARCHAR(20) = 'PUBLICADO',
        @desde DATE = '2026-01-01',
        @hasta DATE = NULL,
        @palabra NVARCHAR(100) = N'samsung';

-- UsuarioRepository.buscarPorCorreo
SELECT id_usuario, nombre, apellido, correo, password_hash, rol, intentos_fallidos, bloqueado, fecha_registro
FROM dbo.usuario
WHERE correo = @correo;

-- ObjetoRepository.buscarPorId
SELECT id_objeto, titulo, descripcion, categoria, lugar, fecha_hallazgo, estado, id_publicador, fecha_publicacion
FROM dbo.objeto
WHERE id_objeto = @id_objeto;

-- ObjetoRepository.buscar(FiltroObjeto): un parámetro NULL desactiva su filtro
SELECT TOP (50) id_objeto, titulo, categoria, lugar, fecha_hallazgo, estado
FROM dbo.objeto
WHERE (@categoria IS NULL OR categoria = @categoria)
  AND (@lugar     IS NULL OR lugar = @lugar)
  AND (@estado    IS NULL OR estado = @estado)
  AND (@desde     IS NULL OR fecha_hallazgo >= @desde)
  AND (@hasta     IS NULL OR fecha_hallazgo <= @hasta)
  AND (@palabra   IS NULL OR titulo LIKE N'%' + @palabra + N'%'
                          OR descripcion LIKE N'%' + @palabra + N'%')
ORDER BY fecha_hallazgo DESC, id_objeto DESC;

-- Indicadores
SELECT estado, COUNT(*) AS total FROM dbo.objeto GROUP BY estado;
SELECT categoria, COUNT(*) AS total FROM dbo.objeto GROUP BY categoria ORDER BY total DESC;
SELECT COUNT(*) AS reclamos_pendientes FROM dbo.reclamo WHERE estado = 'PENDIENTE';

BEGIN TRANSACTION;

-- UsuarioRepository.guardar
INSERT INTO dbo.usuario (nombre, apellido, correo, password_hash)
OUTPUT INSERTED.id_usuario
VALUES (N'Nuevo', N'Usuario', N'nuevo@uni.pe', REPLICATE('x', 60));

-- ObjetoRepository.guardar
INSERT INTO dbo.objeto (titulo, descripcion, categoria, lugar, fecha_hallazgo, id_publicador)
OUTPUT INSERTED.id_objeto
VALUES (N'Calculadora', N'Casio gris', N'Otros', N'Biblioteca', '2026-11-01', 2);

ROLLBACK TRANSACTION;
GO
