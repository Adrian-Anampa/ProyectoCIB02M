Sí. Considerando **Java 21 + JavaFX 21 + Scene Builder dentro del flujo de IntelliJ IDEA + SQL Server**, yo ya no separaría al grupo simplemente en “frontend / backend / BD”, porque en una aplicación de escritorio JavaFX eso puede terminar generando demasiadas dependencias entre personas.

Lo organizaría por **capas y módulos funcionales**, de modo que Oscar, Renato, Adrian y Han puedan trabajar simultáneamente sobre el mismo proyecto.

## 1. ¿Qué aplicación están construyendo?

El proyecto sería un **sistema de objetos perdidos y encontrados** para una institución.

Un estudiante podría:

- Registrarse e iniciar sesión.
- Publicar un objeto encontrado.
- Adjuntar una fotografía.
- Buscar objetos por categoría, lugar, fecha o estado.
- Reclamar un objeto que considere suyo.
- Responder una pregunta de verificación/custodia.
- Recibir avisos sobre sus reclamos.
- Confirmar la devolución del objeto.

Un administrador podría:

- Revisar reclamos.
- Aprobarlos o rechazarlos.
- Consultar objetos publicados.
- Revisar la bitácora.
- Consultar indicadores y estadísticas.

La aplicación finalmente se entregaría como un **aplicativo Java ejecutable**, conectado mediante JDBC a SQL Server.

La arquitectura general sería:

```text
                    JavaFX 21
                       │
                 FXML + CSS
                       │
                 Controllers
                       │
                    Services
                       │
              Repository / DAO
                       │
                      JDBC
                       │
              Microsoft SQL Server
```

La regla más importante sería:

> **Una ventana JavaFX nunca debería ejecutar SQL directamente.**

Por ejemplo, esto estaría mal:

```text
CatalogoController
        ↓
      SQL
```

Lo correcto sería:

```text
CatalogoController
        ↓
ObjetoService
        ↓
ObjetoRepository
        ↓
SQL Server
```

Eso les da la modularidad que están buscando.

---

# 2. División del equipo

Propongo esta distribución:

| Integrante | Responsabilidad principal |
|---|---|
| **Oscar** | JavaFX, FXML, Scene Builder y experiencia de usuario |
| **Renato** | SQL Server, JDBC, modelos y capa Repository |
| **Adrian** | Usuarios, autenticación, reclamos, seguridad y custodia |
| **Han** | Objetos, imágenes, QR, avisos e indicadores |

La separación no significa que cada persona haga una aplicación diferente. Todos trabajan sobre **el mismo proyecto de IntelliJ y el mismo repositorio Git**.

---

# 3. Oscar — JavaFX, Scene Builder y capa visual

Oscar sería principalmente responsable de la **interfaz gráfica de toda la aplicación**.

Su herramienta principal sería:

```text
IntelliJ IDEA
      +
Scene Builder
      +
FXML
      +
CSS
```

Scene Builder permite diseñar visualmente las interfaces sin tener que construir cada `Button`, `TextField`, `VBox`, etc. manualmente.

Oscar trabajaría principalmente en:

```text
src/main/resources/
│
├── fxml/
│
├── css/
│
└── images/
```

y parcialmente en:

```text
controller/
```

---

## M1 — Login y registro

Crearía:

```text
login.fxml
registro.fxml
```

Por ejemplo, en Scene Builder podría diseñar:

```text
┌─────────────────────────────────┐
│        OBJETOS PERDIDOS         │
│                                 │
│  Correo                         │
│  [________________________]     │
│                                 │
│  Contraseña                     │
│  [________________________]     │
│                                 │
│       [ Iniciar sesión ]        │
│                                 │
│ ¿No tienes cuenta? Registrarse  │
└─────────────────────────────────┘
```

Oscar define:

- posiciones;
- botones;
- campos;
- tablas;
- colores;
- íconos;
- CSS;
- navegación visual.

Adrian implementaría la lógica real del login.

---

# 4. Catálogo

Oscar también construiría:

```text
catalogo.fxml
```

Por ejemplo:

