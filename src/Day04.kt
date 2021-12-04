fun main() {

    data class Cell(val number: Int, var marked: Boolean = false)
    data class Board(val cells: List<List<Cell>>, var result: Int? = null)
    data class Game(val order: List<Int>, val boards: List<Board>)

    fun parse(input: List<String>): Game {
        val order = input[0].split(",").map { it.toInt() }
        val boards = input.drop(1).chunked(6).map { l ->
            Board(l.drop(1).map { s ->
                s.split(" ")
                    .filter { it.isNotBlank() }
                    .map { Cell(it.toInt()) }
            })
        }
        return Game(order, boards)
    }

    fun Board.mark(number: Int): Int? {
        cells.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell.number == number) {
                    cell.marked = true
                    if (cells.all { it[x].marked } || cells[y].all { it.marked }) {
                        val sum = cells.sumOf { r ->
                            r.filterNot { it.marked }.sumOf { it.number }
                        }
                        result = sum * number
                    }
                }
            }
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val (order, boards) = parse(input)
        order.forEach { number ->
            boards.forEach { board ->
                board.mark(number)?.let { return it }
            }
        }
        error("no solution")
    }

    fun part2(input: List<String>): Int {
        val (order, boards) = parse(input)
        var result: Int? = null
        order.forEach { number ->
            boards.forEach { board ->
                if (board.result == null)
                    board.mark(number)?.let { result = it }
            }
        }
        result?.let { return it }
        error("no solution")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val input = readInput("Day04")

    expect(4512) { part1(testInput) }
    println(part1(input))

    expect(1924) { part2(testInput) }
    println(part2(input))
}
