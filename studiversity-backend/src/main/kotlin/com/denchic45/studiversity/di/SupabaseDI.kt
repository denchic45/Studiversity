package com.denchic45.studiversity.di

import com.denchic45.studiversity.config
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val supabaseClientModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = config.supabase.url,
            supabaseKey = config.supabase.key,
        ) {
            requestTimeout = 30.seconds
            install(GoTrue) {
                autoLoadFromStorage = false
                alwaysAutoRefresh = false
            }
            install(Realtime)
            install(Storage)
        }
    }
    single { get<SupabaseClient>().gotrue }
    single { get<SupabaseClient>().realtime }
    single { get<SupabaseClient>().storage }
    single { get<Storage>()["main"] }
}