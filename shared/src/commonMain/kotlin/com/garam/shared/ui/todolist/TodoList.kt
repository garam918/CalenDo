@file:OptIn(ExperimentalUuidApi::class)

package com.garam.shared.ui.todolist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garam.shared.data.AppPreferences
import com.garam.shared.data.Category
import com.garam.shared.data.CurrentTodoMode
import com.garam.shared.data.Goal
import com.garam.shared.data.GoalType
import com.garam.shared.data.Todo
import com.garam.shared.data.TodoStatus
import com.garam.shared.notification.PermissionStatus
import com.garam.shared.notification.createNotificationScheduler
import com.garam.shared.notification.rememberPermissionHandler
import com.garam.shared.ui.bottomSheet.CategoryAddDialog
import com.garam.shared.ui.bottomSheet.PlanEditDialog
import com.garam.shared.ui.bottomSheet.TodoEditDialog
import com.garam.shared.ui.dialog.NotificationDialog
import com.garam.shared.ui.snackbar.SnackbarScreen
import com.garam.shared.util.functions.filterTodosByDate
import com.garam.shared.util.functions.getMonthStartAndEnd
import com.garam.shared.util.functions.getWeekStartEnd
import com.garam.shared.util.functions.getWeeksInMonth
import com.garam.shared.util.functions.isDateInCurrentWeek
import com.garam.shared.util.functions.localDateToDateString
import com.garam.shared.util.functions.monthToString
import com.garam.shared.util.functions.stringToColor
import com.garam.shared.util.functions.stringToCategoryIconResource
import com.garam.shared.util.functions.toKorICalDay
import com.garam.shared.util.modifier.singleClickable
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray20
import com.garam.shared.util.resources.colorGray40
import com.garam.shared.util.resources.colorGray60
import com.garam.shared.util.resources.colorGray70
import com.garam.shared.util.resources.colorGray90
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.shared.util.uiComponent.CircularCheckbox
import com.garam.todolist.Res
import com.garam.todolist.*
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.onDay
import kotlinx.datetime.yearMonth
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TodoList(navController: NavController, viewModel: TodoViewModel = koinViewModel()) {

    val isShowNotificationDialog = AppPreferences.getBoolean("NotificationDialog", false)


    var startScreenMode by remember {
        mutableStateOf(
            AppPreferences.getString(
                "start_screen_mode",
                "CalenDo"
            )
        )
    }
    var sortMode by remember { mutableStateOf(AppPreferences.getString("sort_mode", "Saved")) }
    var firstDayOfWeek by remember {
        mutableStateOf(
            AppPreferences.getString(
                "first_day_of_week",
                "Mon"
            )
        )
    }

    var collapsed by remember { mutableStateOf(false) }

    val isWeekCalendarMode = remember { mutableStateOf(true) }

    var goalExpandState by remember { mutableStateOf(false) }

    var currentTodoMode by remember {
        mutableStateOf(
            when (startScreenMode) {
                "CalenDo" -> CurrentTodoMode.DO_IT
                "Todo" -> CurrentTodoMode.TODO
                "Plan" -> CurrentTodoMode.PLAN
                else -> CurrentTodoMode.DO_IT
            }
        )
    }

    var currentSelectedCategory by remember { mutableStateOf<Category?>(null) }

    var notificationDialog by remember { mutableStateOf(true) }

    var categoryAddBottomSheet by remember { mutableStateOf(false) }
    var todoEditBottomSheet by remember { mutableStateOf(false) }
    var planEditBottomSheet by remember { mutableStateOf(false) }

    val snackbarDuration = 1000L

    var isNotificationGranted by remember { mutableStateOf(false) }

    // 1. 권한 핸들러 생성
    val permissionHandler = rememberPermissionHandler { isGranted ->
        isNotificationGranted = isGranted == PermissionStatus.GRANTED
        notificationDialog = false
        AppPreferences.setBoolean("NotificationDialog", true)
        AppPreferences.setString("todo_noti_time", "오전 09:00")
        AppPreferences.setString("plan_noti_time", "오전 09:00")

        viewModel.setTodoPlanNoti()

    }

//    var permissionStatus = false
//
//    LaunchedEffect(Unit) {
//        permissionStatus  = permissionHandler.checkPermissionStatus()
//
//    }


    var selectedTodo by remember { mutableStateOf<Todo?>(null) }

    var selectedPlan by remember { mutableStateOf<Todo?>(null) }

    var isSelectedTodoInGoal by remember { mutableStateOf(false) }

    val selectedDate by viewModel.selectedDate.collectAsState()

    val categoryList by viewModel.categories.collectAsState()
    val todoListFlow by viewModel.todoList.collectAsState()
    val planList by viewModel.planList.collectAsState()

    val currentGoal by viewModel.currentGoal.collectAsState()
    val todoListInGoal by viewModel.todoListInGoal.collectAsState()

    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    var currentGoalTitleText by rememberSaveable { mutableStateOf(currentGoal?.title ?: "") }

    LaunchedEffect(currentGoal?.goalId) {
        // 새로운 goal이 선택될 때만 초기화
        println("currentGoal?.title ${currentGoal?.title}")

        currentGoalTitleText = currentGoal?.title ?: ""
    }

//    val monthlyMaxHeight = 460.dp
//    val weeklyMaxHeight = 110.dp
//
//    val maxCalendarHeight = if (isWeekCalendarMode.value) weeklyMaxHeight else monthlyMaxHeight
//    var targetCalendarHeight by remember { mutableStateOf(maxCalendarHeight) }


    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startDate = remember { currentMonth.minusMonths(100).firstDay } // Adjust as needed
    val endDate = remember { currentMonth.plusMonths(100).lastDay } // Adjust as needed
//    val firstDayOfWeek = remember { firstDayOfWeekFromLocale(locale = Locale.current) } // Available from the library

    val daysOfWeek =
        daysOfWeek(firstDayOfWeek = if (firstDayOfWeek == "Sun") DayOfWeek.SUNDAY else DayOfWeek.MONDAY)

//    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
//    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

    val monthCalendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val weekCalendarState = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = daysOfWeek.first()
    )

    LaunchedEffect(daysOfWeek) {
        monthCalendarState.firstDayOfWeek = daysOfWeek.first()
        weekCalendarState.firstDayOfWeek = daysOfWeek.first()
    }

    val goalDatePair =
        remember { mutableStateOf<Pair<String, String>?>(getWeekStartEnd(selectedDate.toString())) }

    val weekCalendarCoroutineScope = rememberCoroutineScope()
    val monthCalendarCoroutineScope = rememberCoroutineScope()

