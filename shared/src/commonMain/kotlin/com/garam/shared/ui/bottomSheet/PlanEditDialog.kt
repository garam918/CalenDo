package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.data.CategoryIconType
import com.garam.shared.data.Todo
import com.garam.shared.ui.dialog.DeleteDialog
import com.garam.shared.util.functions.dateToString
import com.garam.shared.util.functions.generateRepeatRule
import com.garam.shared.util.functions.localDateToDateStringForDialog
import com.garam.shared.util.functions.parseRRule
import com.garam.shared.util.functions.stringToCategoryIconResource
import com.garam.shared.util.functions.stringToColor
import com.garam.shared.util.functions.timePickerToString
import com.garam.shared.util.functions.toICalDay
import com.garam.shared.util.resources.categoryColors
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray80
import com.garam.shared.util.resources.colorGray90
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.icons
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.shared.util.resources.warningColor
import com.garam.shared.util.uiComponent.CustomTimePicker
import com.garam.todolist.Res
import com.garam.todolist.bottom_sheet_close_icon
import com.garam.todolist.plan_expand_icon
import com.garam.todolist.todo_delete_icon
import com.garam.todolist.week_goal_expand_icon
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class, ExperimentalTime::class)
@Composable
fun PlanEditDialog(
    type: String,
    plan: Todo?,
    openBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit,
    onDelete: (Todo?) -> Unit,
    selectedDate: LocalDate
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var planTitleText by remember { mutableStateOf(plan?.title ?: "") }

    var iconExpandState by remember { mutableStateOf(false) }
    var colorExpandState by remember { mutableStateOf(false) }

    var repeatExpandState by remember { mutableStateOf(false) }
    var startTimeExpandState by remember { mutableStateOf(false) }

    var selectedRepeatType by remember {
        mutableStateOf(
            if (plan?.repeatRule != null) parseRRule(
                plan.repeatRule
            )["FREQ"] else ""
        )
    }

    var repeatRule by remember { mutableStateOf(plan?.repeatRule) }

    var repeatDateString by remember {
        mutableStateOf(
            if (plan?.repeatRule == null) ""
            else dateToString(
                LocalDate.parse(plan.startDate), parseRRule(plan.repeatRule)["FREQ"]!!
            )
        )
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedIcon by remember { mutableStateOf(plan?.icon ?: CategoryIconType.HOME) }
    var selectedColor by remember { mutableStateOf(plan?.color ?: "default_color_1") }

    var startTime by remember { mutableStateOf(plan?.startTime) }

    var isShowPlanDeleteDialog by remember { mutableStateOf(false) }

    if (isShowPlanDeleteDialog) DeleteDialog(
        show = isShowPlanDeleteDialog,
        type = "Plan",
        id = plan?.id.toString(),
        onDismiss = { isShowPlanDeleteDialog = false },
        onDelete = {
            onDelete(plan!!)
            isShowPlanDeleteDialog = false
            onDismiss()
        }
    )


    if (openBottomSheet) ModalBottomSheet(
        onDismissRequest = {

            onDismiss()
            planTitleText = ""
            iconExpandState = false
            colorExpandState = false

            repeatRule = ""
            repeatExpandState = false
            startTimeExpandState = false
            selectedRepeatType = ""
            selectedIcon = CategoryIconType.HOME
            selectedColor = "default_color_1"

        },
        sheetState = sheetState,
        shape = RoundedCornerShape(28.dp),
        containerColor = mainBackgroundColor,
        modifier = Modifier.imePadding().padding(start = 12.dp, end = 12.dp, bottom = 36.dp),
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
//                .pointerInput(Unit) {
//                    awaitPointerEventScope {
//                        // 지속적으로 포인터 이벤트를 기다립니다.
//                        while (true) {
//                            val event = awaitPointerEvent()
//
//                            // DOWN 이벤트가 발생했을 때
//                            if (event.type == PointerEventType.Press) {
//                                // 텍스트 필드가 포커스를 가지고 있을 때만 처리합니다.
////                            if (focusManager.focusedChild != null) {
//                                // 1. 이벤트의 소비 상태를 확인하지 않고 강제로 DOWN 이벤트를 소비합니다.
//                                //    이렇게 하면 텍스트 필드나 버튼으로 이벤트가 전달되지 않습니다.
//                                event.changes.forEach { it.consumeDownChange() }
//
//                                // 2. 포커스 해제 및 키보드 숨김
//                                keyboardController?.hide()
//                                focusManager.clearFocus(force = true)
////                            }
//                            }
//                        }
//                    }
//                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // TextField 가 아닌 곳 터치 → 키보드 내림
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                }
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .background(color = mainBackgroundColor)
                    .padding(start = 24.dp, end = 24.dp, top = 12.dp)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        enabled = true,
//                        indication = null
//                    ) {
//                        focusManager.clearFocus(force = true)
//                        keyboardController?.hide()
//                    }
            ) {


                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {
                                onDismiss()
                            }) {
                            Icon(
                                painter = painterResource(Res.drawable.bottom_sheet_close_icon),
                                contentDescription = null
                            )
                        }

                        Text(
                            text = plan?.title
                                ?: "${localDateToDateStringForDialog(selectedDate)} 일정 추가",
                            fontWeight = FontWeight.Bold,
                            color = colorGray100,
                            fontFamily = fontFamily(),
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        TextButton(
                            onClick = {
                                if (plan == null) onSave(
                                    Todo(
                                        id = Uuid.random().toString(),
                                        categoryId = null,
                                        title = planTitleText,
                                        startDate = selectedDate.toString(),
                                        endDate = selectedDate.toString(),
                                        repeatRule = repeatRule,
                                        status = null,
                                        priority = false,
                                        memo = "",
                                        icon = selectedIcon,
                                        color = selectedColor,
                                        startTime = startTime,
                                        index = 0,
                                        savedTime = Clock.System.now().epochSeconds
                                    )
                                ) else onSave(
                                    plan.copy(
                                        title = planTitleText,
                                        repeatRule = repeatRule,
                                        icon = selectedIcon,
                                        color = selectedColor,
                                        startTime = startTime
                                    )
                                )

                                onDismiss()
                            },
                        ) {
                            Text(
                                text = "저장", fontWeight = FontWeight.Medium, color = colorGray100,
                                fontFamily = fontFamily(), fontSize = 16.sp
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(10.dp))

                }

                item {

                    TextField(
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        value = planTitleText.toString(),
                        onValueChange = {
                            planTitleText = it
                        },
                        modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(14.dp))
                            .background(color = Color.White).clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {}.onFocusChanged {

                                if (!it.isFocused) {
                                    keyboardController?.hide()
                                }

                            },
                        singleLine = true,
                        placeholder = {
                            Text(
                                "일정 제목을 입력해 주세요",
                                color = colorGray10,
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White, // 배경색 제거 (Box 배경 사용)
                            focusedIndicatorColor = Color.Transparent, // 밑줄 제거
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.White,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                }

                item {

                    Column(
                        modifier = Modifier.background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium
                        )
//                            .clickable(          // ← 여기 중요!
//                            interactionSource = remember { MutableInteractionSource() },
//                            enabled = true,
//                            indication = null
//                        ) {
//                            focusManager.clearFocus(force = true)
//                            keyboardController?.hide()
//                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {

                            Text(
                                "아이콘",
                                modifier = Modifier.padding(start = 14.dp),
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = colorGray90
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                painter = stringToCategoryIconResource(selectedIcon),
                                contentDescription = "",
                                modifier = Modifier.background(
                                    color = stringToColor(selectedColor).copy(0.3f),
                                    shape = MaterialTheme.shapes.medium
                                ).padding(4.dp),
                                tint = stringToColor(selectedColor)
                            )
                            IconButton(
                                onClick = { iconExpandState = !iconExpandState },
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.week_goal_expand_icon),
                                    contentDescription = ""
                                )
                            }
                        }

                        if (iconExpandState) Column(
                            modifier = Modifier.background(
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium
                            )
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth().height(1.dp)
                                    .padding(start = 14.dp, end = 14.dp), color = Color.LightGray
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(6), // 한 줄에 6개
                                modifier = Modifier.fillMaxWidth().height(250.dp),
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(icons) { iconType ->
                                    IconButton(onClick = { selectedIcon = iconType }) {
                                        Icon(
                                            painter = stringToCategoryIconResource(iconType),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

                item {

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {

                            Text(
                                "색상",
                                modifier = Modifier.padding(start = 14.dp),
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = colorGray90
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Canvas(modifier = Modifier.size(20.dp)) {
                                drawCircle(
                                    color = stringToColor(selectedColor), // 아래에서 선택된 색 반영
                                    radius = size.minDimension / 2
                                )
                            }
                            IconButton(
                                onClick = { colorExpandState = !colorExpandState },
                                modifier = Modifier.padding(5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.week_goal_expand_icon),
                                    contentDescription = ""
                                )

                            }
                        }

                        if (colorExpandState) Column(
                            modifier = Modifier.background(
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium
                            )
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth().height(1.dp)
                                    .padding(start = 14.dp, end = 14.dp), color = Color.LightGray
                            )
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(6), // 한 줄에 6개
                                modifier = Modifier.fillMaxWidth().height(130.dp),
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(categoryColors) { color ->
                                    IconButton(onClick = { selectedColor = color }) {
                                        Canvas(modifier = Modifier.size(20.dp)) {
                                            drawCircle(
                                                color = stringToColor(color), // 원 색상
                                                radius = size.minDimension / 2
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }

                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium
                        )
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "반복",
                                modifier = Modifier.padding(start = 14.dp),
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = colorGray90
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = repeatDateString,
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = colorGray90.copy(alpha = 0.5f)
                            )
                            IconButton(onClick = { repeatExpandState = !repeatExpandState }) {
                                Icon(
                                    painter = painterResource(Res.drawable.week_goal_expand_icon),
                                    contentDescription = ""
                                )
                            }

                        }
                        if (repeatExpandState) {

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {

                                TextButton(
                                    onClick = {
                                        selectedRepeatType = "WEEKLY"
                                        repeatRule = generateRepeatRule(
                                            "WEEKLY",
                                            daysOfWeek = listOf(if(plan != null) LocalDate.parse(plan.startDate).dayOfWeek.toICalDay() else selectedDate.dayOfWeek.toICalDay())
                                        )
                                        repeatDateString = if (plan != null) dateToString(
                                            LocalDate.parse(plan.startDate),
                                            "WEEKLY"
                                        )
                                        else dateToString(selectedDate, "WEEKLY")

                                    }, contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.background(
                                        color = if (selectedRepeatType == "WEEKLY") Color.Black else Color.White,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                ) {
                                    Text(
                                        "매주",
                                        color = if (selectedRepeatType == "WEEKLY") Color.White else colorGray80,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        selectedRepeatType = "MONTHLY"
                                        repeatRule = generateRepeatRule(
                                            "MONTHLY",
//                                            monthDays = listOf(selectedDate.day)
                                            monthDays = listOf(if(plan != null) LocalDate.parse(plan.startDate).day else selectedDate.day)
                                        )

                                        repeatDateString = if (plan != null) dateToString(
                                            LocalDate.parse(plan.startDate),
                                            "MONTHLY"
                                        )
                                        else dateToString(selectedDate, "MONTHLY")

                                    }, contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.background(
                                        color = if (selectedRepeatType == "MONTHLY") Color.Black else Color.White,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                ) {
                                    Text(
                                        "매월",
                                        color = if (selectedRepeatType == "MONTHLY") Color.White else colorGray80,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        selectedRepeatType = "YEARLY"
                                        repeatRule = generateRepeatRule(
                                            "YEARLY",
                                            monthDays = listOf(if(plan != null) LocalDate.parse(plan.startDate).day else selectedDate.day),
                                            months = listOf(if(plan != null) LocalDate.parse(plan.startDate).month.number else selectedDate.month.number)
                                        )

                                        repeatDateString = if (plan != null) dateToString(
                                            LocalDate.parse(plan.startDate),
                                            "YEARLY"
                                        )
                                        else dateToString(selectedDate, "YEARLY")

                                    }, contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.background(
                                        color = if (selectedRepeatType == "YEARLY") Color.Black else Color.White,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                ) {
                                    Text(
                                        "매년",
                                        color = if (selectedRepeatType == "YEARLY") Color.White else colorGray80,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        selectedRepeatType = ""
                                        repeatRule = null
                                        repeatDateString = ""
                                    }, contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.background(
                                        color = if (selectedRepeatType == "") Color.Black else Color.White,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                ) {
                                    Text(
                                        "없음",
                                        color = if (selectedRepeatType == "") Color.White else colorGray80,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }

                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium
                        ),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "하루 종일",
                            modifier = Modifier.padding(start = 14.dp),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = colorGray90
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = startTime == "AllDay", onCheckedChange = { isChecked ->

                                if (isChecked) {
                                    startTimeExpandState = false
                                    startTime = "AllDay"

                                } else startTime = ""

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

                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth().background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium
                        ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                "시작 시간",
                                modifier = Modifier.padding(start = 14.dp),
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = colorGray90
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = startTime ?: "",
                                modifier = Modifier.background(
                                    colorGray10,
                                    RoundedCornerShape(6.dp)
                                )
                                    .padding(horizontal = 12.dp, vertical = 5.dp),
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = colorGray90
                            )
                            IconButton(onClick = { startTimeExpandState = !startTimeExpandState }) {
                                Icon(
                                    painter = painterResource(Res.drawable.plan_expand_icon),
                                    contentDescription = ""
                                )
                            }

                        }

                        if (startTimeExpandState) {

                            val now =
                                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            val hour = now.hour
                            val min = now.minute

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth().height(1.dp)
                                    .padding(start = 14.dp, end = 14.dp), color = Color.LightGray
                            )

//                        Spacer(modifier = Modifier.height(14.dp))

                            CustomTimePicker { h, m, isAm ->

                                startTime = timePickerToString(if (isAm) h else h + 12, m)

                            }
                        }
                    }
                }

                if (plan != null) item {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {

                            if (plan.repeatRule != null) isShowPlanDeleteDialog = true
                            else {

                                onDelete(plan)
                                onDismiss()
                            }
                        }, modifier = Modifier.fillMaxWidth()
                            .background(colorGray10, shape = RoundedCornerShape(14.dp))
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.todo_delete_icon),
                            contentDescription = "",
                            tint = warningColor,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "삭제",
                            color = Color.Black,
                            modifier = Modifier.weight(1f),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp
                        )
                    }


                }
            }


        }
    }


}