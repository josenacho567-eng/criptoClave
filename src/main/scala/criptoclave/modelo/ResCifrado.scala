package criptoclave.modelo


case class Cifrado(
    texto: String,
    datos: Option[DatosReversion]
)