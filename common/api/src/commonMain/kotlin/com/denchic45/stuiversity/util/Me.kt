package com.denchic45.stuiversity.util

import java.util.*

val UUID?.orMe: String
    get() = this?.toString() ?: "me"