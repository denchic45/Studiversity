package com.studiversity.feature.attachment

import com.studiversity.feature.attachment.usecase.CheckOwnerAttachmentUseCase
import com.studiversity.feature.attachment.usecase.FindAttachmentUseCase
import org.koin.dsl.module

val attachmentModule = module {
    single { AttachmentRepository(get()) }
    single { FindAttachmentUseCase(get(),get()) }
    single { CheckOwnerAttachmentUseCase(get(),get()) }
}