// 모드 바뀌면 목표 높이 재설정


    val snackbarScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (!isShowNotificationDialog) NotificationDialog(
        show = notificationDialog,
        onDismiss = {
            notificationDialog = false
            AppPreferences.setBoolean("NotificationDialog", true)
        },
        onConfirm = {
            permissionHandler.askPermission()
        }

    )

    if (categoryAddBottomSheet) CategoryAddDialog(
        categoryLastIndex = categoryList.lastIndex,
        openBottomSheet = categoryAddBottomSheet,
        selectedCategory = null,
        onDismiss = {

            categoryAddBottomSheet = false
        },
        onDelete = { },
        onSave = { category -> viewModel.insertCategory(category) })

    TodoEditDialog(
        type = if (isSelectedTodoInGoal) "Goal" else "Todo",
        todo = selectedTodo,
        selectedDate = selectedDate.toString(),
        openBottomSheet = todoEditBottomSheet,
        onDismiss = { type, id ->
            todoEditBottomSheet = false

            if (type == "Memo" || type == "Repeat") {
            } else if (type != "" && type != "Delete" && type != "CategoryChange") {

                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarScope.launch {

                    val result = snackbarHostState.showSnackbar(
                        message = when (type) {
                            "Priority" -> "${selectedTodo?.title}이(가) ${if (selectedTodo?.priority == false) "우선순위에 등록되었습니다." else "우선순위에서 해제되었습니다."}"
                            "TodayAdd" -> "${selectedTodo?.title}이(가) 오늘 할일에 추가되었습니다."
                            "TodayChange" -> "${selectedTodo?.title}이(가) 오늘 할일로 변경되었습니다."
                            "TomorrowAdd" -> "${selectedTodo?.title}이(가) 내일 할일에 추가되었습니다."
                            "TomorrowChange" -> "${selectedTodo?.title}이(가) 내일 할일로 변경되었습니다."
                            else -> ""

                        },
                        actionLabel = "되돌리기",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        when (type) {
                            "Priority" -> {
                                val editTodo =
                                    if (selectedTodo?.priority == false) selectedTodo?.copy(
                                        priority = false
                                    )!! else selectedTodo?.copy(priority = true)!!

                                viewModel.upsertTodo(editTodo)

                                if (isSelectedTodoInGoal) viewModel.updateTodoInGoal(editTodo)

                            }

                            "TodayAdd" -> {
                                // 새로 추가된 일정이 지워져야됨
                                viewModel.deleteTodo(id)
                            }

                            "TodayChange" -> {
                                // 시작일과 종료일이 원래대로 돌아가야됨
                                viewModel.upsertTodo(selectedTodo!!)
                            }

                            "TomorrowAdd" -> {
                                // 새로 추가된 일정이 지워져야됨
                                viewModel.deleteTodo(id)
                            }

                            "TomorrowChange" -> {
                                // 시작일과 종료일이 원래대로 돌아가야됨
                                viewModel.upsertTodo(selectedTodo!!)
                            }

                        }
                    }
                }
            } else if (type != "CategoryChange") selectedTodo = null
        },
        onDelete = { todo ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarScope.launch {

                viewModel.deleteTodo(todo.id)
                if (isSelectedTodoInGoal) viewModel.deleteTodoInGoal(todo.id)

                val result = snackbarHostState.showSnackbar(
                    "${todo.title}이(가) 삭제되었습니다.",
                    actionLabel = "되돌리기", duration = SnackbarDuration.Short
                )

                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.upsertTodo(todo)
                    if (currentGoal?.goalId == todo.categoryId) viewModel.addTodo(todo)
                }
            }
        },
        categoryList = categoryList,
        upsertTodo = { type, todo ->
            println("todo list ${todo.memo}")
            if (type != "CategoryChange") {
                viewModel.upsertTodo(todo)

                if (isSelectedTodoInGoal) viewModel.updateTodoInGoal(todo)
            } else {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarScope.launch {

                    viewModel.upsertTodo(todo)
                    val result = snackbarHostState.showSnackbar(
                        "${todo.title}이(가) 변경되었습니다.",
                        actionLabel = "되돌리기", duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.upsertTodo(todo.copy(categoryId = selectedTodo?.categoryId))
                    }
                }


            }
        }
    )

    if (planEditBottomSheet) PlanEditDialog(
        type = "Add",
        plan = selectedPlan,
        openBottomSheet = planEditBottomSheet,
        onDismiss = {
            planEditBottomSheet = false
            selectedPlan = null
        },
        onSave = { plan ->
            viewModel.upsertTodo(plan)
//            scheduler.scheduleNotification(plan.id, plan.title, "", Clock.System.now().toEpochMilliseconds() + 60000)
        },
        onDelete = { plan ->
            viewModel.deleteTodo(plan?.id.toString())
//            scheduler.cancelNotification(plan?.id.toString())
        },
        selectedDate = selectedDate
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {



        val screenWidth = maxWidth

        val weeklyMaxHeight = remember(screenWidth) {
            val cellWidth = screenWidth / 7
            val cellHeight = cellWidth * (3f / 2f)
            cellHeight + 20.dp // 1주 + 헤더 여백
        }
        val startFromSunday = firstDayOfWeek == "Sun"

        val weeksCount = remember(selectedDate, startFromSunday) {
            getWeeksInMonth(selectedDate, startFromSunday)
        }

        // 2. 셀 높이 및 전체 높이 계산
        val cellHeight = remember(screenWidth) { (screenWidth / 7) * (3f / 2f) }

        val monthlyMaxHeight = remember(cellHeight, weeksCount) {
            (cellHeight * weeksCount) + 20.dp// 헤더 포함
        }

        var maxCalendarHeight = if (isWeekCalendarMode.value) weeklyMaxHeight else monthlyMaxHeight
        var targetCalendarHeight by remember(maxCalendarHeight) { mutableStateOf(maxCalendarHeight) }

//        val isCollapsed by remember {
//            derivedStateOf { targetCalendarHeight < maxCalendarHeight * 0.3f } // 30% 이하일 때 접힘으로 판단
//        }

        LaunchedEffect(isWeekCalendarMode.value) {
            maxCalendarHeight = if (isWeekCalendarMode.value) weeklyMaxHeight else monthlyMaxHeight
            targetCalendarHeight = maxCalendarHeight

            collapsed = false

        }



        // 스크롤 연결 → 달력 먼저 사라지고, 그 뒤 리스트 스크롤
        val connection = remember(maxCalendarHeight) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y

//                if(!isWeekCalendarMode.value) {
                    if (delta < 0 && !collapsed) {
                        // 조금이라도 위로 스크롤 → 바로 접기
                        targetCalendarHeight = 0.dp
                        collapsed = true
                        return Offset(0f, delta)
                    }

                    if (delta > 0 && collapsed) {
                        // 완전히 접힌 상태에서 아래로 → 바로 펼치기
                        targetCalendarHeight = maxCalendarHeight
                        collapsed = false
                        return Offset(0f, delta)
                    }

                    return Offset.Zero
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (!isWeekCalendarMode.value) {
                        // 월간 모드에서는 postScroll 무시 (중간 상태 방지)
                        return Offset.Zero
                    }

                    val delta = available.y
                    if (delta > 0 && targetCalendarHeight.value < maxCalendarHeight.value) {
                        // 아래로 스크롤 시 → 달력 다시 펼치기
                        val newHeight = (targetCalendarHeight.value + delta / 3)
                            .coerceIn(0f, maxCalendarHeight.value)
                        val consumedY = newHeight - targetCalendarHeight.value
                        targetCalendarHeight = newHeight.dp
                        collapsed = false
                        return Offset(0f, consumedY)
                    }
                    return Offset.Zero
                }
            }
        }

        val density = LocalDensity.current
        val imeHeight = WindowInsets.ime.getBottom(density)

        val dummyScrollState = rememberScrollState()


        val animatedHeight by animateDpAsState(
            targetValue = targetCalendarHeight,
            animationSpec = tween(durationMillis = 300)
        )

        LaunchedEffect(imeHeight) {
            if (!isWeekCalendarMode.value && imeHeight > 0) {
                // 키보드가 조금이라도 올라오기 시작하면 달력을 접습니다.
                targetCalendarHeight = 0.dp
                collapsed = true
            }
        }

        var text by remember { mutableStateOf(monthToString(selectedDate.yearMonth)) }

        LaunchedEffect(collapsed) {
            text = if (!collapsed) monthToString(selectedDate.yearMonth)
            else localDateToDateString(selectedDate)

            println("dateString $text")
        }

        Scaffold(
            snackbarHost = {
                Box(
                    modifier = Modifier
                        .fillMaxSize() // Box가 Scaffold 영역 전체를 채우도록 합니다.
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                    )
                }
            },
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (isFocused) {
                        focusManager.clearFocus(force = true)
                    }
                })
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .nestedScroll(connection)
                    .background(color = mainBackgroundColor)
                    .windowInsetsPadding(WindowInsets.safeDrawing)


            ) {
                /** 고정 헤더 **/
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 10.dp, top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 뚜잇 / 할일 / 일정 탭
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { currentTodoMode = CurrentTodoMode.DO_IT }) {
                            Text(
                                "뚜잇",
                                fontSize = 22.sp,
                                color = if (currentTodoMode == CurrentTodoMode.DO_IT) Color.Black else Color.Gray,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily(),
                            )
                        }
                        TextButton(onClick = { currentTodoMode = CurrentTodoMode.TODO }) {
                            Text(
                                "할일",
                                fontSize = 22.sp,
                                color = if (currentTodoMode == CurrentTodoMode.TODO) Color.Black else Color.Gray,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily()
                            )
                        }
                        TextButton(onClick = { currentTodoMode = CurrentTodoMode.PLAN }) {
                            Text(
                                "일정",
                                fontSize = 22.sp,
                                color = if (currentTodoMode == CurrentTodoMode.PLAN) Color.Black else Color.Gray,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = fontFamily()
                            )
                        }
                    }
                    // 설정 버튼

                    IconButton(onClick = { navController.navigate("setting") }) {
                        Icon(
                            painter = painterResource(Res.drawable.todo_setting_icon),
                            contentDescription = "설정"
                        )
                    }
                }

                // 년/월 + 오늘/주 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (!collapsed) monthToString(selectedDate.yearMonth)
                        else localDateToDateString(selectedDate),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily()
                    )
                    Spacer(modifier = Modifier.weight(2f))

                    Text(
                        text = "오늘",
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = colorGray70,
                        modifier = Modifier.width(36.dp)
                            .clickable(true, onClick = {

                                val today = LocalDate.now()
                                viewModel.onDateSelected(today)

                                weekCalendarCoroutineScope.launch {
                                    weekCalendarState.animateScrollToWeek(today)
                                }
                                monthCalendarCoroutineScope.launch {

                                    monthCalendarState.scrollToMonth(today.yearMonth)

                                }
                            })
                            .border(
                                width = 1.dp,
                                color = colorGray40,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .background(color = Color.White, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = fontFamily()
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isWeekCalendarMode.value) "주" else "월", fontSize = 11.sp,
                        color = colorGray90, textAlign = TextAlign.Center,
                        modifier = Modifier.width(36.dp)
                            .singleClickable(500, onClick = {

                                currentGoalTitleText = ""

                                isWeekCalendarMode.value = !isWeekCalendarMode.value

                                keyboardController?.hide()
                                focusManager.clearFocus()

                            })
                            .background(color = colorGray10, shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                /** 접히는 달력 **/
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(animatedHeight)
                        .clipToBounds()
                        .verticalScroll(
                            state = dummyScrollState,
                            enabled = true
                        )
                ) {


                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = !isWeekCalendarMode.value,
                    ) {
                        MonthCalendar(
                            currentTodoMode,
                            selectedDate,
                            onDateSelected = { viewModel.onDateSelected(it) },
                            monthCalendarState,
                            daysOfWeek,
                            onMonthScroll = {
                                val date =
                                    if (it == viewModel.selectedDate.value.yearMonth) selectedDate else it.onDay(
                                        1
                                    )

                                viewModel.onDateSelected(date)

                                goalDatePair.value = getMonthStartAndEnd(date.toString())
                                viewModel.getCurrentGoal(
                                    goalDatePair.value?.first!!,
                                    goalDatePair.value?.second!!,
                                    GoalType.MONTHLY
                                )
                            },
                            todoList = todoListFlow,
                            planList = planList
                        )
                    }

                    // 주간 캘린더
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = isWeekCalendarMode.value,
                    ) {
                        WeekCalendar(
                            currentTodoMode = currentTodoMode,
                            selectedDate = selectedDate,
                            onDateSelected = { viewModel.onDateSelected(it) },
                            weekCalendarState,
                            daysOfWeek,
                            onWeekScroll = {

                                val date = if (isDateInCurrentWeek(
                                        selectedDate = selectedDate,
                                        weekStartDate = it, startDayOfWeek = daysOfWeek.first()
                                        //DayOfWeek.MONDAY
                                    )
                                ) selectedDate
                                else it

                                viewModel.onDateSelected(date)

                                goalDatePair.value = getWeekStartEnd(date.toString())
                                viewModel.getCurrentGoal(
                                    goalDatePair.value?.first!!,
                                    goalDatePair.value?.second!!,
                                    GoalType.WEEKLY
                                )
                            },
                            todoList = todoListFlow,
                            planList = planList
                        )
                    }

                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().height(1.dp),
                    color = Color.LightGray
                )

                /** 스크롤 리스트 **/
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    overscrollEffect = null
                ) {


                    // 카테고리 섹션

                    if (currentTodoMode != CurrentTodoMode.PLAN) item(currentGoal?.goalId) {
                        println("goal recomposition")
                        println("goal title ${currentGoal?.title}")

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = mainColor),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {

                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.goal_week_icon),
                                        contentDescription = "",
                                        tint = if (isWeekCalendarMode.value) Color.White else Color(
                                            0xFF7777F9
                                        )
                                    )

                                    Text(
                                        text = if (isWeekCalendarMode.value) "주" else "월",
                                        color = if (isWeekCalendarMode.value) mainColor else Color.White,
                                        fontSize = 9.sp,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                BasicTextField(
                                    value = currentGoalTitleText,
                                    onValueChange = { newText ->
                                        currentGoalTitleText = newText
                                        // 텍스트가 바뀔 때 즉시 goal 반영
                                        val goal = currentGoal
                                        if (goal != null) {
                                            viewModel.upsertGoal(goal.copy(title = newText))
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 6.dp)
                                        .height(36.dp) // 원하는 높이로 고정
                                        .clip(shape = RoundedCornerShape(14.dp))
                                        .background(color = mainColor)
                                        .onFocusChanged { focusState ->
                                            val nowFocused = focusState.isFocused
                                            if (isFocused && !nowFocused) {
                                                val goal = currentGoal
                                                if (goal == null && currentGoalTitleText.isNotBlank()) {
                                                    viewModel.upsertGoal(
                                                        Goal(
                                                            goalId = Uuid.random().toString(),
                                                            title = currentGoalTitleText,
                                                            startDate = goalDatePair.value!!.first,
                                                            endDate = goalDatePair.value!!.second,
                                                            type = if (isWeekCalendarMode.value) GoalType.WEEKLY else GoalType.MONTHLY
                                                        )
                                                    )
                                                }
                                            }
                                            isFocused = nowFocused
                                        },
                                    // 텍스트 스타일 정의 (기존 폰트 설정 유지)
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(colorGray0), // 커서 색상
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            val goal = currentGoal
                                            if (goal == null && currentGoalTitleText.isNotBlank()) {
                                                viewModel.upsertGoal(
                                                    Goal(
                                                        goalId = Uuid.random().toString(),
                                                        title = currentGoalTitleText,
                                                        startDate = goalDatePair.value!!.first,
                                                        endDate = goalDatePair.value!!.second,
                                                        type = if (isWeekCalendarMode.value) GoalType.WEEKLY else GoalType.MONTHLY
                                                    )
                                                )
                                            }
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        }
                                    ),
                                    // 플레이스홀더 및 수직 중앙 정렬을 위한 데코레이션 박스
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp), // 좌우 내측 여백
                                            contentAlignment = Alignment.CenterStart // 수직 중앙 정렬
                                        ) {
                                            if (currentGoalTitleText.isEmpty()) {
                                                Text(
                                                    text = if (isWeekCalendarMode.value) "이번주 목표를 작성해 보세요!" else "이번달 목표를 작성해 보세요!",
                                                    color = colorGray0.copy(alpha = 0.6f),
                                                    fontFamily = fontFamily(),
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            innerTextField() // 실제 입력 필드
                                        }
                                    }
                                )

                                // 26.01.08 출시 후 수정
