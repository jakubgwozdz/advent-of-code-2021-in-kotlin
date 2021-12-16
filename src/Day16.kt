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

    data class Parser(private val src: String, private var pos: Int = 0) {

        private fun read(size: Int): String = src.substring(pos, pos + size).also { pos += size }
        private fun readInt(size: Int) = read(size).toInt(2)
        private fun readHeader() = readInt(3) to readInt(3)

        private fun readLiteral() = sequence {
            var next = true
            while (next) {
                next = read(1) == "1"
                yield(readInt(4))
            }
        }.fold(0L) { acc, i -> acc * 65536 + i }

        private fun <T> readOperation(op: () -> T) = sequence {
            when (read(1)) {
                "0" -> (pos + readInt(15)).let { while (pos < it) this.yield(op()) }
                "1" -> repeat(readInt(11)) { this.yield(op()) }
                else -> error("wtf")
            }
        }

        fun decodeVersion(): Int {
            val (version, type) = readHeader()
            return when (type) {
                4 -> version.also { readLiteral() }
                else -> version + readOperation(this::decodeVersion).sum()
            }
        }

        fun decodeValue(): Long {
            val (version, type) = readHeader()
            return when (type) {
                4 -> readLiteral()
                else -> readOperation(this::decodeValue).reduce(operation(type))
            }
        }

    }

    fun hex2bin(hex: String) = buildString {
        hex.forEach { c -> append(c.digitToInt(16).toString(2).padStart(4, '0')) }
    }

    fun part1(input: String) = Parser(hex2bin(input)).decodeVersion()

    fun part2(input: String) = Parser(hex2bin(input)).decodeValue()

    // test if implementation meets criteria from the description, like:
    val input = readInput("Day16").first()
    expect(31) { part1("A0016C880162017C3686B18A3D4780").also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(7) { part2("880086C3E88112").also { logWithTime(it) } }
    expect(1) { part2("9C0141080250320F1802104A08").also { logWithTime(it) } }
    logWithTime(part2(input))
}
