fun main() {

    data class Entry(val signal: List<String>, val output: List<String>)

    val parse = { line: String ->
        line.split(" ")
            .let { l ->
                Entry(
                    l.take(10).map { s -> s.toSortedSet().joinToString("") },
                    l.takeLast(4).map { s -> s.toSortedSet().joinToString("") })
            }
    }

    fun part1(input: List<String>): Int = input
        .map(parse)
        .map { it.output.filter { s -> s.length in listOf(2, 3, 4, 7) } }
        .flatten()
        .count()

    val digits = mapOf(
        0 to "abcefg",
        1 to "cf",
        2 to "acdeg",
        3 to "acdfg",
        4 to "bcdf",
        5 to "abdfg",
        6 to "abdefg",
        7 to "acf",
        8 to "abcdefg",
        9 to "abcdfg",
    )

    // https://rosettacode.org/wiki/Permutations#Kotlin (GNU license)
    fun <T> permute(input: List<T>): List<List<T>> {
        if (input.size == 1) return listOf(input)
        val perms = mutableListOf<List<T>>()
        val toInsert = input[0]
        for (perm in permute(input.drop(1))) {
            for (i in 0..perm.size) {
                val newPerm = perm.toMutableList()
                newPerm.add(i, toInsert)
                perms.add(newPerm)
            }
        }
        return perms
    }

    fun codeForSignals(signals: Iterable<String>) =
        signals.sorted().joinToString(" ")

    // pre-calculate all 5K+ possibilities
    val permutations = permute(('a'..'g').toList())
        .map { perm ->
            buildMap<String, Int> {
                digits.forEach { (k, v) ->
                    val sig = v.map { perm[it - 'a'] }.sorted().joinToString("")
                    this[sig] = k
                }
            }
        }
        .associateBy { m -> codeForSignals(m.keys) }


    fun part2(input: List<String>) = input.map(parse)
        .map { (signal, output) ->
            output.fold(0) { acc, s -> acc * 10 + permutations[codeForSignals(signal)]!![s]!! }
        }
        .sumOf { it }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    val input = readInput("Day08")
    expect(26) { part1(testInput) }
    println(part1(input))
    expect(61229) { part2(testInput) }
    println(part2(input))
}

