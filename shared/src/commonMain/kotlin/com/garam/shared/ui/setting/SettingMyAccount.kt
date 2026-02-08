package com.garam.shared.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garam.shared.auth.AuthRepositoryProvider
import com.garam.shared.ui.dialog.AccountLogOutDeleteDialog
import com.garam.shared.ui.login.LoginScreen
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray60
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.shared.util.resources.warningColor
import com.garam.todolist.Res
import com.garam.todolist.setting_next_icon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
fun MyAccountScreen(
    rootNavController: NavController,
    navController: NavController,
    viewModel: SettingViewModel
) {

    var userEmail by remember { mutableStateOf(viewModel.userInfo.value?.email) }
    val scope = rememberCoroutineScope()
    val repo = AuthRepositoryProvider().get()
    var type by remember { mutableStateOf("") }

    var isShowAccountDeleteDialog by remember { mutableStateOf(false) }

    var showLoginBottomSheet by remember { mutableStateOf(false) }


    if (isShowAccountDeleteDialog) AccountLogOutDeleteDialog(
        type = type,
        show = isShowAccountDeleteDialog,
        onDismiss = {
            isShowAccountDeleteDialog = false
            type = ""
        },
        onConfirm = {


            if (type == "Delete") {

                showLoginBottomSheet = true

//                scope.launch {
//                    viewModel.deleteAccount()
//                }.invokeOnCompletion {
//
//                    rootNavController.navigate("onboarding") {
//                        popUpTo(0)
//                    }
//                }
            }
            else viewModel.signOut().invokeOnCompletion {

                rootNavController.navigate("onboarding") {
                    popUpTo(0)
                }
            }
        }
    )

    if (showLoginBottomSheet) {
        LoginScreen(
            onNavigateToMain = {

                println("SettingAccount")

                scope.launch {
                    viewModel.deleteAccount()
                }.invokeOnCompletion {

                    rootNavController.navigate("onboarding") {
                        popUpTo(0)
                    }
                }
            },
            onDismiss = {

                showLoginBottomSheet = false

            }, loginScreenType = "DeleteAccount"
        )
    }

    Column(modifier = Modifier.background(color = mainBackgroundColor).fillMaxSize()) {

        SettingTopBar("내 계정", onBackClick = {
            navController.popBackStack()
        })
        Spacer(modifier = Modifier.height(30.dp))


        Row(
            modifier = Modifier.fillMaxWidth()
                .background(color = Color.White, shape = RoundedCornerShape(14.dp))
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            Text(
                text = "연동 계정", modifier = Modifier.weight(1f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 16.sp, color = colorGray100
            )
            Text(
                text = userEmail.toString(), color = colorGray60, fontSize = 14.sp,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable(true, onClick = {
                    type = "LogOut"
                    isShowAccountDeleteDialog = true

                })
                .background(color = Color.White, shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "로그아웃", modifier = Modifier.weight(1f),
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 16.sp, color = colorGray100
            )
            IconButton(onClick = {
                type = "LogOut"
                isShowAccountDeleteDialog = true

            }) {
                Icon(
                    painter = painterResource(Res.drawable.setting_next_icon),
                    contentDescription = ""
                )
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = {
            type = "Delete"
            isShowAccountDeleteDialog = true

        }, modifier = Modifier.fillMaxWidth()) {
            Text(
                "계정 삭제", color = warningColor, textAlign = TextAlign.Center,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 16.sp
            )
        }
    }
}