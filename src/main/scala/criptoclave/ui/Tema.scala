package criptoclave.ui

import criptoclave.modelo.*
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import javax.swing.UIManager

// Colores y fuentes de toda la aplicación
object Tema {

    val fondo: Color = new Color(0xF1, 0xF5, 0xF9)
    val tarjeta: Color = Color.WHITE
    val borde: Color = new Color(0xE2, 0xE8, 0xF0)
    val texto: Color = new Color(0x0F, 0x17, 0x2A)
    val textoSuave: Color = new Color(0x64, 0x74, 0x8B)

    val acento: Color = new Color(0x63, 0x66, 0xF1)
    val acentoSuave: Color = new Color(0xEE, 0xF2, 0xFF)

    val marino: Color = new Color(0x0F, 0x17, 0x2A)
    val marinoClaro: Color = new Color(0x31, 0x2E, 0x81)

    val exito: Color = new Color(0x16, 0xA3, 0x4A)
    val aviso: Color = new Color(0xF5, 0x9E, 0x0B)
    val peligro: Color = new Color(0xDC, 0x26, 0x26)

    def fuente(tamano: Int, negrita: Boolean): Font = {
        val base = UIManager.getFont("Label.font")
        val familia = if base == null then Font.SANS_SERIF else base.getFamily()
        new Font(familia, if negrita then Font.BOLD else Font.PLAIN, tamano)
    }

    def conAlfa(color: Color, alfa: Int): Color = {
        new Color(color.getRed(), color.getGreen(), color.getBlue(), alfa)
    }

    def colorNivel(nivel: Nivel): Color = {
        nivel match {
            case Debil  => peligro
            case Medio  => aviso
            case Fuerte => exito
        }
    }

    def colorVeredicto(veredicto: Veredicto): Color = {
        veredicto match {
            case Mejoro  => exito
            case Empeoro => peligro
            case Igual   => textoSuave
        }
    }

    // Activa el suavizado de bordes para dibujar formas y texto
    def suavizar(g: java.awt.Graphics2D): Unit = {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    }
}
