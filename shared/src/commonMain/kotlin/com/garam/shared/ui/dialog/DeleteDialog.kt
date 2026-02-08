package com.garam.shared.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.garam.shared.ui.snackbar.SnackbarScreen
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray20
import com.garam.shared.util.resources.colorGray80
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.warningColor

@Composable
fun DeleteDialog(
    show: Boolean,
    type : String,
    id: String,
    onDismiss : () -> Unit,
    onDelete : (String) -> Unit
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
//                modifier = Modifier.wrapContentSize()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = when (type) {
                            "Category" -> "카테고리와 할 일이 모두 삭제됩니다"
                            "Todo" -> "전체 삭제할까요?"
                            "Plan" -> "선택한 일정을 삭제할까요?"
                            else -> "카테고리와 할 일이 모두 삭제됩니다"
                        },
                        color = colorGray100,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (type) {
                            "Category" -> "삭제된 내용은 복구할 수 없어요.\n신중하게 결정해 주세요."
                            "Todo" -> "지금까지의 기록이 모두 사라져요.\n기록을 남기고 싶다면, \n[오늘까지만 반복] 을 선택해보세요."
                            "Plan" -> "지금까지의 기록이 모두 사라져요."
                            else -> "카테고리와 할 일이 모두 삭제됩니다"
                        }, textAlign = TextAlign.Center,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                        fontSize = 14.sp, color = colorGray100
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(modifier = Modifier.weight(1f).background(color = colorGray20.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp)),
                            onClick = { onDismiss() }) {

                            Text("취소", color = colorGray80,
                                fontFamily = fontFamily(), fontWeight = FontWeight.Medium,
                                fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(modifier = Modifier.weight(1f).background(color = warningColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)),
                            onClick = {
//                                isShowSnackbar = true
                                onDelete(id) })
                        {
                            Text("삭제", color = warningColor,fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                                fontSize = 14.sp)

                        }


                    }


                }

            }

        }


    }


}