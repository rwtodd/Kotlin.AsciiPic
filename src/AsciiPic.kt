import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by rtodd on 5/19/2017.
 */


fun scaleImage(orig: BufferedImage, width: Int, height: Int) : BufferedImage {
    val scaled = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    scaled.createGraphics().apply {
        setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        drawImage(orig, 0,0, width, height, null)
        dispose()
    }
    return scaled
}

fun makeThumbnail(fname : String, scaledWidth: Int, aspectRatio: Double) : BufferedImage {
    var image = ImageIO.read(File(fname))
    val scaledHeight = ((scaledWidth / aspectRatio / image.width) * image.height).toInt()

    while(scaledWidth < image.width/2) {
        image = scaleImage(image, image.width/2, image.height/2)
    }

    return scaleImage(image, scaledWidth, scaledHeight)
}

object AsciiPic {
    // determine the brightness (0.0 to 255.0) of a Color
    private fun brightness(c: Color) : Double =
         c.red * 0.2126 + c.green * 0.7152 + c.blue * 0.0722


    // select a character to use based on the given brightness
    private var chars = "#A@%$+=*:,. ".toCharArray()
    private fun selectChar(b: Double)  : Char = chars[ (b * chars.size / 256.0).toInt() ]

    // convert a single line of the input from RGB to ascii
    private fun doLine(im: BufferedImage, y: Int) : String =
            (0 until im.width).map {
                selectChar(brightness(Color(im.getRGB(it, y))))
            }.joinToString("")

    // convert an entire image from RGB to ascii
    fun convertImage(im: BufferedImage) : String =
            (0 until im.height).map { doLine(im,it) }.joinToString("\n")
}

fun main(args : Array<String>) {
    println(AsciiPic.convertImage(makeThumbnail(args[0], 200, 2.0)))
}