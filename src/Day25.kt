fun main() {

    fun part1(input: List<String>): Int {
        var steps = 0
        val map = input.map { it.toCharArray() }.toTypedArray()

        var test = true

        do {
            val canMoveEast = map.indices.flatMap { r ->
                map[r].indices.filter { c ->
                    map[r][c] == '>' && map[r][(c + 1) % map[r].size] == '.'
                }.map { c -> r to c }
            }
            canMoveEast.forEach { (r, c) ->
                map[r][c] = '.'
                map[r][(c + 1) % map[r].size] = '>'
            }

            val canMoveSouth = map.indices.flatMap { r ->
                map[r].indices.filter { c ->
                    map[r][c] == 'v' && map[(r + 1) % map.size][c] == '.'
                }.map { c -> r to c }
            }
            canMoveSouth.forEach { (r, c) ->
                map[r][c] = '.'
                map[(r + 1) % map.size][c] = 'v'
            }

            steps++


        //            println("$steps:")
//
//            map.forEach { l ->
//                l.forEach { print(it) }
//                println()
//            }

        } while (canMoveEast.isNotEmpty() || canMoveSouth.isNotEmpty())


        return steps
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    val input = readInput("Day25")
    expect(58) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(0) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
