package com.garam.shared.util.modifier

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Modifier.singleClickable(
    interval: Long = 1500L,
    onClick: () -> Unit
): Modifier = composed {
    // 마지막 클릭 시간을 기억합니다.
    var lastClickTime by remember { mutableLongStateOf(0L) }

    this.clickable {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        // 현재 시간과 마지막 클릭 시간의 차이가 설정한 간격보다 클 때만 실행
        if (currentTime - lastClickTime > interval) {
            lastClickTime = currentTime
            onClick()
        }
    }
}