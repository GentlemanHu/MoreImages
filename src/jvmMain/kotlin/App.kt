import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.russhwolf.settings.get
import com.tinify.*
import kotlinx.coroutines.*
import utils.ImageCompress
import java.awt.FileDialog
import java.io.File
import java.net.URI
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


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun App() {
    var usedCount by remember { mutableStateOf(Tinify.compressionCount()) }
    var buttonEnable by remember { mutableStateOf(false) }

    val dialogs = remember { mutableMapOf<String, Boolean>() }

    var buttonVisible by remember { mutableStateOf(true) }

    val hintDialogs = remember { mutableMapOf<HintDialogBean, Boolean>() }

    var canShowSettingDialog by remember { mutableStateOf(false) }
    var canShowAlertDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("图片压缩处理工具") }

    var isDragging by remember { mutableStateOf(false) }

    val indicatorSize = 200.dp
    val trackWidth: Dp = (indicatorSize * .1f)
    val commonModifier = Modifier.size(indicatorSize)

    var compressProgress by remember { mutableStateOf(0f) }

//    val artsModel by remember { mutableStateOf(ArtsModel(Arts.MAZE)) }
//

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
            buttonVisible = true
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
                durationMillis = 7777, // Duration of 3 seconds
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )

    val endX by infiniteTransition.animateFloat(
        initialValue = -100f, // Initial value of endX
        targetValue = 1000f, // Target value of endX
        animationSpec = infiniteRepeatable( // Repeat the animation infinitely
            animation = tween( // Use a tween animation
                durationMillis = 7777, // Duration of 3 seconds
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f) // Use a quadratic bezier curve as easing
            ),
            repeatMode = RepeatMode.Reverse // Reverse the animation direction when it reaches the target
        )
    )


    // Add a rememberUpdatedState to get the latest values of startX and endX
    val currentStartX by rememberUpdatedState(startX)
    val currentEndX by rememberUpdatedState(endX)


// Animate the first color from #0C2D48 to #F9F871
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0C2D48),
        targetValue = Color(0xFFF9F871),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7777,
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

// Animate the second color from #F9F871 to #FFFFFF
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFFF9F871),
        targetValue = Color(0xFFFFFFFF),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7777,
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

// Animate the third color from #FFFFFF to #FF414345
    val color3 by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFFFFF),
        targetValue = Color(0xFF414345),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7777,
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

