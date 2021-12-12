fun main() {

    val re = Regex("""^(\w+)-(\w+)$""")
    val parseLine = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2) -> v1 to v2 }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun parse(input: List<String>) = buildMap<String, List<String>> {
        input.map(parseLine)
            .forEach {
                compute(it.first) { _, l -> (l ?: emptyList()) + it.second }
                compute(it.second) { _, l -> (l ?: emptyList()) + it.first }
            }
    }

    fun calculateAll(
        edges: Map<String, List<String>>,
        current: String = "start",
        visited: Collection<String> = hashSetOf(),
        canVisitTwice: Boolean = false
    ): Int {
        val newVisited = visited + current
        return if (current == "end") 1 else edges[current]!!.sumOf { next ->
            when {
                next.first().isUpperCase() -> calculateAll(edges, next, newVisited, canVisitTwice)
                next !in visited -> calculateAll(edges, next, newVisited, canVisitTwice)
                canVisitTwice && next != "start" -> calculateAll(edges, next, newVisited, false)
                else -> 0
            }
        }
    }

    fun part1(input: List<String>) = calculateAll(parse(input))

    fun part2(input: List<String>) = calculateAll(parse(input), canVisitTwice = true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val input = readInput("Day12")
    expect(10) { part1(testInput) }
    println(part1(input))
    expect(36) { part2(testInput) }
    println(part2(input))
}

