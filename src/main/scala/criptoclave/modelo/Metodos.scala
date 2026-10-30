package criptoclave.modelo

sealed trait Metodo


case class Cesar(
    desplazamiento: Int
) extends Metodo

case class Vigenere(
    clave: String
) extends Metodo

case class Sustitucion(
    clave: String
) extends Metodo

case class Rsa(
    p: BigInt,
    q: BigInt,
    e: BigInt
) extends Metodo

case object Huffman extends Metodo