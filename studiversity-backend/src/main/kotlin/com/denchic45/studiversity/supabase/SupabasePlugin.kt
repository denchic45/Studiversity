package com.denchic45.studiversity.supabase

import com.denchic45.studiversity.config
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.realtime.Realtime
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

suspend fun Application.configureSupabase() {
    val realtime by inject<Realtime>()
    val goTrue by inject<GoTrue>()
    realtime.connect()
    goTrue.importAuthToken(config.supabase.key)
}