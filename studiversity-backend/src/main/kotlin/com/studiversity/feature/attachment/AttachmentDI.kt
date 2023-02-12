package com.studiversity.feature.attachment

import org.koin.dsl.module

val attachmentModule = module {
    single { AttachmentRepository(get()) }
}