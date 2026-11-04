package criptoclave.flujo

import criptoclave.cifrados.Cifrador
import criptoclave.evaluacion.Evaluador
import criptoclave.modelo.*

object Flujo {

    val porPuntaje: Prueba => Int = prueba => prueba.evaluacion.puntaje
    val porDiferencia: Prueba => Int = prueba => prueba.diferencia

    def baseDe(estado: EstadoApp): Resultado[(String, Evaluacion)] = {
        estado match {
            case Inicio                                => Fallo("Primero evalúa una clave")
            case Evaluada(clave, evaluacion)      => Exito((clave, evaluacion))
            case ConPruebas(clave, evaluacion, _) => Exito((clave, evaluacion))
        }
    }

    def crearPrueba(clave: String, original: Evaluacion, metodo: Metodo): Resultado[Prueba] = {
        for {
            cifrado    <- Cifrador.aplicar(metodo, clave)
            evaluacion <- Evaluador.evaluar(cifrado.texto)
        } yield Prueba(
            metodo,
            cifrado.texto,
            cifrado.datos,
            evaluacion,
            evaluacion.puntaje - original.puntaje
        )
    }

    def evaluar(clave: String): Resultado[EstadoApp] = {
        Evaluador.evaluar(clave).map(evaluacion => Evaluada(clave, evaluacion))
    }

    def probar(estado: EstadoApp, metodo: Metodo): Resultado[EstadoApp] = {
        baseDe(estado).flatMap[EstadoApp] { case (clave, original) =>
            crearPrueba(clave, original, metodo).map(prueba =>
                ConPruebas(clave, original, estado.pruebas :+ prueba)
            )
        }
    }

    def probarTodos(estado: EstadoApp, metodos: List[Metodo], criterio: Prueba => Int): Resultado[EstadoApp] = {
        baseDe(estado).flatMap[EstadoApp] { case (clave, original) =>
            val ranking = metodos
                .flatMap(metodo => crearPrueba(clave, original, metodo) match {
                    case Exito(prueba) => List(prueba)
                    case Fallo(_)      => Nil
                })
                .sortBy(prueba => -criterio(prueba))
            if ranking.isEmpty then Fallo("Ningún método se pudo aplicar")
            else Exito(ConPruebas(clave, original, ranking))
        }
    }

    def reiniciar(): EstadoApp = Inicio

    def revertir(prueba: Prueba): Resultado[String] = {
        Cifrador.revertir(prueba.metodo, prueba.salida, prueba.datos)
    }
    
}