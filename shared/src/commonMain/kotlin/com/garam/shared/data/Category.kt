package com.garam.shared.data

data class Category(
    val categoryId : String,
    val title : String,
    val index : Int,
    val icon :  CategoryIconType,
    val color : String
)

enum class CategoryIconType {
    HOME,
    HEALTH_CROSS,
    PILLS,
    CAFE,
    RESTAURANT,
    DRINK,
    FAVORITE,
    STRAWBERRY_CAKE,
    GIFT,
    MUSIC,
    PIGGY_BANK_SLOT,
    RECEIPT,
    BOOKMARK,
    FLAG,
    PORTFOLIO,
    DOCUMENT,
    CYCLIST,
    TENNIS,
    PLANE,
    CAR,
    CAMPSITE,
    LIGHTNING,
    CROSS
}
fun String.IconType() = when(this) {
    "HOME" -> CategoryIconType.HOME
    "HEALTH_CROSS" -> CategoryIconType.HEALTH_CROSS
    "PILLS" -> CategoryIconType.PILLS
    "CAFE" -> CategoryIconType.CAFE
    "RESTAURANT" -> CategoryIconType.RESTAURANT
    "DRINK" -> CategoryIconType.DRINK
    "FAVORITE" -> CategoryIconType.FAVORITE
    "STRAWBERRY_CAKE" -> CategoryIconType.STRAWBERRY_CAKE
    "GIFT" -> CategoryIconType.GIFT
    "MUSIC" -> CategoryIconType.MUSIC
    "PIGGY_BANK_SLOT" -> CategoryIconType.PIGGY_BANK_SLOT
    "RECEIPT" -> CategoryIconType.RECEIPT
    "BOOKMARK" -> CategoryIconType.BOOKMARK
    "FLAG" -> CategoryIconType.FLAG
    "PORTFOLIO" -> CategoryIconType.PORTFOLIO
    "DOCUMENT" -> CategoryIconType.DOCUMENT
    "CYCLIST" -> CategoryIconType.CYCLIST
    "TENNIS" -> CategoryIconType.TENNIS
    "PLANE" -> CategoryIconType.PLANE
    "CAR" -> CategoryIconType.CAR
    "CAMPSITE" -> CategoryIconType.CAMPSITE
    "LIGHTNING" -> CategoryIconType.LIGHTNING
    "CROSS" -> CategoryIconType.CROSS
    else -> CategoryIconType.HOME
}