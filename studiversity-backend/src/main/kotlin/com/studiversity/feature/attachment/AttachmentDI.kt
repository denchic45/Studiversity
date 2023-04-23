package com.studiversity.feature.attachment

import com.studiversity.feature.attachment.usecase.AddAttachmentUseCase
import com.studiversity.feature.attachment.usecase.CheckOwnerAttachmentUseCase
import com.studiversity.feature.attachment.usecase.FindAttachmentUseCase
import com.studiversity.feature.attachment.usecase.RemoveAttachmentUseCase
import com.studiversity.feature.course.work.submission.usecase.FindAttachmentsByReferenceUseCase
import org.koin.dsl.module

val attachmentModule = module {
    single { AttachmentRepository(get()) }
    single { AddAttachmentUseCase(get(), get()) }
    single { FindAttachmentUseCase(get(),get()) }
    single { FindAttachmentsByReferenceUseCase(get(),get()) }
    single { RemoveAttachmentUseCase(get(),get()) }
    single { CheckOwnerAttachmentUseCase(get(),get()) }
}
