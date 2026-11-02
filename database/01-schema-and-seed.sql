/*
  SQL Server 2019+ - Sistema de Objetos Perdidos y Encontrados
  Ejecutar con una cuenta que pueda crear bases de datos.
  El script es repetible: crea los objetos que todavía no existen.
*/

IF DB_ID(N'ObjetosPerdidosDB') IS NULL
    CREATE DATABASE ObjetosPerdidosDB;
GO

USE ObjetosPerdidosDB;
GO

IF OBJECT_ID(N'dbo.usuarios', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuarios (
        id_usuario          BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre_completo     NVARCHAR(120) NOT NULL,
        correo              NVARCHAR(160) NOT NULL UNIQUE,
        codigo_estudiante   NVARCHAR(30) NOT NULL UNIQUE,
        password_hash       VARBINARY(32) NOT NULL,
        rol                 VARCHAR(20) NOT NULL CONSTRAINT CK_usuarios_rol CHECK (rol IN ('ESTUDIANTE','ADMINISTRADOR')),
        activo              BIT NOT NULL CONSTRAINT DF_usuarios_activo DEFAULT 1,
        fecha_registro      DATETIME2 NOT NULL CONSTRAINT DF_usuarios_fecha DEFAULT SYSDATETIME()
    );
END;
GO

IF OBJECT_ID(N'dbo.objetos_encontrados', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.objetos_encontrados (
        id_objeto               BIGINT IDENTITY(1,1) PRIMARY KEY,
        titulo                  NVARCHAR(120) NOT NULL,
        descripcion             NVARCHAR(1000) NOT NULL,
        categoria               NVARCHAR(60) NOT NULL,
        lugar_hallazgo          NVARCHAR(160) NOT NULL,
        fecha_hallazgo          DATE NOT NULL,
        estado                  VARCHAR(20) NOT NULL CONSTRAINT DF_objetos_estado DEFAULT 'DISPONIBLE'
                                CONSTRAINT CK_objetos_estado CHECK (estado IN ('DISPONIBLE','EN_RECLAMO','DEVUELTO')),
        ruta_imagen             NVARCHAR(500) NULL,
        pregunta_verificacion   NVARCHAR(300) NOT NULL,
        publicado_por           BIGINT NOT NULL,
        fecha_publicacion       DATETIME2 NOT NULL CONSTRAINT DF_objetos_fecha DEFAULT SYSDATETIME(),
        CONSTRAINT FK_objetos_usuario FOREIGN KEY (publicado_por) REFERENCES dbo.usuarios(id_usuario)
    );
    CREATE INDEX IX_objetos_busqueda ON dbo.objetos_encontrados(estado, categoria, fecha_hallazgo DESC);
END;
GO

IF OBJECT_ID(N'dbo.reclamos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.reclamos (
        id_reclamo              BIGINT IDENTITY(1,1) PRIMARY KEY,
        id_objeto               BIGINT NOT NULL,
        id_usuario              BIGINT NOT NULL,
        respuesta_verificacion  NVARCHAR(1000) NOT NULL,
        fecha_reclamo           DATETIME2 NOT NULL CONSTRAINT DF_reclamos_fecha DEFAULT SYSDATETIME(),
        estado                  VARCHAR(20) NOT NULL CONSTRAINT DF_reclamos_estado DEFAULT 'PENDIENTE'
                                CONSTRAINT CK_reclamos_estado CHECK (estado IN ('PENDIENTE','APROBADO','RECHAZADO')),
        respuesta_admin         NVARCHAR(1000) NULL,
        revisado_por            BIGINT NULL,
        fecha_revision          DATETIME2 NULL,
        CONSTRAINT FK_reclamos_objeto FOREIGN KEY (id_objeto) REFERENCES dbo.objetos_encontrados(id_objeto),
        CONSTRAINT FK_reclamos_usuario FOREIGN KEY (id_usuario) REFERENCES dbo.usuarios(id_usuario),
        CONSTRAINT FK_reclamos_admin FOREIGN KEY (revisado_por) REFERENCES dbo.usuarios(id_usuario)
    );
    CREATE INDEX IX_reclamos_estado ON dbo.reclamos(estado, fecha_reclamo DESC);
END;
GO

IF OBJECT_ID(N'dbo.avisos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.avisos (
        id_aviso       BIGINT IDENTITY(1,1) PRIMARY KEY,
        id_usuario     BIGINT NOT NULL,
        id_objeto      BIGINT NULL,
        mensaje        NVARCHAR(500) NOT NULL,
        fecha_aviso    DATETIME2 NOT NULL CONSTRAINT DF_avisos_fecha DEFAULT SYSDATETIME(),
        leido          BIT NOT NULL CONSTRAINT DF_avisos_leido DEFAULT 0,
        CONSTRAINT FK_avisos_usuario FOREIGN KEY (id_usuario) REFERENCES dbo.usuarios(id_usuario),
        CONSTRAINT FK_avisos_objeto FOREIGN KEY (id_objeto) REFERENCES dbo.objetos_encontrados(id_objeto)
    );
END;
GO

IF OBJECT_ID(N'dbo.bitacora', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.bitacora (
        id_bitacora          BIGINT IDENTITY(1,1) PRIMARY KEY,
        id_usuario           BIGINT NULL,
        accion               NVARCHAR(80) NOT NULL,
        fecha_accion         DATETIME2 NOT NULL CONSTRAINT DF_bitacora_fecha DEFAULT SYSDATETIME(),
        elemento_relacionado NVARCHAR(250) NULL,
        CONSTRAINT FK_bitacora_usuario FOREIGN KEY (id_usuario) REFERENCES dbo.usuarios(id_usuario)
    );
END;
GO

CREATE OR ALTER TRIGGER dbo.TR_reclamos_resolucion
ON dbo.reclamos
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.avisos(id_usuario, id_objeto, mensaje)
    SELECT i.id_usuario, i.id_objeto,
           CONCAT(N'Tu reclamo por ', o.titulo, N' fue ', LOWER(i.estado), N'. ', COALESCE(i.respuesta_admin, N''))
    FROM inserted i
    JOIN deleted d ON d.id_reclamo = i.id_reclamo
    JOIN dbo.objetos_encontrados o ON o.id_objeto = i.id_objeto
    WHERE i.estado <> d.estado AND i.estado IN ('APROBADO','RECHAZADO');

    UPDATE o
       SET estado = CASE WHEN i.estado = 'APROBADO' THEN 'DEVUELTO' ELSE 'DISPONIBLE' END
    FROM dbo.objetos_encontrados o
    JOIN inserted i ON i.id_objeto = o.id_objeto
    JOIN deleted d ON d.id_reclamo = i.id_reclamo
    WHERE i.estado <> d.estado AND i.estado IN ('APROBADO','RECHAZADO');

    INSERT INTO dbo.bitacora(id_usuario, accion, elemento_relacionado)
    SELECT i.revisado_por, N'RESOLVER_RECLAMO', CONCAT(N'Reclamo #', i.id_reclamo, N' - ', i.estado)
    FROM inserted i JOIN deleted d ON d.id_reclamo = i.id_reclamo
    WHERE i.estado <> d.estado;
END;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.usuarios WHERE correo = N'admin@demo.pe')
    INSERT INTO dbo.usuarios(nombre_completo, correo, codigo_estudiante, password_hash, rol)
    VALUES (N'Carlos Administrador', N'admin@demo.pe', N'ADM001', HASHBYTES('SHA2_256', N'Admin123*'), 'ADMINISTRADOR');

IF NOT EXISTS (SELECT 1 FROM dbo.usuarios WHERE correo = N'estudiante@demo.pe')
    INSERT INTO dbo.usuarios(nombre_completo, correo, codigo_estudiante, password_hash, rol)
    VALUES (N'Ana Torres', N'estudiante@demo.pe', N'20240001', HASHBYTES('SHA2_256', N'Demo123*'), 'ESTUDIANTE');
GO

DECLARE @admin BIGINT = (SELECT id_usuario FROM dbo.usuarios WHERE correo = N'admin@demo.pe');
IF NOT EXISTS (SELECT 1 FROM dbo.objetos_encontrados)
BEGIN
    INSERT INTO dbo.objetos_encontrados
        (titulo, descripcion, categoria, lugar_hallazgo, fecha_hallazgo, estado, pregunta_verificacion, publicado_por)
    VALUES
        (N'Mochila azul', N'Mochila de lona con cuadernos de ingeniería.', N'Mochilas', N'Biblioteca central', DATEADD(DAY,-2,CAST(GETDATE() AS date)), 'DISPONIBLE', N'¿Qué curso aparece en el cuaderno?', @admin),
        (N'Audífonos inalámbricos', N'Estuche negro con audífonos blancos.', N'Tecnología', N'Laboratorio 204', DATEADD(DAY,-4,CAST(GETDATE() AS date)), 'DISPONIBLE', N'¿Qué marca son los audífonos?', @admin),
        (N'Botella térmica', N'Botella metálica color verde con stickers.', N'Accesorios', N'Cafetería', DATEADD(DAY,-1,CAST(GETDATE() AS date)), 'DISPONIBLE', N'Describe uno de los stickers.', @admin);
END;
GO

PRINT N'Base ObjetosPerdidosDB preparada correctamente.';
GO
