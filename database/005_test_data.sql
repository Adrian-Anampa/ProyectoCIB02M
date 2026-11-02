SET QUOTED_IDENTIFIER ON;  -- obligatorio para escribir en reclamo (índice filtrado)
GO
USE ObjetosPerdidosUNI;
GO

-- 1000 objetos ficticios, reproducibles. Requiere los usuarios de 004_seed.sql.
-- Estados: 70% PUBLICADO, 20% RECLAMADO, 10% ENTREGADO.
WITH d AS (SELECT v FROM (VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9)) t(v)),
numeros AS (SELECT a.v * 100 + b.v * 10 + c.v + 1 AS n FROM d a, d b, d c)
INSERT INTO dbo.objeto
    (titulo, descripcion, categoria, lugar, fecha_hallazgo, estado, id_publicador, fecha_publicacion)
SELECT
    CHOOSE(n % 5 + 1, N'Celular Samsung', N'Mochila negra', N'DNI', N'Llavero con llaves', N'Cuaderno')
        + N' #' + CAST(n AS NVARCHAR(4)),
    N'Objeto ficticio de prueba número ' + CAST(n AS NVARCHAR(4)),
    CHOOSE(n % 5 + 1, N'Celulares', N'Mochilas', N'Documentos', N'Llaves', N'Otros'),
    CHOOSE(n % 7 + 1, N'Biblioteca', N'Cafetería', N'Pabellón A', N'Pabellón B',
                      N'Laboratorio', N'Coliseo', N'Puerta 3'),
    DATEADD(DAY, -(n % 300), CAST('2026-10-31' AS DATE)),
    CASE WHEN n % 10 < 7 THEN 'PUBLICADO' WHEN n % 10 < 9 THEN 'RECLAMADO' ELSE 'ENTREGADO' END,
    n % 2 + 2,
    DATEADD(DAY, 1 - (n % 300), CAST('2026-10-31' AS DATETIME2(0)))
FROM numeros;
GO

-- Reclamos coherentes con el estado de cada objeto.
INSERT INTO dbo.reclamo (id_objeto, id_usuario, estado, respuesta_verificacion, fecha_reclamo, fecha_respuesta)
SELECT
    o.id_objeto,
    CASE o.id_publicador WHEN 2 THEN 3 ELSE 2 END,
    CASE o.estado WHEN 'ENTREGADO' THEN 'APROBADO' ELSE 'PENDIENTE' END,
    N'Respuesta ficticia de verificación',
    o.fecha_publicacion,
    CASE o.estado WHEN 'ENTREGADO' THEN o.fecha_publicacion END
FROM dbo.objeto o
WHERE o.estado IN ('RECLAMADO', 'ENTREGADO');
GO
