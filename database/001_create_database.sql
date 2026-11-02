-- Crea la base del proyecto. Modern_Spanish_CI_AI hace que las búsquedas
-- ignoren mayúsculas y tildes ("cafeteria" encuentra "Cafetería").
IF DB_ID(N'ObjetosPerdidosUNI') IS NULL
    CREATE DATABASE ObjetosPerdidosUNI COLLATE Modern_Spanish_CI_AI;
GO
