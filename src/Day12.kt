fun main() {

    fun parse(input: List<String>) = buildMap<String, Iterable<String>> {
        input.map { it.split("-") }
            .forEach { (v1, v2) ->
                compute(v1) { _, l -> (l ?: hashSetOf()) + v2 }
                compute(v2) { _, l -> (l ?: hashSetOf()) + v1 }
            }
    }

    fun calculateAll(
        edges: Map<String, Iterable<String>>,
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

