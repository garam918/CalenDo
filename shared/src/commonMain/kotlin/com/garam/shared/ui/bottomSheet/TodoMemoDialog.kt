package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.garam.shared.data.Todo
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.todolist.Res
import com.garam.todolist.memo_dialog_img
import com.garam.todolist.todo_memo_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun TodoMemoDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSave: (Todo) -> Unit,
    todo: Todo?
) {
    var memo by remember { mutableStateOf(todo?.memo) }


    if (show) {
        Dialog(onDismissRequest = {
            println("memo dialog $memo")
            onSave(todo!!.copy(memo = memo.toString()))
//            memo = ""
            onDismiss()
        }

        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize().background(mainBackgroundColor,RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.TopCenter
            ) {
                // 흰색 배경 카드
                Surface (
                    shape = RoundedCornerShape(24.dp),
                    color = mainBackgroundColor,
                    shadowElevation = 8.dp,
                    modifier = Modifier.wrapContentSize()
                ) {

                    TextField(
                        value = memo.toString(),
                        onValueChange = { memo = it },
                        placeholder = { Text("메모를 적어주세요",
                            fontFamily = fontFamily(), fontWeight = FontWeight.Normal,
                            fontSize = 16.sp) },
                        modifier = Modifier
                            .width(260.dp)
                            .height(260.dp)
                            .padding(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = mainBackgroundColor,
                            focusedIndicatorColor = mainBackgroundColor,
                            unfocusedIndicatorColor = mainBackgroundColor,
                            unfocusedContainerColor = mainBackgroundColor,
                            disabledIndicatorColor = mainBackgroundColor,
                            cursorColor = Color.Black
                        )
                    )
                }

                // 상단 보라색 테이프
                Image(
                    painter = painterResource(Res.drawable.memo_dialog_img), // 리소스 필요
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-12).dp) // 카드 위로 겹치게
                        .size(width = 100.dp, height = 24.dp)
                )
            }
        }
    }
    
}
