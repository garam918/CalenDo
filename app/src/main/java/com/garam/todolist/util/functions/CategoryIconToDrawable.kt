package com.garam.todolist.util.functions

import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.R

fun iconToDrawable(categoryIconType: CategoryIconType) = when(categoryIconType) {
    CategoryIconType.HOME -> R.drawable.todo_icon_home
    CategoryIconType.HEALTH_CROSS -> R.drawable.todo_icon_health_cross
    CategoryIconType.PILLS -> R.drawable.todo_icon_pills
    CategoryIconType.CAFE -> R.drawable.todo_icon_cafe
    CategoryIconType.RESTAURANT -> R.drawable.todo_icon_restaurant
    CategoryIconType.DRINK -> R.drawable.todo_icon_drink

    CategoryIconType.FAVORITE -> R.drawable.todo_icon_favorite
    CategoryIconType.STRAWBERRY_CAKE -> R.drawable.todo_icon_strawberry_cake
    CategoryIconType.GIFT -> R.drawable.todo_icon_gift
    CategoryIconType.MUSIC -> R.drawable.todo_icon_music
    CategoryIconType.PIGGY_BANK_SLOT -> R.drawable.todo_icon_piggy_bank_slot
    CategoryIconType.RECEIPT -> R.drawable.todo_icon_receipt


    CategoryIconType.BOOKMARK -> R.drawable.todo_icon_bookmark
    CategoryIconType.FLAG -> R.drawable.todo_icon_flag
    CategoryIconType.PORTFOLIO -> R.drawable.todo_icon_portfolio
    CategoryIconType.DOCUMENT -> R.drawable.todo_icon_document
    CategoryIconType.CYCLIST -> R.drawable.todo_icon_cyclist
    CategoryIconType.TENNIS -> R.drawable.todo_icon_tennis


    CategoryIconType.PLANE -> R.drawable.todo_icon_plane
    CategoryIconType.CAR -> R.drawable.todo_icon_car
    CategoryIconType.CAMPSITE -> R.drawable.todo_icon_campsite
    CategoryIconType.LIGHTNING -> R.drawable.todo_icon_lightning
    CategoryIconType.CROSS -> R.drawable.todo_icon_cross
    else -> R.drawable.todo_icon_home
}

fun iconToFilledDrawable(categoryIconType: CategoryIconType) = when(categoryIconType) {
    CategoryIconType.HOME -> R.drawable.todo_icon_home_filled
    CategoryIconType.HEALTH_CROSS -> R.drawable.todo_icon_health_cross_filled
    CategoryIconType.PILLS -> R.drawable.todo_icon_pills_filled
    CategoryIconType.CAFE -> R.drawable.todo_icon_cafe_filled
    CategoryIconType.RESTAURANT -> R.drawable.todo_icon_restaurant_filled
    CategoryIconType.DRINK -> R.drawable.todo_icon_drink_filled

    CategoryIconType.FAVORITE -> R.drawable.todo_icon_favorite_filled
    CategoryIconType.STRAWBERRY_CAKE -> R.drawable.todo_icon_strawberry_cake_filled
    CategoryIconType.GIFT -> R.drawable.todo_icon_gift_filled
    CategoryIconType.MUSIC -> R.drawable.todo_icon_music_filled
    CategoryIconType.PIGGY_BANK_SLOT -> R.drawable.todo_icon_piggy_bank_slot_filled
    CategoryIconType.RECEIPT -> R.drawable.todo_icon_receipt_filled


    CategoryIconType.BOOKMARK -> R.drawable.todo_icon_bookmark_filled
    CategoryIconType.FLAG -> R.drawable.todo_icon_flag_filled
    CategoryIconType.PORTFOLIO -> R.drawable.todo_icon_portfolio_filled
    CategoryIconType.DOCUMENT -> R.drawable.todo_icon_document_filled
    CategoryIconType.CYCLIST -> R.drawable.todo_icon_cyclist_filled
    CategoryIconType.TENNIS -> R.drawable.todo_icon_tennis_filled


    CategoryIconType.PLANE -> R.drawable.todo_icon_plane_filled
    CategoryIconType.CAR -> R.drawable.todo_icon_car_filled
    CategoryIconType.CAMPSITE -> R.drawable.todo_icon_campsite_filled
    CategoryIconType.LIGHTNING -> R.drawable.todo_icon_lightning_filled
    CategoryIconType.CROSS -> R.drawable.todo_icon_cross_filled
    else -> R.drawable.todo_icon_home_filled

}