SET QUOTED_IDENTIFIER ON;  -- obligatorio para índices filtrados
GO
USE ObjetosPerdidosUNI;
GO

-- Filtros del catálogo
CREATE INDEX ix_objeto_categoria      ON dbo.objeto (categoria);
CREATE INDEX ix_objeto_lugar          ON dbo.objeto (lugar);
CREATE INDEX ix_objeto_estado         ON dbo.objeto (estado);
CREATE INDEX ix_objeto_fecha_hallazgo ON dbo.objeto (fecha_hallazgo);

-- Consultas por FK más frecuentes
CREATE INDEX ix_foto_objeto     ON dbo.foto (id_objeto);
CREATE INDEX ix_reclamo_objeto  ON dbo.reclamo (id_objeto);
CREATE INDEX ix_reclamo_usuario ON dbo.reclamo (id_usuario);
CREATE INDEX ix_aviso_usuario   ON dbo.aviso (id_usuario);

-- Red de seguridad: un objeto admite un solo reclamo aprobado.
CREATE UNIQUE INDEX ux_reclamo_un_aprobado
    ON dbo.reclamo (id_objeto)
    WHERE estado = 'APROBADO';
GO
