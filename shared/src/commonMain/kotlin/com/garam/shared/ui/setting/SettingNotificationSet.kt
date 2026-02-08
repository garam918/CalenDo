package com.garam.shared.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.garam.shared.data.AppPreferences
import com.garam.shared.notification.PermissionStatus
import com.garam.shared.notification.createNotificationScheduler
import com.garam.shared.notification.rememberPermissionHandler
import com.garam.shared.platform
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray30
import com.garam.shared.util.resources.colorGray60
import com.garam.shared.util.resources.colorGray80
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.shared.util.resources.subColor
import com.garam.shared.util.uiComponent.CustomTimePicker
import com.garam.todolist.Res
import com.garam.todolist.setting_next_icon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingNotificationSettingScreen(viewModel: SettingViewModel, onBackClick: () -> Unit) {

    val scope = rememberCoroutineScope()

    var isNotificationGrantedStatus by remember { mutableStateOf(PermissionStatus.NOT_DETERMINED) }

    val permissionHandler = rememberPermissionHandler { isGranted ->
        isNotificationGrantedStatus = isGranted
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        scope.launch {
            isNotificationGrantedStatus = permissionHandler.checkPermissionStatus()
        }
    }

    LaunchedEffect(Unit) {
        isNotificationGrantedStatus = permissionHandler.checkPermissionStatus()
    }

    var pushNotificationTimeSettingBottomSheet by remember { mutableStateOf(false) }

    var savedTodoNotiTime by remember {
        mutableStateOf(
            AppPreferences.getString(
                "todo_noti_time",
                ""
            )
        )
    }
    var savedPlanNotiTime by remember {
        mutableStateOf(
            AppPreferences.getString(
                "plan_noti_time",
                ""
            )
        )
    }

    var customTimeType by remember { mutableStateOf("Todo") }

    PushNotificationTimeBottomSheet(
        type = customTimeType,
        openBottomSheet = pushNotificationTimeSettingBottomSheet,
        onDismiss = {
            pushNotificationTimeSettingBottomSheet = false

        },
        onSave = { type, time ->

            val key = when (type) {
                "Todo" -> "todo_noti_time"
                "Plan" -> "plan_noti_time"
                else -> "todo_noti_time"

            }
            AppPreferences.setString(key, time)

            when (type) {
                "Todo" -> savedTodoNotiTime = time
                "Plan" -> savedPlanNotiTime = time
                else -> savedTodoNotiTime = time
            }

            viewModel.cancelNotification(type)
            viewModel.setNotification(type, time)
        }
    )



    Column(modifier = Modifier.background(color = mainBackgroundColor).fillMaxSize()) {

        SettingTopBar("푸시 알림 설정", onBackClick = {
            onBackClick()
        })

        Spacer(modifier = Modifier.height(35.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
            .clickable(true, onClick = {
                if (platform() == "iOS") {

                    when (isNotificationGrantedStatus) {
                        PermissionStatus.GRANTED -> permissionHandler.openAppSettings()
                        PermissionStatus.NOT_DETERMINED -> permissionHandler.askPermission()
                        PermissionStatus.DENIED -> permissionHandler.openAppSettings()
                    }
                } else permissionHandler.openAppSettings()

            }), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "푸시알림을 받으려면 휴대폰 알림을 켜주세요",
                fontFamily = fontFamily(),
                fontSize = 12.sp,
                color = subColor,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.weight(1f).padding(start = 10.dp)
            )
            IconButton(onClick = {
                if (platform() == "iOS") {

                    when (isNotificationGrantedStatus) {
                        PermissionStatus.GRANTED -> permissionHandler.openAppSettings()
                        PermissionStatus.NOT_DETERMINED -> permissionHandler.askPermission()
                        PermissionStatus.DENIED -> permissionHandler.openAppSettings()
                    }
                } else permissionHandler.openAppSettings()

            }, modifier = Modifier.wrapContentSize()) {

                Icon(
                    painter = painterResource(Res.drawable.setting_next_icon),
                    contentDescription = ""
                )

            }
        }

        HorizontalDivider(modifier = Modifier.height(1.dp), color = colorGray100.copy(alpha = 0.1f))

        Spacer(modifier = Modifier.height(10.dp))

        DrawNotificationMenu(
            viewModel, type = "Todo", savedTime = savedTodoNotiTime.toString(),
            onCustomTimeClick = { type ->
                customTimeType = type
                pushNotificationTimeSettingBottomSheet = true
            })

        Spacer(modifier = Modifier.height(10.dp))

        DrawNotificationMenu(
            viewModel, type = "Plan", savedTime = savedPlanNotiTime.toString(),
            onCustomTimeClick = { type ->
                customTimeType = type
                pushNotificationTimeSettingBottomSheet = true
            })

//        Spacer(modifier = Modifier.height(10.dp))

//        DrawNotificationMenu(type = "Plan", savedTime = savedPlanNotiTime.toString(),
//            onCustomTimeClick = { type ->
//                customTimeType = type
//                pushNotificationTimeSettingBottomSheet = true
//            })


    }
}

