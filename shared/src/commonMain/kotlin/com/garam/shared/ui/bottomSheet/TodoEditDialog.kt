package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.data.Category
import com.garam.shared.data.Todo
import com.garam.shared.data.TodoStatus
import com.garam.shared.ui.dialog.DeleteDialog
import com.garam.shared.ui.snackbar.SnackbarScreen
import com.garam.shared.util.functions.addUntilToRRule
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.warningColor
import com.garam.todolist.Res
import com.garam.todolist.bottom_sheet_close_icon
import com.garam.todolist.todo_category_change_icon
import com.garam.todolist.todo_delete_icon
import com.garam.todolist.todo_memo_icon
import com.garam.todolist.todo_priority_fill_icon
import com.garam.todolist.todo_priority_icon
import com.garam.todolist.todo_repeat_end_icon
import com.garam.todolist.todo_repeat_icon
import com.garam.todolist.todo_tomorrow_icon
import com.kizitonwose.calendar.core.now
import com.kizitonwose.calendar.core.plusDays
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, ExperimentalUuidApi::class)
@Composable
fun TodoEditDialog(
    type: String,
    todo: Todo?,
    selectedDate: String,
    openBottomSheet: Boolean,
    onDismiss: (String, String) -> Unit,
    onDelete: (Todo) -> Unit,
    upsertTodo: (String, Todo) -> Unit,
    categoryList: List<Category>
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var todoRepeatBottomSheet by remember { mutableStateOf(false) }
    var categoryChangeBottomSheet by remember { mutableStateOf(false) }
    var todoMemoDialog by remember { mutableStateOf(false) }
    var isShowTodoDeleteDialog by remember { mutableStateOf(false) }

    val snackbarScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

//    var selectedTodo by remember { mutableStateOf(todo) }

    if (todoRepeatBottomSheet) TodoRepeatSettingDialog(
        openBottomSheet = todoRepeatBottomSheet,
        todo = todo,
        onDismiss = { todoRepeatBottomSheet = false },
        onSave = { upsertTodo("",it) })

    if (categoryChangeBottomSheet) CategoryChangeDialog(
        todo = todo,
        openBottomSheet = categoryChangeBottomSheet,
        onDismiss = { categoryChangeBottomSheet = false },
        onSelect = {

            upsertTodo("CategoryChange",it)
//            snackbarScope.launch {
//                val result = snackbarHostState.showSnackbar(message = "${it.title}이(가) 변경되었습니다.", actionLabel = "되돌리기",
//                    duration = SnackbarDuration.Short)
//
//                if (result == SnackbarResult.ActionPerformed) {
//
//                    upsertTodo("",it.copy(categoryId = todo?.categoryId))
//                }
//
//            }

         },
        categoryList = categoryList
    )
    if (todoMemoDialog) TodoMemoDialog(
        todoMemoDialog, onDismiss = { todoMemoDialog = false },
        todo = todo,
        onSave = {
            println("todo edit dialog $it")
            upsertTodo("",it)
        })

    if (isShowTodoDeleteDialog) DeleteDialog(
        show = isShowTodoDeleteDialog,
        type = "Todo",
        id = todo?.id.toString(),
        onDismiss = {
            isShowTodoDeleteDialog = false
//            snackbarScope.launch {
//                snackbarHostState.showSnackbar("${todo?.title}이(가) 삭제되었습니다.", actionLabel = "되돌리기",
//                    duration = SnackbarDuration.Short)
//            }
        },
        onDelete = {
            onDelete(todo!!)
            isShowTodoDeleteDialog = false
            onDismiss("", "")
        }
    )

    if (openBottomSheet) ModalBottomSheet(
        onDismissRequest = { onDismiss("", "") },
        sheetState = sheetState,
        containerColor = mainBackgroundColor,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 36.dp, top = 12.dp),
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .background(color = mainBackgroundColor)
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
        ) {

            item {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
//                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                        onDismiss("","")
                    }) {
                        Icon(painter = painterResource(Res.drawable.bottom_sheet_close_icon), contentDescription = null)
                    }

                    Text(
                        text = todo?.title.toString(),
                        fontWeight = FontWeight.Bold,
                        color = colorGray100,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp,
//                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                }
            }

            if (type != "Goal") item {

                Row(modifier = Modifier.padding(top = 10.dp)) {
                    TextButton(
                        onClick = {
                            todoRepeatBottomSheet = true
                            onDismiss("Repeat", "")
                        },
                        modifier = Modifier.weight(1f)
                            .background(color = colorGray10, shape = RoundedCornerShape(14.dp))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Icon(
                                painter = painterResource(Res.drawable.todo_repeat_icon),
                                contentDescription = "",
                                tint = Color.Black
                            )
                            Text(
                                text = "반복 설정",
                                color = colorGray100,
                                fontFamily = fontFamily(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))


                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    TextButton(
                        onClick = {
                            var dismissType = ""
                            val id = Uuid.random().toString()

                            val editTodo = when {
                                // 선택한 날짜가 오늘 날짜와 같은 경우
                                selectedDate == LocalDate.now().toString() -> {
                                    if (todo?.status?.get(selectedDate) == TodoStatus.COMPLETED ||
                                        todo?.status?.get(selectedDate) == TodoStatus.IN_PROGRESS
                                    ) {
                                        dismissType = "TomorrowAdd"
                                        todo.copy(
                                            id = id,
                                            startDate = LocalDate.now().plusDays(1).toString(),
                                            endDate = LocalDate.now().plusDays(1).toString(),
                                            repeatRule = null
                                        )
                                    } else {
                                        dismissType = "TomorrowChange"
                                        todo?.copy(
                                            startDate = LocalDate.now().plusDays(1).toString(),
                                            endDate = LocalDate.now().plusDays(1).toString()
                                        )
                                    }
                                }
                                // 미래의 일정인 경우
                                LocalDate.parse(selectedDate) > LocalDate.now() -> {
                                    if (todo?.status?.get(selectedDate) == TodoStatus.COMPLETED ||
                                        todo?.status?.get(selectedDate) == TodoStatus.IN_PROGRESS
                                    ) {
                                        dismissType = "TodayAdd"
                                        todo.copy(
                                            id = id,
                                            startDate = LocalDate.now().toString(),
                                            endDate = LocalDate.now().toString(),
                                            repeatRule = null
                                        )
                                    } else {
                                        dismissType = "TodayChange"
                                        todo?.copy(
                                            startDate = LocalDate.now().toString(),
                                            endDate = LocalDate.now().toString()
                                        )
                                    }
                                }

                                // 과거의 일정인 경우
                                LocalDate.parse(selectedDate) < LocalDate.now() -> {
                                    if (todo?.status?.get(selectedDate) == TodoStatus.COMPLETED ||
                                        todo?.status?.get(selectedDate) == TodoStatus.IN_PROGRESS
                                    ) {
                                        dismissType = "TodayAdd"
                                        todo.copy(
                                            id = id,
                                            startDate = LocalDate.now().toString(),
                                            endDate = LocalDate.now().toString(),
                                            repeatRule = null
                                        )
                                    } else {
                                        dismissType = "TodayChange"
                                        todo?.copy(
                                            startDate = LocalDate.now().toString(),
                                            endDate = LocalDate.now().toString()
                                        )
                                    }
                                }

                                else -> {
                                    dismissType = "TomorrowChange"
                                    todo?.copy(
                                        startDate = LocalDate.now().plusDays(1).toString(),
                                        endDate = LocalDate.now().plusDays(1).toString()
                                    )
                                }
                            }

                            upsertTodo("",editTodo!!)
                            onDismiss(dismissType, id)

                        }, modifier = Modifier.weight(1f)
                            .background(color = colorGray10, shape = RoundedCornerShape(14.dp))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(10.dp))

                            Icon(
                                painter = painterResource(Res.drawable.todo_tomorrow_icon),
                                contentDescription = "",
                                tint = Color.Black
                            )
                            Text(
                                text = when {
                                    // 선택한 날짜가 오늘 날짜와 같은 경우
                                    selectedDate == LocalDate.now().toString() -> {
                                        if (todo?.status?.get(selectedDate) == TodoStatus.COMPLETED ||
                                            todo?.status?.get(selectedDate) == TodoStatus.IN_PROGRESS
                                        )
                                            "내일도 하기"
                                        else "내일 하기"
                                    }
                                    // 미래의 일정인 경우
                                    LocalDate.parse(selectedDate) > LocalDate.now() -> {
                                        if (todo?.status?.get(selectedDate) == TodoStatus.COMPLETED ||
                                            todo?.status?.get(selectedDate) == TodoStatus.IN_PROGRESS
                                        )
                                            "오늘도 하기"
                                        else "오늘 하기"
                                    }

                                    LocalDate.parse(selectedDate) < LocalDate.now() -> {
                                        if (todo?.status?.get(selectedDate) == TodoStatus.COMPLETED ||
                                            todo?.status?.get(selectedDate) == TodoStatus.IN_PROGRESS
                                        )
                                            "오늘도 하기"
                                        else "오늘 하기"
                                    }

                                    else -> "내일 하기"


                                },
                                color = colorGray100,
                                fontFamily = fontFamily(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorGray10, shape = RoundedCornerShape(14.dp))
                ) {

                    TextButton(onClick = {

                        upsertTodo("",todo!!.copy(priority = !todo.priority))
                        onDismiss("Priority", "")
                    }) {
                        Icon(
                            painter = if (todo?.priority != true) painterResource(Res.drawable.todo_priority_icon)
                            else painterResource(Res.drawable.todo_priority_fill_icon),
                            contentDescription = "",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (todo?.priority != true) "우선순위 등록" else "우선순위 등록 해제",
                            color = colorGray100,
                            fontFamily = fontFamily(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    TextButton(onClick = {
                        todoMemoDialog = true
                        onDismiss("Memo", "")
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.todo_memo_icon),
                            contentDescription = "",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "메모", color = colorGray100, modifier = Modifier.fillMaxWidth(),
                            fontFamily = fontFamily(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,)
                    }

                    if (type != "Goal") TextButton(onClick = {
                        categoryChangeBottomSheet = true
                        onDismiss("CategoryChange", "")
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.todo_category_change_icon),
                            contentDescription = "",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "카테고리 변경",
                            color = colorGray100,
                            fontFamily = fontFamily(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {

                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    .background(colorGray10, shape = RoundedCornerShape(14.dp))) {
                    if (type != "Goal") {
                        if (todo?.repeatRule != null) TextButton(
                            onClick = {
                                upsertTodo(
                                    "",
                                    todo.copy(
                                        repeatRule = addUntilToRRule(
                                            todo.repeatRule,
                                            selectedDate
                                        )
                                    )
                                )
                                onDismiss("RepeatTodayEnd", "")
                            },
//                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
//                                .background(colorGray10, shape = RoundedCornerShape(14.dp))
                        ) {

                            Icon(
                                painter = painterResource(Res.drawable.todo_repeat_end_icon),
                                contentDescription = "", tint = warningColor,
                                modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "오늘까지만 반복", color = colorGray100,
                                fontFamily = fontFamily(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal, modifier = Modifier.weight(1f)
                            )

                        }
                    }

                    TextButton(
                        onClick = {
                            if (todo?.repeatRule != null) isShowTodoDeleteDialog = true
                            else {
                                onDelete(todo!!)
                                onDismiss("Delete", "")
                            }
                        },
//                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
//                            .background(colorGray10, shape = RoundedCornerShape(14.dp))
                    ) {

                        Icon(
                            painter = painterResource(Res.drawable.todo_delete_icon),
                            contentDescription = "",
                            tint = warningColor,
                            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (todo?.repeatRule == null) "삭제하기" else "전체 삭제",
                            color = colorGray100,
                            fontFamily = fontFamily(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }


}