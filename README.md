# CriptoClave

Evaluador de contraseñas que comprueba si cada método de cifrado las fortalece o no.

Proyecto final de **Programación Funcional con Scala**, Ingeniería de Sistemas Computacionales, Universidad Privada Boliviana (UPB).

**Integrantes:** Jose Ignacio Miranda Funes e Ignacio Cardenas Olivares

---

## ¿Qué hace?

CriptoClave recibe una contraseña, calcula su nivel de seguridad con 7 reglas y permite transformarla con cinco métodos: **César, Vigenère, sustitución, Huffman y RSA**. El resultado se evalúa con el mismo medidor, y el sistema indica si la contraseña quedó más fuerte, más débil o igual.

El objetivo es mostrar que **transformar un texto no es lo mismo que protegerlo**. El puntaje mide la complejidad aparente (largo y tipos de caracteres), no la seguridad real: por ejemplo, Huffman y RSA suben el puntaje de `hola`, pero quien tenga el árbol o la clave privada recupera la contraseña.

## Requisitos

| Herramienta | Versión |
| --- | --- |
| JDK | 11 o superior (se recomienda 17) |
| sbt | 1.12.13 (definido en `project/build.properties`) |
| Scala | 3.3.7 (sbt la descarga sola) |
| Editor (opcional) | VS Code con la extensión Metals |

La primera compilación descarga las dependencias, así que necesita conexión a internet.

## Cómo ejecutarlo

```bash
git clone https://github.com/josenacho567-eng/criptoClave.git
cd criptoClave
sbt compile
sbt run
```

Se abrirá la ventana de CriptoClave.

Si usa VS Code, abra la carpeta del proyecto y ejecute `Metals: Import build` desde la paleta de comandos (`Ctrl+Shift+P`) antes de compilar.

> `sbt run` usa `run / fork := true`, por lo que la ventana corre en su propio proceso. Para cerrar la aplicación basta con cerrar la ventana.

## Cómo se usa

La barra lateral muestra los tres pasos del flujo.

1. **Evaluar clave.** Escriba una contraseña (puede mostrarla con el icono del ojo) y pulse *Evaluar*. Verá el puntaje de 0 a 100, el nivel (Débil, Medio o Fuerte), las reglas cumplidas y una sugerencia por cada regla que falta.
2. **Elegir método.** Seleccione César, Vigenère, Sustitución, Huffman, RSA o *Todos*. Complete el parámetro que corresponda:
   - César: desplazamiento (número entero).
   - Vigenère y Sustitución: llave (texto).
   - Huffman: no necesita parámetros.
   - RSA: usa valores predeterminados (p = 61, q = 53, e = 17).
3. **Comparar resultados.** Revise cuántas pruebas mejoraron o empeoraron, el mejor método y si alguno subió de nivel. Con *Ver detalle* se muestra la salida completa y el resultado de revertir, para comprobar que se recupera la contraseña original.

### Reglas de fortaleza (100 puntos)

| Regla | Puntos |
| --- | --- |
| Al menos 8 caracteres | 20 |
| Al menos 12 caracteres | 15 |
| Contiene mayúsculas | 15 |
| Contiene minúsculas | 10 |
| Contiene números | 15 |
| Contiene símbolos | 15 |
| No es una palabra común | 10 |

Niveles: menos de 40 es **Débil**, de 40 a 69 es **Medio** y 70 o más es **Fuerte**.

### Ejemplo con `hola`

| Método | Resultado | Puntaje | Veredicto |
| --- | --- | --- | --- |
| Original | `hola` | 10 (Débil) | |
| César (3) | `krod` | 20 | Mejoró +10 |
| Sustitución (Clave) | `fnj]` | 35 | Mejoró +25 |
| Vigenère (Clave) | `¬ÝÏÙ` | 40 (Medio) | Mejoró +30 |
| Huffman | `01001110` | 45 (Medio) | Mejoró +35 |
| RSA (61, 53, 17) | `2170 2185 745 1632` | 60 (Medio) | Mejoró +50 |

### Ejemplo con `Sol#2024`

| Método | Puntaje | Veredicto |
| --- | --- | --- |
| Original | 85 (Fuerte) | |
| César (3) | 85 | Igual |
| Vigenère (Clave) | 70 | Empeoró -15 |
| Sustitución (Clave) | 70 | Empeoró -15 |
| Huffman | 60 (Medio) | Empeoró -25 |
| RSA (61, 53, 17) | 60 (Medio) | Empeoró -25 |

