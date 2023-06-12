//package com.denchic45.studiversity.ui.adapter
//
//import android.animation.ArgbEvaluator
//import android.animation.ObjectAnimator
//import android.graphics.Color
//import android.graphics.drawable.PictureDrawable
//import android.view.ViewGroup
//import android.view.animation.LinearInterpolator
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
//import com.denchic45.studiversity.SvgColorListener
//import com.denchic45.studiversity.common.R
//import com.denchic45.studiversity.domain.model.CourseHeader
//import com.denchic45.studiversity.domain.model.Subject
//import com.denchic45.studiversity.domain.model.User
//import com.denchic45.studiversity.databinding.ItemCourseBinding
//import com.denchic45.studiversity.glideSvg.GlideApp
//import com.denchic45.studiversity.util.viewBinding
//
//class CourseAdapter(
//    val onItemClickListener: OnItemClickListener = OnItemClickListener { },
//    val onItemLongClickListener: OnItemLongClickListener = OnItemLongClickListener { }
//) : ListAdapter<CourseHeader, CourseAdapter.CourseHolder>(
//    DIFF_CALLBACK
//) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder {
//        return CourseHolder(
//            parent.viewBinding(ItemCourseBinding::inflate),
//            onItemClickListener,
//            onItemLongClickListener
//        )
//    }
//
//    override fun onBindViewHolder(holder: CourseHolder, position: Int) {
//        holder.onBind(getItem(position))
//    }
//
//
//
//    companion object {
//        private val DIFF_CALLBACK: DiffUtil.ItemCallback<CourseHeader> =
//            object : DiffUtil.ItemCallback<CourseHeader>() {
//                override fun areItemsTheSame(oldItem: CourseHeader, newItem: CourseHeader): Boolean {
//                    return oldItem.subject.id == newItem.subject.id
//                }
//
//                override fun areContentsTheSame(oldItem: CourseHeader, newItem: CourseHeader): Boolean {
//                    return oldItem == newItem
//                }
//            }
//    }
//}