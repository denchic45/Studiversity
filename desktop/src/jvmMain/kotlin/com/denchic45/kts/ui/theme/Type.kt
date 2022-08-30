package com.denchic45.kts.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

val defaultTypography = Typography()

@OptIn(ExperimentalUnitApi::class)
val Typography = Typography(
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = FontFamily(
            Font(resource = "fonts/Gilroy-Medium.ttf")
        )
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = FontFamily(
            Font(resource = "fonts/Gilroy-Medium.ttf")
        )
    ),
    titleLarge = defaultTypography.titleLarge.copy(
        fontFamily = FontFamily(
            Font(resource = "fonts/Gilroy-Semibold.ttf")
        ),
        fontSize = TextUnit(20F, TextUnitType.Sp)
    ),
    bodyLarge = defaultTypography.bodyLarge.copy(
        fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf"))
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(
        fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf")),
    ),
    bodySmall = defaultTypography.bodySmall.copy(
        fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf"))
    )
)