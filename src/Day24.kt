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


    fun List<Op>.runOn(alu: ALU, input: Input) {
        forEach { it.invoke(alu, input) }
    }

    fun part1(program: List<String>): Long {

        val ops = parse(program)

        val chunked = ops.chunked(18)

        val stack = Stack<Pair<Int,Long>>()
        val result = LongArray(14)

        chunked.map { chunk ->
            val p1 = (chunk[4] as Op.Div).o
            val p2 = (chunk[5] as Op.Add).o
            val p3 = (chunk[15] as Op.Add).o
            Triple(p1,p2,p3)
        }.forEachIndexed { i,(p1,p2,p3) ->
            if (p1==1L) stack.offer(i to p3)
            else {
                val (iPush,p3Push)=stack.poll()

                println("Input[$i] == Input[$iPush] + ${p3Push + p2}")
                (9 downTo 1).first{ it +p3Push + p2 <10 }.let {
                    result[iPush] = it.toLong()
                    result[i] = it + p3Push + p2
                }
            }
        }

        val alu = ALU()
        chunked.forEachIndexed { index, l-> l.runOn(alu) {result[index]} }
        println(alu)

        return result.joinToString("").toLong()

    }

    fun part2(program: List<String>): Long {

        val ops = parse(program)

        val chunked = ops.chunked(18)

        val stack = Stack<Pair<Int,Long>>()
        val result = LongArray(14)

        chunked.map { chunk ->
            val p1 = (chunk[4] as Op.Div).o
            val p2 = (chunk[5] as Op.Add).o
            val p3 = (chunk[15] as Op.Add).o
            Triple(p1,p2,p3)
        }.forEachIndexed { i,(p1,p2,p3) ->
            if (p1==1L) stack.offer(i to p3)
            else {
                val (iPush,p3Push)=stack.poll()

                println("Input[$i] == Input[$iPush] + ${p3Push + p2}")
                (1 .. 9).first{ it +p3Push + p2 >0 }.let {
                    result[iPush] = it.toLong()
                    result[i] = it + p3Push + p2
                }
            }
        }

        val alu = ALU()
        chunked.forEachIndexed { index, l-> l.runOn(alu) {result[index]} }
        println(alu)

        return result.joinToString("").toLong()

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
    logWithTime(part2(input))
}
