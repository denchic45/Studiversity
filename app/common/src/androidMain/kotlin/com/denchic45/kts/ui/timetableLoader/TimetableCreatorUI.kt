package com.denchic45.kts.ui.timetableLoader

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.util.DatePatterns
import com.denchic45.stuiversity.util.toString
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import okio.Path.Companion.toOkioPath
import org.apache.poi.util.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.DayOfWeek

@Composable
private fun Uri.toFile(): File {
    val context = LocalContext.current
    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(this, "r", null)
    val inputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)

    val file = File(context.cacheDir, context.contentResolver.getFileName(this))
    val outputStream = FileOutputStream(file)
    IOUtils.copy(inputStream, outputStream)
    parcelFileDescriptor.close()
    return file
}

private fun ContentResolver.getFileName(fileUri: Uri): String {

    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }

    return name
}

@Composable
fun TimetableCreatorScreen(component: TimetableCreatorComponent) {
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data: Intent ->
                component.onFileSelect(data.data!!.toFile().toOkioPath())
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = component::onCreateEmpty) {
            Text("Создать с нуля")
        }
        Spacer(Modifier.height(MaterialTheme.spacing.small))
        Button(onClick = component::onLoadFromFile) {
            Text("Загрузить из документа")
        }
    }

    val showWeekPicker by component.showWeekPicker.collectAsState()
    val weekPickerState = rememberMaterialDialogState()
    if (showWeekPicker) weekPickerState.show()

    var selectedDate by remember {
        mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }

    MaterialDialog(dialogState = weekPickerState, buttons = {
        positiveButton("Выбрать") {
            component.onWeekSelect(
                selectedDate.toJavaLocalDate().toString(DatePatterns.YYYY_ww)
            )
        }
        negativeButton("Отмена", onClick = component::onCancelWeekPicker)
    }) {
        datepicker(
            title = "Выберите неделю расписания",
            allowedDateValidator = { it.dayOfWeek == DayOfWeek.MONDAY },
            initialDate = selectedDate
        ) { date -> selectedDate = date }
    }

    val showFilePicker by component.showFilePicker.collectAsState()
    if (showFilePicker) {
        val chooserIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        pickFileLauncher.launch(
            Intent.createChooser(
                chooserIntent, "Выберите документ с расписанием"
            )
        )
    }
}