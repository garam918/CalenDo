package com.garam.shared.util.functions

import com.garam.shared.util.resources.*

fun stringToColor(color: String) = when(color) {
    "default_color_1" -> todoColor1
    "default_color_2" -> todoColor2
    "default_color_3" -> todoColor3
    "default_color_4" -> todoColor4
    "default_color_5" -> todoColor5
    "default_color_6" -> todoColor6
    "default_color_7" -> todoColor7
    "default_color_8" -> todoColor8
    "default_color_9" -> todoColor9
    "default_color_10" -> todoColor10
    "default_color_11" -> todoColor11
    "default_color_12" -> todoColor12
    else -> todoColor1
}