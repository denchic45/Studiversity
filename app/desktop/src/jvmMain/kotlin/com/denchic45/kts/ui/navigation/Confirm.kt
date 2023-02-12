package com.denchic45.kts.ui.navigation

class ConfirmConfig(
    val title: String,
    val text: String? = null,
    val icon: String? = null
) : OverlayConfig

class ConfirmChild(val config: ConfirmConfig) : OverlayChild