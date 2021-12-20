fun main() {

    data class Img(private val data: List<String>, private val litOutside: Boolean = false) {

        operator fun get(row: Int, column: Int): Int {
            return (-1..1).joinToString("") { dr ->
                val r = dr + row
                val border = if (litOutside) "###" else "..."
                if (r in data.indices) {
                    val line = "$border${data[r]}$border"
                    val c = column + border.length
                    line.substring(c - 1, c + 2)
                }
                else border
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
            return Img(i2, if (alg[0] == '.') false else !litOutside)
        }

        fun count() = data.sumOf { r -> r.count { it == '#' } }
    }

    fun part1(input: List<String>): Int {
        val alg = input.first()
        val image = Img(input.drop(2))

        val img1 = image.enhanced(alg)
        val img2 = img1.enhanced(alg)

        return img2.count()
    }

    fun part2(input: List<String>): Int {
        val alg = input.first()
        var image = Img(input.drop(2))

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
