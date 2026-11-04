package criptoclave.modelo

sealed trait EstadoApp {

    def pruebas: List[Prueba]

    def mejorPrueba: Option[Prueba] = {
        pruebas.reduceOption((a, b) => if a.evaluacion.puntaje >= b.evaluacion.puntaje then a else b)
    }

    def mejoran: Int = {
        pruebas.count(prueba => prueba.veredicto == Mejoro)
    }

    def empeoran: Int = {
        pruebas.count(prueba => prueba.veredicto == Empeoro)
    }

    def algunaSubeDeNivel: Boolean = {
        this match {
            case ConPruebas(_, original, lista) =>
                lista.exists(prueba => ordenNivel(prueba.evaluacion.nivel) > ordenNivel(original.nivel))
            case _ => false
        }
    }

    private def ordenNivel(nivel: Nivel): Int = {
        nivel match {
            case Debil  => 0
            case Medio  => 1
            case Fuerte => 2
        }
    }
}

case object Inicio extends EstadoApp {
    val pruebas: List[Prueba] = Nil
}

case class Evaluada(
    contrasena: String,
    evaluacion: Evaluacion
) extends EstadoApp {
    val pruebas: List[Prueba] = Nil
}

case class ConPruebas(
    contrasena: String,
    evaluacion: Evaluacion,
    pruebas: List[Prueba]
) extends EstadoApp