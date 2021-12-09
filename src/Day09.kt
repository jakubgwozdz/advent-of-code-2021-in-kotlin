fun main() {


    val re = Regex("""^(\d+),(\d+) -> (\d+),(\d+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (x1, y1, x2, y2) ->
                1
            }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun lowpoints(heightmap: List<List<Int>>) =
        heightmap
            .foldIndexed(mutableListOf<Pair<Int, Int>>()) { row, acc, line ->
                line.foldIndexed(acc) { col, acc2, h ->
                    val l = col == 0 || h < heightmap[row][col - 1]
                    val r = col == line.size - 1 || h < heightmap[row][col + 1]
                    val u = row == 0 || h < heightmap[row - 1][col]
                    val d = row == heightmap.size - 1 || h < heightmap[row + 1][col]
                    if (l && r && u && d) acc2 += row to col
                    acc2
                }
            }

    fun part1(input: List<String>): Int {
        val heightmap = input
            .map { line -> line.map { c -> c.digitToInt() } }
        return lowpoints(heightmap).fold(0) { acc, (row, col) -> acc + 1 + heightmap[row][col] }
    }

    fun log(basins: List<IntArray>) {
        println()
        basins.forEach {
            println(it.joinToString("") { i ->
                when (i) {
                    -1 -> "?"
                    -2 -> "#"
                    else -> i.toString()
                }
            })
        }
    }

    fun part2(input: List<String>): Int {
        val heightmap = input
            .map { line -> line.map { c -> c.digitToInt() } }

        val basins = heightmap
            .map { line -> line.map { c -> if (c == 9) -2 else -1 }.toIntArray() }

        lowpoints(heightmap).forEachIndexed { index, (row, col) -> basins[row][col] = index }

        var changed = true
        while (changed) {
            changed = false
            basins.forEachIndexed { row, line ->
                line.forEachIndexed { col, i ->
                    if (basins[row][col] == -1) {
                        basins[row][col] = when {
                            col > 0 && basins[row][col - 1] >= 0 -> basins[row][col - 1].also { changed = true }
                            col < line.size - 1 && basins[row][col + 1] >= 0 -> basins[row][col + 1].also { changed = true }
                            row > 0 && basins[row - 1][col] >= 0 -> basins[row - 1][col].also { changed = true }
                            row < basins.size - 1 && basins[row + 1][col] >= 0 -> basins[row + 1][col].also { changed = true }
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
            .map { (k, v) -> k to v.size }
            .sortedByDescending { (k, v) -> v }
            .take(3)
            .fold(1) { a, (k, v) -> a * v }

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    val input = readInput("Day09")
    expect(15) { part1(testInput) }
    println(part1(input))
    expect(1134) { part2(testInput) }
    println(part2(input))
}

