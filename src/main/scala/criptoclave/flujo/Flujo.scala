package criptoclave.flujo

import criptoclave.cifrados.Cifrador
import criptoclave.evaluacion.Evaluador
import criptoclave.modelo.*

object Flujo {

    // Criterios de ordenamiento (funciones que se pasan a probarTodos)
    val porPuntaje: Prueba => Int = prueba => prueba.evaluacion.puntaje
    val porDiferencia: Prueba => Int = prueba => prueba.diferencia

    // Transiciones de estado
    def evaluar(contrasena: String): Resultado[EstadoApp] = {
        Evaluador.evaluar(contrasena).map(evaluacion => Evaluada(contrasena, evaluacion))
    }

    def probar(estado: EstadoApp, metodo: Metodo): Resultado[EstadoApp] = {
        baseDe(estado).flatMap[EstadoApp] { case (contrasena, original) =>
            crearPrueba(contrasena, original, metodo).map(prueba =>
                ConPruebas(contrasena, original, estado.pruebas :+ prueba)
            )
        }
    }

    def probarTodos(estado: EstadoApp, metodos: List[Metodo], criterio: Prueba => Int): Resultado[EstadoApp] = {
        baseDe(estado).flatMap[EstadoApp] { case (contrasena, original) =>
            val ranking = metodos
                .flatMap(metodo => crearPrueba(contrasena, original, metodo) match {
                    case Exito(prueba) => List(prueba)
                    case Fallo(_)      => Nil
                })
                .sortBy(prueba => -criterio(prueba))
            if ranking.isEmpty then Fallo("Ningún método se pudo aplicar")
            else Exito(ConPruebas(contrasena, original, ranking))
        }
    }

    def reiniciar(): EstadoApp = Inicio

    // Revertir una prueba
    def revertir(prueba: Prueba): Resultado[String] = {
        Cifrador.revertir(prueba.metodo, prueba.salida, prueba.datos)
    }

    // Auxiliares
    private def baseDe(estado: EstadoApp): Resultado[(String, Evaluacion)] = {
        estado match {
            case Inicio                                => Fallo("Primero evalúa una contraseña")
            case Evaluada(contrasena, evaluacion)      => Exito((contrasena, evaluacion))
            case ConPruebas(contrasena, evaluacion, _) => Exito((contrasena, evaluacion))
        }
    }

    private def crearPrueba(contrasena: String, original: Evaluacion, metodo: Metodo): Resultado[Prueba] = {
        for {
            cifrado    <- Cifrador.aplicar(metodo, contrasena)
            evaluacion <- Evaluador.evaluar(cifrado.texto)
        } yield Prueba(
            metodo,
            cifrado.texto,
            cifrado.datos,
            evaluacion,
            evaluacion.puntaje - original.puntaje
        )
    }
}