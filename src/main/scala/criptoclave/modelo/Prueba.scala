package criptoclave.modelo

case class Prueba(
    metodo: Metodo,
    salida: String,
    datos: Option[DatosReversion],
    evaluacion: Evaluacion,
    diferencia: Int
){
    def veredicto: Veredicto ={
        if diferencia > 0 then Mejoro
        else if diferencia < 0 then Empeoro
        else Igual
    }
}
