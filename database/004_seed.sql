USE ObjetosPerdidosUNI;
GO

-- Usuarios SOLO para desarrollo.
-- admin@uni.pe: Admin123!   |   estudiantes: Estudiante123!
INSERT INTO dbo.usuario (nombre, apellido, correo, password_hash, rol) VALUES
    (N'Admin',  N'Sistema', N'admin@uni.pe',       '$2a$12$ZzxoMsQIsaskjHpHKtLLvOp013kqVsAVJoSDpupFMivcE/D0fpBha', 'ADMIN'),
    (N'Ana',    N'Prueba',  N'ana.prueba@uni.pe',  '$2a$12$rD4IGAnJmOpKfgpNB6kQHu5bSuLLJJqpTtuPore5GeuN5o5tLt6IO', 'ESTUDIANTE'),
    (N'Bruno',  N'Prueba',  N'bruno.prueba@uni.pe','$2a$12$rD4IGAnJmOpKfgpNB6kQHu5bSuLLJJqpTtuPore5GeuN5o5tLt6IO', 'ESTUDIANTE');
GO
