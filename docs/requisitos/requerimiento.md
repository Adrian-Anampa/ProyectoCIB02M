# Requerimientos d

## 1. Alcance y responsabilidad

Oscar será responsable de la capa visual del sistema de objetos perdidos y encontrados. Su trabajo comprende las interfaces JavaFX, los archivos FXML, los estilos CSS, la navegación, la presentación de información y la experiencia de usuario.

Trabajará principalmente en:

```text
src/main/resources/fxml/
src/main/resources/css/
src/main/resources/images/
```

También podrá colaborar en la parte estrictamente visual de los controladores. No será responsable de implementar consultas SQL, persistencia, autenticación, reglas de negocio ni almacenamiento de imágenes.

## 2. Requerimientos funcionales

| ID | Requerimiento funcional |
|---|---|
| RF-OSC-01 | El sistema deberá mostrar una pantalla de inicio de sesión con campos para correo y contraseña, un botón para iniciar sesión y un acceso a la pantalla de registro. |
| RF-OSC-02 | La contraseña deberá mostrarse en un campo protegido y podrá incluir una opción para visualizarla temporalmente. |
| RF-OSC-03 | El sistema deberá mostrar un formulario de registro con los campos definidos por el módulo de usuarios. |
| RF-OSC-04 | La interfaz deberá señalar visualmente los campos obligatorios, incompletos o inválidos. |
| RF-OSC-05 | El sistema deberá permitir la navegación entre las pantallas de inicio de sesión, registro y menú principal. |
| RF-OSC-06 | El menú principal deberá mostrar las opciones disponibles según el rol del usuario: estudiante o administrador. La determinación y validación del rol corresponderán al módulo de autenticación. |
| RF-OSC-07 | La interfaz deberá proporcionar acciones consistentes para regresar, volver al menú principal y cerrar sesión. |
| RF-OSC-08 | El catálogo deberá mostrar los objetos encontrados mediante tarjetas, filas o tablas que incluyan fotografía, título, categoría, lugar, fecha y estado. |
| RF-OSC-09 | El catálogo deberá presentar controles de búsqueda y filtrado por palabra clave, categoría, lugar, fecha y estado. |
| RF-OSC-10 | La interfaz deberá permitir abrir el detalle de un objeto seleccionado. |
| RF-OSC-11 | La pantalla de detalle deberá mostrar la fotografía, descripción, categoría, lugar, fecha de hallazgo, estado y acciones disponibles para el usuario. |
| RF-OSC-12 | El sistema deberá proporcionar un formulario para publicar un objeto encontrado con título, descripción, categoría, lugar, fecha y fotografía. |
| RF-OSC-13 | El formulario de publicación deberá incluir un control para seleccionar una imagen y un espacio para mostrar su vista previa. La validación y el almacenamiento de la imagen corresponderán al módulo de objetos. |
| RF-OSC-14 | La interfaz deberá mostrar una imagen predeterminada cuando el objeto no tenga una fotografía disponible. |
| RF-OSC-15 | El sistema deberá mostrar al estudiante una pantalla con sus reclamos y el estado de cada uno: pendiente, aprobado o rechazado. |
| RF-OSC-16 | El usuario deberá poder abrir el detalle de un reclamo para consultar el objeto relacionado, la fecha, el estado y la respuesta administrativa. |
| RF-OSC-17 | La interfaz de custodia deberá mostrar la pregunta de verificación y un campo para que el reclamante ingrese su respuesta. |
| RF-OSC-18 | El sistema deberá mostrar una pantalla de avisos con el mensaje, fecha, objeto relacionado y estado de lectura de cada aviso. |
| RF-OSC-19 | La interfaz deberá diferenciar visualmente los avisos leídos de los avisos pendientes. |
| RF-OSC-20 | El sistema deberá mostrar el código QR generado para un objeto junto con su identificador. La generación del código corresponderá al módulo de QR. |
| RF-OSC-21 | El administrador deberá disponer de un panel visual para acceder a reclamos, objetos, indicadores y bitácora. |
| RF-OSC-22 | La pantalla administrativa de reclamos deberá mostrar los reclamos pendientes y controles para aprobarlos o rechazarlos. |
| RF-OSC-23 | La interfaz deberá solicitar confirmación antes de ejecutar acciones importantes, como aprobar, rechazar, publicar o confirmar una devolución. |
| RF-OSC-24 | El panel de indicadores deberá presentar estadísticas mediante tarjetas informativas y gráficos JavaFX, como `BarChart`, `PieChart` o `LineChart`. |
| RF-OSC-25 | La interfaz de bitácora deberá permitir visualizar las acciones registradas, incluyendo usuario, acción, fecha y elemento relacionado. |
| RF-OSC-26 | El sistema deberá mostrar mensajes visuales de éxito, advertencia y error después de cada operación. |
| RF-OSC-27 | La interfaz deberá mostrar indicadores de carga mientras se realizan consultas o procesos que puedan tardar. |
| RF-OSC-28 | Las listas, tablas y catálogos deberán mostrar un mensaje comprensible cuando no existan resultados. |
| RF-OSC-29 | Los controles definidos en FXML deberán exponer los `fx:id` y eventos acordados con los controladores desarrollados por Adrian y Han. |
| RF-OSC-30 | Las interfaces deberán poder construirse y probarse inicialmente con datos simulados para no depender de que los servicios y la base de datos estén terminados. |

