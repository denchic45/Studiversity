package com.denchic45.kts.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentProfileBinding
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.BaseFragment2
import com.denchic45.kts.ui.avatar.FullImageActivity
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.profile.fullAvatar.FullAvatarActivity
import com.example.appbarcontroller.appbarcontroller.AppBarController
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.io.ByteArrayOutputStream

@Inject
class ProfileFragment(component: (String) -> ProfileViewModel) :
    BaseFragment2<ProfileViewModel, FragmentProfileBinding>(
        R.layout.fragment_profile,
        R.menu.options_profile
    ), HasNavArgs<ProfileFragmentArgs> {

    override val navArgs: ProfileFragmentArgs by navArgs()

    override val component: ProfileViewModel by lazy { component(navArgs.userId) }

    override val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

    // FIXME: Use other library
//    private var galleryResult = registerForActivityResult(PickImageContract()) {
//        cropImage.launch(
//            options(it) {
//                setScaleType(CropImageView.ScaleType.FIT_CENTER)
//                setCropShape(CropImageView.CropShape.RECTANGLE)
//                setGuidelines(CropImageView.Guidelines.ON_TOUCH)
//                setAspectRatio(1, 1)
//                setMaxZoom(4)
//                setAutoZoomEnabled(true)
//                setMultiTouchEnabled(true)
//                setCenterMoveEnabled(true)
//                setShowCropOverlay(true)
//                setAllowFlipping(true)
//                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
//            }
//        )
//    }

//    private val cropImage =
//        registerForActivityResult(CropImageContract()) {
//            it.uriContent?.let { uri ->
//                try {
//                    val bitmap = MediaStore.Images.Media.getBitmap(
//                        requireActivity().contentResolver, uri
//                    )
//                    component.onImageLoad(getBytesFromBitmap(bitmap))
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }

    private fun getBytesFromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return stream.toByteArray()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppBarController.findController(requireActivity()).setExpanded(
            expand = true,
            animate = true
        )

        with(binding) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    component.profileViewState.collect {
                        it.onSuccess {
                            Glide.with(this@ProfileFragment)
                                .load(it.avatarUrl)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(ivAvatar)

                            tvFullName.text = it.fullName

                            it.personalDate?.let {
                                llInfo.visibility = View.VISIBLE
                                tvEmail.text = it.email
                            } ?: run { llInfo.visibility = View.GONE }
                        }
                    }
                }
            }

            ivAvatar.setOnClickListener { component.onAvatarClick() }

            component.openFullImage.observe(viewLifecycleOwner) { url: String ->
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    androidx.core.util.Pair.create(
                        ivAvatar, ViewCompat.getTransitionName(ivAvatar)
                    )
                )
                val intent = Intent(requireActivity(), FullAvatarActivity::class.java)
                intent.putExtra(FullImageActivity.IMAGE_URL, url)
                startActivity(intent, options.toBundle())
            }
            component.openGallery.observe(viewLifecycleOwner) {
                val intent = Intent(
                    Intent.ACTION_PICK
                )
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
//                galleryResult.launch(false)
            }
        }
    }

    companion object {
        const val USER_ID = "USER_UUID"
    }
}