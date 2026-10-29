## Estructura del proyecto

```
criptoclave/
├── build.sbt
├── README.md
├── .gitignore
├── project/
│   └── build.properties
├── docs/
│   ├── propuesta.docx
│   ├── decisiones-de-diseno.md
│   └── ejemplos-de-ejecucion.md
└── src/
    ├── main/scala/criptoclave/
    │   ├── Main.scala
    │   ├── modelo/
    │   │   ├── Tipos.scala
    │   │   ├── Metodo.scala
    │   │   ├── Arbol.scala
    │   │   ├── Evaluacion.scala
    │   │   └── Estado.scala
    │   ├── evaluacion/
    │   │   ├── Reglas.scala
    │   │   └── Evaluador.scala
    │   ├── cifrados/
    │   │   ├── Alfabeto.scala
    │   │   ├── Cesar.scala
    │   │   ├── Vigenere.scala
    │   │   ├── Sustitucion.scala
    │   │   ├── Huffman.scala
    │   │   ├── Rsa.scala
    │   │   └── Cifrador.scala
    │   ├── flujo/
    │   │   └── Flujo.scala
    │   └── ui/
    │       ├── Ventana.scala
    │       ├── PantallaEvaluar.scala
    │       ├── PantallaMetodo.scala
    │       └── PantallaComparacion.scala
    └── test/scala/criptoclave/
        ├── evaluacion/EvaluadorSpec.scala
        ├── cifrados/
        │   ├── CesarSpec.scala
        │   ├── VigenereSpec.scala
        │   ├── SustitucionSpec.scala
        │   ├── HuffmanSpec.scala
        │   ├── RsaSpec.scala
        │   └── CifradorSpec.scala
        └── flujo/FlujoSpec.scala
```

## Qué contiene cada archivo

### Raíz y documentación

| Archivo | Contenido |
| --- | --- |
| `build.sbt` | Versión de Scala, dependencia de la UI (ScalaFX o Swing) y de tests (munit) |
| `README.md` | Cómo compilar y ejecutar, y quién hizo qué |
| `docs/decisiones-de-diseno.md` | Alfabeto de 94, RSA por carácter, `DatosReversion`, `Resultado` propio, el único estado mutable en la UI |
| `docs/ejemplos-de-ejecucion.md` | Salidas y capturas con `hola` y `Sol#2024` |

### `modelo/` (se escribe juntos)

| Archivo | Contenido |
| --- | --- |
| `Tipos.scala` | `Nivel` (Debil, Media, Fuerte), `Veredicto` (Mejoro, Empeoro, Igual), `Resultado[A]` (Exito, Fallo) |
| `Metodo.scala` | `sealed trait Metodo` con `Cesar`, `Vigenere`, `Sustitucion`, `Huffman` y `Rsa(p, q, e)` |
| `Arbol.scala` | `Arbol[A]` con `Hoja` y `Nodo` |
| `Evaluacion.scala` | `Regla[A]`, `ResultadoRegla`, `Evaluacion` (con métodos `cumplidas` y `sugerencias`) |
| `Estado.scala` | `DatosReversion` (`ArbolHuffman`, `ClavePrivada`), `Prueba` (con `veredicto`) y `EstadoApp` (`Inicio`, `Evaluada`, `ConPruebas`) |

### `evaluacion/` (A)

| Archivo | Contenido |
| --- | --- |
| `Reglas.scala` | Lista de las 7 reglas y la lista de palabras comunes |
| `Evaluador.scala` | `evaluar`, puntaje con `foldLeft`, clasificación de nivel, sugerencias con `filter` y `map` |

### `cifrados/` (B, y A si toma César y Vigenère)

| Archivo | Contenido |
| --- | --- |
| `Alfabeto.scala` | Los 94 caracteres y helpers de índice |
| `Cesar.scala` | `aplicar` con `map`, `revertir` |
| `Vigenere.scala` | `aplicar` con `foldLeft`, `revertir` |
| `Sustitucion.scala` | Genera el alfabeto desordenado a partir de la clave, con `Map[Char, Char]` |
| `Huffman.scala` | Frecuencias con `groupBy`, construcción del árbol, tabla de códigos, decodificación recorriendo el árbol |
| `Rsa.scala` | Validación de primos y de `e`, cálculo de `n` y `d`, cifrado con `modPow` |
| `Cifrador.scala` | Punto de entrada: `aplicar` y `revertir` con pattern matching sobre `Metodo` |

### `flujo/` (A)

| Archivo | Contenido |
| --- | --- |
| `Flujo.scala` | `evaluarDesdeEstado`, `probar`, `probarTodos(criterio)` (la función de orden superior), `reiniciar`, y ranking con `reduce`, `count` y `exists` |

### `ui/` (ambos, al final)

| Archivo | Contenido |
| --- | --- |
| `Ventana.scala` | Ventana principal y el único `var` con el `EstadoApp` actual |
| `PantallaEvaluar.scala` | Pantalla 1: contraseña, puntaje, reglas, sugerencias |
| `PantallaMetodo.scala` | Pantalla 2: selector de método y parámetros |
| `PantallaComparacion.scala` | Pantalla 3: tabla de pruebas, resumen y botones |

### `Main.scala`
Arranca la ventana con `Inicio` como estado.

### `test/`
Un archivo por módulo. El más importante es la propiedad `revertir(aplicar(x)) == x` en cada `*Spec` de cifrados.

## Dependencias entre paquetes

```
ui → flujo → evaluacion
              cifrados
         ↘      ↓
            modelo
```

Cada flecha significa "puede importar a". `modelo` no importa a nadie, y `ui` es lo único que conoce la librería gráfica.

¿Quieres que genere ahora el esqueleto de la Fase 0 (`build.sbt`, `modelo/` completo y las firmas con `???`) para subirlo al repo?