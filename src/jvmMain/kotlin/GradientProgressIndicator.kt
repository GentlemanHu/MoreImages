import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Composable
fun GradientProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    gradientStart: Color,
    gradientEnd: Color,
    trackColor: Color,
    strokeWidth: Dp
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
    }
    // 定义一个变量，用来存储绘制区域的大小
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Box {
        Canvas(
            modifier
                .progressSemantics(progress)
                // 使用 onGloballyPositioned 回调函数来获取绘制区域的大小，并赋值给 canvasSize
                .onGloballyPositioned { coordinates ->
                    canvasSize = coordinates.size.toSize()
                }
        ) {
            // Start at 12 o'clock
            val startAngle = 270f
            val sweep = progress * 360f
            drawDeterminateCircularIndicator(startAngle, 360f, trackColor, stroke)
            drawCircularIndicator(
                startAngle = startAngle,
                sweep = sweep,
                gradientStart = gradientStart,
                gradientEnd = gradientEnd,
                stroke = stroke
            )
        }
        // 定义一个变量，用来存储百分比的字符串，例如 "10%"
        val percentageText = "${(progress * 100).toInt()}%"
        // 定义一个变量，用来存储文字的大小，根据 canvasSize 和文字的长度来计算
        val textSize = canvasSize.width * 0.3f / percentageText.length
        // 在圆环的中心位置绘制一个 Text 组件，用来显示百分比
        Text(
            text = percentageText, // 文字内容
            fontSize = textSize.sp, // 文字大小
            color = trackColor, // 文字颜色
            modifier = Modifier.align(Alignment.Center) // 布局位置
        )
    }
}


private fun DrawScope.drawDeterminateCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) = drawCircularIndicator(startAngle, sweep, color, stroke)

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    gradientStart: Color,
    gradientEnd: Color,
    stroke: Stroke
) {
    // To draw this circle we need a rect with edges that line up with the midpoint of the stroke.
    // To do this we need to remove half the stroke width from the total diameter for both sides.
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    rotate(degrees = -90f) {
        drawArc(
            brush = Brush.sweepGradient(
                colorStops = listOf(
                    0.0f to gradientStart,
                    sweep / 360 to gradientEnd,
                ).toTypedArray()
            ),
            startAngle = startAngle + 90,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}