fun main() {

    val re = Regex("""^(\w+) -(\w+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2) -> v1 to v2 }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun part1(input: List<String>): Int {
        TODO()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")
    expect(0) { part1(testInput).also { println(it) } }
    println(part1(input))
    expect(0) { part2(testInput).also { println(it) } }
    println(part2(input))
}
