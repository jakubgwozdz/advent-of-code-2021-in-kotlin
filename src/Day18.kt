internal sealed class Token
internal object Open : Token()
internal object Comma : Token()
internal object Close : Token()
private class Value(val v: Int) : Token()

fun main() {

    class MagnitudeCounter(val list: List<Token>) {
        var pos: Int = 0
        fun count(): Int {
            val t = list[pos++]
            return if (t is Value) t.v
            else {
                check(t is Open)
                val l = count()
                check(list[pos++] is Comma)
                val r = count()
                check(list[pos++] is Close)
                3 * l + 2 * r
            }
        }
    }

    fun List<Token>.magnitude() = MagnitudeCounter(this).count()

    fun MutableList<Token>.explodeAtPos(pos: Int) {
        check(removeAt(pos) == Open)
        val left = removeAt(pos) as Value
        check(removeAt(pos) is Comma)
        val right = removeAt(pos) as Value
        check(removeAt(pos) is Close)

        (pos - 1 downTo 0).firstOrNull { this[it] is Value }?.let {
            this[it] = Value(left.v + (this[it] as Value).v)
        }

        (pos..lastIndex).firstOrNull { this[it] is Value }?.let {
            this[it] = Value(right.v + (this[it] as Value).v)
        }

        add(pos, Value(0))
    }

    fun MutableList<Token>.explode(): Boolean {
        var level = 0
        for (pos in indices) {
            val c = this[pos]
            if (c == Open) {
                level++
                if (level > 4) {
                    explodeAtPos(pos)
                    return true
                }
            } else if (c == Close) {
                level--
            }
        }
        return false
    }

    fun MutableList<Token>.splitAtPos(pos: Int) {
        val t = removeAt(pos) as Value
        val l = t.v / 2
        val r = t.v - l
        addAll(pos, listOf(Open, Value(l), Comma, Value(r), Close))
    }

    fun MutableList<Token>.split(): Boolean {
        for (pos in indices) {
            val t = this[pos]
            if (t is Value && t.v >= 10) {
                splitAtPos(pos)
                return true
            }
        }
        return false
    }

    fun List<Token>.asString() = joinToString("") { t: Token ->
        when (t) {
            Open -> "["
            Close -> "]"
            Comma -> ","
            is Value -> "${t.v}"
        }
    }

    fun addition(a: List<Token>, b: List<Token>) = buildList {
        add(Open)
        addAll(a)
        add(Comma)
        addAll(b)
        add(Close)
        print(asString() + " -> ")
        do {
            val changed = explode() || split()
        } while (changed)
        println(asString())
    }

    fun tokens(it: String) = it.map { c ->
        when {
            c.isDigit() -> Value(c.digitToInt())
            c == '[' -> Open
            c == ']' -> Close
            c == ',' -> Comma
            else -> error("wtf `$c`")
        }
    }

    fun part1(input: List<String>) = input.map { tokens(it) }
        .reduce(::addition)
        .magnitude()

    fun part2(input: List<String>): Int = input.map { tokens(it) }
        .let { l ->
            l.flatMap { l1 -> l.filterNot {it == l}.map { l2 -> addition(l1, l2) } }
                .maxOf { it.magnitude() }
        }
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    val input = readInput("Day18")
//    expect(4140) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
//    expect(3993) { part2(testInput).also { logWithTime(it) } }
//    logWithTime(part2(input))
}
