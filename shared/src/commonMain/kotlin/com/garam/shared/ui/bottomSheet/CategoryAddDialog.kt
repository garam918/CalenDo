package com.garam.shared.ui.bottomSheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.data.Category
import com.garam.shared.data.CategoryIconType
import com.garam.shared.ui.dialog.DeleteDialog
import com.garam.shared.util.functions.stringToCategoryIconResource
import com.garam.shared.util.functions.stringToColor
import com.garam.shared.util.resources.categoryColors
import com.garam.shared.util.resources.colorGray10
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.icons
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.warningColor
import com.garam.todolist.Res
import com.garam.todolist.bottom_sheet_close_icon
import com.garam.todolist.todo_delete_icon
import com.garam.todolist.week_goal_expand_icon
import org.jetbrains.compose.resources.painterResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun CategoryAddDialog(
    categoryLastIndex : Int,
    openBottomSheet: Boolean,
    selectedCategory: Category?,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onSave : (Category) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true

    )

    var iconExpandState by remember { mutableStateOf(false) }
    var colorExpandState by remember { mutableStateOf(false) }
    var categoryTitleText by remember { mutableStateOf(selectedCategory?.title ?: "") }
    var isShowCategoryDeleteDialog by remember { mutableStateOf(false) }

    var selectedIcon by remember { mutableStateOf(selectedCategory?.icon ?: CategoryIconType.HOME) }
    var selectedColor by remember { mutableStateOf(selectedCategory?.color ?: "default_color_1") }

    val focusManager = LocalFocusManager.current

    var isFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val dismissFocus: () -> Unit = {
        println("dismiss")

        keyboardController?.hide()
        focusManager.clearFocus(force = true)
    }

    val defaultCategory = Category(
        categoryId = Uuid.random().toString(),
        title = "",
        index = categoryLastIndex+1,
        icon = CategoryIconType.HOME,
        color = "default_color_1"
    )

    var category by remember { mutableStateOf(
        selectedCategory
            ?: defaultCategory
    ) }

    if(isShowCategoryDeleteDialog) DeleteDialog(
        show = isShowCategoryDeleteDialog,
        type = "Category",
        id = category.categoryId,
        onDismiss = { isShowCategoryDeleteDialog = false },
        onDelete = {
            onDelete(category.categoryId)
            isShowCategoryDeleteDialog = false
            onDismiss()
        }
    )


    if(openBottomSheet) ModalBottomSheet(
        onDismissRequest = {
            category = defaultCategory
            iconExpandState = false
            colorExpandState = false
            categoryTitleText = ""
            selectedIcon = CategoryIconType.HOME
            selectedColor = "default_color_1"
            onDismiss()
                           },
        sheetState = sheetState,
        shape = RoundedCornerShape(28.dp),
        containerColor = mainBackgroundColor,
        modifier = Modifier
//            .pointerInput(Unit) {
//                detectVerticalDragGestures(
//                    onDragStart = { /* 드래그 시작 시 아무것도 안 함 */ },
//                    onDragEnd = { /* 드래그 종료 시 아무것도 안 함 */ },
//                    onDragCancel = { /* 드래그 취소 시 아무것도 안 함 */ },
//                    // 이 onVerticalDrag 람다 내에서 아무 작업도 하지 않으면,
//                    // 시트의 기본 드래그 동작이 발생하지 않고 이벤트가 소비됩니다.
//                    onVerticalDrag = { change, dragAmount ->
//                        // change.consume()을 명시적으로 호출할 필요 없이,
//                        // 함수가 이벤트를 소비합니다.
//                    }
//                )
//            }
            .imePadding()
//            .background(shape = MaterialTheme.shapes.large, color = mainBackgroundColor)
            .padding(start = 12.dp, end = 12.dp, bottom = 36.dp)
        ,
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {

        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize()
//            .background(shape = MaterialTheme.shapes.large, color = mainBackgroundColor)
            .padding(start = 24.dp, end = 24.dp, top = 12.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    println(isFocused)
                    if (isFocused) {
                        focusManager.clearFocus(force = true)
//                        keyboardController?.hide()
                    }
                })
            }
