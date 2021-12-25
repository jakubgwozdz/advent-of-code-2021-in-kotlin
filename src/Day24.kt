@Suppress("EnumEntryName")
private enum class Variable { w, x, y, z }

private fun String.toV() = Variable.valueOf(this)

private fun interface Input {
    fun poll(): Long
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

        override fun toString() = "$v = input.poll()"
    }

    class Add(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] + o
        }

        override fun toString() = "$v = $v + ${o}L"
    }

    class AddR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] + alu[o]
        }

        override fun toString() = "$v = $v + $o"
    }

    class Mul(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] * o
        }

        override fun toString() = "$v = $v * ${o}L"
    }

    class MulR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] * alu[o]
        }

        override fun toString() = "$v = $v * $o"
    }

    class Div(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] / o
        }

        override fun toString() = "$v = $v / ${o}L"
    }

    class DivR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] / alu[o]
        }

        override fun toString() = "$v = $v / $o"
    }

    class Mod(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] % o
        }

        override fun toString() = "$v = $v % ${o}L"
    }

    class ModR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = alu[v] % alu[o]
        }

        override fun toString() = "$v = $v % $o"
    }

    class Eql(val v: Variable, val o: Long) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = if (alu[v] == o) 1 else 0
        }

        override fun toString() = "$v = if ($v == ${o}L) 1L else 0L"
    }

    class EqlR(val v: Variable, val o: Variable) : Op() {
        override fun invoke(alu: ALU, input: Input) {
            alu[v] = if (alu[v] == alu[o]) 1 else 0
        }

        override fun toString() = "$v = if ($v == $o) 1L else 0L"
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

    fun inKotlin(prev: Long, p1: Boolean, p2: Long, p3: Long, input: Input): Long {
        val w = input.poll()
        var z = prev

        if (p1) z /= 26L // pop
        if (prev % 26L + p2 != w) z = z * 26L + w + p3 // push

        return z
    }

    fun solve(
        program: List<String>,
        progression: IntProgression
    ): Long {
        val ops = parse(program)

        val chunked = ops.chunked(18)

        chunked[0].forEach { println(it) }

        // pushing always input + p3
        // to get 0, can't push after pop -> input == last pushed + p2  -> current input == last input + last p3 + current p2

        val stack = Stack<Pair<Int, Long>>()
        val result = LongArray(14)

        val interesting = chunked.map { chunk ->
            val p1 = (chunk[4] as Op.Div).o
            val p2 = (chunk[5] as Op.Add).o
            val p3 = (chunk[15] as Op.Add).o
            Triple(p1 == 26L, p2, p3)
        }.onEachIndexed { i, t ->
            println("$i: $t")
        }

        interesting.forEachIndexed { i, (p1, p2, p3) ->
            if (!p1) stack.offer(i to p3)
            else {
                val (iPush, lastP3) = stack.poll()
                println("Input[$i] == Input[$iPush] + $lastP3 + $p2")
                progression.first { it + lastP3 + p2 in 1..9 }.let {
                    result[iPush] = it.toLong()
                    result[i] = it + lastP3 + p2
                }
            }
        }

        interesting.foldIndexed(0L) { index, prev, (p1, p2, p3) ->
            inKotlin(prev, p1, p2, p3) { result[index] }.also {
                println("$index: $prev (${if (p1) "pop" else "push"}, $p2, $p3) {${result[index]}} -> $it : " +
                        it.toString(26).map { 'A' + it.digitToInt(26) }.joinToString("")
                )
            }
        }
        return result.joinToString("").toLong()
    }

    fun part1(program: List<String>) = solve(program, 9 downTo 1)

    fun part2(program: List<String>) = solve(program, 1..9)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    val input = readInput("Day24")
    logWithTime(part1(input))
    logWithTime(part2(input))
}
