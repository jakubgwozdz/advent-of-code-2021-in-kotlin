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

    fun List<Token>.magnitude() = MagnitudeCounter(this).count()

    fun MutableList<Token>.explode(): Boolean {
        var level = 0
        var pos = 0
        while (pos in indices) {
            val c = this[pos]
            if (c == Open) {
                level++
                if (level > 4) {
                    check(removeAt(pos) == c)
                    val left = removeAt(pos)
                    check(left is Value)
                    val right = removeAt(pos)
                    check(right is Value)
                    check(removeAt(pos) is Close)

                    (pos - 1 downTo 0).firstOrNull { this[it] is Value }?.let {
                        this[it] = Value(left.v + (this[it] as Value).v)
                    }

                    (pos..lastIndex).firstOrNull { this[it] is Value }?.let {
                        this[it] = Value(right.v + (this[it] as Value).v)
                    }

                    add(pos, Value(0))

                    return true
                }
            } else if (c == Close) {
                level--
            }
            pos++
        }
        return false
    }

    fun MutableList<Token>.split(): Boolean {
        var pos = 0
        while (pos in indices) {
            val t = this[pos]
            if (t is Value && t.v >= 10) {
                val l = t.v / 2
                val r = t.v - l
                removeAt(pos)
                addAll(pos, listOf(Open, Value(l), Value(r), Close))
                return true
            }
            pos++
        }
        return false
    }

    fun add(a: List<Token>, b: List<Token>): List<Token> {
        val result = mutableListOf<Token>(Open)
        result += a
        result += b
        result += Close
        do {
            val changed = result.explode() || result.split()
        } while (changed)
        return result
    }

    fun tokens(it: String) = it.mapNotNull { c ->
        when {
            c.isDigit() -> Value(c.digitToInt())
            c == '[' -> Open
            c == ']' -> Close
            c == ',' -> null
            else -> error("wtf `$c`")
        }
    }

    fun part1(input: List<String>) = input.map { tokens(it) }
        .reduce(::add)
        .magnitude()

    fun part2(input: List<String>): Int = input.map { tokens(it) }
        .let { l ->
            l.flatMap { l1 -> l.map { l2 -> add(l1, l2) } }
                .maxOf { it.magnitude() }
        }
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
    expect(4140) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(3993) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
