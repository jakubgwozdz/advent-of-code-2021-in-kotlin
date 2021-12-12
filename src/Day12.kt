fun main() {

    fun parse(input: List<String>) = buildMap<String, Collection<String>> {
        input.map { it.split("-") }
            .forEach { (v1, v2) ->
                compute(v1) { _, l -> (l ?: hashSetOf()) + v2 }
                compute(v2) { _, l -> (l ?: hashSetOf()) + v1 }
            }
    }

    fun solve(
        edges: Map<String, Collection<String>>, // directed graph
        canVisitTwice: Boolean = false,
        current: String = "start",
        visited: Collection<String> = hashSetOf(current)
    ): Int = edges[current]!!.sumOf { next -> when {
            next == "end" -> 1
            next.first().isUpperCase() -> solve(edges, canVisitTwice, next, visited + next)
            next !in visited -> solve(edges, canVisitTwice, next, visited + next)
            canVisitTwice && next != "start" -> solve(edges, false, next, visited + next)
            else -> 0
        }
    }

    fun part1(input: List<String>) = solve(parse(input))

    fun part2(input: List<String>) = solve(parse(input), canVisitTwice = true)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val input = readInput("Day12")
    expect(10) { part1(testInput) }
    println(part1(input))
    expect(36) { part2(testInput) }
    println(part2(input))
}
