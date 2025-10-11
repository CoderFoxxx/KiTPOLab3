package me.twintailedfoxxx.nstu.types

class MyInteger(private val value: Int = 0) : IUserType {
    override fun typeName(): String = "Integer"
    override fun create(): Any = MyInteger()
    override fun clone(): Any = MyInteger(value)
    override fun parseValue(value: String): Any = MyInteger(value.toInt())

    override fun comparator(): Comparator<Any> =
        compareBy {
            (it as MyInteger).value
        }

    override fun toString(): String = "Целое число: $value"
}