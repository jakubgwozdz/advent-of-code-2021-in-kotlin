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
        { (x, y, z) -> Pos(x = x, y = -z, z = y) },
        { (x, y, z) -> Pos(x = x, y = -y, z = -z) },
        { (x, y, z) -> Pos(x = x, y = z, z = -y) },
        { (x, y, z) -> Pos(x = z, y = y, z = -x) },
        { (x, y, z) -> Pos(x = y, y = -z, z = -x) },
        { (x, y, z) -> Pos(x = -z, y = -y, z = -x) },
        { (x, y, z) -> Pos(x = -y, y = z, z = -x) },
        { (x, y, z) -> Pos(x = -x, y = y, z = -z) },
        { (x, y, z) -> Pos(x = -x, y = -z, z = -y) },
        { (x, y, z) -> Pos(x = -x, y = -y, z = z) },
        { (x, y, z) -> Pos(x = -x, y = z, z = y) },
        { (x, y, z) -> Pos(x = -z, y = y, z = x) },
        { (x, y, z) -> Pos(x = -y, y = -z, z = x) },
        { (x, y, z) -> Pos(x = z, y = -y, z = x) },
        { (x, y, z) -> Pos(x = y, y = z, z = x) },
        { (x, y, z) -> Pos(x = -y, y = x, z = z) },
        { (x, y, z) -> Pos(x = z, y = x, z = y) },
        { (x, y, z) -> Pos(x = y, y = x, z = -z) },
        { (x, y, z) -> Pos(x = -z, y = x, z = -y) },
        { (x, y, z) -> Pos(x = -y, y = -x, z = -z) },
        { (x, y, z) -> Pos(x = z, y = -x, z = -y) },
        { (x, y, z) -> Pos(x = y, y = -x, z = z) },
        { (x, y, z) -> Pos(x = -z, y = -x, z = y) },
    )

    fun Scanner.changed(op: (Pos) -> Pos) =
        copy(beacons = beacons.map(op), pos = pos.let(op))

    fun Pos.translate(s: Pos, e: Pos) = Pos(x + e.x - s.x, y + e.y - s.y, z + e.z - s.z)

    fun compare(candidate: Scanner, fixed: Scanner): Scanner? {

        return candidate.beacons.asSequence().flatMap { s ->
            fixed.beacons.asSequence().map<Pos, (Pos) -> Pos> { e -> { it.translate(s, e) } }
        }
            .map { op -> candidate.changed(op) }
            .firstOrNull { result ->
                val c = fixed.beacons.sumOf { f ->
                    result.beacons.count { r -> r == f }
                }
                c >= 12
            }
    }

    fun solve(input: List<String>): MutableList<Scanner> {
        val toGo = Queue<Scanner>()
        val fixed = mutableListOf<Scanner>()

        parse(input).forEach { scanner ->
            if (scanner.id == 0) fixed.add(scanner)
            else toGo.offer(scanner)
        }

        while (toGo.isNotEmpty()) {
            val scanner = toGo.poll()
            logWithTime("processing ${scanner.id}")

            val found = rotations.asSequence()
                .map { op -> scanner.changed(op) }
                .flatMap { new -> fixed.asSequence().map { new to it } }
                .mapNotNull { (n, f) -> compare(n, f) }
                .toList()
                .firstOrNull()

            if (found!=null) fixed.add(found)
                .also { logWithTime("fixed ${fixed.size} so far") }
            else toGo.offer(scanner)
        }
        return fixed
    }

    fun part1(input: List<String>): Int {

        val fixed = solve(input)

        return fixed.map { it.beacons }.flatten().distinct().size

    }

    fun part2(input: List<String>): Int {
        val fixed = solve(input)
        return fixed.maxOf { a ->
            val (xa, ya, za) = a.pos
            fixed.maxOf { b ->
                val (xb, yb, zb) = b.pos
                (xa - xb).absoluteValue + (ya - yb).absoluteValue + (za - zb).absoluteValue
            }
        }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")
//    expect(79) { part1(testInput).also { logWithTime(it) } }
//    logWithTime(part1(input))
//    expect(3621) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
