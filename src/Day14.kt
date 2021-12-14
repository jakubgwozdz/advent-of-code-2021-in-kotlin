fun main() {

    val re = Regex("""^(\w\w) -> (\w)$""")
    val parse = { line: String ->
        re.matchEntire(line)?.destructured?.let { (v1, v2) -> v1 to v2.single() }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun <T> MutableMap<T, Long>.increment(key: T, value: Long = 1L) {
        compute(key) { _, i -> (i ?: 0) + value }
    }

    fun step(
        quantity: MutableMap<Char, Long>, state: Map<String, Long>, rules: Map<String, Char>
    ): Map<String, Long> =
        state.entries.fold(mutableMapOf()) { acc, entry ->
            val (k, n) = entry
            val c = rules[k]!!
            quantity.increment(c, n)
            listOf("${k[0]}$c", "$c${k[1]}")
                .filter { it in rules }
                .forEach { acc.increment(it, n) }
            acc
        }

    fun grow(input: List<String>, times: Int): Long {
        val quantity = mutableMapOf<Char, Long>()
        val rules = input.drop(2).associate(parse)
        input.first().forEach(quantity::increment)

        // initial state
        var next: Map<String, Long> = input.first()
            .windowed(2)
            .filter { it in rules }
            .fold(mutableMapOf()) { acc, s -> acc.apply { increment(s) } }

        repeat(times) { next = step(quantity, next, rules) }
        return quantity.values.sorted().let { it.last() - it.first() }
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

