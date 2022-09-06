package utils

import java.util.*
import java.util.function.Consumer

class FixedSizeQueue<T>(
    val capacity: Int,
) : Iterable<T> {

    private val data = mutableListOf<T>()

    val size: Int
        get() = data.size

    fun add(element: T) {
        if (size < capacity) {
            data.add(0, element)
        } else {
            data.removeLast()
            data.add(0, element)
        }
    }

    operator fun get(index: Int) = data[index]

    fun removeAt(index: Int) {
        data.removeAt(index)
    }

    fun removeLast() {
        data.removeLast()
    }

    override fun forEach(action: Consumer<in T>?) {
        data.forEach(action)
    }

    override fun iterator() = data.iterator()

    override fun spliterator(): Spliterator<T> = data.spliterator()
}