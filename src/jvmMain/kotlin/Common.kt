import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.FrameWindowScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.tinify.Tinify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import utils.ImageCompress
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Window
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter


// 定义一些键值对，用于存储和读取用户的设置
const val webpKey = "webp"
const val progressKey = "progress"
const val forceKey = "force"
const val apiKeyKey = "apiKey"
const val openAfterFinish = "openAfterFinish"
const val maxConcurrent = "maxConcurrent"
const val useInternalEngine = "useInternalEngine"
const val useProxy = "useProxy"
const val proxyUrlKey = "proxyUrl"


const val defaultApi = "JB9dsXdmNNkwPbSVTqDrrGxmZ22h1z2v"

val settings by lazy { Settings() }

val supportedFiles by lazy { listOf("png", "jpg", "jpeg") }

private val desktop by lazy { Desktop.getDesktop() }


fun openFolder(path: String = "") {
    runCatching {
        desktop.open(File(path))
    }
}

// 定义一个 Long 的拓展函数，命名为 toMBString
fun Long.toMBString(): String {
    // 定义一个常量，表示 1 MB 等于多少字节
    val bytesPerMB = 1024 * 1024

    // 定义一个变量，用来存储转换后的 MB 值
    var mbValue = 0.0

    // 使用 try-catch 语句来处理可能的异常
    try {
        // 将 Long 值除以 bytesPerMB 并赋值给 mbValue
        mbValue = this.toDouble() / bytesPerMB
    } catch (e: Exception) {
        // 如果发生异常，打印异常信息并返回空字符串
        e.printStackTrace()
        return ""
    }

    // 使用 format 函数来将 mbValue 转换为保留两位小数的字符串，并返回
    return String.format("%.2f", mbValue) + "MB"
}


inline suspend fun initTinyPng() {
    withContext(Dispatchers.IO) {
        runCatching {
            ImageCompress.setKey()
            Tinify.validate()
        }
    }

}


inline fun doIfNotUseInternal(crossinline action: () -> Unit) {
    if (!settings.get(useInternalEngine, false)) {
        action()
    }
}

inline fun openFilePicker(crossinline onResult: (List<File>) -> Unit) {
//    FileDialog(this.window).apply {
//        isMultipleMode = true
//        isVisible = true
//
//        onResult(this.files.toList())
//    }

    JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        dialogTitle = "选择要压缩的图片"
        approveButtonText = "确定"
        isMultiSelectionEnabled = true
        fileFilter = object : FileFilter() {
            override fun accept(f: File?): Boolean {
                return f?.extension in supportedFiles
            }

            override fun getDescription(): String {
                return "Image files"
            }

        }

        showOpenDialog(null)

        onResult(selectedFiles.toList())
    }
}