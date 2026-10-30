package criptoclave.modelo

sealed trait Arbol[A]{
    def frecuencia: Int
}
case class Hoja[A](
    val caracter: A,
    val frecuencia: Int
) extends Arbol[A]

case class Nodo[A](
    val izquierda: Arbol[A],
    val derecha: Arbol[A] 
) extends Arbol[A] {
    val frecuencia: Int = izquierda.frecuencia + derecha.frecuencia
}