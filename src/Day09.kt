fun main() {

    fun lowPoints(heightmap: List<List<Int>>) =
        heightmap
            .foldIndexed(mutableListOf<Pair<Int, Int>>()) { row, acc, line ->
                line.foldIndexed(acc) { col, acc2, h ->
                    val lowest = (col == 0 || h < heightmap[row][col - 1])
                            && (col == line.size - 1 || h < heightmap[row][col + 1])
                            && (row == 0 || h < heightmap[row - 1][col])
                            && (row == heightmap.size - 1 || h < heightmap[row + 1][col])
                    if (lowest) acc2 += row to col
                    acc2
                }
            }

    fun part1(input: List<String>): Int {
        val heightmap = input.map { line -> line.map { c -> c.digitToInt() } }
        return lowPoints(heightmap).fold(0) { acc, (row, col) -> acc + 1 + heightmap[row][col] }
    }

    fun part2(input: List<String>): Int {
        val heightmap = input.map { line -> line.map { c -> c.digitToInt() } }

        val basins = heightmap.map { line -> line.map { c -> if (c == 9) -2 else -1 }.toMutableList() }

        lowPoints(heightmap).forEachIndexed { index, (row, col) -> basins[row][col] = index }

        var changed = true
        while (changed) {
            changed = false
            basins.forEachIndexed { row, line ->
                line.forEachIndexed { col, i ->
                    if (i == -1) {
                        line[col] = when {
                            col > 0 && basins[row][col - 1] >= 0 -> basins[row][col - 1]
                                .also { changed = true }
                            col < line.size - 1 && basins[row][col + 1] >= 0 -> basins[row][col + 1]
                                .also { changed = true }
                            row > 0 && basins[row - 1][col] >= 0 -> basins[row - 1][col]
                                .also { changed = true }
                            row < basins.size - 1 && basins[row + 1][col] >= 0 -> basins[row + 1][col]
                                .also { changed = true }
                            else -> -1
                        }
                    }
                }
            }
        }
        return basins.asSequence()
            .flatMap { it.asSequence() }
            .filter { it >= 0 }
            .groupBy { it }
            .map { (_, v) ->  v.size }
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

