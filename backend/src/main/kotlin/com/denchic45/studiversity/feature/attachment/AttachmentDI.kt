package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.feature.attachment.usecase.*
import com.denchic45.studiversity.feature.course.work.submission.usecase.FindAttachmentsByReferenceUseCase
import org.koin.dsl.module

val attachmentModule = module {
    single { AttachmentRepository(get()) }
    single { AddAttachmentUseCase(get(), get()) }
    single { FindAttachmentUseCase(get(), get()) }
    single { FindAttachmentsByReferenceUseCase(get(), get()) }
    single { RemoveAttachmentUseCase(get(), get()) }
    single { CheckOwnerAttachmentUseCase(get(), get()) }
    single { FindAttachmentResourceTypeByIdUseCase(get(), get()) }
}
