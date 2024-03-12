package com.denchic45.studiversity.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar.LocalAppBarState
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.ui.usercourses.UserCoursesDialog
import com.denchic45.studiversity.ui.userstudygroups.UserStudyGroupsDialog
import com.denchic45.studiversity.util.toast
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.io.ByteArrayOutputStream
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(component: ProfileComponent) {
    val viewStateResource by component.viewState.collectAsState()
    val childSlot by component.childSlot.subscribeAsState()

    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            updateAppBarState(AppBarContent())
            ProfileContent(
                viewStateResource,
                component::onAvatarClick,
                component::onStudyGroupClick
            )
            viewStateResource.onSuccess { viewState ->
                childSlot.child?.let {
                    when (val child = it.instance) {
                        ProfileComponent.SlotChild.AvatarDialog -> {
                            AlertDialog(onDismissRequest = component::onDialogClose) {
                                Column {
                                    ListItem(
                                        modifier = Modifier.clickable(onClick = component::onOpenAvatarClick),
                                        headlineContent = { Text("Открыть фото") },
                                        leadingContent = {
                                            Icon(Icons.Outlined.AccountCircle, "view photo")
                                        }
                                    )
                                    ListItem(
                                        modifier = Modifier.clickable(onClick = component::onUpdateAvatarClick),
                                        headlineContent = { Text("Изменить фото") },
                                        leadingContent = {
                                            Icon(Icons.Outlined.Edit, "update photo")
                                        }
                                    )
                                    ListItem(
                                        modifier = Modifier.clickable(onClick = component::onRemoveAvatarClick),
                                        headlineContent = { Text("Удалить фото") },
                                        leadingContent = {
                                            Icon(Icons.Outlined.Delete, "delete photo")
                                        }
                                    )
                                }
                            }
                        }

                        ProfileComponent.SlotChild.FullAvatar -> FullAvatarScreen(
                            url = viewState.user.avatarUrl,
                            allowUpdateAvatar = viewState.self,
                            onDeleteClick = {}
                        )

                        ProfileComponent.SlotChild.AvatarChooser -> {
                            AvatarChooser(component::onNewAvatarSelect, component::onDialogClose)
                        }

                        is ProfileComponent.SlotChild.UserCourses -> UserCoursesDialog(child.component)
                        is ProfileComponent.SlotChild.UserStudyGroups -> UserStudyGroupsDialog(child.component)
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarChooser(onNewAvatarSelect: (String, ByteArray) -> Unit, onClose: () -> Unit) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { selectedImageUri = uri }
    }

    LaunchedEffect(Unit) {
        pickFileLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    selectedImageUri?.let { uri ->
        AvatarCropperScreen(
            imageBitmap = uri.decodeBitmap(context).asImageBitmap(),
            onResult = { bitmap ->
                bitmap?.let {
                    context.contentResolver.query(
                        uri,
                        null,
                        null,
                        null,
                        null
                    )?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        cursor.moveToFirst()
                        val name = cursor.getString(nameIndex)
                        val size = cursor.getLong(sizeIndex)

                        if (size / 1024 / 1024 >= 5) {
                            context.toast("Максимальный вес изображения - 5МБ")
                        } else {
                            val stream = ByteArrayOutputStream()
                            bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 0, stream)
                            onNewAvatarSelect(name, stream.toByteArray())
                        }
                    }
                } ?: onClose()
            }
        )
    }
}

private fun Uri.decodeBitmap(
    context: Context,
) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, this))
} else {
    MediaStore.Images.Media.getBitmap(context.contentResolver, this)
}

@Composable
fun FullAvatarScreen(
    url: String,
    allowUpdateAvatar: Boolean,
    onDeleteClick: () -> Unit,
) {
    if (allowUpdateAvatar) {
        LocalAppBarState.current.content = AppBarContent(
            dropdownItems = listOf(
                DropdownMenuItem2(
                    title = uiTextOf("Удалить"), onClick = onDeleteClick
                )
            )
        )
    }

    AsyncImage(
        model = url,
        contentDescription = "full avatar",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ProfileContent(
    viewState: Resource<ProfileViewState>,
    onAvatarClick: () -> Unit,
    onStudyGroupClick: (UUID) -> Unit,
) {
    ResourceContent(resource = viewState) { profile ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = profile.user.avatarUrl,
                    contentDescription = "user avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onAvatarClick),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(MaterialTheme.spacing.normal))
                Text(profile.user.fullName, style = MaterialTheme.typography.titleMedium)
            }
            HorizontalDivider(Modifier.padding(MaterialTheme.spacing.small))
            ProfileStudyGroups(profile.studyGroups, onStudyGroupClick)

            profile.user.account.let { personalDate ->
                ListItem(
                    leadingContent = {
                        Icon(Icons.Outlined.Email, null)
                    },
                    headlineContent = {
                        Text(personalDate.email, style = MaterialTheme.typography.bodyLarge)
                    },
                    supportingContent = {
                        Text("Почта", style = MaterialTheme.typography.bodyMedium)
                    })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProfileStudyGroups(
    studyGroups: List<StudyGroupResponse>,
    onStudyGroupClick: (UUID) -> Unit,
) {
    when (studyGroups.size) {
        0 -> {}
        1 -> {
            val studyGroup = studyGroups.first()
            Card(
                onClick = { onStudyGroupClick(studyGroup.id) },
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.normal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Group, "group")
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    Text("Состоит в группе ${studyGroup.name}")
                }
            }
        }

        else -> {
            var showStudyGroups by remember { mutableStateOf(false) }
            Card(
                onClick = { showStudyGroups = true },
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.normal)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.normal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Group, "group")
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    Text("Группы")
                    Spacer(Modifier.weight(1f))
                    Text(studyGroups.size.toString())
                }
            }

            if (showStudyGroups)
                AlertDialog(onDismissRequest = { showStudyGroups = false }) {
                    Column {
                        studyGroups.forEach {
                            StudyGroupListItem(
                                item = it,
                                modifier = Modifier.clickable { onStudyGroupClick(it.id) }
                            )
                        }
                    }
                }
        }
    }
}