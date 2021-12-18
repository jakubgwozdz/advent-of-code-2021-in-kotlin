import kotlin.math.sign
import kotlin.math.sqrt

fun main() {

    data class Pos(val x: Int, val y: Int)
    data class Velocity(val dx: Int, val dy: Int)

    data class Probe(val p: Pos = Pos(0, 0), val v: Velocity)
    data class Target(val xs: IntRange, val ys: IntRange)

    operator fun Target.contains(p: Pos) = p.x in xs && p.y in ys
    operator fun Pos.plus(v: Velocity) = Pos(x + v.dx, y + v.dy)
    operator fun Pos.compareTo(t: Target) = when {
        this in t -> 0 // inside target
        x > t.xs.last || y < t.ys.first -> 1 // overshot to the right or down
        else -> -1
    }

    operator fun Target.contains(probe: Probe) = contains(probe.p)
    operator fun Probe.compareTo(t: Target) = p.compareTo(t)

    fun Velocity.change() = Velocity(dx - dx.sign, dy - 1)
    fun Probe.step() = Probe(p + v, v.change())

    fun Int.sqrt() = sqrt(toDouble()).toInt()


    val re = Regex("""^target area: x=(.+)\.\.(.+), y=(.+)\.\.(.+)$""")
    val parse = { line: String ->
        parse(line, re) { (v1, v2, v3, v4) ->
            Target(v1.toInt()..v2.toInt(), v3.toInt()..v4.toInt())
        }
    }

    fun part1(input: String): Int = parse(input).ys.first.let { y1 -> y1 * (y1 + 1) / 2 }

    fun part2(input: String): Int {
        val target = parse(input)
        val dxLow = target.xs.first.sqrt()
        val dxHigh = target.xs.last + 1
        val dyLow = target.ys.first
        val dyHigh = -dyLow
        return (dyLow..dyHigh).asSequence()
            .flatMap { vy -> (dxLow..dxHigh).asSequence().map { vx -> Velocity(vx, vy) } }
            .map { initial ->
                generateSequence(Probe(v = initial)) { probe -> probe.step() }
                    .takeWhile { probe -> probe <= target }
                    .last()
            }.count { it.p in target }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test").first()
    val input = readInput("Day17").first()
    expect(45) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(112) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
