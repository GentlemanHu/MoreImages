import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.get
import com.tinify.*
import kotlinx.coroutines.*
import java.awt.FileDialog
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

private interface HandleError {
    fun handleAccountError()

    fun handleClientError()

    fun handleServerError()

    fun handleConnectionError()

    fun handleOtherError(msg: String)

    fun defaultOperation()


}

class ResetException(msg: String = "") : RuntimeException(msg)

private var eHandlder: HandleError? = null

private val errorHandler = CoroutineExceptionHandler { _, throwable ->
    println("error---${throwable}")
    when (throwable) {
        is AccountException -> {
// API 有问题
            eHandlder?.handleAccountError()

        }

        is ClientException -> {
//图片有问题
            eHandlder?.handleClientError()

        }

        is ServerException -> {
//服务器有问题，稍后
            eHandlder?.handleServerError()

        }

        is ConnectionException -> {
// 网络有问题'
            eHandlder?.handleConnectionError()

        }

        is ResetException -> {
            eHandlder?.defaultOperation()
        }

        else -> {
// 其他问题
            eHandlder?.handleOtherError(throwable.message ?: "")
        }
    }
    eHandlder?.defaultOperation()
}

var mScope: CoroutineScope? = null


data class HintDialogBean(
    val content: String,
    val action: () -> Unit,
    val cancelAction: () -> Unit
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App() {
    var usedCount by remember { mutableStateOf(Tinify.compressionCount()) }
    var buttonEnable by remember { mutableStateOf(false) }

    val dialogs = remember { mutableMapOf<String, Boolean>() }

    val hintDialogs = remember { mutableMapOf<HintDialogBean, Boolean>() }

    var canShowSettingDialog by remember { mutableStateOf(false) }
    var canShowAlertDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("图片压缩处理工具") }

    val indicatorSize = 144.dp
    val trackWidth: Dp = (indicatorSize * .1f)
    val commonModifier = Modifier.size(indicatorSize)

    var compressProgress by remember { mutableStateOf(0f) }


    eHandlder = object : HandleError {
        override fun handleAccountError() {
            dialogs["未配置API或者已达到上限，请检查并配置API"] = true
        }

        override fun handleClientError() {
            dialogs["请检查图片或者重试"] = true
        }

        override fun handleServerError() {
            dialogs["TinyPNG服务器出问题，请稍后重试"] = true
        }

        override fun handleConnectionError() {
            dialogs["网络错误，请检查重试"] = true
        }

        override fun handleOtherError(msg: String) {
            dialogs["转换出故障，信息：${msg}"] = true
        }

        override fun defaultOperation() {
            buttonEnable = true
        }

    }

    mScope = CoroutineScope(Dispatchers.IO + errorHandler)

    // Create an infinite transition to animate the gradient
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the start and end positions of the gradient using a quadratic bezier curve
    val startX by infiniteTransition.animateFloat(
        initialValue = -100f, // Initial value of startX
        targetValue = 1000f, // Target value of startX
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 3000, // Duration of 3 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

    val endX by infiniteTransition.animateFloat(
        initialValue = -100f, // Initial value of endX
        targetValue = 1000f, // Target value of endX
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 3000, // Duration of 3 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )


    // Add a rememberUpdatedState to get the latest values of startX and endX
    val currentStartX by rememberUpdatedState(startX)
    val currentEndX by rememberUpdatedState(endX)


// Animate the first color from #0F9F8E to #FFFFFF
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0F9F8E), // Initial value of color1
        targetValue = Color(0xFFFFFFFF), // Target value of color1
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 7777, // Duration of 3 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

// Animate the second color from #FFFFFF to #00FFA3
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFFFFF), // Initial value of color2
        targetValue = Color(0xFF00FFA3), // Target value of color2
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 4444, // Duration of 4 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

// Animate the third color from #00FFA3 to #0C2D48
    val color3 by infiniteTransition.animateColor(
        initialValue = Color(0xFF00FFA3), // Initial value of color3
        targetValue = Color(0xFF0C2D48), // Target value of color3
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 6666, // Duration of 5 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

// Animate the fourth color from #0C2D48 to #F9F871
    val color4 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0C2D48), // Initial value of color4
        targetValue = Color(0xFFF9F871), // Target value of color4
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 6666, // Duration of 6 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

// Animate the fifth color from #F9F871 to #0F9F8E (back to the first color)
    val color5 by infiniteTransition.animateColor(
        initialValue = Color(0xFFF9F871), // Initial value of color5
        targetValue = Color(0xFF0F9F8E), // Target value of color5
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 7777, // Duration of 7 seconds
                easing = CubicBezierEasing(0.5f, 0f, 0.5f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

// Add a rememberUpdatedState to get the latest values of each color
    val currentColor1 by rememberUpdatedState(color1)
    val currentColor2 by rememberUpdatedState(color2)
    val currentColor3 by rememberUpdatedState(color3)
    val currentColor4 by rememberUpdatedState(color4)
    val currentColor5 by rememberUpdatedState(color5)


    val largeRadialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
//            return RadialGradientShader(
//                colors = listOf(currentColor1, currentColor2, currentColor3, currentColor4, currentColor5),
//                center = ,
//                radius = biggerDimension / 2f,
//                tileMode = TileMode.Mirror
//            )
            return LinearGradientShader(
                Offset(0f, 0f),
                Offset(size.height, size.width),
                colors = listOf(currentColor3, currentColor1),
            )
        }
    }

    val gradientButton = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            return LinearGradientShader(
                Offset(0f, 0f),
                Offset(size.height, size.width),
                colors = listOf(currentColor3, currentColor1, currentColor2, currentColor5, currentColor4),
            )
        }
    }

    MaterialTheme {
        // A surface container using a new brush with the animated start and end positions as background color
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(largeRadialGradient)
            ) {

                // A column to arrange the components vertically
                Column(
                    modifier = Modifier.fillMaxSize(), // Fill the entire screen
                    verticalArrangement = Arrangement.Center, // Center the column content vertically
                    horizontalAlignment = Alignment.CenterHorizontally // Center the column content horizontally
                ) {
                    // A text component to show the title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h4,
                        color = Color.White
                    )
                    // A spacer to add some space between the title and the progress bar
                    Spacer(modifier = Modifier.height(16.dp))
                    // A progress bar component to show the progress of file processing
                    GradientProgressIndicator(
                        compressProgress,
                        gradientStart = color1,
                        gradientEnd = color3,
                        modifier = commonModifier,
                        strokeWidth = trackWidth,
                        trackColor = color4
                    )
                    // A spacer to add some space between the progress bar and the button
                    Spacer(modifier = Modifier.height(16.dp))
                    // A button component to select files or folders
                    Button(
                        onClick = {
                            buttonEnable = false

                            FileDialog(ComposeWindow()).apply {
                                isMultipleMode = true
                                isVisible = true

                                if (files.isNullOrEmpty()) {
                                    buttonEnable = true
                                    return@apply
                                }

                                if (files.any { it.extension.lowercase() !in supportedFiles }) {
                                    canShowAlertDialog = true
                                    return@apply
                                }

                                val resultDir = this.directory + "result/"

                                val nDir = File(resultDir)
                                if (!nDir.exists()) {
                                    nDir.mkdir()
                                }

                                val jobs = mutableListOf<Job?>()

                                val doCompress = {
                                    val counter = AtomicInteger(0)

                                    files?.forEach {
                                        val job = ImageCompress.createCompressJob(
                                            scope = mScope,
                                            it.absolutePath,
                                            "$resultDir${it.name}"
                                        ) {
                                            compressProgress =
                                                counter?.getAndIncrement()?.toFloat()?.div(jobs.size.toFloat()) ?: 0f
                                        }

                                        jobs.add(job)
                                    }

                                    mScope?.launch {

                                        joinAll(*jobs.filterNotNull().toTypedArray())

                                        val result = File(resultDir)
                                        var resultSizeMB = ""
                                        if (result.isDirectory) {
                                            val totalSize = result.walk().filter { it.isFile }.sumOf { it.length() }
                                            resultSizeMB = totalSize.toMBString()
                                        }

                                        withContext(Dispatchers.Main) {
                                            usedCount = Tinify.compressionCount()
                                            buttonEnable = true

                                            compressProgress = 0f

                                            dialogs["压缩任务处理完成\n一共处理${files?.size}个文件\n压缩前总大小 ${
                                                files?.sumOf { it.length() }?.toMBString()
                                            }\n压缩后总大小 $resultSizeMB"] = true

                                            if (settings.get(openAfterFinish, true))
                                                openFolder(resultDir)
                                        }

                                    }
                                }
                                var hintBean: HintDialogBean? = null

                                hintBean = HintDialogBean(
                                    content = "一共${files?.size}个文件，总大小 ${
                                        files?.sumOf { it.length() }?.toMBString()
                                    }\n是否开始压缩任务？",
                                    action = {
                                        doCompress()
                                        // TODO 视觉化进度
                                        hintDialogs[hintBean!!] = false
                                    },
                                    cancelAction = {
                                        hintDialogs[hintBean!!] = false
                                        buttonEnable = true
                                    }
                                )

                                hintDialogs[hintBean] = true

                            }


                        }, // Add your logic to handle button click here
                        modifier = Modifier.size(200.dp), // Set the button size to 200 dp
                        shape = CircleShape, // Set the button shape to circle
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        enabled = buttonEnable
                    ) {
                        // A text component inside the button to show the label
                        Text(
                            text = "选择文件(多选）",
                            style = MaterialTheme.typography.h6,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp) // Add some padding around the text
                        )
                    }

                    // A row to arrange the components horizontally at the top right corner of the screen
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp), // Fill the entire width and add some padding around the row, and align the row to the top end of its parent
                        horizontalArrangement = Arrangement.End, // Align the row content to the end (right)
                        verticalAlignment = Alignment.Top, // Align the row content to the top
                    ) {
                        // A text component to show some information
                        Text(
                            text = "接口用量：$usedCount",
                            style = MaterialTheme.typography.body1,
                            color = Color.White,
                            modifier = Modifier.padding(end = 8.dp) // Add some padding to the right of the text
                        )
                        // An icon button component to show a settings icon and handle click events
                        IconButton(
                            onClick = {
                                canShowSettingDialog = true
                            },
                            enabled = buttonEnable
                        ) { // Add your logic to handle button click here
                            Icon(
                                imageVector = Icons.Default.Settings, // Use a predefined settings icon from material icons
                                contentDescription = "设置", // Provide a content description for accessibility purposes
                                tint = Color.White // Set the icon tint color to white
                            )
                        }
                    }
                }
            }
        }
    }


    if (canShowSettingDialog) {
        SettingsDialog(settings, largeRadialGradient) {
            usedCount = Tinify.compressionCount()
            canShowSettingDialog = false
        }
    }

    if (canShowAlertDialog) {
        SupportHintAlertDialog {
            canShowAlertDialog = false
            buttonEnable = true
        }
    }

    dialogs.forEach { (key, value) ->
        if (value) {
            SureAlertDialog(
                title = "提示",
                text = "$key",
                onConfirm = { dialogs[key] = false }
            )
        }
    }

    hintDialogs.forEach { (key, value) ->
        if (value) {
            CommonHintDialog(key.content, key.action, key.cancelAction)
        }
    }

}