```text
┌─────────────────────────────────────────────┐
│ Objetos encontrados                         │
│                                             │
│ Categoría: [Todas ▼] Lugar: [Todos ▼]       │
│                                             │
│ Buscar: [____________________] [Buscar]      │
│                                             │
│ ┌─────────────────────────────────────────┐ │
│ │ 📱 Samsung Galaxy                      │ │
│ │ Biblioteca                             │ │
│ │ Encontrado: 25/09/2026                 │ │
│ │                         [Ver objeto]    │ │
│ └─────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
```

Oscar no necesita saber cómo se ejecuta esta consulta:

```sql
SELECT *
FROM objeto
WHERE categoria = ...
```

Solo necesita saber que existirá algo similar a:

```java
objetoService.buscar(filtros);
```

---

# 5. Otras interfaces de Oscar

Oscar podría construir:

```text
menu-principal.fxml
catalogo.fxml
detalle-objeto.fxml
publicar-objeto.fxml

mis-reclamos.fxml
detalle-reclamo.fxml

custodia.fxml

avisos.fxml

qr.fxml

admin-panel.fxml

indicadores.fxml
```

También:

- cuadros de confirmación;
- mensajes de error;
- validación visual;
- loaders;
- imágenes por defecto;
- diseño responsivo dentro de lo razonable para escritorio.

---

# 6. Contrato Oscar ↔ Adrian/Han

Oscar no debería esperar a que Adrian o Han terminen todo.

Por ejemplo, acuerdan que Han entregará:

```java
List<Objeto> buscarObjetos(FiltroObjeto filtro);
```

Entonces Oscar puede temporalmente trabajar con:

```java
List<Objeto> objetosPrueba = List.of(...);
```

y construir la tabla.

Después simplemente reemplazan los datos falsos por:

```java
objetoService.buscarObjetos(filtro);
```

Esto permite trabajar **en paralelo**.

---

# 7. Renato — SQL Server y persistencia

Renato sería responsable de la **infraestructura de datos**.

Trabajaría principalmente en:

```text
model/
repository/
config/
database/
```

Sería quien define oficialmente cómo se representa la información en la aplicación.

---

# 8. Diseño de SQL Server

Renato diseñaría las tablas:

```text
usuario
objeto
foto
reclamo
custodia
aviso
bitacora
codigo_qr
```

Por ejemplo:

```text
USUARIO
──────────────────
id_usuario PK
nombre
apellido
correo
password_hash
rol
intentos_fallidos
bloqueado
fecha_registro
```

---

## Objeto

```text
OBJETO
──────────────────
id_objeto PK
titulo
descripcion
categoria
lugar
fecha_hallazgo
estado
id_publicador FK
fecha_publicacion
```

---

## Reclamo

```text
RECLAMO
──────────────────
id_reclamo PK
id_objeto FK
id_usuario FK
estado
fecha_reclamo
fecha_respuesta
```

---

# 9. JDBC

Renato también se encargaría de la conexión:

```text
Java
 ↓
JDBC
 ↓
Microsoft JDBC Driver
 ↓
SQL Server
```

Podrían tener:

```java
DatabaseConnection.java
```

que centralice la conexión.

Por ejemplo:

```java
Connection connection =
    DriverManager.getConnection(url, user, password);
```

Pero las demás personas **no deberían repetir este código por todas partes**.

Deberían pedir la conexión a una clase centralizada.

---

# 10. Repository

Renato también diseñaría el patrón Repository/DAO.

Por ejemplo:

```text
repository/
│
├── UsuarioRepository.java
├── ObjetoRepository.java
├── ReclamoRepository.java
├── FotoRepository.java
├── AvisoRepository.java
├── CustodiaRepository.java
└── BitacoraRepository.java
```

Por ejemplo:

```java
public interface ObjetoRepository {

    List<Objeto> buscar(FiltroObjeto filtro);

    Optional<Objeto> buscarPorId(int id);

    void guardar(Objeto objeto);

    void actualizar(Objeto objeto);

}
```

Después:

```java
SqlServerObjetoRepository
```

implementaría esos métodos utilizando JDBC.

Esto mejora muchísimo la modularidad.

---

# 11. ¿Por qué interfaces?

Porque podrían tener:

```text
ObjetoRepository
       ↑
       │
SqlServerObjetoRepository
```

y en pruebas:

