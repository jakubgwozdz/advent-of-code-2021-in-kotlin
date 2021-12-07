import kotlin.math.absoluteValue

fun main() {

    fun parse(input: List<String>) = input.first().split(",").map { it.toInt() }

    fun part1(input: List<String>): Int {
        val positions = parse(input).sorted()
        val d = (positions.minOrNull()!!..positions.maxOrNull()!!).associateWith { p->
            positions.sumOf { (it-p).absoluteValue }
        }
//        val avg = positions.average().toInt()
//        println(avg)
//        println(d[avg-1])
//        println(d[avg])
//        println(d[avg+1])
        val result = d.minOfOrNull { (k,v)->v }!!

        return result
    }

    fun part2(input: List<String>): Int {
        val positions = parse(input).sorted()
        val d = (positions.minOrNull()!!..positions.maxOrNull()!!).associateWith { p->
            positions.sumOf { (it-p).absoluteValue.let {i->i*(i+1)/2} }
        }
//        val avg = positions.average().toInt()
//        println(avg)
//        println(d[avg-1])
//        println(d[avg])
//        println(d[avg+1])
        val result = d.minOfOrNull { (k,v)->v }!!

        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    val input = readInput("Day07")
    expect(37) { part1(testInput) }
    println(part1(input))
    expect(168) { part2(testInput) }
    println(part2(input))
}