//                            if (!goalExpandState) Text(
//                                "${if (todoListInGoal.isEmpty()) 0 else todoListInGoal.size}",
//                                modifier = Modifier.background(
//                                    color = Color.DarkGray,
//                                    shape = CircleShape
//                                )
//                                    .padding(top = 3.dp, bottom = 3.dp, start = 8.dp, end = 8.dp),
//                                color = colorGray40,
//                                fontFamily = fontFamily(),
//                                fontWeight = FontWeight.Medium
//                            )

                                if (goalExpandState) IconButton(onClick = {

                                    if (currentGoal == null) {

                                        val goalId = Uuid.random().toString()

                                        viewModel.upsertGoal(
                                            Goal(
                                                goalId = goalId,
                                                title = currentGoalTitleText,
                                                startDate = goalDatePair.value!!.first,
                                                endDate = goalDatePair.value!!.second,
                                                type = if (isWeekCalendarMode.value)
                                                    GoalType.WEEKLY else GoalType.MONTHLY
                                            )
                                        ).invokeOnCompletion {
                                            val todo = Todo(
                                                id = Uuid.random().toString(),
                                                categoryId = goalId,
                                                title = "",
                                                startDate = goalDatePair.value!!.first,
                                                endDate = goalDatePair.value!!.second,
                                                repeatRule = null,
                                                priority = false,
                                                memo = "",
                                                icon = null,
                                                color = null,
                                                index = null,
                                                startTime = null,
                                                savedTime = Clock.System.now().epochSeconds,
                                                status = mutableMapOf(goalDatePair.value!!.first to TodoStatus.NONE)
                                            )
                                            viewModel.upsertTodo(
                                                todo
                                            ).invokeOnCompletion {

                                                viewModel.addTodo(todo)

                                            }
                                        }

                                    } else {

                                        val todo = Todo(
                                            id = Uuid.random().toString(),
                                            categoryId = currentGoal?.goalId.toString(),
                                            title = "",
                                            startDate = goalDatePair.value!!.first,
                                            endDate = goalDatePair.value!!.second,
                                            repeatRule = null,
                                            priority = false,
                                            memo = "",
                                            icon = null,
                                            color = null,
                                            index = null,
                                            startTime = null,
                                            savedTime = Clock.System.now().epochSeconds,
                                            status = mutableMapOf(goalDatePair.value!!.first to TodoStatus.NONE)
                                        )
                                        viewModel.upsertTodo(
                                            todo
                                        ).invokeOnCompletion {

                                            viewModel.addTodo(todo)

                                        }
                                    }

                                }, modifier = Modifier.size(14.dp)) {
                                    Icon(
                                        painter = painterResource(Res.drawable.week_goal_add_icon),
                                        contentDescription = "",
                                        tint = Color.LightGray
                                    )
                                }

