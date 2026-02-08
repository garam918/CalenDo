package com.garam.shared.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.ui.login.LoginScreen
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray80
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.todolist.Res
import com.garam.todolist.onboarding_first_calendo_img_compose
import com.garam.todolist.onboarding_plan_img_compose
import com.garam.todolist.onboarding_todo_img_compose
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onNavigateToMain: () -> Unit) {
    // 총 페이지 수를 정의합니다.
    val pageCount = 3
    // 페이지 상태를 기억하는 PagerState 객체를 생성합니다.
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }

    var currentPage by remember { mutableIntStateOf(0) }

    Column {


        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(4f)
                .background(color = mainBackgroundColor)
        ) { page ->
            OnboardingPage(
                page = page,
//                totalPages = 3,
//                onNext = { scope.launch { pagerState.animateScrollToPage(page + 1) } },
//                onPrev = { scope.launch { pagerState.animateScrollToPage(page - 1) } },
//                onFinish = { showBottomSheet = true }
            )
//            currentPage = page
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .background(color = mainBackgroundColor)
                .fillMaxWidth()
                .padding(vertical = 36.dp, horizontal = 24.dp), // 하단 패딩 적용
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이전 버튼 (첫 페이지는 숨김)
            if (pagerState.currentPage > 0) {
                TextButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    modifier = Modifier
                        .background(Color.LightGray, shape = CircleShape)
                        .size(60.dp)
                ) {
                    Text(
                        text = "< 이전", color = colorGray80, fontSize = 13.sp,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Spacer(Modifier.width(60.dp)) // 자리 맞춤 (사이즈 60.dp로 통일)
            }

            // 페이지 인디케이터 (● ○ ○)
            Row {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(16.dp, 3.dp)
                            .background(
                                if (index == pagerState.currentPage) Color.Black else Color.LightGray,
                                shape = RectangleShape
                            )
                    )
                }
            }

            // 다음 / 시작 버튼
            if (pagerState.currentPage < 2) {
                TextButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    modifier = Modifier
                        .background(Color.Black, shape = CircleShape)
                        .size(60.dp)
                ) {
                    Text(
                        text = "다음 >", color = colorGray0, fontSize = 13.sp,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Medium,
                    )
                }
            } else {
                TextButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .background(Color.Black, shape = CircleShape)
                        .size(60.dp)
                ) {
                    Text(
                        text = "시작 >", color = colorGray0, fontSize = 13.sp,
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }



    }

    if (showBottomSheet) {
        LoginScreen(onNavigateToMain, onDismiss = { showBottomSheet = false } ,loginScreenType = "Onboarding")
    }
}

