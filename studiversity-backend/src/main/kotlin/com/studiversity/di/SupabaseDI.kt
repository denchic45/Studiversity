package com.studiversity.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val supabaseClientModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = get(named(SupabaseEnv.SUPABASE_URL)),
            supabaseKey = get(named(SupabaseEnv.SUPABASE_KEY)),
        ) {
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