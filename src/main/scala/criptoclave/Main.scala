package criptoclave

import com.formdev.flatlaf.FlatLaf
import com.formdev.flatlaf.FlatLightLaf
import criptoclave.ui.Ventana
import java.util.Collections
import javax.swing.UIManager
import scala.swing.Swing

object Main {

    def main(args: Array[String]): Unit = {
        Swing.onEDT {
            // El color de acento se aplica a enfoques, selecciones y barras
            FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#6366F1"))
            FlatLightLaf.setup()

            // Barras de desplazamiento delgadas y redondeadas
            UIManager.put("ScrollBar.width", 10)
            UIManager.put("ScrollBar.thumbArc", 999)
            UIManager.put("ScrollBar.thumbInsets", new java.awt.Insets(2, 2, 2, 2))

            val ventana = new Ventana()
            ventana.centerOnScreen()
            ventana.visible = true
        }
    }
}
