import java.awt.Color
import java.awt.image.BufferedImage

fun main() {

    val light = Color.WHITE.rgb
    val dark = Color.BLACK.rgb

    operator fun BufferedImage.get(row: Int, column: Int): Int = (row - 1..row + 1).map { y ->
        val outside = getRGB(0, 0)
        if (y !in 0 until height) listOf(outside, outside, outside)
        else (column - 1..column + 1).map { x ->
            if (x !in 0 until width) outside
            else getRGB(x, y)
        }
    }
        .flatten()
        .fold(0) { acc, i -> acc * 2 + if (i == dark) 1 else 0 }

    fun BufferedImage.enhanced(alg: String): BufferedImage {
        val image2 = BufferedImage(
            width,
            height,
            BufferedImage.TYPE_BYTE_BINARY
        )
        (0 until width).forEach { x ->
            (0 until height).forEach { y ->
                val code = this[y, x]
                val color = when (alg[code]) {
                    '.' -> light
                    '#' -> dark
                    else -> error("alg[$code]=${alg[code]}")
                }
                image2.setRGB(x, y, color)
            }
        }
        return image2
    }

    fun BufferedImage.count(): Int = (0 until width).sumOf { x ->
        (0 until height).count { y -> getRGB(x, y) == dark }
    }

    fun renderAsBitmap(data: List<String>): BufferedImage {
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

        return image
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
