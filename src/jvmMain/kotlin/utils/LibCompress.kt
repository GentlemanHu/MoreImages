package utils

import com.badlogicgames.libimagequant.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import javax.imageio.ImageIO


object LibCompress {

    init {
        SharedLibraryLoader().load("imagequant-java")
    }

//    fun compress(path: String, resultPath: String) {
//        Thumbnails.of(File(path))
//            .scale(1.0)
//            .outputQuality(1.0)
//            .toFile(File(resultPath))
//
//    }

    fun compress(path: String, resultPath: String) {

        val ori = File(path)
// Read the input image.

// Read the input image.
        val input: BufferedImage = ImageIO.read(ori.inputStream())
        val pixels = (input.raster.dataBuffer as DataBufferByte).getData()

// ABGR -> RGBA.

// ABGR -> RGBA.

        var i = 0
        while (i < pixels.size) {
            val a: Byte = pixels.get(i)
            val b: Byte = pixels.get(i + 1)
            val g: Byte = pixels.get(i + 2)
            val r: Byte = pixels.get(i + 3)
            pixels[i] = r
            pixels[i + 1] = g
            pixels[i + 2] = b
            pixels[i + 3] = a
            i += 4
        }


// Setup libimagequant and quantize the image.

// Setup libimagequant and quantize the image.
        val attribute = LiqAttribute()
        val image = LiqImage(attribute, pixels, input.width, input.height, 0.0)
        val result: LiqResult = image.quantize()

// Based on the quantization result, generate an 8-bit indexed image and retrieve its palette.

// Based on the quantization result, generate an 8-bit indexed image and retrieve its palette.
        val quantizedPixels = ByteArray(input.width * input.height)
        image.remap(result, quantizedPixels)
        val palette: LiqPalette = result.getPalette()

// The resulting 8-bit indexed image and palette could be written out to an indexed PNG or GIF, but instead we convert it
// back to 32-bit RGBA.

// The resulting 8-bit indexed image and palette could be written out to an indexed PNG or GIF, but instead we convert it
// back to 32-bit RGBA.
        val convertedImage = BufferedImage(input.width, input.height, BufferedImage.TYPE_4BYTE_ABGR)
        val convertedPixels = (convertedImage.raster.dataBuffer as DataBufferByte).data
        val size = input.width * input.getHeight()

        var ii = 0
        var j = 0
        while (ii < size) {
            val index = quantizedPixels[ii].toInt() and 0xff // Java's byte is signed
            val color: Int = palette.getColor(index)
            convertedPixels[j] = LiqPalette.getA(color)
            convertedPixels[j + 1] = LiqPalette.getB(color)
            convertedPixels[j + 2] = LiqPalette.getG(color)
            convertedPixels[j + 3] = LiqPalette.getR(color)
            ii++
            j += 4
        }


        ImageIO.write(convertedImage, ori.extension, File(resultPath))

// Good practice to immediately destroy the native resources but not necessary. If the GC cleans up the Java side object the
// native side will be destroyed as well.

// Good practice to immediately destroy the native resources but not necessary. If the GC cleans up the Java side object the
// native side will be destroyed as well.
        result.destroy()
        image.destroy()
        attribute.destroy()
    }
}