//            .pointerInput(Unit) {
//                detectTapGestures(onTap = {
//                    if (isFocused) {
//                        focusManager.clearFocus(force = true)
//                    }
//                })
//            }
//            .clickable(          // ← 여기 중요!
//                interactionSource = remember { MutableInteractionSource() },
//                onClick = { dismissFocus() },
//                indication = null
//            )
//            {
//                dismissFocus()
////                focusManager.clearFocus()
////                keyboardController?.hide()
//            }
        ) {

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
//                contentAlignment = Alignment.Center
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
                        text = if (selectedCategory == null) "카테고리 추가" else "카테고리 수정",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily(),
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    TextButton(
                        onClick = {
                            onSave(
                                category.copy(
                                    categoryId = if (selectedCategory == null) Uuid.random()
                                        .toString() else category.categoryId,
                                    title = categoryTitleText,
                                    color = selectedColor,
                                    icon = selectedIcon
                                )
                            )
                            onDismiss()
                        },

//                    modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            text = "저장",
                            color = Color.Black,
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    value = categoryTitleText,
                    onValueChange = {
                        categoryTitleText = it
                        category = category.copy(title = categoryTitleText)
                    },
                    modifier = Modifier
                        .fillMaxWidth().clip(shape = RoundedCornerShape(14.dp))
                        .background(color = Color.White).onFocusChanged {
                            val nowFocused = it.isFocused

//                        if(!it.isFocused) keyboardController?.hide()

//                        if (isFocused && !nowFocused) {
//
//                        }

                            isFocused = nowFocused

                            if (!it.isFocused) {
                                println("text field ${it.isFocused}")
                                keyboardController?.hide()
                            }

//                        isFocused = it.isFocused
                        },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "카테고리 이름 입력", color = colorGray10, fontFamily = fontFamily(),
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
                    ),
//                keyboardActions = KeyboardActions(
//                    onDone = {
//                        keyboardController?.hide()
//                        focusManager.clearFocus()
//                    }
//
//                )
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Column(
                    modifier = Modifier.background(
                        color = Color.White,
                        shape = MaterialTheme.shapes.medium
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        Text(
                            "아이콘",
                            modifier = Modifier.padding(start = 21.dp),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = stringToCategoryIconResource(selectedIcon),
                            tint = stringToColor(selectedColor),
                            contentDescription = "",
                            modifier = Modifier.background(
                                color = stringToColor(selectedColor).copy(0.3f),
                                shape = MaterialTheme.shapes.medium
                            ).padding(4.dp)
                        )
                        IconButton(
                            onClick = { iconExpandState = !iconExpandState },
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.week_goal_expand_icon),
                                contentDescription = "",
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
                                IconButton(onClick = {
                                    category = category.copy(icon = iconType)
                                    selectedIcon = iconType
                                }) {
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

                Spacer(modifier = Modifier.height(10.dp))
            }

            item {

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
                            modifier = Modifier.padding(start = 21.dp),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
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
                                IconButton(onClick = {
                                    category = category.copy(color = color)
                                    selectedColor = color
                                }) {
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

                if(selectedCategory != null) TextButton(
                    onClick = {
                        isShowCategoryDeleteDialog = true

//                    onDelete(category)
//                    onDismiss()
                    }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        .background(colorGray10, shape = RoundedCornerShape(14.dp))
                ) {

                    Icon(
                        painter = painterResource(Res.drawable.todo_delete_icon),
                        contentDescription = "",
                        tint = warningColor,
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "삭제",
                        color = colorGray100,
                        modifier = Modifier.weight(1f),
                        fontFamily = fontFamily(), fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }


        }
    }
}