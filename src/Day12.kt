fun main() {

    val re = Regex("""^(\w+)-(\w+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2) -> v1 to v2 }
            ?: error("`$line` does not match `${re.pattern}`")
    }


    val paths = mutableSetOf<List<String>>()

    fun calculateAll(edges: Map<String, List<String>>, soFar: List<String>) {
        val current = soFar.last()
        if (current == "end") {
            paths += soFar + current
        }
        edges[current]!!.forEach { next ->
            calculateAll(if (current.first().isUpperCase()) edges else
                edges.mapValues { (_, l) -> l - current }, soFar + next
            )
        }
    }

    fun part1(input: List<String>): Int {
        val edges = buildMap<String, List<String>> {
            input.map(parse)
                .forEach {
                    compute(it.first) { _, l -> (l ?: emptyList()) + it.second }
                    compute(it.second) { _, l -> (l ?: emptyList()) + it.first }
                }
        }
//        edges.forEach { println(it) }
        paths.clear()

        calculateAll(edges, listOf("start"))

        return paths.size
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val input = readInput("Day12")
    expect(10) { part1(testInput) }
    println(part1(input))
    expect(36) { part2(testInput) }
    println(part2(input))
}

