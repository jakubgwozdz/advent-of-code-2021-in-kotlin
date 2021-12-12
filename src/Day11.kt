fun main() {

    fun adjacent(pos: Pair<Int, Int>): Sequence<Pair<Int, Int>> =
        (-1..1).asSequence()
            .flatMap { dr -> (-1..1).asSequence().map { dc -> dr to dc } }
            .map { (dr, dc) -> dr + pos.first to dc + pos.second }
            .filter { (r, c) -> r in 0..9 && c in 0..9 }
            .filterNot { (r, c) -> r == pos.first && c == pos.second }

    fun step(prev: List<List<Int>>): Pair<Int, List<List<Int>>> {
        var flashes = 0
        val next = prev.map { it.toIntArray() }

        val queue = mutableListOf<Pair<Int, Int>>()
        repeat(10) { row ->
            repeat(10) { col ->
                next[row][col]++
                if (next[row][col] == 10) queue.add(row to col).also { flashes++ }
            }
        }
        while (queue.isNotEmpty()) {
            val n = queue.removeAt(0)
            adjacent(n).forEach { (row, col) ->
                next[row][col]++
                if (next[row][col] == 10) queue.add(row to col).also { flashes++ }
            }
        }

        return flashes to next.map { it.map { d -> if (d > 9) 0 else d } }
    }


    fun part1(input: List<String>): Int {
        var state = input.map { it.map(Char::digitToInt) }
        var flashes = 0

        repeat(100) {
            val (f, new) = step(state)
            state = new
            flashes += f
        }

        return flashes
    }

    fun part2(input: List<String>): Int {
        var state = input.map { it.map(Char::digitToInt) }
        var steps = 0

        do {
            val (f, new) = step(state)
            state = new
            steps++
        } while (f < 100)

        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    val input = readInput("Day11")
    expect(1656) { part1(testInput) }
    println(part1(input))
    expect(195) { part2(testInput) }
    println(part2(input))
}

