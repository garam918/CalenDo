package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.garam.shared.data.Todo
import com.garam.shared.util.functions.addUntilToRRule
import com.garam.shared.util.functions.dayOfWeekKorToICalDay
import com.garam.shared.util.functions.generateRepeatRule
import com.garam.shared.util.functions.localDateToDateString
import com.garam.shared.util.functions.localDateToString
import com.garam.shared.util.functions.parseRRule
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray30
import com.garam.shared.util.resources.colorGray80
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.shared.util.uiComponent.CustomDatePicker
import com.garam.todolist.Res
import com.garam.todolist.bottom_sheet_close_icon
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoRepeatSettingDialog(
    openBottomSheet: Boolean,
    todo: Todo?,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit
) {

    val sheetState = rememberModalBottomSheetState()

    var isShowBottomSheet by remember { mutableStateOf(openBottomSheet) }
    val savedRepeatRule = todo?.repeatRule?.let {

        parseRRule(it)
    }

    val savedFreq = savedRepeatRule?.get("FREQ")


    var selectedRepeatType by remember { mutableStateOf(savedFreq ?: "DAILY") }
    var repeatRule by remember { mutableStateOf(todo!!.repeatRule) }

    var isRepeatEndDateShow by remember {
        mutableStateOf(
            savedRepeatRule?.get("UNTIL")?.isNotBlank() == true
        )
    }

    var settingRepeatEndDate by remember {
        mutableStateOf(
            if (savedRepeatRule?.get("UNTIL") == null) todo!!.endDate
            else savedRepeatRule["UNTIL"]
        )
    }

    var repeatEndDate by remember {
        mutableStateOf(
            settingRepeatEndDate
        )
    }

    println("savedRepeatRule ${savedRepeatRule?.get("UNTIL")}")
    println("todo.endDate ${todo!!.endDate}")

    val weekDayList = listOf("월", "화", "수", "목", "금", "토", "일")
    val selectedDayList =
        if (savedRepeatRule != null && savedFreq == "WEEKLY" && savedRepeatRule.containsKey("BYDAY")) savedRepeatRule["BYDAY"]!!.split(
            ","
        ).toMutableList()
        else mutableListOf()

    val monthlyRptSelectedDates =
        if (savedRepeatRule != null && savedFreq == "MONTHLY" && savedRepeatRule.containsKey("BYMONTHDAY")) savedRepeatRule["BYMONTHDAY"]!!.split(
            ","
        ).map { it.toInt() }.toMutableList()
        else mutableListOf()


    val daysArray = (1..32).map { CalendarDay(dayValue = it) }


    if (openBottomSheet) ModalBottomSheet(
        onDismissRequest = {
            isShowBottomSheet = false
            selectedRepeatType = "DAILY"
            repeatRule = ""
            onDismiss()
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(28.dp),
        containerColor = mainBackgroundColor,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 36.dp, top = 12.dp),
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {

        Column {

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {
                    onDismiss()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.bottom_sheet_close_icon),
                        contentDescription = null
                    )
                }

                Text(
                    text = "반복 설정", color = colorGray100, fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily(),
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = {
                        onSave(
                            todo!!.copy(
//                                endDate = if (isRepeatEndDateShow) repeatEndDate else todo.endDate,
                                repeatRule = if (isRepeatEndDateShow) addUntilToRRule(
                                    when (selectedRepeatType) {
                                        "DAILY" -> generateRepeatRule("DAILY")
                                        "WEEKLY" -> generateRepeatRule(
                                            "WEEKLY",
                                            daysOfWeek = selectedDayList
                                        )

                                        "MONTHLY" -> generateRepeatRule(
                                            "MONTHLY",
                                            monthDays = monthlyRptSelectedDates
                                        )

                                        else -> "null"
                                    }, repeatEndDate.toString()
                                )
                                else when (selectedRepeatType) {
                                    "DAILY" -> generateRepeatRule("DAILY")
                                    "WEEKLY" -> generateRepeatRule(
                                        "WEEKLY",
                                        daysOfWeek = selectedDayList
                                    )

                                    "MONTHLY" -> generateRepeatRule(
                                        "MONTHLY",
                                        monthDays = monthlyRptSelectedDates
                                    )

                                    else -> null
                                }
                            )
                        )
                        onDismiss()
                    }
                ) {
                    Text(
                        "저장", color = colorGray100,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(color = colorGray10, shape = RoundedCornerShape(14.dp)),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextButton(
                    onClick = {
                        selectedRepeatType = "DAILY"
                        repeatRule = generateRepeatRule("DAILY")
                    },
                    modifier = Modifier.weight(1f)
                        .background(
                            color = if (selectedRepeatType == "DAILY") Color.White else colorGray10,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Text(
                        "매일", color = colorGray100,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }

                TextButton(
                    onClick = {
                        selectedRepeatType = "WEEKLY"
                        repeatRule = generateRepeatRule("WEEKLY")
                    },
                    modifier = Modifier.weight(1f)
                        .background(
                            color = if (selectedRepeatType == "WEEKLY") Color.White else colorGray10,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Text(
                        "주간", color = colorGray100,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }

                TextButton(
                    onClick = {
                        selectedRepeatType = "MONTHLY"
                        repeatRule = generateRepeatRule("MONTHLY")
                    },
                    modifier = Modifier.weight(1f)
                        .background(
                            color = if (selectedRepeatType == "MONTHLY") Color.White else colorGray10,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Text(
                        "월간", color = colorGray100,
                        fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (selectedRepeatType == "WEEKLY") LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                contentPadding = PaddingValues(5.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                items(weekDayList) {

                    val item = WeekDayItem(it, selectedDayList.contains(dayOfWeekKorToICalDay(it)))
                    CircleWeekDayItem(item, onClick = {
//                        item.isSelected = !item.isSelected

                        if (selectedDayList.contains(dayOfWeekKorToICalDay(it))) selectedDayList.remove(
                            dayOfWeekKorToICalDay(it)
                        )
                        else selectedDayList.add(dayOfWeekKorToICalDay(it))

                    })
                }
            }

            if (selectedRepeatType == "MONTHLY") LazyVerticalGrid(
                columns = GridCells.Fixed(7), // 한 줄에 6개
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 24.dp),
                contentPadding = PaddingValues(5.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(daysArray.size, span = { index ->
                    // 마지막 아이템일 경우, 2개의 칸을 차지하도록 설정 (이미지상 '마지막 날'이 2칸 정도 차지)
                    if (index == daysArray.lastIndex) {
                        GridItemSpan(2)
                    } else {
                        GridItemSpan(1)
                    }
                }) { index ->
                    val day = daysArray[index]

                    day.isSelected = monthlyRptSelectedDates.contains(day.dayValue)

                    if (day.dayValue != 32) {
                        // 일반 날짜 버튼: 정원 형태 유지
                        CircleDayItem(day = day, onClick = {
                            day.isSelected = !day.isSelected
                            if (!monthlyRptSelectedDates.contains(day.dayValue)) monthlyRptSelectedDates.add(
                                day.dayValue
                            )
                            else monthlyRptSelectedDates.remove(day.dayValue)
                        })
                    } else {
                        // '마지막 날' 버튼: 가로로 긴 형태
                        LongDayItem(day = day, onClick = {
                            day.isSelected = !day.isSelected
                            if (!monthlyRptSelectedDates.contains(day.dayValue)) monthlyRptSelectedDates.add(
                                day.dayValue
                            )
                            else monthlyRptSelectedDates.remove(day.dayValue)
                        })
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.height(1.dp).padding(horizontal = 24.dp, vertical = 15.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column {

                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "종료 날짜 설정",
                        modifier = Modifier.weight(1f),
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Normal,
                        color = colorGray100,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isRepeatEndDateShow) localDateToDateString(
                            LocalDate.parse(
                                repeatEndDate.toString()
                            )
                        ) else "",
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Medium,
                        color = colorGray100,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isRepeatEndDateShow, onCheckedChange = { isChecked ->
                            if (isChecked) {
                                isRepeatEndDateShow = true
                            } else {
                                isRepeatEndDateShow = false
                                repeatEndDate = todo!!.endDate
                            }
                        },
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

//                Spacer(modifier = Modifier.height(18.dp))


                if (isRepeatEndDateShow) CustomDatePicker(LocalDate.parse(settingRepeatEndDate.toString())) { date ->

                    repeatEndDate = date.toString()
                    println("settingRepeatEndDate : $settingRepeatEndDate")
                    println("selectedDate : $repeatEndDate")


                }

            }


        }

    }

}

@Composable
fun CircleWeekDayItem(weekDay: WeekDayItem, onClick: () -> Unit) {

    var isSelected by remember { mutableStateOf(weekDay.isSelected) }
    val borderColor = if (isSelected) Color.Black else colorGray30

    Surface(
        // key point: 1f로 정해진 비율을 강제하여 정원 유지
        modifier = Modifier.aspectRatio(1f).clickable(onClick = {
            isSelected = !isSelected
            onClick()
        }
        ),
        shape = CircleShape,
        color = if (isSelected) Color.Black else mainBackgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = weekDay.weekDay,
                color = if (isSelected) Color.White else colorGray80,
                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                fontSize = 14.sp
//                style = MaterialTheme.typography.bodyMedium // 텍스트 스타일 지정
            )
        }
    }

}

@Composable
fun CircleDayItem(day: CalendarDay, onClick: () -> Unit) {
    var isSelected by remember { mutableStateOf(day.isSelected) }
    val borderColor = if (isSelected) Color.Black else Color(0xFFCCCCCC) // ColorGray30

    Surface(
        // key point: 1f로 정해진 비율을 강제하여 정원 유지
        modifier = Modifier.aspectRatio(1f).clickable(onClick = {
            isSelected = !isSelected
            onClick()
        }),
        shape = CircleShape,
        color = if (isSelected) Color.Black else mainBackgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.dayValue.toString(),
                color = if (isSelected) Color.White else colorGray80,
                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun LongDayItem(day: CalendarDay, onClick: () -> Unit) {
    var isSelected by remember { mutableStateOf(day.isSelected) }
    val borderColor = if (isSelected) Color.Black else Color(0xFFCCCCCC)

    Surface(
        // key point: wrapContentWidth로 텍스트 길이에 맞게 너비 조절
        modifier = Modifier.wrapContentWidth().height(32.dp).clickable(onClick = {
            isSelected = !isSelected
            onClick()
        }),
        shape = RoundedCornerShape(24.dp), // 원형과 비슷한 캡슐 모양
        color = if (isSelected) Color.Black else mainBackgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                text = "마지막 날",
                color = if (isSelected) Color.White else colorGray80,
                fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

data class CalendarDay(
    val dayValue: Int, // 1~31 또는 '마지막 날'을 나타내는 특수 값(32)
    var isSelected: Boolean = false
)

data class WeekDayItem(
    val weekDay: String,
    var isSelected: Boolean = false
)