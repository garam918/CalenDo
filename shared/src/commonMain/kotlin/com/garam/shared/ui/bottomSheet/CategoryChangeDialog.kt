package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.data.Category
import com.garam.shared.data.Todo
import com.garam.shared.util.functions.stringToCategoryIconResource
import com.garam.shared.util.functions.stringToColor
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray30
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.todolist.Res
import com.garam.todolist.bottom_sheet_close_icon
import com.garam.todolist.todo_category_change_check_icon
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChangeDialog(
    todo: Todo?,
    openBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Todo) -> Unit,
    categoryList: List<Category>
) {


    val sheetState = rememberModalBottomSheetState()
    var selectedCategory by remember { mutableStateOf(categoryList.find { it.categoryId == todo?.categoryId }) }

    if (openBottomSheet) ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = mainBackgroundColor,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 24.dp, top = 12.dp),
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {

        LazyColumn(modifier = Modifier.padding(horizontal = 24.dp)) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
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
                        text = "카테고리 변경",
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp,
                        color = colorGray100,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    TextButton(
                        onClick = {

                            onSelect(
                                todo!!.copy(
                                    categoryId = selectedCategory!!.categoryId
                                )
                            )

                            onDismiss()
                        }
                    ) {
                        Text(
                            text = "저장", fontWeight = FontWeight.Medium, color = colorGray100,
                            fontFamily = fontFamily()
                        )

                    }

                }

                Spacer(modifier = Modifier.height(5.dp))

            }


            items(categoryList) { category ->

                Row(
                    modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()
                    .clickable(true, onClick = {
                        selectedCategory = category
                    })
                    .background(
                        color = if (selectedCategory?.categoryId == category.categoryId) colorGray10 else mainBackgroundColor,
                        shape = if (selectedCategory?.categoryId == category.categoryId) RoundedCornerShape(
                            14.dp
                        ) else RectangleShape
                    ),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = stringToCategoryIconResource(category.icon),
                        contentDescription = "",
                        modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                            .size(24.dp)
                            .background(
                                color = stringToColor(category.color).copy(0.3f),
                                shape = CircleShape
                            )
                            .padding(4.dp),
                        tint = stringToColor(category.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.title, color = stringToColor(category.color),
                        modifier = Modifier.padding(vertical = 12.dp).weight(1f),
                        fontWeight = FontWeight.Bold, fontFamily = fontFamily()
                    )
                    if (selectedCategory?.categoryId == category.categoryId) Icon(
                        painter = painterResource(Res.drawable.todo_category_change_check_icon),
                        contentDescription = "",
                        modifier = Modifier.padding(end = 12.dp)
                    )

                }


            }
        }
    }


}