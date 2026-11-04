package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import java.awt.BasicStroke
import java.awt.Cursor
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import scala.swing.*

// Pantalla 2: el usuario elige el método de cifrado (tarjetas) y sus parámetros
object PantallaMetodo {

    private case class Opcion(titulo: String, descripcion: String)

    // La última opción prueba todos los métodos
    private val opciones: List[Opcion] = List(
        Opcion("César", "Desplaza cada carácter un número fijo de posiciones."),
        Opcion("Vigenère", "Desplaza cada carácter según una llave que se repite."),
        Opcion("Sustitución", "Cambia cada carácter usando un alfabeto mezclado por la llave."),
        Opcion("Huffman", "Convierte la clave en bits según la frecuencia de cada carácter."),
        Opcion("RSA", "Cifra cada carácter con números primos predeterminados."),
        Opcion("Todos", "Prueba los cinco métodos y los ordena por puntaje.")
    )

    // Tarjeta clicable: el RadioButton guarda cuál está elegida
    private def tarjetaSeleccionable(opcion: Opcion, radio: RadioButton): Component = {
        radio.opaque = false

        val izquierda = new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(0, 0, 0, 6)
            layout(radio) = BorderPanel.Position.North
        }

        val textos = Componentes.columna(
            Componentes.texto(opcion.titulo, 16, true, Tema.texto),
            Swing.VStrut(4),
            Componentes.textoLargo(opcion.descripcion, 190, 13, Tema.textoSuave, false)
        )

        val tarjeta = new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(14, 14, 14, 14)
            layout(izquierda) = BorderPanel.Position.West
            layout(textos) = BorderPanel.Position.Center

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                val elegida = radio.selected
                g.setColor(if elegida then Tema.acentoSuave else Tema.tarjeta)
                g.fillRoundRect(0, 0, size.width - 1, size.height - 1, 20, 20)
                g.setColor(if elegida then Tema.acento else Tema.borde)
                g.setStroke(new BasicStroke(if elegida then 2f else 1f))
                g.drawRoundRect(1, 1, size.width - 3, size.height - 3, 20, 20)
            }
        }

        tarjeta.peer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        tarjeta.peer.addMouseListener(new MouseAdapter {
            override def mousePressed(evento: MouseEvent): Unit = radio.peer.doClick()
        })
        tarjeta
    }

    def crear(estado: EstadoApp, alActualizar: EstadoApp => Unit, alVolver: () => Unit): Component = {
        val radios: List[RadioButton] = opciones.map(_ => new RadioButton(""))
        val grupo = new ButtonGroup(radios*)
        radios.head.selected = true

        val campoDesplazamiento = new TextField("3", 10)
        Componentes.estilizarCampo(campoDesplazamiento.peer, "Ej: 3")
        val campoLlave = new TextField("Clave", 10)
        Componentes.estilizarCampo(campoLlave.peer, "Ej: Clave")

        val ayuda = Componentes.texto("", 13, false, Tema.textoSuave)
        val mensaje = Componentes.texto("", 14, true, Tema.peligro)

        def indiceElegido(): Int = radios.indexWhere(radio => radio.selected)

        def actualizarCampos(): Unit = {
            val indice = indiceElegido()
            campoDesplazamiento.enabled = indice == 0
            campoLlave.enabled = indice == 1 || indice == 2
            ayuda.text = indice match {
                case 0 => "Número entero: cuántas posiciones se desplaza cada carácter."
                case 1 => "Texto que se usa como llave de cifrado."
                case 2 => "Texto que se usa para mezclar el alfabeto."
                case 3 => "Huffman no necesita parámetros."
                case 4 =>
                    val rsa = Predeterminados.rsa
                    s"RSA usa valores predeterminados: p = ${rsa.p}, q = ${rsa.q}, e = ${rsa.e}."
                case _ => "Se probarán todos los métodos con sus valores predeterminados."
            }
            mensaje.text = ""
        }

        def leerMetodo(indice: Int): Resultado[Metodo] = {
            indice match {
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
            val indice = indiceElegido()
            if indice == opciones.length - 1 then {
                mostrarResultado(Flujo.probarTodos(estado, Predeterminados.metodos, Flujo.porPuntaje))
            } else {
                leerMetodo(indice) match {
                    case Exito(metodo) => mostrarResultado(Flujo.probar(estado, metodo))
                    case Fallo(motivo) => mensaje.text = motivo
                }
            }
        }

        val tarjetas: List[Component] = opciones.zip(radios).map {
            case (opcion, radio) => tarjetaSeleccionable(opcion, radio)
        }

        val grilla = new GridPanel(2, 3) {
            opaque = false
            hGap = 14
            vGap = 14
            contents ++= tarjetas
        }

        // Al elegir una tarjeta se repinta la grilla y se actualizan los parámetros
        radios.foreach(radio => radio.peer.addActionListener((evento: ActionEvent) => {
            grilla.repaint()
            actualizarCampos()
        }))
        actualizarCampos()

        val formulario = new GridPanel(2, 2) {
            opaque = false
            hGap = 16
            vGap = 10
            contents += Componentes.texto("Desplazamiento (César)", 14, false, Tema.texto)
            contents += campoDesplazamiento
            contents += Componentes.texto("Llave (Vigenère y Sustitución)", 14, false, Tema.texto)
            contents += campoLlave
        }

        val parametros = Componentes.tarjeta(
            Componentes.columna(
                Componentes.tituloSeccion("Parámetros"),
                Swing.VStrut(10),
                formulario,
                Swing.VStrut(10),
                ayuda
            ),
            18
        )

        val subtitulo: String = Flujo.baseDe(estado) match {
            case Exito((clave, evaluacion)) =>
                s"Tu clave es ${Formato.nombreNivel(evaluacion.nivel)} (${evaluacion.puntaje} pts). Elige cómo transformarla."
            case Fallo(motivo) => motivo
        }

        val contenido = Componentes.columna(
            Componentes.encabezado("Elige un método de cifrado", subtitulo),
            grilla,
            Swing.VStrut(18),
            parametros,
            Swing.VStrut(8),
            mensaje
        )

        val botones = Componentes.fila(
            FlowPanel.Alignment.Right,
            Componentes.botonSecundario("←  Volver") { alVolver() },
            Componentes.botonPrimario("Aplicar y comparar") { aplicar() }
        )
        botones.border = Swing.EmptyBorder(14, 0, 0, 0)

        new BorderPanel {
            opaque = false
            layout(contenido) = BorderPanel.Position.North
            layout(botones) = BorderPanel.Position.South
        }
    }
}
