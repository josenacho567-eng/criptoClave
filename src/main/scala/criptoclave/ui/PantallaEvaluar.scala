package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import scala.swing.*
import scala.swing.event.EditDone

// Pantalla 1: el usuario escribe la clave y ve su fortaleza
object PantallaEvaluar {

    private def clavePrevia(estado: EstadoApp): String = {
        estado match {
            case Inicio               => ""
            case Evaluada(clave, _)   => clave
            case ConPruebas(clave, _, _) => clave
        }
    }

    def crear(estado: EstadoApp, alEvaluar: EstadoApp => Unit, alContinuar: () => Unit): Component = {
        val campo = new TextField(clavePrevia(estado), 24)
        val mensaje = new Label("")
        mensaje.foreground = java.awt.Color.RED

        def evaluar(): Unit = {
            Flujo.evaluar(campo.text) match {
                case Exito(nuevo)  => alEvaluar(nuevo)
                case Fallo(motivo) => mensaje.text = motivo
            }
        }

        // Pulsar Enter en el campo también evalúa
        campo.listenTo(campo)
        campo.reactions += {
            case EditDone(_) => evaluar()
        }

        val botonEvaluar = Formato.boton("Evaluar clave") { evaluar() }
        val botonContinuar = Formato.boton("Elegir método de cifrado") { alContinuar() }
        botonContinuar.enabled = estado != Inicio

        val arriba = new BoxPanel(Orientation.Vertical) {
            contents += new FlowPanel(FlowPanel.Alignment.Left)(new Label("Clave:"), campo, botonEvaluar)
            contents += new FlowPanel(FlowPanel.Alignment.Left)(mensaje)
        }

        val centro: Component = estado match {
            case Inicio                  => new Label("Escribe una clave y pulsa Evaluar.")
            case Evaluada(_, evaluacion) => Formato.panelEvaluacion(evaluacion)
            case ConPruebas(_, evaluacion, _) => Formato.panelEvaluacion(evaluacion)
        }

        val abajo = new FlowPanel(FlowPanel.Alignment.Right)(botonContinuar)

        new BorderPanel {
            border = Swing.EmptyBorder(15)
            layout(arriba) = BorderPanel.Position.North
            layout(centro) = BorderPanel.Position.Center
            layout(abajo) = BorderPanel.Position.South
        }
    }
}
