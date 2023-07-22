import com.russhwolf.settings.get
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.webp.WebpWriter
import com.tinify.Tinify
import kotlinx.coroutines.*
import org.jetbrains.skiko.MainUIDispatcher
import java.io.File
import javax.imageio.ImageIO

object ImageCompress {


    //"YbnH3LkGbQNsvPhfVx0Gjh9f4x3J9w5n"settings.getString(apiKeyKey,"")
    fun setKey(key: String = settings.getString(apiKeyKey, "")) {
        Tinify.setKey(key)
    }


    inline suspend fun compressImage(
        path: String,
        resultPath: String = path,
        crossinline onResultAction: () -> Unit = {}
    ): Boolean {
        runCatching {
            val result = Tinify.fromFile(path)
            result.toFile(resultPath)

            withContext(MainUIDispatcher) {
                onResultAction()
            }
            return true
        }.onFailure {

        }
        return false
    }


    fun createCompressJob(
        scope: CoroutineScope?,
        path: String,
        resultPath: String = path,
        onFinish: () -> Unit = {}
    ): Job? {
        return scope?.async(start = CoroutineStart.LAZY) {
            val result = Tinify.fromFile(path)
            result.toFile(resultPath)
            println("文件压缩完成---${resultPath}")

            if (settings.get(webpKey, true)) {

                val file = File(resultPath)

                val outputWebp = "${file.parent}/${file.nameWithoutExtension}.webp"

                val quality = settings.get(progressKey, 75f).toInt()
                val writer =
                    if (quality == 100) WebpWriter.MAX_LOSSLESS_COMPRESSION.withLossless() else WebpWriter.DEFAULT.withQ(
                        quality
                    )
                ImmutableImageLoader.create().fromFile(file).output(writer, outputWebp)

                withContext(Dispatchers.Main) {
                    onFinish?.invoke()
                }

                // 非强制覆盖图片，不判断大小，直接用webp,删除原来的file
                if (settings.get(forceKey, false)) {
                    file.delete()
                    return@async
                }


                val outputFile = File(outputWebp)
                if (file.length() <= outputFile.length()) {
                    outputFile.delete()
                }
            }
        }
    }

}