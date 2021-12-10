fun main() {

    fun corruptedScore(line: String): Long {
        val stack = mutableListOf<Char>()
        line.forEach {
            when (it) {
                '(','[','{','<' -> stack.add(it)
                ')' -> if (stack.removeLastOrNull() != '(') return 3
                ']' -> if (stack.removeLastOrNull() != '[') return 57
                '}' -> if (stack.removeLastOrNull() != '{') return 1197
                '>' -> if (stack.removeLastOrNull() != '<') return 25137
            }
        }
        return 0
    }

    fun incompleteScore(line: String): Long {
        val stack = mutableListOf<Char>()
        line.forEach {
            when (it) {
                '(','[','{','<' -> stack.add(it)
                ')',']','}','>' -> stack.removeLast()
            }
        }
        return stack.reversed().fold(0L) {acc, c -> acc * 5 + when(c) {
            '(' -> 1
            '[' -> 2
            '{' -> 3
            '<' -> 4
            else -> 0
        } }
    }


    fun part1(input: List<String>) = input
        .sumOf { line -> corruptedScore(line) }

    fun part2(input: List<String>) = input
        .filter { line -> corruptedScore(line) == 0L }
        .map { line -> incompleteScore(line) }
        .let { scores -> scores.sorted()[scores.size / 2] }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    val input = readInput("Day10")
    expect(26397) { part1(testInput) }
    println(part1(input))
    expect(288957) { part2(testInput) }
    println(part2(input))
}

