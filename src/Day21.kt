fun main() {

    fun part1(input: List<String>): Int {

        var (p1, p2) = input.map { it.split(" ").last().toInt() to 0 }

        class Dice(var throws: Int = 0) {
            fun roll() = (throws++ % 10) + 1
        }

        fun playTurn(
            player: Pair<Int, Int>,
            dice: Dice
        ): Pair<Int, Int> {
            var p11 = player
            var (p, s) = p11
            p += dice.roll()
            p += dice.roll()
            p += dice.roll()
            p = (p - 1) % 10 + 1
            s += p
            p11 = p to s
            return p11
        }

        var nextP1 = true
        val dice = Dice()
        while (p1.second < 1000 && p2.second < 1000) {
            if (nextP1) p1 = playTurn(p1, dice) else p2 = playTurn(p2, dice)
            nextP1 = !nextP1
        }

        return p1.second.coerceAtMost(p2.second) * dice.throws
    }

    fun part2(input: List<String>): Long {
        val (p1, p2) = input.map { it.split(" ").last().toInt() }

        val m = (1..10).map { s ->
            s to (1..3).flatMap { r1 ->
                (1..3).flatMap { r2 ->
                    (1..3).map { r3 ->
                        (s + r1 + r2 + r3 - 1) % 10 + 1
                    }
                }
            }.groupBy { it }.mapValues { it.value.size.toLong() }.toList()
        }.toMap()

        fun calculate(
            player1: Pair<Int, Int>,
            player2: Pair<Int, Int>,
            moveP1: Boolean
        ): Pair<Long, Long> {
            val (p, t) = if (moveP1) player1 else player2
            val result = m[p]!!.map { (ending, count) ->
                val tt = t + ending
                if (tt >= 21) if (moveP1) count to 0L else 0L to count
                else {
                    val (np1, np2) = if (moveP1) (ending to tt) to player2 else player1 to (ending to tt)
                    calculate(np1, np2, !moveP1).let { (p1, p2) -> count * p1 to count * p2 }
                }
            }.reduce { (a1, a2), (b1, b2) -> a1 + b1 to a2 + b2 }
            return result
        }

        fun calculate(p1: Int, p2: Int) = calculate(p1 to 0, p2 to 0, true)


        val (s1, s2) = calculate(p1, p2)


        println("$p1:$s1 vs $p2:$s2")
        return s1.coerceAtLeast(s2)

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    val input = readInput("Day21")
    expect(739785) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(444356092776315) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
