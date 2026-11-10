# GlucoAPI - API HealthBlood

API REST en Java (Jersey + Tomcat 11) para gestionar usuarios y medidas de glucosa.

La aplicación corre en **un solo contenedor**: Tomcat + MariaDB. El esquema de la
base de datos (`dist/GlucoAPI.sql`) se aplica automáticamente al arrancar el
contenedor.

## Endpoints

La base de la API es `/api/glucosa`:

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/glucosa/saludar` | Prueba de vida |
| GET | `/api/glucosa/getAllUsuarios` | Lista de usuarios |
| POST | `/api/glucosa/insertUsuario` | Crea un usuario (`application/x-www-form-urlencoded`) |
| GET | `/api/glucosa/getAllMedidas` | Lista de medidas |
| POST | `/api/glucosa/insertMedida` | Crea una medida (`application/x-www-form-urlencoded`) |
| GET | `/api/glucosa/getMedida?idMedida=1` | Consulta una medida |

Ejemplo de inserción de usuario:

```bash
curl -X POST "http://localhost:8080/api/glucosa/insertUsuario" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "nombre=Arturo" \
  --data-urlencode "primerApellido=Del Rio" \
  --data-urlencode "segundoApellido=Rayas" \
  --data-urlencode "email=aaa@gmail.com"
```

## Ejecutar en local con Docker

Desde la raíz del repositorio:

```bash
docker compose -f dist/docker-compose.yml up --build
```

La API queda disponible en `http://localhost:8080/api/glucosa/saludar`.

El volumen `mysql_data` conserva la base de datos entre reinicios locales.
Para borrar todo y empezar de cero:

```bash
docker compose -f dist/docker-compose.yml down -v
```

## Desplegar en Render

1. Sube los cambios al repositorio de GitHub.
2. En Render, crea un **Web Service** con:
   - **Language:** Docker
   - **Dockerfile Path:** `dist/Dockerfile`
   - **Health Check Path:** `/api/glucosa/saludar`
3. No necesitas configurar variables de entorno: el Dockerfile trae valores por
   defecto (`DB_NAME=gluco_api`, `DB_USER=gluco`, `DB_PASS=gluco`).
   Si quieres cambiar la contraseña, define `DB_PASS` en Render.
4. Deploy.

El Dockerfile compila el WAR desde `src/java` en cada build, así que Render
siempre usa el código actual. Ya no depende del WAR precompilado
`dist/API_HealthBlood.war`.

## Importante sobre Render Free

En el plan **Free**, Render usa un filesystem efímero: la base de datos MariaDB
se reinicia vacía en cada redeploy, reinicio o cuando el servicio se duerme por
inactividad. Para una demo funciona perfecto; si necesitas conservar datos,
opciones:

- Usar un plan pagado de Render con un disco persistente montado en
  `/var/lib/mysql`.
- Cambiar a un MySQL externo (Aiven, Railway, etc.) y configurar `DB_HOST`,
  `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS` y `DB_SSL_MODE=REQUIRED`.

## Estructura relevante

- `src/java/org/lasalle/rest/RestGlucosa.java`: endpoints REST.
- `src/java/org/lasalle/controller/ConnectionMysql.java`: conexión JDBC por
  variables de entorno.
- `dist/Dockerfile`: build multi-stage (WAR + Tomcat + MariaDB).
- `docker/entrypoint.sh`: arranca MariaDB, crea la base/usuario y aplica
  `GlucoAPI.sql`.
- `dist/GlucoAPI.sql`: esquema idempotente (`CREATE TABLE IF NOT EXISTS`).
- `lib/`: JARs de runtime usados por el build de Docker y por NetBeans.

## Compilar el WAR con NetBeans

El proyecto sigue siendo un proyecto Ant de NetBeans. Las dependencias ahora
están en `lib/` (rutas relativas), así que **Clean and Build** funciona sin
depender de rutas absolutas de Windows. El WAR se genera en
`dist/API_HealthBlood.war`.
