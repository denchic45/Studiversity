package com.denchic45.kts.ui.components.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.components.BaseDialog
import com.denchic45.kts.ui.components.theme.toColor
import com.denchic45.kts.ui.components.theme.toShape
import com.denchic45.kts.ui.components.tokens.DialogTokens

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    optionalButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
//    properties: DialogProperties = DialogProperties()
) {
    BaseDialog(onDismissRequest = onDismissRequest) {
//        val dialogPaneDescription = getString(Strings.Dialog)
        AlertDialogContent(
            buttons = {
                Row {
                    optionalButton?.let {
                        it()
                        Spacer(Modifier.weight(1f))
                    }
                    AlertDialogFlowRow(
                        mainAxisSpacing = ButtonsMainAxisSpacing,
                        crossAxisSpacing = ButtonsCrossAxisSpacing
                    ) {
                        dismissButton?.invoke()
                        confirmButton()
                    }
                }
            },
            modifier = modifier.then(
                Modifier
//                .semantics { paneTitle = dialogPaneDescription }
            ),
            icon = icon,
            title = title,
            text = text,
            shape = shape,
            containerColor = containerColor,
            tonalElevation = tonalElevation,
            // Note that a button content color is provided here from the dialog's token, but in
            // most cases, TextButtons should be used for dismiss and confirm buttons.
            // TextButtons will not consume this provided content color value, and will used their
            // own defined or default colors.
            buttonContentColor = DialogTokens.ActionLabelTextColor.toColor(),
            iconContentColor = iconContentColor,
            titleContentColor = titleContentColor,
            textContentColor = textContentColor,
        )
    }
}

/**
 * Contains default values used for [AlertDialog]
 */
object AlertDialogDefaults {
    /** The default shape for alert dialogs */
    val shape: Shape @Composable get() = DialogTokens.ContainerShape.toShape()

    /** The default container color for alert dialogs */
    val containerColor: Color @Composable get() = DialogTokens.ContainerColor.toColor()

    /** The default icon color for alert dialogs */
    val iconContentColor: Color @Composable get() = DialogTokens.IconColor.toColor()

    /** The default title color for alert dialogs */
    val titleContentColor: Color @Composable get() = DialogTokens.HeadlineColor.toColor()

    /** The default text color for alert dialogs */
    val textContentColor: Color @Composable get() = DialogTokens.SupportingTextColor.toColor()

    /** The default tonal elevation for alert dialogs */
    val TonalElevation: Dp @Composable get() = DialogTokens.ContainerElevation
}

private val ButtonsMainAxisSpacing = 8.dp
private val ButtonsCrossAxisSpacing = 12.dp
