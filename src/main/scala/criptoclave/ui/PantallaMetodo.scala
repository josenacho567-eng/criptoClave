package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import scala.swing.*
import scala.swing.event.SelectionChanged

// Pantalla 2: el usuario elige el método de cifrado y sus parámetros
object PantallaMetodo {

    private val nombres: List[String] = List("César", "Vigenère", "Sustitución", "Huffman", "RSA")

    def crear(estado: EstadoApp, alActualizar: EstadoApp => Unit, alVolver: () => Unit): Component = {
        val selector = new ComboBox(nombres)
        val campoDesplazamiento = new TextField("3", 8)
        val campoLlave = new TextField("Clave", 12)
        val mensaje = new Label("")
        mensaje.foreground = java.awt.Color.RED

        // RSA siempre usa los valores de Predeterminados, el usuario no los cambia
        val notaRsa = new Label(s"RSA usa valores predeterminados: ${Formato.nombreMetodo(Predeterminados.rsa)}")

        def actualizarCampos(): Unit = {
            val indice = selector.selection.index
            campoDesplazamiento.enabled = indice == 0
            campoLlave.enabled = indice == 1 || indice == 2
        }

        def leerMetodo(): Resultado[Metodo] = {
            selector.selection.index match {
                case 0 =>
                    campoDesplazamiento.text.trim.toIntOption match {
                        case Some(desplazamiento) => Exito(Cesar(desplazamiento))
                        case None                 => Fallo("El desplazamiento debe ser un número entero")
                    }
                case 1 => Exito(Vigenere(campoLlave.text))
                case 2 => Exito(Sustitucion(campoLlave.text))
                case 3 => Exito(Huffman)
                case _ => Exito(Predeterminados.rsa)
            }
        }

        def mostrarResultado(resultado: Resultado[EstadoApp]): Unit = {
            resultado match {
                case Exito(nuevo)  => alActualizar(nuevo)
                case Fallo(motivo) => mensaje.text = motivo
            }
        }

        def aplicar(): Unit = {
            leerMetodo() match {
                case Exito(metodo) => mostrarResultado(Flujo.probar(estado, metodo))
                case Fallo(motivo) => mensaje.text = motivo
            }
        }

        def probarTodos(): Unit = {
            mostrarResultado(Flujo.probarTodos(estado, Predeterminados.metodos, Flujo.porPuntaje))
        }

        val formulario = new GridPanel(3, 2) {
            contents += new Label("Método:")
            contents += selector
            contents += new Label("Desplazamiento (César):")
            contents += campoDesplazamiento
            contents += new Label("Llave (Vigenère / Sustitución):")
            contents += campoLlave
        }

        formulario.listenTo(selector.selection)
        formulario.reactions += {
            case SelectionChanged(_) => actualizarCampos()
        }
        actualizarCampos()

        val encabezado: String = Flujo.baseDe(estado) match {
            case Exito((clave, evaluacion)) =>
                s"Clave: $clave   |   Nivel: ${Formato.nombreNivel(evaluacion.nivel)} (${evaluacion.puntaje} pts)"
            case Fallo(motivo) => motivo
        }

        val arriba = new BoxPanel(Orientation.Vertical) {
            contents += new Label(encabezado)
            contents += Swing.VStrut(15)
            contents += formulario
            contents += Swing.VStrut(10)
            contents += notaRsa
            contents += Swing.VStrut(10)
            contents += mensaje
        }

        val abajo = new FlowPanel(FlowPanel.Alignment.Right)(
            Formato.boton("Volver") { alVolver() },
            Formato.boton("Probar todos los métodos") { probarTodos() },
            Formato.boton("Aplicar método") { aplicar() }
        )

        new BorderPanel {
            border = Swing.EmptyBorder(15)
            layout(arriba) = BorderPanel.Position.North
            layout(abajo) = BorderPanel.Position.South
        }
    }
}
