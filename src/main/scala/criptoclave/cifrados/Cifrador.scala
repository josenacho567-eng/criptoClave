package criptoclave.cifrados

import criptoclave.modelo.ArbolHuffman
import criptoclave.modelo.Cesar
import criptoclave.modelo.Cifrado
import criptoclave.modelo.ClavePrivada
import criptoclave.modelo.DatosReversion
import criptoclave.modelo.Exito
import criptoclave.modelo.Fallo
import criptoclave.modelo.Huffman
import criptoclave.modelo.Metodo
import criptoclave.modelo.Resultado
import criptoclave.modelo.Rsa
import criptoclave.modelo.Sustitucion
import criptoclave.modelo.Vigenere

object Cifrador {

    def llaveValida(llave: String): Boolean = {
        llave.nonEmpty && llave.foldLeft(true)((acum, caracter) => acum && Alfabetos.contiene(caracter))
    }

    def validar(metodo: Metodo, texto: String): Resultado[Unit] = {
        if texto.isEmpty then Fallo("Escribe una contraseña")
        else {
            metodo match {
                // Cesar puede Cifrar con desplazamiento negativo (va hacia atrás)
                case Vigenere(llave) if !llaveValida(llave) =>
                    Fallo("La clave de Vigenère debe tener al menos un carácter y todos válidos")
                case Sustitucion(llave) if !llaveValida(llave) =>
                    Fallo("La clave de sustitución debe tener al menos un carácter y todos válidos")
                case _ =>
                    Exito(())
            }
        }
    }

    def cifrar(metodo: Metodo, texto: String): Cifrado = {
        metodo match {
            case Cesar(desplazamiento) =>
                Cifrado(criptoclave.cifrados.Cesar.aplicarCifrado(texto, desplazamiento), None)
            case Vigenere(llave) =>
                Cifrado(criptoclave.cifrados.Vigenere.aplicarCifrado(texto, llave), None)
            case Sustitucion(llave) =>
                Cifrado(criptoclave.cifrados.Sustitucion.aplicarCifrado(texto, llave), None)
            case Huffman =>
                val (bits, arbol) = criptoclave.cifrados.Huffman.aplicarCifrado(texto)
                Cifrado(bits, Some(ArbolHuffman(arbol)))
            case Rsa(p, q, e) =>
                val (numeros, clavePrivada) = criptoclave.cifrados.RSA.aplicarCifrado( p, q, e,texto)
                Cifrado(numeros, Some(clavePrivada))
        }
    }

    def aplicar(metodo: Metodo, texto: String): Resultado[Cifrado] = {
        validar(metodo, texto).map(_ => cifrar(metodo, texto))
    }

    def revertir(metodo: Metodo, texto: String, datos: Option[DatosReversion]): Resultado[String] = {
        if texto.isEmpty then Fallo("No hay texto para revertir")
        else {
            (metodo, datos) match {
                case (Cesar(desplazamiento), _) =>
                    Exito(criptoclave.cifrados.Cesar.revertirCifrado(texto, desplazamiento))
                case (Vigenere(llave), _) =>
                    Exito(criptoclave.cifrados.Vigenere.revertirCifrado(texto, llave))
                case (Sustitucion(llave), _) =>
                    Exito(criptoclave.cifrados.Sustitucion.revertirCifrado(texto, llave))
                case (Huffman, Some(ArbolHuffman(arbol))) =>
                    Exito(criptoclave.cifrados.Huffman.revertirCifrado(texto, arbol))
                case (Rsa(_, _, _), Some(clavePrivada: ClavePrivada)) =>
                    Exito(criptoclave.cifrados.RSA.revertirCifrado(clavePrivada, texto))
                case (Huffman, _) =>
                    Fallo("Falta el árbol de Huffman para revertir")
                case (Rsa(_, _, _), _) =>
                    Fallo("Falta la clave privada de RSA para revertir")
            }
        }
    }
}