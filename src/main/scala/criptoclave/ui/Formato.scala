package criptoclave.ui

import criptoclave.modelo.*
import scala.swing.*

// Funciones de ayuda para mostrar los datos del modelo en pantalla
object Formato {

    def nombreMetodo(metodo: Metodo): String = {
        metodo match {
            case Cesar(desplazamiento) => s"César (desplazamiento $desplazamiento)"
            case Vigenere(llave)       => s"Vigenère (llave $llave)"
            case Sustitucion(llave)    => s"Sustitución (llave $llave)"
            case Huffman               => "Huffman"
            case Rsa(p, q, e)          => s"RSA (p=$p, q=$q, e=$e)"
        }
    }

    def nombreNivel(nivel: Nivel): String = {
        nivel match {
            case Debil  => "Débil"
            case Medio  => "Medio"
            case Fuerte => "Fuerte"
        }
    }

    def colorNivel(nivel: Nivel): java.awt.Color = {
        nivel match {
            case Debil  => new java.awt.Color(192, 57, 43)
            case Medio  => new java.awt.Color(214, 137, 16)
            case Fuerte => new java.awt.Color(30, 132, 73)
        }
    }

    def textoVeredicto(veredicto: Veredicto): String = {
        veredicto match {
            case Mejoro  => "Mejoró"
            case Empeoro => "Empeoró"
            case Igual   => "Igual"
        }
    }

    def conSigno(numero: Int): String = {
        if numero > 0 then s"+$numero" else s"$numero"
    }

    def resumir(texto: String, maximo: Int): String = {
        if texto.length <= maximo then texto
        else texto.take(maximo) + "..."
    }

    def lineaDeRegla(resultado: ResultadoRegla): String = {
        if resultado.cumplida then s"[OK]  ${resultado.nombre}  (+${resultado.puntosObtenidos})"
        else s"[  ]  ${resultado.nombre}  ->  ${resultado.descripcion}"
    }

    // Botón que ejecuta el bloque que se le pasa al hacer clic
    def boton(texto: String)(accion: => Unit): Button = {
        new Button(Action(texto)(accion))
    }

    // Nivel, barra de puntaje y lista de reglas de una evaluación
    def panelEvaluacion(evaluacion: Evaluacion): Component = {
        val etiquetaNivel = new Label(s"Nivel: ${nombreNivel(evaluacion.nivel)}   -   ${evaluacion.puntaje} / 100")
        etiquetaNivel.foreground = colorNivel(evaluacion.nivel)

        val barra = new ProgressBar()
        barra.min = 0
        barra.max = 100
        barra.value = evaluacion.puntaje

        val lineas: List[String] = evaluacion.resultados.map(resultado => lineaDeRegla(resultado))
        val detalle = new TextArea(lineas.mkString("\n"))
        detalle.editable = false

        val arriba = new BoxPanel(Orientation.Vertical) {
            contents += etiquetaNivel
            contents += barra
        }

        new BorderPanel {
            layout(arriba) = BorderPanel.Position.North
            layout(new ScrollPane(detalle)) = BorderPanel.Position.Center
        }
    }
}
