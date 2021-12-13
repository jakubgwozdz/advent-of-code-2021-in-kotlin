import java.io.File
import kotlin.random.Random

fun main() {

    fun Collection<Pair<Int, Int>>.display() {
        println()
        val w = maxOf { (x, y) -> x }
        val h = maxOf { (x, y) -> y }
        (0..h).forEach { y ->
            (0..w).forEach { x ->
                print(if (contains(x to y)) '#' else '.')
            }
            println()
        }
    }

    fun List<String>.parseDots() =
        takeWhile { it.isNotBlank() }.map { it.split(",").let { (x, y) -> x.toInt() to y.toInt() } }
            .toSet()

    fun String.parseInstruction(): Pair<Char, Int> =
        split("=").let { (a, b) -> a.last() to b.toInt() }

    fun performFold(dots: Set<Pair<Int, Int>>, instruction: Pair<Char, Int>): Set<Pair<Int, Int>> {
        val (d, p) = instruction
        return dots.fold(setOf()) { acc, pair ->
            val (x, y) = pair
            acc + if (d == 'x' && x > p) 2 * p - x to y
            else if (d == 'y' && y > p) x to 2 * p - y
            else x to y
        }
    }

    fun part1(input: List<String>): Int {
        val dots = input.parseDots()
        return performFold(dots, input[dots.size + 1].parseInstruction()).size
    }

    fun part2(input: List<String>) {
        val dots = input.parseDots()
        val code = input.drop(dots.size + 1).map(String::parseInstruction).fold(dots, ::performFold)
        code.display()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    val input = readInput("Day13")
    expect(17) { part1(testInput) }
    println(part1(input))
    part2(testInput)
    part2(input)


    fun create(lines: List<String>): List<String> {
        data class State(
            val dots: List<Pair<Int, Int>>, val w: Int, val h: Int, val folds: List<String>
        )
        return (0..11).fold(
            State(
                lines.flatMapIndexed { y, l -> l.flatMapIndexed { x, c -> if (c == '#') listOf(x to y) else emptyList() } },
                lines.maxOf { it.length },
                lines.size,
                emptyList()
            )
        ) { state, _ ->
            val (dots, w, h, folds) = state
            if (Random.nextBoolean()) {
                val p = w + 1
                val newDots = dots.flatMap { (x, y) ->
                    when {
                        Random.nextInt(10) == 1 -> listOf(x to y, 2 * p - x to y)
                        Random.nextBoolean() -> listOf(2 * p - x to y)
                        else -> listOf(x to y)
                    }
                }
                State(newDots, w + p + 2, h, listOf("fold along x=$p") + folds)
            } else {
                val p = h + 1
                val newDots = dots.flatMap { (x, y) ->
                    when {
                        Random.nextInt(10) == 1 -> listOf(x to 2 * p - y, x to y)
                        Random.nextBoolean() -> listOf(x to 2 * p - y)
                        else -> listOf(x to y)
                    }
                }
                State(newDots, w, h + p + 2, listOf("fold along y=$p") + folds)
            }
        }.let { it.dots.map { (x, y) -> "$x,$y" } + " " + it.folds }
    }

    val ktl = """
        #..#..##..###..#....###..#..#..##...##.
        #.#..#..#..#...#.....#...##.#.####.####
        ##...#..#..#...#.....#...#.##.#########
        ##...#..#..#...#.....#...#..#..#######.
        #.#..#..#..#...#.....#...#..#...#####..
        #..#..##...#...####.###..#..#....###...
    """.trimIndent()

    val l = create(ktl.lines())
    File("src", "Day13_jakub.txt").writeText(l.joinToString("\n", postfix = "\n"))

    part2(readInput("Day13_jakub"))
}

