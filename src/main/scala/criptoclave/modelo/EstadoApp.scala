package criptoclave.modelo

sealed trait EstadoApp

case object Inicio extends EstadoApp

case class Evaluada(
    contrasena: String,
    evaluacion: Evaluacion
) extends EstadoApp

case class ConPruebas(
    contrasena: String,
    evaluacion: Evaluacion,
    pruebas: List[Prueba]
) extends EstadoApp