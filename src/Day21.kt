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

        data class Player(val pos: Int, val score: Int)
        data class WinCount(val a: Long, val b: Long)

        fun WinCount.swap() = WinCount(b, a)
        operator fun WinCount.times(l: Long) = WinCount(l * a, l * b)
        operator fun Long.times(w: WinCount) = WinCount(this * w.a, this * w.b)
        operator fun WinCount.plus(o: WinCount) = WinCount(a + o.a, b + o.b)

        val cache = mutableMapOf<Pair<Player, Player>, WinCount>()

        val moves: Map<Int, List<Pair<Int, Long>>> = (1..10).associateWith { s ->
            (1..3).flatMap { r1 ->
                ((1..3) * (1..3)).map { (r2, r3) -> (s + r1 + r2 + r3 - 1) % 10 + 1 }
            }.groupingBy { it }.eachCount()
                .map { (k, v) -> k to v.toLong() }
        }

        fun calculate(playerA: Player, playerB: Player): WinCount {
            val state = playerA to playerB
            val cached = cache[state]
            if (cached != null) return cached
            else {
                val possibilities = moves[playerA.pos]!!
                val calculated = possibilities.map { (ending, count) ->
                    val score = playerA.score + ending
                    val wins = if (score >= 21) WinCount(1, 0)
                    else calculate(playerA = playerB, playerB = Player(ending, score)).swap()
                    count * wins
                }.reduce { acc, w -> acc + w }
                cache[state] = calculated
                return calculated
            }
        }

        val (p1, p2) = input.map { it.split(" ").last().toInt() }
        val (s1, s2) = calculate(Player(p1, 0), Player(p2, 0))
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
