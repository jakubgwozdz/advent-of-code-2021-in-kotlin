import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

fun main() {

    val light = Color.WHITE
    val dark = Color.BLACK

    fun color(c: Char) = when (c) {
        '.' -> light
        '#' -> dark
        else -> error("wtf '$c'")
    }

    operator fun BufferedImage.get(row: Int, column: Int): Int {
        val outside = getRGB(0, 0)
        return (row - 1..row + 1).map { y ->
            if (y !in 0 until height) listOf(outside, outside, outside)
            else (column - 1..column + 1).map { x ->
                if (x !in 0 until width) outside
                else getRGB(x, y)
            }
        }
            .flatten()
            .fold(0) { acc, i -> acc * 2 + if (i == dark.rgb) 1 else 0 }
    }

    fun BufferedImage.enhanced(alg: List<Color>) =
        BufferedImage(width, height, type).also { result ->
            (0 until width).forEach { x ->
                (0 until height).forEach { y -> result.setRGB(x, y, alg[this[y, x]].rgb) }
            }
        }

    fun BufferedImage.count(): Int = (0 until width).sumOf { x ->
        (0 until height).count { y -> getRGB(x, y) == dark.rgb }
    }

    val border = 60

    fun renderAsBitmap(data: List<String>): BufferedImage {
        val image = BufferedImage(
            border + data[0].length + border,
            border + data.size + border,
            BufferedImage.TYPE_BYTE_INDEXED
        )
        (0 until image.width).forEach { x ->
            (0 until image.height).forEach { y -> image.setRGB(x, y, light.rgb) }
        }
        data.forEachIndexed { row, line ->
            line.forEachIndexed { col, c -> image.setRGB(col + border, row + border, color(c).rgb) }
        }

        return image
    }

    fun part1(input: List<String>): Int {
        val alg = input.first().map { color(it) }
        var image = renderAsBitmap(input.drop(2))
        repeat(2) { image = image.enhanced(alg) }
        return image.count()
    }

    fun part2(input: List<String>): Int {
        val alg = input.first().map { color(it) }

        AnimGif(File("src", "Day20.gif")).use { animGif ->

            var image = renderAsBitmap(input.drop(2))
                .also { animGif += it }

            repeat(50) { r ->
                image = image.enhanced(alg)
                    .also { if (((r+1) % 2) == 0) animGif += it }
            }
            return image.count()
        }
    }

// test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    val input = readInput("Day20")
    expect(35) { part1(testInput).also { logWithTime(it) } }
    logWithTime(part1(input))
    expect(3351) { part2(testInput).also { logWithTime(it) } }
    logWithTime(part2(input))
}
