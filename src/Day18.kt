import java.util.*

internal sealed class Token

internal object Open : Token() {
    override fun toString() = "["
}

internal object Close : Token() {
    override fun toString() = "]"
}

internal class Value(val v: Int) : Token() {
    override fun toString() = "$v"
}

fun main() {

    class MagnitudeCounter(val list: List<Token>) {
        var pos: Int = 0
        fun count(): Int {
            val t = list[pos++]
            if (t is Value) return t.v
            check(t is Open)
            val l = count()
            val r = count()
            check(list[pos++] is Close)
            return 3 * l + 2 * r
        }
    }

    fun magnitude(list: List<Token>) = MagnitudeCounter(list).count()

    fun tryToExplode(list: LinkedList<Token>): Boolean {
        var level = 0
        var pos = 0
        while (pos in list.indices) {
            val c = list[pos]
            if (c == Open) {
                level++
                if (level > 4) {
                    val left = list[pos + 1]
                    val right = list[pos + 2]
                    check(left is Value)
                    check(right is Value)

                    val prevValueIndex = (pos downTo 0).firstOrNull { list[it] is Value }
                    val nextValueIndex = (pos + 3..list.lastIndex).firstOrNull { list[it] is Value }

                    prevValueIndex?.let {
                        val p = list[it]
                        check(p is Value)
                        list[it] = Value(left.v + p.v)
                    }

                    nextValueIndex?.let {
                        val p = list[it]
                        check(p is Value)
                        list[it] = Value(right.v + p.v)
                    }

                    list.removeAt(pos)
                    list.removeAt(pos)
                    list.removeAt(pos)
                    list[pos] = Value(0)

                    return true
                }
            } else if (c == Close) {
                level--
            }
            pos++
        }
        return false
    }

    fun tryToSplit(list: LinkedList<Token>): Boolean {
        var pos = 0
        while (pos in list.indices) {
            val t = list[pos]
            if (t is Value && t.v >= 10) {
                val l = t.v / 2
                val r = t.v - l
                list[pos] = Close
                list.add(pos, Value(r))
                list.add(pos, Value(l))
                list.add(pos, Open)
                return true
            }
            pos++
        }
        return false
    }

    fun reduce(list: LinkedList<Token>) {
        var done = false
        while (!done) {
            done = true
            if (tryToExplode(list)) done = false
            else if (tryToSplit(list)) done = false
        }
    }

    fun tokens(it: String) = LinkedList(it.mapNotNull { c ->
        when {
            c.isDigit() -> Value(c.digitToInt())
            c == '[' -> Open
            c == ']' -> Close
            c == ',' -> null
            else -> error("wtf `$c`")
        }
    }.toList())

    fun add(a: LinkedList<Token>, b: LinkedList<Token>) = LinkedList<Token>().apply {
        add(Open)
        addAll(a)
        addAll(b)
        add(Close)
    }.apply { reduce(this) }

    fun part1(input: List<String>) = input.map { tokens(it) }
        .reduce(::add)
        .let { magnitude(it) }

    fun part2(input: List<String>): Int = input.map { tokens(it) }
        .let { l ->
            l.flatMap { l1 -> l.map { l2 -> add(l1, l2) } }
                .maxOf { magnitude(it) }
        }
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
    expect(4140) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(3993) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
