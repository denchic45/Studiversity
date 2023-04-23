package com.denchic45.kts.util

import kotlin.test.Test

class SystemDirsKtTest {

    @Test
    fun appDirTest() {
        val dir = SystemDirs().appDir
        println("Directory is: $dir")
    }
}