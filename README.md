# Active Passive Math AWS

Proyecto de referencia para investigar un esquema de microservicios con **proxy con algoritmo round-robin** desplegado en **AWS EC2**.

La solución está compuesta por:

- `math-service`: microservicio Spring Boot que expone funciones numéricas (p.ej. secuencia de Fibonacci) vía REST.
- `proxy-service`: microservicio Spring Boot que actúa como **proxy con balanceo round-robin**, reenviando las peticiones a dos instancias de `math-service` desplegadas en EC2.
- Cliente web: página HTML5 + JavaScript (sin librerías externas) servida por `proxy-service`, que invoca el proxy de forma asíncrona.

Tecnologías principales: **Java 17**, **Spring Boot**, **Maven**, **HTML5/JS**, **Git/GitHub**, **AWS EC2**.

---

## Arquitectura

1. El usuario abre el cliente web (HTML + JS) servido por `proxy-service`.
2. El formulario envía, de forma **asíncrona (AJAX)**, la petición al endpoint REST del proxy (por ejemplo `GET /api/fibonacci?terms=10`).
3. `proxy-service` reenvía la petición a una de las instancias de `math-service` usando un **algoritmo de round-robin**: la primera petición va a la instancia 1, la segunda a la 2, la tercera de nuevo a la 1, y así sucesivamente.
4. Si la instancia seleccionada no responde (caída, timeout, error de red), el proxy intenta con la siguiente instancia disponible.
5. El resultado devuelto por `math-service` es regresado al cliente web y renderizado en la página.

Despliegue recomendado en AWS EC2:

- EC2 #1: `math-service` (instancia 1).
- EC2 #2: `math-service` (instancia 2).
- EC2 #3: `proxy-service` + cliente web.

Opcionalmente, puede haber un servidor web (Apache/NGINX) frontal en la misma máquina del proxy para manejar **HTTPS (Let’s Encrypt)** y hacer reverse proxy hacia el puerto de Spring Boot.

---

## Servicios y API

### 1. `math-service`

Aplicación Spring Boot que expone, entre otros, el endpoint de Fibonacci:

- **GET** `/api/fibonacci?terms={n}`
  - Parámetros:
	 - `terms` (int, obligatorio): número de términos de la secuencia.
  - Respuesta: lista JSON con los primeros `n` términos de Fibonacci.

Ejemplo de respuesta para `terms=5`:

```json
[0, 1, 1, 2, 3]
```

### 2. `proxy-service`

Expone un endpoint REST que el cliente web consume y que, internamente, reenvía la petición a las instancias de `math-service`.

- **GET** `/api/fibonacci?terms={n}`
  - El proxy llama a `math-service` activo; si falla, llama al pasivo.
  - Devuelve al cliente exactamente la misma estructura que entregaría `math-service`.

#### Variables de entorno (round-robin)

Las URLs de las instancias de `math-service` se configuran mediante variables de entorno en el sistema operativo de la máquina donde corre `proxy-service`:

- Opción 1 (dos variables):
	- `MATH_SERVICE_URL_1` – URL base de la instancia 1 (por ejemplo `http://10.0.1.10:8080`).
	- `MATH_SERVICE_URL_2` – URL base de la instancia 2 (por ejemplo `http://10.0.2.20:8080`).
- Opción 2 (lista separada por comas):
	- `MATH_SERVICE_URLS` – lista de URLs, por ejemplo `http://10.0.1.10:8080,http://10.0.2.20:8080`.

El proxy recorre estas URLs en orden usando round-robin y construye la URL final así:

```text
<BASE_URL>/api/fibonacci?terms={n}
```

De esta forma, cambiar la instancia activa o pasiva no requiere recompilar el código, solo actualizar las variables de entorno.

### 3. Cliente web HTML + JS

El archivo `proxy-service/src/main/resources/static/index.html` contiene:

- Un formulario HTML para ingresar el valor `terms`.
- Código JavaScript básico (`XMLHttpRequest` o `fetch`) para invocar asíncronamente `GET /api/fibonacci` del **proxy**.
- Render del resultado en el DOM (p.ej. lista con los términos de la secuencia).

---

## Ejecución local

Requisitos previos en tu máquina local:

