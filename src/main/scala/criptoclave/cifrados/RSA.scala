package criptoclave.cifrados
import criptoclave.cifrados.Alfabetos.caracterEn
import criptoclave.cifrados.Alfabetos.indice
import criptoclave.modelo.ClavePrivada
import scala.math.BigInt
import criptoclave.cifrados.Alfabetos.contiene

object RSA {
    def calcLlavePrivada(d1: BigInt, d2: BigInt, d3: BigInt, d4: BigInt, phi: BigInt): BigInt = {
        val d5 = d1 / d3
        val d6 = d5 *d3
        val d7 = d5 *d4
        val d8 = d1 - d6 
        val d9 = if d2 - d7 <0 then (((d2-d7) % phi) + phi) % phi else d2 - d7
        
        if d8 == 1 then d9 else calcLlavePrivada(d3, d4, d8, d9, phi)
    }

    
    def aplicarCifrado(p: BigInt, q: BigInt, e: BigInt, clave: String): (String, ClavePrivada) = {
        val n = p * q
        val phi = (p - 1)*(q -1)
        val d = calcLlavePrivada(phi, phi, e, 1, phi)
        val claveCodificada: String = clave.foldLeft(""){
            (acum, caracter) => {
                if acum == "" then s"${BigInt(caracter.toInt).modPow(e,n)}" else s"$acum ${BigInt(caracter.toInt).modPow(e,n)}"
            }
        }
        (claveCodificada, ClavePrivada(d, n))
    }

    def revertirCifrado(clavePriv: ClavePrivada, clave: String): String = {
        val claveDecodificada = clave.split(" ")
        val arregloCodigos = claveDecodificada.foldLeft(""){
            (acum, elemento) => {
                val codigo = BigInt(elemento)
                if acum == "" then s"${codigo.modPow(clavePriv.d,clavePriv.n).toChar}" else s"$acum${codigo.modPow(clavePriv.d,clavePriv.n).toChar}"
            }
        }
        arregloCodigos
    }

    def main(args: Array[String]): Unit = {
        println(aplicarCifrado(17, 19, 5, "TungTungTungSahur67"))
        println(revertirCifrado(ClavePrivada(173,323),"50 53 230 69 50 53 230 69 50 53 230 69 87 241 168 53 190 175 310"))
    }
}