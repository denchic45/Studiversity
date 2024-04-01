package com.denchic45.studiversity.ui.course

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.confirm.ConfirmDeletionDialog
import com.denchic45.studiversity.ui.confirm.ConfirmDialog

@Composable
fun RemoveCourseConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDeletionDialog(
        title = "Удалить курс?",
        text = """
            Вы лишитесь доступа ко всем элементам этого курса.
            Восстановить курс будет невозможно.
        """.trimIndent(),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun ArchiveCourseConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "Добавить курс в архив?",
        text = "Курс в архиве будет недоступны для всех участников. " +
                "Чтобы снова работать с курсом, его нужно восстановить. " +
                "Этот курс будет перемещен в архив.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun RestorationCourseConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "Восстановить курс?",
        text = "Курс вновь будет доступен для преподавателей и учащихся," +
                "а также будет доступен в меню приложения.",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}