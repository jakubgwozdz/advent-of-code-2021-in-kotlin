import kotlin.math.absoluteValue

fun main() {

    data class Pos(val x: Int, val y: Int, val z: Int)
    data class Scanner(val id: Int, val beacons: List<Pos>, val pos: Pos = Pos(0, 0, 0))

    fun parse(input: List<String>): Sequence<Scanner> = sequence {
        var i = 0
        while (i in input.indices) {
            while (!input[i].startsWith("---")) i++
            val id = input[i].substringAfter("--- scanner ").substringBefore(" ---").toInt()
            i++
            val beacons = buildList {
                while (i in input.indices && input[i].isNotBlank()) {
                    val (x, y, z) = input[i++].split(",").map { it.toInt() }
                    add(Pos(x, y, z))
                }
            }
            val scanner = Scanner(id, beacons.toMutableList())
            yield(scanner)
            i++
        }
    }

    val rotations: List<(Pos) -> Pos> = listOf(
        { (x, y, z) -> Pos(x = x, y = y, z = z) },
        { (x, y, z) -> Pos(x = x, y = -y, z = -z) },
        { (x, y, z) -> Pos(x = x, y = z, z = -y) },
        { (x, y, z) -> Pos(x = x, y = -z, z = y) },
        { (x, y, z) -> Pos(x = -x, y = y, z = -z) },
        { (x, y, z) -> Pos(x = -x, y = -y, z = z) },
        { (x, y, z) -> Pos(x = -x, y = z, z = y) },
        { (x, y, z) -> Pos(x = -x, y = -z, z = -y) },

        { (x, y, z) -> Pos(x = y, y = x, z = -z) },
        { (x, y, z) -> Pos(x = y, y = -x, z = z) },
        { (x, y, z) -> Pos(x = y, y = z, z = x) },
        { (x, y, z) -> Pos(x = y, y = -z, z = -x) },
        { (x, y, z) -> Pos(x = -y, y = x, z = z) },
        { (x, y, z) -> Pos(x = -y, y = -x, z = -z) },
        { (x, y, z) -> Pos(x = -y, y = z, z = -x) },
        { (x, y, z) -> Pos(x = -y, y = -z, z = x) },

        { (x, y, z) -> Pos(x = z, y = x, z = y) },
        { (x, y, z) -> Pos(x = z, y = -x, z = -y) },
        { (x, y, z) -> Pos(x = z, y = y, z = -x) },
        { (x, y, z) -> Pos(x = z, y = -y, z = x) },
        { (x, y, z) -> Pos(x = -z, y = x, z = -y) },
        { (x, y, z) -> Pos(x = -z, y = -x, z = y) },
        { (x, y, z) -> Pos(x = -z, y = y, z = x) },
        { (x, y, z) -> Pos(x = -z, y = -y, z = -x) },
    )

    fun Pos.translate(s: Pos, e: Pos) = Pos(x + e.x - s.x, y + e.y - s.y, z + e.z - s.z)

    fun Scanner.changed(op: (Pos) -> Pos) =
        copy(beacons = beacons.map(op), pos = pos.let(op))

    fun Scanner.matches(other: Scanner): Boolean {
        var failures = 0
        beacons.forEach { b1 ->
            if (b1 !in other.beacons) {
                failures++
                if (failures > beacons.size - 12) return false
            }
        }
        return true
    }

    fun Scanner.tryMatch(fixed: Scanner): Scanner? =
        cartesian(this.beacons, fixed.beacons)
            .map { (s, e) -> { p: Pos -> p.translate(s, e) } }
            .map { op -> changed(op) }
            .firstOrNull(fixed::matches)

    fun solve(input: List<String>): List<Scanner> {
        val toGo = Queue<Scanner>()
        val fixed = mutableListOf<Scanner>()

        parse(input).forEach { scanner ->
            if (scanner.id == 0) fixed.add(scanner)
            else toGo.offer(scanner)
        }

        while (toGo.isNotEmpty()) {
            val scanner = toGo.poll()
//            logWithTime("processing ${scanner.id}")

            val found = rotations.asSequence()
                .map { op -> scanner.changed(op) }
                .flatMap { new -> fixed.asSequence().map { new to it } }
                .mapNotNull { (n, f) -> n.tryMatch(f) }
                .firstOrNull()

            if (found != null) fixed.add(0, found)
//                .also { logWithTime("fixed ${fixed.size} already") }
            else toGo.offer(scanner)
        }
        return fixed
    }

    fun part1(input: List<String>) = solve(input).map { solved -> solved.beacons }
        .flatten().distinct().size

    fun part2(input: List<String>) = solve(input).let { solved ->
        cartesian(solved, solved)
            .map { (a, b) ->
                val (xa, ya, za) = a.pos
                val (xb, yb, zb) = b.pos
                (xa - xb).absoluteValue + (ya - yb).absoluteValue + (za - zb).absoluteValue
            }.maxOrNull()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")
    expect(79) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(3621) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
