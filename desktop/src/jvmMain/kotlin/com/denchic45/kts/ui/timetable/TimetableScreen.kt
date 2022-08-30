package com.denchic45.kts.ui.timetable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.theme.DarkBlue
import com.denchic45.kts.ui.theme.Typography

@Preview
@Composable
fun TimetableScreen() {
    TopAppBar()
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
        elevation = 0.dp
    ) {
        TimetableBody()
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
private fun TopAppBar() {
    val contentHeight = 40.dp
    Row(Modifier.padding(top = contentHeight, bottom = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text("Июль",
            Modifier,
            fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf")),
            fontSize = TextUnit(32F, TextUnitType.Sp))
        Spacer(Modifier.width(24.dp))
        OutlinedButton(
            onClick = {},
            modifier = Modifier.size(contentHeight),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(0.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "previous week arrow icon"
            )
        }
        Spacer(Modifier.width(16.dp))
        OutlinedButton(
            onClick = {},
            modifier = Modifier.size(contentHeight),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(0.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "next week arrow icon"
            )
        }
        Spacer(Modifier.width(16.dp))
        OutlinedButton(
            onClick = {},
            modifier = Modifier.height(contentHeight),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(0.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
        ) {
            Text(
                "Сегодня",
                Modifier.padding(start = 24.dp, end = 24.dp),
                style = Typography.bodyMedium,
                color = DarkBlue
            )
        }
        Spacer(Modifier.weight(1f))
        Spinner()
        Spacer(Modifier.width(24.dp))
        IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon"
            )
        }
        Spacer(Modifier.width(24.dp))
        IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon"
            )
        }
    }
}

@Composable
fun Spinner() {

    val options = listOf("День", "Неделя", "Месяц")

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[1]) }

    Box(Modifier) {
        OutlinedButton(
            onClick = {
                println("LOGGING: onClick")
                expanded = !expanded
            },
            enabled = !expanded,
            modifier = Modifier.size(112.dp, 40.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
        ) {
            Text(selectedOptionText, Modifier.weight(1f), style = Typography.bodyMedium)
            Icon(Icons.Outlined.ArrowDropDown, "")
        }

        DropdownMenu(
            expanded = expanded,
            modifier = Modifier.width(240.dp),
            onDismissRequest = {
                println("LOGGING: onDismissRequest")
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Composable
@Preview
fun TimetableBody() {
    val verticalScroll: ScrollState = rememberScrollState()
    val horizontalScroll: ScrollState = rememberScrollState()

    Row {
        Column(Modifier.width(78.dp)) {
            Box(Modifier.fillMaxWidth().height(124.dp), contentAlignment = Alignment.BottomEnd) {
                Divider(Modifier.height(24.dp).width(1.dp))
            }
            Divider(Modifier.fillMaxWidth().height(1.dp))
            LessonOrders(verticalScroll)
        }
        BoxWithConstraints(Modifier) {
            val modifierHorScroll = if (maxWidth < 1000.dp)
                Modifier.horizontalScroll(horizontalScroll).widthIn(1000.dp)
            else Modifier
            Column {
                DaysOfWeekHeader(modifierHorScroll)
                LessonCells(modifierHorScroll, verticalScroll)
            }
        }
    }
}


@Composable
fun LessonOrders(state: ScrollState) {
    Column(
        Modifier.fillMaxWidth().verticalScroll(state),
        horizontalAlignment = Alignment.End
    ) {
        repeat(8) { LessonsOrder() }
    }
}

@Composable
private fun LessonsOrder() {
    Row(Modifier.height(127.dp)) {
        Column(horizontalAlignment = Alignment.End) {
            Text("8:30",
                Modifier.padding(top = 8.dp, end = 16.dp),
                style = Typography.bodySmall)
            Text("1",
                Modifier.padding(top = 4.dp, end = 16.dp),
                color = Color.Gray,
                style = Typography.headlineSmall,
                fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Semibold.ttf")))
            Spacer(Modifier.weight(1f))
            Divider(Modifier.width(30.dp))
        }
        Divider(Modifier.fillMaxHeight().width(1.dp))
    }
}


@Composable
private fun DaysOfWeekHeader(modifierHorScroll: Modifier) {
    Row(modifierHorScroll) {
        repeat(6) { DayOfWeekCell(it) }
    }
    Divider(Modifier.fillMaxWidth().height(1.dp))
}

@Composable
@Preview
fun RowScope.DayOfWeekCell(order: Int) {
    Row(Modifier.weight(1f).height(IntrinsicSize.Max), verticalAlignment = Alignment.Bottom) {
        if (order != 0)
            Divider(Modifier.width(1.dp).height(24.dp))
        Column(
            Modifier.widthIn(min = 196.dp).height(124.dp).padding(top = 24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ПН",
                style = Typography.bodyMedium
            )
            Text(
                "27",
                modifier = Modifier.padding(top = 12.dp),
                style = Typography.headlineMedium
            )
        }
    }
}


@Composable
@Preview
fun LessonCells(modifier: Modifier, verticalScroll: ScrollState) {
    Row(modifier.verticalScroll(verticalScroll)) {
        repeat(6) {
            Row(Modifier.weight(1F).height(IntrinsicSize.Max)) {
                if (it != 0) {
                    Divider(Modifier.width(1.dp).fillMaxHeight())
                }
                Column(Modifier.fillMaxWidth()) {
                    repeat(8) {
                        LessonCell()
                        if (it != 7)
                            Divider(Modifier.fillMaxWidth().height(1.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
@Preview
fun LessonCell() {
    Column(
        Modifier.widthIn(min = 196.dp).height(126.dp).padding(18.dp)
    ) {
        Icon(
            painterResource("icons/ic_basketball.xml"),
            null,
            Modifier.size(28.dp),
            tint = DarkBlue
        )
        Text(
            "Физкультура",
            Modifier.padding(top = 8.dp),
            style = Typography.titleLarge
        )
        Text(
            "2-й корпус",
            Modifier.padding(top = 4.dp),
            fontSize = TextUnit(18F, TextUnitType.Sp),
            fontFamily = FontFamily(
                Font(
                    resource = "fonts/Gilroy-Medium.ttf"
                )
            )
        )
    }
}