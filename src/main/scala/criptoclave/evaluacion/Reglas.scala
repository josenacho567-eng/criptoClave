package criptoclave.evaluacion

import criptoclave.modelo.Regla

object Reglas{
    val palabrasComunes: List[String] = List(
    "123456", "123456789", "12345678", "1234567890", "1234567", "12345",
    "123123", "123123123", "123321", "654321", "987654321", "111111",
    "11111111", "000000", "00000000", "666666", "88888888", "112233", "121212",
    "password", "password1", "qwerty", "qwerty1", "qwerty123", "qwerty123!",
    "qwerty1!", "qwertyuiop", "q1w2e3r4t5y6", "1q2w3e4r", "1q2w3e4r5t",
    "1qaz2wsx", "zag12wsx", "1g2w3e4r", "asdfghjkl", "abc123", "abcd1234",
    "a123456", "123456a", "iloveyou", "secret", "dragon", "monkey",
    "football", "target123", "tinkle", "gwerty", "gwerty123",
    "hola", "admin", "root", "contrasena", "admin@123", "pass123",
    "pass@123", "2026", "hola67", "triplet", "skibidi", "perros", "gatos", "aura67"
    )
    
    val reglas:List[Regla[String]] = List(
        Regla("Al menos 8 caracteres",
            "Usa al menos 8 caracteres",
            20,
            texto => texto.length >= 8),

        Regla("Al menos 12 caracteres",
            "Alarga la contraseña hasta 12 caracteres o más",
            15,
            texto => texto.length >= 12),

        Regla("Contiene mayúsculas",
            "Agrega una letra mayúscula",
            15,
            texto => texto.exists(letra => letra.isUpper)),

        Regla("Contiene minúsculas",
            "Agrega una letra minúscula",
            10,
            texto => texto.exists(letra => letra.isLower)),

        Regla("Contiene números",
            "Agrega un número",
            15,
            texto => texto.exists(letra => letra.isDigit)),

        Regla("Contiene símbolos",
            "Agrega un símbolo, como # o !",
            15,
            texto => texto.exists(letra => !letra.isLetterOrDigit && !letra.isWhitespace)),

        Regla("No es una palabra común",
            "Evita palabras comunes como \"password\" o \"hola\"",
            10,
            texto => !palabrasComunes.contains(texto.toLowerCase))
    )
}
