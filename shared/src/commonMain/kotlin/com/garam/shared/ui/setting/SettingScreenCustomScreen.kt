package com.garam.shared.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garam.shared.data.AppPreferences
import com.garam.shared.ui.bottomSheet.ScreenCustomDialog
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray60
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.shared.util.resources.warningColor
import com.garam.todolist.Res
import com.garam.todolist.setting_next_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingScreenCustomScreen(viewModel: SettingViewModel, onBackClick: () -> Unit) {


    val startScreenMode = AppPreferences.getString("start_screen_mode", "CalenDo")
    val sortMode = AppPreferences.getString("sort_mode", "Saved")
    var firstDayOfWeek = AppPreferences.getString("first_day_of_week", "Mon")
    var check by remember { mutableStateOf(firstDayOfWeek == "Sun") }

    var selectedType by remember { mutableStateOf("start_screen") }


    var showDialog by remember { mutableStateOf(false) }


    if (showDialog) ScreenCustomDialog(
        selectedType, onDismiss = {
            if (selectedType == "start_screen") {
                AppPreferences.setString(
                    "start_screen_mode", when (it) {
                        0 -> "CalenDo"
                        1 -> "Todo"
                        2 -> "Plan"
                        else -> "CalneDo"
                    }
                )
            } else AppPreferences.setString(
                "sort_mode", when (it) {
                    0 -> "Saved"
                    1 -> "Completed"
                    2 -> "Completed_Reversed"
                    else -> "Saved"
                }
            )
            showDialog = false
        }, if (selectedType == "start_screen") when (startScreenMode) {
            "CalenDo" -> 0
            "Todo" -> 1
            "Plan" -> 2
            else -> 0
        }
        else when (sortMode) {
            "Saved" -> 0
            "Completed" -> 1
            "Completed_Reversed" -> 2
            else -> 0

        }
    )

    Column(
        modifier = Modifier.background(color = mainBackgroundColor).fillMaxSize()

    ) {

        SettingTopBar("화면 커스텀", onBackClick = {
            onBackClick()
        })

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
                .clickable(true, onClick = {
                    selectedType = "start_screen"
                    showDialog = true

                })
                .background(color = Color.White, shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "시작 화면 설정", modifier = Modifier.weight(1f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 16.sp, color = colorGray100
            )
            Text(
                text = when (startScreenMode) {
                    "CalenDo" -> "뚜잇"
                    "Todo" -> "할일"
                    "Plan" -> "일정"
                    else -> "뚜잇"
                },
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 14.sp, color = colorGray60
            )
            IconButton(onClick = {
                selectedType = "start_screen"
                showDialog = true

            }) {
                Icon(painter = painterResource(Res.drawable.setting_next_icon), contentDescription = "")
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .clickable(true,onClick = {
                    selectedType = "todo_sort"
                    showDialog = true
                })
                .background(color = Color.White, shape = RoundedCornerShape(14.dp))
                .padding( horizontal = 12.dp)
        ) {
            Text(
                text = "할일 정렬", modifier = Modifier.weight(1f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 16.sp, color = colorGray100
            )
            Text(
                text = when (sortMode) {
                    "Saved" -> "작성한 순"
                    "Completed" -> "완료한 일이 위"
                    "Completed_Reversed" -> "완료한 일이 아래"
                    else -> "작성한 순"
                },
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 14.sp, color = colorGray60
            )
            IconButton(onClick = {
                selectedType = "todo_sort"
                showDialog = true
            }) {

                Icon(painter = painterResource(Res.drawable.setting_next_icon), contentDescription = "")
            }


        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp).clickable(true, onClick = {


            }), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1주일 일요일부터 시작", modifier = Modifier.weight(1f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 16.sp, color = colorGray100
            )
            Switch(
                checked = check, onCheckedChange = {

                    check = it

                    firstDayOfWeek = if (it) "Sun"
                    else "Mon"

                    AppPreferences.setString("first_day_of_week", firstDayOfWeek)


                }, modifier = Modifier.padding(end = 14.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorGray0,
                    uncheckedThumbColor = colorGray0,
                    checkedTrackColor = mainColor,
                    uncheckedTrackColor = colorGray10,
                    checkedBorderColor = colorGray10,
                    checkedIconColor = colorGray10,
                    uncheckedBorderColor = colorGray10,
                    uncheckedIconColor = colorGray10,
                    disabledCheckedThumbColor = colorGray10,
                    disabledCheckedTrackColor = colorGray10,
                    disabledCheckedBorderColor = Color.Unspecified,
                    disabledCheckedIconColor = Color.Unspecified,
                    disabledUncheckedThumbColor = colorGray10,
                    disabledUncheckedTrackColor = colorGray10,
                    disabledUncheckedBorderColor = Color.Unspecified,
                    disabledUncheckedIconColor = Color.Unspecified,
                )
            )
        }

    }

}