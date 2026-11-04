package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import scala.swing.*

class Ventana extends MainFrame {

    // Único estado mutable de toda la aplicación
    private var estado: EstadoApp = Inicio

    title = "CriptoClave"
    preferredSize = new Dimension(1180, 780)
    minimumSize = new Dimension(1000, 680)

    mostrarEvaluar()

    // Pasos que se pueden visitar desde la barra lateral
    private def habilitados(): List[Boolean] = {
        List(true, estado != Inicio, estado.pruebas.nonEmpty)
    }

    private def irAPaso(indice: Int): Unit = {
        indice match {
            case 0 => mostrarEvaluar()
            case 1 => mostrarMetodo()
            case _ => mostrarComparacion()
        }
    }

    private def mostrar(paso: Int, pantalla: Component): Unit = {
        val barra = BarraLateral.crear(paso, habilitados(), indice => irAPaso(indice))

        val area = new BorderPanel {
            background = Tema.fondo
            border = Swing.EmptyBorder(30, 36, 30, 36)
            layout(pantalla) = BorderPanel.Position.Center
        }

        contents = new BorderPanel {
            layout(barra) = BorderPanel.Position.West
            layout(area) = BorderPanel.Position.Center
        }
        peer.validate()
        peer.repaint()
    }

    private def mostrarEvaluar(): Unit = {
        mostrar(0, PantallaEvaluar.crear(
            estado,
            nuevo => {
                estado = nuevo
                mostrarEvaluar()
            },
            () => mostrarMetodo()
        ))
    }

    private def mostrarMetodo(): Unit = {
        mostrar(1, PantallaMetodo.crear(
            estado,
            nuevo => {
                estado = nuevo
                mostrarComparacion()
            },
            () => mostrarEvaluar()
        ))
    }

    private def mostrarComparacion(): Unit = {
        mostrar(2, PantallaComparacion.crear(
            estado,
            () => mostrarMetodo(),
            () => {
                estado = Flujo.reiniciar()
                mostrarEvaluar()
            }
        ))
    }
}
