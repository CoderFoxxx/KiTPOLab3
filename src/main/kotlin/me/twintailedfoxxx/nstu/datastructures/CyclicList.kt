package me.twintailedfoxxx.nstu.datastructures

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

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
        if (size < 2) return

        breakCycle()
        head = mergeSort(head, comparator)
        makeCyclic()
    }

    private fun breakCycle() {
        if (head == null) return

        var current = head
        repeat(size - 1) {
            current = current!!.next
        }
        current!!.next = null
    }

    private fun makeCyclic() {
        if (head == null) return

        var current = head
        while (current!!.next != null) {
            current = current.next
        }
        current.next = head
    }

    private fun mergeSort(start: Node<T>?, comparator: Comparator<T>): Node<T>? {
        if (start?.next == null) return start

        val middle = getMiddle(start)
        val nextToMiddle = middle!!.next

        middle.next = null

        val left = mergeSort(start, comparator)
        val right = mergeSort(nextToMiddle, comparator)

        return merge(left, right, comparator)
    }

    private fun getMiddle(head: Node<T>?): Node<T>? {
        if (head == null) return null

        var slow = head
        var fast = head

        while (fast!!.next != null && fast.next!!.next != null) {
            slow = slow!!.next!!
            fast = fast.next!!.next!!
        }

        return slow
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

    fun sortFunctional(comparator: Comparator<T>) {
        if (size < 2) return

        breakCycle()
        head = head.mergeSortFunctional(comparator)
        makeCyclic()
    }

    private fun Node<T>?.mergeSortFunctional(comparator: Comparator<T>): Node<T>? = when {
        this == null || next == null -> this
        else -> {
            val (left, right) = splitAtMiddle()
            mergeFunctional(
                left.mergeSortFunctional(comparator),
                right.mergeSortFunctional(comparator),
                comparator
            )
        }
    }

    private fun Node<T>?.splitAtMiddle(): Pair<Node<T>?, Node<T>?> {
        if (this == null || next == null) return Pair(this, null)

        var slow: Node<T>? = this
        var fast: Node<T>? = this
        var prev: Node<T>? = null

        while (fast?.next != null) {
            fast = fast.next?.next
            prev = slow
            slow = slow?.next
        }

        prev?.next = null
        return Pair(this, slow)
    }

    private fun mergeFunctional(left: Node<T>?, right: Node<T>?, comparator: Comparator<T>): Node<T>? {
        if (left == null) return right
        if (right == null) return left

        return if (comparator.compare(left.data, right.data) <= 0) {
            left.next = mergeFunctional(left.next, right, comparator)
            left
        } else {
            right.next = mergeFunctional(left, right.next, comparator)
            right
        }
    }
}