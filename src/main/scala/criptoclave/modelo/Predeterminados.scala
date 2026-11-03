package criptoclave.modelo



object Predeterminados {

    val rsa: Rsa = Rsa(61, 53, 17)

    val metodos: List[Metodo] = List(
        Cesar(3),
        Vigenere("Clave"),
        Sustitucion("Clave"),
        Huffman,
        rsa
    )
}