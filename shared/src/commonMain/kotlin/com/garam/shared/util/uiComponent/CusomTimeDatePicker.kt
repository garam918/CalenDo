package com.garam.shared.util.uiComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray90
import com.garam.shared.util.resources.fontFamily
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlin.math.abs
import kotlin.time.ExperimentalTime

@Composable
fun CustomTimePicker(
    initialHour: Int = 12,
    initialMinute: Int = 0,
    onTimeSelected: (hour: Int, minute: Int, isAm: Boolean) -> Unit
) {
    val hours = (1..12).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }
    val amPm = listOf("오전", "오후")


    val hourState = rememberLazyListState(initialHour)
    val minuteState = rememberLazyListState(initialMinute)
    val amPmState = rememberLazyListState(initialFirstVisibleItemIndex = 0)


    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var selectedAmPm by remember { mutableStateOf(0) } // 0 = 오전, 1 = 오후

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentWidth()
            .padding(vertical = 16.dp)
    ) {

        WheelPicker(
            items = amPm,
            state = amPmState,
            visibleCount = 3,
            onItemSelected = {
                selectedAmPm = it
                onTimeSelected(selectedHour, selectedMinute, selectedAmPm == 0)
            },
            modifier = Modifier.weight(0.8f)
        )

        WheelPicker(
            items = hours,
            state = hourState,
            visibleCount = 5,
            modifier = Modifier.weight(1f)
        ) { index ->
            selectedHour = index + 1
            onTimeSelected(selectedHour, selectedMinute, selectedAmPm == 0)
        }

        WheelPicker(
            items = minutes,
            state = minuteState,
            visibleCount = 5,
            modifier = Modifier.weight(1f)
        ) { index ->
            selectedMinute = index
            onTimeSelected(selectedHour, selectedMinute, selectedAmPm == 0)
        }
    }
}

// ===========================
// 📅 DatePicker
// ===========================
@OptIn(ExperimentalTime::class)
@Composable
fun CustomDatePicker(
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    val coroutine = rememberCoroutineScope()

    val startYear = initialDate.year
    val endYear = initialDate.year + 10
    val years = (startYear..endYear).toList()
    val months = (1..12).toList()
    fun daysInMonth(year: Int, month: Int): List<Int> {
        val yearMonth = Month(month)
        val days = when (yearMonth) {
            Month.FEBRUARY ->
                if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28

            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
        return (1..days).toList()
    }

    val yearState = rememberLazyListState(years.indexOf(initialDate.year))
    val monthState = rememberLazyListState(initialDate.month.number - 1)
    val dayState = rememberLazyListState(initialDate.day - 1)

    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.month.number) }
    var selectedDay by remember { mutableStateOf(initialDate.day) }

    var days by remember { mutableStateOf(daysInMonth(selectedYear, selectedMonth)) }

    fun updateDateState(newYear: Int, newMonth: Int) {
        selectedYear = newYear
        selectedMonth = newMonth

        // 1. 새로운 월/연도에 맞는 일자 목록(days)을 계산합니다.
        val newDays = daysInMonth(selectedYear, selectedMonth)
        days = newDays

        // 2. 선택된 일자(selectedDay)를 새로운 월의 최대 일수를 넘지 않도록 보정합니다.
        // 예를 들어 31일이 30일로 줄면 selectedDay는 30이 됩니다.
        val correctedDay = selectedDay.coerceAtMost(newDays.last())

        // 3. 만약 선택된 일자가 변경되었다면, Day Picker를 새로운 날짜로 스크롤합니다.
        if (selectedDay != correctedDay) {
            selectedDay = correctedDay
            val newDayIndex = newDays.indexOf(correctedDay)
            // 💡 LazyListState를 사용하여 스크롤 위치를 즉시 보정합니다.
            // 이 보정으로 인해 IndexOutOfBounds 에러가 발생하지 않습니다.
            coroutine.launch {
                dayState.animateScrollToItem(newDayIndex)
            }
        }

        // 4. 최종 선택된 날짜를 콜백으로 전달합니다.
        onDateSelected(LocalDate(selectedYear, selectedMonth, selectedDay))
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        WheelPicker(
            items = years.map { it.toString() },
            state = yearState,
            visibleCount = 5,
            modifier = Modifier.weight(1f)
        ) { index ->
//            selectedYear = years[index]
//            days = daysInMonth(selectedYear, selectedMonth)
//            selectedDay = selectedDay.coerceAtMost(days.last())
//            onDateSelected(LocalDate(selectedYear, selectedMonth, selectedDay))

//            coroutine.launch {
                updateDateState(years[index], selectedMonth)
//            }
        }

        WheelPicker(
            items = months.map { it.toString() + "월" },
            state = monthState,
            visibleCount = 5,
            modifier = Modifier.weight(1f)
        ) { index ->

//            selectedMonth = months[index]
//            days = daysInMonth(selectedYear, selectedMonth)
//            selectedDay = selectedDay.coerceAtMost(days.last())
//            onDateSelected(LocalDate(selectedYear, selectedMonth, selectedDay))

//            coroutine.launch {
                updateDateState(selectedYear, months[index])
//            }
        }

        WheelPicker(
            items = days.map { it.toString() + "일" },
            state = dayState,
            visibleCount = 5,
            modifier = Modifier.weight(1f),
        ) { index ->
//            selectedDay = days[index]
//            onDateSelected(LocalDate(selectedYear, selectedMonth, selectedDay))

            runCatching {
                selectedDay = days[index]
            }.onFailure {
                if(index != 0) selectedDay = days[days.lastIndex]
            }

//            onDateSelected(LocalDate(selectedYear, selectedMonth, selectedDay))
            updateDateState(selectedYear, selectedMonth)
        }
    }

}

