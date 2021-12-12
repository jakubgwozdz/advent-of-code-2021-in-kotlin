fun main() {

    val re = Regex("""^(\w+)-(\w+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2) -> v1 to v2 }
            ?: error("`$line` does not match `${re.pattern}`")
    }


    val paths = mutableSetOf<List<String>>()
    var log = false

    fun calculateAll(edges: Map<String, List<String>>, path: List<String>, maxVisits: Int) {
//        println(path.joinToString("-") + " " + maxVisits)
//        edges.forEach { println(it) }
//        println()

        val current = path.last()

        var newMaxVisits = maxVisits
        val newEdges = when {
            current == "start" -> edges.mapValues { (_, l) -> l - current }
            current.first()
                .isLowerCase() && path.count { it == current } >= maxVisits -> edges.mapValues { (_, l) -> l - current }
                .also { newMaxVisits = 1 }
            else -> edges
        }

        if (current == "end") {
            // workaround, todo: fix
            val v = path.filter { it.first().isLowerCase() }.groupBy { it }
                .mapValues { (_, v) -> v.size }
                .count { (k, v) -> v == 2 }
            if (v < 2) {
                if (log) println(path.joinToString("-"))
                paths += path
            }
        } else edges[current]!!.forEach { next ->
            val newPath = path + next
            calculateAll(newEdges, newPath, newMaxVisits)
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
        paths.clear()

        calculateAll(edges, listOf("start"), 1)

        return paths.size
    }

    fun part2(input: List<String>): Int {
        val edges = buildMap<String, List<String>> {
            input.map(parse)
                .forEach {
                    compute(it.first) { _, l -> (l ?: emptyList()) + it.second }
                    compute(it.second) { _, l -> (l ?: emptyList()) + it.first }
                }
                        }.mapValues { (k,v)->v.sorted() }
        paths.clear()
        log = true
        calculateAll(edges, listOf("start"), 2)

        return paths.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    val input = readInput("Day12")
    expect(10) { part1(testInput) }
    println(part1(input))
    expect(36) { part2(testInput) }
    println(part2(input))
}

