import kotlin.math.sign

fun main() {

    data class Pos(val x: Int, val y: Int)
    data class Velocity(val vx: Int, val vy: Int)

    data class Probe(val p: Pos = Pos(0, 0), val v: Velocity)
    data class Target(val xRange: IntRange, val yRange: IntRange)

    operator fun Pos.plus(v: Velocity) = Pos(x + v.vx, y + v.vy)
    fun Velocity.change() = Velocity(vx - vx.sign, vy - 1)
    operator fun Pos.compareTo(t: Target) = when {
        x > t.xRange.last -> 1
        y < t.yRange.first -> 1
        x in t.xRange && y in t.yRange -> 0
        else -> -1
    }

    operator fun Target.contains(p: Pos) = p.x in xRange && p.y in yRange
    operator fun Target.contains(probe: Probe) = contains(probe.p)

    fun Probe.step() = Probe(p + v, v.change())

    fun reasonableVectors(target: Target) = (0..target.xRange.last + 1).asSequence().flatMap { vx ->
        (target.yRange.first..-target.yRange.first * 2).asSequence().map { vy ->
            Velocity(vx, vy)
        }
    }

    val re = Regex("""^target area: x=(.+)\.\.(.+), y=(.+)\.\.(.+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2, v3, v4) -> Target(v1.toInt()..v2.toInt(), v3.toInt()..v4.toInt()) }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun part1(input: String): Int {
        val target = parse(input)

        return reasonableVectors(target)
            .fold(0) { best: Int, initial: Velocity ->
                var h = 0
                val probe = generateSequence(Probe(v = initial)) { probe -> probe.step() }
                    .takeWhile { probe -> probe.p <= target }
                    .onEach { if (h < it.p.y) h = it.p.y }
                    .last()
                if (probe in target && best < h) h else best
            }
    }

    fun part2(input: String): Int {
        val target = parse(input)
        return reasonableVectors(target)
            .map { initial ->
                generateSequence(Probe(v = initial)) { probe -> probe.step() }
                    .takeWhile { probe -> probe.p <= target }
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
