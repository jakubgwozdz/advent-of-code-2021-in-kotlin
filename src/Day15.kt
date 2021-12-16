fun main() {


    data class Pos(val x: Int, val y: Int) : Comparable<Pos> {
        override fun compareTo(other: Pos): Int = compareValuesBy(this, other, Pos::x, Pos::y)
    }

    fun List<List<Int>>.exits(p: Pos) = buildList {
        val (x, y) = p
        if (x + 1 in this@exits[y].indices) add(Pos(x + 1, y))
        if (y + 1 in this@exits.indices) add(Pos(x, y + 1))
        if (x - 1 in this@exits[y].indices) add(Pos(x - 1, y))
        if (y - 1 in this@exits.indices) add(Pos(x, y - 1))
    }

    fun List<List<Int>>.calculate(): Int {
        val start = Pos(0, 0)
        val end = Pos(last().lastIndex, lastIndex)

        val cache = Cache<Pos, Int>()

        return BFSPathfinder<Pos, Pair<Pos, Int>, Int>(
            adderOp = { (_, dist), t -> t to dist + this@calculate[t.y][t.x] },
            distanceOp = { (_, dist) -> dist },
            waysOutOp = { (pos, _) -> exits(pos) },
            meaningfulOp = { (pos, _), d -> cache.isBetterThanPrevious(pos, d) }
        ).findShortest(start to 0) { (pos, _) -> pos == end }!!
            .second
    }

    fun part1(input: List<String>) = input.map { l -> l.map { it.digitToInt() } }.calculate()

    fun part2(input: List<String>) =
        (0..4).map { offsetY ->
            input.map { l ->
                (0..4).map { offsetX ->
                    l.map { it.digitToInt() }
                        .map { (((it - 1) + offsetX + offsetY) % 9) + 1 }
                }
                    .reduce { a, b -> a + b }
            }
        }.reduce { a, b -> a + b }
            .calculate()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")
    logWithTime("start")
    expect(40) { part1(testInput).also { logWithTime("test part 1: $it") } }
    logWithTime("real part 1: ${part1(input)}")
    expect(315) { part2(testInput).also { logWithTime("test part 2: $it") } }
    logWithTime("real part 2: ${part2(input)}")
}
