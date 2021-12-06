fun main() {

    val cache = mutableMapOf<Int, Long>()

    fun calc(daysLeft: Int): Long = cache[daysLeft] ?: run {
        var cnt = 1L
        var d = daysLeft
        while (d > 0) {
            d -= 7
            cnt += calc(d - 2)
        }
        cnt.also { cache[daysLeft] = it }
    }

    fun calc(days: Int, initial: List<Int>) = initial
        .groupBy { it }.map { (k, v) -> k to v.size }
        .sumOf { (k, v) -> calc(days - k) * v }

    fun parse(input: List<String>) = input.first().split(",").map { it.toInt() }
    fun part1(input: List<String>): Long = calc(80, parse(input))
    fun part2(input: List<String>): Long = calc(256, parse(input))

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    val input = readInput("Day06")
    expect(5934) { part1(testInput) }
    println(part1(input))
    expect(26984457539) { part2(testInput) }
    println(part2(input))
}

