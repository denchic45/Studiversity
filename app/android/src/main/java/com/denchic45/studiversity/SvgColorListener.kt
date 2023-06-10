package com.denchic45.studiversity

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.denchic45.studiversity.glideSvg.SvgSoftwareLayerSetter
import com.denchic45.studiversity.util.ViewUtils
import com.denchic45.studiversity.util.paintColor

class SvgColorListener(
    private val view: ImageView,
    private val color: Int,
    private val context: Context
) : SvgSoftwareLayerSetter() {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any,
        target: Target<PictureDrawable>,
        isFirstResource: Boolean
    ): Boolean {
        super.onLoadFailed(e, model, target, isFirstResource)
//        view.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP)
        ViewUtils.paintImageView(view, color, context)
//        view.paintColor(color)
        return false
    }

    override fun onResourceReady(
        resource: PictureDrawable,
        model: Any,
        target: Target<PictureDrawable>,
        dataSource: DataSource,
        isFirstResource: Boolean
    ): Boolean {
        super.onResourceReady(resource, model, target, dataSource, isFirstResource)
        ViewUtils.paintImageView(view, color, context)
        view.paintColor(color)
//        view.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP)
        return false
    }
}