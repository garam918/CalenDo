package com.garam.shared.util.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import com.garam.todolist.Res
import com.garam.todolist.paperlogy_bold
import com.garam.todolist.spinnaker_regular
import com.garam.todolist.spoqa_han_sans_neo_bold
import com.garam.todolist.spoqa_han_sans_neo_light
import com.garam.todolist.spoqa_han_sans_neo_medium
import com.garam.todolist.spoqa_han_sans_neo_regular
import com.garam.todolist.spoqa_han_sans_neo_thin
import org.jetbrains.compose.resources.Font

@Composable
fun fontFamily() : FontFamily = FontFamily(
    Font(
        resource = Res.font.spoqa_han_sans_neo_light, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.Light
    ),
    Font(
        resource = Res.font.spoqa_han_sans_neo_medium, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.Medium
    ),
    Font(
        resource = Res.font.spoqa_han_sans_neo_regular, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.spoqa_han_sans_neo_thin, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.Thin
    ),
    Font(
        resource = Res.font.spoqa_han_sans_neo_bold, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.Bold
    ),
    Font(
        resource = Res.font.spinnaker_regular, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.W400
    ),
    Font(
        resource = Res.font.paperlogy_bold, // 자동 생성된 폰트 리소스 참조
        weight = FontWeight.ExtraBold
    )
)