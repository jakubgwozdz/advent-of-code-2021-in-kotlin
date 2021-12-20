import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.ImageWriter
import javax.imageio.metadata.IIOMetadataNode


class AnimGif(file: File) : AutoCloseable {
    private val iw: ImageWriter = ImageIO.getImageWritersByFormatName("gif").next()
    private val ios = ImageIO.createImageOutputStream(file)

    init {
        iw.output = ios
        iw.prepareWriteSequence(null)
    }

    private var extensionAdded = false

    operator fun plusAssign(image: BufferedImage) {

        val metadata = iw.getDefaultImageMetadata(ImageTypeSpecifier(image), iw.defaultWriteParam)!!
        val metaFormat = metadata.nativeMetadataFormatName!!
        val root = metadata.getAsTree(metaFormat)!!.apply {
            generateSequence(firstChild) { it.nextSibling }
                .first { it.nodeName == "GraphicControlExtension" }
                .let { it as IIOMetadataNode }.apply {
                    setAttribute("userDelay", "FALSE")
                    setAttribute("delayTime", "10")
                    setAttribute("disposalMethod", "restoreToBackgroundColor")
                }

            if (!extensionAdded) {
                appendChild(IIOMetadataNode("ApplicationExtensions").apply {
                    appendChild(IIOMetadataNode("ApplicationExtension").apply {
                        setAttribute("applicationID", "NETSCAPE")
                        setAttribute("authenticationCode", "2.0")
                        userObject = byteArrayOf(1, 0, 0)
                    })
                })
                extensionAdded = true
            }
        }
        metadata.setFromTree(metaFormat, root)
        iw.writeToSequence(IIOImage(image, null, metadata), null)
    }

    override fun close() {
        iw.endWriteSequence()
        ios.close()
    }

}
