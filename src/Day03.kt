fun main() {
    fun part1(input: List<String>): Int {
        val size = input.size
        val ones = input.fold(List(input.first().length) { 0 }) { acc: List<Int>, s ->
            acc.zip(s.map { it.digitToInt() }) { a, c -> a + c }
        }

        val gamma = ones.fold(0) { acc, v -> acc * 2 + if (v > size / 2) 1 else 0 }
        val epsilon = ones.fold(0) { acc, v -> acc * 2 + if (v < size / 2) 1 else 0 }

        return gamma * epsilon
    }

    fun part2(input: List<String>): Int {
        val indices = input.first().indices

        fun rating(op: (Int, Int) -> Boolean): Int = indices.fold(input) { current, i ->
            if (current.size > 1) {
                val ones = current.fold(0) { acc, s -> acc + s[i].digitToInt() }
                if (op(current.size - ones, ones))
                    current.filter { l -> l[i] == '0' }
                else
                    current.filter { l -> l[i] == '1' }
            } else current
        }.single().toInt(2)

        val generator = rating { zeroesCount, onesCount -> zeroesCount > onesCount }
        val scrubber = rating { zeroesCount, onesCount -> zeroesCount <= onesCount }

        return generator * scrubber
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    expect(198) { part1(testInput) }
    expect(230) { part2(testInput) }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