// Animate the fourth color from #FF414345 to #00FFA3
    val color4 by infiniteTransition.animateColor(
        initialValue = Color(0xFF414345),
        targetValue = Color(0xFF00FFA3),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7777,
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

// Animate the fifth color from #00FFA3 to #0C2D48
    val color5 by infiniteTransition.animateColor(
        initialValue = Color(0xFF00FFA3),
        targetValue = Color(0xFF0C2D48),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7777,
                easing = CubicBezierEasing(0.15f, 0.25f, 0.55f, 1f)
            ),
            repeatMode = RepeatMode.Reverse
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

    fun startCompressTask(files: List<File>) {

        if (files.isEmpty()) {
            buttonEnable = true
            return
        }

        if (files.any { it.extension.lowercase() !in supportedFiles }) {
            canShowAlertDialog = true
            return
        }


        buttonVisible = false


        val resultDir = files.first().parent + "CompressResult_${System.currentTimeMillis()}/"

        val nDir = File(resultDir)
        if (!nDir.exists()) {
            nDir.mkdir()
        }

        val maxConcurrent = settings.get(maxConcurrent, 5)
        val jobs = mutableListOf<Job?>()

        val doCompress = {
            val counter = AtomicInteger(0)

            files?.forEach {
                val job = ImageCompress.compressVersionWrapper(
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

                jobs.chunked(maxConcurrent).forEach { subTasks ->
                    joinAll(*subTasks.filterNotNull().toTypedArray())
                }


                val result = File(resultDir)
                var resultSizeMB = ""
                if (result.isDirectory) {
                    val totalSize = result.walk().filter { it.isFile }.sumOf { it.length() }
                    resultSizeMB = totalSize.toMBString()
                }

                withContext(Dispatchers.Main) {
                    usedCount = Tinify.compressionCount()
                    buttonEnable = true
                    buttonVisible = true

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
                buttonVisible = true
            }
        )

        hintDialogs[hintBean] = true

    }


    fun onButtonClick() {
        if (isDragging) return

        buttonEnable = false

        openFilePicker {
            startCompressTask(it)
        }

    }



    MaterialTheme {
        // A surface container using a new brush with the animated start and end positions as background color
        Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(largeRadialGradient)
                    .onExternalDrag(
                        enabled = buttonEnable,
                        onDragStart = { data ->
                            isDragging = true
                            println("${data.dragData}----dragStart")
                        },
                        onDrag = { drag ->
                            isDragging = true
//                            println("${drag.dragData}----onDrag")
                        },
                        onDrop = { externalDragValue ->
                            println(externalDragValue.dragData)
                            if (externalDragValue.dragData is DragData.FilesList) {
                                val files = (externalDragValue.dragData as DragData.FilesList).readFiles()
                                println(files.joinToString(","))
                                val realFiles = files.map { File(URI.create(it)) }

                                isDragging = false
                                startCompressTask(realFiles)
                            }
                        },
                        onDragExit = {
                            isDragging = false
                        }
                    )
            ) {


//                ArtsView(artsModel)

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
                    Spacer(modifier = Modifier.height(25.dp))

//                    DragField()
                    // A button component to select files or folders
                    Box {
                        // A progress bar component to show the progress of file processing
                        GradientProgressIndicator(
                            compressProgress,
                            gradientStart = color3,
                            gradientEnd = color2,
                            modifier = commonModifier,
                            strokeWidth = trackWidth,
                            trackColor = color4
                        )

                        if (buttonVisible) {
                            Button(
                                onClick = { onButtonClick() }, // Add your logic to handle button click here
                                modifier = Modifier.size(indicatorSize - 2 * trackWidth)
                                    .align(Alignment.Center), // Set the button size to 200 dp
                                shape = CircleShape, // Set the button shape to circle
                                colors = ButtonDefaults.buttonColors(backgroundColor = color5),
                                enabled = buttonEnable,
                            ) {
                                // A text component inside the button to show the label
                                Text(
                                    text = if (isDragging) "松手释放" else "点击选择文件或者拖拽文件 (多选）",
                                    style = MaterialTheme.typography.subtitle2,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp) // Add some padding around the text
                                )
                            }
                        }

                    }


                    // A row to arrange the components horizontally at the top right corner of the screen
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp), // Fill the entire width and add some padding around the row, and align the row to the top end of its parent
                        horizontalArrangement = Arrangement.End, // Align the row content to the end (right)
                        verticalAlignment = Alignment.Top, // Align the row content to the top
                    ) {

                        Column {
                            // A text component to show some information
                            val isInternal = settings.get(useInternalEngine, false)
                            if (!isInternal)
                                Text(
                                    text = "接口用量：$usedCount",
                                    style = MaterialTheme.typography.body1,
                                    color = Color.White,
                                    modifier = Modifier.padding(end = 8.dp) // Add some padding to the right of the text
                                )

                            val engineText = if (isInternal) "内置引擎" else "TinyPNG"
                            Text(
                                text = "当前压缩引擎：$engineText",
                                style = MaterialTheme.typography.body1,
                                color = Color.White,
                                modifier = Modifier.padding(end = 8.dp) // Add some padding to the right of the text
                            )
                        }

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
            doIfNotUseInternal {
                mScope?.launch {
                    initTinyPng()
                    withContext(Dispatchers.Main) {
                        usedCount = Tinify.compressionCount()
                    }
                }
            }

            canShowSettingDialog = false
        }
    }

    if (canShowAlertDialog) {
        SupportHintAlertDialog {
            canShowAlertDialog = false
            buttonEnable = true
            buttonVisible = true
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



