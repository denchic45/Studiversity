package com.denchic45.studiversity.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.denchic45.studiversity.ui.theme.DesktopAppTheme
import com.denchic45.studiversity.ui.theme.toDrawablePath


@Preview
@Composable
fun PreviewButtons() = DesktopAppTheme {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button({}) {
            IconWithTextButtonContent(painterResource("ic_timetable".toDrawablePath()), "Hello")
        }
        OutlinedButton({}) {
            IconWithTextButtonContent(painterResource("ic_timetable".toDrawablePath()), "Hello")
        }
        FilledTonalButton({}) {
            IconWithTextButtonContent(painterResource("ic_timetable".toDrawablePath()), "Hello")
        }
        TextButton({}) {
            IconWithTextButtonContent(painterResource("ic_timetable".toDrawablePath()), "Hello")
        }
        ElevatedButton({}) {
            IconWithTextButtonContent(painterResource("ic_timetable".toDrawablePath()), "Hello")
        }
    }
}

@Composable
fun IconWithTextButtonContent(icon: Painter, text: String) {
    Icon(painter = icon, null, Modifier.size(ButtonDefaults.IconSize))
    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
    Text(text, style = MaterialTheme.typography.labelLarge)
}

@Composable
fun TextButtonContent(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge)
}