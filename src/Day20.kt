fun main() {

    data class Img(val data: List<String>, val outside: Char) {
        operator fun get(row: Int, column: Int): Int {
            return (-1..1).joinToString("") { dr ->
                val r = dr + row
                if (r !in data.indices) "$outside$outside$outside"
                else {
                    val x =
                        "$outside$outside$outside$outside$outside${data[r]}$outside$outside$outside$outside$outside"
                    x.substring(column - 1 + 5, column + 2 + 5)
                }
            }.replace('.', '0').replace('#', '1')
                .toInt(2)
        }

        fun enhanced(alg: String): Img {
            val i2 = (-1..data.indices.last + 1).map { r ->
                buildString {
                    (-1..data[0].indices.last + 1).forEach { c ->
                        append(alg[this@Img[r, c]])
                    }
                }
            }
            return Img(i2, if (alg[0] == '.') '.' else if (outside == '.') '#' else '.')
        }

        fun count() = data.sumOf { r -> r.count { it == '#' } }
    }

    fun part1(input: List<String>): Int {
        val alg = input.first()
        val image = Img(input.drop(2), '.')

        val img1 = image.enhanced(alg)
        val img2 = img1.enhanced(alg)

        return img2.count()
    }

    fun part2(input: List<String>): Int {
        val alg = input.first()
        var image = Img(input.drop(2), '.')

        repeat(50) { image = image.enhanced(alg) }
        return image.count()
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")
    expect(35) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(3351) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
