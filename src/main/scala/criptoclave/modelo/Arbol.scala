package criptoclave.modelo

sealed trait Arbol[A]

case class Hoja[A](
    val caracter: A,
    val frecuencia: Int,
    
    

)