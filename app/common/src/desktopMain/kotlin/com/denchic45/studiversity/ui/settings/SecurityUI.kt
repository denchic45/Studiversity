package com.denchic45.studiversity.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun SecurityScreen(component: SecurityComponent) {

    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is SecurityComponent.Child.UpdateEmail -> UpdateEmailDialog(child.component)
            is SecurityComponent.Child.UpdatePassword -> UpdatePasswordDialog(child.component)
        }
    }

    val state = remember { component.state }
    Column {
        ListItem(
            headlineContent = { Text("Почта") },
            supportingContent = { Text(if (state.needToConfirm) state.email else "требует подтверждения через почту") },
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = component::onUpdateEmailClick)
                .let { if (state.needToConfirm) it.background(MaterialTheme.colorScheme.tertiaryContainer) else it }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.normal))
        ListItem(
            headlineContent = { Text("Пароль") },
            supportingContent = { Text("Изменить") },
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = component::onUpdatePasswordClick)
        )
    }
}
