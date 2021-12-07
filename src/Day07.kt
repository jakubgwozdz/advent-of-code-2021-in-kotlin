import kotlin.math.absoluteValue

fun main() {

    fun parse(input: List<String>) = input.first().split(",").map { it.toInt() }

    fun part1(input: List<String>): Int {
        val positions = parse(input).sorted()
        val median = positions[positions.size / 2]
        return positions.sumOf { (it - median).absoluteValue }
    }

    fun part2(input: List<String>): Int {
        val positions = parse(input)
        val costFn = { p: Int ->
            positions.sumOf {
                (it - p).absoluteValue.let { i -> i * (i + 1) / 2 }
            }
        }
        val avg = positions.average().toInt()
        val cost = costFn(avg)

        // see adjacent values, if avg is not integer
        return if (costFn(avg - 1) < cost) costFn(avg - 1)
        else if (costFn(avg + 1) < cost) costFn(avg + 1)
        else cost
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    val input = readInput("Day07")
    expect(37) { part1(testInput) }
    println(part1(input))
    expect(168) { part2(testInput) }
    println(part2(input))
}

