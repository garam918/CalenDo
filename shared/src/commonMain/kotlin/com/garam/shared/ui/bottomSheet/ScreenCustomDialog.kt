package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.data.CategoryIconType
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.todolist.Res
import com.garam.todolist.todo_category_change_check_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCustomDialog(
    type: String,
    onDismiss: (Int) -> Unit,
    selectedIndex : Int
    ) {

    val sheetState = rememberModalBottomSheetState()
    var selectedIndex by remember { mutableIntStateOf(selectedIndex) }

    ModalBottomSheet(
        onDismissRequest = {

            onDismiss(selectedIndex)
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(28.dp),
        containerColor = mainBackgroundColor,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 36.dp, top = 12.dp),
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {
        Column(modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(if(type == "start_screen") "시작 화면 설정" else "할일 정렬"
                , fontWeight = FontWeight.Bold, color = colorGray100, textAlign = TextAlign.Center
                ,fontFamily = fontFamily(), fontSize = 15.sp)

            Spacer(modifier = Modifier.height(16.dp))

            DrawButton(if(type == "start_screen") "뚜잇" else "작성한 순", onClick = {
                selectedIndex = 0
                onDismiss(0)
            }, selectedIndex == 0)
            DrawButton(if(type == "start_screen") "할일" else "완료한 일이 위", onClick = {
                selectedIndex = 1
                onDismiss(1)
            }, selectedIndex == 1)
            DrawButton(if(type == "start_screen") "일정" else "완료한 일이 아래", onClick = {
                selectedIndex = 2
                onDismiss(2)
            }, selectedIndex == 2)

        }
    }
}

@Composable
fun DrawButton(title: String, onClick: () -> Unit, isSelected : Boolean) {

    Row(modifier = Modifier.padding(top = 12.dp).fillMaxWidth().clickable(true, onClick = {
        onClick()
    }).background(color =  if(isSelected) colorGray10 else mainBackgroundColor, shape = if(isSelected) RoundedCornerShape(14.dp) else RectangleShape),
        verticalAlignment = Alignment.CenterVertically) {

        Text(title, modifier = Modifier.weight(1f).padding(vertical = 12.dp, horizontal = 12.dp),
            fontFamily = fontFamily(), fontWeight = FontWeight.Medium, color = colorGray100,
            fontSize = 14.sp)
        if(isSelected) Icon(painter = painterResource(Res.drawable.todo_category_change_check_icon)
            , contentDescription = "", modifier = Modifier.padding(end = 12.dp))
    }
}