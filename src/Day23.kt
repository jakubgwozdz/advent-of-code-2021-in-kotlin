import kotlin.math.absoluteValue

internal enum class AmphipodType(val col: Int, val cost: Int) {
    A(3, 1),
    B(5, 10),
    C(7, 100),
    D(9, 1000)
}

fun main() {

    data class Pos(val row: Int, val col: Int) {
        override fun toString() = "$row:$col"
    }

    data class State(val positions: Map<Pos, AmphipodType>, val cost: Int, val roomSize: Int) {

        fun canMoveHallWay(from: Int, to: Int): Boolean {
            val (b, e) = listOf(from, to).sorted()
            return (b..e)
                .filter { it != from }
                .none { Pos(1, it) in positions }
        }

        fun canMoveInOwnRoom(amph: AmphipodType) = (2..roomSize + 1).all { row ->
            positions[Pos(row, amph.col)].let { it == null || it == amph }
        }

        fun roomPositions(col: Int) = (roomSize + 1 downTo 2).map { Pos(it, col) }

        fun lowestEmpty(col: Int) = roomPositions(col).first { it !in positions }
        fun highestUsed(col: Int) = roomPositions(col).last { it in positions }

        fun move(from: Pos, to: Pos): State {
            val amph = positions[from]!!
            val dist = dist(from, to)
            val moveCost = dist * amph.cost
            return State(positions - from + (to to amph), cost + moveCost, roomSize)
        }

        private fun dist(from: Pos, to: Pos) =
            (from.col - to.col).absoluteValue + (from.row - to.row).absoluteValue

        fun isSolved() = positions.all { (pos, amph) -> amph.col == pos.col }
        fun minCost() = cost + positions
            .filter { (pos, amph) ->
                pos.col != amph.col || shouldClean(pos.col)
            }
            .entries.sumOf { (pos, amph) ->
                val top = pos.copy(row = 1)
                amph.cost * (2.coerceAtLeast(dist(pos, top) + dist(top, Pos(2, amph.col))))
            }

        fun shouldClean(col: Int) =
            roomPositions(col).any { positions[it].let { a -> a != null && a.col != col } }

    }

    val hallways = listOf(1, 2, 4, 6, 8, 10, 11)

    fun possibleMoves(s: State): List<State> = buildList {
        val goingDown = hallways.map { Pos(1, it) }
            .map { it to s.positions[it] }
            .mapNotNull { (pos, amph) -> amph?.let { pos to it } }
            .firstOrNull { (pos, amph) ->
                s.canMoveHallWay(pos.col, amph.col) && s.canMoveInOwnRoom(amph)
            }
        if (goingDown != null) {
            val (pos, amph) = goingDown
            add(s.move(pos, s.lowestEmpty(amph.col)))
        }
//                .forEach { }
        else {
            (3..9 step 2).filter { col ->
                s.roomPositions(col)
                    .any { s.positions[it].let { a -> a != null && a.col != col } }
            }.map { col ->
                s.highestUsed(col).let { it to s.positions[it]!! }
            }.forEach { (pos, amph) ->
                hallways
                    .filter { col -> s.canMoveHallWay(pos.col, col) }
                    .forEach { col -> add(s.move(pos, Pos(1, col))) }
            }
        }
    }

    fun solve(initial: State): Int {

        var result = Int.MAX_VALUE

        val todo = Stack<State>()
        todo.offer(initial)

        while (todo.isNotEmpty()) {
            val state = todo.poll()
            if (state.isSolved()) {
                if (state.cost < result) {
                    result = state.cost
                        .also { logWithTime("found $it") }
                }
            } else {
                possibleMoves(state)
                    .filter { it.cost < result }
                    .filter { it.minCost() < result }
                    .forEach { todo.offer(it) }
            }
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val pos: Map<Pos, AmphipodType> = buildMap {
            (2..3).forEach { r ->
                (3..9 step 2).forEach { c ->
                    this[Pos(r, c)] = AmphipodType.valueOf(input[r][c].toString())
                }
            }
        }
        return solve(State(pos, 0, 2))
    }

    fun part2(input: List<String>): Int {
        val pos: Map<Pos, AmphipodType> = buildMap {
            (3..9 step 2).forEach { c ->
                this[Pos(2, c)] = AmphipodType.valueOf(input[2][c].toString())
                this[Pos(3, c)] = AmphipodType.valueOf("  #D#C#B#A#"[c].toString())
                this[Pos(4, c)] = AmphipodType.valueOf("  #D#B#A#C#"[c].toString())
                this[Pos(5, c)] = AmphipodType.valueOf(input[3][c].toString())
            }
        }

        return solve(State(pos, 0, 4))
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    val input = readInput("Day23")
    expect(12521) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(44169) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
