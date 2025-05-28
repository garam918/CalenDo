package com.garam.todolist.data

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