package com.sumygg.anarts.arts.silksky

import androidx.compose.runtime.mutableStateOf
import com.sumygg.anarts.arts.ArtsConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SilkSky 配置项
 */
class SilkSkyConfig : ArtsConfig() {

    /**
     * 颜色层级数量
     */
    val circleNum = mutableStateOf(15)

    /**
     * 中心空白圆半径
     */
    val sunRadius = mutableStateOf(5.0f)




    private suspend fun animateCircleNum(startValue: Int, endValue: Int, duration: Long) {
        var value = startValue
        val valuePerFrame = (endValue - startValue).toFloat() / (duration / 16)
        var elapsed = 0L

        while (elapsed < duration) {
            elapsed += 16
            value += valuePerFrame.toInt()
            circleNum.value = value.coerceIn(startValue, endValue)
            delay(16)
        }
    }

    private suspend fun animateSunRadius(startValue: Float, endValue: Float, duration: Long) {
        var value = startValue
        val valuePerFrame = (endValue - startValue) / (duration / 16)
        var elapsed = 0L

        while (elapsed < duration) {
            elapsed += 16
            value += valuePerFrame
            sunRadius.value = value.coerceIn(startValue, endValue)
            delay(16)
        }
    }


}
