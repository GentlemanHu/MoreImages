import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.*
import com.russhwolf.settings.get

import com.tinify.Tinify
import kotlinx.coroutines.*
import utils.ImageCompress
import utils.LibCompress
import javax.swing.UIManager

fun exit(app: ApplicationScope) {
    app.exitApplication()
}

fun main() = application {
    LibCompress
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    doIfNotUseInternal {
        ImageCompress.setKey()
        kotlin.runCatching {
            Tinify.validate()
        }
    }

    Window(
        onCloseRequest = { exit(this) },
        icon = BitmapPainter(useResource("assets/image_trans_logo.jpg", ::loadImageBitmap)),
        resizable = false,
        title = "MoreImages"
    ) {
        App()
        mScope?.launch {
            doIfNotUseInternal {
                Tinify.validate()
            }
            throw ResetException()
        }
    }

}

