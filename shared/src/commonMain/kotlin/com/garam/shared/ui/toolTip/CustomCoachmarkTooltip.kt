package com.garam.shared.ui.toolTip

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.fontFamily

@Composable
fun CustomCoachmarkTooltip(
    text: String,
    modifier: Modifier = Modifier,
    tooltipColor: Color = Color(0xFF3A3A3A) // 이미지와 유사한 어두운 회색
) {
    val beakSize = 10.dp // 꼬리표(삼각형)의 크기
    val cornerRadius = 12.dp // 둥근 모서리 크기

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 꼬리표 (삼각형)
        Canvas(modifier = Modifier.size(beakSize * 2, beakSize)) {
            val path = Path().apply {
                moveTo(size.width / 2f, 0f)     // 삼각형의 상단 꼭짓점
                lineTo(0f, size.height)        // 하단 왼쪽
                lineTo(size.width, size.height) // 하단 오른쪽
                close()
            }
            drawPath(path = path, color = tooltipColor)
        }

        // 2. 둥근 사각형 (본체)
        Surface(
            color = tooltipColor,
            shape = RoundedCornerShape(cornerRadius),
            // 꼬리표와 겹치게 하여 빈틈이 없도록 y축으로 살짝 올립니다.
            modifier = Modifier.offset(y = (-1).dp)
        ) {
            Text(
                text = text,
                color = colorGray0,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}