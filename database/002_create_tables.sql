USE ObjetosPerdidosUNI;
GO

CREATE TABLE dbo.usuario (
    id_usuario        INT IDENTITY(1,1) CONSTRAINT pk_usuario PRIMARY KEY,
    nombre            NVARCHAR(80)  NOT NULL,
    apellido          NVARCHAR(80)  NOT NULL,
    correo            NVARCHAR(150) NOT NULL CONSTRAINT uq_usuario_correo UNIQUE,
    password_hash     VARCHAR(60)   NOT NULL,
    rol               VARCHAR(20)   NOT NULL CONSTRAINT df_usuario_rol DEFAULT 'ESTUDIANTE'
        CONSTRAINT ck_usuario_rol CHECK (rol IN ('ESTUDIANTE', 'ADMIN')),
    intentos_fallidos INT           NOT NULL CONSTRAINT df_usuario_intentos DEFAULT 0
        CONSTRAINT ck_usuario_intentos CHECK (intentos_fallidos >= 0),
    bloqueado         BIT           NOT NULL CONSTRAINT df_usuario_bloqueado DEFAULT 0,
    fecha_registro    DATETIME2(0)  NOT NULL CONSTRAINT df_usuario_fecha DEFAULT SYSDATETIME()
);
GO

CREATE TABLE dbo.objeto (
    id_objeto         INT IDENTITY(1,1) CONSTRAINT pk_objeto PRIMARY KEY,
    titulo            NVARCHAR(120) NOT NULL,
    descripcion       NVARCHAR(500) NULL,
    categoria         NVARCHAR(50)  NOT NULL,
    lugar             NVARCHAR(100) NOT NULL,
    fecha_hallazgo    DATE          NOT NULL,
    estado            VARCHAR(20)   NOT NULL CONSTRAINT df_objeto_estado DEFAULT 'PUBLICADO'
        CONSTRAINT ck_objeto_estado CHECK (estado IN ('PUBLICADO', 'RECLAMADO', 'ENTREGADO')),
    id_publicador     INT           NOT NULL
        CONSTRAINT fk_objeto_usuario REFERENCES dbo.usuario (id_usuario),
    fecha_publicacion DATETIME2(0)  NOT NULL CONSTRAINT df_objeto_fecha DEFAULT SYSDATETIME()
);
GO

-- La imagen vive en disco; aquí solo se guarda su ruta.
CREATE TABLE dbo.foto (
    id_foto      INT IDENTITY(1,1) CONSTRAINT pk_foto PRIMARY KEY,
    id_objeto    INT           NOT NULL
        CONSTRAINT fk_foto_objeto REFERENCES dbo.objeto (id_objeto),
    ruta_archivo NVARCHAR(260) NOT NULL
);
GO

CREATE TABLE dbo.reclamo (
    id_reclamo             INT IDENTITY(1,1) CONSTRAINT pk_reclamo PRIMARY KEY,
    id_objeto              INT           NOT NULL
        CONSTRAINT fk_reclamo_objeto REFERENCES dbo.objeto (id_objeto),
    id_usuario             INT           NOT NULL
        CONSTRAINT fk_reclamo_usuario REFERENCES dbo.usuario (id_usuario),
    estado                 VARCHAR(20)   NOT NULL CONSTRAINT df_reclamo_estado DEFAULT 'PENDIENTE'
        CONSTRAINT ck_reclamo_estado CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO')),
    respuesta_verificacion NVARCHAR(300) NULL,
    fecha_reclamo          DATETIME2(0)  NOT NULL CONSTRAINT df_reclamo_fecha DEFAULT SYSDATETIME(),
    fecha_respuesta        DATETIME2(0)  NULL
);
GO

-- Datos privados de verificación: nunca se muestran en el catálogo público.
CREATE TABLE dbo.custodia (
    id_custodia            INT IDENTITY(1,1) CONSTRAINT pk_custodia PRIMARY KEY,
    id_objeto              INT           NOT NULL
        CONSTRAINT uq_custodia_objeto UNIQUE
        CONSTRAINT fk_custodia_objeto REFERENCES dbo.objeto (id_objeto),
    pregunta_verificacion  NVARCHAR(200) NOT NULL,
    caracteristica_privada NVARCHAR(300) NOT NULL,
    fecha_registro         DATETIME2(0)  NOT NULL CONSTRAINT df_custodia_fecha DEFAULT SYSDATETIME()
);
GO

CREATE TABLE dbo.aviso (
    id_aviso   INT IDENTITY(1,1) CONSTRAINT pk_aviso PRIMARY KEY,
    id_usuario INT           NOT NULL
        CONSTRAINT fk_aviso_usuario REFERENCES dbo.usuario (id_usuario),
    id_reclamo INT           NULL
        CONSTRAINT fk_aviso_reclamo REFERENCES dbo.reclamo (id_reclamo),
    mensaje    NVARCHAR(300) NOT NULL,
    leido      BIT           NOT NULL CONSTRAINT df_aviso_leido DEFAULT 0,
    fecha      DATETIME2(0)  NOT NULL CONSTRAINT df_aviso_fecha DEFAULT SYSDATETIME()
);
GO

CREATE TABLE dbo.bitacora (
    id_bitacora INT IDENTITY(1,1) CONSTRAINT pk_bitacora PRIMARY KEY,
    id_usuario  INT           NULL
        CONSTRAINT fk_bitacora_usuario REFERENCES dbo.usuario (id_usuario),
    accion      VARCHAR(50)   NOT NULL,
    id_objeto   INT           NULL
        CONSTRAINT fk_bitacora_objeto REFERENCES dbo.objeto (id_objeto),
    detalle     NVARCHAR(300) NULL,
    fecha       DATETIME2(0)  NOT NULL CONSTRAINT df_bitacora_fecha DEFAULT SYSDATETIME()
);
GO

CREATE TABLE dbo.codigo_qr (
    id_codigo_qr     INT IDENTITY(1,1) CONSTRAINT pk_codigo_qr PRIMARY KEY,
    id_objeto        INT          NOT NULL
        CONSTRAINT uq_codigo_qr_objeto UNIQUE
        CONSTRAINT fk_codigo_qr_objeto REFERENCES dbo.objeto (id_objeto),
    codigo           VARCHAR(64)  NOT NULL CONSTRAINT uq_codigo_qr_codigo UNIQUE,
    fecha_generacion DATETIME2(0) NOT NULL CONSTRAINT df_codigo_qr_fecha DEFAULT SYSDATETIME()
);
GO
