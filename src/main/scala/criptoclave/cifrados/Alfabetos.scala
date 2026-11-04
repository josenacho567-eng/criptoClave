package criptoclave.cifrados

object Alfabetos {
    val caracteres: String = ((33 to 126) ++ (161 to 255))
        .filter(codigo => codigo != 173)
        .map(codigo => codigo.toChar)
        .mkString
    
    val tamano: Int = caracteres.length

    def contiene(caracter: Char): Boolean = {
        caracteres.contains(caracter)
    }

    def indice(caracter: Char): Int = {
        caracteres.indexOf(caracter)
    }
    
    def caracterEn(posicion: Int): Char = {
        val ajustada = ((posicion % tamano) + tamano) % tamano
        caracteres(ajustada)
    }

    def desplazar(caracter: Char, cantidad: Int): Char = {
        if contiene(caracter) then caracterEn(indice(caracter) + cantidad)
        else caracter
    }
}