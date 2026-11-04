package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import java.awt.Color
import scala.swing.*

// Pantalla 3: resumen, lista de pruebas y opción de revertir cada una
object PantallaComparacion {

    private def claveOriginal(estado: EstadoApp): String = {
        Flujo.baseDe(estado) match {
            case Exito((clave, _)) => clave
            case Fallo(_)          => ""
        }
    }

    private def mostrarDetalle(prueba: Prueba, clave: String): Unit = {
        val revertido: String = Flujo.revertir(prueba) match {
            case Exito(texto)  => texto
            case Fallo(motivo) => s"No se pudo revertir: $motivo"
        }
        val coincide = if revertido == clave then "Sí" else "No"
        val texto =
            s"Método: ${Formato.nombreMetodo(prueba.metodo)}\n\n" +
            s"Salida completa:\n${prueba.salida}\n\n" +
            s"Al revertir se obtiene:\n$revertido\n\n" +
            s"¿Coincide con la clave original? $coincide"

        val area = new TextArea(texto, 14, 50)
        area.editable = false
        area.lineWrap = true
        area.wordWrap = true

        Dialog.showMessage(
            message = new ScrollPane(area).peer,
            title = "Detalle de la prueba",
            messageType = Dialog.Message.Plain
        )
    }

    private def tarjetaEstadistica(titulo: String, valor: String, color: Color, nota: String): Component = {
        Componentes.tarjeta(
            Componentes.columna(
                Componentes.texto(titulo, 13, false, Tema.textoSuave),
                Swing.VStrut(4),
                Componentes.texto(valor, 26, true, color),
                Swing.VStrut(2),
                Componentes.texto(nota, 12, false, Tema.textoSuave)
            ),
            16
        )
    }

    private def filaPrueba(prueba: Prueba, clave: String): Component = {
        val salida = Componentes.texto(Formato.resumir(prueba.salida, 44), 13, false, Tema.textoSuave)
        salida.font = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 13)

        val izquierda = Componentes.columna(
            Componentes.texto(Formato.nombreMetodo(prueba.metodo), 16, true, Tema.texto),
            Swing.VStrut(3),
            salida
        )

        val colorNivel = Tema.colorNivel(prueba.evaluacion.nivel)
        val chipNivel = Componentes.pildora(
            s"${prueba.evaluacion.puntaje} pts · ${Formato.nombreNivel(prueba.evaluacion.nivel)}",
            Tema.conAlfa(colorNivel, 30),
            colorNivel
        )

        val colorVeredicto = Tema.colorVeredicto(prueba.veredicto)
        val chipVeredicto = Componentes.pildora(
            s"${Formato.textoVeredicto(prueba.veredicto)} ${Formato.conSigno(prueba.diferencia)}",
            Tema.conAlfa(colorVeredicto, 30),
            colorVeredicto
        )

        val botonDetalle = Componentes.botonSecundario("Ver detalle") { mostrarDetalle(prueba, clave) }

        val derecha = Componentes.fila(FlowPanel.Alignment.Right, chipNivel, chipVeredicto, botonDetalle)

        Componentes.tarjeta(
            new BorderPanel {
                opaque = false
                layout(izquierda) = BorderPanel.Position.West
                layout(derecha) = BorderPanel.Position.East
            },
            12
        )
    }

    def crear(estado: EstadoApp, alProbarOtro: () => Unit, alReiniciar: () => Unit): Component = {
        val clave = claveOriginal(estado)

        val subtitulo: String = Flujo.baseDe(estado) match {
            case Exito((original, evaluacion)) =>
                s"Clave original: $original  ·  nivel ${Formato.nombreNivel(evaluacion.nivel)} (${evaluacion.puntaje} pts)"
            case Fallo(motivo) => motivo
        }

        val (nombreMejor, notaMejor): (String, String) = estado.mejorPrueba match {
            case Some(prueba) => (Formato.nombreCorto(prueba.metodo), s"${prueba.evaluacion.puntaje} puntos")
            case None         => ("—", "sin pruebas")
        }

        val total = estado.pruebas.length
        val subeDeNivel = estado.algunaSubeDeNivel

        val estadisticas = new GridPanel(1, 4) {
            opaque = false
            hGap = 16
            contents += tarjetaEstadistica("Mejoraron", estado.mejoran.toString, Tema.exito, s"de $total pruebas")
            contents += tarjetaEstadistica("Empeoraron", estado.empeoran.toString, Tema.peligro, s"de $total pruebas")
            contents += tarjetaEstadistica("Mejor método", nombreMejor, Tema.acento, notaMejor)
            contents += tarjetaEstadistica(
                "¿Subió de nivel?",
                if subeDeNivel then "Sí" else "No",
                if subeDeNivel then Tema.exito else Tema.textoSuave,
                "respecto a la clave original"
            )
        }

        val parteSuperior = Componentes.columna(
            Componentes.encabezado("Resultados", subtitulo),
            estadisticas,
            Swing.VStrut(22),
            Componentes.tituloSeccion("Pruebas realizadas"),
            Swing.VStrut(10)
        )

        val filas: List[Component] = estado.pruebas.map(prueba => filaPrueba(prueba, clave))
        val separadas: List[Component] = filas.flatMap(fila => List(fila, Swing.VStrut(12)))
        val lista = Componentes.columna(separadas*)

        val envoltorio = new BorderPanel {
            opaque = false
            layout(lista) = BorderPanel.Position.North
        }

        val desplazable = new ScrollPane(envoltorio)
        desplazable.border = Swing.EmptyBorder(0)
        desplazable.peer.setOpaque(false)
        desplazable.peer.getViewport().setOpaque(false)
        desplazable.peer.getVerticalScrollBar().setUnitIncrement(18)

        val botones = Componentes.fila(
            FlowPanel.Alignment.Right,
            Componentes.botonSecundario("Probar otro método") { alProbarOtro() },
            Componentes.botonPrimario("Evaluar otra clave") { alReiniciar() }
        )
        botones.border = Swing.EmptyBorder(14, 0, 0, 0)

        new BorderPanel {
            opaque = false
            layout(parteSuperior) = BorderPanel.Position.North
            layout(desplazable) = BorderPanel.Position.Center
            layout(botones) = BorderPanel.Position.South
        }
    }
}
