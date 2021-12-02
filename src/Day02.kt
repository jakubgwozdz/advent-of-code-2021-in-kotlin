fun main() {
    data class Command(val word: String, val value: Int)

    val re = Regex("""^(\w+) (\d+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (a, b) -> Command(a, b.toInt()) }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun part1(input: List<String>): Int {
        data class Position(val x: Int, val y: Int)
        val operation: (Position, Command) -> Position = { p, (word, value) ->
            when (word) {
                "forward" -> p.copy(x = p.x + value)
                "up" -> p.copy(y = p.y - value)
                "down" -> p.copy(y = p.y + value)
                else -> error("invalid command `$word`")
            }
        }
        return input.asSequence()
            .map(parse)
            .fold(Position(0, 0), operation)
            .let { (x, y) -> x * y }
    }

    fun part2(input: List<String>): Int {
        data class Position(val x: Int, val y: Int, val aim: Int)

        val operation: (Position, Command) -> Position = { p, (word, value) ->
            when (word) {
                "forward" -> p.copy(x = p.x + value, y = p.y + p.aim * value)
                "up" -> p.copy(aim = p.aim - value)
                "down" -> p.copy(aim = p.aim + value)
                else -> error("invalid command `$word`")
            }
        }
        return input.asSequence()
            .map(parse)
            .fold(Position(0, 0, 0), operation)
            .let { (x, y) -> x * y }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    expect(150) { part1(testInput) }
    expect(900) { part2(testInput) }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
