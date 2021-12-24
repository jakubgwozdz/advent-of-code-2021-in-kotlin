import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("EnumEntryName")
private enum class Variable { w, x, y, z }

fun main() {
    fun part1(program: List<String>): Long {
        class Input(number: Long) {
            private val i: List<Int> = number.toString().map { it.digitToInt() }
            private var pos: Int = 0
            fun poll(): Long = i[pos++].toLong()
        }

        class ALU() {
            var w = 0L
            var x = 0L
            var y = 0L
            var z = 0L
            operator fun set(v: Variable, l: Long) = when (v) {
                Variable.w -> w = l
                Variable.x -> x = l
                Variable.y -> y = l
                Variable.z -> z = l
            }

            operator fun get(v: Variable) = when (v) {
                Variable.w -> w
                Variable.x -> x
                Variable.y -> y
                Variable.z -> z
            }
        }


        fun inp(v: Variable, alu: ALU, input: Input) {
            alu[v] = input.poll()
        }

        fun add(v: Variable, value: Long, alu: ALU) {
            alu[v] = alu[v] + value
        }

        fun add(v: Variable, value: Variable, alu: ALU) = add(v, alu[value], alu)

        fun mul(v: Variable, value: Long, alu: ALU) {
            alu[v] = alu[v] * value
        }

        fun mul(v: Variable, value: Variable, alu: ALU) = mul(v, alu[value], alu)

        fun div(v: Variable, value: Long, alu: ALU) {
            check(value != 0L)
            alu[v] = alu[v] / value
        }

        fun div(v: Variable, value: Variable, alu: ALU) = div(v, alu[value], alu)

        fun mod(v: Variable, value: Long, alu: ALU) {
            check(alu[v] >= 0L)
            check(value > 0L)
            alu[v] = alu[v] % value
        }

        fun mod(v: Variable, value: Variable, alu: ALU) = mod(v, alu[value], alu)

        fun eql(v: Variable, value: Long, alu: ALU) {
            alu[v] = if (alu[v] == value) 1 else 0
        }

        fun eql(v: Variable, value: Variable, alu: ALU) = eql(v, alu[value], alu)

        fun String.toV() = Variable.valueOf(this)
        val ops = program.map { l ->
            val op = l.split(" ")
            val v = op[1].toV()
            when (op[0]) {
                "inp" -> { alu: ALU, input: Input -> inp(v, alu, input) }
                "add" -> if (op[2] in "wxyz") { alu: ALU, _: Input -> add(v, op[2].toV(), alu) }
                else { alu: ALU, _: Input -> add(v, op[2].toLong(), alu) }
                "mul" -> if (op[2] in "wxyz") { alu: ALU, _: Input -> mul(v, op[2].toV(), alu) }
                else { alu: ALU, _: Input -> mul(v, op[2].toLong(), alu) }
                "div" -> if (op[2] in "wxyz") { alu: ALU, _: Input -> div(v, op[2].toV(), alu) }
                else { alu: ALU, _: Input -> div(v, op[2].toLong(), alu) }
                "mod" -> if (op[2] in "wxyz") { alu: ALU, _: Input -> mod(v, op[2].toV(), alu) }
                else { alu: ALU, _: Input -> mod(v, op[2].toLong(), alu) }
                "eql" -> if (op[2] in "wxyz") { alu: ALU, _: Input -> eql(v, op[2].toV(), alu) }
                else { alu: ALU, _: Input -> eql(v, op[2].toLong(), alu) }
                else -> error(l)
            }
        }

        var lastReport = Instant.ofEpochMilli(0)

        return (99999999999999 downTo 11111111111111)
            .asSequence()
            .filter { '0' !in it.toString() }
            .onEach { number ->
                val now = Instant.now()
                if (lastReport.isBefore(now - Duration.of(10, ChronoUnit.SECONDS))) {
                    logWithTime("@ $number")
                    lastReport = now
                }
            }
            .first { number ->
                val input = Input(number)
                try {
                    val alu = ALU()
                    ops.forEach { it(alu, input) }
                    alu.z == 0L
                } catch (e: Exception) {
                    false
                }
            }
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    val input = readInput("Day24")
//    expect(0) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(0) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
