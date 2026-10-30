package criptoclave.modelo

sealed trait DatosReversion

case class ArbolHuffman(
    arbol: Arbol[Char]
) extends DatosReversion


case class ClavePrivada(
    d: BigInt,
    n: BigInt
) extends DatosReversion

