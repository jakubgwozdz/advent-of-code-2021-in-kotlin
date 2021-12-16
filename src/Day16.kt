internal interface PacketProvider {
    fun version(): Int
    fun sum(): Long
    fun product(): Long
    fun min(): Long
    fun max(): Long
    fun gt(): Long
    fun lt(): Long
    fun eq(): Long
}

fun main() {

    abstract class Packet {
        abstract fun versionSum(): Int
        abstract fun calculate(): Long
    }

    data class Literal(val version: Int, val value: Long) : Packet() {
        override fun versionSum() = version
        override fun calculate() = value
    }


    data class SubPackets(val packets: Iterable<Packet>) : PacketProvider {
        override fun version() = packets.sumOf { it.versionSum() }

        override fun sum() = packets.map { it.calculate() }.reduce { acc, x -> acc + x }
        override fun product() = packets.map { it.calculate() }.reduce { acc, x -> acc * x }
        override fun min() = packets.map { it.calculate() }.reduce { a, b -> minOf(a, b) }
        override fun max() = packets.map { it.calculate() }.reduce { a, b -> maxOf(a, b) }
        override fun gt() = packets.map { it.calculate() }.reduce { a, b -> if (a > b) 1 else 0 }
        override fun lt() = packets.map { it.calculate() }.reduce { a, b -> if (a < b) 1 else 0 }
        override fun eq() = packets.map { it.calculate() }.reduce { a, b -> if (a == b) 1 else 0 }
    }

    data class Operator(val version: Int, val type: Int, val packets: PacketProvider) : Packet() {
        override fun versionSum() = version + packets.version()

        override fun calculate() = when (type) {
            0 -> packets.sum()
            1 -> packets.product()
            2 -> packets.min()
            3 -> packets.max()
            5 -> packets.gt()
            6 -> packets.lt()
            7 -> packets.eq()
            else -> TODO()
        }
    }

    data class ParseProcess(val src: String, var pos: Int = 0) {
        fun takeBits(size: Int): String = src.substring(pos, pos + size).also { pos += size }.also { print(">$size ") }

        fun <T> decode(op: (Packet) -> T): T {
            val version = takeBits(3).toInt(2)
            return when (val type = takeBits(3).toInt(2).also{print("_$it ")}) {
                4 -> {
                    val builder = StringBuilder()
                    do {
                        val next = takeBits(5)
                        builder.append(next.drop(1))
                    } while (next[0] == '1')
                    Literal(version, builder.toString().toLong(2)).let(op)
                }
                else -> {
                    Operator(version, type, subPackets()).let(op).also { print("< ") }
                }
            }
        }

        private fun subPackets() = sequence {
            when (takeBits(1)) {
                "0" -> {
                    val end = takeBits(15).toInt(2) + pos
                    while (pos < end) {
                        yield(decode { it })
                    }
                }
                "1" -> {
                    repeat(takeBits(11).toInt(2)) { yield(decode { it }) }
                }
                else -> TODO()
            }
        }.toList().let { SubPackets(it) }
    }

    fun createProcess(hex: String): ParseProcess {
        val src = hex.flatMap { c ->
            c.digitToInt(16).toString(2).padStart(4, '0').map { it }
        }.joinToString("")
        return ParseProcess(src)
    }

    fun part1(input: String) = createProcess(input).decode { it.versionSum() }

    fun part2(input: String) = createProcess(input).decode { it.calculate() }

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
