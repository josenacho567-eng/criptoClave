package criptoclave.ui

import criptoclave.modelo.*

// Textos que se muestran a partir de los datos del modelo
object Formato {

    def nombreMetodo(metodo: Metodo): String = {
        metodo match {
            case Cesar(desplazamiento) => s"César (desplazamiento $desplazamiento)"
            case Vigenere(llave)       => s"Vigenère (llave $llave)"
            case Sustitucion(llave)    => s"Sustitución (llave $llave)"
            case Huffman               => "Huffman"
            case Rsa(p, q, e)          => s"RSA (p=$p, q=$q, e=$e)"
        }
    }

    def nombreCorto(metodo: Metodo): String = {
        metodo match {
            case Cesar(_)        => "César"
            case Vigenere(_)     => "Vigenère"
            case Sustitucion(_)  => "Sustitución"
            case Huffman         => "Huffman"
            case Rsa(_, _, _)    => "RSA"
        }
    }

    def nombreNivel(nivel: Nivel): String = {
        nivel match {
            case Debil  => "Débil"
            case Medio  => "Medio"
            case Fuerte => "Fuerte"
        }
    }

    def mensajeNivel(nivel: Nivel): String = {
        nivel match {
            case Debil  => "Es fácil de adivinar. Mejórala con las sugerencias de la derecha."
            case Medio  => "Es aceptable, pero todavía puede ser más fuerte."
            case Fuerte => "¡Excelente! Esta clave es difícil de adivinar."
        }
    }

    def textoVeredicto(veredicto: Veredicto): String = {
        veredicto match {
            case Mejoro  => "Mejoró"
            case Empeoro => "Empeoró"
            case Igual   => "Igual"
        }
    }

    def conSigno(numero: Int): String = {
        if numero > 0 then s"+$numero" else s"$numero"
    }

    def resumir(texto: String, maximo: Int): String = {
        if texto.length <= maximo then texto
        else texto.take(maximo) + "..."
    }
}
