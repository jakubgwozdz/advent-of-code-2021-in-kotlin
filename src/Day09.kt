fun main() {

    fun <T> List<List<T>>.neighbours(row: Int, col: Int) = sequence {
        if (col > 0) yield(this@neighbours[row][col - 1])
        if (col < this@neighbours[row].size - 1) yield(this@neighbours[row][col + 1])
        if (row > 0) yield(this@neighbours[row - 1][col])
        if (row < this@neighbours.size - 1) yield(this@neighbours[row + 1][col])
    }

    fun lowPoints(heightmap: List<List<Int>>) =
        heightmap
            .foldIndexed(mutableListOf<Pair<Int, Int>>()) { row, acc, line ->
                line.foldIndexed(acc) { col, acc2, h ->
                    if (heightmap.neighbours(row, col).all { h < it }) acc2 += row to col
                    acc2
                }
            }

    fun part1(input: List<String>): Int {
        val heightmap = input.map { line -> line.map { c -> c.digitToInt() } }
        return lowPoints(heightmap).fold(0) { acc, (row, col) -> acc + 1 + heightmap[row][col] }
    }

    fun part2(input: List<String>): Int {
        val heightmap = input.map { line -> line.map { c -> c.digitToInt() } }

        val basins =
            heightmap.map { line -> line.map { c -> if (c == 9) -2 else -1 }.toMutableList() }

        lowPoints(heightmap).forEachIndexed { index, (row, col) -> basins[row][col] = index }

        var changed = true
        while (changed) {
            changed = false
            basins.forEachIndexed { row, line ->
                line.forEachIndexed { col, i ->
                    if (i == -1) {
                        changed = true
                        basins.neighbours(row,col)
                            .firstOrNull { it >= 0 }
                            ?.let { line[col] = it }
                    }
                }
            }
        }
        return basins.asSequence()
            .flatMap { it.asSequence() }
            .filter { it >= 0 }
            .groupBy { it }
            .map { (_, v) -> v.size }
            .sortedDescending()
            .take(3)
            .fold(1) { a, s -> a * s }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val input = readInput("Day09")
    expect(15) { part1(testInput) }
    println(part1(input))
    expect(1134) { part2(testInput) }
    println(part2(input))
}

