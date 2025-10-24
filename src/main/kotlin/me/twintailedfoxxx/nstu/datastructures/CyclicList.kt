package me.twintailedfoxxx.nstu.datastructures

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Paths

class CyclicList<T> : Serializable {
    private var head: Node<T>? = null
    private var size: Int = 0

    companion object {
        fun <T> deserialize(fileName: String) : CyclicList<T>? {
            val fis = FileInputStream(fileName)
            val ois = ObjectInputStream(fis)
            val readObj: CyclicList<T>?

            try {
                readObj = ois.readObject() as CyclicList<T>
            } finally {
                ois.close()
                fis.close()
            }

            return readObj
        }

//        fun <T> deserializeFromJson(fileName: String, clazz: Class<T>) : CyclicList<T>? {
//            val gson = Gson()
//            val json = Files.readString(Paths.get(fileName))
//            val type = TypeToken.getParameterized(CyclicList::class.java, clazz).type
//
//            return gson.fromJson(json, type)
//        }
    }

    fun add(data: T) {
        val newNode = Node(data)
        if(head == null) {
            head = newNode
            head!!.next = head
        } else {
            var current = head
            while(current!!.next != head) {
                current = current.next
            }

            newNode.next = head
            current.next = newNode
        }

        size++
    }

    fun add(index: Int, data: T) {
        require(index in 0..size) { "Index out of bounds for insertion: $index, Size: $size" }

        if (index == size) {
            add(data)
            return
        }

        val newNode = Node(data)
        if (index == 0) {
            if (head == null) {
                head = newNode
                head!!.next = head
            } else {
                val lastNode = getNode(size - 1)
                newNode.next = head
                head = newNode
                lastNode.next = head
            }
        } else {
            val prevNode = getNode(index - 1)
            newNode.next = prevNode.next
            prevNode.next = newNode
        }
        size++
    }

    fun forEach(func: (data: T) -> Unit) {
        if(head == null) {
            return
        }

        var current = head
        do {
            func(current!!.data)
            current = current.next
        } while (current != head)
    }

    fun get(idx: Int): T {
        return getNode(idx).data
    }

    fun remove(idx: Int) {
        require(idx in 0..<size) { "Index out of bounds" }

        if(size == 1) {
            head = null
        } else if(idx == 0) {
            val lastNode = getNode(size - 1)
            head = head!!.next
            lastNode.next = head
        } else {
            val prevNode = getNode(idx - 1)
            prevNode.next = prevNode.next!!.next
        }

        size--
    }

    fun toList() : List<T> {
        val list = ArrayList<T>()
        if(head == null) {
            return list
        }

        var current = head
        for(i in 0 until size) {
            list.add(current!!.data)
            current = current.next
        }

        return list
    }

    fun serialize(fileName: String) {
        val fos = FileOutputStream(fileName)
        val oos = ObjectOutputStream(fos)

        try {
            oos.writeObject(this)
        } finally {
            oos.close()
            fos.close()
        }
    }

//    fun serializeToJson(fileName: String, clazz: Class<T>) {
//        val gson = Gson()
//        val json = gson.toJson(this)
//        Files.write(Paths.get(fileName), json.toByteArray())
//    }

    fun size(): Int {
        return size
    }

    override fun toString(): String {
        if(head == null) {
            return "[]"
        }

        val sb: StringBuilder = StringBuilder("[ ")
        var current: Node<T>? = head
        while(current != null) {
            sb.append(current.data.toString())
            if(current.next != null) {
                sb.append(", ")
            }
            current = current.next
        }

        sb.append(" ]")
        return sb.toString()
    }

    private fun getNode(idx: Int): Node<T> {
        require(idx in 0..<size) { "Index out of bounds" }

        var current = head
        for(i in 0 until idx) {
            current = current!!.next
        }

        return current!!
    }

    fun sort(comparator: Comparator<T>) {
        if(size <= 1) {
            return
        }

        val last: Node<T> = getNode(size - 1)
        last.next = null
        head = quickSort(head, comparator)

        val tail: Node<T>? = getTail(head)
        if(tail != null) {
            tail.next = head
        }
    }

    private fun quickSort(head: Node<T>?, comparator: Comparator<T>): Node<T>? {
        if(head?.next == null) {
            return head
        }

        val pivot: Node<T> = head
        val rest: Node<T> = head.next!!

        var lessHead: Node<T>? = null
        var greaterHead: Node<T>? = null
        var current: Node<T>? = rest

        while(current != null) {
            val next: Node<T>? = current.next

            if(comparator.compare(current.data, pivot.data) < 0) {
                current.next = lessHead
                lessHead = current
            } else {
                current.next = greaterHead
                greaterHead = current
            }

            current = next
        }

        val sortedLess: Node<T>? = quickSort(lessHead, comparator)
        val sortedGreater: Node<T>? = quickSort(greaterHead, comparator)
        val tailOfLess: Node<T>? = getTail(sortedLess)

        if(tailOfLess != null) {
            tailOfLess.next = pivot
        }

        pivot.next = sortedGreater

        return when(sortedLess != null) {
            true -> sortedLess
            else -> pivot
        }
    }


    private fun getTail(head: Node<T>?): Node<T>? {
        if(head == null) {
            return null;
        }

        var current: Node<T> = head
        while(current.next != null) {
            current = current.next!!
        }

        return current
    }

    private fun merge(left: Node<T>?, right: Node<T>?, comparator: Comparator<T>): Node<T>? {
        if (left == null) return right
        if (right == null) return left

        val dummy = Node(null as T)
        var current: Node<T>? = dummy
        var leftPtr = left
        var rightPtr = right

        while (leftPtr != null && rightPtr != null) {
            if (comparator.compare(leftPtr.data, rightPtr.data) <= 0) {
                current!!.next = leftPtr
                leftPtr = leftPtr.next
            } else {
                current!!.next = rightPtr
                rightPtr = rightPtr.next
            }
            current = current.next
        }

        current!!.next = leftPtr ?: rightPtr

        return dummy.next
    }
}