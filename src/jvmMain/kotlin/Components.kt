import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

@Composable
// 封装的设置弹窗函数，接收一个Settings对象，一个Brush对象和一个回调函数作为参数，用来处理弹窗关闭的逻辑
fun SettingsDialog(settings: Settings, brush: Brush, onDismiss: () -> Unit) {
    // 从Settings对象中获取用户的设置，如果没有则使用默认值
    var webpSwitch by remember { mutableStateOf(settings.get(webpKey, true)) }
    var progress by remember { mutableStateOf(settings.get(progressKey, 75f)) }
    var forceSwitch by remember { mutableStateOf(settings.get(forceKey, false)) }
    var apiKey by remember { mutableStateOf(settings.get(apiKeyKey, "")) }
    var openAfterFinishSwitch by remember { mutableStateOf(settings.get(openAfterFinish, true)) }

    // 使用Dialog或者Popup来显示一个Box，代替AlertDialog
    Popup(alignment = Alignment.Center, focusable = true) {
        Box(
            modifier = Modifier
                .background(brush)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center, // Center the column content vertically
                horizontalAlignment = Alignment.Start // Center the column content horizontally)
            ) {
                Text(
                    text = "设置", color = Color.White, fontSize = 30.sp
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "压缩完自动转换为webp", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = webpSwitch,
                        onCheckedChange = {
                            webpSwitch = it
                            settings.set(webpKey, it)
                        }
                    ) // 当用户切换开关时，把新的值存入Settings对象中
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "压缩完自动打开文件夹", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = openAfterFinishSwitch,
                        onCheckedChange = {
                            openAfterFinishSwitch = it
                            settings.set(openAfterFinish, it)
                        }
                    ) // 当用户切换开关时，把新的值存入Settings对象中
                }
                if (webpSwitch) { // 如果第一个选项为开，则显示第二个和第三个选项
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "webp压缩比率（越低文件越小，质量越差）", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box { // 使用Box来包裹Slider和Text
                            Slider(
                                value = progress,
                                onValueChange = {
                                    progress = it
                                    settings.set(progressKey, it)
                                }, // 当用户拖动进度条时，把新的值存入Settings对象中
                                valueRange = 0f..100f
                            )
                            Text(
                                text = "${progress.toInt()}%",
                                fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White
                            ) // 使用Modifier.align(Alignment.Center)来让Text显示在中间
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "强制覆盖为webp（忽略文件大小，统一转换为webp）", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = forceSwitch,
                            onCheckedChange = {
                                forceSwitch = it
                                settings.set(forceKey, it)
                            }) // 当用户切换开关时，把新的值存入Settings对象中
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = {
                            println("value----$it")
                            apiKey = it
                            settings.set(apiKeyKey, it)
                        }, // 当用户输入API key时，把新的值存入Settings对象中
                        label = { Text(text = "API key", color = Color.White) },
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        readOnly = false,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
//                    Button(onClick = { /* 保存API key的逻辑 */ }) {
//                        Text(text = "保存API", color = Color.White)
//                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Button(onClick = onDismiss) {
                    Text(text = "关闭设置", color = Color.White)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun SupportHintAlertDialog(crossinline action: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
//            action()
        },
        confirmButton = {
            Button(onClick = { action() }, modifier = Modifier.padding(top = 20.dp)) {
                Text("好的")
            }
        },
        title = { Text("只支持${supportedFiles.joinToString(",")}类型文件") },
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
inline fun CommonHintDialog(content: String, crossinline action: () -> Unit, crossinline cancelAction: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
//            action()
        },
        confirmButton = {
            Button(onClick = { action() }, modifier = Modifier.padding(top = 20.dp)) {
                Text("确认")
            }
        },
        dismissButton = {
            Button(onClick = { cancelAction() }, modifier = Modifier.padding(top = 20.dp)) {
                Text("取消")
            }
        },
        title = { Text("提示") },
        text = { Text(content) }
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SureAlertDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = { onConfirm() }, modifier = Modifier.padding(top = 20.dp)) {
                Text("好的")
            }
        }
    )
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DragField() {
    var isDroppable by remember { mutableStateOf(true) }
    val (textFieldValue, setTextFieldValue) = remember { mutableStateOf(TextFieldValue()) }
    TextField(
        value = textFieldValue,
        onValueChange = setTextFieldValue,
        modifier = Modifier
            .border(4.dp, if (isDroppable) Color.Green else Color.Red)
            .onExternalDrag(
                onDragStart = { externalDragValue ->
                    println(externalDragValue)
//                    isDroppable = externalDragValue.dragData is androidx.compose.ui.DragData.Text
                },
                onDragExit = {
                    isDroppable = false
                },
                onDrop = { externalDragValue ->
                    println(externalDragValue.dragData)
                    if (externalDragValue.dragData is DragData.FilesList) {
                        val files = (externalDragValue.dragData as DragData.FilesList).readFiles()
                        println(files.joinToString(","))
                    }
                    if (externalDragValue.dragData is DragData.Image) {
                        val image = (externalDragValue.dragData as DragData.Image).readImage()
                        println(image)
                    }
                    isDroppable = false
                    val dragData = externalDragValue.dragData
                    if (dragData is androidx.compose.ui.DragData.Text) {
                        setTextFieldValue(
                            textFieldValue.copy(
                                text = textFieldValue.text.substring(0, textFieldValue.selection.start) +
                                        dragData.readText() +
                                        textFieldValue.text.substring(textFieldValue.selection.end),
                                selection = TextRange(textFieldValue.selection.end)
                            )
                        )
                    }
                },
            )
    )
}

