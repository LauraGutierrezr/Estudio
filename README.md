# Servicio de Números de Catalan

Aplicación Spring Boot que expone un servicio REST para calcular los **Números de Catalan** desde $C_0$ hasta $C_n$ usando programación dinámica y aritmética entera.

Tecnologías: **Java 17**, **Spring Boot**, **Maven**.

---

## API

- **Método HTTP**: `GET`
- **Endpoint**: `/catalan`
- **Parámetro de query**: `value` (entero, no negativo)

Ejemplo de llamado (local):

```bash
curl "http://localhost:8080/catalan?value=10"
```

Ejemplo de salida (JSON):

```json
{
  "operation": "Secuencia de Catalan",
  "input": 10,
  "output": "1, 1, 2, 5, 14, 42, 132, 429, 1430, 4862, 16796"
}
```

Campo `output`: contiene la lista de valores $C_0, C_1, ..., C_n$ separados por comas.

---

## Implementación (resumen)

- Se implementa la recurrencia:
  - $C_0 = 1$
  - Para $n \ge 1$: $C_n = \sum_{i=0}^{n-1} C_i \cdot C_{n-1-i}$
- Se usa un arreglo de `BigInteger` para almacenar los valores $C_0..C_n$ y evitar desbordamientos.
- No se usan funciones de librería que calculen Catalan ni combinatorias cerradas.

Toda la lógica está en `math-service` en el controlador `MathController`.

---

## Ejecución local

Requisitos: Java 17 y Maven.

```bash
git clone <URL_DE_TU_REPOSITORIO_GITHUB>
cd active-passive-full/math-service

mvn spring-boot:run
```

El servicio quedará disponible en:

```text
http://localhost:8080/catalan?value=10
```

---

## Ejemplo en EC2

Desplegado en una instancia EC2, la llamada tendría la forma:

```text
https://amazonxxx.x.xxx.x.xxx:{port}/catalan?value=10
```

Devolviendo el mismo JSON mostrado en la sección de API.
```


