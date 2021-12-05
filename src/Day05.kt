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

    class PointIterator(start: Point, end: Point) : Iterator<Point> {
        val dx = (end.x - start.x).sign
        val dy = (end.y - start.y).sign
        val xs = minOf(start.x, end.x)..maxOf(start.x, end.x)
        val ys = minOf(start.y, end.y)..maxOf(start.y, end.y)

        var x = start.x
        var y = start.y
        override fun hasNext() = x in xs && y in ys
        override fun next() = Point(x, y).also { x += dx;y += dy }

    }

    operator fun Point.rangeTo(other: Point) = Iterable { PointIterator(this, other) }

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

