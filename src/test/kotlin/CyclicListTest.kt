package me.twintailedfoxxx.nstu.datastructures

import me.twintailedfoxxx.nstu.types.MyInteger
import me.twintailedfoxxx.nstu.types.PolarVector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.io.File

class CyclicListTest {
    private lateinit var integerList: CyclicList<Any>
    private lateinit var vectorList: CyclicList<Any>
    private val integerType = MyInteger()
    private val vectorType = PolarVector()

    @BeforeEach
    fun setup() {
        integerList = CyclicList()
        vectorList = CyclicList()
    }

    @Test
    fun `test add and get integers`() {
        integerList.add(integerType.parseValue("5"))
        integerList.add(integerType.parseValue("10"))
        integerList.add(integerType.parseValue("15"))

        assertEquals("Целое число: 5", integerList.get(0).toString())
        assertEquals("Целое число: 10", integerList.get(1).toString())
        assertEquals("Целое число: 15", integerList.get(2).toString())
        assertEquals(3, integerList.size())
    }

    @Test
    fun `test add and get vectors`() {
        vectorList.add(vectorType.parseValue("5.0 45.0"))
        vectorList.add(vectorType.parseValue("3.0 90.0"))

        assertEquals("Длина: 5.0, Угол: 45.0°", vectorList.get(0).toString())
        assertEquals("Длина: 3.0, Угол: 90.0°", vectorList.get(1).toString())
        assertEquals(2, vectorList.size())
    }

    @Test
    fun `test insert at index`() {
        integerList.add(integerType.parseValue("1"))
        integerList.add(integerType.parseValue("3"))
        integerList.add(1, integerType.parseValue("2"))

        assertEquals("Целое число: 1", integerList.get(0).toString())
        assertEquals("Целое число: 2", integerList.get(1).toString())
        assertEquals("Целое число: 3", integerList.get(2).toString())
    }

    @Test
    fun `test remove element`() {
        vectorList.add(vectorType.parseValue("1.0 0.0"))
        vectorList.add(vectorType.parseValue("2.0 45.0"))
        vectorList.add(vectorType.parseValue("3.0 90.0"))

        vectorList.remove(1)
        assertEquals(2, vectorList.size())
        assertEquals("Длина: 1.0, Угол: 0.0°", vectorList.get(0).toString())
        assertEquals("Длина: 3.0, Угол: 90.0°", vectorList.get(1).toString())
    }

    @Test
    fun `test imperative sort integers`() {
        integerList.add(integerType.parseValue("3"))
        integerList.add(integerType.parseValue("1"))
        integerList.add(integerType.parseValue("2"))

        integerList.sort(integerType.comparator())

        assertEquals("Целое число: 1", integerList.get(0).toString())
        assertEquals("Целое число: 2", integerList.get(1).toString())
        assertEquals("Целое число: 3", integerList.get(2).toString())
    }

//    @Test
//    fun `test functional sort vectors`() {
//        vectorList.add(vectorType.parseValue("3.0 0.0"))
//        vectorList.add(vectorType.parseValue("1.0 45.0"))
//        vectorList.add(vectorType.parseValue("2.0 90.0"))
//
//        //vectorList.sortFunctional(vectorType.comparator())
//
//        assertEquals("Длина: 1.0, Угол: 45.0°", vectorList.get(0).toString())
//        assertEquals("Длина: 2.0, Угол: 90.0°", vectorList.get(1).toString())
//        assertEquals("Длина: 3.0, Угол: 0.0°", vectorList.get(2).toString())
//    }

    @Test
    fun `test serialization and deserialization`() {
        integerList.add(integerType.parseValue("1"))
        integerList.add(integerType.parseValue("2"))

        val tempFile = File.createTempFile("test", ".bin")
        tempFile.deleteOnExit()

        integerList.serialize(tempFile.absolutePath)
        val deserializedList = CyclicList.deserialize<Any>(tempFile.absolutePath)

        assertNotNull(deserializedList)
        assertEquals(2, deserializedList!!.size())
        assertEquals("Целое число: 1", deserializedList.get(0).toString())
        assertEquals("Целое число: 2", deserializedList.get(1).toString())
    }

    @Test
    fun `test toList conversion`() {
        vectorList.add(vectorType.parseValue("1.0 0.0"))
        vectorList.add(vectorType.parseValue("2.0 45.0"))

        val list = vectorList.toList()
        assertEquals(2, list.size)
        assertEquals("Длина: 1.0, Угол: 0.0°", list[0].toString())
        assertEquals("Длина: 2.0, Угол: 45.0°", list[1].toString())
    }

    @Test
    fun `test forEach`() {
        val elements = mutableListOf<String>()
        integerList.add(integerType.parseValue("1"))
        integerList.add(integerType.parseValue("2"))

        integerList.forEach { elements.add(it.toString()) }

        assertEquals(2, elements.size)
        assertEquals("Целое число: 1", elements[0])
        assertEquals("Целое число: 2", elements[1])
    }

    @Test
    fun `test invalid index operations`() {
        integerList.add(integerType.parseValue("1"))

        assertThrows(IllegalArgumentException::class.java) {
            integerList.get(-1)
        }

        assertThrows(IllegalArgumentException::class.java) {
            integerList.get(1)
        }

        assertThrows(IllegalArgumentException::class.java) {
            integerList.remove(1)
        }
    }
}