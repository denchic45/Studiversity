package com.denchic45.kts.ui.navigation

class ConfirmConfig(
    val title: String,
    val text: String? = null,
    val icon: String? = null,
    val onConfirm:()->Unit
) : OverlayConfig

class ConfirmChild(val config: ConfirmConfig) : OverlayChild