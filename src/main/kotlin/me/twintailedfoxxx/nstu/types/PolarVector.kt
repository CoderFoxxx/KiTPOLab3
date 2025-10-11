package me.twintailedfoxxx.nstu.types

class PolarVector(private val length: Double = 0.0, private val angle: Double = 0.0) : IUserType {
    override fun typeName(): String = "PolarVector"
    override fun create(): Any = PolarVector()
    override fun clone(): Any = PolarVector(length, angle)

    override fun parseValue(value: String): Any {
        val parts: List<String> = value.trim().split(" ")
        println(parts)
        if(parts.size != 2) {
            throw IllegalArgumentException("Требуется два числа (длина и угол), разделенные пробелом")
        }

        val length = parts[0].toDouble()
        val angle = parts[1].toDouble()

        return PolarVector(length, angle)
    }

    override fun comparator(): Comparator<Any> =
        compareBy {
            (it as PolarVector).length
        }

    override fun toString(): String = "Длина: $length, Угол: $angle°"
}