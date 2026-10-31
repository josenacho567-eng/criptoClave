package criptoclave.modelo


sealed trait Nivel

case object Debil extends Nivel

case object Medio extends  Nivel

case object Fuerte extends Nivel
////////////////////////////////
sealed trait Veredicto

case object Mejoro extends Veredicto

case object Empeoro extends Veredicto

case object Igual extends Veredicto

////////////////////////////////////
sealed trait Resultado[+A]

case class Exito[+A](
    val valor: A
) extends Resultado[A]

case class Fallo(
    val motivo: String
) extends Resultado[Nothing]

