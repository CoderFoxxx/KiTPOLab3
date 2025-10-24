package me.twintailedfoxxx.nstu.types

import com.google.gson.JsonParser

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

    override fun parseFromJson(json: String): Any {
        var trimmedJson = json.trim { it <= ' ' }

        if (trimmedJson.startsWith("\"") && trimmedJson.endsWith("\"")) {
            trimmedJson = trimmedJson.substring(1, trimmedJson.length - 1)
        }

        if (trimmedJson.startsWith("{")) {
            val json = JsonParser.parseString(trimmedJson).asJsonObject
            val len = json["length"].asDouble
            val ang = json["angle"].asDouble
            return PolarVector(len, ang)
        } else {
            val parts: Array<String> = trimmedJson.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            require(parts.size == 2) { "Требуется два числа вщ" }
            val len = parts[0].toDouble()
            val ang = parts[1].toDouble()
            return PolarVector(len, ang)
        }
    }

    override fun comparator(): Comparator<Any> =
        compareBy {
            (it as PolarVector).length
        }

    override fun toString(): String = "Длина: $length, Угол: $angle°"
}