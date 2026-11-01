package criptoclave.cifrados

import criptoclave.cifrados.Alfabetos.indice
import criptoclave.cifrados.Alfabetos.tamano
import criptoclave.cifrados.Alfabetos.caracterEn
import criptoclave.cifrados.Alfabetos.desplazar
import scala.annotation.tailrec

object Vigenere {
    def extenderLlave(llave: String, diferencia: Int, acum: String, contador: Int = 0): String = {
        if diferencia == 0 then acum
        else extenderLlave(llave, diferencia - 1, s"$acum${llave(contador%llave.length())}", contador + 1)
    }

    def aplicarCifrado(clave: String, llave: String): String = {
        @tailrec
        def aplicarCifradoAux(clave: String, llave: String, acum: String = "", pos: Int = 0): String = {
            val llaveEnPos: Char = llave(pos)
            val caracterAux: Char = desplazar(clave.head, indice(llaveEnPos))
            if clave.length() == 1 then s"$acum$caracterAux"
            else {
                if acum == "" then aplicarCifradoAux(clave.tail, llave, s"$caracterAux", pos+1)
                else aplicarCifradoAux(clave.tail, llave, s"$acum$caracterAux", pos+1)
            }
        }
        if clave.length() > llave.length() then aplicarCifradoAux(clave, extenderLlave(llave, clave.length() - llave.length(), llave))
        else aplicarCifradoAux(clave, llave)
    }

    def revertirCifrado(clave: String, llave: String, acum: String = "", pos: Int = 0): String = {
        @tailrec
        def revertirCifradoAux(clave: String, llave: String, acum: String = "", pos: Int = 0): String = {
            val llaveEnPos: Char = llave(pos)
            val caracterAux: Char = desplazar(clave.head, -indice(llaveEnPos))
            if clave.length() == 1 then s"$acum$caracterAux"
            else {
                if acum == "" then revertirCifradoAux(clave.tail, llave, s"$caracterAux", pos+1)
                else revertirCifradoAux(clave.tail, llave, s"$acum$caracterAux", pos+1)
            }
        }
        if clave.length() > llave.length() then revertirCifradoAux(clave, extenderLlave(llave, clave.length() - llave.length(), llave))
        else revertirCifradoAux(clave, llave)
    }

    def main(args: Array[String]): Unit = {
        println(aplicarCifrado("ho la ", "zzza"))
        println(revertirCifrado("äëèÄ", "zzza"))
        println(revertirCifrado(aplicarCifrado("ho la ", "zzza"), "zzza"))
        println(aplicarCifrado("paris", "loup"))
        println(revertirCifrado(aplicarCifrado("paris", "loup"), "loup"))
        println(revertirCifrado("??é?á", "loup"))
        println(aplicarCifrado("Sol#2024", "Clave"))

    }
}