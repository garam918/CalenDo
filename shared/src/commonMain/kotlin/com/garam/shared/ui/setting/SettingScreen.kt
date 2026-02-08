package com.garam.shared.ui.setting

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.garam.shared.data.SettingType
import com.garam.shared.ui.login.LoginScreen
import com.garam.shared.util.AppInfo
import com.garam.shared.util.resources.colorGray0
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray40
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.mainColor
import com.garam.todolist.Res
import com.garam.todolist.calendo_feedback_link_string
import com.garam.todolist.privacy_policy_string
import com.garam.todolist.setting_account_icon
import com.garam.todolist.setting_back_icon
import com.garam.todolist.setting_category_edit_icon
import com.garam.todolist.setting_document_icon
import com.garam.todolist.setting_main_img
import com.garam.todolist.setting_next_icon
import com.garam.todolist.setting_notification_set_icon
import com.garam.todolist.setting_privacy_policy_icon
import com.garam.todolist.setting_screen_custom_icon
import com.garam.todolist.setting_version_icon
import com.garam.todolist.terms_of_use_string
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingMainScreen(
    rootNavController: NavController,
    onBackToMain: () -> Unit,
    settingViewModel: SettingViewModel
) {

    val settingNavController = rememberNavController()
    val currentDestination by settingNavController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    val canNavigateBack = currentRoute != "settingHome"
    val navBackStackEntry by settingNavController.currentBackStackEntryAsState()

//    val navBackStackEntry = remember { settingNavController.currentBackStackEntryAsState() }
//
    LaunchedEffect(currentRoute) {
        if (currentRoute == "editCategory") {
            println(currentRoute)
            settingViewModel.getCurrentUser()
//            settingViewModel.getCategory()
        } else if (currentRoute == "settingHome") {
            settingViewModel.getCurrentUser()

        }
    }


    val title = when (currentRoute) {
        "settingHome" -> "설정"
        "accountSetting" -> "내 계정"
        "screenCustom" -> "화면 커스텀"
        "editCategory" -> "카테고리 관리"
        "notificationSetting" -> "푸시 알림 설정"
        else -> "설정"
    }

    Scaffold(
//        topBar = {
//            SettingTopBar(
//                title = title,
//                onBackClick = {
//                    if (canNavigateBack) settingNavController.popBackStack()
//                    else onBackToMain()
//                }
//            )
//        },
        containerColor = mainBackgroundColor,
        modifier = Modifier.background(color = mainBackgroundColor).padding(horizontal = 16.dp)
    ) { padding ->
        Box(modifier = Modifier.background(color = mainBackgroundColor)
            .clipToBounds()
//            .padding(padding)
        ) {
            SettingNavHost(rootNavController, settingNavController, settingViewModel)
        }
    }

//    Box(modifier = Modifier.background(color = mainBackgroundColor)
//        .padding(horizontal = 16.dp)) {
//        SettingNavHost(rootNavController, settingNavController, settingViewModel)
//    }
}

@Composable
fun DrawMenu(type: SettingType, email: String, versionString: String, onClick: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(type != SettingType.VERSION_INFO, onClick = onClick)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                when (type) {
                    SettingType.MY_ACCOUNT -> Res.drawable.setting_account_icon
                    SettingType.CUSTOM_SCREEN -> Res.drawable.setting_screen_custom_icon
                    SettingType.CATEGORY_MANAGE -> Res.drawable.setting_category_edit_icon
                    SettingType.NOTIFICATION_SET -> Res.drawable.setting_notification_set_icon

                    SettingType.TERMS_OF_USE -> Res.drawable.setting_document_icon
                    SettingType.PRIVACY_POLICY -> Res.drawable.setting_privacy_policy_icon
                    SettingType.VERSION_INFO -> Res.drawable.setting_version_icon
                }
            ),
            contentDescription = "",
            modifier = Modifier.padding(vertical = 15.dp, horizontal = 12.dp)
        )
        Text(
            text = when (type) {
                SettingType.MY_ACCOUNT -> "내 계정"
                SettingType.CUSTOM_SCREEN -> "화면 커스텀"
                SettingType.CATEGORY_MANAGE -> "카테고리 관리"
                SettingType.NOTIFICATION_SET -> "푸쉬 알림 설정"

                SettingType.TERMS_OF_USE -> "이용약관"
                SettingType.PRIVACY_POLICY -> "개인정보 정책"
                SettingType.VERSION_INFO -> "버전 정보"
            }, modifier = Modifier.weight(1f),
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = colorGray100
        )
        if (email.isNotBlank() || versionString.isNotBlank()) Text(
            text = email + versionString,
            color = colorGray40,
            modifier = Modifier.padding(end = 4.dp),
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Normal,
        )
        if (type != SettingType.VERSION_INFO) IconButton(
            onClick = onClick,
//            modifier = Modifier.padding()
        ) {
            Icon(
            painter = painterResource(Res.drawable.setting_next_icon),
            contentDescription = "",

        ) }
        else Spacer(modifier = Modifier.width(12.dp))


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Medium,
                color = colorGray100,
                fontSize = 18.sp

            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                Icon(painterResource(Res.drawable.setting_back_icon), contentDescription = "뒤로가기")
            }

        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = mainBackgroundColor
        ),
        modifier = Modifier.background(color = mainBackgroundColor).fillMaxWidth()
    )
}

