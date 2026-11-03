package criptoclave

import com.formdev.flatlaf.FlatDarkLaf
import criptoclave.ui.Ventana
import javax.swing.UIManager
import scala.swing.Swing

object Main {

    def main(args: Array[String]): Unit = {
        Swing.onEDT {
            UIManager.put("defaultFont", new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15))
            UIManager.put("Button.arc", 12)
            UIManager.put("Component.arc", 12)
            UIManager.put("TextComponent.arc", 12)
            FlatDarkLaf.setup()

            val ventana = new Ventana()
            ventana.centerOnScreen()
            ventana.visible = true
        }
    }
}