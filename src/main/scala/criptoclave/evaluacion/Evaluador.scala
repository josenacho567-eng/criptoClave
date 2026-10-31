package criptoclave.evaluacion

import criptoclave.evaluacion.Reglas.reglas
import criptoclave.modelo.ResultadoRegla
import criptoclave.modelo.Nivel
import criptoclave.modelo.Debil
import criptoclave.modelo.Medio
import criptoclave.modelo.Fuerte
import criptoclave.modelo.Evaluacion
import criptoclave.modelo.Fallo
import criptoclave.modelo.Exito
import criptoclave.modelo.Resultado

object Evaluador{


    def aplicarReglas(texto: String): List[ResultadoRegla] = {
        
        reglas.map(elemento => {
            val cumplida: Boolean = elemento.condicion(texto)
            ResultadoRegla(elemento.nombre,
            elemento.descripcion,
            cumplida,
            if cumplida then elemento.puntos else 0
            )
        })
    }

    def sumarPuntajes(resultados: List[ResultadoRegla]): Int = {
        resultados.foldLeft(0){
            (acumulador,elemento) => {
                acumulador + elemento.puntosObtenidos
            }
        }
    }


    def clasificar(puntaje: Int): Nivel = {
        puntaje match
            case puntaje if puntaje < 40 => Debil
            case puntaje if puntaje < 70 => Medio
            case _ => Fuerte 
        
    }

    def evaluar(texto: String): Resultado[Evaluacion] = {
        if texto.isEmpty then Fallo("Escribe una contraseña")
        else {
            val resultados = aplicarReglas(texto)
            val puntaje    = sumarPuntajes(resultados)
            Exito(Evaluacion(puntaje, clasificar(puntaje), resultados))
        }
    }
}