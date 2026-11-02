#!/usr/bin/env bash
# Borra y reconstruye ObjetosPerdidosUNI, crea el login objetos_app y corre las
# pruebas. La contraseña del login se guarda en .env (fuera de Git).
set -euo pipefail

cd "$(dirname "$0")/.."
CONTAINER="${CONTAINER:-sqlservercont}"
SQLCMD=/opt/mssql-tools18/bin/sqlcmd

# Las contraseñas viajan por variable de entorno, nunca como argumento.
como_sa() {
    docker exec -i "$CONTAINER" sh -c \
        'SQLCMDPASSWORD="$MSSQL_SA_PASSWORD" exec "$0" -S localhost -U sa -C -b -f 65001 "$@"' \
        "$SQLCMD" "$@"
}
como_app() {
    SQLCMDPASSWORD="$OP_DB_PASSWORD" docker exec -i -e SQLCMDPASSWORD "$CONTAINER" \
        "$SQLCMD" -S localhost -U objetos_app -C -b -f 65001 "$@"
}

if [ -f .env ]; then
    OP_DB_PASSWORD="$(sed -n 's/^OP_DB_PASSWORD=//p' .env)"
fi
if [ -z "${OP_DB_PASSWORD:-}" ]; then
    OP_DB_PASSWORD="Op1!$(openssl rand -hex 16)"
    (umask 077; printf 'OP_DB_USER=objetos_app\nOP_DB_PASSWORD=%s\n' "$OP_DB_PASSWORD" > .env)
    echo "Contraseña nueva guardada en .env"
fi

echo "== Eliminando base anterior =="
como_sa <<'SQL'
IF DB_ID(N'ObjetosPerdidosUNI') IS NOT NULL
BEGIN
    ALTER DATABASE ObjetosPerdidosUNI SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ObjetosPerdidosUNI;
END
GO
SQL

for script in database/00*.sql; do
    echo "== $script =="
    como_sa < "$script"
done

echo "== Login objetos_app =="
como_sa <<SQL
IF SUSER_ID(N'objetos_app') IS NULL
    CREATE LOGIN objetos_app WITH PASSWORD = N'$OP_DB_PASSWORD';
ELSE
    ALTER LOGIN objetos_app WITH PASSWORD = N'$OP_DB_PASSWORD';
GO
USE ObjetosPerdidosUNI;
CREATE USER objetos_app FOR LOGIN objetos_app;
ALTER ROLE db_datareader ADD MEMBER objetos_app;
ALTER ROLE db_datawriter ADD MEMBER objetos_app;
GO
SQL

echo "== Pruebas =="
como_sa -W -s ' | ' < database/pruebas.sql

echo "== Consultas con objetos_app =="
como_app -W -s ' | ' < querys/consultas_repositorios.sql > /dev/null
echo "OK"

echo "== Base reconstruida =="
