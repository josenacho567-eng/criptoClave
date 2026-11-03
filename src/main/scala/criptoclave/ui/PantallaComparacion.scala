package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import scala.swing.*

// Pantalla 3: tabla con todas las pruebas, resumen y opción de revertir
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

    def crear(estado: EstadoApp, alProbarOtro: () => Unit, alReiniciar: () => Unit): Component = {
        val clave = claveOriginal(estado)

        val filas: Array[Array[Any]] = estado.pruebas
            .map(prueba => Array[Any](
                Formato.nombreMetodo(prueba.metodo),
                Formato.resumir(prueba.salida, 35),
                prueba.evaluacion.puntaje,
                Formato.nombreNivel(prueba.evaluacion.nivel),
                Formato.conSigno(prueba.diferencia),
                Formato.textoVeredicto(prueba.veredicto)
            ))
            .toArray

        val columnas = List("Método", "Salida", "Puntaje", "Nivel", "Diferencia", "Veredicto")
        val tabla = new Table(filas, columnas)
        tabla.peer.setRowHeight(28)
        // Las celdas no se pueden editar y solo se elige una fila a la vez
        tabla.peer.setDefaultEditor(classOf[Object], null)
        tabla.peer.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION)

        val mejor: String = estado.mejorPrueba match {
            case Some(prueba) =>
                s"Mejor resultado: ${Formato.nombreMetodo(prueba.metodo)} con ${prueba.evaluacion.puntaje} puntos"
            case None => "Todavía no hay pruebas"
        }
        val sube: String =
            if estado.algunaSubeDeNivel then "Algún método subió el nivel de la clave"
            else "Ningún método subió el nivel de la clave"

        val resumen = new TextArea(
            s"Mejoran: ${estado.mejoran}   |   Empeoran: ${estado.empeoran}\n$mejor\n$sube"
        )
        resumen.editable = false

        val mensaje = new Label("")
        mensaje.foreground = java.awt.Color.RED

        def verDetalle(): Unit = {
            estado.pruebas.lift(tabla.peer.getSelectedRow()) match {
                case Some(prueba) => mostrarDetalle(prueba, clave)
                case None         => mensaje.text = "Selecciona una fila de la tabla"
            }
        }

        val arriba = new BoxPanel(Orientation.Vertical) {
            contents += new Label(s"Clave original: $clave")
            contents += Swing.VStrut(10)
            contents += resumen
            contents += Swing.VStrut(10)
        }

        val abajo = new BoxPanel(Orientation.Vertical) {
            contents += mensaje
            contents += new FlowPanel(FlowPanel.Alignment.Right)(
                Formato.boton("Evaluar otra clave") { alReiniciar() },
                Formato.boton("Ver detalle y revertir") { verDetalle() },
                Formato.boton("Probar otro método") { alProbarOtro() }
            )
        }

        new BorderPanel {
            border = Swing.EmptyBorder(15)
            layout(arriba) = BorderPanel.Position.North
            layout(new ScrollPane(tabla)) = BorderPanel.Position.Center
            layout(abajo) = BorderPanel.Position.South
        }
    }
}