//                            Spacer(modifier = Modifier.width(8.dp))
//
//                            IconButton(
//                                onClick = { goalExpandState = !goalExpandState },
//                                modifier = Modifier.padding(end = 14.dp).size(14.dp)
//                            ) {
//                                Icon(
//                                    painter = painterResource(Res.drawable.week_goal_expand_icon),
//                                    contentDescription = "",
//                                    tint = Color.LightGray
//                                )
//
//                            }
                            }

                            if (!goalExpandState) Spacer(modifier = Modifier.height(8.dp))

                            if (goalExpandState) {
                                if (todoListInGoal.isEmpty()) Text(
                                    text = if (isWeekCalendarMode.value) "이번 주에 완료하고 싶은 일들을 \n미리 적어볼까요?"
                                    else "이번 달에 완료하고 싶은 일들을 \n미리 적어볼까요?",
                                    color = colorGray0.copy(0.4f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                                    fontFamily = fontFamily(),
                                    fontWeight = FontWeight.Normal
                                )
                                else {

                                    todoListInGoal.forEach { todo ->

                                        var todoTitle by remember(todo.id) { mutableStateOf(todo.title) }
                                        var localStatus by remember(todo.id, selectedDate) {
                                            mutableStateOf(
                                                todo.status?.get(selectedDate.toString())
                                                    ?: TodoStatus.NONE
                                            )
                                        }

                                        var todoPriority by remember(todo.priority) {
                                            mutableStateOf(
                                                todo.priority
                                            )
                                        }
                                        var todoMemo by remember(todo.memo) { mutableStateOf(todo.memo) }

                                        Row(
                                            modifier = Modifier
                                                .padding(vertical = 2.dp)
                                                .fillMaxWidth().background(
                                                    color = mainColor,
                                                    shape = RoundedCornerShape(14.dp)
                                                ),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularCheckbox(
                                                type = "Goal",
                                                status = localStatus,
                                                onCheckedChange = { status ->
                                                    localStatus = status
                                                    todo.status?.set(
                                                        selectedDate.toString(),
                                                        status
                                                    )

                                                    viewModel.upsertTodo(
                                                        todo.copy(
                                                            status = todo.status
                                                        )
                                                    )

                                                }
                                            )

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                BasicTextField(
                                                    value = todoTitle,
                                                    onValueChange = { todoTitle = it },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(36.dp) // 높이를 36dp로 명시적 지정
                                                        .onFocusChanged { focusState ->
                                                            val nowFocused = focusState.isFocused
                                                            if (isFocused && !nowFocused) {
                                                                viewModel.upsertTodo(todo.copy(title = todoTitle))
                                                            }
                                                            isFocused = nowFocused
                                                        },
                                                    // 텍스트 스타일 설정 (중요: 수직 중앙 정렬을 위해 TextStyle 조절)
                                                    textStyle = TextStyle(
                                                        color = Color.White,
                                                        fontSize = 14.sp, // 높이에 맞게 폰트 크기 조절
                                                        textAlign = TextAlign.Start
                                                    ),
                                                    singleLine = true,
                                                    cursorBrush = SolidColor(Color.White),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Text
                                                    ),
                                                    keyboardActions = KeyboardActions(
                                                        onDone = {
                                                            viewModel.upsertTodo(todo.copy(title = todoTitle))
                                                            keyboardController?.hide()
                                                            focusManager.clearFocus()
                                                        }
                                                    ),
                                                    // decorationBox를 사용해 힌트(Placeholder)와 수직 정렬을 구현합니다.
                                                    decorationBox = { innerTextField ->
                                                        Box(
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentAlignment = Alignment.CenterStart // 텍스트를 세로 중앙에 배치
                                                        ) {
//                                                        if (todoTitle.isEmpty()) {
//                                                            Text(
//                                                                text = "할 일을 입력하세요", // placeholder 내용
//                                                                color = colorGray0.copy(alpha = 0.4f),
//                                                                fontSize = 14.sp
//                                                            )
//                                                        }
                                                            innerTextField() // 실제 입력되는 텍스트 필드
                                                        }
                                                    }
                                                )

                                                if (todoPriority) Icon(
                                                    painter = painterResource(Res.drawable.todo_item_priority_icon),
                                                    contentDescription = "",
                                                    tint = colorGray0,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                if (todoMemo.isNotBlank()) Spacer(
                                                    modifier = Modifier.width(
                                                        4.dp
                                                    )
                                                )
                                                if (todoMemo.isNotBlank()) Icon(
                                                    painter = painterResource(
                                                        Res.drawable.todo_item_memo_icon
                                                    ),
                                                    tint = colorGray0,
                                                    contentDescription = "",
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }

                                            IconButton(
                                                modifier = Modifier.size(14.dp),
                                                onClick = {
                                                    selectedTodo = todo
                                                    isSelectedTodoInGoal = true
                                                    todoEditBottomSheet = true
                                                }) {
                                                Icon(
                                                    painterResource(Res.drawable.todo_item_more_icon),
                                                    contentDescription = "",
                                                    tint = colorGray0
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(14.dp))


                                        }
                                    }


                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }


                        }
                    }

                    if (currentTodoMode != CurrentTodoMode.PLAN) item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "할일",
                            modifier = Modifier.padding(start = 16.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorGray60,
                            fontFamily = fontFamily()
                        )
                    }

                    if (currentTodoMode != CurrentTodoMode.PLAN) item {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            item {
                                TextButton(
                                    onClick = { currentSelectedCategory = null },
                                    modifier = Modifier.alpha(if (currentSelectedCategory != null) 0.4f else 1f)
                                ) {

                                    Image(
                                        painter = painterResource(Res.drawable.category_all_btn_icon),
                                        contentDescription = ""
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "전체",
                                        fontWeight = FontWeight.Bold,
                                        color = colorGray100,
                                        fontFamily = fontFamily()
                                    )
                                }
                            }

                            items(categoryList) { category ->

                                TextButton(
                                    onClick = { currentSelectedCategory = category },
                                    modifier = Modifier
                                        .alpha(if (currentSelectedCategory != category) 0.4f else 1f)
                                ) {
                                    Icon(
                                        painter = stringToCategoryIconResource(category.icon),
                                        contentDescription = "",
                                        modifier = Modifier.size(24.dp)
                                            .background(
                                                color = stringToColor(category.color).copy(0.3f),
                                                shape = CircleShape
                                            )
                                            .padding(4.dp),
                                        tint = stringToColor(category.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category.title,
                                        color = stringToColor(category.color),
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }

                            }

                            item {
                                TextButton(onClick = { categoryAddBottomSheet = true }
                                ) {
                                    Text(
                                        "+ 추가",
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        color = colorGray40,
                                        fontFamily = fontFamily(),
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.background(
                                            color = colorGray10,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }


                        }
                    }



                    if (currentTodoMode != CurrentTodoMode.PLAN) {
                        if (currentSelectedCategory == null) items(categoryList) { category ->

                            Card(
                                border = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Unspecified
                                ),
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(top = 12.dp, start = 12.dp, end = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        painter = stringToCategoryIconResource(category.icon),
                                        contentDescription = "아이콘",
                                        modifier = Modifier.size(24.dp).background(
                                            color = stringToColor(category.color).copy(0.3f),
                                            shape = CircleShape
                                        ).padding(4.dp),
                                        tint = stringToColor(category.color)
                                    )

                                    Text(
                                        text = category.title,
                                        modifier = Modifier.padding(start = 8.dp).weight(1f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = fontFamily(),
                                        color = stringToColor(category.color)
                                    )

                                    IconButton(
                                        onClick = {
                                            viewModel.upsertTodo(
                                                Todo(
                                                    id = Uuid.random().toString(),
                                                    categoryId = category.categoryId,
                                                    title = "",
                                                    startDate = selectedDate.toString(),
                                                    endDate = selectedDate.toString(),
                                                    repeatRule = null,
                                                    priority = false,
                                                    memo = "",
                                                    icon = null,
                                                    color = category.color,
                                                    startTime = null,
                                                    index = null,
                                                    savedTime = Clock.System.now().epochSeconds,
                                                    status = mutableMapOf(selectedDate.toString() to TodoStatus.NONE)

                                                )
                                            )
//                                        keyboardController?.show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            painterResource(Res.drawable.todo_add_icon),
                                            contentDescription = ""
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))


                                val todoList = todoListFlow[category]


                                todoList?.let { it ->

                                    filterTodosByDate(it, selectedDate).sortedByDescending {
                                        val priorityRank = if (it.priority) 1L else 0L

                                        val sortRank = when (sortMode) {
                                            "Saved" -> {
                                                it.savedTime
                                            }

                                            "Completed_Reversed" -> {
                                                val isCompleted =
                                                    it.status?.values?.contains(TodoStatus.COMPLETED) == true
                                                val completedRank = if (isCompleted) 0 else 1
                                                completedRank * 1_000_000_000L + it.savedTime
                                            }

                                            "Completed" -> {
                                                val isCompleted =
                                                    it.status?.values?.contains(TodoStatus.COMPLETED) == true
                                                val completedRank = if (isCompleted) 1 else 0
                                                completedRank * 1_000_000_000L + it.savedTime
                                            }

                                            else -> {
                                                it.savedTime
                                            }
                                        }

                                        priorityRank * 1_000_000_000_000_000_000 + sortRank
                                    }
                                }?.forEach { schedule ->

                                    var todoTitle by remember(key1 = schedule.id) {
                                        mutableStateOf(
                                            schedule.title
                                        )
                                    }



                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularCheckbox(
                                            type = "Todo",
                                            status = schedule.status?.get(selectedDate.toString())
                                                ?: TodoStatus.NONE,
                                            onCheckedChange = { status ->
                                                viewModel.upsertTodo(
                                                    schedule.copy(
                                                        status =
                                                            mutableMapOf(
                                                                selectedDate.toString() to
                                                                        status
                                                            )
                                                    )
                                                )
                                            }
                                        )

                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextField(
                                                keyboardOptions = KeyboardOptions(
                                                    imeAction = ImeAction.Done,
                                                    keyboardType = KeyboardType.Text
                                                ),

                                                keyboardActions = KeyboardActions(
                                                    onDone = {
                                                        viewModel.upsertTodo(
                                                            schedule.copy(
                                                                title = todoTitle
                                                            )
                                                        )
                                                        keyboardController?.hide()
                                                        focusManager.clearFocus()
                                                    }
                                                ),
                                                value = todoTitle,
                                                onValueChange = { todoTitle = it },
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = Color.White,
                                                    focusedIndicatorColor = Color.White,
                                                    unfocusedIndicatorColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    disabledIndicatorColor = Color.White,
                                                    cursorColor = Color.Black
                                                ),
                                                maxLines = 1,
                                                singleLine = true,
                                                placeholder = {
                                                    Text(
                                                        when (schedule.id) {
                                                            "pre_todo_1" -> "할일을 작성해 주세요"
                                                            "pre_todo_2" -> "한번 터치하면 완료 상태가 됩니다"
                                                            "pre_todo_3" -> "한번 더 터치하면 진행중 상태가 됩니다"
                                                            else -> ""
                                                        },
                                                        fontFamily = fontFamily(),
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        color = colorGray100.copy(alpha = if (schedule.id == "pre_todo_3") 1f else 0.45f),
                                                        maxLines = 1
                                                    )
                                                },
                                                modifier = Modifier.weight(1f)
                                                    .onFocusChanged { focusState ->

                                                        val nowFocused = focusState.isFocused
                                                        if (isFocused && !nowFocused) {
                                                            // focus → unfocus 시 실행
                                                            viewModel.upsertTodo(
                                                                schedule.copy(
                                                                    title = todoTitle
                                                                )
                                                            )
                                                        }
                                                        isFocused = nowFocused

//                                            if (!it.isFocused) viewModel.upsertTodo(
//                                                schedule.copy(
//                                                    title = todoTitle
//                                                )
//                                            )
                                                    },
                                            )

                                            if (schedule.priority) Icon(
                                                painter = painterResource(Res.drawable.todo_item_priority_icon),
                                                contentDescription = "",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            if (schedule.repeatRule != null) Icon(
                                                painter = painterResource(
                                                    Res.drawable.todo_item_repeat_icon
                                                ),
                                                contentDescription = "",
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            if (schedule.memo.isNotBlank()) Icon(
                                                painter = painterResource(
                                                    Res.drawable.todo_item_memo_icon
                                                ),
                                                contentDescription = "",
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        IconButton(onClick = {
                                            selectedTodo = schedule
                                            isSelectedTodoInGoal = false
                                            todoEditBottomSheet = true
                                        }) {
                                            Icon(
                                                painterResource(Res.drawable.todo_item_more_icon),
                                                contentDescription = ""
                                            )
                                        }
                                    }

                                }

                            }
                        }
                        else {
                            val todoList = todoListFlow[currentSelectedCategory]!!
                            val currentCategoryTodoList =
                                filterTodosByDate(todoList, selectedDate).sortedByDescending {
                                    val priorityRank = if (it.priority) 1L else 0L

                                    val sortRank = when (sortMode) {
                                        "Saved" -> {
                                            it.savedTime
                                        }

                                        "Completed_Reversed" -> {
                                            val isCompleted =
                                                it.status?.values?.contains(TodoStatus.COMPLETED) == true
                                            val completedRank = if (isCompleted) 0 else 1
                                            completedRank * 1_000_000_000L + it.savedTime
                                        }

                                        "Completed" -> {
                                            val isCompleted =
                                                it.status?.values?.contains(TodoStatus.COMPLETED) == true
                                            val completedRank = if (isCompleted) 1 else 0
                                            completedRank * 1_000_000_000L + it.savedTime
                                        }

                                        else -> {
                                            it.savedTime
                                        }
                                    }

                                    priorityRank * 1_000_000_000_000_000_000 + sortRank
                                }


                            item {

                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFEEEEEF),
                                        contentColor = Color(0xFFEEEEEF)
                                    )
                                ) {
                                    TextButton(onClick = {
                                        viewModel.upsertTodo(
                                            Todo(
                                                id = Uuid.random().toString(),
                                                categoryId = currentSelectedCategory!!.categoryId,
                                                title = "",
                                                startDate = selectedDate.toString(),
                                                endDate = selectedDate.toString(),
                                                repeatRule = null,
                                                priority = false,
                                                memo = "",
                                                icon = null,
                                                color = currentSelectedCategory!!.color,
                                                startTime = null,
                                                index = null,
                                                savedTime = Clock.System.now().epochSeconds,
                                                status = mutableMapOf(selectedDate.toString() to TodoStatus.NONE)

                                            )
                                        )

                                    }) {
                                        Text(
                                            text = "할일 추가하기",
                                            color = colorGray70,
                                            fontFamily = fontFamily(),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            painter = painterResource(Res.drawable.todo_add_icon),
                                            contentDescription = ""
                                        )
                                    }
                                }

                            }


                            items(currentCategoryTodoList, key = { item -> item.id }) { todo ->

                                var todoTitle by remember { mutableStateOf(todo.title) }

                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)

                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colorGray0
                                    )
                                ) {

                                    Row(
                                        modifier = Modifier.background(color = colorGray0),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        CircularCheckbox(
                                            type = "Todo",
                                            status = todo.status?.get(selectedDate.toString())
                                                ?: TodoStatus.NONE,
                                            onCheckedChange = { status ->
                                                viewModel.upsertTodo(
                                                    todo.copy(
                                                        status =
                                                            mutableMapOf(
                                                                selectedDate.toString() to
                                                                        status
                                                            )
                                                    )
                                                )
                                            }
                                        )

                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextField(
                                                keyboardOptions = KeyboardOptions(
                                                    imeAction = ImeAction.Done,
                                                    keyboardType = KeyboardType.Text
                                                ),

                                                // (선택 사항) 'Done' 버튼을 눌렀을 때의 동작을 정의합니다.
                                                keyboardActions = KeyboardActions(
                                                    onDone = {
                                                        viewModel.upsertTodo(
                                                            todo.copy(
                                                                title = todoTitle
                                                            )
                                                        )
                                                        keyboardController?.hide()
                                                        focusManager.clearFocus()
                                                    }
                                                ),
                                                value = todoTitle,
                                                onValueChange = { todoTitle = it },
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = Color.White,
                                                    focusedIndicatorColor = Color.White,
                                                    unfocusedIndicatorColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    disabledIndicatorColor = Color.White,
                                                    cursorColor = Color.Black
                                                ),
                                                maxLines = 1,
                                                singleLine = true,
                                                placeholder = {
                                                    Text(
                                                        when (todo.id) {
                                                            "pre_todo_1" -> "할일을 작성해 주세요"
                                                            "pre_todo_2" -> "한번 터치하면 완료 상태가 됩니다"
                                                            "pre_todo_3" -> "한번 더 터치하면 진행중 상태가 됩니다"
                                                            else -> ""
                                                        },
                                                        fontFamily = fontFamily(),
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Normal,
                                                        color = colorGray100.copy(alpha = if (todo.id == "pre_todo_3") 1f else 0.45f),
                                                        maxLines = 1
                                                    )
                                                },
                                                modifier = Modifier.weight(1f)
                                                    .onFocusChanged { focusState ->

                                                        val nowFocused = focusState.isFocused
                                                        if (isFocused && !nowFocused) {
                                                            // focus → unfocus 시 실행
                                                            viewModel.upsertTodo(
                                                                todo.copy(
                                                                    title = todoTitle
                                                                )
                                                            )
                                                        }
                                                        isFocused = nowFocused

//                                        if (!it.isFocused) viewModel.upsertTodo(todo.copy(title = todoTitle))
                                                    }
                                            )
                                            if (todo.priority) Icon(
                                                painter = painterResource(Res.drawable.todo_item_priority_icon),
                                                contentDescription = ""
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            if (todo.repeatRule != null) Icon(
                                                painter = painterResource(Res.drawable.todo_item_repeat_icon),
                                                contentDescription = ""
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            if (todo.memo.isNotBlank()) Icon(
                                                painter = painterResource(Res.drawable.todo_item_memo_icon),
                                                contentDescription = ""
                                            )

                                        }


                                        IconButton(onClick = {
                                            selectedTodo = todo
                                            isSelectedTodoInGoal = false
                                            todoEditBottomSheet = true
                                        }) {
                                            Icon(
                                                painterResource(Res.drawable.todo_item_more_icon),
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }

                            }


                        }
                    }


                    // 일정 섹션
                    if (currentTodoMode != CurrentTodoMode.TODO) item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "일정",
                            modifier = Modifier.padding(start = 16.dp),
                            fontSize = 14.sp,
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Medium,
                            color = colorGray60
                        )
                    }

                    if (currentTodoMode != CurrentTodoMode.TODO) item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFEEEEEF),
                                contentColor = Color(0xFFEEEEEF)
                            )
                        ) {
                            TextButton(onClick = { planEditBottomSheet = true }) {
                                Text(
                                    text = "일정 추가하기",
                                    color = colorGray70,
                                    fontFamily = fontFamily(),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    painter = painterResource(Res.drawable.todo_add_icon),
                                    tint = Color(0xFF686869),
                                    contentDescription = ""
                                )
                            }
                        }


                    }

                    if (currentTodoMode != CurrentTodoMode.TODO) items(
                        filterTodosByDate(
                            planList,
                            selectedDate
                        )
                    ) { schedule ->

                        var planTitle by remember { mutableStateOf(schedule.title) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 16.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = stringToCategoryIconResource(schedule.icon!!),
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp).background(
                                        color = stringToColor(schedule.color!!).copy(0.3f),
                                        shape = RoundedCornerShape(6.dp)
                                    ).padding(4.dp),
                                    tint = stringToColor(schedule.color)
                                )
                                TextField(
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Text
                                    ),

                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            viewModel.upsertTodo(
                                                schedule.copy(
                                                    title = planTitle
                                                )
                                            )
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        }

                                    ),
                                    value = planTitle,
                                    onValueChange = { planTitle = it },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        focusedIndicatorColor = Color.White,
                                        unfocusedIndicatorColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        disabledIndicatorColor = Color.White,
                                        cursorColor = Color.Black
                                    ),
                                    modifier = Modifier.weight(1f).onFocusChanged { focusState ->

                                        val nowFocused = focusState.isFocused
                                        if (isFocused && !nowFocused) {
                                            // focus → unfocus 시 실행
                                            viewModel.upsertTodo(schedule.copy(title = planTitle))
                                        }
                                        isFocused = nowFocused

//                                if (!it.isFocused) viewModel.upsertTodo(schedule.copy(title = planTitle))
                                    }
                                )

                                if (schedule.startTime != null && schedule.startTime.isNotBlank()) Text(
                                    text = schedule.startTime,
                                    color = colorGray100.copy(alpha = 0.5f),
                                    fontFamily = fontFamily(),
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    modifier = Modifier.background(
                                        colorGray10,
                                        RoundedCornerShape(6.dp)
                                    )
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                )

                                IconButton(onClick = {
                                    selectedPlan = schedule
                                    planEditBottomSheet = true
                                }) {
                                    Icon(
                                        painterResource(Res.drawable.todo_item_more_icon),
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

}


@OptIn(ExperimentalTime::class)
@Composable
fun WeekCalendar(
    currentTodoMode: CurrentTodoMode,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    state: WeekCalendarState,
    daysOfWeek: List<DayOfWeek>,
    onWeekScroll: (LocalDate) -> Unit,
    todoList: MutableMap<Category, List<Todo>>,
    planList: List<Todo>
) {

    LaunchedEffect(state) {

        snapshotFlow {
            state.firstVisibleWeek.days.first().date
        }.distinctUntilChanged()
            .collect {

                if (isDateInCurrentWeek(selectedDate, it, DayOfWeek.MONDAY)) onWeekScroll(
                    selectedDate
                )
                else onWeekScroll(it)

            }
    }

    LaunchedEffect(selectedDate) {
        state.scrollToWeek(selectedDate)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {

        Column {
            WeekCalendar(
                state = state,
                dayContent = { day ->
                    var todoCount = 0
                    todoList.forEach {
                        todoCount += filterTodosByDate(it.value, day.date).size
                    }

                    WeekDay(
                        currentTodoMode = currentTodoMode, day = day, selectedDate = selectedDate,
                        todoList = todoList, planList = planList
                    ) {

                        onDateSelected(it.date)

                    }
                },
                weekHeader = {
                    DaysOfWeekTitle(daysOfWeek)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }


}

@OptIn(ExperimentalTime::class)
@Composable
fun MonthCalendar(
    currentTodoMode: CurrentTodoMode,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    state: CalendarState,
    daysOfWeek: List<DayOfWeek>,
    onMonthScroll: (YearMonth) -> Unit,
    todoList: MutableMap<Category, List<Todo>>,
    planList: List<Todo>
) {

    LaunchedEffect(state) {

        snapshotFlow { state.firstVisibleMonth.yearMonth }
            .distinctUntilChanged()
            .collect {
                onMonthScroll(it)
            }
    }

    LaunchedEffect(selectedDate) {

        state.scrollToMonth(selectedDate.yearMonth)

    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {

        Column {
            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    MonthDay(
                        currentTodoMode = currentTodoMode,
                        day = day,
                        selectedDate = selectedDate,
                        todoList = todoList,
                        planList = planList
                    ) {
                        onDateSelected(it.date)
                    }
                }, monthHeader = {
                    DaysOfWeekTitle(daysOfWeek)
                },
                userScrollEnabled = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun WeekDay(
    currentTodoMode: CurrentTodoMode, day: WeekDay, selectedDate: LocalDate,
    todoList: MutableMap<Category, List<Todo>>, planList: List<Todo>, onClick: (WeekDay) -> Unit
) {

    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp), color = Color.LightGray)


    Column(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clickable(
                onClick = { onClick(day) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DrawCalendar(currentTodoMode, day, null, selectedDate, todoList, planList)
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun DrawCalendar(
    currentTodoMode: CurrentTodoMode,
    weekDay: WeekDay?,
    calendarDay: CalendarDay?,
    selectedDate: LocalDate,
    todoList: MutableMap<Category, List<Todo>>,
    planList: List<Todo>
) {

    val targetDate = weekDay?.date ?: calendarDay!!.date

    Spacer(modifier = Modifier.height(3.dp))

    Text(
        text = targetDate.day.toString(),
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        color =
            if (targetDate == selectedDate) colorGray0
            else if (weekDay?.position == WeekDayPosition.RangeDate) {
                if (targetDate == LocalDate.now()) mainColor
                else colorGray60
            } else if (calendarDay?.position == DayPosition.MonthDate) {
                if (targetDate == LocalDate.now()) mainColor
                else colorGray60
            } else colorGray20.copy(alpha = 0.6f),
        fontWeight = if (targetDate != selectedDate && targetDate == LocalDate.now()) FontWeight.SemiBold else FontWeight.W400,
        fontFamily = fontFamily(),
        maxLines = 1,
        softWrap = false,
        modifier = Modifier
            .background(
                color = if (targetDate == selectedDate) mainColor else Color.Unspecified,
                shape = if (targetDate == selectedDate) RoundedCornerShape(9.dp) else ShapeDefaults.Small
            )
            .padding(horizontal = if (targetDate.day < 10) 10.dp else 6.dp)
    )

    Spacer(modifier = Modifier.height(2.dp))

    when (currentTodoMode) {
        CurrentTodoMode.DO_IT -> {
            val testList = mutableListOf<Todo>()
            todoList.forEach {
                testList.addAll(filterTodosByDate(it.value, targetDate))
            }
            testList.addAll(filterTodosByDate(planList, targetDate))
            if (testList.size <= 4) testList.forEach { todo ->
                Text(
                    text = todo.title.ifBlank { " " },
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 1.dp).fillMaxWidth().background(
                        color = if (todo.categoryId == null) Color(0xFFDDDEE1)
                        else Color(0xFFDBDAF8), shape = RoundedCornerShape(4.dp)
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            else testList.forEachIndexed { index, todo ->
                if (index <= 2) Text(
                    text = todo.title.ifBlank { " " },
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth().background(
                        color = if (todo.categoryId == null) Color(0xFFDDDEE1)
                        else Color(0xFFDBDAF8), shape = RoundedCornerShape(4.dp)
                    ),
                    maxLines = 1
                )
                else {
                    if (index == 3) {
                        Text(
                            text = "+ ${testList.size - 3}개",
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 9.sp,
                            color = colorGray60
                        )
                        return@forEachIndexed
                    }

                }

                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        CurrentTodoMode.TODO -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(1.dp),
                content = {

                    items(todoList.toList().filter {
                        filterTodosByDate(
                            it.second,
                            targetDate
                        ).isNotEmpty()
                    }.take(4)) { map ->

                        val todoCount = filterTodosByDate(map.second, targetDate).size
                        Spacer(modifier = Modifier.size(1.dp))

                        Row(
                            modifier = Modifier.background(
                                color = stringToColor(map.first.color).copy(0.3f),
                                shape = RoundedCornerShape(4.dp)
                            ).padding(horizontal = 2.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = stringToCategoryIconResource(map.first.icon),
                                contentDescription = "",
                                modifier = Modifier.size(10.dp).alpha(1f),
                                tint = stringToColor(map.first.color)
                            )
                            Text(
                                "$todoCount",
                                color = stringToColor(map.first.color),
                                fontSize = 9.sp,
                                fontFamily = fontFamily(),
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Spacer(modifier = Modifier.size(1.dp))
                    }
                },
            )
        }

        CurrentTodoMode.PLAN -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(1.dp), content = {

                    items(filterTodosByDate(planList, targetDate).take(4)) { todo ->

                        Spacer(modifier = Modifier.size(1.dp))

                        Row(
                            modifier = Modifier.background(
                                color = stringToColor(todo.color.toString()).copy(0.3f),
                                shape = RoundedCornerShape(4.dp)
                            ).padding(horizontal = 2.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = stringToCategoryIconResource(todo.icon!!),
                                contentDescription = "",
                                modifier = Modifier.size(10.dp).alpha(1f),
                                tint = stringToColor(todo.color.toString())
                            )

                        }
                        Spacer(modifier = Modifier.size(1.dp))


                    }

                })
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun MonthDay(
    currentTodoMode: CurrentTodoMode,
    day: CalendarDay,
    selectedDate: LocalDate,
    todoList: MutableMap<Category, List<Todo>>,
    planList: List<Todo>,
    onClick: (CalendarDay) -> Unit
) {
    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp), color = Color.LightGray)
    Column(
        modifier = Modifier
            .aspectRatio(2f / 3f)
            .clickable(
                enabled = true,
                //day.position == DayPosition.MonthDate,
                onClick = { onClick(day) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DrawCalendar(currentTodoMode, null, day, selectedDate, todoList, planList)
    }
}


@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.toKorICalDay(),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            )
        }
    }
}