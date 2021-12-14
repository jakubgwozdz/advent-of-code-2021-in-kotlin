fun main() {

    val re = Regex("""^(\w\w) -> (\w)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2) -> v1 to v2.single() }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun step(
        quantity: MutableMap<Char, Long>,
        next: Map<String, Long>,
        rules: Map<String, Char>,
        chains: Map<String, List<String>>
    ): Map<String, Long> = buildMap {
        next.forEach { (k, c) ->
            quantity.compute(rules[k]!!) { _, i -> (i ?: 0) + c }
            chains[k]!!.forEach { chained -> compute(chained) { _, i -> (i ?: 0) + c } }
        }
    }

    fun grow(input: List<String>, times: Int): Long {
        val rules = input.drop(2).associate(parse)
        val chains = rules.mapValues { (k, v) ->
            listOf("${k[0]}$v", "$v${k[1]}").filter { it in rules }
        }

        var next = buildMap<String, Long> {
            input.first().windowed(2).filter { it in rules }
                .forEach { s -> compute(s) { _, i -> (i ?: 0) + 1 } }
        }

        return buildMap<Char, Long> {
            input.first().forEach { c -> compute(c) { _, i -> (i ?: 0) + 1 } }
            repeat(times) { next = step(this, next, rules, chains) }
        }.values.sorted()
            .let { it.last() - it.first() }
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

