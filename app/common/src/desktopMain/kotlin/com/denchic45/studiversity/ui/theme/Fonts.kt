package com.denchic45.studiversity.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font


private fun String.toFontPath() = "font/$this.ttf"

actual val RobotoFamily = FontFamily(
    Font("roboto_thin".toFontPath(), FontWeight.Thin),
    Font("roboto_medium".toFontPath(), FontWeight.Light),
    Font("roboto_regular".toFontPath(), FontWeight.Normal),
    Font("roboto_medium".toFontPath(), FontWeight.Medium),
    Font("roboto_bold".toFontPath(), FontWeight.Bold),
    Font("roboto_black".toFontPath(), FontWeight.Black)
)

actual val ManropeFamily = FontFamily(
    Font("manrope_extralight".toFontPath(), FontWeight.ExtraLight),
    Font("manrope_light".toFontPath(), FontWeight.Light),
    Font("manrope_regular".toFontPath(), FontWeight.Normal),
    Font("manrope_medium".toFontPath(), FontWeight.Medium),
    Font("manrope_semibold".toFontPath(), FontWeight.SemiBold),
    Font("manrope_bold".toFontPath(), FontWeight.Bold),
    Font("manrope_extrabold".toFontPath(), FontWeight.ExtraBold)
)

val InterFamily = FontFamily(
    Font("inter_thin".toFontPath(), FontWeight.Thin),
    Font("inter_extralight".toFontPath(), FontWeight.ExtraLight),
    Font("inter_light".toFontPath(), FontWeight.Light),
    Font("inter_regular".toFontPath(), FontWeight.Normal),
    Font("inter_medium".toFontPath(), FontWeight.Medium),
    Font("inter_semibold".toFontPath(), FontWeight.SemiBold),
    Font("inter_bold".toFontPath(), FontWeight.Bold),
    Font("inter_extraBold".toFontPath(), FontWeight.ExtraBold),
    Font("inter_extraBlack".toFontPath(), FontWeight.Black),
)