package com.garam.shared.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray20
import com.garam.shared.util.resources.colorGray80
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.shared.util.resources.warningColor

@Composable
fun AccountLogOutDeleteDialog(
    type: String,
    show: Boolean,
    onDismiss : () -> Unit,
    onConfirm : () -> Unit
) {

    if(show) {

        Dialog(
            onDismissRequest = {

                onDismiss()

            }
        ) {
            Surface (
                shape = RoundedCornerShape(24.dp),
                color = mainBackgroundColor,
                shadowElevation = 8.dp,
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = if(type == "Delete") "정말 계정을 삭제하실 건가요?" else "정말 로그아웃하시겠어요?",
                        color = colorGray100,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if(type == "Delete") "계정 정보와 지금 까지 기록한 내용은 \n복구되지 않습니다."
                        else "언제든 다시 로그인하실 수 있어요.\n지금은 안전하게 로그아웃할게요!", textAlign = TextAlign.Center,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                        fontSize = 14.sp, color = colorGray100
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(modifier = Modifier.weight(1f).background(color = colorGray20.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp)),
                            onClick = { onDismiss() }) {

                            Text(if(type == "Delete") "계속 사용" else "취소", color = colorGray80,
                                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                                fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(modifier = Modifier.weight(1f).background(color =
                            if(type == "Delete") warningColor.copy(alpha = 0.15f) else mainColor, shape = RoundedCornerShape(8.dp)),
                            onClick = {
                                onConfirm() })
                        {
                            Text(if(type == "Delete") "삭제" else "로그아웃",
                                color = if(type == "Delete") warningColor else colorGray0,
                                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                                fontSize = 14.sp)

                        }


                    }


                }

            }

        }


    }
}