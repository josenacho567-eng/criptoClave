package criptoclave.cifrados

import criptoclave.cifrados.Alfabetos.desplazar
import criptoclave.cifrados.Alfabetos.tamano

object Cesar {
    def aplicarCifrado(clave: String, desplazamiento: Int, acum: String = ""): String = {
        if clave.length() == 1 then {
            val caracterDesplazado: Char = desplazar(clave.head, desplazamiento)
            s"$acum$caracterDesplazado"
        } else {
            val caracterDesplazado: Char = desplazar(clave.head, desplazamiento)
            if acum == "" then aplicarCifrado(clave.tail, desplazamiento, s"$caracterDesplazado")
            else aplicarCifrado(clave.tail, desplazamiento, s"$acum$caracterDesplazado")
        }

    }

    def revertirCifrado(clave: String, desplazamiento: Int, acum: String = ""): String = {
        if clave.length() == 1 then {
            val caracterDesplazado: Char = desplazar(clave.head, tamano - desplazamiento)
            s"$acum$caracterDesplazado"
        } else {
            val caracterDesplazado: Char = desplazar(clave.head, tamano - desplazamiento)
            if acum == "" then aplicarCifrado(clave.tail, tamano - desplazamiento, s"$caracterDesplazado")
            else aplicarCifrado(clave.tail, tamano - desplazamiento, s"$acum$caracterDesplazado")
        }
    }

}