package criptoclave.modelo

sealed trait Metodo


case object Cesar extends  Metodo

case object Vigenere extends Metodo

case object Sustitucion extends Metodo

case object Huffman extends Metodo

case object RSA extends Metodo