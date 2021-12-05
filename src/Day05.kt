import kotlin.math.sign

fun main() {

    data class Point(val x: Int, val y: Int)
    data class Line(val p1: Point, val p2: Point)

    val re = Regex("""^(\d+),(\d+) -> (\d+),(\d+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (x1, y1, x2, y2) ->
                Line(
                    Point(x1.toInt(), y1.toInt()),
                    Point(x2.toInt(), y2.toInt())
                )
            }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    operator fun Point.rangeTo(other: Point): Iterable<Point> {
        val dx = (other.x - this.x).sign
        val dy = (other.y - this.y).sign
        val xs = minOf (this.x , other.x) .. maxOf(this.x, other.x)
        val ys = minOf (this.y , other.y) .. maxOf(this.y, other.y)
        val start = this

        return Iterable {
            object : Iterator<Point> {
                var p = start
                override fun hasNext() = p.x in xs && p.y in ys
                override fun next() = p.also { p = Point(p.x + dx, p.y + dy) }
            }
        }
    }

    fun calculate(lines: List<Line>): Int {
        val covered = mutableMapOf<Point, Int>()
        lines.forEach { l ->
            (l.p1..l.p2).forEach { point -> covered.compute(point) { _, n -> (n ?: 0) + 1 } }
        }
        return covered.values.filter { it > 1 }.size
    }

    fun part1(input: List<String>): Int = input.map(parse)
        .filter { it.p1.x == it.p2.x || it.p1.y == it.p2.y }
        .let { calculate(it) }

    fun part2(input: List<String>): Int = input.map(parse)
        .let { calculate(it) }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    expect(5)
    { part1(testInput) }
    expect(12)
    { part2(testInput) }

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

