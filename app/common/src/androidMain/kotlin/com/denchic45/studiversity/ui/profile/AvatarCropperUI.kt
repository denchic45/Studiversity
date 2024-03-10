package com.denchic45.studiversity.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty

@Composable
fun AvatarCropperScreen(imageBitmap: ImageBitmap, onResult: (ImageBitmap?) -> Unit) {
    var croppedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val handleSize: Float = LocalDensity.current.run { 20.dp.toPx() }
    var isCropping by remember { mutableStateOf(false) }
    var crop by remember { mutableStateOf(false) }

    croppedImage?.let {
        onResult(croppedImage)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ImageCropper(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            imageBitmap = imageBitmap,
            contentDescription = "Image Cropper",
            cropStyle = CropDefaults.style(),
            cropProperties = CropDefaults.properties(
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "Rect")
                ),
                handleSize = handleSize
            ),
            crop = crop,
            onCropStart = {
                isCropping = true
            },
            onCropSuccess = {
                croppedImage = it
                isCropping = false
//            crop = false}
            }
        )
        AppTheme(true) {
            Row(Modifier.padding(horizontal = MaterialTheme.spacing.normal)) {
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { onResult(null) }) {
                    Text("Отмена")
                }
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                TextButton(onClick = {
                    crop = true
                }) {
                    Text("Подтвердить")
                }
            }
        }
    }
}