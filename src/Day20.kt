fun main() {

    data class Image(val input: List<String>, val outside: Char) {
        operator fun get(row: Int, column: Int): String {
            return (-1..1).joinToString("") { dr ->
                val r = dr + row
                if (r !in input.indices) "$outside$outside$outside"
                else {
                    val x = "$outside$outside$outside$outside$outside${input[r]}$outside$outside$outside$outside$outside"
                    x.substring(column-1+5, column+2+5)
                }
            }
        }
    }


fun String.enhance(image: Image): Image {
    val i2 = (-1..image.input.indices.last+1).map { r ->
        buildString {
            (-1..image.input[0].indices.last+1).forEach { c->
                val p = image[r,c]
                val code = p.replace('.','0').replace('#','1')
                    .toInt(2)
                val v = this@enhance[code]
                append(v)
            }
        }
    }
    return Image(i2, if(this[0]=='.') '.' else if (image.outside == '.') '#' else '.')
}

fun part1(input: List<String>): Int {
    val alg = input.first()
    val image = Image(input.drop(2), '.')

    val img1 = alg.enhance(image)
    val img2 = alg.enhance(img1)

    return img2.input.sumOf { r -> r.count { it == '#' } }
}

fun part2(input: List<String>): Int {
    val alg = input.first()
    var image = Image(input.drop(2), '.')

    repeat(50) { image = alg.enhance(image)}
    return image.input.sumOf { r -> r.count { it == '#' } }
}

// test if implementation meets criteria from the description, like:
val testInput = readInput("Day20_test")
val input = readInput("Day20")
expect (35) { part1(testInput).also { logWithTime(it) } }
logWithTime(part1(input))
expect (3351) { part2(testInput).also { logWithTime(it) } }
logWithTime(part2(input))
}