// ===========================
// 🌀 공통 휠 컴포넌트
// ===========================
@Composable
fun WheelPicker(
    items: List<String>,
    state: LazyListState,
    visibleCount: Int = 5,
    modifier : Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 18.sp,
        fontFamily = fontFamily(), fontWeight = FontWeight.Normal),
    onItemSelected: (Int) -> Unit

) {
    val coroutine = rememberCoroutineScope()
    val midIndex = visibleCount / 2
    val itemHeightDp = 38

    var isInitialized by remember { mutableStateOf(false) }

    val totalHeight = (visibleCount * itemHeightDp).dp

    val totalItemHeight = itemHeightDp.dp // 아이템 한 개의 높이

    val padding = itemHeightDp * (visibleCount / 2)

    // 중앙에 가장 가까운 실제 인덱스를 계산
    val selectedIndex by remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val centerY = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                val itemCenter = (item.offset + item.size) / 2
                abs(itemCenter - centerY)
            }?.index ?: 0
        }
    }
    LaunchedEffect(Unit) {
        // 잠시 대기하여 레이아웃이 잡히도록 함
        yield()
        isInitialized = true
    }

    // 중앙에 위치한 아이템 선택 콜백
    LaunchedEffect(selectedIndex) {
        if (selectedIndex in items.indices) {
            onItemSelected(selectedIndex)
        }
    }

    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress && isInitialized) {
            val targetIndex = selectedIndex
            coroutine.launch {
                state.animateScrollToItem(targetIndex)
            }
            onItemSelected(targetIndex)
        }
    }

    Box(modifier = modifier.height(totalHeight)) {

        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeightDp.dp)
                .background(
                    colorGray10
                )
        )

        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                top = (padding + 8).dp, bottom = padding.dp
            ),
            verticalArrangement = Arrangement.Top
        ) {
            items(items.size) { index ->
                val isSelected = index == selectedIndex

                Text(
                    text = items[index],
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,

                    color = if (isSelected) colorGray90 else Color.Gray,
                    modifier = Modifier
                        .height(totalItemHeight)
                        .fillMaxWidth()
                        .clickable {
                            coroutine.launch {

                                val target = (index - midIndex).coerceIn(0, items.size - 1)
                                state.animateScrollToItem(target)
                            }
                        },
                    textAlign = TextAlign.Center,
                    style = textStyle
                )
            }
        }
    }
}