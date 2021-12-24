import AmphipodType.*
import RoomType.*
import kotlin.math.absoluteValue

internal enum class RoomType { Hallway, HallwayAbove, RoomA, RoomB, RoomC, RoomD, Wall }
internal enum class AmphipodType(val roomType: RoomType, val col: Int, val cost: Int) {
    A(
        RoomA,
        3,
        1
    ),
    B(RoomB, 5, 10), C(RoomC, 7, 100), D(RoomD, 9, 1000)
}

fun main() {

    data class Pos(val row: Int, val col: Int) {
        override fun toString() = "$row:$col"
    }

    fun Pos.roomType() = when {
        row == 1 && col in listOf(3, 5, 7, 9) -> HallwayAbove
        row == 1 && col in 1..11 -> Hallway
        row in 2..3 && col == 3 -> RoomA
        row in 2..3 && col == 5 -> RoomB
        row in 2..3 && col == 7 -> RoomC
        row in 2..3 && col == 9 -> RoomD
        else -> Wall
    }

    data class State(val positions: Map<Pos, AmphipodType>, val cost: Int) {
        fun print() {
            println("cost $cost")
            repeat(5) { r ->
                repeat(13) { c ->
                    val p = Pos(r, c)
                    print(
                        when {
                            p in positions -> positions[p]
                            p.roomType() == Wall -> "#"
                            else -> "."
                        }
                    )
                }
                println()
            }
        }

        fun canMove(from: Int, to: Int): Boolean {
            val (b, e) = listOf(from, to).sorted()
            return (b..e)
                .filter { it != from }
                .none { Pos(1, it) in positions }
        }

        fun move(from: Pos, to: Pos): State {
            val c = (from.col - to.col).absoluteValue + (from.row - to.row).absoluteValue
            val amph = positions[from]!!
//            println("moving $amph from $from to $to")
            return State(positions - from + (to to amph), cost + c * amph.cost)
        }

    }

    fun part1(input: List<String>): Int {
        val pos: Map<Pos, AmphipodType> = buildMap {
            (2..3).forEach { r ->
                (3..9 step 2).forEach { c ->
                    this[Pos(r, c)] = AmphipodType.valueOf(input[r][c].toString())
                }
            }
        }

        val edges = buildSet {
            (1..10).forEach { c ->
                add((1 to c) to (1 to c + 1))
                add((1 to c + 1) to (1 to c))
            }
            (3..9 step 2).forEach { c ->
                add((1 to c) to (2 to c))
                add((2 to c) to (1 to c))
                add((2 to c) to (3 to c))
                add((3 to c) to (2 to c))
            }
        }

        val hallways = listOf(1, 2, 4, 6, 8, 10, 11)

        fun possibleMoves(s: State): List<State> = buildList {
            s.positions.forEach { (pos, amph) ->
                when {
                    pos.row == 3 && amph.roomType == pos.roomType() -> {} // all good, stop
                    pos.row == 2 && amph.roomType == pos.roomType() && s.positions[pos.copy(row = 3)] == amph -> {} // all good, stop
                    pos.row == 2 -> hallways
                        .filter { col -> s.canMove(pos.col, col) }
                        .forEach { col -> add(s.move(pos, Pos(1, col))) }
                    pos.row == 3 && Pos(2, pos.col) !in s.positions -> hallways
                        .filter { col -> s.canMove(pos.col, col) }
                        .forEach { col -> add(s.move(pos, Pos(1, col))) }
                    pos.row == 1 && s.canMove(pos.col, amph.col) -> {
                        val p2 = Pos(2, amph.col)
                        val p3 = Pos(3, amph.col)
                        if (p3 !in s.positions) {
                            add(s.move(pos, p3))
                        } else if (s.positions[p3] == amph && p2 !in s.positions) {
                            add(s.move(pos, p2))
                        }
                    }

                }
            }
        }


        val end = buildMap {
            this[Pos(2, 3)] = A
            this[Pos(3, 3)] = A
            this[Pos(2, 5)] = B
            this[Pos(3, 5)] = B
            this[Pos(2, 7)] = C
            this[Pos(3, 7)] = C
            this[Pos(2, 9)] = D
            this[Pos(3, 9)] = D
        }


        var solution:State? = null
//        fun calc


        val queue = Queue<State>()
        queue.offer(State(pos, 0))

        while (queue.isNotEmpty()) {
            val state = queue.poll()
//            state.print()
            if (state.positions == end) {
                if (solution == null || solution.cost > state.cost)
                    solution = state
                println(state.cost)
            }
            if (state.cost < (solution?.cost ?: Int.MAX_VALUE))
                possibleMoves(state)
                    .sortedBy { s->s.cost }//- AmphipodType.values().filter { state.positions[Pos(3,)) } }
//                .also { println("adding ${it.size} states") }
                    .forEach { queue.offer(it) }

        }


        return solution!!.cost
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    val input = readInput("Day23")
    expect(12521) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(0) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}


//        repeat(5) { r ->
//            repeat(13) { c ->
//                print(
//                    when (type(r, c)) {
//                        Hallway -> "."
//                        HallwayAbove -> "_"
//                        RoomA -> "A"
//                        RoomB -> "B"
//                        RoomC -> "C"
//                        RoomD -> "D"
//                        Wall -> "#"
//                    }
//                )
//            }
//            println()
//        }