@Composable
fun DrawNotificationMenu(
    viewModel: SettingViewModel, type: String, savedTime: String,
    onCustomTimeClick: (String) -> Unit
) {

    val timeDefaultButtonModifier = Modifier
        .wrapContentSize()
        .clip(shape = RoundedCornerShape(size = 30.dp))
        .border(width = 1.dp, color = colorGray30, shape = RoundedCornerShape(size = 30.dp))
        .background(color = Color.White)

    val timeSelectedButtonModifier = Modifier
        .background(color = Color.Black, shape = RoundedCornerShape(30.dp))

    var isChecked by remember { mutableStateOf(savedTime.isNotBlank()) }

    LaunchedEffect(savedTime) {

        isChecked = savedTime.isNotBlank()

    }

    var savedNotificationTime by remember { mutableStateOf(savedTime) }

    LaunchedEffect(savedTime) {

        savedNotificationTime = savedTime

    }


    var selectedIndex by remember {
        mutableStateOf(
            when (savedTime) {
                "오전 09:00" -> 0
                "오후 12:00" -> 1
                "오후 09:00" -> 2
                else -> 3
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {


        Row(modifier = Modifier.fillMaxWidth()) {

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (type) {
                            "Todo" -> "할일 알림"
                            "Plan" -> "일정 알림"
                            else -> "할일 알림"
                        },
                        fontFamily = fontFamily(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = colorGray100
                    )

                    if (isChecked && savedNotificationTime.isNotBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = savedNotificationTime,
                            color = colorGray100.copy(alpha = 0.6f),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (type) {
                        "Todo" -> "오늘 해야하는 할일을 미리 알려드려요"
                        "Plan" -> "등록된 일정을 미리 알려드려요"
                        else -> "오늘 해야하는 할일을 미리 알려드려요"
                    },
                    fontFamily = fontFamily(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorGray60
                )

            }

            Switch(
                onCheckedChange = {
                    isChecked = it

                    if (!isChecked) {
                        when (type) {
                            "Todo" -> AppPreferences.setBoolean("todo_noti", false)
                            "Plan" -> AppPreferences.setBoolean("plan_noti", false)
                        }
                    } else {
                        when (type) {
                            "Todo" -> AppPreferences.setBoolean("todo_noti", true)
                            "Plan" -> AppPreferences.setBoolean("plan_noti", true)
                        }
                    }
                    // 일정 or 할일 알림 안뜨게

                }, checked = isChecked,
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

        if (isChecked) {
            Spacer(modifier = Modifier.height(16.dp))

            Row() {

                TextButton(
                    onClick = {
                        selectedIndex = 0
                        AppPreferences.setString(
                            when (type) {
                                "Todo" -> "todo_noti_time"
                                "Plan" -> "plan_noti_time"
                                else -> "todo_noti_time"

                            }, "오전 09:00"
                        )
                        savedNotificationTime = "오전 09:00"
                        viewModel.cancelNotification(type)
                        viewModel.setNotification(type, "오전 09:00")
                    },
                    modifier = if (selectedIndex == 0) timeSelectedButtonModifier else timeDefaultButtonModifier
                ) {
                    Text(
                        "오전 9시", color = if (selectedIndex == 0) Color.White else colorGray80,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal, fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        selectedIndex = 1
                        AppPreferences.setString(
                            when (type) {
                                "Todo" -> "todo_noti_time"
                                "Plan" -> "plan_noti_time"
                                else -> "todo_noti_time"

                            }, "오후 12:00"
                        )

                        savedNotificationTime = "오후 12:00"

                        viewModel.cancelNotification(type)
                        viewModel.setNotification(type, "오후 12:00")
                    },
                    modifier = if (selectedIndex == 1) timeSelectedButtonModifier else timeDefaultButtonModifier
                ) {
                    Text(
                        "오후 12시", color = if (selectedIndex == 1) Color.White else colorGray80,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal, fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        selectedIndex = 2
                        AppPreferences.setString(
                            when (type) {
                                "Todo" -> "todo_noti_time"
                                "Plan" -> "plan_noti_time"
                                else -> "todo_noti_time"

                            }, "오후 09:00"
                        )

                        savedNotificationTime = "오후 09:00"

                        viewModel.cancelNotification(type)
                        viewModel.setNotification(type, "오후 09:00")
                    },
                    modifier = if (selectedIndex == 2) timeSelectedButtonModifier else timeDefaultButtonModifier
                ) {
                    Text(
                        "오후 9시", color = if (selectedIndex == 2) Color.White else colorGray80,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal, fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        selectedIndex = 3
                        onCustomTimeClick(type)
                    },
                    modifier = if (selectedIndex == 3) timeSelectedButtonModifier else timeDefaultButtonModifier
                ) {
                    Text(
                        "직접 설정", color = if (selectedIndex == 3) Color.White else colorGray80,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal, fontSize = 14.sp,
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushNotificationTimeBottomSheet(
    type: String,
    openBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {

    val sheetState = rememberModalBottomSheetState()

    var customTimeAmPm by remember { mutableStateOf("오전") }
    var customTimeHour by remember { mutableStateOf("09") }
    var customTimeMin by remember { mutableStateOf("00") }


    if (openBottomSheet) ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(28.dp),
        containerColor = mainBackgroundColor,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 24.dp)
    ) {

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "푸시알림 시간 설정",
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorGray100
                )

                TextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {


                        onSave(type, "$customTimeAmPm $customTimeHour:$customTimeMin")
                        onDismiss()

                    }) {
                    Text(
                        text = "저장",
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorGray100
                    )

                }
            }

            CustomTimePicker(initialHour = 9, initialMinute = 0) { hour, minute, isAm ->

                if (isAm) customTimeAmPm = "오전" else customTimeAmPm = "오후"

                customTimeHour = hour.toString()
                customTimeMin = if (minute == 0) "00"
                else if (minute < 10) "0$minute"
                else minute.toString()

            }
        }
    }
}