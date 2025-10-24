package me.twintailedfoxxx.nstu.types

import com.google.gson.JsonParser

class MyInteger(private val value: Int = 0) : IUserType {
    override fun typeName(): String = "Integer"
    override fun create(): Any = MyInteger()
    override fun clone(): Any = MyInteger(value)
    override fun parseValue(value: String): Any = MyInteger(value.toInt())

    override fun comparator(): Comparator<Any> =
        compareBy {
            (it as MyInteger).value
        }

    override fun parseFromJson(json: String): Any {
        var trimmedJson = json.trim()

        if (trimmedJson.startsWith("\"") && trimmedJson.endsWith("\"")) {
            trimmedJson = trimmedJson.substring(1, trimmedJson.length - 1)
        }

        if (trimmedJson.startsWith("{")) {
            val parsed = JsonParser.parseString(trimmedJson).asJsonObject
            val `val` = parsed["value"].asDouble
            return MyInteger(`val`.toInt())
        } else {
            return MyInteger(value)
        }
    }

    override fun toString(): String = "Целое число: $value"
}