```text
ObjetoRepository
       ↑
       │
FakeObjetoRepository
```

Así pueden probar lógica sin necesitar constantemente SQL Server.

---

# 12. Scripts SQL

Renato mantendría una carpeta:

```text
database/
│
├── 001_create_database.sql
├── 002_create_tables.sql
├── 003_constraints.sql
├── 004_indexes.sql
├── 005_seed.sql
└── 006_test_data.sql
```

Eso permite que cualquier integrante pueda reconstruir la BD.

---

# 13. Datos de prueba

Renato también puede generar:

```text
1000 objetos ficticios
```

para probar:

- filtros;
- rendimiento;
- búsquedas;
- indicadores.

Y crear índices sobre:

```text
categoria
lugar
estado
fecha_hallazgo
```

---

# 14. Adrian — Usuarios, seguridad y reclamos

Adrian sería responsable principalmente de la **lógica sensible del negocio**.

Trabajaría principalmente en:

```text
service/
controller/
security/
```

Sus módulos serían:

```text
M1 Usuarios
M3 Reclamos
M4 Custodia
```

---

# 15. M1 — Usuarios

Adrian implementaría:

```text
Registro
Login
Logout
Roles
Sesión
Bloqueos
Contraseñas
```

Por ejemplo:

```text
LoginController
       ↓
AuthService
       ↓
UsuarioRepository
       ↓
SQL Server
```

Oscar crea visualmente:

```text
login.fxml
```

Adrian conecta:

```text
LoginController.java
```

---

# 16. Validación institucional

Adrian también implementaría cosas como:

```java
correo.endsWith("@universidad.edu.pe")
```

aunque convendría colocar el dominio como configuración.

También:

- correo obligatorio;
- contraseña segura;
- correo único;
- usuario activo.

---

# 17. Hash de contraseña

Nunca guardar:

```text
123456
```

directamente en SQL Server.

Se guardaría algo similar a:

```text
$2a$12$...
```

usando BCrypt.

---

# 18. SessionManager

Adrian podría implementar:

```java
SessionManager
```

para saber quién inició sesión.

Ejemplo conceptual:

```java
SessionManager.getInstance().getUsuarioActual();
```

Esto permite que cualquier pantalla sepa:

```text
Usuario actual:
Juan Pérez

Rol:
ESTUDIANTE
```

sin consultar constantemente la BD.

---

# 19. Roles

Por ejemplo:

```java
enum Rol {
    ESTUDIANTE,
    ADMIN
}
```

El administrador podría acceder a:

```text
Panel administrativo
Aprobar reclamos
Indicadores
Bitácora
```

mientras un estudiante no.

---

# 20. M3 — Reclamos

Adrian sería responsable de todo el proceso.

Estado del objeto:

```text
PUBLICADO
    ↓
RECLAMADO
    ↓
ENTREGADO
```

Estado del reclamo:

```text
             PENDIENTE
              /     \
             /       \
       APROBADO     RECHAZADO
```

Adrian debe controlar esas reglas.

Por ejemplo:

> No se puede aprobar un reclamo de un objeto ya entregado.

Eso pertenece al `Service`, no al FXML ni a SQL directamente.

---

# 21. ReclamoService

Podría existir:

```java
ReclamoService
```

con métodos:

```java
crearReclamo(...)

aprobarReclamo(...)

rechazarReclamo(...)

confirmarEntrega(...)
```

---

# 22. Bitácora

Cada operación importante podría generar:

```text
Usuario 15
Acción: RECLAMO_APROBADO
Objeto: 82
Fecha: ...
```

Adrian define cuándo registrar esas operaciones.

Renato se encarga de almacenarlas correctamente.

---

# 23. M4 — Custodia

Adrian también implementaría el mecanismo de verificación.

Por ejemplo, alguien encuentra:

```text
Mochila negra
```

La publicación pública muestra:

```text
Marca: Adidas
Lugar: Biblioteca
Color: negro
```

pero existe una característica privada:

```text
Tiene un llavero rojo dentro del bolsillo delantero.
```

El reclamante podría recibir una pregunta:

```text
¿Qué característica distintiva tiene la mochila?
```

La respuesta sirve como evidencia adicional para quien administra la entrega.

