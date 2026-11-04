package criptoclave.ui

import criptoclave.flujo.Flujo
import criptoclave.modelo.*
import java.awt.event.ActionEvent
import scala.swing.*

// Pantalla 1: el usuario escribe la clave y ve su fortaleza
object PantallaEvaluar {

    private def clavePrevia(estado: EstadoApp): String = {
        estado match {
            case Inicio                  => ""
            case Evaluada(clave, _)      => clave
            case ConPruebas(clave, _, _) => clave
        }
    }

    private def consejo(contenido: String): Component = {
        Componentes.texto(s"•   $contenido", 15, false, Tema.texto)
    }

    private def panelConsejos(): Component = {
        Componentes.tarjeta(
            Componentes.columna(
                Componentes.tituloSeccion("Consejos para una clave fuerte"),
                Swing.VStrut(14),
                consejo("Usa 12 caracteres o más."),
                Swing.VStrut(8),
                consejo("Mezcla mayúsculas, minúsculas, números y símbolos."),
                Swing.VStrut(8),
                consejo("Evita palabras comunes como \"password\" o \"hola\"."),
                Swing.VStrut(8),
                consejo("Después podrás probar métodos de cifrado y comparar el resultado.")
            ),
            22
        )
    }

    private def filaRegla(resultado: ResultadoRegla): Component = {
        val nombre = Componentes.texto(resultado.nombre, 15, true, Tema.texto)
        val lineas: List[Component] =
            if resultado.cumplida then List(nombre)
            else List(nombre, Componentes.texto(resultado.descripcion, 13, false, Tema.textoSuave))

        val puntos = Componentes.texto(
            if resultado.cumplida then s"+${resultado.puntosObtenidos}" else "",
            15,
            true,
            Tema.exito
        )

        new BorderPanel {
            opaque = false
            border = javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.borde),
                javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0)
            )
            layout(Componentes.marca(resultado.cumplida)) = BorderPanel.Position.West
            layout(Componentes.columna(lineas*)) = BorderPanel.Position.Center
            layout(puntos) = BorderPanel.Position.East
        }
    }

    private def panelResultado(evaluacion: Evaluacion, alContinuar: () => Unit): Component = {
        val color = Tema.colorNivel(evaluacion.nivel)
        val nivel = Componentes.pildora(
            s"Nivel ${Formato.nombreNivel(evaluacion.nivel)}",
            Tema.conAlfa(color, 30),
            color
        )
        val continuar = Componentes.botonPrimario("Elegir método de cifrado  →") { alContinuar() }

        val tarjetaMedidor = Componentes.tarjeta(
            Componentes.columnaCentrada(
                Componentes.texto("Fortaleza", 14, true, Tema.textoSuave),
                Swing.VStrut(6),
                Componentes.medidor(evaluacion),
                Swing.VStrut(8),
                nivel,
                Swing.VStrut(12),
                Componentes.textoLargo(Formato.mensajeNivel(evaluacion.nivel), 240, 14, Tema.textoSuave, true),
                Swing.VStrut(20),
                continuar
            ),
            22
        )

        val encabezadoReglas: List[Component] = List(
            Componentes.tituloSeccion("Reglas evaluadas"),
            Swing.VStrut(2),
            Componentes.texto(
                s"${evaluacion.cumplidas.length} de ${evaluacion.resultados.length} cumplidas",
                13,
                false,
                Tema.textoSuave
            ),
            Swing.VStrut(8)
        )
        val filas: List[Component] = evaluacion.resultados.map(resultado => filaRegla(resultado))
        val tarjetaReglas = Componentes.tarjeta(Componentes.columna((encabezadoReglas ++ filas)*), 22)

        val izquierda = new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(0, 0, 0, 18)
            layout(tarjetaMedidor) = BorderPanel.Position.Center
        }

        Componentes.arriba(new BorderPanel {
            opaque = false
            layout(izquierda) = BorderPanel.Position.West
            layout(tarjetaReglas) = BorderPanel.Position.Center
        })
    }

    def crear(estado: EstadoApp, alEvaluar: EstadoApp => Unit, alContinuar: () => Unit): Component = {
        val campo = new PasswordField(clavePrevia(estado), 30)
        val campoConOjo = Componentes.campoClave(campo, "Escribe tu clave aquí")

        val mensaje = Componentes.texto("", 14, true, Tema.peligro)

        def evaluar(): Unit = {
            Flujo.evaluar(new String(campo.password)) match {
                case Exito(nuevo)  => alEvaluar(nuevo)
                case Fallo(motivo) => mensaje.text = motivo
            }
        }

        // Enter evalúa (no se usa EditDone porque también se dispara al perder el foco)
        campo.peer.addActionListener((evento: ActionEvent) => evaluar())

        val botonEvaluar = Componentes.botonPrimario("Evaluar") { evaluar() }

        val botonConMargen = new BorderPanel {
            opaque = false
            border = Swing.EmptyBorder(0, 12, 0, 0)
            layout(botonEvaluar) = BorderPanel.Position.Center
        }

        val filaEntrada = new BorderPanel {
            opaque = false
            layout(campoConOjo) = BorderPanel.Position.Center
            layout(botonConMargen) = BorderPanel.Position.East
        }

        val entrada = Componentes.tarjeta(
            Componentes.columna(
                Componentes.texto("Tu clave", 14, true, Tema.textoSuave),
                Swing.VStrut(8),
                filaEntrada,
                Swing.VStrut(8),
                mensaje
            ),
            20
        )

        val resultado: Component = estado match {
            case Inicio                       => Componentes.arriba(panelConsejos())
            case Evaluada(_, evaluacion)      => panelResultado(evaluacion, alContinuar)
            case ConPruebas(_, evaluacion, _) => panelResultado(evaluacion, alContinuar)
        }

        val parteSuperior = Componentes.columna(
            Componentes.encabezado("Evalúa tu clave", "Escribe una clave y descubre qué tan fuerte es."),
            entrada,
            Swing.VStrut(20)
        )

        // Deja el cursor listo para escribir apenas aparece la pantalla
        javax.swing.SwingUtilities.invokeLater(new Runnable {
            def run(): Unit = {
                campo.peer.requestFocusInWindow()
            }
        })

        new BorderPanel {
            opaque = false
            layout(parteSuperior) = BorderPanel.Position.North
            layout(resultado) = BorderPanel.Position.Center
        }
    }
}
