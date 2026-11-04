package criptoclave.ui

import criptoclave.modelo.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Cursor
import java.awt.Insets
import scala.swing.*

// Piezas visuales reutilizables: tarjetas, botones, chips, medidor, etc.
object Componentes {

    // ---------- Texto ----------

    def texto(contenido: String, tamano: Int, negrita: Boolean, color: Color): Label = {
        val etiqueta = new Label(contenido)
        etiqueta.font = Tema.fuente(tamano, negrita)
        etiqueta.foreground = color
        etiqueta.horizontalAlignment = Alignment.Left
        etiqueta
    }

    // Texto que baja de línea cuando pasa del ancho indicado
    def textoLargo(contenido: String, ancho: Int, tamano: Int, color: Color, centrado: Boolean): Label = {
        val alineacion = if centrado then "center" else "left"
        val etiqueta = new Label(s"<html><body style='width: ${ancho}px; text-align: $alineacion'>$contenido</body></html>")
        etiqueta.font = Tema.fuente(tamano, false)
        etiqueta.foreground = color
        etiqueta
    }

    // ---------- Contenedores ----------

    private def apilar(alineacion: Float, hijos: Seq[Component]): BoxPanel = {
        val panel = new BoxPanel(Orientation.Vertical)
        panel.opaque = false
        hijos.foreach(hijo => {
            hijo.peer.setAlignmentX(alineacion)
            panel.contents += hijo
        })
        panel
    }

    def columna(hijos: Component*): BoxPanel = apilar(0f, hijos)

    def columnaCentrada(hijos: Component*): BoxPanel = apilar(0.5f, hijos)

    def fila(alineacion: FlowPanel.Alignment.Value, hijos: Component*): FlowPanel = {
        val panel = new FlowPanel(alineacion)(hijos*)
        panel.opaque = false
        panel
    }

    // Pega el contenido arriba para que no se estire hacia abajo
    def arriba(contenido: Component): Component = {
        new BorderPanel {
            opaque = false
            layout(contenido) = BorderPanel.Position.North
        }
    }

    def encabezado(titulo: String, subtitulo: String): Component = {
        val bloque = columna(
            texto(titulo, 28, true, Tema.texto),
            Swing.VStrut(4),
            texto(subtitulo, 15, false, Tema.textoSuave)
        )
        bloque.border = Swing.EmptyBorder(0, 0, 20, 0)
        bloque
    }

    def tituloSeccion(contenido: String): Label = texto(contenido, 17, true, Tema.texto)

