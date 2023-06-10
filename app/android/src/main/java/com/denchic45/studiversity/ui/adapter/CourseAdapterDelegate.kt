package com.denchic45.studiversity.ui.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.ItemCourseBinding
import com.denchic45.studiversity.glideSvg.GlideApp
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate

class CourseAdapterDelegate :
    ListItemAdapterDelegate<CourseResponse, CourseAdapterDelegate.CourseHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): CourseHolder {
        return CourseHolder(
            parent.viewBinding(ItemCourseBinding::inflate)
        )
    }

    override fun isForViewType(item: Any): Boolean = item is CourseResponse

    override fun onBindViewHolder(item: CourseResponse, holder: CourseHolder) {
        return holder.onBind(item)
    }

    class CourseHolder(
        itemCourseBinding: ItemCourseBinding,
    ) : BaseViewHolder<CourseResponse, ItemCourseBinding>(
        itemCourseBinding
    ) {
        private val ivSubjectIcon = binding.ivSubjectIcon
        private val tvSubjectName = binding.tvSubjectName
        override fun onBind(item: CourseResponse) {
            val subject = item.subject
//            val resColor: Int = itemView.context
//                .resources
//                .getIdentifier(subject.colorName, "color", itemView.context.packageName)
            com.denchic45.studiversity.glideSvg.GlideApp.with(itemView.context)
                .`as`(PictureDrawable::class.java)
                .transition(DrawableTransitionOptions.withCrossFade())
//                .listener(
//                    SvgColorListener(
//                        ivSubjectIcon,
//                        resColor,
//                        itemView.context
//                    )
//                )
                .load(subject?.iconUrl)
                .into(ivSubjectIcon)

            tvSubjectName.text = item.name
        }

        fun setSelect(select: Boolean) {
            val colorLightBlue = ContextCompat.getColor(itemView.context, R.color.light_blue)
            val a: ObjectAnimator = if (select) {
                ObjectAnimator.ofInt(
                    itemView,
                    "backgroundColor",
                    Color.WHITE,
                    colorLightBlue
                )
            } else {
                ObjectAnimator.ofInt(
                    itemView,
                    "backgroundColor",
                    colorLightBlue,
                    Color.WHITE
                )
            }
            a.interpolator = LinearInterpolator()
            a.duration = 200
            a.setEvaluator(ArgbEvaluator())
            a.start()
        }

    }
}