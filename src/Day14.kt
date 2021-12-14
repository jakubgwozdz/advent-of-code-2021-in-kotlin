typealias Counters<T> = Map<T, Long>
typealias MutableCounters<T> = MutableMap<T, Long>

fun <T> MutableCounters<T>.increment(key: T, value: Long = 1L) {
    compute(key) { _, i -> (i ?: 0) + value }
}

inline fun <reified T> counters(orig: Counters<T> = emptyMap()): MutableCounters<T> = orig.toMutableMap()

fun main() {

    val re = Regex("""^(\w)(\w) -> (\w)$""")
    val parse = { line: String ->
        re.matchEntire(line)?.destructured?.let { (v1, v2, v3) ->
            v1 + v2 to (v3.single() to listOf(v1 + v3, v3 + v2))
        }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    data class State(val counts: Counters<Char>, val newPairs: Counters<String>)

    fun initial(polymer: String): State {
        val cs = counters<Char>()
        val ps = counters<String>()
        polymer.forEach { cs.increment(it) }
        polymer.windowed(2).forEach { ps.increment(it) }
        return State(cs, ps)
    }

    fun State.step(rules: Map<String, Pair<Char, List<String>>>): State {
        val cs = counters(counts)
        val ps = counters<String>()
        newPairs.forEach { (k, n) ->
            val (c, l) = rules[k]!!
            cs.increment(c, n)
            l.forEach { ps.increment(it, n) }
        }
        return State(cs, ps)
    }

    fun grow(input: List<String>, times: Int): Long {
        val rules = input.drop(2).associate(parse)

        var state = initial(input.first())
        repeat(times) { state = state.step(rules) }

        return state.counts.values.sorted().let { it.last() - it.first() }
    }

    fun part1(input: List<String>) = grow(input, 10)

    fun part2(input: List<String>) = grow(input, 40)

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    val input = readInput("Day14")
    expect(1588) { part1(testInput) }
    println(part1(input))
    expect(2188189693529) { part2(testInput) }
    println(part2(input))
}

