package com.arkhamusserver.arkhamus.logic.admin

enum class NiceColor(val colorCode: String) {
    RED("#FF0000"),
    LIME("#00FF00"),
    BLUE("#0000FF"),
    YELLOW("#FFFF00"),
    CYAN_AQUA("#00FFFF"),
    MAGENTA("#FF00FF"),
    SALMON("#FA8072"),
    FOREST_GREEN("#228B22"),
    VIOLET("#EE82EE"),
    BROWN("#A52A2A"),
    CRIMSON("#DC143C"),
    CHARTREUSE("#7FFF00"),
    CORAL("#FF7F50"),
    CHOCOLATE("#D2691E"),
    CORNFLOWER_BLUE("#6495ED"),
    DARK_ORCHID("#9932CC"),
    DARK_SALMON("#E9967A"),
    DEEP_PINK("#FF1493"),
    GOLD("#FFD700"),
    GREEN_YELLOW("#ADFF2F"),
    HOT_PINK("#FF69B4"),
    INDIAN_RED("#CD5C5C"),
    INDIGO("#4B0082"),
    LIGHT_CORAL("#F08080"),
    LIGHT_GREEN("#90EE90"),
    LIGHT_SALMON("#FFA07A"),
    LIGHT_SEA_GREEN("#20B2AA"),
    LIGHT_SKY_BLUE("#87CEFA"),
    LIGHT_SLATE_GREY("#778899"),
    MEDIUM_AQUAMARINE("#66CDAA"),
    MEDIUM_ORCHID("#BA55D3"),
    MEDIUM_PURPLE("#9370DB"),
    BLACK("#000000"),
    ;

    fun getCode(): String {
        return colorCode
    }
}