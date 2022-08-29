package com.denchic45.kts.ui.timetable

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.denchic45.kts.ui.theme.Blue
import com.denchic45.kts.ui.theme.Typography

@Preview
@OptIn(ExperimentalUnitApi::class)
@Composable
fun TimetableScreen() {
    MediumTopAppBar(
        title = { Text("Июль", fontSize = TextUnit(32F, TextUnitType.Sp)) },
        modifier = Modifier.heightIn(min = 112.dp),
        actions = {
            IconButton(
                modifier = Modifier,
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back arrow icon"
                )
            }
        }
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp),
        elevation = 0.dp
    ) {
        TimetableBody()
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
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
    }
}


@Composable
private fun DaysOfWeekHeader(modifierHorScroll: Modifier) {
    Row(modifierHorScroll) {
        repeat(6) { DayOfWeekCell(it) }
    }
    Divider(modifier = Modifier.fillMaxWidth().height(1.dp))
}

@Composable
@Preview
fun RowScope.DayOfWeekCell(order: Int) {
    Row(Modifier.weight(1f).height(IntrinsicSize.Max), verticalAlignment = Alignment.Bottom) {
        if (order != 0)
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
            )
        Column(
            modifier = Modifier.widthIn(min = 196.dp)
                .height(124.dp)
                .padding(top = 24.dp)
                .fillMaxWidth(),
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    repeat(8) {
                        LessonCell()
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
        modifier = Modifier.widthIn(min = 196.dp).height(126.dp).padding(18.dp)
    ) {
        Icon(
            painterResource("icons/ic_basketball.xml"),
            null,
            modifier = Modifier.size(28.dp),
            tint = Blue
        )
        Text(
            "Физкультура",
            modifier = Modifier.padding(top = 8.dp),
            style = Typography.titleLarge
        )
        Text(
            "2-й корпус",
            modifier = Modifier.padding(top = 4.dp),
            fontSize = TextUnit(18F, TextUnitType.Sp),
            fontFamily = FontFamily(
                Font(
                    resource = "fonts/Gilroy-Medium.ttf"
                )
            )
        )
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    )
}