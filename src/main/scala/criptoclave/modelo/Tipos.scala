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
sealed trait Resultado[+A] {

    def map[B](f: A => B): Resultado[B] = {
        this match {
            case Exito(valor)  => Exito(f(valor))
            case Fallo(motivo) => Fallo(motivo)
        }
    }

    def flatMap[B](f: A => Resultado[B]): Resultado[B] = {
        this match {
            case Exito(valor)  => f(valor)
            case Fallo(motivo) => Fallo(motivo)
        }
    }

    def getOrElse[B >: A](porDefecto: => B): B = {
        this match {
            case Exito(valor) => valor
            case Fallo(_)     => porDefecto
        }
    }
}

case class Exito[+A](
    val valor: A
) extends Resultado[A]

case class Fallo(
    val motivo: String
) extends Resultado[Nothing]

