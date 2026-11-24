# The Coffee Club — Sistema de ventas y administración

Proyecto en **Servlets + JSP (JSTL) + DAO + JDBC + MySQL**, con la misma estructura del proyecto de referencia (gestorEventos).

## Requisitos
- JDK 17+, Maven 3.9+, Apache Tomcat 10.1+ (Jakarta EE 10), MySQL 8

## Puesta en marcha
1. Ejecuta `database/coffeeclub.sql` en MySQL (crea la BD `coffeeclub` con datos de ejemplo).
2. Ajusta usuario/contraseña en `src/main/resources/db.properties`.
3. `mvn clean package` y despliega `target/coffeeclub.war` en Tomcat.
4. Abre `http://localhost:8080/coffeeclub/`

## Cuentas de prueba
| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@coffeeclub.com | Admin1234 |
| Empleado | sofia@coffeeclub.com | Empleado1 |

## Estructura
- `controller/` Servlets (login, venta, usuarios, productos, reportes, perfil) y filtro de autenticación/rol.
- `model/dao/` DAO con JDBC y PreparedStatement; `model/models/` entidades.
- `WEB-INF/views/` JSP (no accesibles directamente); `css/style.css` con tema claro/oscuro; `js/` carrito del POS y tema.

## Notas
- Contraseñas con PBKDF2; la venta se guarda en una transacción.
- Tema claro/oscuro con el botón de la barra superior.
- Pantallas basadas en el Figma; ventanas emergentes, modo oscuro y perfil de admin se derivaron del diseño sin verlos todos (límite del plan de Figma).
- No se pudo compilar con Maven en mi entorno (sin acceso a Maven Central); el Java se verificó con javac. Si Maven marca algo, avísame.
