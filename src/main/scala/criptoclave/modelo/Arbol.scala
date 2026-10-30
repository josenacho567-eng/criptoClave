package criptoclave.modelo

sealed trait Arbol[A]{
    def frecuencia: Int
}
case class Hoja[A](
    val caracter: A,
    val frecuencia: Int
) extends Arbol[A]

case class Nodo[A](
    val izquerda: Arbol[A],
    val derecha: Arbol[A]
) extends Arbol[A] {
    def frecuencia: Int = izquerda.frecuencia + derecha.frecuencia
}