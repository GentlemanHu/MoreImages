package com.sumygg.anarts.arts.dotswave

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.sumygg.anarts.arts.ArtsConfig
import com.sumygg.anarts.ui.formitem.ColorChooseFormItem
import com.sumygg.anarts.ui.formitem.ColorListFormItem

/**
 * DotsWave表单配置项
 */
class DotsWaveConfig : ArtsConfig() {

    /**
     * 背景颜色
     */
    @get:ColorChooseFormItem(title = "background")
    val background = mutableStateOf(Color.Black)

    /**
     * 颜色系列
     */
    @get:ColorListFormItem(title = "colors")
//    val colors = mutableStateListOf(
//        Color(0xFFFFBE0B),
//        Color(0xFFFB5607),
//        Color(0xFFFF006E),
//        Color(0xFF8338EC),
//        Color(0xFF3A86FF),
//    )

    val colors = mutableStateListOf(
        Color(0xFFFFFFFF),
        Color(0xFF90DAC8),
        Color(0xFFB6E7D7),
        Color(0xFFB3E5E6),
        Color(0xFFF1F1D5),
        Color(0xFFF2ECD9),
        Color(0xFFD5F1E8),

    )

}
