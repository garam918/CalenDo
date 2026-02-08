package com.garam.shared

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.garam.shared.auth.AuthRepositoryProvider
import com.garam.shared.ui.onboarding.OnboardingScreen
import com.garam.shared.ui.setting.SettingMainScreen
import com.garam.shared.ui.setting.SettingViewModel
import com.garam.shared.ui.todolist.TodoList
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val navController = rememberNavController()
    val repo = AuthRepositoryProvider()
    val currentUser = repo.get().currentUser()

    val isLoggedIn = remember { mutableStateOf(currentUser != null) }

    val settingViewModel: SettingViewModel = koinViewModel()


    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn.value) "main" else "onboarding",
        enterTransition = {
            slideInHorizontally(animationSpec = tween(300))
        },

        exitTransition = {

            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
        },

        popEnterTransition = {
            slideInHorizontally(animationSpec = tween(300))
        },

        popExitTransition = {

            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
        }
    ) {

        composable("onboarding") {
            OnboardingScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        // 온보딩 화면을 백 스택에서 제거하여 뒤로가기 버튼으로 돌아가지 못하게 합니다.
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            TodoList(navController)
        }
        composable("setting") {
            SettingMainScreen(
                rootNavController = navController,
                onBackToMain = { navController.popBackStack() },
                settingViewModel = settingViewModel
            )
        }

//        navigation(startDestination = "settingHome", route = "setting") {
//
//            // 1. 설정 메인 화면 (목록)
//            composable("settingHome") {
//                SettingMainScreen(
//                    // 이제 rootNavController만 사용하므로 바로 전달하거나 람다를 사용
//                    onBackToMain = { navController.popBackStack() },
//                    onNavigateToDetail = { route -> navController.navigate(route) },
//                    settingViewModel = settingViewModel,
//                    uriHandler = LocalUriHandler.current
//                )
//            }
//
//            // 2. 설정 내부 상세 화면들 (SettingNavHost에 있던 것들을 여기로 이동)
//            composable("accountSetting") {
//                MyAccountScreen(
//                    rootNavController = navController, // rootNavController 하나만 넘김
//                    viewModel = settingViewModel
//                )
//            }
//            composable("screenCustom") { SettingScreenCustomScreen(settingViewModel) }
//            composable("editCategory") { SettingCategoryManageScreen(settingViewModel) }
//            composable("notificationSetting") { SettingNotificationSettingScreen(settingViewModel) }
//        }
    }

}
