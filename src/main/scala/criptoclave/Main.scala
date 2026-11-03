package criptoclave

import criptoclave.ui.Ventana
import scala.swing.Swing

object Main {

    def main(args: Array[String]): Unit = {
        Swing.onEDT {
            val ventana = new Ventana()
            ventana.centerOnScreen()
            ventana.visible = true
        }
    }
}
