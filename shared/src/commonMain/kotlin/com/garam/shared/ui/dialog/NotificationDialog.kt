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
fun NotificationDialog(
    show: Boolean,
    onDismiss : () -> Unit,
    onConfirm : () -> Unit
) {

    if (show) {

        Dialog(
            onDismissRequest = {
                onDismiss()
            }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = mainBackgroundColor,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "‘뚜잇’에서 보내는 알림을 \n받아보시겠나요?",
                        color = colorGray100,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "경고, 사운드 및 아이콘 배지가 알림에 \n포함될 수 있습니다. \n설정에서 이를 구성할 수 있습니다.",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp, color = colorGray100
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            modifier = Modifier.weight(1f).background(
                                color = colorGray20.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                            onClick = { onDismiss() }) {

                            Text(
                                text = "허용 안 함", color = colorGray80,
                                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            modifier = Modifier.weight(1f).background(
                                color = mainColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                            onClick = {
                                onConfirm()
                            })
                        {
                            Text(
                                text = "허용",
                                color = colorGray0,
                                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )

                        }


                    }


                }

            }

        }


    }

}