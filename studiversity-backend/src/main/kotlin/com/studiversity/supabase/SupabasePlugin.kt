package com.studiversity.supabase

import com.studiversity.di.SupabaseEnv
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.realtime.Realtime
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

suspend fun Application.configureSupabase() {
    val realtime by inject<Realtime>()
    val goTrue by inject<GoTrue>()
    realtime.connect()
    goTrue.importAuthToken(get(named(SupabaseEnv.SUPABASE_KEY)))
}