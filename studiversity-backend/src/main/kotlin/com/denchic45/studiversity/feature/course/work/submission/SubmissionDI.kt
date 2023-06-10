package com.denchic45.studiversity.feature.course.work.submission

import com.denchic45.studiversity.feature.course.work.submission.usecase.*
import org.koin.dsl.module

private val useCaseModule = module {
    single { FindSubmissionUseCase(get(), get()) }
    single { FindSubmissionsByWorkUseCase(get(), get(), get()) }
    single { FindSubmissionByStudentUseCase(get(), get(), get(), get()) }
    single { UpdateSubmissionContentUseCase(get(), get()) }
    single { SubmitSubmissionUseCase(get(), get()) }
    single { CancelSubmissionUseCase(get(), get()) }
    single { SetGradeSubmissionUseCase(get(), get(), get()) }
    single { CancelGradeSubmissionUseCase(get(), get()) }
    single { RequireSubmissionAuthorUseCase(get(), get()) }
    single { IsSubmissionAuthorUseCase(get(), get()) }
//    single { AddFileAttachmentOfSubmissionUseCase(get(), get()) }
//    single { AddLinkAttachmentOfSubmissionUseCase(get(), get()) }
//    single { FindAttachmentsOfSubmissionUseCase(get(), get()) }
//    single { RemoveAttachmentOfSubmissionUseCase(get(), get()) }
//    single { FindAttachmentOfSubmissionUseCase(get(), get()) }
}

private val repositoryModule = module {
    single { SubmissionRepository() }
}

val courseSubmissionModule = module { includes(useCaseModule, repositoryModule) }