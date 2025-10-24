package me.twintailedfoxxx.nstu.types

import java.io.Serializable

interface IUserType : Serializable {
    fun typeName() : String
    fun create() : Any
    fun clone() : Any
    fun parseValue(value: String) : Any
    fun parseFromJson(json: String) : Any
    fun comparator() : Comparator<Any>
}