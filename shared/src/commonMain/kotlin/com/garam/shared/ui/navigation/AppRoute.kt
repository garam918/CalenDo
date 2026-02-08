package com.garam.shared.ui.navigation

sealed class AppRoute(val route: String) {
    object Onboarding : AppRoute("onboarding")
    object Login : AppRoute("login") // bottomSheet
    object Schedule : AppRoute("schedule")
    object Settings : AppRoute("settings")

    // Settings 내부 세부 화면
    object Profile : AppRoute("settings/profile")
    object Notifications : AppRoute("settings/notifications")
}
