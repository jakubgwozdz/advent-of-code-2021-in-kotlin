import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Suppress("EnumEntryName")
private enum class Variable { w, x, y, z }

private fun String.toV() = Variable.valueOf(this)

private fun interface Input {
    fun poll(): Long
}

private data class Input1(val number: List<Int>) : Input {
    var pos: Int = 0
    override fun poll(): Long = number[pos++].toLong()
}

private data class ALU(
    var w: Long = 0L,
    var x: Long = 0L,
    var y: Long = 0L,
    var z: Long = 0L,
) {

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

    override fun toString(): String = "w=$w, x=$x, y=$y, z=$z"

}


private sealed class Op {
    abstract fun invoke(alu: ALU, input: Input)

    class Inp(val v: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = input.poll()
        }
    }

    class Add(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] + o
        }
    }

    class AddR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] + alu[o]
        }
    }

    class Mul(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] * o
        }
    }

    class MulR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] * alu[o]
        }
    }

    class Div(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            if (o == 0L) {
                throw IllegalStateException("div by 0")
            }
            alu[v] = alu[v] / o
        }
    }

    class DivR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            if (alu[o] == 0L) {
                throw IllegalStateException("div by 0")
            }
            alu[v] = alu[v] / alu[o]
        }
    }

    class Mod(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            if (alu[v] < 0L) {
                throw IllegalStateException("mod of < 0")
            }
            if (o <= 0L) {
                throw IllegalStateException("mod by <= 0")
            }
            alu[v] = alu[v] % o
        }
    }

    class ModR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            if (alu[v] < 0L) {
                throw IllegalStateException("mod of < 0")
            }
            if (alu[o] <= 0L) {
                throw IllegalStateException("mod by <= 0")
            }
            alu[v] = alu[v] % alu[o]
        }
    }

    class Eql(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = if (alu[v] == o) 1 else 0
        }
    }

    class EqlR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = if (alu[v] == alu[o]) 1 else 0
        }
    }
}

fun main() {

    fun parse(program: List<String>): List<Op> = program.map { l ->
        val op = l.split(" ")
        val v = op[1].toV()
        when (op[0]) {
            "inp" -> Op.Inp(v)
            "add" -> if (op[2] in "wxyz") Op.AddR(v, op[2].toV()) else Op.Add(v, op[2].toLong())
            "mul" -> if (op[2] in "wxyz") Op.MulR(v, op[2].toV()) else Op.Mul(v, op[2].toLong())
            "div" -> if (op[2] in "wxyz") Op.DivR(v, op[2].toV()) else Op.Div(v, op[2].toLong())
            "mod" -> if (op[2] in "wxyz") Op.ModR(v, op[2].toV()) else Op.Mod(v, op[2].toLong())
            "eql" -> if (op[2] in "wxyz") Op.EqlR(v, op[2].toV()) else Op.Eql(v, op[2].toLong())
            else -> error(l)
        }
    }

    val cache = mutableMapOf<String, Pair<ALU, Input>>()

    fun List<Op>.runOn(alu: ALU, input: Input) {
        forEach { it.invoke(alu, input) }
    }

    fun part1(program: List<String>): Long {

        val ops = parse(program)

        var lastReport = Instant.ofEpochMilli(0)

        fun List<Op>.solve(alu: ALU, soFar: List<Int> = emptyList()): Int? {

            return (9 downTo 1).firstOrNull {
                val now = soFar + it
                val copy = alu.copy()

                this[0].invoke(copy) { it.toLong() }
                val toGo = this.drop(1).takeWhile { it !is Op.Inp }
                val rest = this.subList(1 + toGo.size, this.size)
                toGo.runOn(copy) { error("noop") }

                logWithTime("$now -> $copy")

                if (rest.isNotEmpty()) rest.solve(copy, now) else {
                    val n = Instant.now()
                    if (lastReport.isBefore(n - Duration.of(10, ChronoUnit.SECONDS))) {
                        logWithTime("@$now -> $copy}")
                        lastReport = n
                    }

                }

                (copy.z == 0L).also { result ->
                    if (result) {
                        println(soFar)
                    }
                }

            }
        }
//        ops.solve(ALU())

        val chunked = ops.chunked(18)

//        val cache = Cache<List<Int>, Long>()
//        BFSPathfinder(
//            adderOp = { (l:List<Int>,Long), Int -> }
//        )

//119474
        (0..13).forEach { digit->
            (1..9).forEach { v->
                val alu =ALU()
                (0..13).forEach { d->
                    val n = if( d==digit) v else 1
                    print(n)
                    chunked[d].runOn(alu) {n.toLong()}
                }
                println(" ${alu.z}")
            }

        }

        // x= z%26 != w-dx
        // z /= dz
        // if (x) z *= 25
        // if (x) z += w * dy


        // 0 = z - w*dy

        TODO()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    val input = readInput("Day24")

    expect("0101") {
        val alu = ALU()
        parse(testInput).runOn(alu) { 5 }
        "${alu.w}${alu.x}${alu.y}${alu.z}"
            .also { logWithTime(it) }
    }
    logWithTime(part1(input))
    expect(0) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
