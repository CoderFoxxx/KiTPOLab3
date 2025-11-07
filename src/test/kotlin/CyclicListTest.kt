package me.twintailedfoxxx.nstu.datastructures

import me.twintailedfoxxx.nstu.types.MyInteger
import me.twintailedfoxxx.nstu.types.PolarVector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.io.StreamCorruptedException
import java.util.*

class CyclicListTest {
    private lateinit var integerList: CyclicList<Any>
    private lateinit var integerExpectedList: CyclicList<Any>
    private lateinit var vectorList: CyclicList<Any>
    private val integerType = MyInteger()
    private val vectorType = PolarVector()

    @BeforeEach
    fun setup() {
        integerList = CyclicList()
        integerExpectedList = CyclicList()
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

    @Test
    fun `same values sorting test`() {
        for(i in 0..<10) {
            integerList.add(integerType.parseValue("0"))
            integerExpectedList.add(integerType.parseValue("0"))
        }

        integerList.sort(integerType.comparator())
        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `backwards list sorting test`() {
        for(i in 10 downTo 0) {
            integerList.add(integerType.parseValue("$i"))
        }

        for(i in 0..10) {
            integerExpectedList.add(integerType.parseValue("$i"))
        }

        integerList.sort(integerType.comparator())
        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `repeating values sorting test`() {
        val numbers = listOf("0", "1", "2", "3", "4", "2")
        for(number in numbers) {
            integerList.add(integerType.parseValue(number))
        }
        integerList.sort(integerType.comparator())

        Collections.sort(numbers)
        for(number in numbers) {
            integerExpectedList.add(integerType.parseValue(number))
        }

        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `group of repeating values sorting test`() {
        val numbers = listOf("3", "1", "2", "1", "3", "2")
        for(number in numbers) {
            integerList.add(integerType.parseValue(number))
        }
        integerList.sort(integerType.comparator())

        Collections.sort(numbers)
        for(number in numbers) {
            integerExpectedList.add(integerType.parseValue(number))
        }

        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `max value at the start sorting test`() {
        val numbers = listOf("9999", "6", "4", "0", "2", "1")
        for(number in numbers) {
            integerList.add(integerType.parseValue(number))
        }
        integerList.sort(integerType.comparator())

        Collections.sort(numbers)
        for(number in numbers) {
            integerExpectedList.add(integerType.parseValue(number))
        }

        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `max value at the middle sorting test`() {
        val numbers = listOf("8", "6", "9999", "2", "4")
        for(number in numbers) {
            integerList.add(integerType.parseValue(number))
        }
        integerList.sort(integerType.comparator())

        Collections.sort(numbers)
        for(number in numbers) {
            integerExpectedList.add(integerType.parseValue(number))
        }

        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `max value at the end sorting test`() {
        val numbers = listOf("8", "6", "4", "0", "2", "9999")
        for(number in numbers) {
            integerList.add(integerType.parseValue(number))
        }
        integerList.sort(integerType.comparator())

        Collections.sort(numbers)
        for(number in numbers) {
            integerExpectedList.add(integerType.parseValue(number))
        }

        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `multiple max values sorting test`() {
        val numbers = listOf("9999", "8", "6", "9999", "0", "2", "9999")
        for(number in numbers) {
            integerList.add(integerType.parseValue(number))
        }
        integerList.sort(integerType.comparator())

        Collections.sort(numbers)
        for(number in numbers) {
            integerExpectedList.add(integerType.parseValue(number))
        }

        assertEquals(integerExpectedList.toString(), integerList.toString())
    }

    @Test
    fun `sort performance test`() {
        for(i in 1..1024 step { it * 2 }) {
            val size = 10000
            val random = Random()

            for (j in 0 until size) {
                integerList.add(integerType.parseValue(random.nextInt(10000).toString()))
            }

            val startTime = System.currentTimeMillis()
            integerList.sort(integerType.comparator())
            val endTime = System.currentTimeMillis()

            println("Sorting $size elements took ${endTime - startTime} ms")
        }
    }

    @Test
    fun `test serialization and deserialization`() {
        integerList.add(integerType.parseValue("1"))
        integerList.add(integerType.parseValue("2"))

        val tempFile = File.createTempFile("test", ".bin")
        tempFile.deleteOnExit()

        integerList.serialize(tempFile.absolutePath)
        val deserializedList = CyclicList.deserialize<Any>(tempFile.absolutePath)

        assertEquals(2, deserializedList.size())
        assertEquals("Целое число: 1", deserializedList.get(0).toString())
        assertEquals("Целое число: 2", deserializedList.get(1).toString())
    }

    @Test
    fun `test deserialization of invalid file`() {
        val tempFile = File.createTempFile("test", ".bin")
        tempFile.deleteOnExit()
        tempFile.writeText("Invalid data")

        assertThrows(StreamCorruptedException::class.java) {
            CyclicList.deserialize<Any>(tempFile.absolutePath)
        }
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

    private infix fun IntRange.step(next: (Int) -> Int) =
        generateSequence(first, next).takeWhile { if (first < last) it <= last else it >= last }
}