package com.denchic45.kts.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.theme.toDrawablePath

@Composable
actual fun AnimatedLogo() {
    Image(
        painterResource("ic_logo".toDrawablePath()),
        null,
        Modifier.size(124.dp)
    )
}