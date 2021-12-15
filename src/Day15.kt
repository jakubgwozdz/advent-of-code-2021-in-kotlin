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

        data class State(val pos: Pos, val dist: Int)
        operator fun Collection<State>.contains(pos:Pos) = any{ it.pos == pos }

        val cache = Cache<Pos,Int>()
        fun Collection<State>.distance(): Int = last().dist

        val m = mapOf<String,Int>()
        val j = m + ("s" to 1)

        val pathfinder = BFSPathfinder<Pos, Map<Pos, Int>, Int>(
            adderOp = { l, t -> l + Pair(t, l.values.last() + this@calculate[t.y][t.x]) },
            distanceOp = { it.values.last() },
            waysOutOp = { l -> exits(l.keys.last()).filter { pos -> pos !in l } },
            meaningfulOp = { l, d -> cache.isBetterThanPrevious(l.keys.last(), d) }
        )
        return pathfinder.findShortest(mapOf(start to 0)) { end in it }!!.values.last()
    }

    fun part1(input: List<String>) = input.map { l -> l.map { it.digitToInt() } }.calculate()

    fun part2(input: List<String>) =
        (0..4).map { offsetY ->
            input.map { l ->
                (0..4).map { offsetX ->
                    l.map { it.digitToInt() }.map { (((it - 1) + offsetX + offsetY) % 9) + 1 }
                }
                    .reduce { a, b -> a + b }
            }
        }.reduce { a, b -> a + b }
            .calculate()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    val input = readInput("Day15")
    expect(40) { part1(testInput).also { println(it) } }
    println(part1(input))
    expect(315) { part2(testInput).also { println(it) } }
    println(part2(input))
}
