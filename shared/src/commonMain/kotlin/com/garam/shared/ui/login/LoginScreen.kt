package com.garam.shared.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.shared.auth.AuthRepositoryProvider
import com.garam.shared.auth.rememberGoogleAuthHandler
import com.garam.shared.platform
import com.garam.shared.util.resources.colorGray100
import com.garam.shared.util.resources.colorGray20
import com.garam.shared.util.resources.colorGray50
import com.garam.shared.util.resources.colorGray60
import com.garam.shared.util.resources.fontFamily
import com.garam.shared.util.resources.mainBackgroundColor
import com.garam.todolist.Res
import com.garam.todolist.google_login_icon
import com.garam.todolist.apple_login_icon
import com.garam.todolist.privacy_policy_string
import com.garam.todolist.terms_of_use_string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi


@Composable
fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    type: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.background(color = Color.White, shape = RoundedCornerShape(24.dp))
            .fillMaxWidth().height(48.dp)

        , border = if(type == "google") BorderStroke(width = 1.dp, color = colorGray20) else null
    ) {
        Icon(painter = if(type == "google") painterResource(Res.drawable.google_login_icon)
        else painterResource(Res.drawable.apple_login_icon), contentDescription = "",
            tint = Color.Unspecified)
        Spacer(modifier = Modifier.width(15.dp))
        Text(text = text, color = colorGray100,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Medium, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class, ExperimentalTime::class)
@Composable
fun LoginScreen(onNavigateToMain: () -> Unit, onDismiss : () -> Unit, viewModel: LoginViewModel = koinViewModel(), loginScreenType : String) {

    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }


    val repo = AuthRepositoryProvider()

    val googleAuthHandler = rememberGoogleAuthHandler()
    val googleLoginScope = rememberCoroutineScope()




    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
            showSheet = false
                           },
        sheetState = sheetState,
        containerColor = mainBackgroundColor
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight().padding(16.dp,10.dp,16.dp,16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
//        SocialLoginButton(
//            text = "카카오 로그인",
//            backgroundColor = Color(0xFFFEE500),
//            contentColor = Color.Black,
//            onClick = { /* 카카오 로그인 로직 */ }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        SocialLoginButton(
//            text = "네이버 로그인",
//            backgroundColor = Color(0xFF03C75A),
//            contentColor = Color.White,
//            onClick = { /* 네이버 로그인 로직 */ }
//        )

            Text(text = "소셜 로그인", textAlign = TextAlign.Center,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Bold, fontSize = 15.sp, color = colorGray100)
            Spacer(modifier = Modifier.height(16.dp))
            SocialLoginButton(
                text = "구글 로그인",
                backgroundColor = Color.White,
                contentColor = Color.Gray,
                type = "google",
                onClick = {

                    googleLoginScope.launch {
                        val tokenList = googleAuthHandler.signIn()

                        if(tokenList.isNotEmpty()) {
                            val idToken = tokenList[0].toString()
                            val accessToken = tokenList[1].toString()

                            println("idToken : $idToken")
                            println("accessToken : $accessToken")

                            if(loginScreenType != "DeleteAccount") {

                                val userData = repo.get().signInWithGoogle(idToken, accessToken)

                                if (userData != null) {

                                    if (repo.get().isExistAccount(userData.uid)) {

                                        println(userData.uid)

                                        viewModel.saveGoalList(userData.uid)
                                        viewModel.saveCategoryList(userData.uid)
                                        viewModel.saveTodoList(userData.uid).invokeOnCompletion {

                                            isLoggedIn = true

                                        }

                                    } else viewModel.saveUserData(userData).invokeOnCompletion {

                                        viewModel.saveTutorialTodo(userData.uid)
                                            .invokeOnCompletion {
                                                isLoggedIn = true
                                            }

                                    }
                                }
                            }
                            else {
                                val reAuthenticate = repo.get().reAuthenticate(idToken, accessToken)
                                println("reAuthenticate $reAuthenticate")

                                if(reAuthenticate) {
                                    isLoggedIn = true
                                }
                            }
                        }
                        else {

                        }
                    }

                }
            )

            if(platform() == "iOS") {
                val appleLoginScope = rememberCoroutineScope()


                Spacer(modifier = Modifier.height(16.dp))
                SocialLoginButton(
                    text = "애플 로그인",
                    backgroundColor = Color.White,
                    contentColor = Color.Gray,
                    type = "apple",
                    onClick = {
                        appleLoginScope.launch {

                            val currentUser = repo.get().currentUser()
                            val user = if(currentUser == null) repo.get().signInWithApple()
                            else repo.get().linkInWithApple()


                            if(currentUser == null) {

                                if (user != null) {

                                    if (repo.get().isExistAccount(user.uid)) {

                                        viewModel.saveGoalList(user.uid)
                                        viewModel.saveCategoryList(user.uid)
                                        viewModel.saveTodoList(user.uid).invokeOnCompletion {

                                            isLoggedIn = true

                                        }


                                    } else viewModel.saveUserData(user).invokeOnCompletion {
                                        viewModel.saveTutorialTodo(user.uid).invokeOnCompletion {
                                            isLoggedIn = true
                                        }

                                    }
                                } else {
                                    println("apple user null")

                                }
                            }
                            else {
                                if(user != null) {
                                    viewModel.saveUserData(user).invokeOnCompletion {

                                        isLoggedIn = true
                                    }
                                }
                                else {
                                    val user = repo.get().signInWithApple()!!


                                    viewModel.saveGoalList(user.uid)
                                    viewModel.saveCategoryList(user.uid)
                                    viewModel.saveTodoList(user.uid).invokeOnCompletion {

                                        isLoggedIn = true

                                    }
                                }

                            }

                        }


                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if(loginScreenType == "Onboarding") TextButton(modifier = Modifier.padding(12.dp), onClick = {
                showSheet = false
                CoroutineScope(Dispatchers.Main).launch {
                    val user = repo.get().signInAnonymously()

                    if(user != null) {
                        viewModel.saveUserData(user).invokeOnCompletion {

                            viewModel.saveTutorialTodo(user.uid).invokeOnCompletion {
                                isLoggedIn = true
                            }
                        }
                    }
                }
            }) {
                Text(text = "로그인 없이 사용", textDecoration = TextDecoration.Underline,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.Normal, fontSize = 14.sp, color = colorGray60)
            }

            val annotatedString = ClickText()
            val uriHandler = LocalUriHandler.current

            Text(text = annotatedString,
                modifier = Modifier.padding(horizontal = 20.dp).clickable {
                    annotatedString.getLinkAnnotations(start = 0, end = annotatedString.length)
                        .forEach { annotation ->

                            when (annotation.item) {

                                is LinkAnnotation.Url -> uriHandler.openUri((annotation.item as LinkAnnotation.Url).url)
                            }
                        }
                },
                textAlign = TextAlign.Center,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.Normal, fontSize = 11.sp, color = colorGray50)

        }

        if (isLoggedIn) onNavigateToMain()

    }
}

@Composable
fun ClickText() : AnnotatedString {

    val annotatedText = buildAnnotatedString {
        append("가입하면 뚜잇의 ")

        withLink(
            LinkAnnotation.Url(
                url = stringResource(Res.string.terms_of_use_string),
                styles = TextLinkStyles(style = SpanStyle(color = colorGray50, fontWeight = FontWeight.Bold))
            )
        ) {
            append("이용약관")
        }

        append(" 및 ")

        withLink(
            LinkAnnotation.Url(
                url = stringResource(Res.string.privacy_policy_string),
                styles = TextLinkStyles(style = SpanStyle(color = colorGray50, fontWeight = FontWeight.Bold))
            )
        ) {
            append("개인정보처리방침")
        }

        append("에 동의하게 됩니다.")
    }

    return annotatedText
}