- Java 17 (por ejemplo, Amazon Corretto 17).
- Maven 3.x.

### 1. Clonar el repositorio

```bash
git clone <URL_DE_TU_REPOSITORIO_GITHUB>
cd active-passive-full
```

### 2. Compilar todo el proyecto

```bash
mvn -pl math-service,proxy-service clean package
```

### 3. Ejecutar `math-service` localmente

```bash
cd math-service
mvn spring-boot:run
```

# Active Passive Math AWS

Proyecto sencillo para investigar un esquema de microservicios con **proxy round-robin** desplegado en **AWS EC2**.

Componentes:

- `math-service`: microservicio Spring Boot que expone una función numérica (Fibonacci) vía REST.
- `proxy-service`: microservicio Spring Boot que actúa como **proxy** y reparte las peticiones entre dos instancias de `math-service` usando round-robin.
- Cliente web: página HTML5 + JavaScript (sin librerías externas) servida por `proxy-service`.

Tecnologías: **Java 17**, **Spring Boot**, **Maven**, **HTML5/JS**, **AWS EC2**.

---

## Arquitectura y API

1. El usuario abre el cliente web (HTML + JS) en `proxy-service`.
2. El formulario invoca, de forma asíncrona (AJAX), `GET /api/fibonacci?terms=n` en el proxy.
3. `proxy-service` reenvía la petición a una de las instancias de `math-service` usando round-robin.
4. Si la instancia elegida falla, prueba con la otra; si ambas fallan, devuelve error.
5. La respuesta JSON se muestra en la página.

### `math-service`

- **GET** `/api/fibonacci?terms={n}` → devuelve JSON con los primeros `n` términos.

Ejemplo (`terms=5`):

```json
[0, 1, 1, 2, 3]
```

### `proxy-service`

- **GET** `/api/fibonacci?terms={n}` → el proxy llama a una instancia de `math-service` y devuelve su respuesta.

Variables de entorno para las instancias de `math-service`:

- `MATH_SERVICE_URL_1` – por ejemplo `http://10.0.1.10:8080`
- `MATH_SERVICE_URL_2` – por ejemplo `http://10.0.2.20:8080`

El proxy construye la URL como:

```text
<MATH_SERVICE_URL_X>/api/fibonacci?terms={n}
```

### Cliente web (HTML + JS)


---

## Ejecución local (resumen)

Requisitos: Java 17 y Maven.

```bash
git clone <URL_DE_TU_REPOSITORIO_GITHUB>
cd active-passive-full

# Compilar servicios
mvn -pl math-service,proxy-service clean package

# Terminal 1: math-service
cd math-service
mvn spring-boot:run

# Terminal 2: proxy-service
cd proxy-service
export MATH_SERVICE_URL_1=http://localhost:8080
export MATH_SERVICE_URL_2=http://localhost:8080
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

Luego abre en el navegador:

```text
http://localhost:8081/
```

---

## Despliegue básico en AWS EC2

### math-service (dos instancias)

En cada instancia EC2 para `math-service`:

```bash
sudo yum update -y
sudo yum install -y java-17-amazon-corretto-headless maven git

git clone <URL_DE_TU_REPOSITORIO_GITHUB>
cd active-passive-full/math-service
mvn package
java -jar target/math-service-0.0.1-SNAPSHOT.jar
```

### proxy-service (otra instancia)

```bash
sudo yum update -y
sudo yum install -y java-17-amazon-corretto-headless maven git

git clone <URL_DE_TU_REPOSITORIO_GITHUB>
cd active-passive-full/proxy-service
mvn package

export MATH_SERVICE_URL_1=http://<IP_PRIVADA_MATH_1>:8080
export MATH_SERVICE_URL_2=http://<IP_PRIVADA_MATH_2>:8080
java -jar target/proxy-service-0.0.1-SNAPSHOT.jar --server.port=8081
```

Prueba desde tu navegador con la IP pública del proxy:

```text
http://<IP_PUBLICA_PROXY>:8081/
```

---

## Entregables

- Código en GitHub (este repositorio).
- Pantallazos del cliente web y de las instancias EC2 corriendo.
- Enlace a un video corto (≤ 1 min) mostrando el flujo cliente → proxy → servicios numéricos.
```


