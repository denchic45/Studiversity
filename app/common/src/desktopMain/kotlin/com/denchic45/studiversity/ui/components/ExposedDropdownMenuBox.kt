package com.denchic45.studiversity.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExposedDropdownMenuBox(
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    val focusRequester = rememberSaveable { FocusRequester() }
    val textFieldSize = rememberSaveable { mutableStateOf(Size.Zero) }
    val textFieldEnterState = rememberSaveable { mutableStateOf(false) }
    val scope = rememberSaveable(expanded, onExpandedChange) {
        object : ExposedDropdownMenuBoxScope {
            override val textFieldEnterState = textFieldEnterState
            override fun Modifier.menuAnchor(): Modifier {
                return composed {
                    onGloballyPositioned { coordinates ->
                        textFieldSize.value = coordinates.size.toSize()
                    }.expandable(
                        enterState = textFieldEnterState,
                        onExpandedChange = { onExpandedChange() }
                    ).focusRequester(focusRequester).onKeyEvent {
                        when {
                            (it.key == Key.Enter) -> {
                                onExpandedChange(); true
                            }

                            else -> {
                                false
                            }
                        }
                    }
                }
            }

            override fun Modifier.exposedDropdownSize(matchTextFieldWidth: Boolean): Modifier {
                return let {
                    if (matchTextFieldWidth) {
                        it.requiredWidth(textFieldSize.value.width.dp)
                    } else {
                        it
                    }
                }
            }
        }
    }
    Box(modifier) {
        scope.content()
    }
    SideEffect {
        if (expanded) focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Modifier.expandable(
    enterState: MutableState<Boolean>,
    onExpandedChange: () -> Unit,
) = pointerInput(Unit) {
    awaitEachGesture {
        var event: PointerEvent
        do {
            event = awaitPointerEvent(PointerEventPass.Initial)
        } while (
            !event.changes.all { it.changedToUp() }
        )
        onExpandedChange()
    }
}.onPointerEvent(PointerEventType.Enter) {
    enterState.value = true
}.onPointerEvent(PointerEventType.Exit) {
    enterState.value = false
}

interface ExposedDropdownMenuBoxScope {
    val textFieldEnterState: MutableState<Boolean>
    fun Modifier.menuAnchor(): Modifier
    fun Modifier.exposedDropdownSize(matchTextFieldWidth: Boolean = true): Modifier

//    @Composable
//    fun ExposedDropdownMenu(
//        expanded: MutableState<Boolean>,
//        modifier: Modifier = Modifier,
//        content: @Composable ColumnScope.() -> Unit,
//    ) {
//        DropdownMenu(
//            expanded = expanded.value,
//            onDismissRequest = {
//                if (!textFieldEnterState.value) {
//                    expanded.value = false
//                } else {
//                    expanded.value = false
//                    expanded.value = true
//                }
//            },
//            modifier = modifier.exposedDropdownSize()
//        ) {
//            content()
//        }
//    }
}

object ExposedDropdownMenuDefaults {
    @Composable
    fun TrailingIcon(expanded: Boolean) {
        Icon(
            Icons.Filled.ArrowDropDown, null, Modifier.rotate(if (expanded) 180f else 0f)
        )
    }

    val ItemContentPadding: PaddingValues = PaddingValues(
        horizontal = ExposedDropdownMenuItemHorizontalPadding,
        vertical = 0.dp
    )
}

private val ExposedDropdownMenuItemHorizontalPadding = 16.dp