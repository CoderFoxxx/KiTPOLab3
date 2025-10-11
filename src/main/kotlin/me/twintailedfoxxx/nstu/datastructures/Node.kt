package me.twintailedfoxxx.nstu.datastructures

import java.io.Serializable

data class Node<T>(val data: T, var next: Node<T>? = null) : Serializable