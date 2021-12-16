fun main() {

    abstract class Packet {
        abstract fun versionSum():Int
        abstract fun calculate():Long
    }
    data class Literal(val version: Int, val value: Long) : Packet() {
        override fun versionSum(): Int = version
        override fun calculate(): Long = value
    }

    data class Operator(val version: Int, val type: Int, val packets: List<Packet>) : Packet() {
        override fun versionSum() = version + packets.sumOf { it.versionSum() }

        override fun calculate() = when (type) {
            0 -> packets.fold(0L) { acc, p -> acc + p.calculate() }
            1 -> packets.fold(1L) { acc, p -> acc * p.calculate() }
            2 -> packets.minOf { it.calculate() }
            3 -> packets.maxOf { it.calculate() }
            5 -> if (packets[0].calculate() > packets[1].calculate()) 1 else 0
            6 -> if (packets[0].calculate() < packets[1].calculate()) 1 else 0
            7 -> if (packets[0].calculate() == packets[1].calculate()) 1 else 0
            else -> TODO()
        }
    }

    data class ParseProcess(val src: String, var pos: Int = 0) {
        fun takeBits(size: Int): String = src.substring(pos, pos + size).also { pos += size }

        fun <T>decodeLiteral(version: Int, op: (Packet) -> T): T {
            val builder = StringBuilder()
            do {
                val next = takeBits(5)
                builder.append(next.drop(1))
            } while (next[0] == '1')
            return Literal(version, builder.toString().toLong(2)).let(op)
        }

        fun <T>decodeOperator(version: Int, type: Int, op: (Packet) -> T): T {
            val packets: List<Packet> = when (takeBits(1)) {
                "0" -> buildList {
                    val end = takeBits(15).toInt(2) + pos
                    while (pos < end) {
                        add(decode { it })
                    }
                }
                "1" -> buildList {
                    repeat(takeBits(11).toInt(2)) { add(decode { it }) }
                }
                else -> TODO()
            }
            return Operator(version, type, packets).let(op)
        }

        fun <T>decode(op:(Packet)->T ): T {
            val version = takeBits(3).toInt(2)
            return when (val type = takeBits(3).toInt(2)) {
                4 -> decodeLiteral(version, op)
                else -> decodeOperator(version, type, op)
            }
        }
    }

    fun createProcess(hex: String): ParseProcess {
        val src = hex.flatMap { c ->
            c.digitToInt(16).toString(2).padStart(4, '0').map { it }
        }.joinToString("")
        return ParseProcess(src)
    }

    fun part1(input: String)= createProcess(input).decode{it.versionSum()}

    fun part2(input: String) = createProcess(input).decode{it.calculate()}

// test if implementation meets criteria from the description, like:
    val input = readInput("Day16").first()
    expect(31) { part1("A0016C880162017C3686B18A3D4780").also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(7) { part2("880086C3E88112").also { logWithTime(it) } }
    expect(1) { part2("9C0141080250320F1802104A08").also { logWithTime(it) } }
    logWithTime(part2(input))
}
