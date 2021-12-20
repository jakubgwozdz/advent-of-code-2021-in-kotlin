import java.awt.Color
import java.awt.image.BufferedImage

internal interface Img {
    operator fun get(row: Int, column: Int): Int
    fun enhanced(alg: String): Img
    fun count(): Int
}


fun main() {

    data class TextImg(private val data: List<String>, private val litOutside: Boolean = false) :
        Img {

        override operator fun get(row: Int, column: Int): Int {
            return (-1..1).joinToString("") { dr ->
                val r = dr + row
                val border = if (litOutside) "###" else "..."
                if (r in data.indices) {
                    val line = "$border${data[r]}$border"
                    val c = column + border.length
                    line.substring(c - 1, c + 2)
                } else border
            }.replace('.', '0').replace('#', '1')
                .toInt(2)
        }

        override fun enhanced(alg: String): Img {
            val i2 = (-1..data.indices.last + 1).map { r ->
                buildString {
                    (-1..data[0].indices.last + 1).forEach { c ->
                        append(alg[this@TextImg[r, c]])
                    }
                }
            }
            return TextImg(i2, if (alg[0] == '.') false else !litOutside)
        }

        override fun count() = data.sumOf { r -> r.count { it == '#' } }
    }

    fun renderAsText(data: List<String>): Img {
        return TextImg(data)
    }

    val light = Color.WHITE.rgb
    val dark = Color.BLACK.rgb

    data class Bitmap(val image: BufferedImage) : Img {
        override fun get(row: Int, column: Int): Int = (row - 1..row + 1).map { y ->
            val outside = image.getRGB(0, 0)
            if (y !in 0 until image.height) listOf(outside, outside, outside)
            else (column - 1..column + 1).map { x ->
                if (x !in 0 until image.width) outside
                else image.getRGB(x, y)
            }
        }
            .flatten()
            .fold(0) { acc, i -> acc * 2 + if (i == dark) 1 else 0 }

        override fun enhanced(alg: String): Img {
            val image2 = BufferedImage(
                image.width,
                image.height,
                BufferedImage.TYPE_BYTE_BINARY
            )
            (0 until image2.width).forEach { x ->
                (0 until image2.height).forEach { y ->
                    val code = this[y, x]
                    val color = when (alg[code]) {
                        '.' -> light
                        '#' -> dark
                        else -> error("alg[$code]=${alg[code]}")
                    }
                    image2.setRGB(x, y, color)
                }
            }
            return Bitmap(image2)
        }

        override fun count(): Int = (0 until image.width).sumOf { x ->
            (0 until image.height).count { y -> image.getRGB(x, y) == dark }
        }
    }

    fun renderAsBitmap(data: List<String>): Img {
        val image = BufferedImage(
            data.first().length + 100,
            data.size + 100,
            BufferedImage.TYPE_BYTE_BINARY
        )
        (0 until image.width).forEach { x ->
            (0 until image.height).forEach { y ->
                image.setRGB(x, y, light)
            }
        }
        data.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                image.setRGB(
                    col + 50, row + 50, when (c) {
                        '.' -> light
                        '#' -> dark
                        else -> error("$c at $row:$col")
                    }
                )
            }
        }

        return Bitmap(image)
    }

    fun part1(input: List<String>): Int {
        val alg = input.first()
        var image = renderAsBitmap(input.drop(2))
        repeat(2) { image = image.enhanced(alg) }
        return image.count()
    }

    fun part2(input: List<String>): Int {
        val alg = input.first()
        var image = renderAsBitmap(input.drop(2))
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
