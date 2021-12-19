import kotlin.math.absoluteValue

fun main() {

    val re = Regex("""^(\w+) -(\w+)$""")
    val parse = { line: String ->
        re.matchEntire(line)
            ?.destructured
            ?.let { (v1, v2) -> v1 to v2 }
            ?: error("`$line` does not match `${re.pattern}`")
    }

    data class Pos(val x: Int, val y: Int, val z: Int)

    data class Scanner(val id: Int, val beacons: List<Pos>, val pos:Pos = Pos(0,0,0))


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

    val sincos = listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0)

    val rotations: List<(Pos) -> Pos> = (0..3).flatMap { rx ->
        (0..3).flatMap { ry ->
            (0..3).map { rz -> Triple(rx, ry, rz) }
        }
    }
        .map<Triple<Int, Int, Int>, (Pos) -> Pos> { (rx, ry, rz) ->
            val (sina, cosa) = sincos[rx]
            val (sinb, cosb) = sincos[ry]
            val (sinc, cosc) = sincos[rz]

            val xx = cosa * cosb
            val xy = cosa * sinb * sinc - sina * cosc
            val xz = cosa * sinb * cosc + sina * sinc

            val yx = sina * cosb
            val yy = sina * sinb * sinc + cosa * cosc
            val yz = sina * sinb * cosc - cosa * sinc

            val zx = -sinb
            val zy = cosb * sinc
            val zz = cosb * cosc

            { (x, y, z) ->
                Pos(xx * x + xy * y + xz * z, yx * x + yy * y + yz * z, zx * x + zy * y + zz * z)
            }
        }


//
//    val rotations: List<(Pos)->Pos> = listOf(
//        {(x,y,z)->Pos(x,y,z)},
//        {(x,y,z)->Pos(-x,y,z)},
//        {(x,y,z)->Pos(x,z,y)},
//        {(x,y,z)->Pos(-x,z,y)},
//        {(x,y,z)->Pos(x,-y,z)},
//        {(x,y,z)->Pos(-x,y,z)},
//        {(x,y,z)->Pos(x,z,y)},
//        {(x,y,z)->Pos(-x,z,y)},
//    )

    fun Scanner.changed(op: (Pos) -> Pos) =
        copy(beacons = beacons.map(op), pos = pos.let(op))

    fun Pos.translate(s: Pos, e: Pos) = Pos(x + e.x - s.x, y + e.y - s.y, z + e.z - s.z)

    fun compare(candidate: Scanner, fixed: Scanner): Scanner? {
//        logWithTime("comparing ${candidate.id} to ${fixed.id}")

        return candidate.beacons.asSequence().flatMap { s ->
            fixed.beacons.asSequence().map<Pos, (Pos) -> Pos> { e -> { it.translate(s, e) } }
        }
//            .onEach { logWithTime("checking translation ${it(Pos(0,0,0))}") }
            .map { op -> candidate.changed(op) }
            .firstOrNull { result ->
                val c = fixed.beacons.sumOf { f ->
                    result.beacons.count { r -> r == f }
                }
//                    .also { println(it) }
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
    //                .onEach { logWithTime("checking rotation ${it(Pos(1,2,3))}") }
                .map { op -> scanner.changed(op) }
                .flatMap { new -> fixed.asSequence().map { new to it } }
                .mapNotNull { (n, f) -> compare(n, f) }
                .toList()
    //                .firstOrNull()

            if (found.isNotEmpty()) fixed.add(found[0])
                .also { logWithTime("fixed ${fixed.size} so far") }
            else toGo.offer(scanner)
        }
        return fixed
    }

    fun part1(input: List<String>): Int {

        val fixed = solve(input)

        return fixed.map {it.beacons}.flatten().distinct().size

    }

    fun part2(input: List<String>): Int {
        val fixed = solve(input)
        return fixed.maxOf { a ->
            val (xa,ya,za) = a.pos
            fixed.maxOf { b ->
                val (xb,yb,zb) = b.pos
                (xa-xb).absoluteValue + (ya-yb).absoluteValue + (za-zb).absoluteValue
            }
        }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    val input = readInput("Day19")
    expect(79) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(3621) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