    // Tarjeta blanca con esquinas redondeadas y sombra suave
    def tarjeta(contenido: Component, relleno: Int): Component = {
        new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(relleno + 2, relleno + 2, relleno + 5, relleno + 4)
            layout(contenido) = BorderPanel.Position.North

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                val ancho = size.width
                val alto = size.height
                g.setColor(new Color(15, 23, 42, 12))
                g.fillRoundRect(1, 4, ancho - 3, alto - 4, 24, 24)
                g.setColor(Tema.tarjeta)
                g.fillRoundRect(0, 0, ancho - 3, alto - 5, 22, 22)
                g.setColor(Tema.borde)
                g.setStroke(new BasicStroke(1f))
                g.drawRoundRect(0, 0, ancho - 4, alto - 6, 22, 22)
            }
        }
    }

    // ---------- Chips ----------

    def pildora(contenido: String, fondo: Color, letra: Color): Label = {
        val etiqueta = new Label(contenido) {
            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                g.setColor(fondo)
                g.fillRoundRect(0, 0, size.width, size.height, size.height, size.height)
                super.paintComponent(g)
            }
        }
        etiqueta.font = Tema.fuente(13, true)
        etiqueta.foreground = letra
        etiqueta.border = Swing.EmptyBorder(4, 12, 4, 12)
        etiqueta
    }

    // ---------- Botones ----------

    private def botonBase(contenido: String, estilo: String, fondo: Color, letra: Color)(accion: => Unit): Button = {
        val boton = new Button(Action(contenido)(accion))
        boton.background = fondo
        boton.foreground = letra
        boton.font = Tema.fuente(15, true)
        boton.peer.setMargin(new Insets(11, 24, 11, 24))
        boton.peer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        boton.peer.putClientProperty("JButton.buttonType", "roundRect")
        boton.peer.putClientProperty("FlatLaf.style", estilo)
        boton
    }

    def botonPrimario(contenido: String)(accion: => Unit): Button = {
        val estilo = "background: #6366F1; foreground: #FFFFFF; hoverBackground: #4F46E5; " +
            "pressedBackground: #4338CA; borderWidth: 0; focusWidth: 0"
        botonBase(contenido, estilo, Tema.acento, Color.WHITE)(accion)
    }

    def botonSecundario(contenido: String)(accion: => Unit): Button = {
        val estilo = "background: #FFFFFF; foreground: #0F172A; hoverBackground: #EEF2FF; " +
            "pressedBackground: #E0E7FF; borderColor: #CBD5E1; hoverBorderColor: #6366F1; " +
            "borderWidth: 1; focusWidth: 0"
        botonBase(contenido, estilo, Color.WHITE, Tema.texto)(accion)
    }

    // ---------- Campos de texto ----------

    def estilizarCampo(campo: javax.swing.JTextField, sugerencia: String): Unit = {
        campo.putClientProperty("JTextField.placeholderText", sugerencia)
        campo.putClientProperty("JComponent.roundRect", true)
        campo.putClientProperty("FlatLaf.style", "margin: 9,14,9,14; focusWidth: 2")
        campo.setFont(Tema.fuente(16, false))
    }


    // Campo de clave con botón de ojo para mostrar u ocultar el texto.
    // El estado (visible u oculto) lo guarda el propio campo con su carácter de eco.
    def campoClave(campo: PasswordField, sugerencia: String): Component = {
        val caracterOculto: Char = campo.peer.getEchoChar()

        campo.font = Tema.fuente(16, false)
        campo.peer.setOpaque(false)
        campo.peer.setBackground(Tema.tarjeta)
        campo.peer.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 6, 11, 4))
        campo.peer.putClientProperty("JTextField.placeholderText", sugerencia)

        val ojo = new Panel {
            opaque = false

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                val visible = campo.peer.getEchoChar() == 0.toChar
                val color = if visible then Tema.acento else Tema.textoSuave
                g.translate(size.width / 2 - 14, size.height / 2 - 11)
                g.setColor(color)
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))

                val contorno = new java.awt.geom.Path2D.Float()
                contorno.moveTo(0f, 11f)
                contorno.quadTo(14f, -5f, 28f, 11f)
                contorno.quadTo(14f, 27f, 0f, 11f)
                contorno.closePath()
                g.draw(contorno)
                g.fillOval(10, 7, 8, 8)

                // Ojo tachado cuando la clave está visible (clic para ocultarla)
                if visible then {
                    g.setColor(Tema.tarjeta)
                    g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
                    g.drawLine(5, 23, 23, -1)
                    g.setColor(color)
                    g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
                    g.drawLine(5, 23, 23, -1)
                }
            }
        }
        ojo.preferredSize = new Dimension(40, 36)
        ojo.tooltip = "Mostrar u ocultar la clave"
        ojo.peer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        ojo.peer.addMouseListener(new java.awt.event.MouseAdapter {
            override def mousePressed(evento: java.awt.event.MouseEvent): Unit = {
                if campo.peer.getEchoChar() == 0.toChar then campo.peer.setEchoChar(caracterOculto)
                else campo.peer.setEchoChar(0.toChar)
                ojo.repaint()
                campo.peer.requestFocusInWindow()
            }
        })

        val contenedor = new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(0, 12, 0, 4)
            layout(campo) = BorderPanel.Position.Center
            layout(ojo) = BorderPanel.Position.East

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                val enfocado = campo.peer.isFocusOwner()
                g.setColor(Tema.tarjeta)
                g.fillRoundRect(0, 0, size.width - 1, size.height - 1, size.height, size.height)
                g.setColor(if enfocado then Tema.acento else new Color(0xCB, 0xD5, 0xE1))
                g.setStroke(new BasicStroke(if enfocado then 2.5f else 1.5f))
                g.drawRoundRect(1, 1, size.width - 3, size.height - 3, size.height, size.height)
            }
        }

        // Repinta el borde cuando el campo gana o pierde el foco
        campo.peer.addFocusListener(new java.awt.event.FocusAdapter {
            override def focusGained(evento: java.awt.event.FocusEvent): Unit = contenedor.repaint()
            override def focusLost(evento: java.awt.event.FocusEvent): Unit = contenedor.repaint()
        })

        contenedor
    }

    // ---------- Dibujos ----------

    // Círculo con un visto (cumplida) o una cruz (no cumplida), con margen a la derecha
    def marca(cumplida: Boolean): Component = {
        val color = if cumplida then Tema.exito else Tema.peligro
        val icono = new Panel {
            opaque = false

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                g.setColor(Tema.conAlfa(color, 30))
                g.fillOval(0, 0, 26, 26)
                g.setColor(color)
                g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
                if cumplida then {
                    g.drawLine(7, 13, 11, 17)
                    g.drawLine(11, 17, 19, 9)
                } else {
                    g.drawLine(9, 9, 17, 17)
                    g.drawLine(17, 9, 9, 17)
                }
            }
        }
        icono.preferredSize = new Dimension(26, 26)
        icono.minimumSize = new Dimension(26, 26)
        icono.maximumSize = new Dimension(26, 26)

        new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(0, 0, 0, 12)
            layout(icono) = BorderPanel.Position.North
        }
    }

    // Medidor circular con el puntaje de la evaluación
    def medidor(evaluacion: Evaluacion): Component = {
        val color = Tema.colorNivel(evaluacion.nivel)
        val panel = new Panel {
            opaque = false

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                val lado = 170
                val x = (size.width - lado) / 2
                val y = 10
                g.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
                g.setColor(Tema.borde)
                g.drawArc(x, y, lado, lado, 90, -360)
                if evaluacion.puntaje > 0 then {
                    g.setColor(color)
                    g.drawArc(x, y, lado, lado, 90, -(evaluacion.puntaje * 360 / 100))
                }

                val numero = evaluacion.puntaje.toString
                g.setColor(Tema.texto)
                g.setFont(Tema.fuente(46, true))
                val medidasNumero = g.getFontMetrics()
                g.drawString(
                    numero,
                    (size.width - medidasNumero.stringWidth(numero)) / 2,
                    y + lado / 2 + medidasNumero.getAscent() / 3
                )

                val pie = "de 100 puntos"
                g.setColor(Tema.textoSuave)
                g.setFont(Tema.fuente(13, false))
                val medidasPie = g.getFontMetrics()
                g.drawString(pie, (size.width - medidasPie.stringWidth(pie)) / 2, y + lado / 2 + 34)
            }
        }
        panel.preferredSize = new Dimension(220, 190)
        panel.minimumSize = new Dimension(220, 190)
        panel.maximumSize = new Dimension(220, 190)
        panel
    }
}