@Composable
fun SettingNavHost(
    rootNavController: NavController,
    navController: NavHostController,
    settingViewModel: SettingViewModel
) {
    val uriHandler = LocalUriHandler.current

    NavHost(
        modifier = Modifier.background(color = mainBackgroundColor),
        navController = navController,
        startDestination = "settingHome",
    ) {
        // 메인 설정 화면
        composable("settingHome") {
            SettingMainScreen(
                rootNavController = rootNavController,
                onNavigate = { route -> navController.navigate(route) },
                uriHandler = uriHandler,
                viewModel = settingViewModel
            )
        }

        composable("accountSetting") {
            MyAccountScreen(
                rootNavController = rootNavController, navController, viewModel = settingViewModel
            )
        }
        composable("screenCustom") { SettingScreenCustomScreen(settingViewModel, onBackClick = { navController.popBackStack() }) }
        composable("editCategory") { SettingCategoryManageScreen(settingViewModel, onBackClick = { navController.popBackStack() }) }
        composable("notificationSetting") { SettingNotificationSettingScreen(settingViewModel, onBackClick = { navController.popBackStack() }) }
    }
}

@Composable
fun SettingMainScreen(
    rootNavController: NavController,
    onNavigate: (String) -> Unit,
    uriHandler: UriHandler,
    viewModel: SettingViewModel
) {
    val feedbackUrl = stringResource(Res.string.calendo_feedback_link_string)

    val termsOfUseUrl = stringResource(Res.string.terms_of_use_string)
    val privacyPolicyUrl = stringResource(Res.string.privacy_policy_string)

    var showLoginBottomSheet by remember { mutableStateOf(false) }

    val userInfo = viewModel.userInfo.collectAsState()


    if (showLoginBottomSheet) {
        LoginScreen(
            onNavigateToMain = {
                rootNavController.navigate("main") {
                    popUpTo(0)
//                popUpTo("setting") { inclusive = true }
                }
            },
            onDismiss = {
                viewModel.getCurrentUser()
                showLoginBottomSheet = false

            }, loginScreenType = "Setting"
        )
    }

    LazyColumn(modifier = Modifier.background(color = mainBackgroundColor)) {

        item {
            SettingTopBar("설정", onBackClick = {
                rootNavController.popBackStack()
            })
        }

        item {
            Box(modifier = Modifier.padding(top = 16.dp)) {
                Image(
                    painter = painterResource(Res.drawable.setting_main_img),
                    contentDescription = ""
                )

                Column(
                    modifier = Modifier
                        .padding(top = 130.dp, bottom = 16.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(14.dp))
                ) {
                    Text(
                        text = "뚜잇, 어땠나요?",
                        modifier = Modifier.padding(top = 16.dp, start = 12.dp),
                        color = mainColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = fontFamily()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "좋았던 점이나 아쉬운 점을 남겨주시면 꼼꼼히 읽고 \n개선에 반영할게요!",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = mainColor.copy(0.6f),
                        fontFamily = fontFamily(),
                        fontWeight = FontWeight.Normal,
                    )

                    TextButton(
                        onClick = { uriHandler.openUri(feedbackUrl) },
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 16.dp)
                            .fillMaxWidth()
                            .background(color = Color.Black, shape = RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            "뚜잇 개선에 참여하기", color = colorGray0,
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(10.dp))
        }
        item {

            // 메뉴 섹션 1
            Column(
                modifier = Modifier.background(Color.White, RoundedCornerShape(14.dp))
            ) {
                DrawMenu(
                    type = SettingType.MY_ACCOUNT,
                    email = if (userInfo.value?.loginType == "anonymous") "로그인하여 기록 저장" else userInfo.value?.email.toString(),
                    versionString = "",
                    onClick = {
                        if (userInfo.value?.loginType == "anonymous") showLoginBottomSheet = true
                        else onNavigate("accountSetting")
                    })
                DrawMenu(SettingType.CUSTOM_SCREEN, "", "", onClick = {
                    onNavigate("screenCustom")
                })
                DrawMenu(SettingType.CATEGORY_MANAGE, "", "", onClick = {
                    onNavigate("editCategory")
                })
                DrawMenu(SettingType.NOTIFICATION_SET, "", "", onClick = {
                    onNavigate("notificationSetting")
                })
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {

            Column(
                modifier = Modifier.background(Color.White, RoundedCornerShape(14.dp))
            ) {
                DrawMenu(SettingType.TERMS_OF_USE, "", "", onClick = {
                    uriHandler.openUri(termsOfUseUrl)
                })
                DrawMenu(SettingType.PRIVACY_POLICY, "", "", onClick = {
                    uriHandler.openUri(privacyPolicyUrl)
                })
                DrawMenu(SettingType.VERSION_INFO, "", AppInfo.appVersion, onClick = {
                })
            }
        }
    }
}