## Estructura del proyecto

```
criptoClave/
├── build.sbt
├── project/
│   ├── build.properties
│   └── plugins.sbt
└── src/main/scala/criptoclave/
    ├── Main.scala              Arranca la ventana
    ├── modelo/                 Tipos del dominio
    ├── evaluacion/             Reglas y evaluador de contraseñas
    ├── cifrados/               Los cinco métodos y su punto de entrada
    ├── flujo/                  Transiciones puras del estado de la aplicación
    └── ui/                     Ventana, pantallas, componentes y tema
```

| Paquete | Contenido |
| --- | --- |
| `modelo` | `Regla[A]`, `ResultadoRegla`, `Evaluacion`, `Nivel`, `Metodo`, `Arbol[A]`, `DatosReversion`, `Cifrado`, `Prueba`, `Veredicto`, `Resultado[+A]`, `EstadoApp`, `Predeterminados` |
| `evaluacion` | `Reglas` (7 reglas y lista de palabras comunes) y `Evaluador` (puntaje, nivel y sugerencias) |
| `cifrados` | `Alfabetos`, `Cesar`, `Vigenere`, `Sustitucion`, `Huffman`, `RSA` y `Cifrador` (valida, cifra y revierte según el `Metodo`) |
| `flujo` | `Flujo`: evaluar, probar un método, probar todos, reiniciar y revertir |
| `ui` | `Ventana`, `PantallaEvaluar`, `PantallaMetodo`, `PantallaComparacion`, `BarraLateral`, `Componentes`, `Tema` y `Formato` |

Las dependencias van en una sola dirección:

```
ui → flujo → evaluacion
             cifrados
        ↘      ↓
          modelo
```

`modelo` no depende de nadie y solo `ui` conoce la librería gráfica.

## Diseño

- **Estado inmutable.** `EstadoApp` es un `sealed trait` con tres casos (`Inicio`, `Evaluada`, `ConPruebas`). Cada acción devuelve un estado nuevo y la pantalla se redibuja a partir de él. El único `var` del proyecto es el estado actual guardado en `Ventana`.
- **Errores como valores.** `Resultado[+A]` (`Exito` o `Fallo`) tiene `map`, `flatMap` y `getOrElse`, lo que permite encadenar pasos con `for`.
- **Funciones como datos.** Cada `Regla` guarda su condición `A => Boolean`, y `probarTodos` recibe un `criterio: Prueba => Int` para ordenar el ranking.
- **Datos de reversión.** `DatosReversion` guarda lo que cada método necesita para revertir: el árbol de Huffman (`ArbolHuffman`) o la clave privada de RSA (`ClavePrivada`). Los demás métodos guardan `None`.
- **Alfabeto de 188 caracteres** (códigos 33 a 126 y 161 a 255, sin el 173). Así un cifrado puede convertir una letra en dígito o símbolo y la comparación antes/después es informativa. Los caracteres fuera del alfabeto, como el espacio, pasan sin cambios.

### Conceptos de Scala aplicados

| Concepto | Dónde |
| --- | --- |
| `case class` y `case object` | `modelo/` |
| Jerarquías con `sealed trait` | `Metodo`, `EstadoApp`, `Nivel`, `Veredicto`, `Resultado`, `Arbol`, `DatosReversion` |
| Genéricos | `Regla[A]`, `Arbol[A]`, `Resultado[+A]` |
| Pattern matching | `Cifrador`, `Evaluador.clasificar`, `Flujo.baseDe`, `Huffman.generarCodigos`, `Formato` |
| `map`, `filter`, `flatMap`, `foldLeft`, `reduceOption`, `count`, `exists` | `Evaluador`, `Evaluacion`, `Flujo`, `EstadoApp`, `Huffman` |
| Funciones de orden superior | `Flujo.probarTodos`, `Regla.condicion`, `Resultado.map` y `flatMap` |
| `for` sobre `Resultado` | `Flujo.crearPrueba` |

## Tecnologías

- Scala 3.3.7
- sbt
- [scala-swing](https://github.com/scala/scala-swing) 3.0.0
- [FlatLaf](https://www.formdev.com/flatlaf/) 3.5.4

## Limitaciones

- El puntaje mide complejidad aparente, no seguridad real.
- RSA usa primos pequeños fijos y cifra carácter por carácter.
- Este proyecto es solo una demostración y no debe usarse para proteger información real.
