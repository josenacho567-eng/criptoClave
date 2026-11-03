package criptoclave.cifrados
import criptoclave.modelo.Hoja
import criptoclave.modelo.Arbol
import criptoclave.modelo.Nodo
import scala.annotation.tailrec

object Huffman {
    def contarCaracter(clave: String, caracter: Char, acum: Int = 0): Int = {
        if clave.length == 1 then {
            if clave.head == caracter then acum +1
                else acum
            }
        else if clave.head == caracter then contarCaracter(clave.tail, caracter, acum +1)
        else contarCaracter(clave.tail, caracter, acum)
    }

    def contarCaracteres(clave: String): List[Hoja[Char]] = {
        val caracteres = clave.distinct.toList
        val tamano: Int = caracteres.length
        @tailrec
        def contarCaracteresDeLaLista(clave: String, listaChar: List[Char], tamano: Int, contador: Int = 0, acum: List[Hoja[Char]] = Nil): List[Hoja[Char]] = {
            if contador == tamano then acum
            else contarCaracteresDeLaLista(clave, listaChar, tamano, contador + 1, Hoja(listaChar(contador), contarCaracter(clave, listaChar(contador)))::acum)
            
        }
        contarCaracteresDeLaLista(clave, caracteres, tamano)
    }
    
    def construirArbol(lista: List[Arbol[Char]]): Arbol[Char] = {
        val listaOrdenada = lista.sortBy(hoja => hoja.frecuencia)
        listaOrdenada match
            case primero :: segundo :: demas => construirArbol((Nodo(primero,segundo)::demas))
            case ultimo :: Nil => ultimo
    }
    
    def generarCodigos(arbol: Arbol[Char], prefijo: String = ""): Map[Char, String] = {
        arbol match
            case Hoja(caracter, _) => Map(caracter -> prefijo)
            case Nodo(izquierda, derecha) => generarCodigos(izquierda, s"${prefijo}0") ++ generarCodigos(derecha, s"${prefijo}1")
    }

    def codificar(texto: String, codigos: Map[Char, String]): String = {
        texto.foldLeft("")((acum, caracter) => s"$acum${codigos(caracter)}")
    }

    def aplicarCifrado(clave: String): (String, Arbol[Char]) = {
        val arbol = construirArbol(contarCaracteres(clave))
        val codigos = arbol match {
            case Hoja(caracter, _) => Map(caracter -> "0")
            case _ => generarCodigos(arbol)
        }
        val cifrado = codificar(clave,codigos) 
        (cifrado, arbol)
    }

    def revertirCifrado(clave: String, arbol: Arbol[Char]): String = {
    arbol match {
        case Hoja(caracter, _) => clave.map(_ => caracter)
        case _ => decodificar(clave, arbol, arbol)
        }
    }

    def decodificar(clave: String, arbol: Arbol[Char], arbol2: Arbol[Char], acum: String = ""): String = {
        arbol match
            case Nodo(izquierda, derecha) => {
                if clave.isEmpty() then acum
                else if clave.head == '0' then decodificar(clave.tail, izquierda, arbol2, acum)
                else decodificar(clave.tail, derecha, arbol2, acum)
            }
            case Hoja(caracter, frecuencia) => {
                if clave.isEmpty then (if acum == "" then s"$caracter" else s"$acum$caracter")
                else decodificar(clave, arbol2, arbol2, if acum == "" then s"$caracter" else s"$acum$caracter" )
            }
    }

}