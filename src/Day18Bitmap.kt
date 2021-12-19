import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal val scaleX = 10
internal val scaleY = 50

fun main(): Unit = File("src", "Day18.txt").useLines { lines ->

    fun BufferedImage.drawLine(raster: Int, prefix: String, line: String) {
        graphics.let { g ->
            g.font = Font("Courier New", Font.PLAIN, 15)
            g.color = Color.WHITE
            g.drawString("$prefix$line", 10, raster * scaleY + 20)
            var depth = 0
            var pos = 5

            fun drawNumber(i: Int, right: Boolean) {
                val width = scaleX * i
                g.color = Color.WHITE
                g.fillRect(pos,raster*scaleY+40, 1, 10)
                g.color = if (right) Color.BLUE else Color.RED
                g.fillRect(pos+1, raster*scaleY+40, width, 10-2*depth)
                pos += width + 1
            }

            fun drawOpen() {
                g.color = Color.CYAN
//                g.fillRect(pos, raster*scaleY+30 + depth, 1, 1)
            }

            fun drawClose() {
                g.color = Color.YELLOW
//                g.fillRect(pos, raster*scaleY+35 + depth, 1, 1)
            }

            line.forEachIndexed { index, c ->
                when {
                    (c == '[') -> drawOpen().also { depth++ }
                    (c == ']') -> drawClose().also { depth-- }
                    (c.isDigit() && line[index + 1].isDigit()) -> drawNumber(
                        c.digitToInt() * 10 + line[index + 1].digitToInt(),
                        line[index - 1] == ','
                    )
                    (c.isDigit() && !line[index + 1].isDigit()) -> drawNumber(
                        c.digitToInt(),
                        line[index - 1] == ','
                    )
                }
            }
        }
    }

    fun BufferedImage.parseLine(raster: Int): String = TODO()

    fun file(index: Int) = File("src", "Day18_sum$index.png")
    fun readLast(index: Int): String {
        val bitmap = ImageIO.read(file(index))
        return bitmap.parseLine(bitmap.height / scaleY)
    }


    lines.forEachIndexed { index, line ->

        // preparation
        val bitmap = BufferedImage(1000, 10000, BufferedImage.TYPE_INT_RGB)
        var raster = 0

        if (index > 0) {
            bitmap.drawLine(raster++, "  ", readLast(index - 1))
            bitmap.drawLine(raster++, "+ ", line)
        } else {
            bitmap.drawLine(raster++, "  ", line)
        }

        check(raster < bitmap.height / scaleY)

        ImageIO.write(bitmap.getSubimage(0, 0, bitmap.width, raster * scaleY), "png", file(index))
    }


}

