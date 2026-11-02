# Encuentra+ — Objetos perdidos y encontrados

Aplicación de escritorio JavaFX 21 con FXML editable en Scene Builder, arquitectura por capas y soporte para SQL Server. El modo inicial es `mock`, de modo que todas las pantallas funcionan sin instalar una base de datos.

## Requisitos

- JDK 21. En el equipo de desarrollo actual está disponible en `C:\Users\angel\.jdks\ms-21.0.12.1`.
- Maven no necesita instalación global: el proyecto incluye Maven Wrapper.
- Scene Builder 21 o posterior es opcional para editar los FXML.
- SQL Server 2019 o posterior solo es necesario para el modo conectado.

## Compilar y ejecutar

Desde PowerShell, ubicado en la carpeta del proyecto:

```powershell
$env:JAVA_HOME = 'C:\Users\angel\.jdks\ms-21.0.12.1'
.\mvnw.cmd clean test
.\mvnw.cmd javafx:run
```

Usuarios del modo demostración:

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@uni.edu.pe` | `123456` |

Los estudiantes pueden crear una cuenta desde la pantalla de registro y luego iniciar sesión.

## IntelliJ IDEA

1. Abrir esta carpeta o el archivo `pom.xml` como proyecto Maven.
2. Elegir **Project SDK 21**.
3. Recargar el proyecto Maven.
4. Ejecutar la configuración Maven `javafx:run`, o la clase `pe.edu.objetoshallados.App` desde Maven.

No es necesario agregar manualmente el SDK de JavaFX: Maven descarga sus módulos.

## Visual Studio Code

1. Abrir esta carpeta completa con VS Code.
2. Instalar las extensiones recomendadas cuando VS Code lo solicite: **Extension Pack for Java** y **Maven for Java**.
3. Seleccionar el JDK 21 con `Java: Configure Java Runtime`.
4. Abrir la vista Maven y ejecutar `Plugins > javafx > javafx:run`, o usar la terminal con los comandos anteriores.

La carpeta `.vscode` contiene recomendaciones y una configuración de ejecución.

## Scene Builder

Los diseños están en `src/main/resources/fxml/` y los estilos en `src/main/resources/css/app.css`. En Scene Builder se puede abrir cualquier `.fxml` directamente. Si solicita el classpath, agrega `target/classes` después de compilar una vez.

## SQL Server

1. Ejecutar `database/01-schema-and-seed.sql` en SQL Server Management Studio o Azure Data Studio.
2. Mantener las credenciales fuera del código y definirlas antes de arrancar:

```powershell
$env:APP_MODE = 'sqlserver'
$env:APP_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=ObjetosPerdidosDB;encrypt=true;trustServerCertificate=true'
$env:APP_DB_USER = 'sa'
$env:APP_DB_PASSWORD = 'tu-clave'
.\mvnw.cmd javafx:run
```

Si no se define `APP_MODE=sqlserver`, la aplicación usa datos simulados. La interfaz nunca consulta SQL directamente: los controladores llaman servicios y estos delegan en el repositorio.

## Estructura

```text
src/main/java/.../controller   Controladores JavaFX
src/main/java/.../service      Validaciones y casos de uso
src/main/java/.../repository   Mock y JDBC SQL Server
src/main/resources/fxml        Pantallas editables en Scene Builder
src/main/resources/css         Identidad visual común
database                       Esquema, índices, trigger y datos iniciales
docs                           Requisitos, arquitectura, informes y UML
data/images                    Imágenes persistidas en desarrollo (ignoradas por Git)
.github                        Plantilla de pull request del equipo
```

El contrato entre la interfaz, los servicios y la persistencia está definido en
`src/main/java/pe/edu/objetoshallados/repository/AppRepository.java`. Las implementaciones
pueden cambiar entre datos de demostración y SQL Server sin modificar los FXML.