## 3. Requerimientos no funcionales

| ID | Requerimiento no funcional |
|---|---|
| RNF-OSC-01 | Las interfaces deberán ser compatibles con Java 21, JavaFX 21, FXML y Scene Builder. |
| RNF-OSC-02 | Todos los archivos FXML deberán poder abrirse y modificarse correctamente desde Scene Builder. |
| RNF-OSC-03 | La interfaz no deberá ejecutar consultas SQL ni acceder directamente a SQL Server. |
| RNF-OSC-04 | La comunicación deberá respetar la arquitectura `FXML → Controller → Service → Repository → SQL Server`. |
| RNF-OSC-05 | Los archivos deberán organizarse en carpetas separadas para FXML, CSS e imágenes. |
| RNF-OSC-06 | Los estilos visuales deberán centralizarse principalmente en archivos CSS, evitando repetir estilos dentro de cada archivo FXML. |
| RNF-OSC-07 | Todas las pantallas deberán mantener una identidad visual uniforme en colores, tipografía, botones, campos, tablas, iconos y espaciados. |
| RNF-OSC-08 | La interfaz deberá adaptarse al redimensionamiento normal de una aplicación de escritorio sin superponer, cortar o deformar los controles importantes. |
| RNF-OSC-09 | Las operaciones lentas deberán ejecutarse fuera del JavaFX Application Thread para evitar que la interfaz se congele. |
| RNF-OSC-10 | Los mensajes de error deberán ser comprensibles para el usuario y no deberán mostrar excepciones, consultas SQL ni información técnica interna. |
| RNF-OSC-11 | La navegación deberá ser intuitiva y mantener las acciones principales visibles y claramente identificadas. |
| RNF-OSC-12 | Los formularios deberán permitir desplazarse entre campos mediante el teclado. |
| RNF-OSC-13 | Cada campo deberá tener una etiqueta descriptiva; no se deberá utilizar únicamente el `promptText` para identificarlo. |
| RNF-OSC-14 | Los colores deberán tener suficiente contraste y no deberán ser el único medio para comunicar errores o estados. |
| RNF-OSC-15 | Los componentes repetidos, como encabezados, menús, tarjetas y diálogos, deberán reutilizar estilos o componentes comunes. |
| RNF-OSC-16 | Los nombres de archivos, `fx:id`, clases CSS y métodos de eventos deberán seguir una convención uniforme y descriptiva. |
| RNF-OSC-17 | Cada pantalla deberá cargarse sin errores de `FXMLLoader`, referencias inexistentes o incompatibilidades con su controlador. |
| RNF-OSC-18 | La capa visual deberá poder probarse con servicios simulados o datos falsos, sin requerir permanentemente una conexión activa con SQL Server. |
| RNF-OSC-19 | Los recursos gráficos deberán cargarse desde rutas relativas del proyecto para que funcionen al generar el ejecutable. |
| RNF-OSC-20 | La interfaz deberá ocultar o deshabilitar las opciones no autorizadas según el rol recibido. La autorización definitiva también deberá validarse en la lógica de negocio. |

## 4. Entregables

### Primer sprint

1. `login.fxml`
2. `registro.fxml`
3. `menu-principal.fxml`
4. `catalogo.fxml`
5. `publicar-objeto.fxml`
6. `app.css`
7. Navegación básica entre las pantallas.
8. Validaciones y mensajes visuales.
9. Datos simulados para probar el catálogo y los formularios.

### Siguientes entregas

- `detalle-objeto.fxml`
- `mis-reclamos.fxml`
- `detalle-reclamo.fxml`
- `custodia.fxml`
- `avisos.fxml`
- `qr.fxml`
- `admin-panel.fxml`
- `reclamos-admin.fxml`
- `bitacora.fxml`
- `indicadores.fxml`

## 5. Límites de la responsabilidad

Se construirá cómo se ve y cómo se recorre la aplicación. La conexión de las interfaces con la funcionalidad deberá realizarse mediante contratos acordados con el resto del equipo:

- Adrian: autenticación, usuarios, roles, sesión, reclamos, custodia y seguridad.
- Han: objetos, búsqueda, imágenes, QR, avisos e indicadores.
- Renato: modelos, repositorios, JDBC, SQL Server y scripts de base de datos.

La interfaz nunca deberá comunicarse directamente con la base de datos.
