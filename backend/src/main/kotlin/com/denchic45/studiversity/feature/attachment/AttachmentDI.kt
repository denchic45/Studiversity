package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.feature.attachment.usecase.*
import org.koin.dsl.module

val attachmentModule = module {
    single { AttachmentRepository(get()) }
    single { AddAttachmentUseCase(get(), get()) }
    single { FindAttachmentsByResourceUseCase(get(), get()) }
    single { RemoveAttachmentUseCase(get(), get()) }
    single { CheckOwnerAttachmentUseCase(get(), get()) }
    single { FindAttachmentResourceTypeByIdUseCase(get(), get()) }
}
