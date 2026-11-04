package criptoclave.ui

import java.awt.Color
import java.awt.Cursor
import java.awt.GradientPaint
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import scala.swing.*

// Barra oscura de la izquierda: logo y los tres pasos del flujo
object BarraLateral {

    private val titulos: List[String] = List("Evaluar clave", "Elegir método", "Comparar resultados")

    private def logo(): Component = {
        val panel = new Panel {
            opaque = false

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                g.setPaint(new GradientPaint(0f, 0f, new Color(0x81, 0x8C, 0xF8), 44f, 44f, Tema.acento))
                g.fillRoundRect(0, 0, 44, 44, 14, 14)

                g.setColor(Color.WHITE)
                g.setFont(Tema.fuente(24, true))
                val medidas = g.getFontMetrics()
                g.drawString("C", (44 - medidas.stringWidth("C")) / 2, 31)

                g.setFont(Tema.fuente(20, true))
                g.drawString("CriptoClave", 58, 21)

                g.setColor(new Color(255, 255, 255, 140))
                g.setFont(Tema.fuente(12, false))
                g.drawString("Fortaleza de claves", 58, 38)
            }
        }
        panel.preferredSize = new Dimension(238, 44)
        panel.minimumSize = new Dimension(238, 44)
        panel.maximumSize = new Dimension(238, 44)
        panel
    }

    private def paso(indice: Int, titulo: String, actual: Int, habilitado: Boolean, alIr: Int => Unit): Component = {
        val esActual = indice == actual
        val completado = indice < actual

        val panel = new Panel {
            opaque = false

            override protected def paintComponent(g: Graphics2D): Unit = {
                Tema.suavizar(g)
                if esActual then {
                    g.setColor(new Color(255, 255, 255, 30))
                    g.fillRoundRect(0, 0, size.width - 1, size.height - 1, 16, 16)
                }

                val colorCirculo =
                    if esActual then Tema.acento
                    else if completado then Tema.exito
                    else new Color(255, 255, 255, 45)
                g.setColor(colorCirculo)
                g.fillOval(14, 14, 28, 28)

                val numero = (indice + 1).toString
                g.setColor(Color.WHITE)
                g.setFont(Tema.fuente(14, true))
                val medidasNumero = g.getFontMetrics()
                g.drawString(
                    numero,
                    14 + (28 - medidasNumero.stringWidth(numero)) / 2,
                    14 + (28 + medidasNumero.getAscent() - medidasNumero.getDescent()) / 2
                )

                g.setColor(if habilitado then Color.WHITE else new Color(255, 255, 255, 110))
                g.setFont(Tema.fuente(15, esActual))
                val medidasTitulo = g.getFontMetrics()
                g.drawString(
                    titulo,
                    54,
                    (size.height + medidasTitulo.getAscent() - medidasTitulo.getDescent()) / 2
                )
            }
        }
        panel.preferredSize = new Dimension(238, 56)
        panel.minimumSize = new Dimension(238, 56)
        panel.maximumSize = new Dimension(238, 56)

        if habilitado then {
            panel.peer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
            panel.peer.addMouseListener(new MouseAdapter {
                override def mousePressed(evento: MouseEvent): Unit = alIr(indice)
            })
        }
        panel
    }

    def crear(actual: Int, habilitados: List[Boolean], alIr: Int => Unit): Component = {
        val pasos: List[Component] = titulos.zip(habilitados).zipWithIndex.map {
            case ((titulo, habilitado), indice) => paso(indice, titulo, actual, habilitado, alIr)
        }

        val pie = Componentes.texto("Programación Funcional · Scala", 12, false, new Color(255, 255, 255, 120))

        val barra = new BoxPanel(Orientation.Vertical) {
            border = Swing.EmptyBorder(28, 16, 24, 16)
            preferredSize = new Dimension(270, 100)

            override protected def paintComponent(g: Graphics2D): Unit = {
                g.setPaint(new GradientPaint(0f, 0f, Tema.marino, 0f, size.height.toFloat, Tema.marinoClaro))
                g.fillRect(0, 0, size.width, size.height)
            }
        }

        val elementos: List[Component] =
            List(logo(), Swing.VStrut(34)) ++
            pasos.flatMap(elemento => List(elemento, Swing.VStrut(6))) ++
            List(Swing.VGlue, pie)

        elementos.foreach(elemento => {
            elemento.peer.setAlignmentX(0f)
            barra.contents += elemento
        })
        barra
    }
}
