package com.garam.shared.util.functions

import androidx.compose.runtime.Composable
import com.garam.shared.data.CategoryIconType
import com.garam.todolist.Res
import com.garam.todolist.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun stringToCategoryIconResource(iconType: CategoryIconType) = when (iconType) {
    CategoryIconType.HOME -> painterResource(Res.drawable.todo_icon_home)
    CategoryIconType.HEALTH_CROSS -> painterResource(Res.drawable.todo_icon_health_cross)
    CategoryIconType.PILLS -> painterResource(Res.drawable.todo_icon_pills)
    CategoryIconType.CAFE -> painterResource(Res.drawable.todo_icon_cafe)
    CategoryIconType.RESTAURANT -> painterResource(Res.drawable.todo_icon_restaurant)

    CategoryIconType.DRINK -> painterResource(Res.drawable.todo_icon_drink)
    CategoryIconType.FAVORITE -> painterResource(Res.drawable.todo_icon_favorite)
    CategoryIconType.STRAWBERRY_CAKE -> painterResource(Res.drawable.todo_icon_strawberry_cake)
    CategoryIconType.GIFT -> painterResource(Res.drawable.todo_icon_gift)
    CategoryIconType.MUSIC -> painterResource(Res.drawable.todo_icon_music)

    CategoryIconType.PIGGY_BANK_SLOT -> painterResource(Res.drawable.todo_icon_piggy_bank_slot)
    CategoryIconType.RECEIPT -> painterResource(Res.drawable.todo_icon_receipt)
    CategoryIconType.BOOKMARK -> painterResource(Res.drawable.todo_icon_bookmark)
    CategoryIconType.FLAG -> painterResource(Res.drawable.todo_icon_flag)
    CategoryIconType.PORTFOLIO -> painterResource(Res.drawable.todo_icon_portfolio)
    CategoryIconType.DOCUMENT -> painterResource(Res.drawable.todo_icon_document)

    CategoryIconType.CYCLIST -> painterResource(Res.drawable.todo_icon_cyclist)
    CategoryIconType.TENNIS -> painterResource(Res.drawable.todo_icon_tennis)
    CategoryIconType.PLANE -> painterResource(Res.drawable.todo_icon_plane)
    CategoryIconType.CAR -> painterResource(Res.drawable.todo_icon_car)
    CategoryIconType.CAMPSITE -> painterResource(Res.drawable.todo_icon_campsite)

    CategoryIconType.LIGHTNING -> painterResource(Res.drawable.todo_icon_lightning)
    CategoryIconType.CROSS -> painterResource(Res.drawable.todo_icon_cross)

}

@Composable
fun stringToFiledCategoryIconResource(iconType: CategoryIconType) = when (iconType) {
    CategoryIconType.HOME -> painterResource(Res.drawable.todo_icon_home_filled)
    CategoryIconType.HEALTH_CROSS -> painterResource(Res.drawable.todo_icon_health_cross_filled)
    CategoryIconType.PILLS -> painterResource(Res.drawable.todo_icon_pills_filled)
    CategoryIconType.CAFE -> painterResource(Res.drawable.todo_icon_cafe_filled)
    CategoryIconType.RESTAURANT -> painterResource(Res.drawable.todo_icon_restaurant_filled)

    CategoryIconType.DRINK -> painterResource(Res.drawable.todo_icon_drink_filled)
    CategoryIconType.FAVORITE -> painterResource(Res.drawable.todo_icon_favorite_filled)
    CategoryIconType.STRAWBERRY_CAKE -> painterResource(Res.drawable.todo_icon_strawberry_cake_filled)
    CategoryIconType.GIFT -> painterResource(Res.drawable.todo_icon_gift_filled)
    CategoryIconType.MUSIC -> painterResource(Res.drawable.todo_icon_music_filled)

    CategoryIconType.PIGGY_BANK_SLOT -> painterResource(Res.drawable.todo_icon_piggy_bank_slot_filled)
    CategoryIconType.RECEIPT -> painterResource(Res.drawable.todo_icon_receipt_filled)
    CategoryIconType.BOOKMARK -> painterResource(Res.drawable.todo_icon_bookmark_filled)
    CategoryIconType.FLAG -> painterResource(Res.drawable.todo_icon_flag_filled)
    CategoryIconType.PORTFOLIO -> painterResource(Res.drawable.todo_icon_portfolio_filled)
    CategoryIconType.DOCUMENT -> painterResource(Res.drawable.todo_icon_document_filled)

    CategoryIconType.CYCLIST -> painterResource(Res.drawable.todo_icon_cyclist_filled)
    CategoryIconType.TENNIS -> painterResource(Res.drawable.todo_icon_tennis_filled)
    CategoryIconType.PLANE -> painterResource(Res.drawable.todo_icon_plane_filled)
    CategoryIconType.CAR -> painterResource(Res.drawable.todo_icon_car_filled)
    CategoryIconType.CAMPSITE -> painterResource(Res.drawable.todo_icon_campsite_filled)

    CategoryIconType.LIGHTNING -> painterResource(Res.drawable.todo_icon_lightning_filled)
    CategoryIconType.CROSS -> painterResource(Res.drawable.todo_icon_cross_filled)

}