---

# 24. Han — Objetos e incrementos

Han sería responsable del otro gran grupo de funcionalidades:

```text
M2 Objetos
M5 QR
M6 Avisos
M7 Indicadores
Fotos
```

Trabajaría principalmente en:

```text
service/
controller/
util/
```

---

# 25. M2 — Publicación de objetos

Han implementaría:

```java
ObjetoService
```

con operaciones:

```text
Crear
Editar
Buscar
Filtrar
Ver detalle
Cambiar estado
```

Por ejemplo:

```java
public List<Objeto> buscarObjetos(FiltroObjeto filtro)
```

El flujo sería:

```text
catalogo.fxml
     ↓
CatalogoController
     ↓
ObjetoService
     ↓
ObjetoRepository
     ↓
SQL Server
```

---

# 26. Filtros

Han implementaría búsquedas por:

```text
Categoría
Lugar
Estado
Fecha
Palabra clave
```

Renato crea los índices necesarios para que esas consultas sean rápidas.

---

# 27. Fotos

Han también tendría la gestión de imágenes.

Cuando Oscar crea:

```text
[Seleccionar imagen]
```

Han implementa:

```text
FileChooser
     ↓
validar archivo
     ↓
procesar imagen
     ↓
guardar archivo
     ↓
registrar ruta en SQL Server
```

Validaciones:

```text
JPG / PNG
≤ 5 MB
archivo realmente válido
```

Los nombres podrían utilizar:

```java
UUID.randomUUID();
```

Por ejemplo:

```text
84cd3401-4770-4d53-8aed-a312fc4e7e74.jpg
```

---

# 28. Fotos y SQL Server

Yo no guardaría obligatoriamente toda la imagen en la BD.

Podrían guardar:

```text
/data/images/UUID.jpg
```

y en SQL Server:

```text
foto
─────────────────
id_foto
id_objeto
ruta_archivo
```

Si después deciden tener almacenamiento remoto, pueden cambiar la implementación sin modificar todo el proyecto.

---

# 29. M5 — QR

Han implementaría QR utilizando, por ejemplo, ZXing.

Flujo:

```text
Objeto creado
     ↓
generar identificador
     ↓
generar QR
     ↓
mostrar QR
```

Por ejemplo:

```text
OBJ-000154
```

o:

```text
58b6150f-d4e1...
```

Oscar crea:

```text
qr.fxml
```

Han crea:

```java
QrService
```

---

# 30. M6 — Avisos

Cuando ocurre:

```text
Reclamo aprobado
```

el usuario debería recibir:

```text
🔔 Tu reclamo del objeto "Mochila Adidas"
   fue aprobado.
```

Han podría implementar:

```java
AvisoService
```

mientras Renato tiene:

```java
AvisoRepository
```

y Adrian genera ciertos eventos.

Por ejemplo:

```text
Adrian:
Reclamo aprobado
       ↓
AvisoService
       ↓
Han:
crear aviso
       ↓
Renato:
AvisoRepository
       ↓
SQL Server
```

---

# 31. M7 — Indicadores

Han puede desarrollar la lógica del dashboard.

Por ejemplo:

```text
Objetos publicados:        1240
Objetos entregados:         724
Reclamos pendientes:         43
```

También:

```text
Objetos por categoría
```

```text
Celulares       315
Mochilas        221
Documentos      184
Llaves          163
Otros           357
```

Renato construye las consultas eficientes.

Han las consume.

Oscar las muestra utilizando JavaFX:

```text
BarChart
PieChart
LineChart
```

Esto muestra claramente que M7 es colaborativo:

```text
Renato → datos
Han → lógica
Oscar → visualización
```

---

# 32. Estructura final del proyecto

Yo propondría:

