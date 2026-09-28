# Planeación y estado de la fase inicial

## Resumen

La fase inicial preparó la interfaz Vue y un endpoint Spring Boot para iniciar sesión. El frontend y la API funcionan localmente en `http://localhost:5173` y `http://localhost:8080`. El login consulta MySQL, pero no hay usuarios en la tabla del contenedor activo, por lo que todavía no existe una cuenta válida para entrar.

**Estado global: parcial.** El flujo de login está implementado. Registro y recuperación no están conectados a rutas activas del backend. No se implementaron órdenes de reparación, vehículos, clientes ni persistencia de sesiones.

## Módulos y estado

| Módulo | Estado | Descripción |
|---|---|---|
| Interfaz de acceso | Implementado | Formulario de usuario, contraseña y selector de rol; envía JSON a `POST /api/login`, muestra errores/respuesta y guarda el resultado en `localStorage`. |
| API de autenticación | Implementado localmente | Spring Boot consulta el registro por nombre o correo, valida el hash BCrypt y compara el rol (`ADMINISTRADOR`, `GERENTE`, `CLIENTE`). Responde `200`, `400`, `401` o `500` según el caso. |
| Registro de usuarios | Incompleto | Existe una vista Vue, pero el controlador que ejecuta la aplicación `com.taller.auth.ApiApplication` no expone `POST /api/registro`. La vista tampoco envía correo, necesario en el esquema MySQL observado. |
| Recuperación de acceso | Incompleto | Existe una vista Vue, pero el controlador activo no expone `POST /api/recuperar`; no se implementó un mecanismo de verificación ni cambio de contraseña. |
| Órdenes de reparación | No iniciado | No hay vistas, endpoints ni modelos de órdenes en esta fase. |

### Datos y contrato del login

El frontend envía este JSON a `POST http://localhost:8080/api/login`:

```json
{
  "usuario": "nombre o correo",
  "contrasena": "contraseña ingresada",
  "rol": "Administrador | Gerente | Cliente"
}
```

En la base de datos que estaba activa durante la revisión, `usuarios` tiene `id`, `nombre`, `email`, `password_hash`, `rol`, `creado_en` y `actualizado_en`. La API compara la contraseña con BCrypt y convierte el rol a mayúsculas para compararlo con el `ENUM` de MySQL. La respuesta exitosa incluye `id`, `usuario` (el nombre) y `rol`; una autenticación no válida responde `401`.

Esta estructura no coincide con la inicialmente descrita (`usuario`, `contrasena`). En el contenedor revisado, `SELECT nombre, rol FROM usuarios` no devolvió filas. Por eso `admin / mecanico123` no es una credencial disponible en esa instancia y el login la rechaza. No se agregaron ni modificaron registros durante la revisión.

## Archivos generados

### Backend

- `backend/src/main/java/com/taller/auth/ApiApplication.java`: punto de entrada Spring Boot usado para ejecutar la API activa.
- `backend/src/main/java/com/taller/auth/LoginController.java`: ruta `POST /api/login`, validación del payload, consulta a MySQL, comparación BCrypt, validación de rol y respuestas HTTP.
- `backend/src/main/resources/application.properties`: puerto y propiedades de conexión JDBC a MySQL.
- `backend/pom.xml`: dependencias Spring Boot Web/JDBC, BCrypt y conector MySQL; versión Java 21.
- `backend/src/main/java/com/taller/mecanico/controller/LoginController.java` y `backend/src/main/java/com/taller/mecanico/Application.java`: implementación previa con otro esquema. No son el controlador activo de `com.taller.auth.ApiApplication`; conviene retirar o consolidar esta implementación antes de cerrar la fase. Hay dos clases principales, por lo que `mvn package` requiere declarar explícitamente la principal.

### Frontend

