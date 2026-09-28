# Sistema de Paqueteria - Programacion I

Proyecto academico en Java para administrar envios, clientes, estados y despacho de paquetes. La implementacion aplica los conceptos trabajados en clase: clases abstractas, interfaces, excepciones y framework de colecciones.

## Requisitos

- Java JDK 17 o superior.
- Maven 3.8+ (opcional; el proyecto tambien contiene scripts para compilar con `javac`).

## Ejecucion rapida sin Maven

### Windows
1. Abrir una consola en la carpeta del proyecto.
2. Ejecutar `run.bat`.

### Linux/macOS
1. Dar permisos: `chmod +x run.sh`.
2. Ejecutar `./run.sh`.

## Ejecucion con Maven

```bash
mvn clean compile
mvn exec:java
```

## Funcionalidades principales

- Registrar envios estandar y express.
- Generar codigo unico de rastreo.
- Buscar un envio por codigo.
- Actualizar estado del envio con validaciones.
- Despachar paquetes siguiendo una cola FIFO.
- Listar y filtrar envios.
- Exportar el inventario a CSV.
- Cargar datos de demostracion.
- Manejar excepciones propias y de validacion.

## Estructuras de colecciones usadas

- `Map<String, Envio>` para localizar rapidamente los envios por codigo.
- `List<Envio>` para conservar el historial de registros.
- `Set<String>` para conservar ciudades destino sin repetir.
- `Queue<String>` (`ArrayDeque`) para la cola FIFO de despacho.

## Arquitectura

`ui` -> `service` -> `model`

Las excepciones estan en `exception` y las utilidades en `util`.

## Git

La entrega fue organizada en una rama llamada `entregable`.

Para subirla a un repositorio remoto:

```bash
git remote add origin URL_DEL_REPOSITORIO
git push -u origin entregable
```

## Documentacion

La documentacion completa esta en `docs/Manual_Usuario_y_Tecnico.pdf`.