```text
objetos-perdidos/
│
├── pom.xml
│
├── README.md
│
├── .gitignore
│
├── database/
│   ├── 001_create_database.sql
│   ├── 002_create_tables.sql
│   ├── 003_indexes.sql
│   ├── 004_seed.sql
│   └── 005_test_data.sql
│
├── data/
│   └── images/
│
└── src/
    ├── main/
    │
    ├── java/
    │   └── pe/
    │       └── universidad/
    │           └── objetosperdidos/
    │
    │               ├── Main.java
    │               │
    │               ├── config/
    │               │   ├── DatabaseConfig.java
    │               │   └── DatabaseConnection.java
    │               │
    │               ├── model/
    │               │   ├── Usuario.java
    │               │   ├── Objeto.java
    │               │   ├── Reclamo.java
    │               │   ├── Foto.java
    │               │   ├── Custodia.java
    │               │   ├── Aviso.java
    │               │   └── Bitacora.java
    │               │
    │               ├── repository/
    │               │   ├── UsuarioRepository.java
    │               │   ├── ObjetoRepository.java
    │               │   ├── ReclamoRepository.java
    │               │   └── ...
    │               │
    │               ├── repository/impl/
    │               │   ├── SqlServerUsuarioRepository.java
    │               │   ├── SqlServerObjetoRepository.java
    │               │   └── ...
    │               │
    │               ├── service/
    │               │   ├── AuthService.java
    │               │   ├── UsuarioService.java
    │               │   ├── ObjetoService.java
    │               │   ├── ReclamoService.java
    │               │   ├── AvisoService.java
    │               │   ├── CustodiaService.java
    │               │   └── IndicadorService.java
    │               │
    │               ├── controller/
    │               │   ├── LoginController.java
    │               │   ├── RegistroController.java
    │               │   ├── CatalogoController.java
    │               │   ├── PublicarObjetoController.java
    │               │   ├── ReclamoController.java
    │               │   ├── AvisosController.java
    │               │   └── ...
    │               │
    │               ├── security/
    │               │   ├── PasswordManager.java
    │               │   └── SessionManager.java
    │               │
    │               ├── util/
    │               │   ├── ImageUtils.java
    │               │   ├── QRUtils.java
    │               │   └── ValidationUtils.java
    │               │
    │               └── event/
    │                   ├── EventBus.java
    │                   └── ...
    │
    └── resources/
        ├── fxml/
        │   ├── login.fxml
        │   ├── registro.fxml
        │   ├── catalogo.fxml
        │   └── ...
        │
        ├── css/
        │   └── app.css
        │
        └── images/
```

---

# 33. La modularidad

Para que sea realmente modular, evitaría una clase gigantesca como:

```java
SistemaObjetosPerdidos.java
```

con 5000 líneas.

En su lugar:

```text
Usuarios
Objetos
Reclamos
Custodia
QR
Avisos
Indicadores
```

cada módulo debería tener su propia lógica.

Conceptualmente:

```text
                    ┌─────────────┐
                    │   USUARIOS  │
                    └──────┬──────┘
                           │
┌──────────┐        ┌──────▼──────┐        ┌───────────┐
│ OBJETOS  │◄──────►│    NÚCLEO   │◄──────►│ RECLAMOS  │
└────┬─────┘        └──────┬──────┘        └─────┬─────┘
     │                     │                     │
     │              ┌──────▼──────┐              │
     │              │   AVISOS    │              │
     │              └─────────────┘              │
     │                                           │
┌────▼─────┐                              ┌──────▼─────┐
│    QR    │                              │ CUSTODIA   │
└──────────┘                              └────────────┘
```

---

# 34. Dependencias entre los integrantes

La interacción principal sería:

```text
                    OSCAR
              JavaFX / FXML / CSS
                     ↑
                     │
               Controllers
                     │
          ┌──────────┴──────────┐
          │                     │
       ADRIAN                  HAN
 Usuarios/Reclamos       Objetos/QR/Avisos
          │                     │
          └──────────┬──────────┘
                     ↓
                   RENATO
             Repository / JDBC
                     ↓
                 SQL Server
```

Pero no significa que Renato sea un cuello de botella.

Los contratos deben definirse primero.

---

# 35. Contratos al inicio

Antes de cada sprint deberían acordar interfaces.

Por ejemplo:

```java
public interface ObjetoRepository {

    Optional<Objeto> findById(int id);

    List<Objeto> findAll();

    List<Objeto> buscar(FiltroObjeto filtro);

    void save(Objeto objeto);

}
```

Han puede desarrollar:

```java
ObjetoService
```

usando esa interfaz incluso aunque Renato todavía esté terminando:

```java
SqlServerObjetoRepository
```

