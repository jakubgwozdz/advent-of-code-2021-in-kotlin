fun main() {
    fun part1(input: List<String>): Int {
        return input.map { it.toInt() }
            .windowed(2)
            .count { (a, b) -> b > a }
    }

    fun part2(input: List<String>): Int {
        return input.asSequence().map { it.toInt() }
            .windowed(3) { it.sum() }
            .windowed(2)
            .count { (a, b) -> b > a }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    expect(7) { part1(testInput) }
    expect(5) { part2(testInput) }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
