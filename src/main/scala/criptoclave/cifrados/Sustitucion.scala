package criptoclave.cifrados

import criptoclave.cifrados.Alfabetos.caracteres
import criptoclave.cifrados.Alfabetos.contiene

object Sustitucion {
    def alfabetoMezclado(clave: String): String = {
        val claveLimpia: String = clave.filter(c => contiene(c)).distinct
        val resto: String = caracteres.filter(c => !claveLimpia.contains(c))
        s"$claveLimpia$resto"
    }

    def tablaCifrado(clave: String): Map[Char, Char] = {
        caracteres.zip(alfabetoMezclado(clave)).toMap
    }

    def tablaReversion(clave: String): Map[Char, Char] = {
        alfabetoMezclado(clave).zip(caracteres).toMap
    }

    def aplicarCifrado(texto: String, clave: String): String = {
        val tabla: Map[Char, Char] = tablaCifrado(clave)
        texto.map(c => tabla.getOrElse(c, c))
    }

    def revertirCifrado(texto: String, clave: String): String = {
        val tabla: Map[Char, Char] = tablaReversion(clave)
        texto.map(c => tabla.getOrElse(c, c))
    }
}