- `frontend/src/views/Login.vue`: formulario de acceso y llamada `fetch` al endpoint.
- `frontend/src/views/Registro.vue`: formulario de alta; su contrato actual no coincide con el esquema activo ni con las rutas activas del backend.
- `frontend/src/views/Recuperar.vue`: formulario de solicitud de ayuda; falta integrar una ruta de recuperación segura.
- `frontend/src/main.js`: inicializa Vue Router y registra las rutas `/login`, `/registro` y `/recuperar`.
- `frontend/src/App.vue`: monta la vista activa del router.
- `frontend/src/style.css`: importa Tailwind CSS y define ajustes base.
- `frontend/vite.config.js`: plugins de Vue y Tailwind; servidor local en puerto `5173`.
- `frontend/package.json` y `frontend/package-lock.json`: dependencias Vue, Vue Router, Vite y Tailwind. Actualmente `package.json` solo declara `dev`; falta agregar el script `build` antes de publicar en un host estático.
- `frontend/index.html`: documento HTML inicial de Vite.

## Configuración y credenciales MySQL

La ubicación actual de la configuración es `backend/src/main/resources/application.properties`. Allí están `spring.datasource.url`, `spring.datasource.username` y `spring.datasource.password`, junto con `server.port`. Los valores presentes son solo para desarrollo local. No copies la contraseña en este documento, en capturas ni en variables del frontend.

Para despliegue, sustituye esos valores por variables de entorno del servicio backend, por ejemplo `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`. El frontend solo debe conocer la URL pública de la API, nunca credenciales de MySQL. La base local de Docker en `localhost:3306` no está disponible desde un proveedor externo: para producción se necesita una base MySQL administrada, como Amazon RDS for MySQL, o una instancia privada accesible por red segura. No publiques el puerto 3306 abierto a Internet.

## Publicación sugerida

### Frontend en Vercel

1. En GitHub, asegúrate de que el proyecto tenga un `frontend/package.json` con el script `"build": "vite build"`.
2. En Vercel, importa el repositorio y selecciona `frontend` como directorio raíz.
3. Configura instalación con `npm install`, compilación con `npm run build` y salida `dist`.
4. Cambia la URL fija `http://localhost:8080` de `Login.vue` por una variable `VITE_API_BASE_URL` que apunte a la API publicada.
5. Vercel documenta el despliegue de proyectos Vite en [Vite on Vercel](https://vercel.com/docs/frameworks/frontend/vite).

### Backend en AWS Elastic Beanstalk

1. Declara una única clase principal Spring Boot en `backend/pom.xml` y genera un JAR ejecutable con Maven (`mvn clean package`).
2. Crea un entorno de plataforma Java SE en Elastic Beanstalk y despliega el JAR.
3. Configura las variables `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` en el entorno. La URL debe apuntar a una base MySQL administrada y accesible desde la red del backend.
4. Configura la URL pública del frontend en CORS y la variable `VITE_API_BASE_URL` en Vercel; la API local solo permite actualmente los orígenes localhost.
5. Sigue la guía oficial de plataforma [Java SE en Elastic Beanstalk](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-se-platform.html) y la guía de [Amazon RDS for MySQL](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_MySQL.html).

También puede usarse otro proveedor que ejecute Java/Spring Boot y permita configurar variables secretas. Antes de producción hay que desplegar la base de datos, no conectar el backend alojado a `localhost` del equipo de desarrollo.

## Pendientes para cerrar la fase

1. Confirmar/crear las cuentas y los roles reales en la base correcta; actualmente la tabla observada no tiene filas.
2. Consolidar las dos clases principales y las dos implementaciones de `LoginController`; fijar explícitamente cuál se ejecuta.
3. Implementar `POST /api/registro` con los campos requeridos por MySQL y sin permitir que un usuario público se asigne rol Administrador o Gerente.
4. Implementar recuperación con verificación de identidad y flujo seguro de restablecimiento; no cambiar contraseñas solo con el nombre de usuario.
5. Mover secretos fuera de `application.properties` para publicación y rotarlos si alguna vez se subieron a un repositorio público.
6. Agregar `npm run build`, `VITE_API_BASE_URL` y configurar CORS para los dominios publicados.
7. Implementar y documentar los módulos del taller: clientes, vehículos, órdenes de reparación, estados, historial y permisos por rol.
