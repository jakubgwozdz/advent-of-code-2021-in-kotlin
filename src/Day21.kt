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

        val m: Map<Int, List<Pair<Int, Long>>> = (1..10).associateWith { s ->
            (1..3).flatMap { r1 ->
                ((1..3) * (1..3)).map { (r2, r3) -> (s + r1 + r2 + r3 - 1) % 10 + 1 }
            }.groupingBy { it }.eachCount()
                .map { (k, v) -> k to v.toLong() }
        }

        fun calculate(
            playerA: Pair<Int, Int>,
            playerB: Pair<Int, Int>,
        ): Pair<Long, Long> = m[playerA.first]!!
            .map { (ending, count) ->
                val tt = playerA.second + ending
                if (tt >= 21) count to 0L
                else calculate(playerB, ending to tt).let { (a, b) -> count * b to count * a }
            }
            .reduce { (accA, accB), (a, b) -> accA + a to accB + b }

        val (p1, p2) = input.map { it.split(" ").last().toInt() }
        val (s1, s2) = calculate(p1 to 0, p2 to 0)
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
