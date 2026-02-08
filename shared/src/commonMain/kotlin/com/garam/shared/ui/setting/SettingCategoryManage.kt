package com.garam.shared.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitLongPressOrCancellation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeConsumed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.garam.shared.data.AppPreferences
import com.garam.shared.data.Category
import com.garam.shared.ui.bottomSheet.CategoryAddDialog
import com.garam.shared.ui.toolTip.CustomCoachmarkTooltip
import com.garam.shared.util.functions.stringToCategoryIconResource
import com.garam.shared.util.functions.stringToColor
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray70
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.planAddBtnBgColor
import com.garam.todolist.Res
import com.garam.todolist.setting_next_icon
import com.garam.todolist.todo_add_icon
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

@Composable
fun SettingCategoryManageScreen(viewModel: SettingViewModel, onBackClick: () -> Unit) {


    val isShowCategoryToolTip = AppPreferences.getBoolean("CategoryToolTip", false)
    val categoryList by viewModel.categories.collectAsState()
    var testItemList by remember {
        mutableStateOf(categoryList.sortedBy { it.index }.toMutableList())
    }

    LaunchedEffect(categoryList) {

        testItemList = categoryList.sortedBy { it.index }.toMutableList()
    }


    val itemHeights = remember { mutableStateMapOf<Int, Float>() }

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }

    var isShowCategoryEditBottomSheet by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    var showTooltip by remember { mutableStateOf(true) }

    if (isShowCategoryEditBottomSheet) CategoryAddDialog(
        categoryLastIndex = categoryList.lastIndex,
        isShowCategoryEditBottomSheet,
        selectedCategory = selectedCategory,
        onDismiss = {
            selectedCategory = null
            isShowCategoryEditBottomSheet = false
        },
        onDelete = { categoryId -> viewModel.deleteCategory(categoryId) },
        onSave = { category -> viewModel.upsertCategory(category) }
    )

    val slowDownFactor = 0.9f
        //0.5f


    LazyColumn(
        modifier = Modifier.background(color = mainBackgroundColor).fillMaxSize()
//        .padding(top = 30.dp)
    ) {

        item {
            SettingTopBar("카테고리 관리", onBackClick = {
                onBackClick()
            })
        }

        item {

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = planAddBtnBgColor
                )
            ) {
                TextButton(onClick = { isShowCategoryEditBottomSheet = true }) {
                    Text(
                        text = "카테고리 추가하기", fontFamily = fontFamily(),
                        fontWeight = FontWeight.Normal, fontSize = 15.sp, color = colorGray70
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(Res.drawable.todo_add_icon),
                        contentDescription = ""
                    )
                }
            }


        }

        itemsIndexed(testItemList) { index, category ->
            val isDragging = draggingIndex == index
            val offsetY = if (isDragging) dragOffset else 0f
            // elevation / zIndex for dragged item

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .clickable(true, onClick = {

                        selectedCategory = category
                        isShowCategoryEditBottomSheet = true

                    })
                    .onGloballyPositioned { coordinates ->
                        itemHeights[index] = coordinates.size.height.toFloat()
                    }
                    .graphicsLayer {
                        translationY = offsetY
                    }
                    .zIndex(if (isDragging) 1f else 0f)
                    // pointerInput: long press then drag
                    .pointerInput(Unit) {
                        // each item has its own gesture scope

                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingIndex = index
                                dragOffset = 0f
                            },
                            onDragEnd = {
                                draggingIndex = null
                                dragOffset = 0f

                                val list = testItemList.toMutableList()


                                list.forEachIndexed { index, it ->
                                    val editCategory = it.copy(index = index)
                                    viewModel.upsertCategory(editCategory)
                                }

                            },
                            onDragCancel = {
                                draggingIndex = null
                                dragOffset = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()

//                            dragOffset += dragAmount.y
                                dragOffset += dragAmount.y * slowDownFactor

                                val currentIndex =
                                    draggingIndex ?: return@detectDragGesturesAfterLongPress
                                val direction = dragAmount.y.sign.toInt() // 아래(1) 위(-1)
                                val neighborIndex =
                                    (currentIndex + direction).coerceIn(0, testItemList.lastIndex)

                                val neighborHeight = itemHeights[neighborIndex]
                                    ?: return@detectDragGesturesAfterLongPress

                                // 이웃 아이템 높이만큼 이동했을 때 순서 변경

                                val swapThreshold = neighborHeight * 1.2f


                                if (abs(dragOffset) > swapThreshold
//                                neighborHeight * 0.8f
                                ) {
                                    testItemList.swap(currentIndex, neighborIndex)
                                    draggingIndex = neighborIndex
                                    dragOffset -= direction * neighborHeight
                                }
                            }
                        )
                    }
                    .background(
                        color = if (isDragging) Color.LightGray else Color.White,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    modifier = Modifier.weight(1f),
                    text = category.title,
                    color = stringToColor(category.color),
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
                Icon(
                    painter = painterResource(Res.drawable.setting_next_icon),
                    contentDescription = ""
                )

            }

            if (index == testItemList.lastIndex && showTooltip && !isShowCategoryToolTip) {
                Spacer(modifier = Modifier.height(20.dp))
                CustomCoachmarkTooltip(
                    text = "드래그하여 순서를 변경해보세요",
                    // 툴팁의 가로 폭을 리스트 아이템과 비슷하게 조절
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                        .clickable { showTooltip = false } // 툴팁 클릭 시 숨기기
                )

                AppPreferences.setBoolean("CategoryToolTip", true)
            }
        }

    }
}

private fun <T> MutableList<T>.swap(from: Int, to: Int) {
    if (from == to) return
    val item = removeAt(from)
    add(to, item)
}