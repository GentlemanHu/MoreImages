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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.tinify.AccountException
import com.tinify.ClientException
import com.tinify.ConnectionException
import com.tinify.ServerException

import com.tinify.Tinify
import kotlinx.coroutines.*
import java.awt.FileDialog
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

fun exit(app: ApplicationScope) {
    app.exitApplication()
}

fun main() = application {

    ImageCompress.setKey()
    kotlin.runCatching {
        Tinify.validate()
    }

    Window(
        onCloseRequest = { exit(this) },
        icon = BitmapPainter(useResource("assets/image_trans_logo.jpg", ::loadImageBitmap)),
        resizable = false,
        title = "MoreImages"
    ) {
        App()
        mScope?.launch {
            Tinify.validate()
            throw ResetException()
        }
    }

}

