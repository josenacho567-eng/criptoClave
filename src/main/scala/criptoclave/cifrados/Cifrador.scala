package criptoclave.cifrados

import criptoclave.modelo.Metodo
import criptoclave.modelo.Resultado
import criptoclave.modelo.Exito

object Cifrador {
    def aplicar(metodo: Metodo, clave: String): Resultado[String] = {
        metodo match
            case criptoclave.modelo.Cesar(desplazamiento) => Exito[String](criptoclave.cifrados.Cesar.aplicarCifrado(clave, desplazamiento))
            case criptoclave.modelo.Vigenere(llave) => Exito[String](criptoclave.cifrados.Vigenere.aplicarCifrado(clave, llave))
            case criptoclave.modelo.Sustitucion(llave) => Exito[String](criptoclave.cifrados.Sustitucion.aplicarCifrado(clave, llave))
    }
}