@Composable
fun OnboardingPage(
    page: Int,
//    totalPages: Int,
//    onNext: () -> Unit,
//    onPrev: () -> Unit,
//    onFinish: () -> Unit
) {

    val (title, subtitle, image) = when (page) {
        0 -> Triple(
            "뚜잇",
            "일정 캘린더와 투두리스트를 한 번에!\n두 마리 토끼를 잡는 똑똑한 관리",
            Res.drawable.onboarding_first_calendo_img_compose
        )
        1 -> Triple(
            "할일",
            "이번 달 목표부터,\n오늘의 할 일까지 정리해보세요!",
            Res.drawable.onboarding_todo_img_compose
        )
        2 -> Triple(
            "일정",
            "중요한 일정은 미리 등록하고\n놓치지 마세요",
            Res.drawable.onboarding_plan_img_compose
        )
        else -> Triple("", "", null)
    }

    Column(
        modifier = Modifier
            .background(color = mainBackgroundColor)
            .padding(horizontal = 24.dp),
            // 콘텐츠 영역 내부와 하단 네비게이션 영역에 각각 적용합니다.
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 2. 콘텐츠 영역
        // page 0: 이미지 하단 배치, page 1/2: 이미지 상단 확장
        if (page == 0) {
            // --- 첫 번째 화면 (뚜잇) 레이아웃: 텍스트 - 이미지 순서 ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남은 공간을 모두 차지하여 하단 네비게이션과 분리
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(100.dp))

                OnboardingTextContent(page, title, subtitle) // 텍스트 컴포저블 재사용

                Spacer(Modifier.height(45.dp))

                if (image != null) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // --- 두 번째/세 번째 화면 (할일/일정) 레이아웃: 이미지 상단 확장 + 텍스트 아래 분리 ---

            // 2-1. 이미지 영역 (상단 확장)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // ⚠️ 핵심: 남은 공간을 모두 차지하여 상단으로 확장
            ) {
                Spacer(Modifier.height(100.dp))

                OnboardingTextContent(page, title, subtitle) // 텍스트 컴포저블 재사용

                if (image != null) {
                    Column {
                        Spacer(Modifier.height(36.dp))

                        Image(
                            painter = painterResource(image),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                }

                // ⚠️ 이미지 위에 텍스트를 오버레이하고 싶다면 여기에 Text 컴포넌트를 배치
                // 하지만 요청하신 대로 겹치지 않게 하려면 텍스트는 아래에 배치합니다.
            }
        }


        // 3. 하단 네비게이션 영역
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 24.dp), // 하단 패딩 적용
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // 이전 버튼 (첫 페이지는 숨김)
//            if (page > 0) {
//                TextButton(
//                    onClick = onPrev,
//                    modifier = Modifier
//                        .background(Color.LightGray, shape = CircleShape)
//                        .size(60.dp)
//                ) {
//                    Text(
//                        text = "< 이전", color = colorGray80, fontSize = 13.sp,
//                        fontFamily = fontFamily(),
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            } else {
//                Spacer(Modifier.width(60.dp)) // 자리 맞춤 (사이즈 60.dp로 통일)
//            }
//
//            // 페이지 인디케이터 (● ○ ○)
//            Row {
//                repeat(totalPages) { index ->
//                    Box(
//                        modifier = Modifier
//                            .padding(horizontal = 4.dp)
//                            .size(16.dp, 3.dp)
//                            .background(
//                                if (index == page) Color.Black else Color.LightGray,
//                                shape = RectangleShape
//                            )
//                    )
//                }
//            }
//
//            // 다음 / 시작 버튼
//            if (page < totalPages - 1) {
//                TextButton(
//                    onClick = onNext,
//                    modifier = Modifier
//                        .background(Color.Black, shape = CircleShape)
//                        .size(60.dp)
//                ) {
//                    Text(
//                        text = "다음 >", color = colorGray0, fontSize = 13.sp,
//                        fontFamily = fontFamily(),
//                        fontWeight = FontWeight.Medium,
//                    )
//                }
//            } else {
//                TextButton(
//                    onClick = onFinish,
//                    modifier = Modifier
//                        .background(Color.Black, shape = CircleShape)
//                        .size(60.dp)
//                ) {
//                    Text(
//                        text = "시작 >", color = colorGray0, fontSize = 13.sp,
//                        fontFamily = fontFamily(),
//                        fontWeight = FontWeight.Medium,
//                    )
//                }
//            }
//        }

    }
}

@Composable
private fun OnboardingTextContent(page: Int, title: String, subtitle: String) {
    // page 0: Center, page 1: Start, page 2: End 정렬
    val horizontalAlignment = when(page) {
        1 -> Alignment.CenterStart
        2 -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    Column() {

        // 타이틀
        Text(
            text = title,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            color = mainColor,
            // ⚠️ 두 번째/세 번째 화면의 정렬을 맞추기 위해 fillMaxWidth()와 wrapContentSize() 사용
            modifier = if (page != 0) {
                Modifier.fillMaxWidth().wrapContentSize(align = horizontalAlignment).padding(top = 100.dp)
            } else {
                Modifier.fillMaxWidth().wrapContentSize(align = Alignment.Center)
            }
        )

        Spacer(Modifier.height(8.dp))

        // 서브타이틀
        Text(
            text = subtitle,
            textAlign = when (page) {
                0 -> TextAlign.Center
                1 -> TextAlign.Start
                2 -> TextAlign.End
                else -> TextAlign.Center
            },
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = colorGray80,
            modifier = if (page != 0) {
                Modifier.fillMaxWidth().wrapContentSize(align = horizontalAlignment)
            } else {
                Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically)
            }
        )
    }
}