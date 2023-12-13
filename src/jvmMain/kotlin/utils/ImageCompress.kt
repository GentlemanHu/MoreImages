package utils

import apiKeyKey
import com.russhwolf.settings.get
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.webp.WebpWriter
import com.tinify.Options
import com.tinify.Tinify
import compressCount
import defaultApi
import forceKey
import kotlinx.coroutines.*
import onlyConvertToWebP
import org.jetbrains.skiko.MainUIDispatcher
import progressKey
import proxyUrlKey
import settings
import useInternalEngine
import useProxy
import webpKey
import java.io.File

object ImageCompress {


    fun setKey(key: String = settings.getString(apiKeyKey, defaultApi)) {
        if (settings.get(useProxy, false))
            Tinify.setProxy(settings.getString(proxyUrlKey, "http://127.0.0.1:7890"))
        Tinify.setKey(key)
    }


    fun createCompressJob2(
        scope: CoroutineScope?,
        path: String,
        resultPath: String = path,
        onFinish: () -> Unit = {}
    ): Job? {
        return scope?.async(start = CoroutineStart.LAZY) {
            if (!settings.get(onlyConvertToWebP, false)) {
                LibCompress.compress(path, resultPath)
            } else {
                File(path).copyTo(File(resultPath), true)
            }
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
                    println(file.name + "delete success")
                    return@async
                }


                val outputFile = File(outputWebp)
                println("fileLenght---${file.length()}---webP---${outputFile.length()}")
                if (file.length() <= outputFile.length()) {
                    println(outputFile.name + "webp delete success")
                    outputFile.delete()
                } else {
                    file.delete()
                    println("${file.name}---delete")
                }
            }
        }
    }


    fun createCompressJob(
        scope: CoroutineScope?,
        path: String,
        resultPath: String = path,
        onFinish: () -> Unit = {}
    ): Job? {
        return scope?.async(start = CoroutineStart.LAZY) {
            if (!settings.get(onlyConvertToWebP, false)) {
                println("压缩第 1 轮")
                var result = Tinify.fromFile(path)
                repeat(settings.get(compressCount, 5) - 1) {
                    println("压缩第 ${it + 2} 轮")
                    result = Tinify.fromBuffer(result.toBuffer())
                }

                result.toFile(resultPath)
                println("文件压缩完成---${resultPath}")
            } else {
                File(path).copyTo(File(resultPath), true)
            }

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
                } else {
                    file.delete()
                    println("${file.name}---delete")
                }
            }
        }
    }


    fun compressVersionWrapper(
        scope: CoroutineScope?,
        path: String,
        resultPath: String = path,
        onFinish: () -> Unit = {}
    ): Job? {
        return if (settings.get(useInternalEngine, false)) createCompressJob2(
            scope,
            path,
            resultPath,
            onFinish
        ) else createCompressJob(scope, path, resultPath, onFinish)
    }

}