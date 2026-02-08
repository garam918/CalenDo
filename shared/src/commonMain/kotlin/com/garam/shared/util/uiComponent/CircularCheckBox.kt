package com.garam.shared.util.uiComponent

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.*
import com.garam.shared.data.TodoStatus
import com.garam.shared.util.resources.colorGray0
import com.garam.todolist.Res
import com.garam.todolist.todo_status_completed_icon
import com.garam.todolist.todo_status_in_progress_icon
import com.garam.todolist.todo_status_none_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun CircularCheckbox(
    type: String,
    status: TodoStatus,
    onCheckedChange: (TodoStatus) -> Unit
) {

    Icon(
        painter = painterResource(
            when(status) {
                TodoStatus.NONE -> Res.drawable.todo_status_none_icon
                TodoStatus.COMPLETED -> Res.drawable.todo_status_completed_icon
                TodoStatus.IN_PROGRESS -> Res.drawable.todo_status_in_progress_icon
            }
        ),
        contentDescription = "",
        modifier = Modifier.padding(start = 12.dp).clickable(true, onClick = { onCheckedChange(
            when(status) {
                TodoStatus.NONE -> TodoStatus.COMPLETED
                TodoStatus.COMPLETED -> TodoStatus.IN_PROGRESS
                TodoStatus.IN_PROGRESS -> TodoStatus.NONE
            }
        ) }),
        tint = if (type == "Goal") colorGray0 else Color.Unspecified
    )
}