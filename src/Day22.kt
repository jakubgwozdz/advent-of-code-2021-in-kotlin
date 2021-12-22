import java.util.*

fun main() {

    data class Command(val on: Boolean, val x: IntRange, val y: IntRange, val z: IntRange)

    val re = Regex("""^(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2, v3, v4, v5, v6, v7) ->
                Command(
                    v1 == "on",
                    v2.toInt()..v3.toInt(),
                    v4.toInt()..v5.toInt(),
                    v6.toInt()..v7.toInt()
                )
            }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    fun part1(input: List<String>): Int {
        val commands = input.map(parse)

        val result = mutableSetOf<Triple<Int, Int, Int>>()
        commands
            .filter { (on, x, y, z) ->
                x.first in (-50..50) &&
                        x.last in (-50..50) &&
                        y.first in (-50..50) &&
                        y.last in (-50..50) &&
                        z.first in (-50..50) &&
                        z.last in (-50..50)
            }

            .forEach { (on, x, y, z) ->
                var x1 = x.first.coerceIn(-50..50)
                var x2 = x.last.coerceIn(-50..50)
                var y1 = y.first.coerceIn(-50..50)
                var y2 = y.last.coerceIn(-50..50)
                var z1 = z.first.coerceIn(-50..50)
                var z2 = z.last.coerceIn(-50..50)
                (x1..x2).forEach { xx ->
                    (y1..y2).forEach { yy ->
                        (z1..z2).forEach { zz ->
                            if (on) result += Triple(xx, yy, zz)
                            else result -= Triple(xx, yy, zz)
                        }
                    }
                }
            }

        return result.size
    }

    fun part2(input: List<String>): Long {
        val commands = input.map(parse)
        logWithTime("parsed ${commands.size} commands")

        fun cubes(
            commands: List<Command>,
            selector: (Command) -> IntRange
        ): List<IntRange> {
            val fs = commands.map { selector(it).first } + commands.map { selector(it).last + 1 }
            val ls = commands.map { selector(it).first - 1 } + commands.map { selector(it).last }

            val firsts = fs.sorted().distinct().dropLast(1)
            val lasts = ls.sorted().distinct().drop(1)

            return firsts.zip(lasts).map { (f, l) -> f..l }
        }

        val xs = cubes(commands) { it.x }
        val ys = cubes(commands) { it.y }
        val zs = cubes(commands) { it.z }

        logWithTime("split into ${xs.size}*${ys.size}*${zs.size} cubes")

        val result = BitSet(xs.size * ys.size * zs.size)

        fun IntRange.splitInto(ranges: List<IntRange>): IntRange {
            val s = ranges.indexOfFirst { it.first == this.first }
            val e = ranges.indexOfLast { it.last == this.last }
            return s..e
        }

        commands.forEach { (on, x, y, z) ->
            val xRanges = x.splitInto(xs)
            val yRanges = y.splitInto(ys)
            val zRanges = z.splitInto(zs)
            xRanges.forEach { xx ->
                yRanges.forEach { yy ->
                    zRanges.forEach { zz ->
                        result[xx * ys.size * zs.size + yy * zs.size + zz] = on
                    }
                }
            }
        }

        logWithTime("commands executed")
        logWithTime("${result.cardinality()} cubes turned on")

        return generateSequence(result.nextSetBit(0)) { index ->
            result.nextSetBit(index + 1).let { if (it >= 0) it else null }
        }
            .sumOf { index ->
                val xx = index / zs.size / ys.size
                val yy = index / zs.size % ys.size
                val zz = index % zs.size
                xs[xx].count().toLong() * ys[yy].count().toLong() * zs[zz].count().toLong()
            }
    }

    // test if implementation meets criteria from the description, like:
    val input = readInput("Day22")
    expect(39) { part1(readInput("Day22_test")).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(2758514936282235) { part2(readInput("Day22_test2")).also { logWithTime(it) } }
    logWithTime(part2(input))
}


