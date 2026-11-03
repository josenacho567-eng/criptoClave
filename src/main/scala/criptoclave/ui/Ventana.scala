package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import scala.swing.*

class Ventana extends MainFrame {

    // Único estado mutable de toda la aplicación
    private var estado: EstadoApp = Inicio

    title = "CriptoClave"
    preferredSize = new Dimension(950, 700)

    mostrarEvaluar()

    private def mostrar(pantalla: Component): Unit = {
        contents = pantalla
        peer.validate()
        peer.repaint()
    }

    private def mostrarEvaluar(): Unit = {
        mostrar(PantallaEvaluar.crear(
            estado,
            nuevo => {
                estado = nuevo
                mostrarEvaluar()
            },
            () => mostrarMetodo()
        ))
    }

    private def mostrarMetodo(): Unit = {
        mostrar(PantallaMetodo.crear(
            estado,
            nuevo => {
                estado = nuevo
                mostrarComparacion()
            },
            () => mostrarEvaluar()
        ))
    }

    private def mostrarComparacion(): Unit = {
        mostrar(PantallaComparacion.crear(
            estado,
            () => mostrarMetodo(),
            () => {
                estado = Flujo.reiniciar()
                mostrarEvaluar()
            }
        ))
    }
}