Para pruebas puede existir:

```java
FakeObjetoRepository
```

Eso es modularidad real.

---

# 36. Cómo dividir el trabajo sin bloquearse

Por ejemplo, para **publicar un objeto**:

### Oscar

Hace:

```text
publicar-objeto.fxml
```

con:

```text
Título
Descripción
Categoría
Lugar
Fecha
Fotografía

[Publicar]
```

### Han

Hace:

```java
PublicarObjetoController
ObjetoService
ImageService
```

### Renato

Hace:

```text
tabla objeto
tabla foto
ObjetoRepository
FotoRepository
```

Resultado:

```text
Oscar
  ↓
FXML
  ↓
Han
Controller + Service
  ↓
Renato
Repository + SQL Server
```

Tres personas trabajan sobre la misma funcionalidad simultáneamente.

---

# 37. Otro ejemplo: aprobar reclamo

### Oscar

```text
reclamos-admin.fxml

[ Aprobar ]
[ Rechazar ]
```

### Adrian

```java
ReclamoService.aprobarReclamo()
```

### Renato

```java
ReclamoRepository.actualizarEstado()
```

### Han

Cuando Adrian aprueba:

```text
AvisoService.crearAviso()
```

El usuario recibe:

```text
Tu reclamo ha sido aprobado.
```

Los cuatro pueden participar en una sola funcionalidad sin mezclar responsabilidades.

---

# 38. Git

Todos deberían usar el mismo repositorio.

Yo utilizaría:

```text
main
│
└── develop
      │
      ├── feature/login
      ├── feature/catalogo
      ├── feature/reclamos
      ├── feature/database
      └── feature/indicadores
```

No recomiendo hacer:

```text
rama-oscar
rama-renato
rama-adrian
rama-han
```

durante todo el proyecto.

Es mejor que las ramas representen **funcionalidades**, no personas.

Por ejemplo Oscar y Adrian pueden trabajar juntos en:

```text
feature/login
```

Oscar modifica:

```text
login.fxml
app.css
```

Adrian:

```text
LoginController.java
AuthService.java
```

Hay muy pocas probabilidades de conflicto.

---

# 39. Primer sprint recomendado

Al principio no intenten desarrollar M1–M7 simultáneamente.

Primero deberían conseguir esto:

```text
SQL Server
    ↓
JDBC
    ↓
Login
    ↓
Menú
    ↓
Publicar objeto
    ↓
Ver catálogo
```

Distribución:

**Oscar**

```text
login.fxml
registro.fxml
menu.fxml
catalogo.fxml
publicar-objeto.fxml
```

**Renato**

```text
BD
Usuario
Objeto
JDBC
repositories
```

**Adrian**

```text
registro
login
roles
sesión
```

**Han**

```text
crear objeto
listar objetos
filtros básicos
```

Cuando termine ese sprint ya tendrán una aplicación funcional de extremo a extremo:

```text
JavaFX
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
JDBC
   ↓
SQL Server
```

Ese debería ser su primer gran objetivo.

---

## Resumen de responsabilidades

| Integrante | Hace principalmente | No debería encargarse principalmente de |
|---|---|---|
| **Oscar** | Scene Builder, FXML, CSS, JavaFX, tablas, formularios, navegación, UX | SQL y lógica de negocio |
| **Renato** | SQL Server, JDBC, Repository, modelos, índices, scripts y seed | Diseño visual |
| **Adrian** | Usuarios, autenticación, roles, sesión, reclamos, custodia y seguridad | Diseño SQL o interfaces completas |
| **Han** | Objetos, búsquedas, imágenes, QR, avisos e indicadores | Diseño general de BD |

Y la idea central del proyecto debería ser esta:

> **Oscar construye cómo se ve; Adrian y Han construyen qué hace; Renato construye cómo se persiste.**

Pero todos se conectan mediante interfaces bien definidas:

```text
FXML
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
SQL Server
```

Esa separación encaja muy bien con **JavaFX 21 + Scene Builder + IntelliJ + Maven + SQL Server** y les permitiría demostrar al profesor no solo que el programa funciona, sino que tiene una **arquitectura mantenible, modular y claramente dividida entre los cuatro integrantes**.