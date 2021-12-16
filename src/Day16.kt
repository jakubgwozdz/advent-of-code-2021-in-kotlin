fun main() {

    fun operation(type: Int): (Long, Long) -> Long = when (type) {
        0 -> { a, b -> a + b }
        1 -> { a, b -> a * b }
        2 -> { a, b -> if (a < b) a else b }
        3 -> { a, b -> if (a > b) a else b }
        5 -> { a, b -> if (a > b) 1 else 0 }
        6 -> { a, b -> if (a < b) 1 else 0 }
        7 -> { a, b -> if (a == b) 1 else 0 }
        else -> TODO()
    }

    data class Parser(val src: String, var pos: Int = 0) {

        fun read(size: Int): String = src.substring(pos, pos + size).also { pos += size }

        private fun parseLiteral(): Long {
            val builder = StringBuilder()
            do {
                val next = read(5)
                builder.append(next.drop(1))
            } while (next[0] == '1')

            return builder.toString().toLong(2)
        }

        private fun <T> parseOperation(op: () -> T) = sequence {
            when (read(1)) {
                "0" -> (readInt(15) + pos).let { while (pos < it) this.yield(op()) }
                "1" -> repeat(readInt(11)) { this.yield(op()) }
                else -> error("wtf")
            }
        }

        fun decodeVersion(): Int {
            val version = readInt(3)
            return when (readInt(3)) {
                4 -> version.also { parseLiteral() }
                else -> version + parseOperation(this::decodeVersion).sum()
            }
        }

        fun decodeValue(): Long {
            readInt(3)
            return when (val type = readInt(3)) {
                4 -> parseLiteral()
                else -> parseOperation(this::decodeValue).reduce(operation(type))
            }
        }

        private fun readInt(size: Int) = read(size).toInt(2)

    }

    fun createProcess(hex: String): Parser {
        val src = hex.flatMap { c ->
            c.digitToInt(16).toString(2).padStart(4, '0').map { it }
        }.joinToString("")
        return Parser(src)
    }

    fun part1(input: String) = createProcess(input).decodeVersion()

    fun part2(input: String) = createProcess(input).decodeValue()

    // test if implementation meets criteria from the description, like:
    val input = readInput("Day16").first()
    expect(31)
    { part1("A0016C880162017C3686B18A3D4780").also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(7)
    { part2("880086C3E88112").also { logWithTime(it) } }
    expect(1)
    { part2("9C0141080250320F1802104A08").also { logWithTime(it) } }
    logWithTime(part2(input))
}
