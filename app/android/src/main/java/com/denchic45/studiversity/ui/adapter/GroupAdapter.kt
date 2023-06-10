//package com.denchic45.studiversity.ui.adapter
//
//import android.annotation.SuppressLint
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import com.denchic45.studiversity.data.domain.model.DomainModel
//import com.denchic45.studiversity.databinding.ItemIconContentBinding
//import com.denchic45.studiversity.util.viewBinding
//import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
//
//class GroupAdapter : CustomAdapter<StudyGroupResponse, GroupAdapter.GroupHolder> {
//    private lateinit var specialtyItemClickListener: OnItemClickListener
//
//    constructor(itemClickListener: OnItemClickListener) : super(
//        DIFF_CALLBACK,
//        itemClickListener
//    )
//
//    constructor(
//        itemClickListener: OnItemClickListener,
//        itemLongClickListener: OnItemLongClickListener
//    ) : super(
//        DIFF_CALLBACK, itemClickListener, itemLongClickListener
//    )
//
//    fun setSpecialtyItemClickListener(specialtyItemClickListener: OnItemClickListener) {
//        this.specialtyItemClickListener = specialtyItemClickListener
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): GroupHolder {
//        return GroupHolder(
//            parent.viewBinding(ItemIconContentBinding::inflate),
//            onItemClickListener,
//            onItemLongClickListener
//        )
//
//    }
//
//    override fun onBindViewHolder(holder: BaseViewHolder<DomainModel, *>, position: Int) {
//        holder.onBind(getItem(position))
//    }
//
//
//    companion object {
//        private val DIFF_CALLBACK: DiffUtil.ItemCallback<DomainModel> =
//            object : DiffUtil.ItemCallback<DomainModel>() {
//                override fun areItemsTheSame(oldItem: DomainModel, newItem: DomainModel): Boolean {
//                    return oldItem.id == newItem.id
//                }
//
//                @SuppressLint("DiffUtilEquals")
//                override fun areContentsTheSame(
//                    oldItem: DomainModel,
//                    newItem: DomainModel
//                ): Boolean {
//                    return oldItem === newItem
//                }
//            }
//    }
//}