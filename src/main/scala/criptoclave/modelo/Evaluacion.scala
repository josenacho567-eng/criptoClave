package criptoclave.modelo

case class Regla[A](
    nombre: String,
    descripcion: String,
    puntos: Int,
    condicion: A => Boolean
)

case class ResultadoRegla(
    nombre: String,
    descripcion: String,
    cumplida: Boolean,
    puntosObtenidos: Int
)

case class Evaluacion(
    puntaje: Int,
    nivel: Nivel,
    resultados: List[ResultadoRegla]
){

    def cumplidas: List[ResultadoRegla] = {
        resultados.filter(regla => regla.cumplida)
    }
    def noCumplidas: List[ResultadoRegla] = {
        resultados.filter(regla => !regla.cumplida)
    }
    def mensajeErr: List[String] ={
        noCumplidas.map(regla => regla.descripcion)
    }


}